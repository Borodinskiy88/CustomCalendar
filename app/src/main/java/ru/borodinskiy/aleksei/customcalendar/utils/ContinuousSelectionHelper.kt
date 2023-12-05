package ru.borodinskiy.aleksei.customcalendar.utils

import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class DateSelection(val startDate: LocalDate? = null, val endDate: LocalDate? = null) {
    val daysBetween by lazy(LazyThreadSafetyMode.NONE) {
        if (startDate == null || endDate == null) null else {
            ChronoUnit.DAYS.between(startDate, endDate)
        }
    }
}

object ContinuousSelectionHelper {

    fun getSelection(
        clickedDate: LocalDate,
        dateSelection: DateSelection,
    ): DateSelection {

        val (selectionStartDate, selectionEndDate) = dateSelection
        return if (selectionStartDate != null) {
            if (clickedDate < selectionStartDate || selectionEndDate != null) {
                DateSelection(startDate = clickedDate, endDate = null)
            } else if (clickedDate != selectionStartDate) {
                DateSelection(startDate = selectionStartDate, endDate = clickedDate)
            } else {
                DateSelection(startDate = clickedDate, endDate = null)
            }
        } else {
            DateSelection(startDate = clickedDate, endDate = null)
        }
    }
}
