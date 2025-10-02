package com.joviansapps.ganymede.data

/**
 * Actions possibles dans la calculatrice utilisant le pattern sealed class
 * pour une gestion d'état type-safe et exhaustive
 */
sealed class CalculatorAction {

    // === Actions de saisie ===
    /**
     * Saisie d'un chiffre
     */
    data class InputNumber(val number: String) : CalculatorAction()

    /**
     * Saisie d'un opérateur arithmétique
     */
    data class InputOperator(val operator: com.joviansapps.ganymede.data.Operator) : CalculatorAction()

    /**
     * Saisie du point décimal
     */
    data object InputDecimal : CalculatorAction()

    /**
     * Saisie des parenthèses
     */
    data class InputParenthesis(val type: ParenthesisType) : CalculatorAction()

    // === Actions d'évaluation ===
    /**
     * Évaluer l'expression courante
     */
    data object Evaluate : CalculatorAction()

    /**
     * Effacer tout
     */
    data object Clear : CalculatorAction()

    /**
     * Effacer la dernière entrée
     */
    data object ClearEntry : CalculatorAction()

    /**
     * Supprimer le dernier caractère (backspace)
     */
    data object Backspace : CalculatorAction()

    // === Actions de mémoire ===
    /**
     * Stocker en mémoire
     */
    data object MemoryStore : CalculatorAction()

    /**
     * Rappeler la mémoire
     */
    data object MemoryRecall : CalculatorAction()

    /**
     * Ajouter à la mémoire
     */
    data object MemoryAdd : CalculatorAction()

    /**
     * Soustraire de la mémoire
     */
    data object MemorySubtract : CalculatorAction()

    /**
     * Effacer la mémoire
     */
    data object MemoryClear : CalculatorAction()

    // === Actions de fonctions ===
    /**
     * Appliquer une fonction mathématique
     */
    data class ApplyFunction(val function: MathFunction) : CalculatorAction()

    /**
     * Changer le mode d'angle
     */
    data object ToggleAngleMode : CalculatorAction()

    // === Actions d'historique ===
    /**
     * Sélectionner un élément de l'historique
     */
    data class SelectHistoryItem(val item: CalculationHistoryItem) : CalculatorAction()

    /**
     * Effacer l'historique
     */
    data object ClearHistory : CalculatorAction()

    // === Actions de compatibilité avec l'ancien code ===
    /**
     * Actions anciennes pour compatibilité
     */
    data class Number(val value: String) : CalculatorAction()
    data class Operator(val op: String) : CalculatorAction()
    data class Function(val function: String) : CalculatorAction()
    data object Decimal : CalculatorAction()
    data object Delete : CalculatorAction()
    data object DeleteAll : CalculatorAction()
    data object ClearAll : CalculatorAction()
    data object ToggleDegrees : CalculatorAction()
    data object ToggleTrig : CalculatorAction()
    data object MemoryPlus : CalculatorAction()
    data object MemoryMinus : CalculatorAction()
    data object LeftParenthesis : CalculatorAction()
    data object RightParenthesis : CalculatorAction()
    data class SetExpression(val expression: String) : CalculatorAction()
    data class RemoveHistoryItem(val index: Int) : CalculatorAction()
}

/**
 * Opérateurs arithmétiques supportés
 */
enum class Operator(val symbol: String, val precedence: Int, val isRightAssociative: Boolean = false) {
    // Opérateurs binaires
    PLUS("+", 1),
    MINUS("-", 1),
    MULTIPLY("×", 2),
    DIVIDE("÷", 2),
    POWER("^", 3, true),
    MODULO("%", 2),

    // Opérateurs unaires
    NEGATE("±", 4),
    PERCENT("%", 4);

    val isBinary: Boolean get() = this != NEGATE && this != PERCENT
    val isUnary: Boolean get() = !isBinary
}

/**
 * Types de parenthèses
 */
enum class ParenthesisType(val symbol: String) {
    OPEN("("),
    CLOSE(")");

    fun opposite(): ParenthesisType = when (this) {
        OPEN -> CLOSE
        CLOSE -> OPEN
    }
}

/**
 * Fonctions mathématiques disponibles
 */
enum class MathFunction(
    val displayName: String,
    val symbol: String,
    val requiresAngleMode: Boolean = false,
    val category: FunctionCategory = FunctionCategory.BASIC
) {
    // Fonctions trigonométriques
    SIN("Sinus", "sin", true, FunctionCategory.TRIGONOMETRIC),
    COS("Cosinus", "cos", true, FunctionCategory.TRIGONOMETRIC),
    TAN("Tangente", "tan", true, FunctionCategory.TRIGONOMETRIC),
    ASIN("Arc sinus", "asin", true, FunctionCategory.TRIGONOMETRIC),
    ACOS("Arc cosinus", "acos", true, FunctionCategory.TRIGONOMETRIC),
    ATAN("Arc tangente", "atan", true, FunctionCategory.TRIGONOMETRIC),

    // Fonctions hyperboliques
    SINH("Sinus hyperbolique", "sinh", false, FunctionCategory.HYPERBOLIC),
    COSH("Cosinus hyperbolique", "cosh", false, FunctionCategory.HYPERBOLIC),
    TANH("Tangente hyperbolique", "tanh", false, FunctionCategory.HYPERBOLIC),

    // Fonctions logarithmiques et exponentielles
    LOG("Logarithme décimal", "log", false, FunctionCategory.LOGARITHMIC),
    LN("Logarithme naturel", "ln", false, FunctionCategory.LOGARITHMIC),
    EXP("Exponentielle", "exp", false, FunctionCategory.LOGARITHMIC),
    POW10("10^x", "10^", false, FunctionCategory.LOGARITHMIC),

    // Fonctions racines et puissances
    SQRT("Racine carrée", "√", false, FunctionCategory.ALGEBRAIC),
    CBRT("Racine cubique", "∛", false, FunctionCategory.ALGEBRAIC),
    SQUARE("Carré", "x²", false, FunctionCategory.ALGEBRAIC),
    CUBE("Cube", "x³", false, FunctionCategory.ALGEBRAIC),
    RECIPROCAL("Inverse", "1/x", false, FunctionCategory.ALGEBRAIC),

    // Fonctions diverses
    ABS("Valeur absolue", "abs", false, FunctionCategory.BASIC),
    FACTORIAL("Factorielle", "!", false, FunctionCategory.BASIC),
    RANDOM("Nombre aléatoire", "rand", false, FunctionCategory.BASIC);
}

/**
 * Catégories de fonctions pour l'organisation de l'interface
 */
enum class FunctionCategory(val displayName: String) {
    BASIC("De base"),
    TRIGONOMETRIC("Trigonométriques"),
    HYPERBOLIC("Hyperboliques"),
    LOGARITHMIC("Logarithmiques"),
    ALGEBRAIC("Algébriques")
}

/**
 * Constantes mathématiques
 */
enum class MathConstant(val displayName: String, val symbol: String, val value: Double) {
    PI("Pi", "π", Math.PI),
    E("Nombre d'Euler", "e", Math.E),
    PHI("Nombre d'or", "φ", (1 + Math.sqrt(5.0)) / 2),
    SQRT2("Racine de 2", "√2", Math.sqrt(2.0)),
    SQRT3("Racine de 3", "√3", Math.sqrt(3.0))
}
