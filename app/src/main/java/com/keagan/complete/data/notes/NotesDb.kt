package com.keagan.complete.data.notes

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class NotesDb : RoomDatabase() {
    abstract fun notes(): NotesDao

    companion object {
        @Volatile private var INSTANCE: NotesDb? = null
        fun get(context: Context): NotesDb =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    NotesDb::class.java, "notes.db"
                ).build().also { INSTANCE = it }
            }
    }
}
