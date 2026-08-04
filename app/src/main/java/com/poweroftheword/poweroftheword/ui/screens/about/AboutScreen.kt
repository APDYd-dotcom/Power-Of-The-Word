package com.poweroftheword.poweroftheword.ui.screens.about

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import coil.compose.AsyncImage
import com.poweroftheword.poweroftheword.R
import com.poweroftheword.poweroftheword.ui.components.AboutScreenSkeleton
import com.poweroftheword.poweroftheword.ui.screens.program.ProgramViewModel
import com.poweroftheword.poweroftheword.ui.screens.settings.SettingsViewModel
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import com.poweroftheword.poweroftheword.BuildConfig.BASE_URL
import com.poweroftheword.poweroftheword.ui.theme.LocalStatusBarAppearance
import com.poweroftheword.poweroftheword.util.localizedString


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBackClick: () -> Unit,
    onDonationClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: PastorViewModel = koinViewModel(),
    programViewModel: ProgramViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel(),
    socialMediaViewModel: SocialMediaViewModel = koinViewModel()
) {

    val context = androidx.compose.ui.platform.LocalContext.current
    val scrollState = rememberScrollState()
    val pastors by viewModel.pastor.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    val programs by programViewModel.programs.collectAsState()
    val isProgramsLoading by programViewModel.isLoading.collectAsState()

    val socialMediaList by socialMediaViewModel.socialMedia.collectAsState()
    val isSocialMediaLoading by socialMediaViewModel.isLoading.collectAsState()

    val userDarkMode by settingsViewModel.isDarkMode.collectAsState()
    val isDark = userDarkMode ?: isSystemInDarkTheme()
    val statusBarAppearance = LocalStatusBarAppearance.current

    LaunchedEffect(isDark) {
        statusBarAppearance.isDarkIcons = !isDark
    }

    DisposableEffect(Unit) {
        onDispose {
            statusBarAppearance.isDarkIcons = null
        }
    }

    val pastor = pastors.firstOrNull()
    val isRefreshing = isLoading || isProgramsLoading || isSocialMediaLoading

    Scaffold(
        containerColor = if (isDark) Color(0xFF121826) else MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        localizedString(R.string.about),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isDark) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(CircleShape)
                            .background(if (isDark) Color.White.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = null,
                            tint = if (isDark) Color.White else MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                viewModel.loadPastor()
                programViewModel.loadPrograms()
                socialMediaViewModel.loadSocialMedia()
            },
            modifier = Modifier.padding(padding)
        ) {
            if (isRefreshing && pastors.isEmpty() && programs.isEmpty() && socialMediaList.isEmpty()) {
                AboutScreenSkeleton()
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {

                    // 👤 HEADER / PROFILE
                    Card(
                        modifier = Modifier.padding(16.dp),
                        shape = RoundedCornerShape(32.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E2635) else MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 2.dp),
                        border = if (isDark) BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)) else CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (pastor != null) {
                                AsyncImage(
                                    model = pastor.photo,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(20.dp)),
                                    contentScale = ContentScale.Crop,
                                    placeholder = painterResource(id = R.drawable.dailword1),
                                    error = painterResource(id = R.drawable.dailword1)
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.dailword1),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(20.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.width(20.dp))

                            Column {
                                Text(
                                    text = pastor?.fullName ?: localizedString(R.string.pastor_name),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else MaterialTheme.colorScheme.onSurface
                                )

                                Text(
                                    text = localizedString(R.string.pastor_title),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isDark) Color(0xFF3D74F6) else MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // 📝 DESCRIPTION SECTION
                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        Text(
                            text = localizedString(R.string.about_motto),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = pastor?.bio ?: localizedString(R.string.about_description),
                            style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
                            color = if (isDark) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // 📅 WEEKLY PROGRAM SECTION
                    AboutSectionTitle(localizedString(R.string.weekly_program), isDark)

                    Card(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E2635) else MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        border = if (isDark) BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)) else CardDefaults.outlinedCardBorder()
                    ) {
                        Column {
                            if (programs.isEmpty()) {
                                Text(
                                    text = "No programs scheduled.",
                                    modifier = Modifier.padding(24.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isDark) Color.Gray else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                programs.forEachIndexed { index, program ->
                                    ModernProgramRow(
                                        day = program.day,
                                        time = "${program.startHour} - ${program.endHour}",
                                        title = program.title,
                                        isDark = isDark
                                    )
                                    if (index != programs.lastIndex) {
                                        HorizontalDivider(
                                            modifier = Modifier.padding(horizontal = 24.dp),
                                            color = if (isDark) Color.White.copy(alpha = 0.05f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // 📞 CONTACTS SECTION
                    AboutSectionTitle(localizedString(R.string.contacts), isDark)

                    // Social Media & Actions
                    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                        socialMediaList.chunked(2).forEach { rowItems ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                rowItems.forEach { social ->
                                    Box(modifier = Modifier.weight(1f)) {
                                        ContactItem(
                                            imageUrl = social.logo,
                                            size = 30,
                                            text = social.name,
                                            isDark = isDark,
                                            onClik = {
                                                try {
                                                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(social.url))
                                                    context.startActivity(intent)
                                                } catch (e: Exception) {
                                                    // Fallback or error handling
                                                }
                                            }
                                        )
                                    }
                                }
                                if (rowItems.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                        
                        ContactItem(iconVector = Icons.Default.Email, onClik = { 
                            val intent = android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
                                data = android.net.Uri.parse("mailto:${pastor?.email ?: "info@poweroftheword.com"}")
                            }
                            context.startActivity(intent)
                        }, text = pastor?.email ?: "info@poweroftheword.com", isDark = isDark)
                        ContactItem(iconVector = Icons.Default.Favorite, onClik = { onDonationClick() }, text = localizedString(R.string.donate_power_word), isDark = isDark)
                       // ContactItem(iconVector = Icons.Default.MenuBook, text = localizedString(R.string.power_word_story), isDark = isDark)
                        ContactItem(iconVector = Icons.Default.Settings, onClik = { onSettingsClick() }, text = localizedString(R.string.settings), isDark = isDark)
                    }
                    
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun AboutSectionTitle(title: String, isDark: Boolean) {
    Text(
        text = title,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = if (isDark) Color(0xFF3D74F6) else MaterialTheme.colorScheme.primary,
        letterSpacing = 0.5.sp
    )
}

@Composable
fun ModernProgramRow(day: String, time: String, title: String, isDark: Boolean) {
    Column(modifier = Modifier.padding(24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = if (isDark) Color(0xFF3D74F6).copy(alpha = 0.1f) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = day.uppercase(),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFF3D74F6) else MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = time,
                style = MaterialTheme.typography.labelMedium,
                color = if (isDark) Color.Gray else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isDark) Color.White else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ContactItem(
    icon: Int? = null,
    imageUrl: String? = null,
    iconVector: ImageVector? = null,
    onClik: () -> Unit = {},
    size: Int = 22,
    text: String,
    isDark: Boolean
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClik() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = if (isDark) Color(0xFF1E2635) else MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = if (isDark) BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)) else CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            text.contains("info@") || text.contains("@") || text.lowercase().contains("email") -> Color(0xFFFF6B6B).copy(alpha = 0.1f)
                            text.contains("Donate") || text.contains("Dons") || text.contains("Shigikira") || text.contains("Changia") -> Color(0xFFB36BFF).copy(alpha = 0.1f)
                            text.contains("Story") || text.contains("histoire") || text.contains("Hadithi") || text.contains("Amakuru") -> Color(0xFF4A90E2).copy(alpha = 0.1f)
                            text.contains("Settings") || text.contains("Paramètres") || text.contains("Igenamiterere") || text.contains("Mipangilio") -> (if (isDark) Color(0xFF3D74F6) else MaterialTheme.colorScheme.primary).copy(alpha = 0.1f)
                            else -> (if (isDark) Color(0xFF3D74F6) else MaterialTheme.colorScheme.primaryContainer).copy(alpha = 0.4f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier.size(size.dp),
                        contentScale = ContentScale.Fit
                    )
                } else if (icon != null) {
                    Image(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        modifier = Modifier.size(size.dp),
                        contentScale = ContentScale.Fit
                    )
                } else if (iconVector != null) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        tint = when {
                            text.contains("info@") || text.contains("@") || text.lowercase().contains("email") -> Color(0xFFFF6B6B)
                            text.contains("Donate") || text.contains("Dons") || text.contains("Shigikira") || text.contains("Changia") -> Color(0xFFB36BFF)
                            text.contains("Story") || text.contains("histoire") || text.contains("Hadithi") || text.contains("Amakuru") -> Color(0xFF4A90E2)
                            else -> if (isDark) Color(0xFF3D74F6) else MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (isDark) Color.White else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
                maxLines = 1
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = if (isDark) Color.Gray else MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
