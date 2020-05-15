package ru.skillbranch.gameofthrones.data.local

import androidx.room.*
import ru.skillbranch.gameofthrones.App
import ru.skillbranch.gameofthrones.BuildConfig
import ru.skillbranch.gameofthrones.data.local.AppDb.Companion.DATABASE_NAME
import ru.skillbranch.gameofthrones.data.local.converters.Converter
import ru.skillbranch.gameofthrones.data.local.dao.CharactersDao
import ru.skillbranch.gameofthrones.data.local.dao.HouseDao
import ru.skillbranch.gameofthrones.data.local.entities.Character
import ru.skillbranch.gameofthrones.data.local.entities.CharacterFull
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.data.local.entities.House

// 51:30
object DbManager {
    val db = Room.databaseBuilder(
        App.applicationContext(),
        AppDb::class.java, DATABASE_NAME
    )
        .build()
}

@Database(
    entities = [House::class, Character::class],
    version = AppDb.DATABASE_VERSION,
    exportSchema = false,
    views = [CharacterItem::class, CharacterFull::class]
)

@TypeConverters(Converter::class)
abstract class AppDb : RoomDatabase() {

    companion object {
        const val DATABASE_NAME = BuildConfig.APPLICATION_ID + ".db"
        const val DATABASE_VERSION = 1
    }

    abstract fun houseDao(): HouseDao
    abstract fun charactersDao(): CharactersDao
}