package ru.skillbranch.gameofthrones.repositories

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import ru.skillbranch.gameofthrones.AppConfig
import ru.skillbranch.gameofthrones.data.local.DbManager
import ru.skillbranch.gameofthrones.data.local.dao.CharacterDao
import ru.skillbranch.gameofthrones.data.local.dao.HouseDao
import ru.skillbranch.gameofthrones.data.local.entities.Character
import ru.skillbranch.gameofthrones.data.local.entities.CharacterFull
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.data.local.entities.House
import ru.skillbranch.gameofthrones.data.remote.NetworkService
import ru.skillbranch.gameofthrones.data.remote.RestService
import ru.skillbranch.gameofthrones.data.remote.res.CharacterRes
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes

object RootRepository {

    private val api: RestService = NetworkService.api
    private val houseDao: HouseDao = DbManager.db.houseDao()
    private val characterDao: CharacterDao by lazy {
        DbManager.db.characterDao()
    }
    private val errHandler = CoroutineExceptionHandler { _, exception ->
        //Log.d("coroutineError", "Caught $exception")
        exception.printStackTrace()
    }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO + errHandler)


    /**
     * Получение данных о требуемых домах по их полным именам из сети
     * @param houseNames - массив полных названий домов (смотри AppConfig)
     * @param result - колбек содержащий в себе список данных о домах
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    suspend fun getNeedHouses(vararg houseNames: String): List<HouseRes> {
        return houseNames.fold(mutableListOf<HouseRes>()) { acc, title ->
            acc.also { it.add(api.houseByName(title).first()) }
        }
    }

    /**
     * Получение данных о требуемых домах по их полным именам и персонажах в каждом из домов из сети
     * @param houseNames - массив полных названий домов (смотри AppConfig)
     * @param result - колбек содержащий в себе список данных о доме и персонажей в нем (Дом - Список Персонажей в нем)
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    suspend fun needHouseWithCharacters(vararg houseNames: String): List<Pair<HouseRes, List<CharacterRes>>> {
        val result = mutableListOf<Pair<HouseRes, List<CharacterRes>>>()
        val houses = getNeedHouses(*houseNames)
        scope.launch {
            houses.forEach { house ->
                var i = 0
                val characters = mutableListOf<CharacterRes>()
                result.add(house to characters)
                house.members.forEach { character ->
                    launch(CoroutineName("character $character")) {
                        api.character(character)
                            .apply { houseId = house.shortName }
                            .also { characters.add(it) }
                        i++
                        println("complete coroutine $i/${house.swornMembers.size} ${house.name}")
                    }
                }
            }
        }.join()
        return result
    }

    suspend fun sync() {
        //Log.d("RootViewModel", "in sync")
        val pairs = needHouseWithCharacters(*AppConfig.NEED_HOUSES)
        val initial = mutableListOf<House>() to mutableListOf<Character>()

        val lists = pairs.fold(initial) { acc, (houseRes, characterResList) ->
            val house = houseRes.toHouse()
            val characters = characterResList.map { it.toCharacter() }
            acc.also { (hs, ch) ->
                hs.add(house)
                ch.addAll(characters)
            }
        }
        houseDao.upsert(lists.first)
        characterDao.upsert(lists.second)
    }

    /**
     * Метод возвращет true если в базе нет ни одной записи, иначе false
     * @param result - колбек о завершении очистки db
     */
    suspend fun isNeedUpdate(): Boolean {
        return houseDao.recordsCount() == 0
    }

    fun getCharactersByTitle(title: String): LiveData<List<CharacterItem>> {
        return characterDao.findCharacters(title)
    }

    suspend fun setBookmarked(characterId: String) {
        withContext(Dispatchers.IO) {
            val char = characterDao.findCharacterObject(characterId)
            char.isBookmarked = !char.isBookmarked
            characterDao.updateCharacter(char)
        }
    }

    fun getCharacter(characterId: String): LiveData<CharacterFull> {
        return characterDao.findCharacter(characterId)
    }

    /**
     * Получение данных о всех домах из сети
     * @param result - колбек содержащий в себе список данных о домах
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getAllHouses(result: (houses: List<HouseRes>) -> Unit) {
        scope.launch {
            var resultList = mutableListOf<HouseRes>()
            scope.launch {
                var page = 1
                var isInsert: Boolean
                do {
                    isInsert = resultList.addAll(api.houses(page))
                    page++
                } while (isInsert)
            }.join()
            result(resultList)
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getNeedHouseWithCharacters(
        vararg houseNames: String,
        result: (houses: List<Pair<HouseRes, List<CharacterRes>>>) -> Unit
    ) {
        scope.launch { result(needHouseWithCharacters(*houseNames)) }
    }

    /**
     * Запись данных о домах в DB
     * @param houses - Список персонажей (модель HouseRes - модель ответа из сети)
     * необходимо произвести трансформацию данных
     * @param complete - колбек о завершении вставки записей db
     */
//    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun insertHouses(houses: List<HouseRes>, complete: () -> Unit) {
        val list = houses.map { it.toHouse() }
        scope.launch {
            houseDao.upsert(list)
            complete()
        }
    }

    /**
     * Запись данных о пересонажах в DB
     * @param Characters - Список персонажей (модель CharacterRes - модель ответа из сети)
     * необходимо произвести трансформацию данных
     * @param complete - колбек о завершении вставки записей db
     */
//    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun insertCharacters(characters: List<CharacterRes>, complete: () -> Unit) {
        val list = characters.map { it.toCharacter() }
        scope.launch {
            characterDao.upsert(list)
            complete()
        }
    }

    /**
     * При вызове данного метода необходимо выполнить удаление всех записей в db
     * @param complete - колбек о завершении очистки db
     */
//    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun dropDb(complete: () -> Unit) {
        scope.launch {
            if (houseDao.recordsCount() != 0) {
                characterDao.deleteTable()
                houseDao.deleteTable()
            }
            complete()
        }
    }

    /**
     * Поиск всех персонажей по имени дома, должен вернуть список краткой информации о персонажах
     * дома - смотри модель CharacterItem
     * @param name - краткое имя дома (его первычный ключ)
     * @param result - колбек содержащий в себе список краткой информации о персонажах дома
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun findCharactersByHouseName(name: String, result: (characters: List<CharacterItem>) -> Unit) {
        var list: List<CharacterItem>
        scope.launch {
            list = characterDao.findCharacterList(name)
            result(list)
        }
    }


}

