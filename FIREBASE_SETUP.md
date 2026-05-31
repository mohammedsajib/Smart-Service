# Lalmonirhat Smart Service - Firebase Integration & Firestore Schema Guide

This document contains full database schemas, setup structures, security rules, and build instructions to transition the local Room SQL persistence in **Lalmonirhat Smart Service** directly into **Google Firebase** in production.

---

## Part 1: Firebase Console Setup & Registration

### Step 1: Create a Project
1. Open the [Firebase Console](https://console.firebase.google.com/).
2. Click **Add Project** and name it `Lalmonirhat Smart Service`.
3. Enable or disable Google Analytics depending on telemetry preferences, and hit **Create Project**.

### Step 2: Register Android Platform
1. Inside the console's dashboard, select the **Android** icon.
2. Enter the unique Application ID configured in our `build.gradle.kts`:
   - **Android Package Name:** `com.aistudio.lalmonirhatsmart.fjksp`
3. Hit Register.
4. Download the `google-services.json` file.
5. Place this downloaded file inside your native project folder:
   - Root Path: `app/google-services.json`

### Step 3: Enable SDK Support (Gradle)
Add the following plugins inside your `build.gradle.kts` files:

**Project Level `build.gradle.kts`:**
```kotlin
plugins {
    id("com.google.gms.google-services") version "4.4.1" apply false
}
```

**App/Module Level `app/build.gradle.kts`:**
```kotlin
plugins {
    id("com.google.gms.google-services")
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:34.12.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
}
```

---

## Part 2: Firebase Authentication Setup

1. In the left panel of the Firebase console, go to **Build** -> **Authentication**.
2. Click **Get Started**.
3. Under the **Sign-in method** tab, choose **Email/Password**.
4. Enable the provider and hit Save.

### Kotlin Auth Logic Mapping:
To register or sign-in users with email:

```kotlin
import com.google.firebase.auth.FirebaseAuth

val auth = FirebaseAuth.getInstance()

// Register Citizen
auth.createUserWithEmailAndPassword(email, password)
    .addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val firebaseUser = auth.currentUser
            // Save additional profile data to Firestore
        }
    }

// Log In Citizen
auth.signInWithEmailAndPassword(email, password)
    .addOnCompleteListener { task ->
        if (task.isSuccessful) {
            // Retrieve session and display UI
        }
    }
```

---

## Part 3: Cloud Firestore Database Schema

The database relies on three collection blueprints. This structure matches the Room database entities compiled in our local code.

### 1. Collection: `users`
Each document has a custom ID equal to the authenticated user's `uid`.

```json
{
  "uid": "USER_AUTHENTICATION_UID",
  "fullName": "Sajib Mia",
  "email": "user@smart.com",
  "phone": "01711223344",
  "address": "Lalmonirhat Sadar",
  "bloodGroup": "O+",
  "role": "user"    // Can be "user" or "admin"
}
```

### 2. Collection: `notices`
Auto-generated document IDs.

```json
{
  "id": "AUTO_DOCUMENT_ID",
  "titleBn": "লালমনিরহাটে উৎসবের ক্যাম্পেইন",
  "titleEn": "Festival Campaign in Lalmonirhat",
  "contentBn": "সকল নাগরিককে ট্রাফিক সচেতনতা মানতে অনুরোধ করা হচ্ছে।",
  "contentEn": "All citizens are requested to follow district road safety rules.",
  "dateString": "31 May, 2026",
  "isEmergency": true
}
```

### 3. Collection: `smart_services`
Auto-generated document IDs.

```json
{
  "id": "AUTO_DOCUMENT_ID",
  "category": "HEALTH", // GOVT, HEALTH, EDUCATION, TRANSPORT, EMERGENCY, AGRICULTURE, BUSINESS, TOURISM
  "titleBn": "লালমনিরহাট সদর হাসপাতাল",
  "titleEn": "Lalmonirhat Sadar Hospital",
  "subCategoryBn": "হাসপাতাল",
  "subCategoryEn": "General Hospitals",
  "descriptionBn": "জেলার সর্বপ্রধান চিকিৎসা সেবা কেন্দ্র যেখানে চব্বিশ ঘণ্টা জরুরি বিভাগ সক্রিয় রয়েছে।",
  "descriptionEn": "Primary district general healthcare center featuring 24/7 emergency care.",
  "contactNo": "01713355202",
  "webUrl": "https://dghs.gov.bd",
  "imageUrl": "https://images.unsplash.com/photo-1586773860418-d3b3de97e663",
  "locationBn": "সদর হাসপাতাল রোড, লালমনিরহাট",
  "locationEn": "Sadar Hospital Road, Lalmonirhat",
  "isVerified": true
}
```

### 4. Collection: `favorites`
Track user pins. Each document maps a user ID and a set array of favorited catalog service IDs.

```json
{
  "userId": "USER_AUTHENTICATION_UID",
  "serviceIds": [1, 5, 9] // Array of service IDs favorited
}
```

---

## Part 4: Cloud Firestore Security Rules

Deploy these rules under **Firestore Database** -> **Rules** to safeguard listings from malicious edits while keeping public directory lookups accessible.

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // User profile validation
    match /users/{userId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Public directory helplines & Notices
    match /notices/{noticeId} {
      allow read: if true; // Open to general public
      allow write: if request.auth != null && get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == 'admin';
    }

    match /smart_services/{serviceId} {
      allow read: if true; // Open to general public
      allow write: if request.auth != null && get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == 'admin';
    }

    // Citizen Favorites management
    match /favorites/{favoriteId} {
      allow read, write: if request.auth != null && request.auth.uid == favoriteId;
    }
  }
}
```

---

## Part 5: APK Compilation & Build Instructions

To build a standalone installable release version of the "Lalmonirhat Smart Service" APK to share with testers:

### Method 1: Build Debug APK (Fast Validation)
```bash
gradle assembleDebug
```
- **Output Artifact Location:** `app/build/outputs/apk/debug/app-debug.apk`
- Useful for installing directly on real physical Android devices via USB debugger without code signing keys.

### Method 2: Build Release APK (Play Store ready)
1. Ensure your signing key configurations (`my-upload-key.jks`) are configured in parameters.
2. Execute the compilation task:
```bash
gradle assembleRelease
```
- **Output Artifact Location:** `app/build/outputs/apk/release/app-release.apk`
- The generated APK is ready to sideload or submit to target application repositories.
