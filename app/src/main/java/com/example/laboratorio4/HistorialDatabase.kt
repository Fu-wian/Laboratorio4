package com.example.laboratorio4

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class HistorialDatabase(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME    = "laboratorio4.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_HISTORY  = "history"
        const val COL_ID         = "id"
        const val COL_FIELD      = "field"
        const val COL_OLD_VALUE  = "old_value"
        const val COL_NEW_VALUE  = "new_value"
        const val COL_TIMESTAMP  = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_HISTORY (
                $COL_ID        INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_FIELD     TEXT NOT NULL,
                $COL_OLD_VALUE TEXT NOT NULL,
                $COL_NEW_VALUE TEXT NOT NULL,
                $COL_TIMESTAMP TEXT NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_HISTORY")
        onCreate(db)
    }

    fun insertEntry(entry: HistorialEntry): Long {
        val values = ContentValues().apply {
            put(COL_FIELD,     entry.field)
            put(COL_OLD_VALUE, entry.oldValue)
            put(COL_NEW_VALUE, entry.newValue)
            put(COL_TIMESTAMP, entry.timestamp)
        }
        return writableDatabase.insert(TABLE_HISTORY, null, values)
    }

    fun getAllEntries(fieldFilter: String? = null): List<HistorialEntry> {
        val selection     = if (fieldFilter != null) "$COL_FIELD = ?" else null
        val selectionArgs = if (fieldFilter != null) arrayOf(fieldFilter) else null

        val cursor = readableDatabase.query(
            TABLE_HISTORY,
            null,
            selection,
            selectionArgs,
            null, null,
            "$COL_TIMESTAMP DESC"
        )

        val entries = mutableListOf<HistorialEntry>()
        cursor.use { c ->
            val idIdx        = c.getColumnIndexOrThrow(COL_ID)
            val fieldIdx     = c.getColumnIndexOrThrow(COL_FIELD)
            val oldValueIdx  = c.getColumnIndexOrThrow(COL_OLD_VALUE)
            val newValueIdx  = c.getColumnIndexOrThrow(COL_NEW_VALUE)
            val timestampIdx = c.getColumnIndexOrThrow(COL_TIMESTAMP)

            while (c.moveToNext()) {
                entries += HistorialEntry(
                    id        = c.getLong(idIdx),
                    field     = c.getString(fieldIdx),
                    oldValue  = c.getString(oldValueIdx),
                    newValue  = c.getString(newValueIdx),
                    timestamp = c.getString(timestampIdx)
                )
            }
        }
        return entries
    }

    fun clearAllEntries(): Int =
        writableDatabase.delete(TABLE_HISTORY, null, null)

    fun countEntries(): Int {
        val cursor = readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM $TABLE_HISTORY", null
        )
        var count = 0
        cursor.use { if (it.moveToFirst()) count = it.getInt(0) }
        return count
    }
}