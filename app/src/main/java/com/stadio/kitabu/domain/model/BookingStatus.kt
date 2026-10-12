package com.stadio.kitabu.domain.model

/**
 * Represents the lifecycle state of a single BookingEntity row.
 *
 * PENDING   -> Reservation placed, book not yet picked up. Can be Cancelled.
 * ACTIVE    -> Book has been picked up / is currently rented out. Can be Renewed or Returned.
 * RETURNED  -> Booking is complete. Terminal state, no actions available.
 */
enum class BookingStatus {
    PENDING,
    ACTIVE,
    RETURNED
}