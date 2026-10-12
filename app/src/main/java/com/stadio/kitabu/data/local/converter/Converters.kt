package com.stadio.kitabu.data.local.converter

import androidx.room.TypeConverter
import com.stadio.kitabu.domain.model.BookingStatus

class Converters {

    @TypeConverter
    fun fromBookingStatus(status: BookingStatus): String = status.name

    @TypeConverter
    fun toBookingStatus(value: String): BookingStatus = BookingStatus.valueOf(value)
}