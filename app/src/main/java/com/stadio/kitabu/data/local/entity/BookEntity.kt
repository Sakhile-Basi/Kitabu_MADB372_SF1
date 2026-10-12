package com.stadio.kitabu.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a single book in the library catalog.
 *
 * isAvailable is the source of truth for the Catalog's status badge (green/red)
 * and is flipped by BookingDao whenever a reservation is created or a book is returned.
 */
@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    val bookId: Long = 0L,
    val title: String,
    val author: String,
    val category: String,
    val isAvailable: Boolean = true
)