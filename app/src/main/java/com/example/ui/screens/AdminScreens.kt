package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SmartService
import com.example.ui.MainViewModel

@Composable
fun AdminDashboardScreen(
    viewModel: MainViewModel,
    isBn: Boolean,
    onBack: () -> Unit
) {
    var activeTab by remember { mutableStateOf("SERVICES") } // "SERVICES" or "NOTICES"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App header
        CustomTopBar(
            title = Trans.t("অ্যাডমিন প্যানেল", "Admin Panel", isBn),
            onBack = onBack
        )

        // Tab Selection
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { activeTab = "SERVICES" },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeTab == "SERVICES") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (activeTab == "SERVICES") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Icon(Icons.Default.PostAdd, contentDescription = "Add Service")
                Spacer(modifier = Modifier.width(4.dp))
                Text(Trans.t("সেবা যুক্ত করুন", "Add Service", isBn))
            }

            Button(
                onClick = { activeTab = "NOTICES" },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeTab == "NOTICES") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (activeTab == "NOTICES") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Icon(Icons.Default.Campaign, contentDescription = "Add Notice")
                Spacer(modifier = Modifier.width(4.dp))
                Text(Trans.t("নোটিশ জারি", "Add Notice", isBn))
            }
        }

        // Selected Tab Content
        if (activeTab == "SERVICES") {
            AddServiceForm(viewModel, isBn)
        } else {
            AddNoticeForm(viewModel, isBn)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddServiceForm(viewModel: MainViewModel, isBn: Boolean) {
    val scrollState = rememberScrollState()

    var category by remember { mutableStateOf("GOVT") }
    var titleBn by remember { mutableStateOf("") }
    var titleEn by remember { mutableStateOf("") }
    var subCategoryBn by remember { mutableStateOf("") }
    var subCategoryEn by remember { mutableStateOf("") }
    var descriptionBn by remember { mutableStateOf("") }
    var descriptionEn by remember { mutableStateOf("") }
    var contactNo by remember { mutableStateOf("") }
    var webUrl by remember { mutableStateOf("") }
    var locationBn by remember { mutableStateOf("") }
    var locationEn by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    var expandedDropdown by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            Trans.t("নতুন নাগরিক সেবা বা তথ্য যুক্ত করুন", "Add New Citizen Service / Directory Info", isBn),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        // Dropdown Category Selector
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = Trans.t(getCategoryNameBn(category), getCategoryNameEn(category), isBn),
                onValueChange = {},
                readOnly = true,
                label = { Text(Trans.t("ক্যাটাগরি নির্ধারণ করুন *", "Select Category *", isBn)) },
                trailingIcon = {
                    IconButton(onClick = { expandedDropdown = true }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Open")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(
                expanded = expandedDropdown,
                onDismissRequest = { expandedDropdown = false }
            ) {
                val cats = listOf("GOVT", "HEALTH", "EDUCATION", "TRANSPORT", "EMERGENCY", "AGRICULTURE", "BUSINESS", "TOURISM")
                cats.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(Trans.t(getCategoryNameBn(cat), getCategoryNameEn(cat), isBn)) },
                        onClick = {
                            category = cat
                            expandedDropdown = false
                        }
                    )
                }
            }
        }

        // Sub Category
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = subCategoryBn,
                onValueChange = { subCategoryBn = it },
                label = { Text(Trans.t("সাব-ক্যাটাগরি (বাংলা) *", "Sub Category (BN) *", isBn)) },
                placeholder = { Text("যেমন: হাসপাতাল / ট্রেনের সূচি") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = subCategoryEn,
                onValueChange = { subCategoryEn = it },
                label = { Text(Trans.t("সাব-ক্যাটাগরি (EN) *", "Sub Category (EN) *", isBn)) },
                placeholder = { Text("e.g., Hospitals / Trains") },
                modifier = Modifier.weight(1f)
            )
        }

        // Titles
        OutlinedTextField(
            value = titleBn,
            onValueChange = { titleBn = it },
            label = { Text(Trans.t("সেবা বা প্রতিষ্ঠানের শিরোনাম (বাংলা) *", "Service/Organization Title (BN) *", isBn)) },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = titleEn,
            onValueChange = { titleEn = it },
            label = { Text(Trans.t("সেবা বা প্রতিষ্ঠানের শিরোনাম (EN) *", "Service/Organization Title (EN) *", isBn)) },
            modifier = Modifier.fillMaxWidth()
        )

        // Descriptions
        OutlinedTextField(
            value = descriptionBn,
            onValueChange = { descriptionBn = it },
            label = { Text(Trans.t("বিস্তারিত বিবরণ (বাংলা) *", "Detailed Description (BN) *", isBn)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        OutlinedTextField(
            value = descriptionEn,
            onValueChange = { descriptionEn = it },
            label = { Text(Trans.t("বিস্তারিত বিবরণ (EN) *", "Detailed Description (EN) *", isBn)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        // Hotline / Contact
        OutlinedTextField(
            value = contactNo,
            onValueChange = { contactNo = it },
            label = { Text(Trans.t("যোগাযোগ নম্বর / হেল্পলাইন", "Hotline / Phone Number", isBn)) },
            placeholder = { Text("যেমন: 01713XXXXXX") },
            modifier = Modifier.fillMaxWidth()
        )

        // Web Link / Application portal URL
        OutlinedTextField(
            value = webUrl,
            onValueChange = { webUrl = it },
            label = { Text(Trans.t("ওয়েবসাইট লিঙ্ক / অনলাইন পোর্টাল", "Website link / Portal URL", isBn)) },
            placeholder = { Text("যেমন: https://bdris.gov.bd") },
            modifier = Modifier.fillMaxWidth()
        )

        // Location Info
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = locationBn,
                onValueChange = { locationBn = it },
                label = { Text(Trans.t("ঠিকানা / অবস্থান (বাংলা)", "Location (BN)", isBn)) },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = locationEn,
                onValueChange = { locationEn = it },
                label = { Text(Trans.t("ঠিকানা / অবস্থান (EN)", "Location (EN)", isBn)) },
                modifier = Modifier.weight(1f)
            )
        }

        // Image URL (optional)
        OutlinedTextField(
            value = imageUrl,
            onValueChange = { imageUrl = it },
            label = { Text(Trans.t("ছবি লিংক / ব্যানার (ঐচ্ছিক)", "Display Image URL (Optional)", isBn)) },
            placeholder = { Text("https://images.unsplash.com/...") },
            modifier = Modifier.fillMaxWidth()
        )

        // Submit Button
        Button(
            onClick = {
                if (titleBn.isNotEmpty() && titleEn.isNotEmpty() && subCategoryBn.isNotEmpty() && subCategoryEn.isNotEmpty()) {
                    viewModel.addService(
                        category = category,
                        titleBn = titleBn,
                        titleEn = titleEn,
                        subCategoryBn = subCategoryBn,
                        subCategoryEn = subCategoryEn,
                        descriptionBn = descriptionBn,
                        descriptionEn = descriptionEn,
                        contactNo = contactNo,
                        webUrl = webUrl,
                        locationBn = locationBn,
                        locationEn = locationEn,
                        imageUrl = imageUrl
                    )
                    showDialog = true
                    // Clear fields
                    titleBn = ""
                    titleEn = ""
                    subCategoryBn = ""
                    subCategoryEn = ""
                    descriptionBn = ""
                    descriptionEn = ""
                    contactNo = ""
                    webUrl = ""
                    locationBn = ""
                    locationEn = ""
                    imageUrl = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Submit")
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                Trans.t("তথ্য সংরক্ষণ করুন", "Save Directory Info", isBn),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text(Trans.t("ঠিক আছে", "OK", isBn))
                }
            },
            title = { Text(Trans.t("সংরক্ষণ সম্পন্ন", "Saved Successfully", isBn)) },
            text = { Text(Trans.t("নতুন সেবাটি ডাটাবেজে সফলভাবে যুক্ত করা হয়েছে!", "The directory listing has been successfully saved to the Room SQL store!", isBn)) }
        )
    }
}

@Composable
fun AddNoticeForm(viewModel: MainViewModel, isBn: Boolean) {
    var titleBn by remember { mutableStateOf("") }
    var titleEn by remember { mutableStateOf("") }
    var contentBn by remember { mutableStateOf("") }
    var contentEn by remember { mutableStateOf("") }
    var isEmergency by remember { mutableStateOf(false) }

    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            Trans.t("নতুন প্রশাসনিক নোটিশ প্রকাশ করুন", "Publish New Citizen Notice", isBn),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        OutlinedTextField(
            value = titleBn,
            onValueChange = { titleBn = it },
            label = { Text(Trans.t("নোটিশের শিরোনাম (বাংলা) *", "Notice Title (BN) *", isBn)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = titleEn,
            onValueChange = { titleEn = it },
            label = { Text(Trans.t("নোটিশের শিরোনাম (EN) *", "Notice Title (EN) *", isBn)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = contentBn,
            onValueChange = { contentBn = it },
            label = { Text(Trans.t("নোটিশের তথ্য (বাংলা) *", "Notice Body Content (BN) *", isBn)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4
        )

        OutlinedTextField(
            value = contentEn,
            onValueChange = { contentEn = it },
            label = { Text(Trans.t("নোটিশের তথ্য (EN) *", "Notice Body Content (EN) *", isBn)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4
        )

        // Emergency Toggle Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Checkbox(
                checked = isEmergency,
                onCheckedChange = { isEmergency = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                Trans.t("জরুরি নোটিশ হিসেবে চিহ্নিত করুন", "Flag as Emergency Notice", isBn),
                fontWeight = FontWeight.Medium,
                color = if (isEmergency) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
        }

        // Save btn
        Button(
            onClick = {
                if (titleBn.isNotEmpty() && titleEn.isNotEmpty() && contentBn.isNotEmpty() && contentEn.isNotEmpty()) {
                    viewModel.addNotice(
                        titleBn = titleBn,
                        titleEn = titleEn,
                        contentBn = contentBn,
                        contentEn = contentEn,
                        isEmergency = isEmergency
                    )
                    showDialog = true
                    titleBn = ""
                    titleEn = ""
                    contentBn = ""
                    contentEn = ""
                    isEmergency = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isEmergency) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Campaign, contentDescription = "Broadcast")
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                Trans.t("নোটিশ জারি করুন", "Deploy Notice Broadcast", isBn),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text(Trans.t("ঠিক আছে", "OK", isBn))
                }
            },
            title = { Text(Trans.t("নোটিশ জারি সম্পন্ন", "Broadcast Dispatched", isBn)) },
            text = { Text(Trans.t("নতুন নাগরিক নোটিশটি অ্যাপের হোম স্ক্রিনে সফলভাবে সম্প্রচার করা হয়েছে!", "The notice broadcast is now live on the citizen home dashboard ticker!", isBn)) }
        )
    }
}

// Category helper maps
private fun getCategoryNameBn(cat: String): String {
    return when (cat) {
        "GOVT" -> "সরকারি সেবা"
        "HEALTH" -> "স্বাস্থ্যসেবা"
        "EDUCATION" -> "শিক্ষা সেবা"
        "TRANSPORT" -> "পরিবহন"
        "EMERGENCY" -> "জরুরি সেবা"
        "AGRICULTURE" -> "কৃষি সেবা"
        "BUSINESS" -> "স্থানীয় ব্যবসা"
        "TOURISM" -> "দর্শনীয় স্থান"
        else -> cat
    }
}

private fun getCategoryNameEn(cat: String): String {
    return when (cat) {
        "GOVT" -> "Govt Services"
        "HEALTH" -> "Healthcare"
        "EDUCATION" -> "Education Info"
        "TRANSPORT" -> "Transport Trains/Buses"
        "EMERGENCY" -> "Emergency Hotlines"
        "AGRICULTURE" -> "Agriculture DAE"
        "BUSINESS" -> "Local Directory"
        "TOURISM" -> "Tourism Spots"
        else -> cat
    }
}
