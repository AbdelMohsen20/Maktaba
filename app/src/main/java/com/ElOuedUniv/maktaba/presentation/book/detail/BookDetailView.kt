package com.ElOuedUniv.maktaba.presentation.book.detail

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ElOuedUniv.maktaba.presentation.common.UriImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailView(
    onBackClick: () -> Unit,
    viewModel: BookDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) {
            onBackClick()
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            viewModel.onAction(BookDetailUiAction.EditCoverChanged(uri?.toString()))
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.book?.title ?: "Book Detail") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.isEditing) {
                        IconButton(onClick = { viewModel.onAction(BookDetailUiAction.SaveBook) }) {
                            Icon(Icons.Default.Check, contentDescription = "Save")
                        }
                        IconButton(onClick = { viewModel.onAction(BookDetailUiAction.CancelEdit) }) {
                            Icon(Icons.Default.Close, contentDescription = "Cancel")
                        }
                    } else {
                        IconButton(onClick = { viewModel.onAction(BookDetailUiAction.ToggleEdit) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.book != null) {
                if (uiState.isEditing) {
                    OutlinedTextField(
                        value = uiState.editTitle,
                        onValueChange = { viewModel.onAction(BookDetailUiAction.EditTitleChanged(it)) },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = uiState.editAuthor,
                        onValueChange = { viewModel.onAction(BookDetailUiAction.EditAuthorChanged(it)) },
                        label = { Text("Author") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = uiState.editNbPages,
                        onValueChange = { viewModel.onAction(BookDetailUiAction.EditNbPagesChanged(it)) },
                        label = { Text("Pages") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = uiState.editIsbn,
                        onValueChange = {},
                        label = { Text("ISBN") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        readOnly = true
                    )

                    Text(text = "Cover image", style = MaterialTheme.typography.titleMedium)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clickable { imagePickerLauncher.launch("image/*") },
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        if (uiState.editCoverImageUri != null) {
                            UriImage(
                                uriString = uiState.editCoverImageUri,
                                modifier = Modifier.fillMaxSize(),
                                contentDescription = "Selected cover image"
                            )
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Tap to choose a cover image")
                            }
                        }
                    }
                } else {
                    val book = uiState.book!!
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        UriImage(
                            uriString = book.imageUrl,
                            modifier = Modifier.fillMaxSize(),
                            contentDescription = "Book cover"
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = book.author.ifBlank { "Unknown author" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Reading Overview",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                DetailsBlock(
                                    title = "Status",
                                    value = if (book.nbPages > 0) "Reading" else "Not started",
                                    icon = Icons.Default.Info
                                )
                                DetailsBlock(
                                    title = "Pages",
                                    value = if (book.nbPages > 0) "${book.nbPages}" else "Not set",
                                    icon = Icons.Default.Menu
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            DetailChip(icon = Icons.Default.Info, label = "ISBN", value = book.isbn)
                            Spacer(modifier = Modifier.height(12.dp))
                            DetailChip(icon = Icons.Default.Info, label = "Author", value = book.author.ifBlank { "Unknown" })
                        }
                    }
                }
            } else {
                uiState.errorMessage?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                } ?: Text("Book not found")
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Book") },
            text = { Text("Are you sure you want to remove this book from your library?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.onAction(BookDetailUiAction.DeleteBook)
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DetailChip(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun DetailsBlock(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(modifier = Modifier.width(140.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}
