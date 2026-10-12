package com.stadio.kitabu.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.stadio.kitabu.ui.dashboard.components.BookingCard

/**
 * "My Library" / Reservations Dashboard. Content-only, same pattern as
 * CatalogScreen - the bottom nav bar lives in the outer NavGraph Scaffold.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("My Reservations") }) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
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
                uiState.bookings.isEmpty() -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("No active reservations yet - browse the catalog to reserve a book.")
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.bookings, key = { it.booking.bookingId }) { bookingWithBook ->
                            BookingCard(
                                bookingWithBook = bookingWithBook,

                                onRenew = {
                                    viewModel.onRenew(
                                        bookingId = bookingWithBook.booking.bookingId,
                                        currentDeadline = bookingWithBook.booking.returnDeadline
                                    )
                                },

                                onReturn = {
                                    viewModel.onReturn(
                                        bookingId = bookingWithBook.booking.bookingId,
                                        bookId = bookingWithBook.book.bookId
                                    )
                                },

                                onPickup = {
                                    viewModel.onPickup(
                                        bookingId = bookingWithBook.booking.bookingId
                                    )
                                },

                                onCancel = {
                                    viewModel.onCancel(
                                        bookingWithBook.booking
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
