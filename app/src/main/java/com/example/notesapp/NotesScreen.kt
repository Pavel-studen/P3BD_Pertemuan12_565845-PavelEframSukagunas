package com.example.notesapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notesapp.ui.theme.NotesAppTheme

@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    vm: NotesViewModel = viewModel()
){
    val notes by vm.notes.collectAsState()
    var inputTitle by rememberSaveable{ mutableStateOf("") }
    var inputNote by rememberSaveable{ mutableStateOf("") }
    var editingId by rememberSaveable{ mutableStateOf<Long?>(null) }
    var editingTitle by rememberSaveable{ mutableStateOf("") }
    var editingNote by rememberSaveable{ mutableStateOf("") }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ){
        Text(text = "Catatan Saya", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))

        // Form input (CREATE)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ){
            OutlinedTextField(
                value = inputTitle,
                onValueChange = { inputTitle = it },
                label = { Text("Judul catatan") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = inputNote,
                onValueChange = { inputNote = it },
                label = { Text("Isi catatan") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = {
                vm.addNote(inputTitle, inputNote)
                inputTitle = ""
                inputNote = ""
            }){
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                Text("Tambah")
            }
        }
        Text(
            text = "Jumlah catatan: ${notes.size}",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Daftar catatan (READ)
        if(notes.isEmpty()){
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                Text("Belum ada catatan", color = Color.Gray)
            }
        } else{
            LazyColumn(modifier = Modifier.fillMaxSize()){
                items(notes, key = { it.id }){ note ->
                    if(editingId == note.id){
                        NoteItemEditing(
                            title = editingTitle,
                            note = editingNote,
                            onTitleChange = { editingTitle = it },
                            onNoteChange = { editingNote = it },
                            onSave = {
                                vm.updateNote(note, editingTitle, editingNote)
                                editingId = null
                            },
                            onCancel = { editingId = null }
                        )
                    } else {
                        NoteItem(
                            note = note,
                            onEdit = {
                                editingId = note.id
                                editingTitle = note.title
                                editingNote = note.note
                            },
                            onDelete = { vm.deleteNote(note) }
                        )
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun NoteItem(
    note: Note,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
){
    ListItem(
        headlineContent = { Text(note.title) },
        supportingContent = { Text("ID: ${note.id}") },
        trailingContent = {
            Row{
                IconButton(onClick = onEdit){
                    Icon(painterResource(R.drawable.ic_edit), "Edit",
                        tint = Color(0xFF2196F3))
                }
                IconButton(onClick = onDelete){
                    Icon(painterResource(R.drawable.ic_delete), "Hapus",
                        tint = Color(0xFFF44336))
                }
            }
        }
    )
}

@Composable
fun NoteItemEditing(
    title: String,
    note: String,
    onTitleChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ){
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ){
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Ubah judul") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                label = { Text("Ubah catatan") },
                modifier = Modifier.fillMaxWidth()
            )}
        IconButton(onClick = onSave){
            Icon(painterResource(R.drawable.ic_check), "Simpan",
                tint = Color(0xFF4CAF50))
        }
        IconButton(onClick = onCancel){
            Icon(painterResource(R.drawable.ic_close), "Batal")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoteItemPreview(){
    NotesAppTheme{
        NoteItem(note = Note(id = 1, title = "Belajar Room Database", note = "Implementasi CRUD dengan Jetpack Compose"))
    }
}
