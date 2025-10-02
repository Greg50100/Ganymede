package com.joviansapps.ganymede.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.joviansapps.ganymede.core.data.local.GanymedeDatabase
import com.joviansapps.ganymede.core.data.local.dao.CalculationDao
import com.joviansapps.ganymede.core.data.repository.CalculationRepositoryImpl
import com.joviansapps.ganymede.core.data.repository.PreferencesRepositoryImpl
import com.joviansapps.ganymede.core.domain.repository.CalculationRepository
import com.joviansapps.ganymede.core.domain.repository.PreferencesRepository
import com.joviansapps.ganymede.crash.CrashReporter
import com.joviansapps.ganymede.crash.FirebaseCrashReporter
import com.joviansapps.ganymede.utils.ErrorManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

// Extension pour DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ganymede_settings")

/**
 * Qualifiers pour différents types de dispatchers
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UnconfinedDispatcher

/**
 * Module Hilt pour la base de données et les DAOs
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideGanymedeDatabase(@ApplicationContext context: Context): GanymedeDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            GanymedeDatabase::class.java,
            GanymedeDatabase.DATABASE_NAME
        )
        .fallbackToDestructiveMigration() // À retirer en production
        .build()
    }

    @Provides
    fun provideCalculationDao(database: GanymedeDatabase): CalculationDao {
        return database.calculationDao()
    }
}

/**
 * Module pour DataStore et les préférences
 */
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }
}

/**
 * Module pour les dispatchers Coroutines
 */
@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {

    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @UnconfinedDispatcher
    fun provideUnconfinedDispatcher(): CoroutineDispatcher = Dispatchers.Unconfined
}

/**
 * Module pour les outils de reporting et gestion d'erreurs
 */
@Module
@InstallIn(SingletonComponent::class)
object ReportingModule {

    @Provides
    @Singleton
    fun provideCrashReporter(@ApplicationContext context: Context): CrashReporter {
        return FirebaseCrashReporter(context)
    }

    @Provides
    @Singleton
    fun provideErrorManager(
        @ApplicationContext context: Context,
        crashReporter: CrashReporter
    ): ErrorManager {
        return ErrorManager(context, crashReporter)
    }
}

/**
 * Module pour les bindings des interfaces vers leurs implémentations
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCalculationRepository(
        calculationRepositoryImpl: CalculationRepositoryImpl
    ): CalculationRepository

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(
        preferencesRepositoryImpl: PreferencesRepositoryImpl
    ): PreferencesRepository
}

/**
 * Module pour les utilitaires et helpers
 */
@Module
@InstallIn(SingletonComponent::class)
object UtilityModule {

    @Provides
    @Singleton
    fun provideAppConfiguration(): AppConfiguration {
        return AppConfiguration()
    }
}

/**
 * Configuration de l'application
 */
data class AppConfiguration(
    val enableDebugLogging: Boolean = true,
    val enableCrashReporting: Boolean = true,
    val enableAnalytics: Boolean = false,
    val databaseVersion: Int = 1,
    val maxHistorySize: Int = 100,
    val autoSaveInterval: Long = 30000L, // 30 secondes
    val enableHapticFeedback: Boolean = true,
    val defaultTheme: String = "auto"
)
