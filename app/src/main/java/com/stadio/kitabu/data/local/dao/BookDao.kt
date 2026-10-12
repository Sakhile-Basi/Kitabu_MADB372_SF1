package com.stadio.kitabu.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.stadio.kitabu.data.local.entity.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    // --- Create ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    // --- Read ---

    /** Full catalog, reactive - powers the Catalog screen's default "All books" filter. */
    @Query("SELECT * FROM books ORDER BY title ASC")
    fun getAllBooks(): Flow<List<BookEntity>>

    /** Backs the Catalog's "Available" / "Borrowed" dropdown filter. */
    @Query("SELECT * FROM books WHERE isAvailable = :isAvailable ORDER BY title ASC")
    fun getBooksByAvailability(isAvailable: Boolean): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE bookId = :bookId")
    fun getBookById(bookId: Long): Flow<BookEntity?>

    /** SQL LIKE search across title and author, used by both Catalog's search bar and the Search screen. */
    @Query(
        """
        SELECT * FROM books 
        WHERE title LIKE '%' || :query || '%' 
           OR author LIKE '%' || :query || '%'
        ORDER BY title ASC
        """
    )
    fun searchBooks(query: String): Flow<List<BookEntity>>

    // --- Update ---

    @Update
    suspend fun updateBook(book: BookEntity)

    /**
     * Updates a book's availability when a reservation,
     * cancellation, or return is processed.
     */
    @Query("UPDATE books SET isAvailable = :isAvailable WHERE bookId = :bookId")
    suspend fun setAvailability(bookId: Long, isAvailable: Boolean)

    // --- Delete ---

    @Delete
    suspend fun deleteBook(book: BookEntity)
}