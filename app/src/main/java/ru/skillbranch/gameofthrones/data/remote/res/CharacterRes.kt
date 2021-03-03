package ru.skillbranch.gameofthrones.data.remote.res

import com.squareup.moshi.Json
import ru.skillbranch.gameofthrones.data.local.entities.Character
import ru.skillbranch.gameofthrones.data.local.entities.HouseType

data class CharacterRes(
    @Json(name = "url")
    val url: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "gender")
    val gender: String,
    @Json(name = "culture")
    val culture: String,
    @Json(name = "born")
    val born: String,
    @Json(name = "died")
    val died: String,
    @Json(name = "titles")
    val titles: List<String> = listOf(),
    @Json(name = "aliases")
    val aliases: List<String> = listOf(),
    @Json(name = "father")
    val father: String,
    @Json(name = "mother")
    val mother: String,
    @Json(name = "spouse")
    val spouse: String,
    @Json(name = "allegiances")
    val allegiances: List<String> = listOf(),
    @Json(name = "books")
    val books: List<String> = listOf(),
    @Json(name = "povBooks")
    val povBooks: List<Any> = listOf(),
    @Json(name = "tvSeries")
    val tvSeries: List<String> = listOf(),
    @Json(name = "playedBy")
    val playedBy: List<String> = listOf()

) : IRes {
    lateinit var houseId: String
    override val id: String
        get() = url.lastSegment()
    override var isBookmarked: Boolean = false
    val fatherId
        get() = father.lastSegment()
    val motherId
        get() = mother.lastSegment()

    fun toCharacter(): Character {
        return Character(
            url.lastSegment(),
            name,
            gender,
            culture,
            born,
            died,
            titles,
            aliases,
            father,
            mother,
            spouse,
            HouseType.fromString(houseId),
            isBookmarked
        )
    }
}
