package com.stadio.kitabu.ui.navigation

/**
 * Single source of truth for every route string in the app. BookDetail carries
 * an argument, so it exposes createRoute() rather than a bare route constant -
 * every navigate() call builds the route through this function, never by
 * hand-concatenating strings, so a typo can't silently break deep-linking.
 */
sealed class Screen(val route: String) {
    data object Catalog : Screen("catalog")
    data object Dashboard : Screen("dashboard")
    data object Search : Screen("search")
    data object Profile : Screen("profile")

    data object BookDetail : Screen("bookDetail/{bookId}") {
        const val ARG_BOOK_ID = "bookId"
        fun createRoute(bookId: Long) = "bookDetail/$bookId"
    }
}
