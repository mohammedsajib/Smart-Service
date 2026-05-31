package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BdGreen
import com.example.ui.theme.BdRed

// ==========================================
// 1. BILINGUAL TRANSLATION ENGINE
// ==========================================

object Trans {
    fun t(bn: String, en: String, isBn: Boolean): String {
        return if (isBn) bn else en
    }
}

// ==========================================
// 2. SYSTEM UTILS (REAL PHONE CALL / WEB INTENTS)
// ==========================================

object IntentUtils {
    fun dialPhoneNumber(context: Context, phoneNumber: String) {
        if (phoneNumber.isEmpty() || phoneNumber == "N/A" || phoneNumber == "999") {
            // Direct launch for standard numbers or toast if unavailable
            if (phoneNumber == "999") {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:999")
                }
                context.startActivity(intent)
                return
            }
            Toast.makeText(context, "Contact number is not available", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open dialer", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchWebUrl(context: Context, url: String) {
        if (url.isEmpty()) {
            Toast.makeText(context, "Web link not available for this service", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Try wrapping with http if missing
            try {
                val formattedShort = if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    "https://$url"
                } else {
                    url
                }
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(formattedShort))
                context.startActivity(intent)
            } catch (ex: Exception) {
                Toast.makeText(context, "Could not open link inside Android", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

// ==========================================
// 3. SERVICE CATEGORY VISUAL CONFIGURATION
// ==========================================

data class CategoryMeta(
    val code: String,
    val nameBn: String,
    val nameEn: String,
    val icon: ImageVector,
    val color: Color
)

fun getCategoryMetadata(): List<CategoryMeta> {
    return listOf(
        CategoryMeta("GOVT", "সরকারি সেবা", "Govt Services", Icons.Default.AccountBalance, Color(0xFF1E88E5)),
        CategoryMeta("HEALTH", "স্বাস্থ্যসেবা", "Healthcare", Icons.Default.MedicalServices, Color(0xFFE53935)),
        CategoryMeta("EDUCATION", "শিক্ষা সেবা", "Education", Icons.Default.School, Color(0xFFFB8C00)),
        CategoryMeta("TRANSPORT", "পরিবহন", "Transport", Icons.Default.DirectionsBus, Color(0xFF43A047)),
        CategoryMeta("EMERGENCY", "জরুরি সেবা", "Emergency", Icons.Default.Emergency, Color(0xFFD81B60)),
        CategoryMeta("AGRICULTURE", "কৃষি সেবা", "Agriculture", Icons.Default.Agriculture, Color(0xFF7CB342)),
        CategoryMeta("BUSINESS", "স্থানীয় ব্যবসা", "Local Business", Icons.Default.Storefront, Color(0xFF8E24AA)),
        CategoryMeta("TOURISM", "দর্শনীয় স্থান", "Tourism Spots", Icons.Default.Landscape, Color(0xFF00ACC1))
    )
}

// ==========================================
// 4. ROBUST AND STABLE TOP APP BAR COMPOSABLE
// ==========================================

@Composable
fun CustomTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit)? = null
) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )

            if (actions != null) {
                actions()
            }
        }
    }
}

