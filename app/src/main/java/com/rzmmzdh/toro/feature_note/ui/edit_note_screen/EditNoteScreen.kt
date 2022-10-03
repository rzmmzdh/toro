package com.rzmmzdh.toro.feature_note.ui.edit_note_screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.rzmmzdh.toro.R
import com.rzmmzdh.toro.feature_note.ui.core.Constant.emptyNoteAlertMessages
import com.rzmmzdh.toro.feature_note.ui.core.Screen
import com.rzmmzdh.toro.feature_note.ui.core.component.ToroFab
import com.rzmmzdh.toro.feature_note.ui.core.navigateTo
import com.rzmmzdh.toro.theme.size
import com.rzmmzdh.toro.theme.style
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(state: EditNoteViewModel = hiltViewModel(), navController: NavController) {
    val inputFocusManager = LocalFocusManager.current
    Scaffold(
        topBar = {
            EditNoteTopBar(title = state.currentNote.value.category.title, onCategoryClick = {
                state.onEvent(EditNoteEvent.OnCategorySelect(it))
            })
        },
        floatingActionButton = {
            ToroFab(onClick = {
                state.onEvent(EditNoteEvent.OnNoteSave)
                inputFocusManager.clearFocus()
                if (!state.currentNote.value.isEmpty) {
                    navController.navigateTo(
                        route = Screen.Home.route
                    )
                }
            }, icon = {
                Icon(
                    Icons.Rounded.Check,
                    null,
                    modifier = Modifier.size(32.dp)
                )
            })
        },
    ) { paddingValues ->
        EditNoteBody(paddingValues = paddingValues, state, onDone = {
            state.onEvent(EditNoteEvent.OnNoteSave)
            inputFocusManager.clearFocus()
            if (!state.currentNote.value.isEmpty) {
                navController.navigateTo(Screen.Home.route)
            }
        })

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditNoteTopBar(title: String, onCategoryClick: (NoteCategory) -> Unit) {
    val menuExpanded = remember { mutableStateOf(false) }
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = { menuExpanded.value = !menuExpanded.value },
                ) {
                    Icon(Icons.Rounded.KeyboardArrowDown, null, modifier = Modifier.size(24.dp))
                }
                Text(
                    text = title,
                    style = MaterialTheme.style.topBarTitle,
                )
            }
            DropdownMenu(
                expanded = menuExpanded.value,
                onDismissRequest = { menuExpanded.value = !menuExpanded.value },
            ) {
                val categories = NoteCategory.values()
                categories.forEach {
                    DropdownMenuItem(text = {
                        Text(
                            text = it.title,
                            style = MaterialTheme.style.categoryList,
                            modifier = Modifier.fillMaxSize()
                        )
                    }, onClick = {
                        onCategoryClick(it)
                        menuExpanded.value = !menuExpanded.value
                    })
                }

            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun EditNoteBody(
    paddingValues: PaddingValues,
    state: EditNoteViewModel,
    onDone: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding(),
                start = MaterialTheme.size.noteInputBoxPadding,
                end = MaterialTheme.size.noteInputBoxPadding
            ),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NoteTitleInput(value = state.currentNote.value.title, onValueChange = {
            state.onEvent(EditNoteEvent.OnTitleChange(it))
        })
        NoteBodyInput(
            value =
            state.currentNote.value.body,
            onValueChange = {
                state.onEvent(EditNoteEvent.OnBodyChange(it))
            },
        )
    }
    EmptyNoteAlert(state)

}

@Composable
private fun EmptyNoteAlert(state: EditNoteViewModel) {
    if (state.currentNote.value.isEmpty) {
        AlertDialog(
            onDismissRequest = {
                state.onEvent(EditNoteEvent.OnAlertDismiss)
            },
            icon = { Icon(Icons.Rounded.Info, null) },
            title = {
                Text(
                    text = emptyNoteAlertMessages[Random.nextInt(until = emptyNoteAlertMessages.size)],
                    style = MaterialTheme.style.errorBoxTitle,
                    textAlign = TextAlign.Center,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        state.onEvent(EditNoteEvent.OnAlertDismiss)
                    }
                ) {
                    Text(stringResource(R.string.ok), style = MaterialTheme.style.errorBoxButton)
                }
            },
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun NoteTitleInput(value: String, onValueChange: (String) -> Unit) {
    val inputFocusManager = LocalFocusManager.current
    TextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = MaterialTheme.style.noteTitleInputValue,
        placeholder = {
            Text(
                stringResource(id = R.string.subject),
                style = MaterialTheme.style.noteTitleInputPlaceholder,
                modifier = Modifier.fillMaxWidth()
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { inputFocusManager.moveFocus(FocusDirection.Down) }),
        modifier = Modifier
            .fillMaxWidth()
            .height(MaterialTheme.size.noteTitleInputHeight)
            .padding(MaterialTheme.size.noteTitleInputPadding),
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun NoteBodyInput(
    value: String,
    onValueChange: (String) -> Unit,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = MaterialTheme.style.noteBodyInputValue,
        placeholder = {
            Text(
                stringResource(R.string.body),
                style = MaterialTheme.style.noteBodyInputPlaceholder,
                modifier = Modifier.fillMaxWidth()
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(MaterialTheme.size.noteBodyInputPadding),
    )
}