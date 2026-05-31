package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// ==========================================
// 1. DATA ENTITIES
// ==========================================

@Entity(tableName = "notices")
data class Notice(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titleBn: String,
    val titleEn: String,
    val contentBn: String,
    val contentEn: String,
    val dateString: String,
    val isEmergency: Boolean = false
)

@Entity(tableName = "smart_services")
data class SmartService(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String, // GOVT, HEALTH, EDUCATION, TRANSPORT, EMERGENCY, AGRICULTURE, BUSINESS, TOURISM
    val titleBn: String,
    val titleEn: String,
    val subCategoryBn: String,
    val subCategoryEn: String,
    val descriptionBn: String,
    val descriptionEn: String,
    val contactNo: String,
    val webUrl: String,
    val imageUrl: String = "",
    val locationBn: String = "",
    val locationEn: String = "",
    val isVerified: Boolean = true
)

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val uid: String, // E.g., email or random UID
    val fullName: String,
    val email: String,
    val phone: String,
    val address: String,
    val bloodGroup: String = "N/A",
    val role: String = "user" // "user" or "admin"
)

@Entity(tableName = "favorites", primaryKeys = ["userId", "serviceId"])
data class Favorite(
    val userId: String,
    val serviceId: Int
)

// ==========================================
// 2. DATA ACCESS OBJECTS (DAO)
// ==========================================

@Dao
interface AppDao {
    // Notices
    @Query("SELECT * FROM notices ORDER BY id DESC")
    fun getAllNotices(): Flow<List<Notice>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotice(notice: Notice)

    @Update
    suspend fun updateNotice(notice: Notice)

    @Query("DELETE FROM notices WHERE id = :id")
    suspend fun deleteNoticeById(id: Int)

    // Services
    @Query("SELECT * FROM smart_services")
    fun getAllServices(): Flow<List<SmartService>>

    @Query("SELECT * FROM smart_services WHERE category = :category")
    fun getServicesByCategory(category: String): Flow<List<SmartService>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: SmartService)

    @Update
    suspend fun updateService(service: SmartService)

    @Query("DELETE FROM smart_services WHERE id = :id")
    suspend fun deleteServiceById(id: Int)

    // User Profile
    @Query("SELECT * FROM user_profiles WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserProfile?

    @Query("SELECT * FROM user_profiles WHERE uid = :uid LIMIT 1")
    fun getUserProfileFlow(uid: String): Flow<UserProfile?>

    @Query("SELECT * FROM user_profiles WHERE uid = :uid LIMIT 1")
    suspend fun getUserProfileDirect(uid: String): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(user: UserProfile)

    @Update
    suspend fun updateUserProfile(user: UserProfile)

    // Favorites
    @Query("SELECT serviceId FROM favorites WHERE userId = :userId")
    fun getFavoriteIds(userId: String): Flow<List<Int>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: Favorite)

    @Query("DELETE FROM favorites WHERE userId = :userId AND serviceId = :serviceId")
    suspend fun removeFavorite(userId: String, serviceId: Int)
}

// ==========================================
// 3. DATABASE SETUP & PRE-POPULATION
// ==========================================

@Database(
    entities = [Notice::class, SmartService::class, UserProfile::class, Favorite::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lalmonirhat_smart_database"
                )
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    prepopulateDatabase(database.dao())
                }
            }
        }
    }
}

// ==========================================
// 4. RICH LALMONIRHAT PRE-POPULATED DATA
// ==========================================

suspend fun prepopulateDatabase(dao: AppDao) {
    // 1. Initial Notices
    dao.insertNotice(
        Notice(
            titleBn = "লালমনিরহাট স্মার্ট সার্ভিস অ্যাপে আপনাকে স্বাগতম!",
            titleEn = "Welcome to Lalmonirhat Smart Service App!",
            contentBn = "এখন থেকে লালমনিরহাট জেলার সকল গুরুত্বপূর্ণ নাগরিক সেবা ও তথ্য এক ঠিকানায় খুব সহজেই পেয়ে যাবেন।",
            contentEn = "From now on, receive all important citizen services and public utility directory listings of Lalmonirhat district in one unified interface.",
            dateString = "31 May, 2026",
            isEmergency = true
        )
    )
    dao.insertNotice(
        Notice(
            titleBn = "সড়ক নিরাপত্তা সচেতনতা ক্যাম্পেইন",
            titleEn = "Road Safety Awareness Campaign",
            contentBn = "লালমনিরহাটে ট্রাফিক ট্র্যাকিং জোরদার করতে এবং সাধারণ মানুষকে সচেতন করার লক্ষ্যে আগামী পরশু থেকে জেলা পুলিশের উদ্যোগে বিশেষ ক্যাম্পেইন শুরু হতে যাচ্ছে।",
            contentEn = "A road safety and traffic rules awareness campaign is scheduled to commence the day after tomorrow by the District Police force.",
            dateString = "30 May, 2026",
            isEmergency = false
        )
    )
    dao.insertNotice(
        Notice(
            titleBn = "অনলাইনে জন্ম এবং মৃত্যু নিবন্ধন আবেদন যাচাই",
            titleEn = "Online Birth & Death Registration Updates",
            contentBn = "জন্ম নিবন্ধন ফি এবং সরকারি বিধিমালা সংশোধন করা হয়েছে। বিস্তারিত জানার জন্য সরকারি সেবা ক্যাটাগরি ব্রাউজ করুন।",
            contentEn = "Official rules and fee brackets for online birth and death registration have been revised. Browse Government Services for details.",
            dateString = "28 May, 2026",
            isEmergency = false
        )
    )

    // 2. Government Services
    dao.insertService(
        SmartService(
            category = "GOVT",
            titleBn = "জন্ম ও মৃত্যু নিবন্ধন পোর্টাল",
            titleEn = "Birth & Death Registration Portal",
            subCategoryBn = "নাগরিক সেবা",
            subCategoryEn = "Citizen Services",
            descriptionBn = "অনলাইনে নতুন জন্ম সনদের জন্য আবেদন করুন, আবেদনের বর্তমান অবস্থা পরীক্ষা করুন এবং ভুল তথ্য সংশোধন করুন। সংশোধন ফি এবং প্রয়োজনীয় কাগজপত্রের তালিকা নির্দেশিকা অপশনে রয়েছে।",
            descriptionEn = "Submit applications for new birth registration certificates online, verify current processing status, and rectify records. Correct government fees apply.",
            contactNo = "16122",
            webUrl = "https://bdris.gov.bd/"
        )
    )
    dao.insertService(
        SmartService(
            category = "GOVT",
            titleBn = "জাতীয় পরিচয়পত্র (NID) সেবা",
            titleEn = "National ID (NID) Services",
            subCategoryBn = "নাগরিক পরিচয়",
            subCategoryEn = "National Registry",
            descriptionBn = "বাংলাদেশ নির্বাচন কমিশন পোর্টাল থেকে নতুন এনআইডি ভোটার নিবন্ধন, অনলাইন কার্ড কপি ডাউনলোড এবং এনআইডি কার্ডের তথ্য সংশোধন প্রক্রিয়া।",
            descriptionEn = "Check registration guidelines, update address, download online soft-copy of NID, and check correction status securely via Bangladesh Election Commission portal.",
            contactNo = "105",
            webUrl = "https://services.nidw.gov.bd/"
        )
    )
    dao.insertService(
        SmartService(
            category = "GOVT",
            titleBn = "ই-পাসপোর্ট সেবা",
            titleEn = "E-Passport Portal",
            subCategoryBn = "ভ্রমণ দলিল",
            subCategoryEn = "Travel Documents",
            descriptionBn = "নতুন পাসপোর্ট আবেদন, স্লট বুকিং, ফি জমার চালানের নিয়ম এবং লালমনিরহাট আঞ্চলিক পাসপোর্ট অফিসের লোকেশন সহ বিস্তারিত তথ্যাদি।",
            descriptionEn = "Easily complete virtual registrations for Bangladesh e-Passports, retrieve schedules, check bank challan options, and book physical appointments.",
            contactNo = "01720889977",
            webUrl = "https://www.epassport.gov.bd/"
        )
    )
    dao.insertService(
        SmartService(
            category = "GOVT",
            titleBn = "ভূমি পোর্টাল ও ই-নামজারি",
            titleEn = "Land Portal & E-Namjari",
            subCategoryBn = "ভূমি রাজস্বর",
            subCategoryEn = "Land Registry",
            descriptionBn = "ভূমি নামজারি (মিউটেশন), অনলাইনে খতিয়ান (পর্চা) তল্লাশি, ভূমি রেকর্ড ও খতিয়ান সেবার অফিশিয়াল লিংক।",
            descriptionEn = "Initiate land mutation files, check digital khatian archives, pay land holding taxes, and contact the local land registry office.",
            contactNo = "16122",
            webUrl = "https://land.gov.bd/"
        )
    )
    dao.insertService(
        SmartService(
            category = "GOVT",
            titleBn = "ড্রাইভিং লাইসেন্স তথ্য (BRTA)",
            titleEn = "Driving License Portal (BRTA)",
            subCategoryBn = "পরিবহন লাইসেন্স",
            subCategoryEn = "Road Safety Licenses",
            descriptionBn = "বিআরটিএ পোর্টাল থেকে লার্নার লাইসেন্স, স্মার্ট কার্ড ড্রাইভিং লাইসেন্স পরীক্ষার সিলেবাস ও ফি জমার নির্দেশনা।",
            descriptionEn = "Register for driver learner cards, complete licensing profiles, practice exam questionnaires, and pay official fees instantly on the BRTA BSP portal.",
            contactNo = "16107",
            webUrl = "https://bsp.brta.gov.bd/"
        )
    )

    // 3. Health Services
    dao.insertService(
        SmartService(
            category = "HEALTH",
            titleBn = "লালমনিরহাট সদর হাসপাতাল",
            titleEn = "Lalmonirhat Sadar Hospital",
            subCategoryBn = "হাসপাতাল",
            subCategoryEn = "General Hospitals",
            descriptionBn = "লালমনিরহাট জেলার প্রধান সরকারি হাসপাতাল। এখানে চব্বিশ ঘণ্টা জরুরি চিকিৎসা, ইনডোর এবং আউটডোর ওটি সুবিধা এবং বিশেষজ্ঞ কনসালট্যান্ট সেবা রয়েছে।",
            descriptionEn = "Central public hospital facility of Lalmonirhat District. Provides 24/7 general patient admission, emergency triage, ICU, and specialist consultants.",
            contactNo = "01713355202",
            webUrl = "https://dghs.gov.bd",
            imageUrl = "https://images.unsplash.com/photo-1586773860418-d3b3de97e663?auto=format&fit=crop&q=80&w=300",
            locationBn = "সদর হাসপাতাল রোড, লালমনিরহাট",
            locationEn = "Sadar Hospital Road, Lalmonirhat"
        )
    )
    dao.insertService(
        SmartService(
            category = "HEALTH",
            titleBn = "ডাক্তার আশরাফুল ইসলাম (কনসালটেন্ট)",
            titleEn = "Dr. Ashraful Islam (Medicine Specialist)",
            subCategoryBn = "বিশেষজ্ঞ ডাক্তার",
            subCategoryEn = "Specialist Doctors",
            descriptionBn = "এমবিবিএস, এফসিপিএস (মেডিসিন)। লালমনিরহাট সদর হাসপাতাল। রোগী দেখার সময়: প্রতিদিন বিকেল ৪:০০ থেকে রাত ৮:০০ পর্যন্ত (শুক্রবার বন্ধ)।",
            descriptionEn = "MBBS, FCPS (Internal Medicine). Senior Resident Consultant of Lalmonirhat Sadar Hospital. Chambers visitation from 4:00 PM to 8:00 PM daily.",
            contactNo = "01711223344",
            webUrl = ""
        )
    )
    dao.insertService(
        SmartService(
            category = "HEALTH",
            titleBn = "রেড ক্রিসেন্ট অ্যাম্বুলেন্স সার্ভিস",
            titleEn = "Red Crescent Ambulance Service",
            subCategoryBn = "জরুরি অ্যাম্বুলেন্স",
            subCategoryEn = "Ambulance Contacts",
            descriptionBn = "নিরাপদে ও সঠিক সময়ে রোগী বহনের জন্য লালমনিরহাটে ২৪ ঘণ্টা নিয়োজিত রেড ক্রিসেন্ট সোসাইটির লাইফ সাপোর্ট অ্যাম্বুলেন্স সেবা।",
            descriptionEn = "Call the local Bangladesh Red Crescent Society headquarters for immediate, oxygen-equipped cardiac or general patient transfers round the clock.",
            contactNo = "01711317208",
            webUrl = ""
        )
    )

    // 4. Education Services
    dao.insertService(
        SmartService(
            category = "EDUCATION",
            titleBn = "লালমনিরহাট সরকারি কলেজ",
            titleEn = "Lalmonirhat Government College",
            subCategoryBn = "কলেজ",
            subCategoryEn = "Colleges",
            descriptionBn = "উচ্চ মাধ্যমিক, জাতীয় বিশ্ববিদ্যালয়ের অধীনে স্নাতক পাস, সম্মান এবং স্নাতকোত্তর কোর্সের প্রাচীন বিদ্যাপীঠ।",
            descriptionEn = "Historic educational campus offering Higher Secondary, National University degrees (Honours and Masters) with stellar academic backgrounds.",
            contactNo = "0591-61430",
            webUrl = "http://www.lmgc.edu.bd/",
            imageUrl = "https://images.unsplash.com/photo-1541339907198-e08756dedf3f?auto=format&fit=crop&q=80&w=300"
        )
    )
    dao.insertService(
        SmartService(
            category = "EDUCATION",
            titleBn = "সারাদেশের বোর্ডের পরীক্ষার রেজাল্ট পোর্টাল",
            titleEn = "National Exam Results Portal",
            subCategoryBn = "পরীক্ষার ফলাফল",
            subCategoryEn = "Result Links",
            descriptionBn = "এসএসসি (SSC), এইচএসসি (HSC) এবং সমমানের পরীক্ষার ফলাফল জানতে সরকারি অফিসিয়াল ই-বোর্ড রেজাল্ট পোর্টাল লিংক।",
            descriptionEn = "Direct digital gateway link to retrieve SSC, HSC, and equivalent public board examination marks sheets and certificates instantly.",
            contactNo = "01710203040",
            webUrl = "https://educationboardresults.gov.bd/"
        )
    )

    // 5. Transport Services
    dao.insertService(
        SmartService(
            category = "TRANSPORT",
            titleBn = "লালমনি এক্সপ্রেস (ট্রেন)",
            titleEn = "Lalmoni Express (Intercity Train)",
            subCategoryBn = "রেলপথ সময়সূচী",
            subCategoryEn = "Train Schedules",
            descriptionBn = "লালমনিরহাট থেকে ঢাকা ক্যান্টনমেন্ট স্টেশনগামী আন্তঃনগর বিলাসবহুল ব্রডগেজ ট্রেন। লালমনিরহাট থেকে ছাড়ার সময়: প্রতিদিন ১০:১০ মিনিটে (সাপ্তাহিক বন্ধ: শুক্রবার)।",
            descriptionEn = "Luxury direct intercity train connects Lalmonirhat Junction to Dhaka Cantonment. Departs Lalmonirhat daily at 10:10 PM. Weekly Off: Friday.",
            contactNo = "131",
            webUrl = "https://eticket.railway.gov.bd/"
        )
    )
    dao.insertService(
        SmartService(
            category = "TRANSPORT",
            titleBn = "হানিফ এন্টারপ্রাইজ বাস কাউন্টার",
            titleEn = "Hanif Enterprise Bus Depot",
            subCategoryBn = "সড়ক পরিবহন",
            subCategoryEn = "Bus Stations",
            descriptionBn = "লালমনিরহাট সদর বাস টার্মিনাল কাউন্টার। লালমনিরহাট টু ঢাকা এসি এবং নন-এসি আলিশান যাত্রীবাহী কোচের সিট বুকিং করতে কল করুন।",
            descriptionEn = "Main ticketing counter at Sadar Bus Terminal. Routes spanning Lalmonirhat to Dhaka (AC / Non-AC high deck coaches) with daily schedule.",
            contactNo = "01713049511",
            webUrl = "https://www.shohoz.com/bus-tickets"
        )
    )

    // 6. Emergency Services
    dao.insertService(
        SmartService(
            category = "EMERGENCY",
            titleBn = "জাতীয় হেল্পলাইন ৯৯৯",
            titleEn = "National Helpline 999",
            subCategoryBn = "টোল ফ্রি জরুরি",
            subCategoryEn = "Toll-Free Helpline",
            descriptionBn = "যেকোনো জরুরি পুলিশি সহায়তা, ফায়ার সার্ভিস কিংবা অ্যাম্বুলেন্স সার্ভিসের জন্য সম্পূর্ণ টোল ফ্রি নাম্বার। দেশজুড়ে ২৪ ঘণ্টা কার্যকর।",
            descriptionEn = "A unified emergency toll-free service in Bangladesh linking citizens directly with Police, Fire Brigades, and Ambulance deployments in seconds.",
            contactNo = "999",
            webUrl = ""
        )
    )
    dao.insertService(
        SmartService(
            category = "EMERGENCY",
            titleBn = "লালমনিরহাট ফায়ার সার্ভিস স্টেশন",
            titleEn = "Lalmonirhat Fire & Rescue Station",
            subCategoryBn = "অগ্নিনির্বাপণ ও উদ্ধার",
            subCategoryEn = "Fire & Rescue Stations",
            descriptionBn = "অগ্নিকাণ্ড বা যেকোনো সড়ক ও জল দুর্ঘটনার শিকার হলে উদ্ধারকাজে সার্বক্ষণিক লড়াকু লালমনিরহাট ফায়ার স্টেশন দল।",
            descriptionEn = "Call the official local station controls directly for responsive, heavily equipped expert squads deployed 24/7 across Lalmonirhat.",
            contactNo = "01711234567",
            webUrl = ""
        )
    )
    dao.insertService(
        SmartService(
            category = "EMERGENCY",
            titleBn = "লালমনিরহাট জেলা সদর থানা পুলিশ",
            titleEn = "Lalmonirhat Sadar Police Precinct",
            subCategoryBn = "আইন শৃঙ্খলা",
            subCategoryEn = "Law Enforcement",
            descriptionBn = "লালমনিরহাট সদরের নিরাপত্তার তদারকিতে নিয়োজিত ওয়ান-স্টপ ডিউটি অফিসার এবং ওসি ও ডেস্কে যোগাযোগের অফিশিয়াল হটলাইন নম্বর।",
            descriptionEn = "Direct controls to the Lalmonirhat Sadar Police Precinct Officer-in-Charge. Immediate assistance on security, logs, and civil support.",
            contactNo = "01320135890",
            webUrl = ""
        )
    )

    // 7. Agriculture Services
    dao.insertService(
        SmartService(
            category = "AGRICULTURE",
            titleBn = "কৃষি সম্প্রসারণ অধিদপ্তর লালমনিরহাট",
            titleEn = "Agriculture Extension Dept (DAE)",
            subCategoryBn = "সরকারি কৃষি দপ্তর",
            subCategoryEn = "Public Farm Offices",
            descriptionBn = "লালমনিরহাটের ধান, তামাক ও ভুট্টা চাষীদের জন্য বৈজ্ঞানিক সার প্রয়োগ, রোগবালাই দমন ও সরকারি ফসল ভর্তুকির সঠিক গাইডলাইন সেবা প্রদানকারী কেন্দ্র।",
            descriptionEn = "Provides scientific pest control parameters, soil test briefings, seed subsidies, and agricultural advisory to local farmers.",
            contactNo = "01715566778",
            webUrl = "http://www.dae.lalmonirhat.gov.bd/"
        )
    )
    dao.insertService(
        SmartService(
            category = "AGRICULTURE",
            titleBn = "ভুট্টা ও তামাক চাষাবাদের বৈজ্ঞানিক পরামর্শ",
            titleEn = "Scientific Maize & Tobacco Cultivation Tips",
            subCategoryBn = "চাষাবাদ গাইড",
            subCategoryEn = "Farming Guidelines",
            descriptionBn = "লালমনিরহাটের উর্বর বেলে দো-আঁশ মাটিতে রেকর্ড ফলন পাওয়ার বৈজ্ঞানিক নিয়মাবলী, পানি সেচ চক্র ও মৌসুম অনুযায়ী আধুনিক চাষপদ্ধতি।",
            descriptionEn = "Discover modern farming mechanics, smart soil rotation, organic composting formulas, and water-cycle guidelines matched for Lalmonirhat soil.",
            contactNo = "16123",
            webUrl = "http://www.ais.gov.bd/"
        )
    )

    // 8. Local Business Directory
    dao.insertService(
        SmartService(
            category = "BUSINESS",
            titleBn = "লালমনি চাইনিজ রেস্টুরেন্ট এন্ড পার্টি সেন্টার",
            titleEn = "Lalmoni Chinese Restaurant",
            subCategoryBn = "রেস্তোরাঁ ও ক্যাফে",
            subCategoryEn = "Restaurants & Catering",
            descriptionBn = "লালমনিরহাট সদরে অবস্থিত সুস্বাদু বাংলা, থাই, চাইনিজ এবং ফাস্টফুড ডিশের আধুনিক ফ্যামিলি ক্যাফে লাউঞ্জ। হোম ডেলিভারির জন্য কল করুন।",
            descriptionEn = "A premium family-dining venue offering rich Bengali, Thai-Chinese delicacies, and fast foods. Cozy interior with prompt party catering options.",
            contactNo = "01789456123",
            webUrl = "",
            imageUrl = "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&q=80&w=300"
        )
    )
    dao.insertService(
        SmartService(
            category = "BUSINESS",
            titleBn = "রয়েল ব্রডব্যান্ড লালমনিরহাট",
            titleEn = "Royal Broadband Internet Provider",
            subCategoryBn = "ইন্টারনেট লাইন সেবাদাতা",
            subCategoryEn = "ISP Directory",
            descriptionBn = "লালমনিরহাট পৌর এলাকায় নিরবচ্ছিন্ন ফাইবার অপটিক ইন্টারনেট ও বাফারলেস এফটিটিএইচ হোম নেটওয়ার্ক সেবা প্রদানকারী। চব্বিশ ঘন্টা স্পোর্ট ডেস্ক চালু থাকে।",
            descriptionEn = "Highly responsive premium local ISP provider offering high-speed FTTH home broadband packages, intranet support, and 24/7 technical assistance.",
            contactNo = "01999888777",
            webUrl = "https://www.broadband.gov.bd/"
        )
    )

    // 9. Tourism Section
    dao.insertService(
        SmartService(
            category = "TOURISM",
            titleBn = "তিস্তা ব্যারেজ ও সেচ প্রকল্প",
            titleEn = "Teesta Barrage Irrigation Scheme",
            subCategoryBn = "দর্শনীয় স্থান",
            subCategoryEn = "Exotic Tourist Spots",
            descriptionBn = "হাতীবান্ধা উপজেলায় অবস্থিত বাংলাদেশের বৃহত্তম সেচ ব্যারেজ। তিস্তা নদীর প্রমত্তা বিশালতা এবং দুই পাশের সবুজে ঘেরা বাঁধ দর্শনার্থীদের মূল আকর্ষণ। বিশেষ করে বর্ষাকালে এর অপরূপ রূপ দেখতে হাজারো পর্যটকদের মিলনমেলা বসে।",
            descriptionEn = "The massive landmark irrigation project spanning the Teesta River in Hatibandha. Features beautiful lock-gates, high reservoirs, and amazing boat rides, especially in monsoon.",
            contactNo = "01700112233",
            webUrl = "http://www.lalmonirhat.gov.bd",
            imageUrl = "https://images.unsplash.com/photo-1501785888041-af3ef285b470?auto=format&fit=crop&q=80&w=300",
            locationBn = "গড্ডিমারী, হাতীবান্ধা, লালমনিরহাট",
            locationEn = "Gaddimari, Hatibandha, Lalmonirhat"
        )
    )
    dao.insertService(
        SmartService(
            category = "TOURISM",
            titleBn = "কাকিনা জমিদার বাড়ি ও কাচারি পার্ক",
            titleEn = "Kakina Zamindar Palace",
            subCategoryBn = "ঐতিহাসিক স্থান",
            subCategoryEn = "Historical Heritages",
            descriptionBn = "লালমনিরহাটের কালিগঞ্জ উপজেলার ঐতিহ্যবাহী কাকিনায় অবস্থিত এই ইতিহাসখ্যাত প্রাসাদ। তৎকালীন বিলাসী শাসন ব্যবস্থার বহু কৃতিচিহ্ন, রাজকীয় পুকুর, এবং প্রাচীন স্থাপত্যের এক ধ্বংসস্তূপ নিদর্শন আজও দর্শনার্থীদের আকৃষ্ট করে।",
            descriptionEn = "The ancient palatial estate at Kaliganj. Features rich architectural masonry, high-brick gateway columns, and quiet historic foliage telling tales of centuries-old rulers.",
            contactNo = "01722883344",
            webUrl = "",
            imageUrl = "https://images.unsplash.com/photo-1564507592333-c60657eea523?auto=format&fit=crop&q=80&w=300",
            locationBn = "কাকিনা, কালীগঞ্জ, লালমনিরহাট",
            locationEn = "Kakina, Kaliganj, Lalmonirhat"
        )
    )
    dao.insertService(
        SmartService(
            category = "TOURISM",
            titleBn = "তিন বিঘা করিডোর ও দহগ্রাম ছিটমহল",
            titleEn = "Tin Bigha Corridor (Dahagram)",
            subCategoryBn = "জাতীয় স্থলসীমা",
            subCategoryEn = "National Landmarks",
            descriptionBn = "পাটগ্রাম উপজেলায় ভারতের সীমান্তের কোল ঘেঁষে অবস্থিত স্বাধীন করিডোর। দুই দেশের ঐতিহাসিক ছিটমহল মুক্তির গৌরবময় স্মারক। এখানে প্রতিদিন শত শত মানুষ ঐতিহাসিক সীমান্ত রেখা স্পর্শ করতে আসেন।",
            descriptionEn = "A landmark strip of land linking Dahagram enclave to district mainland in Patgramupazila. Holds immense national historic value and represents sovereign unity.",
            contactNo = "01712398402",
            webUrl = "",
            imageUrl = "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?auto=format&fit=crop&q=80&w=300",
            locationBn = "দহগ্রাম সীমান্ত, পাটগ্রাম, লালমনিরহাট",
            locationEn = "Dahagram Border, Patgram, Lalmonirhat"
        )
    )

    // Prefill default administrator account
    // This allows testing the Admin Panel fully!
    dao.insertUserProfile(
        UserProfile(
            uid = "admin_uid",
            fullName = "Smart Admin (লালমনিরহাট)",
            email = "admin@smart.com",
            phone = "01711223344",
            address = "জেলা সদর, লালমনিরহাট",
            bloodGroup = "O+",
            role = "admin"
        )
    )
    dao.insertUserProfile(
        UserProfile(
            uid = "user_uid",
            fullName = "সজিব মিয়া (Sajib)",
            email = "user@smart.com",
            phone = "01888223344",
            address = "লালমনিরহাট সদর",
            bloodGroup = "A+",
            role = "user"
        )
    )
}
