package com.stadio.kitabu

import android.app.Application
import com.stadio.kitabu.di.AppContainer
import com.stadio.kitabu.di.DefaultAppContainer

/**
 * Registered in AndroidManifest.xml via android:name=".KitabuApp".
 *
 * container is built once here, in onCreate, and lives for the whole process -
 * every ViewModel pulls the same LibraryRepository instance from it via
 * the viewModelFactory { initializer { ... } } pattern, e.g.:
 *
 *   companion object {
 *       val Factory: ViewModelProvider.Factory = viewModelFactory {
 *           initializer {
 *               val app = (this[APPLICATION_KEY] as KitabuApp)
 *               CatalogViewModel(app.container.libraryRepository)
 *           }
 *       }
 *   }
 */
class KitabuApp : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}