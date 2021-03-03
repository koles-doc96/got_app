package ru.skillbranch.gameofthrones.data.local.entities

import androidx.room.*

@Entity(tableName = "characters")
data class Character(
    @PrimaryKey
    val id: String,
    val name: String,
    val gender: String,
    val culture: String,
    val born: String,
    val died: String,
    val titles: List<String> = listOf(),
    val aliases: List<String> = listOf(),
    val father: String, //rel
    val mother: String, //rel
    val spouse: String,
    @ColumnInfo(name = "house_id")
    val houseId: HouseType, //rel
    @ColumnInfo(name = "is_bookmarked")
    var isBookmarked: Boolean

)

@DatabaseView(
    """
        SELECT id, house_id AS house, name, titles, aliases, is_bookmarked
        FROM characters
        ORDER BY name ASC
    """
)
data class CharacterItem(
    val id: String,
    val house: HouseType, //rel
    val name: String,
    val titles: List<String>,
    val aliases: List<String>,
    @ColumnInfo(name = "is_bookmarked")
    var isBookmarked: Boolean

)

@DatabaseView(
    """
         SELECT characters.id, characters.name, characters.born, characters.died, characters.titles,
        characters.aliases, characters.is_bookmarked, characters.house_id, characters.mother,
        characters.father, houses.words, mother.name AS m_name, mother.id AS m_id, mother.house_id AS m_house,
        mother.is_bookmarked AS m_is_bookmarked, father.name AS f_name, father.id AS f_id, father.house_id AS f_house,
        father.is_bookmarked AS f_is_bookmarked
         FROM characters
         LEFT JOIN characters AS mother ON characters.mother = mother.id
         LEFT JOIN characters AS father ON characters.father = father.id
         INNER JOIN houses ON characters.house_id = houses.id
    """
)
data class CharacterFull(
    val id: String,
    val name: String,
    val words: String,
    val born: String,
    val died: String,
    val titles: List<String>,
    val aliases: List<String>,
    @ColumnInfo(name = "is_bookmarked")
    var isBookmarked: Boolean,
    @ColumnInfo(name = "house_id")
    val house: HouseType, //rel
    @Embedded(prefix = "m_")
    val mother: RelativeCharacter?,
    @Embedded(prefix = "f_")
    val father: RelativeCharacter?
)

data class RelativeCharacter(
    val id: String,
    val name: String,
    val house: String, //rel
    val is_bookmarked: Boolean
)