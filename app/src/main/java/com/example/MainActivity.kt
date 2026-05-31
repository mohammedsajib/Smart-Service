package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Notice
import com.example.ui.MainViewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

// Simple, crash-proof type-safe local router states
sealed class AppScreen {
    object Home : AppScreen()
    data class CategoryDetails(val meta: CategoryMeta) : AppScreen()
    object AdminPanel : AppScreen()
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            MyApplicationTheme {
                // Tracking language
                val isBn by viewModel.isBangla.collectAsState()
                
                // Track active screen
                var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.Home) }
                
                // Track active navigation tab (Home, Favorites, Profile)
                var currentNavTab by remember { mutableStateOf("HOME") }

                // Selected Notice Detail Dialog
                var activeNoticeModal by remember { mutableStateOf<Notice?>(null) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        // M3 standard bottom navigation bar when on parent roots
                        if (currentScreen == AppScreen.Home) {
                            NavigationBar(
                                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 8.dp
                            ) {
                                NavigationBarItem(
                                    selected = currentNavTab == "HOME",
                                    onClick = { currentNavTab = "HOME" },
                                    icon = { Icon(if (currentNavTab == "HOME") Icons.Default.Home else Icons.Default.Home, contentDescription = "Home") },
                                    label = { Text(Trans.t("হোম", "Home", isBn), fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                                )

                                NavigationBarItem(
                                    selected = currentNavTab == "FAVORITES",
                                    onClick = { currentNavTab = "FAVORITES" },
                                    icon = { Icon(if (currentNavTab == "FAVORITES") Icons.Default.Star else Icons.Default.StarBorder, contentDescription = "Favorites") },
                                    label = { Text(Trans.t("প্রিয় তালিকা", "Favorites", isBn), fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                                )

                                NavigationBarItem(
                                    selected = currentNavTab == "PROFILE",
                                    onClick = { currentNavTab = "PROFILE" },
                                    icon = { Icon(if (currentNavTab == "PROFILE") Icons.Default.Person else Icons.Default.Person, contentDescription = "Profile") },
                                    label = { Text(Trans.t("প্রোফাইল", "Profile", isBn), fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Active Screen controller
                            when (val screen = currentScreen) {
                                is AppScreen.Home -> {
                                    when (currentNavTab) {
                                        "HOME" -> {
                                            HomeScreenLayout(
                                                viewModel = viewModel,
                                                isBn = isBn,
                                                onCategoryClick = { cat ->
                                                    currentScreen = AppScreen.CategoryDetails(cat)
                                                },
                                                onNoticeClick = { notice ->
                                                    activeNoticeModal = notice
                                                },
                                                onProfileTabNavigate = { currentNavTab = "PROFILE" }
                                            )
                                        }
                                        "FAVORITES" -> {
                                            FavoritesScreen(
                                                viewModel = viewModel,
                                                isBn = isBn,
                                                onBackToHome = { currentNavTab = "HOME" }
                                            )
                                        }
                                        "PROFILE" -> {
                                            ProfileScreen(
                                                viewModel = viewModel,
                                                isBn = isBn,
                                                onAdminClick = { currentScreen = AppScreen.AdminPanel }
                                            )
                                        }
                                    }
                                }

                                is AppScreen.CategoryDetails -> {
                                    CategoryDetailsScreen(
                                        categoryMeta = screen.meta,
                                        viewModel = viewModel,
                                        isBn = isBn,
                                        onBack = { currentScreen = AppScreen.Home }
                                    )
                                }

                                is AppScreen.AdminPanel -> {
                                    AdminDashboardScreen(
                                        viewModel = viewModel,
                                        isBn = isBn,
                                        onBack = { currentScreen = AppScreen.Home }
                                    )
                                }
                            }
                        }

                        // Detailed global notification notice reader popover
                        if (activeNoticeModal != null) {
                            val activeNotice = activeNoticeModal!!
                            AlertDialog(
                                onDismissRequest = { activeNoticeModal = null },
                                confirmButton = {
                                    Button(onClick = { activeNoticeModal = null }) {
                                        Text(Trans.t("বন্ধ করুন", "Close", isBn), fontWeight = FontWeight.Bold)
                                    }
                                },
                                title = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Campaign, contentDescription = "Alert", tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = Trans.t(activeNotice.titleBn, activeNotice.titleEn, isBn),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 16.sp
                                        )
                                    }
                                },
                                text = {
                                    Column {
                                        Text(
                                            text = Trans.t(activeNotice.contentBn, activeNotice.contentEn, isBn),
                                            fontSize = 13.sp,
                                            lineHeight = 18.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(14.dp))
                                        Text(
                                            text = "${Trans.t("প্রকাশের তারিখ:", "Published on:", isBn)} ${activeNotice.dateString}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
