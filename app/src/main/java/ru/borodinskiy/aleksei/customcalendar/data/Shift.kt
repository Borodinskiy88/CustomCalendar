package ru.borodinskiy.aleksei.customcalendar.data

import ru.borodinskiy.aleksei.customcalendar.enumeration.StatusShift

data class Shift(
    val end: String,
    val start: String,
    val status: StatusShift
)