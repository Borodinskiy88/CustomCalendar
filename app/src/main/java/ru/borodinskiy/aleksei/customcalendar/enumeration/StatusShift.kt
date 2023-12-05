package ru.borodinskiy.aleksei.customcalendar.enumeration

import androidx.compose.ui.graphics.Color
import ru.borodinskiy.aleksei.customcalendar.ui.theme.blueLink
import ru.borodinskiy.aleksei.customcalendar.ui.theme.grayFirstShift
import ru.borodinskiy.aleksei.customcalendar.ui.theme.graySecondShift
import ru.borodinskiy.aleksei.customcalendar.ui.theme.redShade2
import ru.borodinskiy.aleksei.customcalendar.ui.theme.yellow

enum class StatusShift(color: Color) {
    FIRST_SHIFT(color = grayFirstShift),
    SECOND_SHIFT(color = graySecondShift),
    DAY_OFF(color = redShade2),
    READY_FROM_WORK(color = yellow),
    SICK(color = blueLink)
}