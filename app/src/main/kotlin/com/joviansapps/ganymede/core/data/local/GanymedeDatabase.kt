package com.joviansapps.ganymede.core.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import android.content.Context
import com.joviansapps.ganymede.core.data.local.dao.CalculationDao
import com.joviansapps.ganymede.core.data.local.entity.CalculationEntity
import java.util.Date

/**
 * Base de données Room principale pour Ganymede
 */
@Database(
    entities = [CalculationEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GanymedeDatabase : RoomDatabase() {

    abstract fun calculationDao(): CalculationDao

    companion object {
        const val DATABASE_NAME = "ganymede_database"
    }
}

/**
 * Convertisseurs pour Room
 */
class Converters {
    /**
     * Convertit une date (Date) en timestamp (Long) pour stockage dans SQLite
     */
    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    /**
     * Convertit un timestamp (Long) en date (Date) lors de la lecture depuis SQLite
     */
    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }
}
