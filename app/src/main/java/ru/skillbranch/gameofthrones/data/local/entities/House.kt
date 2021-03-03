package ru.skillbranch.gameofthrones.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "houses")
data class House(
    @PrimaryKey
    val id: HouseType,
    val name: String,
    val region: String,
    @ColumnInfo(name = "coat_of_arms")
    val coatOfArms: String,
    val words: String,
    val titles: List<String>,
    val seats: List<String>,
    @ColumnInfo(name = "coat_of_lord")
    val currentLord: String, //rel
    val heir: String, //rel
    val overlord: String,
    val founded: String,
    val founder: String, //rel
    val diedOut: String,
    @ColumnInfo(name = "ancestral_weapons")
    val ancestralWeapons: List<String>
)