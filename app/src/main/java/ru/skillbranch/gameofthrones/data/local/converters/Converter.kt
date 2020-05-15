package ru.skillbranch.gameofthrones.data.local.converters

import androidx.room.TypeConverter
import ru.skillbranch.gameofthrones.data.local.entities.HouseType

// 52:28
class Converter {
    @TypeConverter
    fun fromString(value: String): List<String> = value.split(";")

    @TypeConverter
    fun fromArrayList(list: List<String>) = list.joinToString(";")

    @TypeConverter
    fun fromTitle(value: String): HouseType = HouseType.fromString(value)

    @TypeConverter
    fun fromEnum(anEnum: HouseType): String = anEnum.title
}