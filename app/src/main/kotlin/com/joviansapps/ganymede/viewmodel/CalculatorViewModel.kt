package com.joviansapps.ganymede.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.function.Function
import java.math.BigDecimal
import kotlin.math.*
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

/** ViewModel calculatrice (scientifique légère). */
class CalculatorViewModel : ViewModel() {
    // Etats internes
    private val _expr        = MutableStateFlow("")
    private val _result      = MutableStateFlow("0")
    private val _history     = MutableStateFlow<List<String>>(emptyList())
    private val _displayText = MutableStateFlow("")
    private var memoryValue  = 0.0
    private val _isDegrees   = MutableStateFlow(false) // false = radians, true = degrees
    private var justEvaluated = false

    // Undo/redo stacks
    private val undoStack = ArrayDeque<String>()
    private val redoStack = ArrayDeque<String>()

    // Format options
    enum class FormatMode { PLAIN, THOUSANDS, SCIENTIFIC }
    private val _formatMode = MutableStateFlow(FormatMode.PLAIN)

    // expose format mode
    val formatMode: StateFlow<FormatMode> = _formatMode

    // Exposition
    val expr: StateFlow<String>        = _expr
    val result: StateFlow<String>      = _result
    val history: StateFlow<List<String>> = _history
    val displayText: StateFlow<String> = _displayText
    val isDegrees: StateFlow<Boolean>  = _isDegrees

    private val operators = setOf('+','-','*','/','^')

    // Utilitaires
    private fun refreshDisplayFromExpr() { _displayText.value = _expr.value }
    private fun pushState() { undoStack.addLast(_expr.value); if (undoStack.size > 100) undoStack.removeFirst() ; redoStack.clear() }
    private fun append(s: String) { pushState(); _expr.value += s; refreshDisplayFromExpr() }

    // allow external setExpression (e.g. from history)
    fun setExpression(s: String) { pushState(); _expr.value = s; justEvaluated = false; refreshDisplayFromExpr() }

    fun setFormatMode(mode: FormatMode) { _formatMode.value = mode }

    fun undo() {
        if (undoStack.isEmpty()) return
        redoStack.addLast(_expr.value)
        _expr.value = undoStack.removeLast()
        refreshDisplayFromExpr()
    }

    fun redo() {
        if (redoStack.isEmpty()) return
        undoStack.addLast(_expr.value)
        _expr.value = redoStack.removeLast()
        refreshDisplayFromExpr()
    }

    // Toggle Degrés/Radians
    fun toggleDegrees() { _isDegrees.value = !_isDegrees.value }

    // Saisie basique
    fun onNumber(digit: String) {
        if (justEvaluated) {
            // Commencer une nouvelle expression après évaluation
            _expr.value = ""
            _result.value = "0"
            justEvaluated = false
        }
        append(digit)
    }
    fun onDecimal() {
        if (justEvaluated) {
            _expr.value = ""
            _result.value = "0"
            justEvaluated = false
        }
        val parts = _expr.value.split(Regex("[+*/^()-]"))
        val current = parts.lastOrNull() ?: ""
        if (!current.contains('.')) append(if (current.isEmpty()) "0." else ".")
    }
    fun onOperator(op: String) {
        // Si on sort d'une évaluation, on enchaîne avec le résultat précédent
        if (justEvaluated) {
            val last = _result.value
            // Utiliser le résultat formaté comme base si expression vide
            _expr.value = if (_expr.value.isBlank()) last + op else _expr.value + op
            justEvaluated = false
            refreshDisplayFromExpr()
            return
        }
        if (_expr.value.isEmpty()) return
        val last = _expr.value.last()
        if (last in operators) _expr.value = _expr.value.dropLast(1) + op else _expr.value += op
        refreshDisplayFromExpr()
    }
    fun onLeftParen()  { if (justEvaluated) { _expr.value = ""; _result.value = "0"; justEvaluated = false }; append("(") }
    fun onRightParen() { if (justEvaluated) { _expr.value = ""; _result.value = "0"; justEvaluated = false }; append(")") }

    // Fonctions scientifiques et tokens variés
    fun onFunction(func: String) {
        // Si on sort d'une évaluation, recommencer une nouvelle expression (comme onNumber)
        if (justEvaluated) {
            _expr.value = ""
            _result.value = "0"
            justEvaluated = false
        }
        // Si la chaîne fournie contient déjà une parenthèse ou un opérateur, l'ajouter telle quelle
        if (func.endsWith("(") || func.contains("^") || func.contains("(") || func.contains(")")) {
            append(func)
            return
        }
        // Pour les trigonométriques, insérer la version degrés si besoin
        val trig = setOf("sin","cos","tan","asin","acos","atan")
        if (func in trig) {
            if (_isDegrees.value) {
                append("${func}_deg(")
            } else append("$func(")
            return
        }
        // Fonctions classiques : ajouter '(' pour l'appel
        val classic = setOf("sinh","cosh","tanh","asinh","acosh","atanh","sqrt","log","ln","abs")
        if (func in classic) append("$func(") else append(func)
    }

    // Mémoire
    fun onMemoryPlus()  { evaluateInternal()?.let { memoryValue += it } }
    fun onMemoryMinus() { evaluateInternal()?.let { memoryValue -= it } }
    fun onMemoryRecall(){ append(formatNumber(memoryValue)) }
    fun onMemoryClear() { memoryValue = 0.0 }
    fun hasMemory(): Boolean = memoryValue != 0.0

    // Edition
    fun onClear() { pushState(); _expr.value = ""; _result.value = "0"; justEvaluated = false; refreshDisplayFromExpr() }
    fun onDelete() { if (_expr.value.isNotEmpty()) { pushState(); _expr.value = _expr.value.dropLast(1); justEvaluated = false; refreshDisplayFromExpr() } }

    // Evaluation
    fun onEvaluate() {
        val before = _expr.value
        when(val out = evaluateInternalRaw()) {
            is EvalOutcome.Value -> {
                val r = formatNumber(out.value)
                _result.value = r
                _history.update { it + "$before = $r" }
                _displayText.value = r
                justEvaluated = true
            }
            EvalOutcome.DivisionByZero -> { _result.value = "Division par zéro"; _displayText.value = _result.value; justEvaluated = true }
            EvalOutcome.Error -> { _result.value = "Erreur"; _displayText.value = _result.value; justEvaluated = true }
        }
    }

    fun clearHistory() { _history.value = emptyList() }
    fun removeHistoryItem(index: Int) { _history.update { it.filterIndexed { i, _ -> i != index } } }

    // Event externe (ex: purge total depuis UI historique)
    fun onEvent(event: Any) { if (event is com.joviansapps.ganymede.data.CalculatorEvent.DeleteAll) { onClear(); clearHistory() } }

    // Evaluation interne
    private fun evaluateInternal(): Double? = (evaluateInternalRaw() as? EvalOutcome.Value)?.value

    private sealed interface EvalOutcome { data class Value(val value: Double): EvalOutcome; data object DivisionByZero: EvalOutcome; data object Error: EvalOutcome }

    // Lanczos approximation pour Gamma
    private fun gamma(z: Double): Double {
        val p = doubleArrayOf(
            676.5203681218851,
            -1259.1392167224028,
            771.32342877765313,
            -176.61502916214059,
            12.507343278686905,
            -0.13857109526572012,
            9.9843695780195716e-6,
            1.5056327351493116e-7
        )
        if (z < 0.5) return PI / (sin(PI * z) * gamma(1 - z))
        var x = 0.99999999999980993
        val a = z - 1.0
        for (i in p.indices) {
            x += p[i] / (a + i + 1)
        }
        val t = a + p.size - 0.5
        return sqrt(2 * PI) * t.pow(a + 0.5) * exp(-t) * x
    }

    private fun evaluateInternalRaw(): EvalOutcome {
        var expression = _expr.value
        if (expression.isBlank()) return EvalOutcome.Error
        if (expression.last() in operators) expression = expression.dropLast(1)
        val open = expression.count { it == '(' }
        val close = expression.count { it == ')' }
        if (open > close) expression += ")".repeat(open - close)
        // remplacements pratiques pour que exp4j comprenne nos tokens
        expression = expression.replace("√", "sqrt")
        expression = expression.replace("×", "*")
        expression = expression.replace("÷", "/")
        // considérer 'log(' comme log base 10 pour correspondre à l'UI
        expression = expression.replace("log(", "log10(")

        // Sécuriser : si mode degrés actif, convertir toute occurrence brute de sin(, cos(, tan(, asin(, acos(, atan(
        if (_isDegrees.value) {
            expression = expression
                .replace("sin(", "sin_deg(")
                .replace("cos(", "cos_deg(")
                .replace("tan(", "tan_deg(")
                .replace("asin(", "asin_deg(")
                .replace("acos(", "acos_deg(")
                .replace("atan(", "atan_deg(")
                // éviter double remplacement si déjà _deg(
                .replace("_deg_deg(", "_deg(")
        }

        // Convertit les usages 'a % b' en 'mod(a,b)' pour exp4j
        if (expression.contains('%')) {
            var tmp = expression.replace("%", " % ")
            // élargir la capture du premier opérande pour inclure une chaîne de puissances (ex: 2^5%3)
            val modRegex = Regex("""([0-9.^)]+)\s*%\s*([0-9.(]+)""")
            while (modRegex.containsMatchIn(tmp)) {
                tmp = tmp.replace(modRegex) { m -> "mod(${m.groupValues[1]},${m.groupValues[2]})" }
            }
            expression = tmp
        }

        // Debug: afficher l'expression finale et le mode degrés pour aider les tests
        // println("EVAL_EXPR: '$expression' degrees=${_isDegrees.value}")

        return try {
            // Déclarer fonctions personnalisées
            val fact = object: Function("fact", 1) {
                override fun apply(vararg args: Double): Double {
                    val n = args[0]
                    if (n < 0.0) return Double.NaN
                    return gamma(n + 1.0) // use Gamma(n+1) to support non-integers
                }
            }

            // trig en degrés/système
            val sinDeg = object: Function("sin_deg", 1) { override fun apply(vararg args: Double) = sin(args[0] * PI / 180.0) }
            val cosDeg = object: Function("cos_deg", 1) { override fun apply(vararg args: Double) = cos(args[0] * PI / 180.0) }
            val tanDeg = object: Function("tan_deg", 1) { override fun apply(vararg args: Double) = tan(args[0] * PI / 180.0) }
            val asinDeg = object: Function("asin_deg", 1) { override fun apply(vararg args: Double) = asin(args[0]) * 180.0 / PI }
            val acosDeg = object: Function("acos_deg", 1) { override fun apply(vararg args: Double) = acos(args[0]) * 180.0 / PI }
            val atanDeg = object: Function("atan_deg", 1) { override fun apply(vararg args: Double) = atan(args[0]) * 180.0 / PI }

            val log10 = object: Function("log10", 1) { override fun apply(vararg args: Double) = log10(args[0]) }
            val ln = object: Function("ln", 1) { override fun apply(vararg args: Double) = ln(args[0]) }
            val modFunc = object: Function("mod", 2) { override fun apply(vararg args: Double) = args[0] % args[1] }

            // Fonctions hyperboliques et inverses
            val sinhF = object: Function("sinh", 1) { override fun apply(vararg a: Double) = kotlin.math.sinh(a[0]) }
            val coshF = object: Function("cosh", 1) { override fun apply(vararg a: Double) = kotlin.math.cosh(a[0]) }
            val tanhF = object: Function("tanh", 1) { override fun apply(vararg a: Double) = kotlin.math.tanh(a[0]) }
            val asinhF = object: Function("asinh", 1) { override fun apply(vararg a: Double) = kotlin.math.asinh(a[0]) }
            val acoshF = object: Function("acosh", 1) { override fun apply(vararg a: Double) = kotlin.math.acosh(a[0]) }
            val atanhF = object: Function("atanh", 1) { override fun apply(vararg a: Double) = kotlin.math.atanh(a[0]) }
            val absF = object: Function("abs", 1) { override fun apply(vararg a: Double) = kotlin.math.abs(a[0]) }

            val builder = ExpressionBuilder(expression)
                .function(fact)
                .function(log10)
                .function(ln)
                .function(modFunc)
                .function(sinhF)
                .function(coshF)
                .function(tanhF)
                .function(asinhF)
                .function(acoshF)
                .function(atanhF)
                .function(absF)
                .variables("pi","e")
                .functions(listOf(sinDeg, cosDeg, tanDeg, asinDeg, acosDeg, atanDeg))

            val exp = builder.build()
            exp.setVariable("pi", Math.PI)
            exp.setVariable("e", Math.E)
            val value = exp.evaluate()
            when {
                value.isNaN()      -> EvalOutcome.Error
                value.isInfinite() -> EvalOutcome.DivisionByZero
                else               -> EvalOutcome.Value(value)
            }
        } catch (_: ArithmeticException) {
            EvalOutcome.DivisionByZero
        } catch (_: Exception) {
            EvalOutcome.Error
        }
    }

    // Format numérique sans séparateurs, sans zéros superflus
    private fun formatNumber(v: Double): String {
        if (v.isNaN() || v.isInfinite()) return "Erreur"
        // considérer comme entier si proche d'un entier (corrige erreurs d'approximation comme 120.0000000000002)
        val rounded = kotlin.math.round(v)
        val eps = 1e-6
        val isNearInt = kotlin.math.abs(v - rounded) < eps

        return when (_formatMode.value) {
            FormatMode.PLAIN -> {
                if (isNearInt) rounded.toLong().toString() else BigDecimal(v).stripTrailingZeros().toPlainString()
            }
            FormatMode.THOUSANDS -> {
                // Pour les entiers proches, formater explicitement avec grouping via String.format
                if (isNearInt) {
                    String.format(Locale.US, "%,d", rounded.toLong())
                } else {
                    val df = (NumberFormat.getInstance(Locale.US) as DecimalFormat)
                    df.isGroupingUsed = true
                    df.maximumFractionDigits = 10
                    df.minimumFractionDigits = 0
                    df.format(v)
                }
            }
            FormatMode.SCIENTIFIC -> {
                // Toujours formatter en notation scientifique (même pour nombres entiers)
                val df = DecimalFormat("0.######E0")
                df.format(v)
            }
        }
    }
}