package com.stadio.kitabu.ui.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stadio.kitabu.ui.catalog.components.BookCard
import com.stadio.kitabu.ui.catalog.components.CatalogFilterDropdown
import com.stadio.kitabu.ui.catalog.components.CatalogSearchBar
import com.stadio.kitabu.ui.components.StadioHeader

/**
 * Home/Catalog screen. Bottom navigation is provided by the outer NavGraph's
 * Scaffold - this composable is content-only so it can be reused for the
 * Search tab too (same grid, same ViewModel, different entry point).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    onBookClick: (bookId: Long) -> Unit,
    viewModel: CatalogViewModel = viewModel(factory = CatalogViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    StadioHeader()
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            Text(
                text = "Book Catalog",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 12.dp,
                    bottom = 16.dp
                )
            )

            CatalogFilterDropdown(
                selected = uiState.filter,
                onFilterSelected =
                    viewModel::onFilterSelected,
                modifier = Modifier.padding(
                    horizontal = 16.dp
                )
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            CatalogSearchBar(
                query = searchQuery,
                onQueryChange =
                    viewModel::onSearchQueryChanged,
                modifier = Modifier.padding(
                    horizontal = 16.dp
                )
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            when {
                uiState.isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.books.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("No books match your search or filter.")
                    }
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.books, key = { it.bookId }) { book ->
                            BookCard(book = book, onClick = onBookClick)
                        }
                    }
                }
            }
        }
    }
}
