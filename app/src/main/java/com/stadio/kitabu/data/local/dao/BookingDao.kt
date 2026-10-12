package com.stadio.kitabu.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.stadio.kitabu.data.local.entity.BookingEntity
import com.stadio.kitabu.data.local.relation.BookingWithBook
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {

    // --- Create ---

    @Insert
    suspend fun insertBooking(booking: BookingEntity): Long

    // --- Read ---

    @Transaction
    @Query(
        """
        SELECT * FROM bookings
        WHERE status != 'RETURNED'
        ORDER BY returnDeadline ASC
        """
    )
    fun getActiveAndPendingBookings(): Flow<List<BookingWithBook>>

    @Query(
        "SELECT COUNT(*) FROM bookings WHERE status = 'ACTIVE'"
    )
    fun getActiveBookingCount(): Flow<Int>

    // --- Update ---

    @Query(
        """
        UPDATE bookings
        SET status = 'ACTIVE'
        WHERE bookingId = :bookingId
          AND status = 'PENDING'
        """
    )
    suspend fun activateBooking(bookingId: Long)

    @Query(
        """
        UPDATE bookings
        SET returnDeadline = :newDeadline
        WHERE bookingId = :bookingId
          AND status = 'ACTIVE'
        """
    )
    suspend fun renewBooking(
        bookingId: Long,
        newDeadline: Long
    )

    @Query(
        """
        UPDATE bookings
        SET status = 'RETURNED'
        WHERE bookingId = :bookingId
          AND status = 'ACTIVE'
        """
    )
    suspend fun markAsReturned(bookingId: Long)

    // --- Delete ---
    @Query(
        """
    DELETE FROM bookings
    WHERE bookingId = :bookingId
      AND status = 'PENDING'
    """
    )
    suspend fun cancelPendingBooking(
        bookingId: Long
    ): Int

}