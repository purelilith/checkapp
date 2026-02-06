package com.benefind.checkapp
import android.database.sqlite.SQLiteDatabase
data class Add(val name: String, val code: String)

fun getAdditives(db: SQLiteDatabase): List<Additive> {
    val list = mutableListOf<Additive>()
    val cursor = db.rawQuery("SELECT name, code FROM additives", null)
    if (cursor.moveToFirst()) {
        do {
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            val code = cursor.getString(cursor.getColumnIndexOrThrow("code"))
            list.add(Additive(name, code))
        } while (cursor.moveToNext())
    }
    cursor.close()
    return list
}
