plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
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
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21

        // Active la prise en charge des dernières fonctionnalités Java via desugaring
        isCoreLibraryDesugaringEnabled = true
    }

    buildFeatures {
        compose = true
    }

    composeOptions { }

    lint {
        abortOnError = false
    }

    buildTypes {
        getByName("release") {
            // Remplace `useProguard true`
            isMinifyEnabled = true

            // Crunch PNGs (activé par défaut en release, mais explicité ici)
            isCrunchPngs = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            // En général, la minification est désactivée pour le débogage
            isMinifyEnabled = false
        }
    }
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Compose de base
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.activity.compose)
    implementation(libs.compose.material3)
    implementation(libs.navigation.compose)
    implementation(libs.compose.foundation)
    implementation("net.objecthunter:exp4j:0.4.8")
    // Icônes Material (ArrowBack etc.)
    implementation(libs.androidx.compose.material.icons.extended)

    // DataStore Preferences
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Firebase Crashlytics (use version catalog alias)
    implementation(libs.firebase.crashlytics)

    // Coil for image loading (Compose)
    implementation("io.coil-kt:coil-compose:2.4.0")

    debugImplementation(libs.compose.ui.tooling)

    // Lifecycle ViewModel pour Compose (fournit viewModel() utilisé dans CanvasViewPreview)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

    testImplementation(libs.junit)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // TODO: Envisager l'usage du BOM Compose pour ui/foundation/material3/navigation/activity afin d'éviter le drift de versions.

    // TODO: Mettre à jour DataStore preferences vers 1.1.x
    // implementation("androidx.datastore:datastore-preferences:1.1.1")

    // TODO: Mettre à jour Coil Compose vers 2.7.x
    // implementation("io.coil-kt:coil-compose:2.7.0")

    // TODO: Mettre à jour coroutines-test vers 1.9.x
    // testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")

    // TODO: Optionnel — utiliser le BOM Firebase pour aligner Crashlytics et dépendances associées
    // implementation(platform("com.google.firebase:firebase-bom:<version>"))

    // Ajoute la dépendance nécessaire pour le desugaring
    coreLibraryDesugaring(libs.android.desugar.jdk.libs)
}
