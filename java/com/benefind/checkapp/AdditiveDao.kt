package com.benefind.checkapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AdditiveDao {
    @Query("SELECT * FROM additives")
    suspend fun getAll(): List<Additive>

    @Query("SELECT * FROM additives WHERE code LIKE :query OR name LIKE :query")
    suspend fun search(query: String): List<Additive>

    // Поиск по списку слов: проверяем и код, и имя
    // Мы используем оператор IN для кодов и проверяем, содержится ли имя в списке слов
    @Query("SELECT * FROM additives WHERE UPPER(code) IN (:words) OR UPPER(name) IN (:words)")
    suspend fun getByWords(words: List<String>): List<Additive>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(additives: List<Additive>)
}