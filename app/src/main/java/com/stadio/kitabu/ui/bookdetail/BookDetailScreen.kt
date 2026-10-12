package com.stadio.kitabu.ui.bookdetail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stadio.kitabu.ui.catalog.components.AvailabilityBadge
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

private val placeholderPages: List<ImageVector> = listOf(
    Icons.Filled.MenuBook,
    Icons.Filled.AutoStories,
    Icons.Filled.Category
)

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
fun BookDetailScreen(
    bookId: Long,
    onBack: () -> Unit,
    viewModel: BookDetailViewModel =
        viewModel(factory = BookDetailViewModel.factory(bookId))
) {
    val book by viewModel.book.collectAsStateWithLifecycle()

    val reservationComplete by
    viewModel.reservationComplete.collectAsStateWithLifecycle()

    val errorMessage by
    viewModel.errorMessage.collectAsStateWithLifecycle()

    var showReservationSheet by remember {
        mutableStateOf(false)
    }

    var selectedDuration by remember {
        mutableIntStateOf(14)
    }

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(reservationComplete) {
        if (reservationComplete) {
            onBack()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        },
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close"
                        )
                    }
                }
            )
        },
        bottomBar = {
            val currentBook = book

            if (currentBook != null) {
                Button(
                    onClick = {
                        if (currentBook.isAvailable) {
                            showReservationSheet = true
                        }
                    },
                    enabled = currentBook.isAvailable,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = if (currentBook.isAvailable) {
                            "Reserve"
                        } else {
                            "Currently Unavailable"
                        }
                    )
                }
            }
        }
    ) { innerPadding ->

        val currentBook = book

        if (currentBook == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
        ) {

            val pagerState =
                rememberPagerState(
                    pageCount = {
                        placeholderPages.size
                    }
                )

            Box {

                HorizontalPager(
                    state = pagerState
                ) { page ->

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.2f),
                        color =
                            MaterialTheme.colorScheme.surfaceVariant
                    ) {

                        Box(
                            contentAlignment =
                                Alignment.Center
                        ) {
                            Icon(
                                imageVector =
                                    placeholderPages[page],
                                contentDescription = null,
                                modifier =
                                    Modifier.size(96.dp),
                                tint =
                                    MaterialTheme.colorScheme
                                        .onSurfaceVariant
                            )
                        }
                    }
                }

                AvailabilityBadge(
                    isAvailable =
                        currentBook.isAvailable,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement =
                    Arrangement.Center
            ) {

                repeat(
                    placeholderPages.size
                ) { index ->

                    val isSelected =
                        pagerState.currentPage == index

                    Box(
                        modifier = Modifier
                            .padding(
                                horizontal = 4.dp
                            )
                            .size(
                                if (isSelected) {
                                    8.dp
                                } else {
                                    6.dp
                                }
                            )
                            .background(
                                color =
                                    if (isSelected) {
                                        MaterialTheme
                                            .colorScheme
                                            .primary
                                    } else {
                                        MaterialTheme
                                            .colorScheme
                                            .outlineVariant
                                    },
                                shape = CircleShape
                            )
                    )
                }
            }

            Column(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 8.dp
                )
            ) {

                Text(
                    text = currentBook.title,
                    style =
                        MaterialTheme.typography.titleLarge
                )

                Text(
                    text = currentBook.author,
                    style =
                        MaterialTheme.typography.bodyLarge,
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant
                )

                Text(
                    text =
                        "Category: ${currentBook.category}",
                    style =
                        MaterialTheme.typography.bodyLarge,
                    modifier =
                        Modifier.padding(top = 8.dp)
                )
            }
        }
    }

    if (showReservationSheet) {

        val currentBook = book

        if (currentBook != null) {

            val estimatedDeadline =
                System.currentTimeMillis() +
                        TimeUnit.DAYS.toMillis(
                            selectedDuration.toLong()
                        )

            val dateFormatter =
                SimpleDateFormat(
                    "dd MMM yyyy",
                    Locale.getDefault()
                )

            ModalBottomSheet(
                onDismissRequest = {
                    showReservationSheet = false
                }
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 24.dp,
                            end = 24.dp,
                            bottom = 32.dp
                        )
                ) {

                    Text(
                        text =
                            "Reserve ${currentBook.title}",
                        style =
                            MaterialTheme.typography.titleLarge
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Text(
                        text = "Choose rental duration",
                        style =
                            MaterialTheme.typography.bodyLarge
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    listOf(
                        7,
                        14,
                        21
                    ).forEach { days ->

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            RadioButton(
                                selected =
                                    selectedDuration == days,
                                onClick = {
                                    selectedDuration = days
                                }
                            )

                            Text(
                                text = "$days days"
                            )
                        }
                    }

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    Text(
                        text =
                            "Return by: ${
                                dateFormatter.format(
                                    Date(
                                        estimatedDeadline
                                    )
                                )
                            }",
                        style =
                            MaterialTheme.typography.bodyLarge
                    )

                    Spacer(
                        modifier =
                            Modifier.height(20.dp)
                    )

                    Button(
                        onClick = {
                            viewModel.reserve(
                                durationDays =
                                    selectedDuration
                            )
                        },
                        modifier =
                            Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Confirm Reservation"
                        )
                    }
                }
            }
        }
    }
}