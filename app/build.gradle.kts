plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // Le plugin compose-compiler n'est plus nécessaire, Compose est configuré directement dans composeOptions
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
    // Hilt pour l'injection de dépendances
    alias(libs.plugins.hilt)
    // Utilise KSP déclaré au niveau racine
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.joviansapps.ganymede"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.joviansapps.ganymede"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
        isCoreLibraryDesugaringEnabled = true
    }

    // Configuration Kotlin pour Android - suppression des warnings de dépréciation
    kotlinOptions {
        jvmTarget = "21"
        @Suppress("DEPRECATION")
        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi"
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8" // Version compatible avec Kotlin 1.9.22
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
        disable += listOf("MissingTranslation", "ExtraTranslation")
    }

    packaging {
        resources {
            excludes += listOf(
                "**/desktop.ini",
                "/META-INF/{AL2.0,LGPL2.1}",
                "META-INF/DEPENDENCIES"
            )
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            manifestPlaceholders["crashlyticsCollectionEnabled"] = false
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isCrunchPngs = true
            isDebuggable = false

            manifestPlaceholders["crashlyticsCollectionEnabled"] = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Configuration pour les tests
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Compose BOM pour une gestion cohérente des versions
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.activity.compose)
    implementation(libs.compose.material3)
    implementation(libs.navigation.compose)
    implementation(libs.compose.foundation)
    implementation(libs.androidx.compose.material.icons.extended)

    // Hilt - Injection de dépendances - Migration vers KSP
    implementation("com.google.dagger:hilt-android:2.48")
    ksp("com.google.dagger:hilt-compiler:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Architecture Components
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

    // DataStore Preferences
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Room Database (pour la persistance locale) - Migration vers KSP
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation(libs.firebase.crashlytics)
    implementation("com.google.firebase:firebase-analytics-ktx")

    // Math et utilitaires
    implementation("net.objecthunter:exp4j:0.4.8")

    // UI Components
    implementation("com.google.accompanist:accompanist-pager:0.34.0")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.34.0")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.34.0")

    // Image loading
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // Debug
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Tests - Migration vers KSP
    testImplementation(libs.junit)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    testImplementation("com.google.dagger:hilt-android-testing:2.48")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("io.mockk:mockk:1.13.8")

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.48")

    kspTest("com.google.dagger:hilt-compiler:2.48")
    kspAndroidTest("com.google.dagger:hilt-compiler:2.48")

    // Core library desugaring
    coreLibraryDesugaring(libs.android.desugar.jdk.libs)
}
