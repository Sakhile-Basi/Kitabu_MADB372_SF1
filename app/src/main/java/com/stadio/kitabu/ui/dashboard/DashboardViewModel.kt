package com.stadio.kitabu.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.stadio.kitabu.KitabuApp
import com.stadio.kitabu.data.local.entity.BookingEntity
import com.stadio.kitabu.data.local.relation.BookingWithBook
import com.stadio.kitabu.data.repository.LibraryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

data class DashboardUiState(
    val bookings: List<BookingWithBook> = emptyList(),
    val isLoading: Boolean = true
)

class DashboardViewModel(
    private val repository: LibraryRepository
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> =
        repository.getActiveAndPendingBookings()
            .map { bookings ->
                DashboardUiState(
                    bookings = bookings,
                    isLoading = false
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(
                    stopTimeoutMillis = 5000
                ),
                initialValue = DashboardUiState(
                    isLoading = true
                )
            )

    fun onPickup(bookingId: Long) {
        viewModelScope.launch {
            repository.activateBooking(bookingId)
        }
    }

    fun onRenew(
        bookingId: Long,
        currentDeadline: Long
    ) {
        viewModelScope.launch {
            val newDeadline =
                currentDeadline + TimeUnit.DAYS.toMillis(14)

            repository.renewBooking(
                bookingId = bookingId,
                newDeadline = newDeadline
            )
        }
    }

    fun onReturn(
        bookingId: Long,
        bookId: Long
    ) {
        viewModelScope.launch {
            repository.returnBooking(
                bookingId = bookingId,
                bookId = bookId
            )
        }
    }

    fun onCancel(
        booking: BookingEntity
    ) {
        viewModelScope.launch {
            repository.cancelBooking(booking)
        }
    }

    companion object {

        val Factory: ViewModelProvider.Factory =
            viewModelFactory {

                initializer {
                    val app =
                        this[APPLICATION_KEY] as KitabuApp

                    DashboardViewModel(
                        repository =
                            app.container.libraryRepository
                    )
                }
            }
    }
}

