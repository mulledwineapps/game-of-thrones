package ru.skillbranch.gameofthrones.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import ru.skillbranch.gameofthrones.data.local.entities.House

@Dao
// 48:35
interface HouseDao: BaseDao<House> {

    @Query("""
        SELECT COUNT(*) FROM houses
    """)
    // 48:50 для определения, есть ли необходимость запрашивать данные из сети
    suspend fun recordsCount(): Int

    // метод, который должен быть объявляен, как дефолтный и не может быть абстрактным
    @Transaction
    fun upsert(objList: List<House>) {
        // если значение = -1, вставка считается неуспешной, для всех таких элементов вызываем update
        insert(objList)
            .mapIndexed{index, l -> if (l == -1L) objList[index] else null}
            .filterNotNull()
            .also { if (it.isNotEmpty()) update(it) }
    }
}