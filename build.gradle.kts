// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    // Le plugin compose-compiler n'est pas nécessaire au niveau racine, il sera utilisé dans le module app
    alias(libs.plugins.google.gms.google.services) apply false
    alias(libs.plugins.google.firebase.crashlytics) apply false
    // Utilisation des références depuis libs.versions.toml
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
}

// Tâches de nettoyage - correction de buildDir déprécié
tasks.register("clean", Delete::class) {
    delete(layout.buildDirectory)
}
