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

        @Query("SELECT * FROM additives WHERE code IN (:codes)")
        suspend fun getByCodes(codes: List<String>): List<Additive>

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertAll(additives: List<Additive>)
    }