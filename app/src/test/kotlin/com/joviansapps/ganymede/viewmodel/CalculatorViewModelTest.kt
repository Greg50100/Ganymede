package com.joviansapps.ganymede.viewmodel

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class CalculatorViewModelTest {

    @Test
    fun evaluateSimpleExpression_returnsCorrectResult() = runTest {
        val vm = CalculatorViewModel()
        vm.setExpression("2+2")
        vm.onEvaluate()
        // result is a formatted string
        assertEquals("4", vm.result.value)
    }

    @Test
    fun evaluateDivisionByZero_returnsDivisionByZeroMessage() = runTest {
        val vm = CalculatorViewModel()
        vm.setExpression("1/0")
        vm.onEvaluate()
        assertTrue(vm.result.value.contains("Division"))
    }

    @Test
    fun undoRedo_restoresPreviousExpression() = runTest {
        val vm = CalculatorViewModel()
        vm.setExpression("1+2")
        vm.setExpression("1+23")
        // current expression should be "1+23"
        assertEquals("1+23", vm.expr.value)
        vm.undo()
        assertEquals("1+2", vm.expr.value)
        vm.redo()
        assertEquals("1+23", vm.expr.value)
    }

    @Test
    fun historyRecordsEvaluations_andCanBeReused() = runTest {
        val vm = CalculatorViewModel()
        vm.setExpression("3*3")
        vm.onEvaluate()
        // history should contain the evaluated expression
        val hist = vm.history.value
        assertTrue(hist.any { it.startsWith("3*3 =") })
        // reuse left part
        val item = hist.first()
        val left = item.split(" = ").firstOrNull() ?: ""
        vm.setExpression(left)
        assertEquals("3*3", vm.expr.value)
    }

    @Test
    fun formatting_thousands_and_scientific_modes_changeResultPresentation() = runTest {
        val vm = CalculatorViewModel()
        // Plain mode
        vm.setFormatMode(CalculatorViewModel.FormatMode.PLAIN)
        vm.setExpression("1000000")
        vm.onEvaluate()
        val plain = vm.result.value
        // Thousands
        vm.setFormatMode(CalculatorViewModel.FormatMode.THOUSANDS)
        vm.setExpression("1000000")
        vm.onEvaluate()
        val thousands = vm.result.value
        // Scientific
        vm.setFormatMode(CalculatorViewModel.FormatMode.SCIENTIFIC)
        vm.setExpression("1000000")
        vm.onEvaluate()
        val sci = vm.result.value

        assertTrue(plain == "1000000" || plain.contains("1000000"))
        // thousands should contain a grouping separator or differ from plain
        assertTrue(thousands != plain)
        // scientific should contain E or e
        assertTrue(sci.contains("E") || sci.contains("e"))
    }
}


