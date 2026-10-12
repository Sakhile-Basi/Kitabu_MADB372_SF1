package com.stadio.kitabu.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.stadio.kitabu.KitabuApp
import com.stadio.kitabu.data.repository.LibraryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ProfileViewModel(
    repository: LibraryRepository
) : ViewModel() {

    val activeBookingCount: StateFlow<Int> =
        repository.getActiveBookingCount()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(
                    stopTimeoutMillis = 5000
                ),
                initialValue = 0
            )

    companion object {

        val Factory: ViewModelProvider.Factory =
            viewModelFactory {

                initializer {
                    val app =
                        this[APPLICATION_KEY] as KitabuApp

                    ProfileViewModel(
                        repository =
                            app.container.libraryRepository
                    )
                }
            }
    }
}

