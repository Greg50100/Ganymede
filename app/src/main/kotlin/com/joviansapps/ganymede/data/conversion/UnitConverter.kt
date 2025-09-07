package com.joviansapps.ganymede.data.conversion

interface UnitConverter<T> {
    val units: List<ConverterUnit<T>>

    fun convertAll(value: T, unit: ConverterUnit<T>): List<Pair<ConverterUnit<T>, T>> {
        return units.map { it to unit.convert(it, value) }
    }
}

