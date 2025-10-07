package com.keagan.complete.data.notes

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.keagan.complete.data.todos.Todo
import com.keagan.complete.data.todos.TodosDao

@Database(
    entities = [
        Note::class,
        Todo::class
    ],
    version = 2,
    exportSchema = false
)
abstract class NotesDb : RoomDatabase() {
    abstract fun notes(): NotesDao
    abstract fun todos(): TodosDao

    companion object {
        @Volatile private var INSTANCE: NotesDb? = null
        fun get(context: Context): NotesDb =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    NotesDb::class.java, "notes.db"
                )
                    // Dev convenience: drop/recreate if schema changes.
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
