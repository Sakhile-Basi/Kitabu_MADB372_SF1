package com.stadio.kitabu.data.repository

import androidx.room.withTransaction
import com.stadio.kitabu.data.local.KitabuDatabase
import com.stadio.kitabu.data.local.dao.BookDao
import com.stadio.kitabu.data.local.dao.BookingDao
import com.stadio.kitabu.data.local.entity.BookEntity
import com.stadio.kitabu.data.local.entity.BookingEntity
import com.stadio.kitabu.data.local.relation.BookingWithBook
import com.stadio.kitabu.domain.model.BookingStatus
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for ViewModels. No ViewModel should touch a DAO directly -
 * this keeps the MVVM layering clean per Section C, and is the one place where
 * cross-entity consistency (Book.isAvailable <-> Booking.status) is enforced.
 *
 * Operations that touch BOTH tables (reserve, return) go through database.withTransaction
 * so a failure partway through cannot leave the two tables inconsistent - e.g. a booking
 * existing while the book still shows "available".
 */
class LibraryRepository(
    private val database: KitabuDatabase,
    private val bookDao: BookDao,
    private val bookingDao: BookingDao
) {

    // --- Catalog / Search reads ---

    fun getAllBooks(): Flow<List<BookEntity>> = bookDao.getAllBooks()

    fun getBooksByAvailability(isAvailable: Boolean): Flow<List<BookEntity>> =
        bookDao.getBooksByAvailability(isAvailable)

    fun searchBooks(query: String): Flow<List<BookEntity>> = bookDao.searchBooks(query)

    fun getBookById(bookId: Long): Flow<BookEntity?> = bookDao.getBookById(bookId)

    // --- Dashboard / Profile reads ---

    fun getActiveAndPendingBookings(): Flow<List<BookingWithBook>> =
        bookingDao.getActiveAndPendingBookings()

    fun getActiveBookingCount(): Flow<Int> = bookingDao.getActiveBookingCount()

    // --- Reservation lifecycle (the atomic operations) ---

    /**
     * Called from BookDetailScreen's Reserve/Join Waitlist CTA.
     * Inserts the booking as PENDING and flips the book to unavailable, atomically.
     */
    suspend fun reserveBook(bookId: Long, userName: String, bookingDate: Long, returnDeadline: Long) {
        database.withTransaction {
            bookingDao.insertBooking(
                BookingEntity(
                    bookOwnerId = bookId,
                    userName = userName,
                    bookingDate = bookingDate,
                    returnDeadline = returnDeadline,
                    status = BookingStatus.PENDING
                )
            )
            bookDao.setAvailability(bookId, isAvailable = false)
        }
    }

    /** Renew: just extends the deadline, no availability change needed. */
    suspend fun renewBooking(bookingId: Long, newDeadline: Long) {
        bookingDao.renewBooking(bookingId, newDeadline)
    }

    /**
     * Return: marks the booking RETURNED and frees up the book, atomically.
     */
    suspend fun returnBooking(bookingId: Long, bookId: Long) {
        database.withTransaction {
            bookingDao.markAsReturned(bookingId)
            bookDao.setAvailability(bookId, isAvailable = true)
        }
    }

    /**
     * Cancel: only valid for PENDING bookings (pre-pickup). Deletes the row and
     * frees up the book, atomically.
     */
    suspend fun cancelBooking(
        booking: BookingEntity
    ) {
        database.withTransaction {

            val deletedRows =
                bookingDao.cancelPendingBooking(
                    booking.bookingId
                )

            if (deletedRows > 0) {
                bookDao.setAvailability(
                    bookId = booking.bookOwnerId,
                    isAvailable = true
                )
            }
        }
    }

    suspend fun activateBooking(bookingId: Long) {
        bookingDao.activateBooking(bookingId)
    }
}
