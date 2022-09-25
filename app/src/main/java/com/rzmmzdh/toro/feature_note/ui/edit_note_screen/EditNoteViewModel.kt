package com.rzmmzdh.toro.feature_note.ui.edit_note_screen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rzmmzdh.toro.feature_note.domain.model.Note
import com.rzmmzdh.toro.feature_note.domain.usecase.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject

@HiltViewModel
class EditNoteViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    var openDialog = mutableStateOf(false)
        private set
    var currentNote = mutableStateOf(Note(
        title = "",
        body = "",
        lastModificationDate = Clock.System.now()))
        private set

    val errorMessages =
        listOf("هممم... فکر نکنم بشه فکر خالی رو ذخیره کرد.",
            "خالی، خالی، خالی...",
            "از مترادف های خالی، تُهی و خلوت می باشند.")

    init {
        savedStateHandle.get<Int>("noteId")?.let { noteId ->
            viewModelScope.launch {
                currentNote.value = currentNote.value.copy(id = noteId)
                if (currentNote.value.id != -1) {
                    noteUseCases.getNote(noteId).collectLatest { note ->
                        currentNote.value =
                            currentNote.value.copy(id = note.id,
                                title = note.title,
                                body = note.body,
                                lastModificationDate = note.lastModificationDate)
                    }
                }
            }
        }
    }

    fun onEvent(event: EditNoteEvent) {
        viewModelScope.launch {
            when (event) {
                is EditNoteEvent.SaveNote -> {
                    if (currentNote.value.id != -1) {
                        val existingNote = Note(
                            id = currentNote.value.id,
                            title = currentNote.value.title,
                            body = currentNote.value.body,
                            lastModificationDate = currentNote.value.lastModificationDate
                        )
                        noteUseCases.insertNote(existingNote)
                    } else {
                        val newNote = Note(title = currentNote.value.title,
                            body = currentNote.value.body,
                            lastModificationDate = currentNote.value.lastModificationDate)
                        noteUseCases.insertNote(newNote)
                    }
                }
                is EditNoteEvent.OnTitleChanged -> currentNote.value =
                    currentNote.value.copy(title = event.value)
                is EditNoteEvent.OnBodyChanged -> currentNote.value =
                    currentNote.value.copy(body = event.value)
                is EditNoteEvent.OpenDialog -> openDialog.value = !openDialog.value
                is EditNoteEvent.DeleteNote -> TODO()
            }
        }

    }

}