package com.stadio.kitabu.ui.bookdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.stadio.kitabu.KitabuApp
import com.stadio.kitabu.data.local.entity.BookEntity
import com.stadio.kitabu.data.repository.LibraryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

private const val DEFAULT_USER_NAME = "Current User"

/** Valid rental durations in days. */
private val VALID_RENTAL_DURATIONS = setOf(
    7,
    14,
    21
)

class BookDetailViewModel(
    private val repository: LibraryRepository,
    private val bookId: Long
) : ViewModel() {

    val book: StateFlow<BookEntity?> =
        repository.getBookById(bookId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )

    private val _reservationComplete =
        MutableStateFlow(false)

    val reservationComplete: StateFlow<Boolean> =
        _reservationComplete.asStateFlow()

    private val _errorMessage =
        MutableStateFlow<String?>(null)

    val errorMessage: StateFlow<String?> =
        _errorMessage.asStateFlow()

    fun reserve(durationDays: Int) {

        val currentBook = book.value

        if (currentBook == null) {
            _errorMessage.value = "Book information is unavailable."
            return
        }

        if (!currentBook.isAvailable) {
            _errorMessage.value =
                "This book is currently unavailable."
            return
        }

        if (durationDays !in VALID_RENTAL_DURATIONS) {
            _errorMessage.value =
                "Please select a valid rental duration."
            return
        }

        val bookingDate =
            System.currentTimeMillis()

        val returnDeadline =
            bookingDate +
                    TimeUnit.DAYS.toMillis(durationDays.toLong())

        if (returnDeadline <= bookingDate) {
            _errorMessage.value =
                "Return date must be after the reservation date."
            return
        }

        viewModelScope.launch {
            repository.reserveBook(
                bookId = bookId,
                userName = DEFAULT_USER_NAME,
                bookingDate = bookingDate,
                returnDeadline = returnDeadline
            )

            _reservationComplete.value = true
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    companion object {

        fun factory(
            bookId: Long
        ): ViewModelProvider.Factory =
            viewModelFactory {

                initializer {
                    val app =
                        this[APPLICATION_KEY] as KitabuApp

                    BookDetailViewModel(
                        repository =
                            app.container.libraryRepository,
                        bookId = bookId
                    )
                }
            }
    }
}