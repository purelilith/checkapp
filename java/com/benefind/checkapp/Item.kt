package com.benefind.checkapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

// ??? data классы в котлин
// аннотация entity создает таблицу с именем items
@Entity (tableName = "items")
data class Item (
    // аннотация PrimaryKey автоматически присваивает id элементам таблицы
    @PrimaryKey(autoGenerate = true)
    // идентификаторы присваиваются с нуля в типе данных Integer
    var id: Int? = null,
    // создаем колонки таблицы с любыми названиями и любое количество при помощи аннотации
    // name и number - колонки таблицы с типом данных string
    @ColumnInfo(name = "number")
    var number: String,
    @ColumnInfo(name = "name")
    var name: String,
)