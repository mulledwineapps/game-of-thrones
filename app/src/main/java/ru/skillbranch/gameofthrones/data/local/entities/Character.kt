package ru.skillbranch.gameofthrones.data.local.entities

import androidx.room.*

// 48:55
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
    val houseId: HouseType //rel
)

// DatabaseView - это виртуальная таблица, где мы заранее подготавливаем запрос
// (какие поля, порядок сортировки),
// а дальше вызываем поиск непосредственно в dao методах
@DatabaseView(
    """
    SELECT house_id AS house, id, name, aliases, titles
    FROM characters
    ORDER BY name ASC
    """
)

data class CharacterItem(
    val id: String,
    val house: HouseType, //rel // было String, но String обращается house.title + 48:57
    val name: String,
    val titles: List<String>,
    val aliases: List<String>
)

@DatabaseView(
    """
    SELECT character.id, character.name, character.born, character.died, character.titles,
    character.aliases, character.house_id, character.mother, character.father, houses.words,
    mother.name AS m_name, mother.id AS m_id, mother.house_id AS m_house,
    father.name AS f_name, father.id AS f_id, father.house_id AS f_house
    FROM characters AS character
    LEFT JOIN characters AS mother ON character.mother = mother.id
    LEFT JOIN characters AS father ON character.father = father.id
    INNER JOIN houses ON character.house_id = houses.id
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
    @ColumnInfo(name = "house_id")
    val house: HouseType,
    @Embedded(prefix = "f_")
    val father: RelativeCharacter?,
    @Embedded(prefix = "m_")
    val mother: RelativeCharacter?
)

data class RelativeCharacter(
    val id: String,
    val name: String,
    val house: String //rel
)