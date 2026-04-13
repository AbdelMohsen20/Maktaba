package com.ElOuedUniv.maktaba.presentation.book.add

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ElOuedUniv.maktaba.presentation.common.UriImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookView(
    onBackClick: () -> Unit,
    viewModel: AddBookViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ADD BOOK", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text(text = "Cancel")
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.onAction(AddBookUiAction.OnAddClick) }) {
                        Text(text = "Confirm")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val titleError = uiState.titleError
            OutlinedTextField(
                value = uiState.title,
                onValueChange = { viewModel.onAction(AddBookUiAction.OnTitleChange(it)) },
                label = { Text("Title") },
                isError = titleError != null,
                modifier = Modifier.fillMaxWidth()
            )
            if (titleError != null) {
                Text(
                    text = titleError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            val isbnError = uiState.isbnError
            OutlinedTextField(
                value = uiState.isbn,
                onValueChange = { viewModel.onAction(AddBookUiAction.OnIsbnChange(it)) },
                label = { Text("ISBN") },
                isError = isbnError != null,
                modifier = Modifier.fillMaxWidth()
            )
            if (isbnError != null) {
                Text(
                    text = isbnError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            val pagesError = uiState.pagesError
            OutlinedTextField(
                value = uiState.nbPages,
                onValueChange = { viewModel.onAction(AddBookUiAction.OnPagesChange(it)) },
                label = { Text("Pages") },
                isError = pagesError != null,
                modifier = Modifier.fillMaxWidth()
            )
            if (pagesError != null) {
                Text(
                    text = pagesError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            OutlinedTextField(
                value = uiState.author,
                onValueChange = { viewModel.onAction(AddBookUiAction.OnAuthorChange(it)) },
                label = { Text("Author") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.categoryId,
                onValueChange = { viewModel.onAction(AddBookUiAction.OnCategoryChange(it)) },
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ADD COVER IMAGE",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            val imagePickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent(),
                onResult = { uri: Uri? ->
                    uri?.let { viewModel.onAction(AddBookUiAction.OnCoverImageSelected(it.toString())) }
                }
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clickable { imagePickerLauncher.launch("image/*") },
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                if (uiState.coverImageUri != null) {
                    UriImage(
                        uriString = uiState.coverImageUri,
                        modifier = Modifier.fillMaxSize(),
                        contentDescription = "Selected cover image"
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Text("📷 Tap to add a cover image")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBackClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = { viewModel.onAction(AddBookUiAction.OnAddClick) },
                    enabled = uiState.isFormValid,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save Book")
                }
            }
        }
    }
}
