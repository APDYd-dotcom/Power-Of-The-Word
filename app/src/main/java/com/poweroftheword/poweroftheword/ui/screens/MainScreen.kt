package com.poweroftheword.poweroftheword.ui.screens

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.poweroftheword.poweroftheword.domain.model.FeedItem
import com.poweroftheword.poweroftheword.domain.model.LiveItem
import com.poweroftheword.poweroftheword.domain.model.VideoItem
import com.poweroftheword.poweroftheword.ui.navigation.Screen
import com.poweroftheword.poweroftheword.ui.screens.about.AboutScreen
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
import com.poweroftheword.poweroftheword.ui.screens.live.LivePlayerScreen
import com.poweroftheword.poweroftheword.ui.screens.live.LiveScreen
import com.poweroftheword.poweroftheword.ui.screens.live.LiveViewModel
import com.poweroftheword.poweroftheword.ui.screens.program.ProgramScreen
import com.poweroftheword.poweroftheword.ui.screens.program.ProgramViewModel
import com.poweroftheword.poweroftheword.ui.screens.radio.RadioScreen
import com.poweroftheword.poweroftheword.ui.screens.radio.RadioViewModel
import com.poweroftheword.poweroftheword.ui.screens.settings.ContactScreen
import com.poweroftheword.poweroftheword.ui.screens.settings.SettingsScreen
import com.poweroftheword.poweroftheword.ui.screens.settings.SettingsViewModel
import com.poweroftheword.poweroftheword.ui.screens.video.VideoDetailScreen
import com.poweroftheword.poweroftheword.ui.screens.video.VideoListScreen
import com.poweroftheword.poweroftheword.ui.screens.video.VideoListViewModel
import com.poweroftheword.poweroftheword.util.truncate

@Composable
fun MainScreen() {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val radioViewModel: RadioViewModel = hiltViewModel()
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()

    val bottomNavItems = listOf(
        Screen.Home,
        Screen.Videos,
        Screen.Audios,
        Screen.Radio,
        Screen.About,
        Screen.Feed
    )

    // Helper for tab navigation logic
    fun navigateToTab(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    // Hide bottom bar ONLY on detail and player screens
    val hideBottomBarRoutes = listOf(
        Screen.VideoDetail.route,
        Screen.FeedDetail.route,
        Screen.LivePlayer.route
    )
    val isBottomBarVisible = currentDestination?.route !in hideBottomBarRoutes

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        //  Main navigation
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(bottom = if (isBottomBarVisible) 70.dp else 0.dp) //  avoid overlap only when visible
        ) {

            composable(Screen.Home.route) {
                val viewModel: HomeViewModel = hiltViewModel()
                HomeScreen(
                    viewModel = viewModel,
                    isDarkMode = isDarkMode ?: androidx.compose.foundation.isSystemInDarkTheme(),
                    onThemeToggle = { settingsViewModel.setTheme(it) },
                    onVideoClick = { video: VideoItem ->
                        navController.navigate(Screen.VideoDetail.createRoute(video.id))
                    },
                    onFeedClick = { feed: FeedItem ->
                        navController.navigate(Screen.FeedDetail.createRoute(feed.id))
                    },
                    onLiveClick = { live: LiveItem ->
                        navController.navigate(Screen.LivePlayer.createRoute(live.id))
                    },
                    onSeeAllLive = {
                        navController.navigate(Screen.Live.route)
                    },
                    onRadioClick = { radio ->
                        radioViewModel.playById(radio.id)
                        navigateToTab(Screen.Radio.route)
                    },
                    onSeeAllVideos = {
                        navigateToTab(Screen.Videos.route)
                    },
                    onSeeAllFeeds = {
                        navigateToTab(Screen.Feed.route)
                    }
                )
            }

            composable(Screen.Videos.route) {
                val viewModel: VideoListViewModel = hiltViewModel()
                VideoListScreen(
                    viewModel = viewModel,
                    onVideoClick = { video ->
                        navController.navigate(Screen.VideoDetail.createRoute(video.id))
                    }
                )
            }

            composable(Screen.Audios.route) {
                val viewModel: AudioListViewModel = hiltViewModel()
                AudioListScreen(
                    viewModel = viewModel,
                    context = navController.context
                )
            }

            composable(Screen.Live.route) {
                val viewModel: LiveViewModel = hiltViewModel()
                LiveScreen(
                    viewModel = viewModel,
                    onLiveClick = { live ->
                        navController.navigate(Screen.LivePlayer.createRoute(live.id))
                    },
                    onBackClick = {navController.popBackStack()}
                )
            }

            composable(
                route = Screen.LivePlayer.route,
                arguments = listOf(navArgument("liveId") { type = NavType.IntType })
            ) { backStackEntry ->
                val liveId = backStackEntry.arguments?.getInt("liveId") ?: 0
                val viewModel: LiveViewModel = hiltViewModel()
                val videoViewModel: VideoListViewModel = hiltViewModel()
                LivePlayerScreen(
                    liveId = liveId,
                    viewModel = viewModel,
                    videoViewModel = videoViewModel,
                    onBackClick = { navController.popBackStack() },
                    onVideoClick = { video ->
                        navController.navigate(Screen.VideoDetail.createRoute(video.id))
                    }
                )
            }

            composable(Screen.Radio.route) {
                RadioScreen(
                    viewModel = radioViewModel
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
                arguments = listOf(navArgument("feedId") { type = NavType.StringType }),
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(500)
                    ) + fadeIn(animationSpec = tween(500))
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(500)
                    ) + fadeOut(animationSpec = tween(500))
                },
                popEnterTransition = {
                    fadeIn(animationSpec = tween(500))
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(500)
                    ) + fadeOut(animationSpec = tween(500))
                }
            ) { backStackEntry ->
                val feedId = backStackEntry.arguments?.getString("feedId")
                val viewModel: FeedViewModel = hiltViewModel()
                val feeds by viewModel.feeds.collectAsState()
                val feed = feeds.find { it.id == feedId?.toInt() }
                FeedDetailScreen(
                    feed = feed,
                    viewModel = viewModel,
                    onBackClick = { 
                        if (!navController.popBackStack()) {
                            navigateToTab(Screen.Home.route)
                        }
                    }
                )
            }

            composable(
                route = Screen.VideoDetail.route,
                arguments = listOf(navArgument("videoId") { type = NavType.StringType }),
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(500)
                    ) + fadeIn(animationSpec = tween(500))
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(500)
                    ) + fadeOut(animationSpec = tween(500))
                },
                popEnterTransition = {
                    fadeIn(animationSpec = tween(500))
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(500)
                    ) + fadeOut(animationSpec = tween(500))
                }
            ) { backStackEntry ->
                val viewModel: VideoListViewModel = hiltViewModel()
                val videoId = backStackEntry.arguments?.getString("videoId")
                val videos by viewModel.filteredVideos.collectAsState()
                val video = videos.find { it.id.toString() == videoId }
                VideoDetailScreen(
                    viewModel = viewModel,
                    onBackClick = { 
                        if (!navController.popBackStack()) {
                            navigateToTab(Screen.Home.route)
//                            popUpTo(Screen.Home.route) { inclusive = false }
                        }
                    },
                    onVideoClick = { v ->
                        navController.navigate(Screen.VideoDetail.createRoute(v.id))
                    },
                    videoId = video?.id.toString()
                )
            }

            composable(Screen.DailyWord.route) {
                val viewModel: DailyWordViewModel = hiltViewModel()
                DailyWordScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Horaire.route) {
                val viewModel: HoraireViewModel = hiltViewModel()
                HoraireScreen(
                    viewModel = viewModel,
                    onBackClick = {navController.popBackStack()}
                )
            }

            composable(Screen.Programs.route) {
                val viewModel: ProgramViewModel = hiltViewModel()
                ProgramScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Donation.route) {
                DonationScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.About.route) {
                AboutScreen( 
                    onBackClick = { navController.popBackStack() },
                    onDonationClick = { navController.navigate(Screen.Donation.route) },
                    onSettingsClick = { navController.navigate(Screen.Settings.route) }
                )
            }

            composable(Screen.Contact.route) {
                ContactScreen(onBackClick = { navController.popBackStack() })
            }

            composable(Screen.Settings.route) {
                val viewModel: SettingsViewModel = hiltViewModel()
                SettingsScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onNavigateToFeed = { navigateToTab(Screen.Feed.route) },
                    onNavigateToDailyWord = { navController.navigate(Screen.DailyWord.route) },
                    onNavigateToHoraire = { navController.navigate(Screen.Horaire.route) },
                    onNavigateToPrograms = { navController.navigate(Screen.Programs.route) },
                    onNavigateToDonation = { navController.navigate(Screen.Donation.route) },
                    onNavigateToAbout = { navigateToTab(Screen.About.route) },
                    onNavigateToContact = { navController.navigate(Screen.Contact.route) }
                )
            }
        }



        //  Bottom bar (FLOATING)
        if (isBottomBarVisible) {
            Box(
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                BottomAppBar(navController)
            }
        }
    }
}

@Composable
fun BottomAppBar(navController: NavController) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        Screen.Home,
        Screen.Videos,
        Screen.Audios,
        Screen.Radio,
        Screen.About,
        Screen.Feed
    )

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(20.dp)), //  rounded modern look

        containerColor = MaterialTheme.colorScheme.background.copy(alpha = 1f),
        tonalElevation = 8.dp
    ) {

        bottomNavItems.forEach { screen ->

            val selected = currentDestination?.hierarchy?.any {
                it.route == screen.route
            } == true

            NavigationBarItem(
                selected = selected,

                icon = {
                    screen.icon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = stringResource(screen.titleResId),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },

                label = {
                    Text(
                        text = stringResource(screen.titleResId).truncate(10),
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },

                alwaysShowLabel = false, //  modern style

                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },

                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                )
            )
        }
    }
}
