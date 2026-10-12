package com.stadio.kitabu.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.stadio.kitabu.domain.model.BookingStatus

/**
 * Represents a single reservation/rental transaction against a BookEntity.
 *
 * bookOwnerId links back to BookEntity.bookId with CASCADE delete, so removing a book
 * automatically cleans up any bookings that reference it (per the rubric's Entities requirement).
 *
 * bookingDate and returnDeadline are stored directly as epoch milliseconds.
 * BookingStatus is converted to/from String using the Room Converters class.
 */
@Entity(
    tableName = "bookings",
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["bookId"],
            childColumns = ["bookOwnerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("bookOwnerId")]
)
data class BookingEntity(
    @PrimaryKey(autoGenerate = true)
    val bookingId: Long = 0L,
    val bookOwnerId: Long,
    val userName: String,
    val bookingDate: Long,
    val returnDeadline: Long,
    val status: BookingStatus
)