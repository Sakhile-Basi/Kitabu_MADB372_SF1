package com.stadio.kitabu.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.stadio.kitabu.data.local.converter.Converters
import com.stadio.kitabu.data.local.dao.BookDao
import com.stadio.kitabu.data.local.dao.BookingDao
import com.stadio.kitabu.data.local.entity.BookEntity
import com.stadio.kitabu.data.local.entity.BookingEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [BookEntity::class, BookingEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class KitabuDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao
    abstract fun bookingDao(): BookingDao

    companion object {
        @Volatile
        private var INSTANCE: KitabuDatabase? = null

        /**
         * Thread-safe singleton. scope is used only once, by the prepopulation
         * callback below, to insert sample books off the main thread on first launch.
         */
        fun getDatabase(context: Context, scope: CoroutineScope): KitabuDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KitabuDatabase::class.java,
                    "kitabu_database"
                )
                    // Safe for coursework/dev: avoids crashes on schema bumps instead of writing migrations.
                    .fallbackToDestructiveMigration()
                    .addCallback(KitabuDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class KitabuDatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Only fires the very first time the DB file is created on-device.
            INSTANCE?.let { database ->
                scope.launch {
                    populateSampleBooks(database.bookDao())
                }
            }
        }

        private suspend fun populateSampleBooks(bookDao: BookDao) {
            bookDao.insertBooks(
                listOf(
                    BookEntity(
                        title = "Clean Code",
                        author = "Robert C. Martin",
                        category = "Software Engineering"
                    ),
                    BookEntity(
                        title = "The Pragmatic Programmer",
                        author = "Andrew Hunt",
                        category = "Software Engineering"
                    ),
                    BookEntity(
                        title = "Design Patterns",
                        author = "Erich Gamma",
                        category = "Software Engineering"
                    ),
                    BookEntity(
                        title = "Introduction to Algorithms",
                        author = "Thomas H. Cormen",
                        category = "Computer Science"
                    ),
                    BookEntity(
                        title = "Database System Concepts",
                        author = "Abraham Silberschatz",
                        category = "Computer Science"
                    ),
                    BookEntity(
                        title = "Computer Networks",
                        author = "Andrew S. Tanenbaum",
                        category = "Computer Science"
                    ),
                    BookEntity(
                        title = "The Midnight Library",
                        author = "Matt Haig",
                        category = "Fiction"
                    ),
                    BookEntity(
                        title = "Atomic Habits",
                        author = "James Clear",
                        category = "Self-Help"
                    ),
                    BookEntity(
                        title = "Sapiens",
                        author = "Yuval Noah Harari",
                        category = "History"
                    ),
                    BookEntity(
                        title = "Kotlin in Action",
                        author = "Dmitry Jemerov",
                        category = "Software Engineering"
                    )
                )
            )
        }
    }
}