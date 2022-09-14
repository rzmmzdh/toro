package com.rzmmzdh.toro.feature_note.data.repository

import com.rzmmzdh.toro.feature_note.data.datasource.NoteDataSource
import com.rzmmzdh.toro.feature_note.domain.model.Note
import com.rzmmzdh.toro.feature_note.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(private val localDataSource: NoteDataSource) :
    NoteRepository {
    override suspend fun insertNote(note: Note) {
        localDataSource.insertNote(note)
    }

    override fun getNote(id: Int): Flow<Note> {
        return localDataSource.getNote(id)
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return localDataSource.getAllNotes()
    }

    override suspend fun deleteNote(note: Note) {
        localDataSource.deleteNote(note)
    }
}