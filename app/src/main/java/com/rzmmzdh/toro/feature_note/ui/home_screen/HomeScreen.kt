package com.rzmmzdh.toro.feature_note.ui.home_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.rzmmzdh.toro.R
import com.rzmmzdh.toro.feature_note.domain.model.Note
import com.rzmmzdh.toro.feature_note.ui.core.Screen
import com.rzmmzdh.toro.feature_note.ui.core.colorTransition
import com.rzmmzdh.toro.feature_note.ui.edit_note_screen.NoteCategory
import com.rzmmzdh.toro.theme.size
import com.rzmmzdh.toro.theme.space
import com.rzmmzdh.toro.theme.style
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    state: HomeScreenViewModel = hiltViewModel(),
    navController: NavController,
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val noteListState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()
    val inputFocusManager = LocalFocusManager.current
    Scaffold(
        modifier = modifier,
        topBar = {
            SearchableTopBar(
                title = state.search.value.searchQuery,
                onValueChange = {
                    state.onEvent(HomeScreenEvent.OnSearch(it))
                },
                onSearch = {
                    state.onEvent(HomeScreenEvent.OnSearch(it))
                    inputFocusManager.clearFocus()
                }
            )
        },
        floatingActionButton = {
            LargeFloatingActionButton(
                onClick = { navController.navigate(Screen.EditNote.route) },
                content = {
                    Icon(
                        Icons.Sharp.Add,
                        null,
                        modifier = Modifier.size(36.dp)
                    )
                })
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { paddingValues ->

        var selectedNoteCategory: NoteCategory? by remember { mutableStateOf(null) }
        var clearFilterButtonVisible by remember { mutableStateOf(false) }
        var noteDeleteNotificationVisible by remember { mutableStateOf(false) }


        Notes(
            paddingValues = paddingValues,
            notes = state.notes.value.notes,
            noteListState = noteListState,
            onFilterItemSelected = {
                state.onEvent(HomeScreenEvent.OnFilterItemSelect(it))
                coroutineScope.launch { noteListState.animateScrollToItem(0) }
                selectedNoteCategory = it
                clearFilterButtonVisible = true
            },
            clearFilterButtonVisible = clearFilterButtonVisible,
            onClearFilter = {
                state.onEvent(HomeScreenEvent.OnClearFilter)
                coroutineScope.launch {
                    noteListState.animateScrollToItem(0)
                }
                clearFilterButtonVisible = !clearFilterButtonVisible
                selectedNoteCategory = null
            }, onNoteClick = {
                navController.navigate(
                    Screen.EditNote.withNoteId(
                        it.id
                    )
                )
            }, onNoteDelete = {
                state.onEvent(HomeScreenEvent.OnNoteDelete(it))
                noteDeleteNotificationVisible = true
            },
            selectedCategory = selectedNoteCategory
        )
        if (noteDeleteNotificationVisible) {
            NoteDeleteNotification(
                key = { noteDeleteNotificationVisible },
                onDismiss = { noteDeleteNotificationVisible = false },
                onAction = {
                    state.onEvent(HomeScreenEvent.OnUndoNoteDelete)
                    if (noteDeleteNotificationVisible) noteDeleteNotificationVisible = false
                },
                snackbarHostState = snackBarHostState,
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchableTopBar(
    modifier: Modifier = Modifier,
    title: String,
    onValueChange: (String) -> Unit,
    onSearch: (String) -> Unit
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            TextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = title,
                onValueChange = { onValueChange(it) },
                textStyle = MaterialTheme.style.topBarTitle,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearch(title) }),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.toro_title),
                        modifier = Modifier.fillMaxWidth(),
                        color = colorTransition(
                            initialColor = MaterialTheme.colorScheme.primary,
                            targetColor = MaterialTheme.colorScheme.tertiary,
                            tweenAnimationDuration = 5000
                        ),
                        style = MaterialTheme.style.topBarTitle
                    )
                },
                colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent),
                singleLine = true,
            )
        })
}

@Composable
private fun Notes(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    notes: List<Note>,
    noteListState: LazyGridState,
    selectedCategory: NoteCategory?,
    onFilterItemSelected: (NoteCategory) -> Unit,
    clearFilterButtonVisible: Boolean,
    onClearFilter: () -> Unit,
    onNoteClick: (Note) -> Unit,
    onNoteDelete: (Note) -> Unit
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = BottomCenter) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                )
        ) {
            NoteFilter(
                selectedCategory = selectedCategory,
                onFilterItemSelected = {
                    onFilterItemSelected(it)
                },
            )
            NoteList(
                listState = noteListState,
                notes = notes,
                onNoteClick = onNoteClick,
                onNoteDelete = onNoteDelete,
            )
        }
        ClearFilter(
            paddingValues = paddingValues,
            onClearFilter = onClearFilter,
            clearFilterButtonVisible = clearFilterButtonVisible
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ClearFilter(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues,
    onClearFilter: () -> Unit,
    clearFilterButtonVisible: Boolean
) {
    AnimatedVisibility(
        visible = clearFilterButtonVisible,
    ) {
        ElevatedFilterChip(
            modifier = modifier.padding(bottom = paddingValues.calculateBottomPadding()),
            selected = false,
            onClick = { onClearFilter() },
            label = {
                Text(
                    stringResource(R.string.clear_filter),
                    style = MaterialTheme.style.clearFilter
                )
            },
            colors = FilterChipDefaults.elevatedFilterChipColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun NoteFilter(
    modifier: Modifier = Modifier,
    selectedCategory: NoteCategory?,
    onFilterItemSelected: (NoteCategory) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(start = 8.dp, end = 8.dp)
            .horizontalScroll(rememberScrollState(), reverseScrolling = true),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = CenterVertically,
    ) {
        NoteCategory.values().forEach { category ->
            ElevatedFilterChip(
                modifier = Modifier.padding(4.dp),
                label = {
                    Text(
                        category.title,
                        style = MaterialTheme.style.categoryItem
                    )
                },
                selected = (category == selectedCategory),
                onClick = {
                    onFilterItemSelected(category)
                },
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NoteList(
    modifier: Modifier = Modifier,
    listState: LazyGridState,
    notes: List<Note>,
    onNoteClick: (Note) -> Unit,
    onNoteDelete: (Note) -> Unit,
) {
    LazyVerticalGrid(
        modifier = modifier
            .fillMaxSize()
            .padding(
                start = MaterialTheme.size.noteCardListPadding,
                end = MaterialTheme.size.noteCardListPadding
            ),
        state = listState,
        columns = GridCells.Fixed(2)
    ) {
        items(items = notes, key = { it.id }) {
            Card(
                modifier = Modifier
                    .size(MaterialTheme.size.noteCard)
                    .padding(8.dp)
                    .animateItemPlacement()
                    .clickable(onClick = { onNoteClick(it) }
                    )

            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    NoteTag(tag = it.category.title)
                    NoteDeleteButton(onDeleteIconClick = { onNoteDelete(it) })
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(MaterialTheme.space.noteItemPadding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        NoteTitle(note = it)
                        NoteBody(note = it)
                    }
                }

            }
        }
    }
}

@Composable
private fun NoteTag(modifier: Modifier = Modifier, tag: String) {
    Box(
        modifier = modifier
            .fillMaxSize(), contentAlignment = BottomEnd
    ) {
        Text(
            tag.substringAfter(delimiter = stringResource(R.string.white_space)).trim(),
            modifier = Modifier
                .alpha(0.5F)
                .padding(end = 4.dp),
            style = MaterialTheme.style.noteTag,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun NoteDeleteButton(
    modifier: Modifier = Modifier,
    onDeleteIconClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize(), contentAlignment = TopStart
    ) {
        IconButton(
            onClick = onDeleteIconClick,
            modifier = Modifier
                .size(18.dp)
        ) {
            Icon(
                Icons.Rounded.Close, null, modifier = Modifier
                    .size(18.dp)
                    .alpha(0.5F)
            )
        }
    }
}

@Composable
private fun NoteTitle(modifier: Modifier = Modifier, note: Note) {
    Text(
        modifier = modifier,
        text = note.title,
        style = MaterialTheme.style.noteCardTitle, maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun NoteBody(modifier: Modifier = Modifier, note: Note) {
    Text(
        modifier = modifier,
        text = note.body,
        style =
        MaterialTheme.style.noteCardBody,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis

    )
}

@Composable
private fun NoteDeleteNotification(
    modifier: Modifier = Modifier,
    key: Any,
    onDismiss: () -> Unit,
    onAction: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    LaunchedEffect(key1 = key) {
        val noteDeletedMessage =
            snackbarHostState.showSnackbar(
                message = "Note deleted.",
                actionLabel = "Undo",
                duration = SnackbarDuration.Short
            )
        when (noteDeletedMessage) {
            SnackbarResult.Dismissed -> onDismiss()
            SnackbarResult.ActionPerformed -> {
                onAction()
            }
        }

    }
}

