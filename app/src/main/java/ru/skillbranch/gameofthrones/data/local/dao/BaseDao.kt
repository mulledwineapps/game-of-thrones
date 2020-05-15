package ru.skillbranch.gameofthrones.data.local.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

// 47:19
interface BaseDao<T : Any> {
    // onConflict = OnConflictStrategy.IGNORE означает, что если это поле уже есть в базе (уже вставлено),
    // то повторная вставка не произойдёт
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    // возвращает коллекцию идентификаторов, которые были или не были вставлены
    fun insert(obj: List<T>): List<Long>

    @Update
    fun update(obj: List<T>)
}