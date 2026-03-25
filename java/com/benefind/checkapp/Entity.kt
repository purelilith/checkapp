package com.benefind.checkapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "additives")
data class Additive(
    @PrimaryKey
    val name: String,             // Основное поле (обязательное)
    val code: String?,            // Необязательное поле для Е-добавок
    val legality: String,         // Статус (Разрешен/Опасно и т.д.)
    val description: String,      // Описание свойств
    val category: String = "FOOD" // "FOOD" или "COSMETIC" для фильтрации
) : Serializable