package com.stadio.kitabu.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.stadio.kitabu.KitabuApp
import com.stadio.kitabu.data.local.entity.BookEntity
import com.stadio.kitabu.data.repository.LibraryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

/** Mirrors the "Available" dropdown from the Catalog wireframe. */
enum class CatalogFilter { ALL, AVAILABLE, BORROWED }

data class CatalogUiState(
    val books: List<BookEntity> = emptyList(),
    val filter: CatalogFilter = CatalogFilter.ALL,
    val isLoading: Boolean = true
)

class CatalogViewModel(
    private val repository: LibraryRepository
) : ViewModel() {

    private val _filter = MutableStateFlow(CatalogFilter.ALL)
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    /**
     * Switches the underlying Room query whenever the search text changes:
     * blank query -> full catalog, non-blank -> the DAO's SQL LIKE search.
     * flatMapLatest cancels the previous Flow collection so a fast typist
     * never has two competing queries updating the UI out of order.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private val sourceBooks =
        _searchQuery.flatMapLatest { query ->

            val sanitizedQuery = query.trim()

            if (sanitizedQuery.isBlank()) {
                repository.getAllBooks()
            } else {
                repository.searchBooks(
                    sanitizedQuery
                )
            }
        }

    /**
     * Layers the Available/Borrowed dropdown filter on top of whatever the search
     * produced, so search and filter compose together rather than fighting each other.
     */
    val uiState: StateFlow<CatalogUiState> = combine(sourceBooks, _filter) { books, filter ->
        val filtered = when (filter) {
            CatalogFilter.ALL -> books
            CatalogFilter.AVAILABLE -> books.filter { it.isAvailable }
            CatalogFilter.BORROWED -> books.filter { !it.isAvailable }
        }
        CatalogUiState(books = filtered, filter = filter, isLoading = false)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = CatalogUiState(isLoading = true)
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onFilterSelected(filter: CatalogFilter) {
        _filter.value = filter
    }

    companion object {
        /**
         * No Hilt in this project, so ViewModels are built here and handed to
         * viewModel(factory = CatalogViewModel.Factory) at the call site.
         * APPLICATION_KEY pulls the KitabuApp instance out of CreationExtras.
         */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as KitabuApp
                CatalogViewModel(app.container.libraryRepository)
            }
        }
    }
}
