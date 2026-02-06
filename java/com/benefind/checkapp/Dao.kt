package com.benefind.checkapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// интерфейс созданный для управления БД, позволяет показать все элементы таблицы
@Dao
interface Dao {
    @Insert
    fun insertItem(item: Item)
    @Query("SELECT * FROM items")
    fun getAllItem():Flow<List<Item>> //подключаем flow(подключается в Gradle), он выдает изменения в БД прям на экране смартфона
}