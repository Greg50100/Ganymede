package com.joviansapps.ganymede.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.joviansapps.ganymede.data.CalculatorAction
import com.joviansapps.ganymede.data.CalculationHistoryItem
import com.joviansapps.ganymede.data.CalculatorState
import com.joviansapps.ganymede.data.OperationStatus
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.function.Function
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.*

// Extension DataStore sur le Context
private val Context.dataStore by preferencesDataStore(name = "calculator_history")

/**
 * ViewModel for the scientific calculator, now with persistent history using DataStore.
 */
class CalculatorViewModel(
    application: Application,
    private val enablePersistence: Boolean = true
) : AndroidViewModel(application) {

    // Constructeur sans argument pour les tests unitaires (persistance désactivée)
    constructor() : this(Application(), enablePersistence = false)

    private val dataStore = if (enablePersistence) application.dataStore else null
    private val HISTORY_KEY = stringPreferencesKey("calculation_history")
    // Utiliser un séparateur peu commun pour stocker la liste dans une seule chaîne
    private val HISTORY_SEPARATOR = "|||"

    private val _uiState = MutableStateFlow(CalculatorState())
    val uiState = _uiState.asStateFlow()

    // État exposé attendu par les tests
    val expr: StateFlow<String> = uiState
        .map { it.expression }
        .stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value.expression)

    val displayText = uiState
        .map { it.result }
        .stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value.result)

    // Alias pour compatibilité tests
    val result: StateFlow<String> = displayText

    // Wrapper compatible UI + tests
    data class HistoryDisplay(private val value: String, val raw: com.joviansapps.ganymede.data.CalculationHistoryItem) : CharSequence {
        fun formatForDisplay(): String = value
        override val length: Int get() = value.length
        override fun get(index: Int): Char = value[index]
        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence = value.subSequence(startIndex, endIndex)
        override fun toString(): String = value
    }

    // L’UI utilisait auparavant des items avec formatForDisplay(); les tests utilisent les fonctions CharSequence
    val history: StateFlow<List<HistoryDisplay>> = uiState
        .map { st -> st.history.map { HistoryDisplay(it.formatForDisplay(), it) } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, _uiState.value.history.map { HistoryDisplay(it.formatForDisplay(), it) })

    private var memoryValue = 0.0
    private val operators = setOf('+', '-', '*', '/', '^')

    enum class FormatMode { PLAIN, THOUSANDS, SCIENTIFIC }
    private val _formatMode = MutableStateFlow(FormatMode.PLAIN)
    val formatMode = _formatMode.asStateFlow()

    // Piles pour undo/redo de l’expression
    private val undoStack: ArrayDeque<String> = ArrayDeque()
    private val redoStack: ArrayDeque<String> = ArrayDeque()

    init {
        // Charger l'historique au démarrage si la persistance est activée
        if (enablePersistence) {
            loadHistory()
        }
    }

    private fun loadHistory() {
        val ds = dataStore ?: return
        viewModelScope.launch {
            val prefs = ds.data.first()
            val savedStrings = prefs[HISTORY_KEY]?.split(HISTORY_SEPARATOR)?.filter { it.isNotEmpty() } ?: emptyList()
            // Convert saved string lines into CalculationHistoryItem instances
            val savedHistoryItems = savedStrings.map { s ->
                val parts = s.split(" = ")
                val expr = parts.getOrNull(0) ?: s
                val res = parts.getOrNull(1) ?: ""
                CalculationHistoryItem(expression = expr, result = res)
            }
            _uiState.update { it.copy(history = savedHistoryItems) }
        }
    }

    private fun saveHistory(history: List<CalculationHistoryItem>) {
        val ds = dataStore ?: return
        viewModelScope.launch {
            ds.edit { prefs ->
                val joined = history.map { it.formatForDisplay() }.joinToString(HISTORY_SEPARATOR)
                prefs[HISTORY_KEY] = joined
            }
        }
    }

    /**
     * Gère les actions de la calculatrice
     */
    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.InputNumber -> inputNumber(action.number)
            is CalculatorAction.InputOperator -> inputOperator(action.operator.symbol)
            is CalculatorAction.ApplyFunction -> applyFunction(action.function.symbol)
            is CalculatorAction.SetExpression -> setExpression(action.expression)
            is CalculatorAction.RemoveHistoryItem -> removeHistoryItem(action.index)
            // Actions compatibles avec l'ancien code
            is CalculatorAction.Number -> inputNumber(action.value)
            is CalculatorAction.Operator -> inputOperator(action.op)
            is CalculatorAction.Function -> applyFunction(action.function)
            CalculatorAction.Decimal -> inputDecimal()
            CalculatorAction.Evaluate -> evaluate()
            CalculatorAction.Delete -> delete()
            CalculatorAction.DeleteAll -> clear()
            CalculatorAction.ClearAll -> clear()
            CalculatorAction.ToggleDegrees -> toggleAngleMode()
            CalculatorAction.ToggleTrig -> toggleAngleMode()
            CalculatorAction.MemoryPlus -> memoryAdd()
            CalculatorAction.MemoryMinus -> memorySubtract()
            CalculatorAction.MemoryRecall -> memoryRecall()
            CalculatorAction.MemoryStore -> memoryStore()
            CalculatorAction.MemoryClear -> memoryClear()
            CalculatorAction.LeftParenthesis -> inputParenthesis("(")
            CalculatorAction.RightParenthesis -> inputParenthesis(")")
            CalculatorAction.ClearHistory -> clearHistory()
            else -> {
                Log.w("CalculatorViewModel", "Action non implémentée: $action")
            }
        }
    }

    // Compatibilité tests: méthode publique
    fun onEvaluate() = evaluate()

    // Compatibilité tests: setter public de l’expression avec gestion undo/redo
    fun setExpression(expression: String) {
        // Empile l’expression actuelle avant changement
        undoStack.addLast(_uiState.value.expression)
        redoStack.clear()
        _uiState.update { it.copy(expression = expression, operationStatus = OperationStatus.Ready) }
    }

    // Expose un changeur de mode de formatage
    fun setFormatMode(mode: FormatMode) { _formatMode.value = mode }

    // Undo/Redo basiques pour tests
    fun undo() {
        if (undoStack.isEmpty()) return
        val current = _uiState.value.expression
        val previous = undoStack.removeLast()
        redoStack.addLast(current)
        _uiState.update { it.copy(expression = previous, operationStatus = OperationStatus.Ready) }
    }

    fun redo() {
        if (redoStack.isEmpty()) return
        val current = _uiState.value.expression
        val next = redoStack.removeLast()
        undoStack.addLast(current)
        _uiState.update { it.copy(expression = next, operationStatus = OperationStatus.Ready) }
    }

    private fun inputNumber(number: String) {
        _uiState.update { state ->
            if (state.justEvaluated) {
                state.copy(expression = number, result = "0", operationStatus = OperationStatus.Ready)
            } else {
                state.copy(expression = state.expression + number)
            }
        }
    }

    private fun inputDecimal() {
        _uiState.update { state ->
            if (state.justEvaluated) {
                state.copy(expression = "0.", result = "0", operationStatus = OperationStatus.Ready)
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

    private fun inputOperator(op: String) {
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
            state.copy(expression = newExpr, operationStatus = OperationStatus.Ready)
        }
    }

    private fun inputParenthesis(paren: String) {
        _uiState.update { state ->
            if (state.justEvaluated) {
                state.copy(expression = paren, result = "0", operationStatus = OperationStatus.Ready)
            } else {
                state.copy(expression = state.expression + paren)
            }
        }
    }

    private fun applyFunction(func: String) {
        _uiState.update { state ->
            val expressionToAppend = when {
                func.endsWith("(") || func.contains("^") -> func
                func in setOf("sin", "cos", "tan", "asin", "acos", "atan") && state.isDegrees -> "${func}_deg("
                else -> "$func("
            }
            if (state.justEvaluated) {
                state.copy(expression = expressionToAppend, result = "0", operationStatus = OperationStatus.Ready)
            } else {
                state.copy(expression = state.expression + expressionToAppend)
            }
        }
    }

    private fun delete() {
        _uiState.update { state ->
            if (state.expression.isNotEmpty()) {
                state.copy(expression = state.expression.dropLast(1), operationStatus = OperationStatus.Ready)
            } else {
                state
            }
        }
    }

    private fun clear() {
        _uiState.update { it.copy(expression = "", result = "0", operationStatus = OperationStatus.Ready) }
    }

    private fun clearAll() {
        val newState = CalculatorState(angleMode = _uiState.value.angleMode, memory = _uiState.value.memory)
        _uiState.update { newState }
        saveHistory(newState.history)
    }

    private fun removeHistoryItem(index: Int) {
        val newHistory = _uiState.value.history.filterIndexed { i, _ -> i != index }
        _uiState.update { it.copy(history = newHistory) }
        saveHistory(newHistory)
    }

    private fun clearHistory() {
        _uiState.update { it.copy(history = emptyList()) }
        saveHistory(emptyList())
    }

    private fun toggleAngleMode() {
        _uiState.update { state ->
            val newAngleMode = state.angleMode.next()
            state.copy(angleMode = newAngleMode)
        }
    }

    private fun memoryAdd() {
        evaluateInternal()?.let {
            val newMemory = _uiState.value.memory.add(it)
            _uiState.update { state -> state.copy(memory = newMemory) }
        }
    }

    private fun memorySubtract() {
        evaluateInternal()?.let {
            val newMemory = _uiState.value.memory.subtract(it)
            _uiState.update { state -> state.copy(memory = newMemory) }
        }
    }

    private fun memoryRecall() {
        val memoryValue = _uiState.value.memory.value
        _uiState.update { state ->
            state.copy(expression = state.expression + formatNumber(memoryValue), operationStatus = OperationStatus.Ready)
        }
    }

    private fun memoryStore() {
        evaluateInternal()?.let { value ->
            val newMemory = _uiState.value.memory.store(value)
            _uiState.update { state -> state.copy(memory = newMemory) }
        }
    }

    private fun memoryClear() {
        val clearedMemory = _uiState.value.memory.clear()
        _uiState.update { state -> state.copy(memory = clearedMemory) }
    }

    private fun evaluate() {
        val state = _uiState.value
        if (state.expression.isBlank()) return

        val expressionToEval = preprocessExpression(state.expression, state.isDegrees)

        try {
            val resultValue = evaluateExpression(expressionToEval, state.isDegrees)
            val formattedResult = formatNumber(resultValue)
            val historyItem = CalculationHistoryItem(
                expression = state.expression,
                result = formattedResult,
                angleMode = state.angleMode
            )
            val newHistory = state.history + historyItem
            _uiState.update {
                it.copy(
                    result = formattedResult,
                    history = newHistory,
                    operationStatus = OperationStatus.JustEvaluated
                )
            }
            saveHistory(newHistory)
        } catch (e: ArithmeticException) {
            _uiState.update { it.copy(result = "Division by zero", operationStatus = OperationStatus.Error) }
        } catch (e: Exception) {
            _uiState.update { it.copy(result = "Error", operationStatus = OperationStatus.Error) }
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
        if (expression.isNotEmpty() && expression.last() in operators) expression = expression.dropLast(1)

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
