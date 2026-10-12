package com.stadio.kitabu.di

import android.content.Context
import com.stadio.kitabu.data.local.KitabuDatabase
import com.stadio.kitabu.data.repository.LibraryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/**
 * Manual DI contract. No Hilt/Koin in scope for this project, so this interface
 * exists purely so KitabuApp (and, if ever needed, a fake container in tests)
 * can swap implementations without ViewModels caring how the repository was built.
 */
interface AppContainer {
    val libraryRepository: LibraryRepository
}

/**
 * Builds the real dependency graph once per process:
 * applicationScope -> KitabuDatabase (singleton) -> DAOs -> LibraryRepository.
 *
 * applicationScope (not viewModelScope) is passed to KitabuDatabase.getDatabase()
 * because the one-time prepopulation callback must outlive any single screen's
 * ViewModel - it only ever runs once, on first app install, independent of navigation.
 */
class DefaultAppContainer(private val context: Context) : AppContainer {

    private val applicationScope = CoroutineScope(SupervisorJob())

    private val database: KitabuDatabase by lazy {
        KitabuDatabase.getDatabase(context, applicationScope)
    }

    override val libraryRepository: LibraryRepository by lazy {
        LibraryRepository(
            database = database,
            bookDao = database.bookDao(),
            bookingDao = database.bookingDao()
        )
    }
}
