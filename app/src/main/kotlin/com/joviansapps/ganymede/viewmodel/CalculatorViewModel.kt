package com.joviansapps.ganymede.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joviansapps.ganymede.data.CalculatorEvent
import com.joviansapps.ganymede.data.CalculatorAction
import com.joviansapps.ganymede.data.CalculatorState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.update
import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.function.Function
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.*

/**
 * ViewModel for the scientific calculator, refactored to use a single state object (MVI pattern).
 */
class CalculatorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorState())
    val uiState = _uiState.asStateFlow()

    // Convenience observable flows used by UI components
    val history = uiState
        .map { it.history }
        .stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value.history)

    val displayText = uiState
        .map { it.result }
        .stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value.result)

    /**
     * Backwards-compatible event handler used by older UI components.
     * Translates CalculatorEvent -> CalculatorAction and delegates to onAction.
     */
    fun onEvent(event: CalculatorEvent) {
        when (event) {
            CalculatorEvent.Decimal -> onAction(CalculatorAction.Decimal)
            CalculatorEvent.Evaluate -> onAction(CalculatorAction.Evaluate)
            CalculatorEvent.Delete -> onAction(CalculatorAction.Delete)
            CalculatorEvent.DeleteAll -> onAction(CalculatorAction.ClearHistory)
            CalculatorEvent.ToggleTrig -> onAction(CalculatorAction.ToggleDegrees)
            is CalculatorEvent.Number -> onAction(CalculatorAction.Number(event.value.toString()))
            is CalculatorEvent.Operator -> onAction(CalculatorAction.Operator(event.op.toString()))
        }
    }

    // Internal state not directly needed by the UI
    private var memoryValue = 0.0
    private val operators = setOf('+', '-', '*', '/', '^')

    // Format options (can be moved to state if UI needs to control it)
    enum class FormatMode { PLAIN, THOUSANDS, SCIENTIFIC }
    // expose as StateFlow so UI can observe current mode
    private val _formatMode = MutableStateFlow(FormatMode.PLAIN)
    val formatMode = _formatMode.asStateFlow()

    fun setFormatMode(mode: FormatMode) {
        _formatMode.value = mode
        // Re-evaluate to apply new format if a result is showing
        if (_uiState.value.justEvaluated) {
            val currentResult = uiState.value.result.toDoubleOrNull()
            if (currentResult != null) {
                _uiState.update { it.copy(result = formatNumber(currentResult)) }
            }
        }
    }

    /**
     * Single entry point for all user actions.
     * Updates the state based on the incoming action.
     */
    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Number -> enterNumber(action.number)
            is CalculatorAction.Operator -> enterOperator(action.operator)
            is CalculatorAction.Function -> enterFunction(action.function)
            is CalculatorAction.SetExpression -> setExpression(action.expression)
            is CalculatorAction.RemoveHistoryItem -> removeHistoryItem(action.index)
            is CalculatorAction.ClearHistory -> clearHistory()
            CalculatorAction.Decimal -> enterDecimal()
            CalculatorAction.Evaluate -> evaluate()
            CalculatorAction.Delete -> delete()
            CalculatorAction.Clear -> clear()
            CalculatorAction.ClearAll -> clearAll()
            CalculatorAction.ToggleDegrees -> toggleDegrees()
            CalculatorAction.MemoryPlus -> memoryPlus()
            CalculatorAction.MemoryMinus -> memoryMinus()
            CalculatorAction.MemoryRecall -> memoryRecall()
            CalculatorAction.MemoryClear -> memoryClear()
            CalculatorAction.LeftParenthesis -> enterParenthesis("(")
            CalculatorAction.RightParenthesis -> enterParenthesis(")")
        }
    }

    private fun enterNumber(number: String) {
        _uiState.update { state ->
            if (state.justEvaluated) {
                state.copy(expression = number, result = "0", justEvaluated = false)
            } else {
                state.copy(expression = state.expression + number)
            }
        }
    }

    private fun enterDecimal() {
        _uiState.update { state ->
            if (state.justEvaluated) {
                state.copy(expression = "0.", result = "0", justEvaluated = false)
            } else {
                val parts = state.expression.split(Regex("[+*/^()-]"))
                val current = parts.lastOrNull() ?: ""
                if (!current.contains('.')) {
                    val newExpr = if (current.isEmpty() || state.expression.lastOrNull() in operators) {
                        state.expression + "0."
                    } else {
                        state.expression + "."
                    }
                    state.copy(expression = newExpr)
                } else {
                    state
                }
            }
        }
    }

    private fun enterOperator(op: String) {
        _uiState.update { state ->
            val newExpr = if (state.justEvaluated) {
                state.result + op
            } else if (state.expression.isNotEmpty() && state.expression.last() in operators) {
                state.expression.dropLast(1) + op
            } else if (state.expression.isEmpty()) {
                state.expression
            } else {
                state.expression + op
            }
            state.copy(expression = newExpr, justEvaluated = false)
        }
    }

    private fun enterParenthesis(paren: String) {
        _uiState.update { state ->
            if (state.justEvaluated) {
                state.copy(expression = paren, result = "0", justEvaluated = false)
            } else {
                state.copy(expression = state.expression + paren)
            }
        }
    }

    private fun enterFunction(func: String) {
        _uiState.update { state ->
            val expressionToAppend = when {
                func.endsWith("(") || func.contains("^") -> func
                func in setOf("sin", "cos", "tan", "asin", "acos", "atan") && state.isDegrees -> "${func}_deg("
                else -> "$func("
            }
            if (state.justEvaluated) {
                state.copy(expression = expressionToAppend, result = "0", justEvaluated = false)
            } else {
                state.copy(expression = state.expression + expressionToAppend)
            }
        }
    }

    private fun delete() {
        _uiState.update { state ->
            if (state.expression.isNotEmpty()) {
                state.copy(expression = state.expression.dropLast(1), justEvaluated = false)
            } else {
                state
            }
        }
    }

    private fun clear() {
        _uiState.update { it.copy(expression = "", result = "0", justEvaluated = false) }
    }

    private fun clearAll() {
        _uiState.update { CalculatorState(isDegrees = it.isDegrees, hasMemory = it.hasMemory) }
    }

    private fun setExpression(expression: String) {
        _uiState.update { it.copy(expression = expression, justEvaluated = false) }
    }

    private fun removeHistoryItem(index: Int) {
        _uiState.update { state ->
            state.copy(history = state.history.filterIndexed { i, _ -> i != index })
        }
    }

    private fun clearHistory() {
        _uiState.update { it.copy(history = emptyList()) }
    }

    private fun toggleDegrees() {
        _uiState.update { it.copy(isDegrees = !it.isDegrees) }
    }

    private fun memoryPlus() {
        evaluateInternal()?.let { memoryValue += it }
        _uiState.update { it.copy(hasMemory = memoryValue != 0.0) }
    }

    private fun memoryMinus() {
        evaluateInternal()?.let { memoryValue -= it }
        _uiState.update { it.copy(hasMemory = memoryValue != 0.0) }
    }

    private fun memoryRecall() {
        _uiState.update { it.copy(expression = it.expression + formatNumber(memoryValue), justEvaluated = false) }
    }

    private fun memoryClear() {
        memoryValue = 0.0
        _uiState.update { it.copy(hasMemory = false) }
    }

    private fun evaluate() {
        val state = _uiState.value
        if (state.expression.isBlank()) return

        val expressionToEval = preprocessExpression(state.expression, state.isDegrees)

        try {
            val resultValue = evaluateExpression(expressionToEval, state.isDegrees)
            val formattedResult = formatNumber(resultValue)
            _uiState.update {
                it.copy(
                    result = formattedResult,
                    history = it.history + "${it.expression} = $formattedResult",
                    justEvaluated = true
                )
            }
        } catch (e: ArithmeticException) {
            _uiState.update { it.copy(result = "Division by zero", justEvaluated = true) }
        } catch (e: Exception) {
            _uiState.update { it.copy(result = "Error", justEvaluated = true) }
        }
    }

    private fun evaluateInternal(): Double? {
        val state = _uiState.value
        if (state.expression.isBlank()) return null
        return try {
            val expressionToEval = preprocessExpression(state.expression, state.isDegrees)
            evaluateExpression(expressionToEval, state.isDegrees)
        } catch (e: Exception) {
            null
        }
    }

    private fun preprocessExpression(expr: String, isDegrees: Boolean): String {
        var expression = expr
        if (expression.last() in operators) expression = expression.dropLast(1)

        val open = expression.count { it == '(' }
        val close = expression.count { it == ')' }
        if (open > close) expression += ")".repeat(open - close)

        expression = expression.replace("√", "sqrt")
            .replace("×", "*")
            .replace("÷", "/")
            .replace("log(", "log10(")

        if (isDegrees) {
            expression = expression
                .replace("sin(", "sin_deg(")
                .replace("cos(", "cos_deg(")
                .replace("tan(", "tan_deg(")
                .replace("asin(", "asin_deg(")
                .replace("acos(", "acos_deg(")
                .replace("atan(", "atan_deg(")
                .replace("_deg_deg(", "_deg(")
        }

        if (expression.contains('%')) {
            var tmp = expression.replace("%", " % ")
            val modRegex = Regex("""([0-9.^)]+)\s*%\s*([0-9.(]+)""")
            while (modRegex.containsMatchIn(tmp)) {
                tmp = tmp.replace(modRegex) { m -> "mod(${m.groupValues[1]},${m.groupValues[2]})" }
            }
            expression = tmp
        }
        return expression
    }

    private fun evaluateExpression(expression: String, isDegrees: Boolean): Double {
        val fact = object: Function("fact", 1) { override fun apply(vararg args: Double) = gamma(args[0] + 1.0) }
        val sinDeg = object: Function("sin_deg", 1) { override fun apply(vararg args: Double) = sin(args[0] * PI / 180.0) }
        val cosDeg = object: Function("cos_deg", 1) { override fun apply(vararg args: Double) = cos(args[0] * PI / 180.0) }
        val tanDeg = object: Function("tan_deg", 1) { override fun apply(vararg args: Double) = tan(args[0] * PI / 180.0) }
        val asinDeg = object: Function("asin_deg", 1) { override fun apply(vararg args: Double) = asin(args[0]) * 180.0 / PI }
        val acosDeg = object: Function("acos_deg", 1) { override fun apply(vararg args: Double) = acos(args[0]) * 180.0 / PI }
        val atanDeg = object: Function("atan_deg", 1) { override fun apply(vararg args: Double) = atan(args[0]) * 180.0 / PI }
        val log10 = object: Function("log10", 1) { override fun apply(vararg args: Double) = log10(args[0]) }
        val ln = object: Function("ln", 1) { override fun apply(vararg args: Double) = ln(args[0]) }
        val modFunc = object: Function("mod", 2) { override fun apply(vararg args: Double) = args[0] % args[1] }
        val sinhF = object: Function("sinh", 1) { override fun apply(vararg a: Double) = sinh(a[0]) }
        val coshF = object: Function("cosh", 1) { override fun apply(vararg a: Double) = cosh(a[0]) }
        val tanhF = object: Function("tanh", 1) { override fun apply(vararg a: Double) = tanh(a[0]) }
        val asinhF = object: Function("asinh", 1) { override fun apply(vararg a: Double) = asinh(a[0]) }
        val acoshF = object: Function("acosh", 1) { override fun apply(vararg a: Double) = acosh(a[0]) }
        val atanhF = object: Function("atanh", 1) { override fun apply(vararg a: Double) = atanh(a[0]) }
        val absF = object: Function("abs", 1) { override fun apply(vararg a: Double) = abs(a[0]) }

        val builder = ExpressionBuilder(expression)
            .functions(fact, log10, ln, modFunc, sinhF, coshF, tanhF, asinhF, acoshF, atanhF, absF)
            .functions(sinDeg, cosDeg, tanDeg, asinDeg, acosDeg, atanDeg)
            .variables("pi", "e")

        val exp = builder.build()
        exp.setVariable("pi", Math.PI)
        exp.setVariable("e", Math.E)

        val value = exp.evaluate()
        if (!value.isFinite()) {
            throw ArithmeticException("Result is not finite")
        }
        return value
    }

    private fun gamma(z: Double): Double {
        val p = doubleArrayOf(676.5203681218851, -1259.1392167224028, 771.32342877765313, -176.61502916214059, 12.507343278686905, -0.13857109526572012, 9.9843695780195716e-6, 1.5056327351493116e-7)
        if (z < 0.5) return PI / (sin(PI * z) * gamma(1 - z))
        var x = 0.99999999999980993
        val a = z - 1.0
        for (i in p.indices) { x += p[i] / (a + i + 1) }
        val t = a + p.size - 0.5
        return sqrt(2 * PI) * t.pow(a + 0.5) * exp(-t) * x
    }

    private fun formatNumber(v: Double): String {
        val rounded = round(v)
        val isNearInt = abs(v - rounded) < 1e-9
        val mode = _formatMode.value

        return when (mode) {
            FormatMode.PLAIN -> {
                if (isNearInt) rounded.toLong().toString() else BigDecimal(v).stripTrailingZeros().toPlainString()
            }
            FormatMode.THOUSANDS -> {
                if (isNearInt) {
                    String.format(Locale.US, "%,d", rounded.toLong())
                } else {
                    (NumberFormat.getInstance(Locale.US) as DecimalFormat).apply {
                        isGroupingUsed = true
                        maximumFractionDigits = 10
                        minimumFractionDigits = 0
                    }.format(v)
                }
            }
            FormatMode.SCIENTIFIC -> DecimalFormat("0.######E0").format(v)
        }
    }
}
