package com.stadio.kitabu.ui.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.stadio.kitabu.data.local.relation.BookingWithBook
import com.stadio.kitabu.domain.model.BookingStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

private val dateFormatter =
    SimpleDateFormat(
        "dd MMM yyyy",
        Locale.getDefault()
    )

@Composable
fun BookingCard(
    bookingWithBook: BookingWithBook,
    onRenew: () -> Unit,
    onReturn: () -> Unit,
    onPickup: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val booking = bookingWithBook.booking
    val book = bookingWithBook.book

    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp)
        ) {

            Surface(
                modifier = Modifier.size(64.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.MenuBook,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .fillMaxWidth()
            ) {

                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleSmall
                )

                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Reserved: ${
                        dateFormatter.format(
                            Date(booking.bookingDate)
                        )
                    }",
                    style = MaterialTheme.typography.bodySmall
                )

                when (booking.status) {

                    BookingStatus.ACTIVE -> {
                        ActiveBookingContent(
                            bookingDate = booking.bookingDate,
                            returnDeadline = booking.returnDeadline,
                            onRenew = onRenew,
                            onReturn = onReturn
                        )
                    }

                    BookingStatus.PENDING -> {
                        PendingBookingContent(
                            onPickup = onPickup,
                            onCancel = onCancel
                        )
                    }

                    BookingStatus.RETURNED -> Unit
                }
            }
        }
    }
}

@Composable
private fun ActiveBookingContent(
    bookingDate: Long,
    returnDeadline: Long,
    onRenew: () -> Unit,
    onReturn: () -> Unit
) {
    val now = System.currentTimeMillis()

    val millisRemaining =
        (returnDeadline - now).coerceAtLeast(0)

    val oneDayMillis =
        TimeUnit.DAYS.toMillis(1)

    val daysLeft =
        if (millisRemaining == 0L) {
            0L
        } else {
            (millisRemaining + oneDayMillis - 1) / oneDayMillis
        }

    val totalDuration =
        (returnDeadline - bookingDate)
            .coerceAtLeast(1)

    val elapsed =
        (now - bookingDate)
            .coerceIn(
                minimumValue = 0,
                maximumValue = totalDuration
            )

    val progress =
        elapsed.toFloat() /
                totalDuration.toFloat()

    Text(
        text = "Due: ${
            dateFormatter.format(
                Date(returnDeadline)
            )
        }",
        style = MaterialTheme.typography.bodySmall
    )

    Text(
        text = "$daysLeft day${
            if (daysLeft == 1L) "" else "s"
        } left",
        style = MaterialTheme.typography.bodySmall
    )

    LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )


    Row(
        modifier = Modifier.padding(top = 8.dp),
        horizontalArrangement =
            Arrangement.spacedBy(8.dp)
    ) {

        OutlinedButton(
            onClick = onRenew
        ) {
            Text("Renew")
        }

        TextButton(
            onClick = onReturn
        ) {
            Text("Return")
        }
    }
}

@Composable
private fun PendingBookingContent(
    onPickup: () -> Unit,
    onCancel: () -> Unit
) {
    Text(
        text = "Pending pickup",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.secondary
    )

    Row(
        modifier = Modifier.padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = onPickup
        ) {
            Text("Pick Up")
        }

        TextButton(
            onClick = onCancel
        ) {
            Text("Cancel")
        }
    }
}