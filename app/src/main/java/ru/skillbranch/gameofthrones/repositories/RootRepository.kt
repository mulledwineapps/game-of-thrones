package ru.skillbranch.gameofthrones.repositories

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import ru.skillbranch.gameofthrones.AppConfig
import ru.skillbranch.gameofthrones.data.local.DbManager
import ru.skillbranch.gameofthrones.data.local.dao.CharactersDao
import ru.skillbranch.gameofthrones.data.local.dao.HouseDao
import ru.skillbranch.gameofthrones.data.local.entities.Character
import ru.skillbranch.gameofthrones.data.local.entities.CharacterFull
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.data.local.entities.House
import ru.skillbranch.gameofthrones.data.remote.NetworkService
import ru.skillbranch.gameofthrones.data.remote.RestService
import ru.skillbranch.gameofthrones.data.remote.res.CharacterRes
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes

// 53:24 мастер-класса
// 57:25 верхняя часть файла
object RootRepository {

    private val api: RestService = NetworkService.api
    private val houseDao: HouseDao = DbManager.db.houseDao()
    private val charactersDao: CharactersDao = DbManager.db.charactersDao()

    private val errHandler = CoroutineExceptionHandler { _, exception ->
        println("Caught $exception")
        exception.printStackTrace()
    }

    // 57:37 ! важно: если вы используете в качестве coroutine контекста просто Job,
    // тогда если одна из его дочерних корутин будет остановлена с ошибкой,
    // это приведёт к остановке и отмене родительской корутины и, соответственно, все дочерние
    // корутины данного скоупа
    // с SupervisorJob ошибка дочерней корутины не вызовет остановки родительской
    // viewModelScope (см RootViewModel), например, также использует SupervisorJob
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO + errHandler)

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    suspend fun getNeedHouses(vararg houseNames: String): List<HouseRes> {
        return houseNames.fold(mutableListOf()) { acc, title ->
            acc.also { it.add(api.houseByName(title).first()) }
        }
    }

    /**
     * Получение данных о всех домах из сети
     * @param result - колбек содержащий в себе список данных о домах
     */
    // 58:14
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getAllHouses(result: (houses: List<HouseRes>) -> Unit) {
        scope.launch {
            val houses = mutableListOf<HouseRes>()
            var page = 0
            while (true) {
                val res = api.houses(++page)
                if (res.isEmpty()) break
                houses.addAll(res)
            }
            result(houses) // строка-отсебятина
        }
    }

    /**
     * Получение данных о требуемых домах по их полным именам из сети
     * @param houseNames - массив полных названий домов (смотри AppConfig)
     * @param result - колбек содержащий в себе список данных о домах
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getNeedHouses(vararg houseNames: String, result: (houses: List<HouseRes>) -> Unit) {
        scope.launch { result(getNeedHouses(*houseNames)) }
    }

    /**
     * Получение данных о требуемых домах по их полным именам и персонажах в каждом из домов из сети
     * @param houseNames - массив полных названий домов (смотри AppConfig)
     * @param result - колбек содержащий в себе список данных о доме и персонажей в нем (Дом - Список Персонажей в нем)
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getNeedHouseWithCharacters(
        vararg houseNames: String,
        result: (houses: List<Pair<HouseRes, List<CharacterRes>>>) -> Unit
    ) {
        scope.launch { result(needHouseWithCharacters(*houseNames)) } // timing?
    }

    /**
     * Запись данных о домах в DB
     * @param houses - Список персонажей (модель HouseRes - модель ответа из сети)
     * необходимо произвести трансформацию данных
     * @param complete - колбек о завершении вставки записей db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun insertHouses(houses: List<HouseRes>, complete: () -> Unit) {
        scope.launch {
            houseDao.insert(houses.map { it.toHouse() })
            complete.invoke()
        }
    }

    /**
     * Запись данных о пересонажах в DB
     * @param characters - Список персонажей (модель CharacterRes - модель ответа из сети)
     * необходимо произвести трансформацию данных
     * @param complete - колбек о завершении вставки записей db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun insertCharacters(characters: List<CharacterRes>, complete: () -> Unit) {
        scope.launch {
            charactersDao.insert(characters.map { it.toCharacter() })
            complete.invoke()
        }
    }

    /**
     * При вызове данного метода необходимо выполнить удаление всех записей в db
     * @param complete - колбек о завершении очистки db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun dropDb(complete: () -> Unit) {
        scope.launch {
            DbManager.db.clearAllTables()
            complete.invoke()
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
        scope.launch {
            val character = charactersDao.findCharactersList(name)
            result(character)
        }
    }

    /**
     * Поиск персонажа по его идентификатору, должен вернуть полную информацию о персонаже
     * и его родственных отношения - смотри модель CharacterFull
     * @param id - идентификатор персонажа
     * @param result - колбек содержащий в себе полную информацию о персонаже
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun findCharacterFullById(id: String, result: (character: CharacterFull) -> Unit) {
        scope.launch {
            val character = charactersDao.findCharacterFull(id)
            result(character)
        }
    }

    /**
     * Метод возвращет true если в базе нет ни одной записи, иначе false
     * @param result - колбек о завершении очистки db
     */
    fun isNeedUpdate(result: (isNeed: Boolean) -> Unit) {
        scope.launch {
            result(isNeedUpdate())
        }
    }

    // 54:40
    suspend fun needHouseWithCharacters(vararg houseNames: String): List<Pair<HouseRes, List<CharacterRes>>> {
        val result = mutableListOf<Pair<HouseRes, List<CharacterRes>>>()
        val houses: List<HouseRes> = getNeedHouses(*houseNames)

        // получение данных происходит в несколько потоков
        // scope - локальный scope, который определён для RootRepository
        scope.launch {
            // проходим по очереди по всем домам, перебираем всех его участников,
            // для каждого из них создаём дочерню корутину
            // (в неё передаётся название корутины, это делается для отладки)
            // далее получаем персонажа, устанавливаем ему короткое имя и добавляем в коллекцию
            houses.forEach { house ->
                var i = 0
                println("houseByName ${house.url} scope this ctx ${this.coroutineContext}")
                val characters = mutableListOf<CharacterRes>()
                result.add(house to characters)
                house.members.forEach { character ->
                    launch(CoroutineName("character $character")) {
                        api.character(character)
                            .apply { houseId = house.shortName }
                            .also { characters.add(it) }
                        i++
                        println(
                            "complete coroutine $i/${house.swornMembers.size} ${house.name}" +
                                    "${this.coroutineContext[CoroutineName]}"
                        )
                    }
                }
            }
        }.join()
        // coroutine builder launch возвращает объект job
        // вызов на нём suspend функции join() означает, что нужно
        // дождаться выполнения всех дочерних корутин, которые в данном job объявлены
        // и тогда только завершится родительская корутина
        //
        // в логах можно увидеть, что персонажи получаются не последовательно, а асинхронно -
        // параллельно с получением персонажей одного дома, запускается корутина на получение
        // персонажей другого дома
        return result
    }

    // синхронизирует все данные между сетью и локальным хранилищем
    // * перед AppConfig.NEED_HOUSES позволяет передать массив строк в качестве vararg
    suspend fun sync() {
        val pairs = needHouseWithCharacters(*AppConfig.NEED_HOUSES)
        val initial = mutableListOf<House>() to mutableListOf<Character>()

        val lists = pairs.fold(initial) { acc, (houseRes, charactersList) ->
            val house: House = houseRes.toHouse()
            val characters: List<Character> = charactersList.map { it.toCharacter() }
            acc.also { (hs, ch) ->
                hs.add(house)
                ch.addAll((characters))
            }
        }

        houseDao.upsert(lists.first)
        charactersDao.upsert(lists.second)
    }

    suspend fun isNeedUpdate(): Boolean = houseDao.recordsCount() == 0

    fun findCharacters(houseName: String): LiveData<List<CharacterItem>> =
        charactersDao.findCharacters(houseName)

    fun findCharacter(characterId: String): LiveData<CharacterFull> =
        charactersDao.findCharacter(characterId)

}