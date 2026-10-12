package com.stadio.kitabu.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.stadio.kitabu.data.local.entity.BookEntity
import com.stadio.kitabu.data.local.entity.BookingEntity

/**
 * Powers the Dashboard screen: each row needs both the booking's own fields
 * (reservation date, return deadline, status) AND the associated book's
 * title/author/category for display on the card.
 *
 * Room resolves this as a single query with a JOIN under the hood when used
 * as the return type of a @Transaction @Query in BookingDao.
 */
data class BookingWithBook(
    @Embedded
    val booking: BookingEntity,

    @Relation(
        parentColumn = "bookOwnerId",
        entityColumn = "bookId"
    )
    val book: BookEntity
)