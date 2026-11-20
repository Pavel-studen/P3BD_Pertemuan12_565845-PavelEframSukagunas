package com.example.notesapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(application: Application) : AndroidViewModel(application){
    private val dao = AppDatabase.getInstance(application).noteDao()

    // READ: Flow dari Room diubah menjadi StateFlow untuk antarmuka pengguna (UI)
    val notes: StateFlow<List<Note>> = dao.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // CREATE
    fun addNote(title: String, noteText: String){
        val textTitle = title.trim()
        val textNote = noteText.trim()
        if(textTitle.isEmpty() && textNote.isEmpty()) return
        viewModelScope.launch { dao.insert(Note(title = textTitle, note = textNote)) }
    }

    // UPDATE
    fun updateNote(note: Note, newTitle: String, newNote: String){
        val textTitle = newTitle.trim()
        val textNote = newNote.trim()
        if(textTitle.isEmpty() && textNote.isEmpty()) return
        viewModelScope.launch { dao.update(note.copy(title = textTitle, note = textNote)) }
    }

    // DELETE
    fun deleteNote(note: Note){
        viewModelScope.launch { dao.delete(note) }
    }
}
