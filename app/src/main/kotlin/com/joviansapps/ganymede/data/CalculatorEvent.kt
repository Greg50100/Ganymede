package com.joviansapps.ganymede.data

sealed interface CalculatorEvent {
    object Decimal      : CalculatorEvent
    object Evaluate     : CalculatorEvent
    object Delete       : CalculatorEvent
    object DeleteAll    : CalculatorEvent
    object ToggleTrig   : CalculatorEvent
    data class Number(val value: Int) : CalculatorEvent
    data class Operator(val op: Char) : CalculatorEvent // '+', '-', '*', '/'
}