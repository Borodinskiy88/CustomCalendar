package ru.borodinskiy.aleksei.customcalendar

import androidx.compose.ui.graphics.Color

enum class BackgroundDays(val shift: String, val color: Color) {
    FIRST_SHIFT("1 смена", Color.LightGray),
    SECOND_SHIFT("2 смена", Color.DarkGray),
    DAY_OFF_SHIFT("Выходной", Color.Red),
    READY_TO_WORK("Готов к подработке", Color.Yellow),
    SICK_LEAVE("Больничный", Color.Blue)
}