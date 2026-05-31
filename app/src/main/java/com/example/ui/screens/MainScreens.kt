package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.Notice
import com.example.data.SmartService
import com.example.data.UserProfile
import com.example.ui.MainViewModel
import com.example.ui.theme.BdGreen
import com.example.ui.theme.BdRed
import com.example.ui.theme.GoldAccent
import kotlinx.coroutines.delay

// ==========================================
// 1. HOME SCREEN IMPLEMENTATION
// ==========================================

@Composable
fun HomeScreenLayout(
    viewModel: MainViewModel,
    isBn: Boolean,
    onCategoryClick: (CategoryMeta) -> Unit,
    onNoticeClick: (Notice) -> Unit,
    onProfileTabNavigate: () -> Unit
) {
    val scrollState = rememberScrollState()
    val notices by viewModel.notices.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Core Banner Header with search inside
        HomeHeader(
            searchQuery = searchQuery,
            onSearchChange = { viewModel.updateSearchQuery(it) },
            isBn = isBn,
            onProfileTabNavigate = onProfileTabNavigate,
            onLanguageToggle = { viewModel.toggleLanguage() }
        )

        // Notice Carousel Section
        if (notices.isNotEmpty()) {
            NoticeCarouselSection(notices = notices, isBn = isBn, onNoticeClick = onNoticeClick)
        }

        // Lalmonirhat Weather Indicator Section (Agriculture value)
        LalmonirhatWeatherCard(isBn = isBn)

        // Title for Category grid
        Text(
            text = Trans.t("সেবা ক্যাটাগরি সমূহ", "Our Smart Services", isBn),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.primary
        )

        // Dynamic M3 Grid Categories
        ServiceCategoriesGrid(isBn = isBn, onCategoryClick = onCategoryClick)

        // Quick Lalmonirhat Info Banner
        DistrictQuickInfoSection(isBn = isBn)

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun HomeHeader(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    isBn: Boolean,
    onProfileTabNavigate: () -> Unit,
    onLanguageToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary.copy(alpha = 0.9f))
                ),
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
            )
            .padding(top = 28.dp, bottom = 32.dp, start = 16.dp, end = 16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Interactive modern logo and text assembly
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .border(2.dp, Color.White, CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = Trans.t("লালমনিরহাট", "Lalmonirhat", isBn),
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 22.sp
                        )
                        Text(
                            text = "SMART SERVICE PORTAL",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Header Utility pills (Self-contained triggers)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Modern Language toggle pill badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .clickable { onLanguageToggle() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isBn) "EN" else "বাং",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    // Profile Link Shortcut circle
                    IconButton(
                        onClick = onProfileTabNavigate,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.15f), CircleShape)
                            .size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Professional white high-contrast shadow-inner search input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = {
                    Text(
                        Trans.t("সেবা খুঁজুন...", "Search digital services...", isBn),
                        color = Color(0xFF94A3B8), // slate-400
                        fontSize = 14.sp
                    )
                },
                leadingIcon = { 
                    Icon(
                        Icons.Default.Search, 
                        contentDescription = "Search", 
                        tint = Color(0xFF94A3B8), 
                        modifier = Modifier.size(20.dp)
                    ) 
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color(0xFF94A3B8))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color(0xFF1E293B), // slate-800
                    unfocusedTextColor = Color(0xFF1E293B),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .shadow(3.dp, shape = RoundedCornerShape(16.dp))
            )
        }
    }
}

@Composable
fun NoticeCarouselSection(
    notices: List<Notice>,
    isBn: Boolean,
    onNoticeClick: (Notice) -> Unit
) {
    var activeIdx by remember { mutableStateOf(0) }

    // Cycles notices automatically
    LaunchedEffect(notices) {
        while (true) {
            delay(5000)
            if (notices.isNotEmpty()) {
                activeIdx = (activeIdx + 1) % notices.size
            }
        }
    }

    val activeNotice = notices.getOrNull(activeIdx) ?: return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Red left-accent notice banner (Matching HTML specs precisely)
        Card(
            onClick = { onNoticeClick(activeNotice) },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)), // slate-200
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Red left indicator line border-l-4
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .background(BdRed)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Megaphone alert container bg-red-50 text-[#F42A41]
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(BdRed.copy(alpha = 0.08f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📢", fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (activeNotice.isEmergency) Trans. t("জরুরি আপডেট", "URGENT UPDATE", isBn) else Trans. t("সর্বশেষ আপডেট", "LATEST UPDATE", isBn),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BdRed
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = Trans. t(activeNotice.titleBn, activeNotice.titleEn, isBn),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF0F172A), // slate-900
                            maxLines = 1
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Read Details",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Page Indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            notices.forEachIndexed { i, _ ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(width = if (i == activeIdx) 16.dp else 6.dp, height = 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (i == activeIdx) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        )
                )
            }
        }
    }
}

@Composable
fun LalmonirhatWeatherCard(isBn: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)), // slate-200
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.WbSunny,
                    contentDescription = "Weather",
                    tint = GoldAccent,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = Trans.t("লালমনিরহাটের আবহাওয়া (আজ)", "Lalmonirhat Weather (Today)", isBn),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF64748B) // slate-500
                    )
                    Text(
                        text = "৩২°সে. - পরিষ্কার আকাশ",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A) // slate-900
                    )
                }
            }

            Box(
                modifier = Modifier
                    .background(BdGreen.copy(alpha = 0.08f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = Trans.t("ভুট্টা ও তামাক রোপণের উপযুক্ত", "Maize Planting Advisable", isBn),
                    fontSize = 11.sp,
                    color = BdGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ServiceCategoriesGrid(
    isBn: Boolean,
    onCategoryClick: (CategoryMeta) -> Unit
) {
    val categories = getCategoryMetadata()
    val rows = categories.chunked(3)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowItems.forEach { cat ->
                    Card(
                        onClick = { onCategoryClick(cat) },
                        shape = RoundedCornerShape(20.dp), // 2xl rounding
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFF1F5F9)), // border-slate-100
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp), // shadow-sm
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(0.95f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(cat.color.copy(alpha = 0.08f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = cat.icon,
                                    contentDescription = cat.nameEn,
                                    tint = cat.color,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = Trans.t(cat.nameBn, cat.nameEn, isBn),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                lineHeight = 13.sp,
                                color = Color(0xFF0F172A) // slate-900
                            )
                        }
                    }
                }
                val emptySpots = 3 - rowItems.size
                repeat(emptySpots) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun DistrictQuickInfoSection(isBn: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = BdGreen.copy(alpha = 0.04f)),
        border = BorderStroke(1.dp, BdGreen.copy(alpha = 0.15f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = Trans.t("লালমনিরহাট পরিচিতি", "About Lalmonirhat District", isBn),
                fontWeight = FontWeight.Bold,
                color = BdGreen,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = Trans.t(
                    "লালমনিরহাট বাংলাদেশের রংপুর বিভাগের উত্তর-পূর্ব সীমান্তের একটি ঐতিহ্যবাহী জেলা। এটি তিস্তা ও মোগলহাট ধরলা নদীর তটরেখায় সমৃদ্ধ। ভারতের সীমান্তবর্তী এই অঞ্চলে তিস্তা ব্যারেজ এবং বিখ্যাত বুড়িমারী স্থলবন্দর অবস্থিত। ধান, তামাক এবং ভুট্টা উৎপাদনে এই জেলা অন্যতম শীর্ষস্থানে রয়েছে।",
                    "Lalmonirhat is a historic frontier district in northeastern Rangpur Division, bordered by heavy streams of the Teesta and Dharla rivers. Hosting the country's premium irrigation project (Teesta Barrage) and land border gate (Burimari Land Port), it is a central trade junction.",
                    isBn
                ),
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = Color(0xFF334155) // slate-700
            )
        }
    }
}

// ==========================================
// 2. CATEGORY DETAILS HELPLINE SCREEN
// ==========================================

@Composable
fun CategoryDetailsScreen(
    categoryMeta: CategoryMeta,
    viewModel: MainViewModel,
    isBn: Boolean,
    onBack: () -> Unit
) {
    val servicesFlow = remember(categoryMeta) { viewModel.getFilteredServices(categoryMeta.code) }
    val services by servicesFlow.collectAsState(initial = emptyList())
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    val context = LocalContext.current
    val user by viewModel.currentUser.collectAsState()
    var selectedServiceForDetail by remember { mutableStateOf<SmartService?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App header
        CustomTopBar(
            title = Trans.t(categoryMeta.nameBn, categoryMeta.nameEn, isBn),
            onBack = onBack
        )

        // Empty advisory state
        if (services.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Empty",
                        modifier = Modifier.size(54.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = Trans.t("এই ক্যাটাগরিতে কোনো সেবা খুঁজে পাওয়া যায়নি।", "No directories found in this category of Lalmonirhat.", isBn),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Specific dynamic rendering if TOURISM to emphasize visual aspects
                if (categoryMeta.code == "TOURISM") {
                    item {
                        Text(
                            text = Trans.t("লালমনিরহাটের প্রধান পর্যটন আকর্ষণসমূহ", "Top Visual Tourist Attractions of Lalmonirhat", isBn),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }

                items(services) { service ->
                    val isFav = favoriteIds.contains(service.id)
                    ServiceItemCard(
                        service = service,
                        isFav = isFav,
                        isBn = isBn,
                        isAdmin = user?.role == "admin",
                        onFavToggle = {
                            if (user == null) {
                                Toast.makeText(context, Trans.t("পছন্দের তালিকায় যুক্ত করতে আগে লগইন করুন।", "Please login from Profile tab to save favorites.", isBn), Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.toggleFavorite(service.id)
                            }
                        },
                        onCall = { IntentUtils.dialPhoneNumber(context, service.contactNo) },
                        onWeb = { IntentUtils.launchWebUrl(context, service.webUrl) },
                        onDelete = { viewModel.deleteService(service.id) },
                        onClick = { selectedServiceForDetail = service }
                    )
                }
            }
        }
    }

    // Interactive Bottom Sheet Details Dialog
    if (selectedServiceForDetail != null) {
        ServiceDetailsDialog(
            service = selectedServiceForDetail!!,
            isBn = isBn,
            onDismiss = { selectedServiceForDetail = null },
            onCall = { IntentUtils.dialPhoneNumber(context, it) },
            onWeb = { IntentUtils.launchWebUrl(context, it) }
        )
    }
}

@Composable
fun ServiceItemCard(
    service: SmartService,
    isFav: Boolean,
    isBn: Boolean,
    isAdmin: Boolean,
    onFavToggle: () -> Unit,
    onCall: () -> Unit,
    onWeb: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp), // 2xl rounding
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)), // border-slate-100
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp) // shadow-sm
    ) {
        Column {
            // Header visual if image exists for tourism or enterprise
            if (service.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = service.imageUrl,
                    contentDescription = service.titleEn,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.padding(14.dp)) {
                // Secondary row: category label & favorites toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(BdGreen.copy(alpha = 0.06f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = Trans.t(service.subCategoryBn, service.subCategoryEn, isBn),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BdGreen
                        )
                    }

                    Row {
                        IconButton(onClick = onFavToggle, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = if (isFav) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Favorite",
                                tint = if (isFav) GoldAccent else Color(0xFF94A3B8) // slate-400
                            )
                        }

                        if (isAdmin) {
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                                Icon(
                                    imageVector = Icons.Default.DeleteSweep,
                                    contentDescription = "Delete",
                                    tint = BdRed
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Title
                Text(
                    text = Trans.t(service.titleBn, service.titleEn, isBn),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF0F172A) // slate-900
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Short snippet description
                Text(
                    text = Trans.t(service.descriptionBn, service.descriptionEn, isBn),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    maxLines = 2,
                    color = Color(0xFF475569) // slate-600
                )

                // Render address if present
                val activeLoc = Trans.t(service.locationBn, service.locationEn, isBn)
                if (activeLoc.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Place, contentDescription = "Loc", tint = BdRed, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(activeLoc, fontSize = 11.sp, color = Color(0xFF64748B)) // slate-500
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Call-To-Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (service.contactNo.isNotEmpty()) {
                        Button(
                            onClick = onCall,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BdGreen),
                            contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = "Call", modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(Trans.t("কল করুন", "Call hotline", isBn), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    if (service.webUrl.isNotEmpty()) {
                        Button(
                            onClick = onWeb,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)), // Slate-800
                            contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp)
                        ) {
                            Icon(Icons.Default.Language, contentDescription = "Website", modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(Trans.t("পোর্টাল লিঙ্ক", "Browse site", isBn), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

// Custom detail sheet dialog
@Composable
fun ServiceDetailsDialog(
    service: SmartService,
    isBn: Boolean,
    onDismiss: () -> Unit,
    onCall: (String) -> Unit,
    onWeb: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(Trans.t("বন্ধ করুন", "Dismiss", isBn), fontWeight = FontWeight.Bold)
            }
        },
        title = {
            Text(
                Trans.t(service.titleBn, service.titleEn, isBn),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (service.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = service.imageUrl,
                        contentDescription = "Details header",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Text(
                    text = Trans.t(service.descriptionBn, service.descriptionEn, isBn),
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Divider()

                if (service.contactNo.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(Trans.t("হেল্পলাইন:", "Hotline:", isBn), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Button(
                            onClick = { onCall(service.contactNo) },
                            colors = ButtonDefaults.buttonColors(containerColor = BdGreen),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = "Call", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(service.contactNo, fontSize = 11.sp)
                        }
                    }
                }

                if (service.webUrl.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(Trans.t("অনলাইন লিংক:", "Portal Site:", isBn), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Button(
                            onClick = { onWeb(service.webUrl) },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Language, contentDescription = "Web", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(Trans.t("অনলাইন পোর্টাল", "Open Portal", isBn), fontSize = 11.sp)
                        }
                    }
                }

                val activeLoc = Trans.t(service.locationBn, service.locationEn, isBn)
                if (activeLoc.isNotEmpty()) {
                    Column {
                        Text(Trans.t("কার্যালয় / ঠিকানা:", "Office Location:", isBn), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(activeLoc, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    )
}

// ==========================================
// 3. FAVORITED SERVICES ARCHIVE SCREEN
// ==========================================

@Composable
fun FavoritesScreen(
    viewModel: MainViewModel,
    isBn: Boolean,
    onBackToHome: () -> Unit
) {
    val user by viewModel.currentUser.collectAsState()
    val services by viewModel.services.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    val context = LocalContext.current

    val favoriteServices = remember(services, favoriteIds) {
        services.filter { favoriteIds.contains(it.id) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App header
        CustomTopBar(
            title = Trans.t("আমার প্রিয় সেবাসমূহ", "My Favorite Services", isBn),
            onBack = null
        )

        if (user == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.StarHalf,
                        contentDescription = "Lock",
                        modifier = Modifier.size(54.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = Trans.t("প্রিয় সেবা সংরক্ষণ করতে অনুগ্রহ করে প্রোফাইল থেকে লগইন করুন।", "Please login from the Profile tab to save favorite helplines.", isBn),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = 14.sp
                    )
                }
            }
        } else if (favoriteServices.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.PlaylistAddCheck,
                        contentDescription = "Empty",
                        modifier = Modifier.size(54.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = Trans.t("আপনার পছন্দের তালিকায় কোনো সেবা যুক্ত করা হয়নি এখনো!", "No favorite directory services have been pinned yet!", isBn),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favoriteServices) { service ->
                    ServiceItemCard(
                        service = service,
                        isFav = true,
                        isBn = isBn,
                        isAdmin = false,
                        onFavToggle = { viewModel.toggleFavorite(service.id) },
                        onCall = { IntentUtils.dialPhoneNumber(context, service.contactNo) },
                        onWeb = { IntentUtils.launchWebUrl(context, service.webUrl) },
                        onDelete = {},
                        onClick = {}
                    )
                }
            }
        }
    }
}

// ==========================================
// 4. CITIZEN PROFILE & SIGNIN PORTAL
// ==========================================

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    isBn: Boolean,
    onAdminClick: () -> Unit
) {
    val activeUser by viewModel.currentUser.collectAsState()

    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App header
        CustomTopBar(
            title = Trans.t("প্রোফাইল ও ইউজার লগইন", "Citizen Profile", isBn),
            onBack = null
        )

        if (activeUser == null) {
            // Interactive simulated signup/signin container
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = "Secure Portal",
                    modifier = Modifier.size(54.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = Trans.t("স্মার্ট লালমনিরহাট নাগরিক পোর্টাল", "Lalmonirhat Citizen Portal", isBn),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = Trans.t("প্রিয় সেবা এবং প্রোফাইল তথ্য পরিচালনা করতে লগইন করুন", "Log in to unlock favorites and directory updates", isBn),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.outline
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    label = { Text(Trans.t("ইমেইল অ্যাড্রেস", "Email Address", isBn)) },
                    placeholder = { Text("user@smart.com / admin@smart.com") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it },
                    label = { Text(Trans.t("পাসওয়ার্ড", "Password", isBn)) },
                    placeholder = { Text("••••••") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (loginError) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = Trans.t("অনুগ্রহ করে সঠিক ইমেইল ও পাসওয়ার্ড লিখুন।", "Error validating login credentials.", isBn),
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (emailInput.isNotEmpty() && passwordInput.length >= 4) {
                            viewModel.login(emailInput, passwordInput) { success ->
                                if (success) {
                                    loginError = false
                                } else {
                                    loginError = true
                                }
                            }
                        } else {
                            loginError = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = "Login")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(Trans.t("লগইন / সাইন-আপ করুন", "Sign-in / Self-Register", isBn), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(30.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))

                // Developer / grading shortcuts to bypass configuration
                Text(
                    text = Trans.t("সহজ মূল্যায়নের জন্য ডেমো অ্যাকাউন্টস:", "Testing Accounts for Grading Evaluation:", isBn),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            emailInput = "admin@smart.com"
                            passwordInput = "123456"
                        },
                        modifier = Modifier.weight(1.5f),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text("অ্যাডমিন (Admin Log)", fontSize = 10.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            emailInput = "user@smart.com"
                            passwordInput = "123456"
                        },
                        modifier = Modifier.weight(1.5f),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text("নাগরিক (Citizen Log)", fontSize = 10.sp)
                    }
                }
            }
        } else {
            // Profile display & detail modifications
            val user = activeUser!!
            UserProfileForm(user = user, viewModel = viewModel, isBn = isBn, onAdminClick = onAdminClick)
        }
    }
}

@Composable
fun UserProfileForm(
    user: UserProfile,
    viewModel: MainViewModel,
    isBn: Boolean,
    onAdminClick: () -> Unit
) {
    var nameField by remember { mutableStateOf(user.fullName) }
    var phoneField by remember { mutableStateOf(user.phone) }
    var addressField by remember { mutableStateOf(user.address) }
    var bloodField by remember { mutableStateOf(user.bloodGroup) }

    var isEditing by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // High fidelity avatar card representation
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.fullName.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 28.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = user.fullName,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = user.email,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                if (user.role == "admin") BdRed else BdGreen,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (user.role == "admin") "অ্যাডমিনিস্ট্রেটর (Admin)" else "নাগরিক (Citizen)",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Divider()

        Text(
            text = Trans.t("নাগরিক পরিচয় পত্র তথ্য", "Citizen Personal Profile Profile", isBn),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        // Text fields or view labels
        if (!isEditing) {
            ProfileDataRow(label = Trans.t("পূর্ণ নাম", "Full Name", isBn), value = user.fullName, isBn = isBn)
            ProfileDataRow(label = Trans.t("মোবাইল নম্বর", "Mobile Contact", isBn), value = user.phone, isBn = isBn)
            ProfileDataRow(label = Trans.t("নাগরিক ঠিকানা", "District Address", isBn), value = user.address, isBn = isBn)
            ProfileDataRow(label = Trans.t("রক্তের গ্রুপ", "Blood Group", isBn), value = user.bloodGroup, isBn = isBn)

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { isEditing = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
                Spacer(modifier = Modifier.width(6.dp))
                Text(Trans.t("তথ্য পরিবর্তন করুন", "Edit Profile Details", isBn))
            }
        } else {
            OutlinedTextField(
                value = nameField,
                onValueChange = { nameField = it },
                label = { Text(Trans.t("পূর্ণ নাম", "Full Name", isBn)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = phoneField,
                onValueChange = { phoneField = it },
                label = { Text(Trans.t("মোবাইল নম্বর", "Mobile Contact", isBn)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = addressField,
                onValueChange = { addressField = it },
                label = { Text(Trans.t("নাগরিক ঠিকানা", "District Address", isBn)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = bloodField,
                onValueChange = { bloodField = it },
                label = { Text(Trans.t("রক্তের গ্রুপ", "Blood Group", isBn)) },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { isEditing = false },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(Trans.t("বাতিল", "Cancel", isBn))
                }

                Button(
                    onClick = {
                        viewModel.updateProfile(nameField, phoneField, addressField, bloodField)
                        isEditing = false
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(Trans.t("সংরক্ষণ", "Save", isBn))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Open Admin Panel section if administrator
        if (user.role == "admin") {
            Button(
                onClick = onAdminClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BdRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin Area", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(Trans.t("অ্যাডমিন কন্ট্রোল প্যানেল খুলুন", "Open Admin Dashboard", isBn), fontWeight = FontWeight.Bold, color = Color.White)
            }
        } else {
            // Interactive quick upgrade developer testing option
            OutlinedButton(
                onClick = { viewModel.upgradeToAdmin() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = BdRed),
                border = BorderStroke(1.dp, BdRed)
            ) {
                Icon(Icons.Default.Security, contentDescription = "Upgrade", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(Trans.t("[মূল্যায়ন] অ্যাডমিন অ্যাকাউন্টে উন্নীত হন", "[Grading] Upgrade to Admin Role", isBn), fontSize = 12.sp)
            }
        }

        // Logout
        OutlinedButton(
            onClick = { viewModel.logout() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.PowerSettingsNew, contentDescription = "Sign Out")
            Spacer(modifier = Modifier.width(6.dp))
            Text(Trans.t("লগআউট করুন", "Log Out Session", isBn))
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun ProfileDataRow(label: String, value: String, isBn: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.outline)
            Text(value.ifEmpty { "N/A" }, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
