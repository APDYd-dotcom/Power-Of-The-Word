package com.poweroftheword.poweroftheword.ui.screens.home
//
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Home
//import androidx.compose.material.icons.filled.Info
//import androidx.compose.material.icons.filled.MusicNote
//import androidx.compose.material.icons.filled.PlayArrow
//import androidx.compose.material.icons.filled.Radio
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.NavigationBar
//import androidx.compose.material3.NavigationBarItem
//import androidx.compose.material3.NavigationBarItemDefaults
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.navigation.NavDestination.Companion.hierarchy
//import androidx.navigation.NavGraph.Companion.findStartDestination
//import androidx.navigation.compose.currentBackStackEntryAsState
//import androidx.navigation.compose.rememberNavController
//import com.poweroftheword.poweroftheword.ui.navigation.Screen
//
//@Composable
//fun BottomNavBar() {
//    val navController = rememberNavController()
//    val bottomNavItems = listOf(
//        Screen.Home,
//        Screen.Videos,
//        Screen.Audios,
//        Screen.Radio,
//        Screen.About,
//        Screen.Feed
//    )
//    bottomBar = {
//        val navBackStackEntry by navController.currentBackStackEntryAsState()
//        val currentDestination = navBackStackEntry?.destination
//
//        val isBottomBarVisible = bottomNavItems.any { it.route == currentDestination?.route }
//
//        if (isBottomBarVisible) {
//            NavigationBar(
//                containerColor = MaterialTheme.colorScheme.background,
//                contentColor = MaterialTheme.colorScheme.primary
//            ) {
//                bottomNavItems.forEach { screen ->
//                    NavigationBarItem(
//                        icon = { screen.icon?.let { Icon(it, contentDescription = null) } },
//                        label = { Text(screen.title) },
//                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
//                        onClick = {
//                            navController.navigate(screen.route) {
//                                popUpTo(navController.graph.findStartDestination().id) {
//                                    saveState = true
//                                }
//                                launchSingleTop = true
//                                restoreState = true
//                            }
//                        },
//                        colors = NavigationBarItemDefaults.colors(
//                            selectedIconColor = com.poweroftheword.poweroftheword.ui.theme.FigmaBrightBlue,
//                            selectedTextColor = com.poweroftheword.poweroftheword.ui.theme.FigmaBrightBlue,
//                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
//                            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
//                            indicatorColor = com.poweroftheword.poweroftheword.ui.theme.FigmaBrightBlue.copy(alpha = 0.1f)
//                        )
//                    )
//                }
//            }
//        }
//    }
//}