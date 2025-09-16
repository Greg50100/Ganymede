package com.joviansapps.ganymede.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class DateCalculatorViewModel : ViewModel() {

    data class UiState(
        // Pour le calcul de différence
        val startDate: LocalDate = LocalDate.now(),
        val endDate: LocalDate = LocalDate.now().plusDays(1),
        val differenceResult: String? = null,

        // Pour l'ajout/soustraction
        val addSubtractDate: LocalDate = LocalDate.now(),
        val yearsToAdd: String = "0",
        val monthsToAdd: String = "0",
        val daysToAdd: String = "0",
        val addSubtractResult: LocalDate? = null,

        // Pour le calcul de Pâques
        val easterYear: String = LocalDate.now().year.toString(),
        val easterDateResult: LocalDate? = null
    )

    sealed interface Event {
        data class SetStartDate(val date: LocalDate) : Event
        data class SetEndDate(val date: LocalDate) : Event
        data class SetAddSubtractDate(val date: LocalDate) : Event
        data class SetYears(val years: String) : Event
        data class SetMonths(val months: String) : Event
        data class SetDays(val days: String) : Event
        // Nouvel event pour Pâques
        data class SetEasterYear(val year: String) : Event
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    init {
        calculateDifference()
        calculateAddSubtract()
        calculateEaster()
    }

    fun onEvent(event: Event) {
        _uiState.update {
            when (event) {
                is Event.SetStartDate -> it.copy(startDate = event.date)
                is Event.SetEndDate -> it.copy(endDate = event.date)
                is Event.SetAddSubtractDate -> it.copy(addSubtractDate = event.date)
                is Event.SetYears -> it.copy(yearsToAdd = event.years)
                is Event.SetMonths -> it.copy(monthsToAdd = event.months)
                is Event.SetDays -> it.copy(daysToAdd = event.days)
                is Event.SetEasterYear -> it.copy(easterYear = event.year)
            }
        }
        calculateDifference()
        calculateAddSubtract()
        calculateEaster()
    }

    private fun calculateDifference() {
        viewModelScope.launch {
            val start = _uiState.value.startDate
            val end = _uiState.value.endDate

            val period = java.time.Period.between(start, end)
            val totalDays = ChronoUnit.DAYS.between(start, end)

            val years = period.years
            val months = period.months
            val days = period.days

            val resultText = buildString {
                append("$totalDays total days\n")
                append("Which is: ")
                if (years > 0) append("$years years ")
                if (months > 0) append("$months months ")
                if (days > 0) append("$days days")
            }
            _uiState.update { it.copy(differenceResult = resultText.trim()) }
        }
    }

    private fun calculateAddSubtract() {
        viewModelScope.launch {
            val initialDate = _uiState.value.addSubtractDate
            val years = _uiState.value.yearsToAdd.toLongOrNull() ?: 0
            val months = _uiState.value.monthsToAdd.toLongOrNull() ?: 0
            val days = _uiState.value.daysToAdd.toLongOrNull() ?: 0

            val resultDate = initialDate
                .plusYears(years)
                .plusMonths(months)
                .plusDays(days)

            _uiState.update { it.copy(addSubtractResult = resultDate) }
        }
    }

    // Calcule la date de Pâques pour une année donnée (algorithme grégorien)
    private fun calculateEaster() {
        viewModelScope.launch {
            val year = _uiState.value.easterYear.toIntOrNull()
            if (year == null || year <= 0) {
                _uiState.update { it.copy(easterDateResult = null) }
                return@launch
            }

            val a = year % 19
            val b = year / 100
            val c = year % 100
            val d = b / 4
            val e = b % 4
            val f = (b + 8) / 25
            val g = (b - f + 1) / 3
            val h = (19 * a + b - d - g + 15) % 30
            val i = c / 4
            val k = c % 4
            val l = (32 + 2 * e + 2 * i - h - k) % 7
            val m = (a + 11 * h + 22 * l) / 451
            val month = (h + l - 7 * m + 114) / 31 // 3=March, 4=April
            val day = ((h + l - 7 * m + 114) % 31) + 1

            val easterDate = LocalDate.of(year, month, day)
            _uiState.update { it.copy(easterDateResult = easterDate) }
        }
    }
}
