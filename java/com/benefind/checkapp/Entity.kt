package com.benefind.checkapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "additives")
data class Additive(
    @PrimaryKey val code: String,
    val name: String,
    val legality: String
)