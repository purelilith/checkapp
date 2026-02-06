package com.benefind.checkapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//класс базы данных room всегда абстрактный, наследуется от RoomDatabase, аннотация Database
//необходимо отдельны
    //пм файлом создать entity - этот дата класс оперирует таблицей созданной в этом файле БД
    ////БД может содержать любое количество таблиц, они создаются отдельным Entity и указываются под запятую здесь
    @Database (entities = [Item::class], version = 1)
    abstract class MainDB : RoomDatabase() { //привязываем интерфейс Dao (отдельный файл)
abstract fun getDao(): Dao

//  ??? companion
companion object {
    //      ??? синтаксис функций в котлине, контекст
    fun getDb(context: Context): MainDB {
//          функция Room для создания БД, в аргументах контекст, к какому классу относится, название с расщирением .db
            return Room.databaseBuilder(
                context.applicationContext,
//               ??? двоеточие 2 раза зачем
//                название базы данных
                MainDB::class.java,
                "adds.db"
            ).build() // функция создающая БД свойства которой мы прописали выше
        }

    }
}