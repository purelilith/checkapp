package com.benefind.checkapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "additives")
data class Additive(
    @PrimaryKey
    val name: String,
    val code: String?,
    val legality: String,
    val description: String,
    val category: String = "FOOD"
) : Serializable