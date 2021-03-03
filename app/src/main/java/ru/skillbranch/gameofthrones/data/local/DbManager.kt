package ru.skillbranch.gameofthrones.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.skillbranch.gameofthrones.App
import ru.skillbranch.gameofthrones.BuildConfig
import ru.skillbranch.gameofthrones.data.local.converters.GoTConverter
import ru.skillbranch.gameofthrones.data.local.dao.CharacterDao
import ru.skillbranch.gameofthrones.data.local.dao.HouseDao
import ru.skillbranch.gameofthrones.data.local.entities.Character
import ru.skillbranch.gameofthrones.data.local.entities.CharacterFull
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.data.local.entities.House

object DbManager {
    val db = Room.databaseBuilder(
        App.applicationContext(),
        AppDatabase::class.java, AppDatabase.DATABASE_NAME)
        .fallbackToDestructiveMigration()
        .build()
}

@Database(
    entities = [House::class, Character::class],
    version = AppDatabase.DATABASE_VERSION,
    exportSchema = false,
    views = [CharacterItem::class, CharacterFull::class]
)

@TypeConverters(GoTConverter::class)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = BuildConfig.APPLICATION_ID + ".db"
        const val DATABASE_VERSION = 3
    }

    abstract fun houseDao() : HouseDao
    abstract fun characterDao() : CharacterDao
}
