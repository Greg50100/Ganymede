package com.joviansapps.ganymede.data

/**
 * Represents all possible user actions on the calculator screen.
 * This sealed interface is used to pass events from the UI to the ViewModel
 * in a structured way, following MVI patterns.
 */
sealed class CalculatorAction {
    data class Number(val number: String) : CalculatorAction()
    data class Operator(val operator: String) : CalculatorAction()
    data class Function(val function: String) : CalculatorAction()
    object Decimal : CalculatorAction()
    object Evaluate : CalculatorAction()
    object Delete : CalculatorAction()
    object Clear : CalculatorAction()
    object ClearAll : CalculatorAction() // Clear expression and history
    object ToggleDegrees : CalculatorAction()
    object MemoryPlus : CalculatorAction()
    object MemoryMinus : CalculatorAction()
    object MemoryRecall : CalculatorAction()
    object MemoryClear : CalculatorAction()
    object LeftParenthesis : CalculatorAction()
    object RightParenthesis : CalculatorAction()
    data class SetExpression(val expression: String) : CalculatorAction()
    data class RemoveHistoryItem(val index: Int) : CalculatorAction()
    object ClearHistory : CalculatorAction()
}
