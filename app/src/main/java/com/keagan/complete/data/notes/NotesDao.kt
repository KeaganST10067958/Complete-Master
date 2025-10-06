package com.keagan.complete.data.notes

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Delete


@Dao
interface NotesDao {
    @Query("SELECT * FROM notes ORDER BY pinned DESC, createdAt DESC")
    fun observeAll(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE pinned = 1 ORDER BY createdAt DESC")
    fun observePinned(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE title LIKE :q OR text LIKE :q ORDER BY pinned DESC, createdAt DESC")
    fun searchAll(q: String): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(note: Note)

    @Delete suspend fun delete(note: Note)

    @Query("UPDATE notes SET pinned = :p WHERE id = :id")
    suspend fun setPinned(id: Long, p: Boolean)
}
