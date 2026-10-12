package com.stadio.kitabu.ui.catalog.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.stadio.kitabu.data.local.entity.BookEntity

/**
 * One catalog grid cell. Tapping the whole card navigates to BookDetailScreen -
 * the "visible reserve trigger" gap from wireframe review is solved by the
 * detail screen's own Reserve CTA, not a button on this card.
 */
@Composable
fun BookCard(
    book: BookEntity,
    onClick: (bookId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onClick(book.bookId) },
        modifier = modifier.fillMaxWidth()
    ) {
        Box {
            // Placeholder cover - real per-book images are out of scope; every
            // book reuses the same generic icon, matching the BookDetail carousel decision.
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.15f),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.MenuBook,
                        contentDescription = null,
                        modifier = Modifier.padding(24.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AvailabilityBadge(
                isAvailable = book.isAvailable,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
            )
        }

        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = book.author,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = book.category,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Shared badge styling used on both the Catalog grid and BookDetail screen,
 * so availability reads the same color language everywhere in the app.
 */
@Composable
fun AvailabilityBadge(isAvailable: Boolean, modifier: Modifier = Modifier) {
    val backgroundColor = if (isAvailable) {
        Color(0xFF2E7D32) // green - Available
    } else {
        Color(0xFFC62828) // red - Borrowed
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = backgroundColor
    ) {
        Text(
            text = if (isAvailable) "Available" else "Borrowed",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
