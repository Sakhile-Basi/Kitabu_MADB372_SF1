package com.stadio.kitabu.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.stadio.kitabu.ui.bookdetail.BookDetailScreen
import com.stadio.kitabu.ui.catalog.CatalogScreen
import com.stadio.kitabu.ui.dashboard.DashboardScreen
import com.stadio.kitabu.ui.profile.ProfileScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue

private data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem(
        Screen.Catalog,
        "Home",
        Icons.Filled.Home
    ),
    BottomNavItem(
        Screen.Dashboard,
        "Library",
        Icons.Filled.LibraryBooks
    ),
    BottomNavItem(
        Screen.Profile,
        "Profile",
        Icons.Filled.Person
    )
)

/**
 * Root composable: owns the NavController, the bottom nav bar, and the NavHost.
 * The bottom bar only renders on the four top-level destinations - it's hidden
 * on BookDetail so that screen can feel like the "full-screen takeover" from
 * the wireframe rather than just another tab.
 */
@Composable
fun KitabuNavHost() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = bottomNavItems.any { it.screen.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.screen.route,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    // Avoid piling up duplicate copies of the same tab on the back stack.
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Catalog.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Catalog.route) {
                CatalogScreen(
                    onBookClick = { bookId ->
                        navController.navigate(Screen.BookDetail.createRoute(bookId))
                    }
                )
            }

            // Reuses the exact same screen + ViewModel as Home - same grid logic.
            composable(Screen.Dashboard.route) {
                DashboardScreen()
            }

            composable(Screen.Profile.route) {
                ProfileScreen()
            }

            composable(
                route = Screen.BookDetail.route,
                arguments = listOf(navArgument(Screen.BookDetail.ARG_BOOK_ID) { type = NavType.LongType })
            ) { entry ->
                val bookId = entry.arguments?.getLong(Screen.BookDetail.ARG_BOOK_ID) ?: 0L
                BookDetailScreen(
                    bookId = bookId,
                    onBack = { navController.navigateUp() }
                )
            }
        }
    }
}
