package com.joviansapps.ganymede.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Test

class CalculatorViewModelTest {

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
}
