package com.benefind.checkapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AdditiveDao {
    @Query("SELECT * FROM additives")
    suspend fun getAll(): List<Additive>

    // Получить только еду или только косметику
    @Query("SELECT * FROM additives WHERE category = :category")
    suspend fun getAllByCategory(category: String): List<Additive>

    // Поиск по подстроке (для EditText)
    @Query("""
        SELECT * FROM additives 
        WHERE (UPPER(name) LIKE UPPER(:query)) 
        OR (code IS NOT NULL AND UPPER(code) LIKE UPPER(:query))
    """)
    suspend fun search(query: String): List<Additive>

    // Поиск точных совпадений по списку слов (для OCR)
    @Query("""
        SELECT * FROM additives 
        WHERE UPPER(name) IN (:words) 
        OR (code IS NOT NULL AND UPPER(code) IN (:words))
    """)
    suspend fun getByWords(words: List<String>): List<Additive>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(additives: List<Additive>)
}