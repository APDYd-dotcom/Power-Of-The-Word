package com.poweroftheword.poweroftheword.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.poweroftheword.poweroftheword.ui.navigation.Screen
import com.poweroftheword.poweroftheword.ui.screens.audio.AudioListScreen
import com.poweroftheword.poweroftheword.ui.screens.audio.AudioListViewModel
import com.poweroftheword.poweroftheword.ui.screens.dailyword.DailyWordScreen
import com.poweroftheword.poweroftheword.ui.screens.dailyword.DailyWordViewModel
import com.poweroftheword.poweroftheword.ui.screens.donation.DonationScreen
import com.poweroftheword.poweroftheword.ui.screens.feed.FeedDetailScreen
import com.poweroftheword.poweroftheword.ui.screens.feed.FeedScreen
import com.poweroftheword.poweroftheword.ui.screens.feed.FeedViewModel
import com.poweroftheword.poweroftheword.ui.screens.home.HomeScreen
import com.poweroftheword.poweroftheword.ui.screens.home.HomeViewModel
import com.poweroftheword.poweroftheword.ui.screens.horaire.HoraireScreen
import com.poweroftheword.poweroftheword.ui.screens.horaire.HoraireViewModel
import com.poweroftheword.poweroftheword.ui.screens.live.LiveScreen
import com.poweroftheword.poweroftheword.ui.screens.live.LiveViewModel
import com.poweroftheword.poweroftheword.ui.screens.radio.RadioScreen
import com.poweroftheword.poweroftheword.ui.screens.radio.RadioViewModel
import com.poweroftheword.poweroftheword.ui.screens.settings.SettingsScreen
import com.poweroftheword.poweroftheword.ui.screens.settings.SettingsViewModel
import com.poweroftheword.poweroftheword.ui.screens.video.VideoListScreen
import com.poweroftheword.poweroftheword.ui.screens.video.VideoListViewModel
import com.poweroftheword.poweroftheword.ui.screens.video.VideoPlayerScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val bottomNavItems = listOf(
        Screen.Home,
        Screen.Videos,
        Screen.Audios,
        Screen.Live,
        Screen.Settings
    )

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            
            val isBottomBarVisible = bottomNavItems.any { it.route == currentDestination?.route }

            if (isBottomBarVisible) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { screen.icon?.let { Icon(it, contentDescription = null) } },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                val viewModel: HomeViewModel = hiltViewModel()
                HomeScreen(
                    viewModel = viewModel,
                    onVideoClick = { video ->
                        navController.navigate(Screen.VideoPlayer.createRoute(video.videoUrl))
                    },
                    onLiveClick = { url ->
                        navController.navigate(Screen.VideoPlayer.createRoute(url))
                    },
                    onRadioClick = {
                        navController.navigate(Screen.Radio.route)
                    },
                    onSeeAllVideos = {
                        navController.navigate(Screen.Videos.route)
                    }
                )
            }
            composable(Screen.Videos.route) {
                val viewModel: VideoListViewModel = hiltViewModel()
                VideoListScreen(
                    viewModel = viewModel,
                    onVideoClick = { video ->
                        navController.navigate(Screen.VideoPlayer.createRoute(video.videoUrl))
                    }
                )
            }
            composable(Screen.Audios.route) {
                val viewModel: AudioListViewModel = hiltViewModel()
                AudioListScreen(
                    viewModel = viewModel,
                    onAudioClick = { audio ->
                        navController.navigate(Screen.VideoPlayer.createRoute(audio.audioUrl))
                    }
                )
            }
            composable(Screen.Live.route) {
                val viewModel: LiveViewModel = hiltViewModel()
                LiveScreen(
                    viewModel = viewModel,
                    onLiveClick = { live ->
                        navController.navigate(Screen.VideoPlayer.createRoute(live.streamUrl))
                    }
                )
            }
            composable(Screen.Radio.route) {
                val viewModel: RadioViewModel = hiltViewModel()
                RadioScreen(
                    viewModel = viewModel,
                    onPlayClick = { radio ->
                        navController.navigate(Screen.VideoPlayer.createRoute(radio.streamUrl))
                    }
                )
            }
            composable(Screen.Feed.route) {
                val viewModel: FeedViewModel = hiltViewModel()
                FeedScreen(
                    viewModel = viewModel,
                    onFeedClick = { feed ->
                        navController.navigate(Screen.FeedDetail.createRoute(feed.id))
                    }
                )
            }
            composable(
                route = Screen.FeedDetail.route,
                arguments = listOf(navArgument("feedId") { type = NavType.StringType })
            ) { backStackEntry ->
                val feedId = backStackEntry.arguments?.getString("feedId")
                val viewModel: FeedViewModel = hiltViewModel()
                val feed = viewModel.feeds.collectAsState().value.find { feed -> feed.id == feedId }
                FeedDetailScreen(feed = feed, onBackClick = { navController.popBackStack() })
            }
            composable(Screen.DailyWord.route) {
                val viewModel: DailyWordViewModel = hiltViewModel()
                DailyWordScreen(viewModel = viewModel)
            }
            composable(Screen.Horaire.route) {
                val viewModel: HoraireViewModel = hiltViewModel()
                HoraireScreen(viewModel = viewModel)
            }
            composable(Screen.Donation.route) {
                DonationScreen()
            }
            composable(Screen.Settings.route) {
                val viewModel: SettingsViewModel = hiltViewModel()
                SettingsScreen(
                    viewModel = viewModel,
                    onNavigateToFeed = { navController.navigate(Screen.Feed.route) },
                    onNavigateToDailyWord = { navController.navigate(Screen.DailyWord.route) },
                    onNavigateToHoraire = { navController.navigate(Screen.Horaire.route) },
                    onNavigateToDonation = { navController.navigate(Screen.Donation.route) }
                )
            }
            composable(
                route = Screen.VideoPlayer.route,
                arguments = listOf(navArgument("videoUrl") { type = NavType.StringType })
            ) { backStackEntry ->
                val videoUrl = backStackEntry.arguments?.getString("videoUrl") ?: ""
                VideoPlayerScreen(videoUrl = videoUrl)
            }
        }
    }
}
