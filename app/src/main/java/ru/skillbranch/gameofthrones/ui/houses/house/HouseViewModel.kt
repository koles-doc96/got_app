package ru.skillbranch.gameofthrones.ui.houses.house

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.extensions.combineAndCompute
import ru.skillbranch.gameofthrones.repositories.RootRepository

class HouseViewModel(private val houseName: String) : ViewModel() {
    private val repository = RootRepository
    private val queryString = MutableLiveData<String>("")

    fun getCharacters(): LiveData<List<CharacterItem>> {
        val characters = repository.getCharactersByTitle(houseName)
        return characters.combineAndCompute(queryString) { list, query ->
            if (query.isEmpty()) list
            else list.filter { it.name.contains(query, true) }
        }
    }

    fun getFavoriteCharacters(): LiveData<List<CharacterItem>> {
        val characters = repository.getCharactersByTitle(houseName)
        return characters.combineAndCompute(queryString) { list, query ->
            if (query.isEmpty()) list.filter { it.isBookmarked }
            else list.filter { it.name.contains(query, true) && it.isBookmarked }
        }
    }

    fun handleSearchQuery(searchStr: String) {
        queryString.value = searchStr
    }
}

class HouseViewModelFactory(private val houseName: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HouseViewModel::class.java)) {
            return HouseViewModel(houseName) as T
        }
        throw  IllegalArgumentException("unknown ViewModel class")
    }
}