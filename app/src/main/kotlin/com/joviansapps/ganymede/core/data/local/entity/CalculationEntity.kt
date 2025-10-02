package com.joviansapps.ganymede.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.joviansapps.ganymede.core.domain.model.CalculationType

/**
 * Entité Room pour stocker l'historique des calculs
 */
@Entity(tableName = "calculation_history")
data class CalculationEntity(
    @PrimaryKey
    val id: String,
    val type: String,
    val input: String,
    val result: String,
    val timestamp: Long,
    val isFavorite: Boolean = false
)

/**
 * Extension pour convertir entre domain et entity
 */
fun CalculationEntity.toDomain() = com.joviansapps.ganymede.core.domain.model.CalculationResult(
    id = id,
    type = CalculationType.valueOf(type),
    input = input,
    result = result,
    timestamp = timestamp,
    isFavorite = isFavorite
)

fun com.joviansapps.ganymede.core.domain.model.CalculationResult.toEntity() = CalculationEntity(
    id = id,
    type = type.name,
    input = input,
    result = result,
    timestamp = timestamp,
    isFavorite = isFavorite
)
