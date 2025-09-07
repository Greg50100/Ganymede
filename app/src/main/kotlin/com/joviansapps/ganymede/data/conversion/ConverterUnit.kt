package com.joviansapps.ganymede.data.conversion

/**
 * Generic converter unit interface.
 */
interface ConverterUnit<T> {
    val id: String
    val labelRes: Int?
    val label: String?
    val category: UnitCategory

    fun convertFrom(value: T): T
    fun convertTo(value: T): T

    fun convert(outputUnit: ConverterUnit<T>, value: T): T {
        return outputUnit.convertTo(this.convertFrom(value))
    }
}
