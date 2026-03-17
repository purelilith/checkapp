package com.benefind.checkapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Additive::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun additiveDao(): AdditiveDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "additives_database"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            prepopulateDatabase(context)
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private fun prepopulateDatabase(context: Context) {
            CoroutineScope(Dispatchers.IO).launch {
                val dao = getDatabase(context).additiveDao()
                dao.insertAll(listOf(
                    Additive("Е100", "Куркумин", "Разрешен"),
                    Additive("Е101", "Рибофлавин", "Разрешен"),
                    Additive("Е102", "Тартразин", "Запрещен"),
                    Additive("Е103", "Алканин", "Запрещен"),
                    Additive("Е104", "Желтый хеналиновый", "Запрещен"),
                    Additive("Е105", "Желтый прочный", "Запрещен"),
                    Additive("Е110", "Желтый «солнечный закат»", "Разрешен"),
                    Additive("Е120", "Кармины", "Разрешен"),
                    Additive("Е121", "Цитрусовый красный 2", "Запрещен"),
                    Additive("Е123", "Амарант", "Запрещен"),
                    Additive("Е240", "Формальдегид", "Запрещен"),
                    Additive("Е621", "Глутамат натрия", "Разрешен")
                ))
            }
        }
    }
}