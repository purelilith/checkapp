package com.benefind.checkapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Additive::class], version = 3)
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
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val database = getDatabase(context)
                                prepopulateDatabase(context, database.additiveDao())
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun prepopulateDatabase(context: Context, dao: AdditiveDao) {
            try {
                val jsonString =
                    context.assets.open("additives.json").bufferedReader().use { it.readText() }
                val jsonArray = org.json.JSONArray(jsonString)

                val additivesList = mutableListOf<Additive>()

                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)

                    val additive = Additive(
                        name = item.getString("name"),
                        code = if (item.has("code")) item.getString("code") else null,
                        legality = item.getString("legality"),
                        description = item.getString("description"),
                        category = if (item.has("category")) item.getString("category") else "COSMETIC"
                    )
                    additivesList.add(additive)
                }

                dao.insertAll(additivesList)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}