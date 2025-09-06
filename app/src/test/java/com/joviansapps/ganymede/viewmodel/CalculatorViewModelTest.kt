package com.joviansapps.ganymede.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculatorViewModelUnitTest {

    @Test
    fun simple_precedence() {
        val vm = CalculatorViewModel()
        vm.onNumber("2"); vm.onOperator("+"); vm.onNumber("3"); vm.onOperator("*"); vm.onNumber("4")
        vm.onEvaluate()
        assertEquals("14", vm.result.value)
    }

    @Test
    fun parentheses_priority() {
        val vm = CalculatorViewModel()
        vm.onLeftParen(); vm.onNumber("2"); vm.onOperator("+"); vm.onNumber("3"); vm.onRightParen(); vm.onOperator("*"); vm.onNumber("4")
        vm.onEvaluate()
        assertEquals("20", vm.result.value)
    }

    @Test
    fun auto_balance_parentheses() {
        val vm = CalculatorViewModel()
        vm.onLeftParen(); vm.onLeftParen(); vm.onNumber("2"); vm.onOperator("+"); vm.onNumber("3") // missing two )
        vm.onEvaluate()
        assertEquals("5", vm.result.value)
    }

    @Test
    fun function_sin_zero() {
        val vm = CalculatorViewModel()
        vm.onFunction("sin"); vm.onNumber("0") // sin(0
        vm.onEvaluate()
        assertEquals("0", vm.result.value) // formatted zero
    }

    @Test
    fun function_sqrt() {
        val vm = CalculatorViewModel()
        vm.onFunction("sqrt"); vm.onNumber("9")
        vm.onEvaluate()
        assertEquals("3", vm.result.value)
    }

    @Test
    fun division_by_zero() {
        val vm = CalculatorViewModel()
        vm.onNumber("5"); vm.onOperator("/"); vm.onNumber("0")
        vm.onEvaluate()
        assertEquals("Division par zéro", vm.result.value)
    }

    @Test
    fun memory_basic_cycle() {
        val vm = CalculatorViewModel()
        vm.onNumber("5")
        vm.onMemoryPlus() // M+ 5
        vm.onClear()
        vm.onNumber("2")
        vm.onMemoryMinus() // memory = 5 - 2 = 3
        vm.onClear()
        vm.onMemoryRecall() // append 3
        vm.onEvaluate()
        assertEquals("3", vm.result.value)
    }

    @Test
    fun power_operation() {
        val vm = CalculatorViewModel()
        vm.onNumber("2"); vm.onOperator("^"); vm.onNumber("3")
        vm.onEvaluate()
        assertEquals("8", vm.result.value)
    }

    @Test
    fun tan_zero() {
        val vm = CalculatorViewModel()
        vm.onFunction("tan"); vm.onNumber("0")
        vm.onEvaluate()
        assertEquals("0", vm.result.value)
    }

    @Test
    fun log_100() {
        val vm = CalculatorViewModel()
        vm.onFunction("log"); vm.onNumber("1"); vm.onNumber("0"); vm.onNumber("0")
        vm.onEvaluate()
        assertEquals("2", vm.result.value)
    }

    @Test
    fun ln_1() {
        val vm = CalculatorViewModel()
        vm.onFunction("ln"); vm.onNumber("1")
        vm.onEvaluate()
        assertEquals("0", vm.result.value)
    }

    @Test
    fun thousand_no_separator() {
        val vm = CalculatorViewModel()
        vm.onNumber("1"); vm.onNumber("0"); vm.onNumber("0"); vm.onNumber("0"); vm.onOperator("+"); vm.onNumber("1")
        vm.onEvaluate()
        assertEquals("1001", vm.result.value)
    }

    @Test
    fun history_accumulates() {
        val vm = CalculatorViewModel()
        vm.onNumber("1"); vm.onOperator("+"); vm.onNumber("1"); vm.onEvaluate()
        vm.onNumber("2"); vm.onOperator("+"); vm.onNumber("2"); vm.onEvaluate()
        assertEquals(2, vm.history.value.size)
        assertEquals(true, vm.history.value[0].contains("1+1"))
        assertEquals(true, vm.history.value[1].contains("2+2"))
    }

    @Test
    fun undoRedo_restoresPreviousExpression() {
        val vm = CalculatorViewModel()
        vm.setExpression("1+2")
        vm.setExpression("1+23")
        assertEquals("1+23", vm.expr.value)
        vm.undo()
        assertEquals("1+2", vm.expr.value)
        vm.redo()
        assertEquals("1+23", vm.expr.value)
    }

    @Test
    fun formatModes_changePresentation() {
        val vm = CalculatorViewModel()
        // plain
        vm.setFormatMode(CalculatorViewModel.FormatMode.PLAIN)
        vm.setExpression("1000000")
        vm.onEvaluate()
        val plain = vm.result.value

        // thousands
        vm.setFormatMode(CalculatorViewModel.FormatMode.THOUSANDS)
        vm.setExpression("1000000")
        vm.onEvaluate()
        val thousands = vm.result.value

        // scientific
        vm.setFormatMode(CalculatorViewModel.FormatMode.SCIENTIFIC)
        vm.setExpression("1000000")
        vm.onEvaluate()
        val sci = vm.result.value

        // sanity checks
        assertTrue(plain.isNotEmpty())
        // thousands should either differ from plain or contain a grouping separator (',' expected)
        assertTrue(thousands != plain || thousands.contains(","))
        // scientific should contain E or e
        assertTrue(sci.contains("E") || sci.contains("e"))
    }

    @Test
    fun decimal_input_prevents_multiple_decimal_points() {
        val vm = CalculatorViewModel()
        vm.onNumber("1")
        vm.onDecimal()
        vm.onDecimal() // should be ignored
        vm.onNumber("2")
        assertEquals("1.2", vm.expr.value)
    }

    @Test
    fun operator_replacement_behaviour() {
        val vm = CalculatorViewModel()
        vm.onNumber("1")
        vm.onOperator("+")
        vm.onOperator("-") // replace + with -
        assertEquals("1-", vm.expr.value)
    }

    @Test
    fun chain_after_evaluation_uses_result() {
        val vm = CalculatorViewModel()
        vm.setExpression("2+2")
        vm.onEvaluate()
        // now press + and 3 -> should start from result
        vm.onOperator("+")
        vm.onNumber("3")
        vm.onEvaluate()
        assertEquals("7", vm.result.value)
    }

    @Test
    fun trig_functions_in_degrees() {
        val vm = CalculatorViewModel()
        vm.toggleDegrees() // switch to degrees
        vm.onFunction("sin")
        vm.onNumber("90")
        vm.onEvaluate()
        // sin(90°) == 1
        assertTrue(vm.result.value.startsWith("1"))
    }

    @Test
    fun factorial_via_fact_function() {
        val vm = CalculatorViewModel()
        vm.setExpression("fact(5)")
        vm.onEvaluate()
        assertEquals("120", vm.result.value)
    }

    @Test
    fun modulo_percent_operation() {
        val vm = CalculatorViewModel()
        vm.setExpression("10%3")
        vm.onEvaluate()
        assertEquals("1", vm.result.value)
    }

    @Test
    fun delete_and_clear_editing() {
        val vm = CalculatorViewModel()
        vm.setExpression("123")
        vm.onDelete()
        assertEquals("12", vm.expr.value)
        vm.onClear()
        assertEquals("", vm.expr.value)
    }

    @Test
    fun history_clear_and_reuse() {
        val vm = CalculatorViewModel()
        vm.setExpression("2+2"); vm.onEvaluate()
        vm.setExpression("3+3"); vm.onEvaluate()
        assertEquals(2, vm.history.value.size)
        vm.clearHistory()
        assertEquals(0, vm.history.value.size)
    }

    @Test
    fun hyperbolic_functions_zero() {
        val vm = CalculatorViewModel()
        vm.setExpression("sinh(0)"); vm.onEvaluate(); assertEquals("0", vm.result.value)
        vm.setExpression("tanh(0)"); vm.onEvaluate(); assertEquals("0", vm.result.value)
        vm.setExpression("cosh(0)"); vm.onEvaluate(); assertEquals("1", vm.result.value)
    }

    @Test
    fun inverse_hyperbolic_zero() {
        val vm = CalculatorViewModel()
        vm.setExpression("asinh(0)"); vm.onEvaluate(); assertEquals("0", vm.result.value)
        vm.setExpression("atanh(0)"); vm.onEvaluate(); assertEquals("0", vm.result.value)
        // acosh(1) = 0
        vm.setExpression("acosh(1)"); vm.onEvaluate(); assertEquals("0", vm.result.value)
    }

    @Test
    fun absolute_value_negative() {
        val vm = CalculatorViewModel()
        vm.setExpression("abs(-5)")
        vm.onEvaluate()
        assertEquals("5", vm.result.value)
    }

    @Test
    fun constants_pi_e() {
        val vm = CalculatorViewModel()
        vm.setExpression("pi"); vm.onEvaluate(); assertTrue(vm.result.value.startsWith("3.14159"))
        vm.setExpression("e"); vm.onEvaluate(); assertTrue(vm.result.value.startsWith("2.71828"))
    }

    @Test
    fun exponent_e_power() {
        val vm = CalculatorViewModel()
        vm.setExpression("e^2")
        vm.onEvaluate()
        val v = vm.result.value.toDouble()
        assertTrue(kotlin.math.abs(v - 7.389056) < 1e-4)
    }

    @Test
    fun exponent_two_power_ten() {
        val vm = CalculatorViewModel()
        vm.setExpression("2^10")
        vm.onEvaluate()
        assertEquals("1024", vm.result.value)
    }

    @Test
    fun trig_identity_sin2_plus_cos2() {
        val vm = CalculatorViewModel()
        vm.setExpression("sin(0)^2+cos(0)^2")
        vm.onEvaluate()
        assertEquals("1", vm.result.value)
    }

    @Test
    fun degrees_mode_cos_60_and_asin_1() {
        val vm = CalculatorViewModel()
        vm.toggleDegrees() // activate degrees
        vm.onFunction("cos"); vm.onNumber("60"); vm.onEvaluate(); // cos(60°)=0.5
        val cosVal = vm.result.value.replace(",", "").toDouble()
        assertTrue(kotlin.math.abs(cosVal - 0.5) < 1e-6)
        // asin(1)=90°
        vm.setExpression("")
        vm.onFunction("asin"); vm.onNumber("1"); vm.onEvaluate()
        assertTrue(vm.result.value.startsWith("90"))
    }

    @Test
    fun mod_combined_with_power() {
        val vm = CalculatorViewModel()
        vm.setExpression("2^5%3") // 32 % 3 = 2
        vm.onEvaluate()
        assertEquals("2", vm.result.value)
    }

    @Test
    fun nested_parentheses_and_auto_balance_multiple() {
        val vm = CalculatorViewModel()
        vm.setExpression("((2+3") // missing 2 )
        vm.onEvaluate()
        assertEquals("5", vm.result.value)
    }
}
