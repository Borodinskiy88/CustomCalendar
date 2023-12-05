package ru.borodinskiy.aleksei.customcalendar.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import ru.borodinskiy.aleksei.customcalendar.ui.theme.darkGrey
import java.time.LocalDate

fun Modifier.backgroundHighlight(
    day: CalendarDay,
    today: LocalDate,
    selection: DateSelection,
    selectionColor: Color,
    continuousSelectionColor: Color,
    textColor: (Color) -> Unit,
): Modifier = composed {
    val (startDate, endDate) = selection

    when (day.position) {
        DayPosition.MonthDate -> {
            when {

                day.date.isBefore(today) -> {
                    //Цвет прошедших дней этого месяца
                    textColor(darkGrey.copy(alpha = 0.4f))
                    this
                }

                startDate == day.date && endDate == null -> {
                    textColor(darkGrey)
                    padding()
                        .background(color = selectionColor, shape = RoundedCornerShape(10.dp)
                        )
                }
                day.date == startDate -> {
                    textColor(darkGrey)
                    padding()
                        .background(
                            color = continuousSelectionColor,
                        )
                        .padding()
                        .background(color = selectionColor, shape = RoundedCornerShape(10.dp))
                }
                //Пространство между начальным и конечным днем заполняется прозрачной рамкой
                startDate != null && endDate != null && (day.date > startDate && day.date < endDate) -> {
                    textColor(Color.Black)
                    padding()
                        .background(color = continuousSelectionColor)
                }
                day.date == endDate -> {
                    textColor(darkGrey)
                    padding()
                        .background(
                            color = continuousSelectionColor,
                        )
                        .background(color = selectionColor, shape = RoundedCornerShape(10.dp))
                }
                day.date == today -> {
                    //Белый квадрат фона за сегодняшнее число
                    textColor(darkGrey)
                    padding(10.dp)
                        .background(color = Color.White, shape = RoundedCornerShape(4.dp))

                }
                else -> {
                    textColor(darkGrey)
                    this
                }
            }
        }
        //Предыдущий месяц
        DayPosition.InDate -> {
            when {
                day.date == startDate -> {
                    textColor(darkGrey)
                    padding()
                        .background(
                            color = continuousSelectionColor,
                        )
                        .padding()
                        .background(color = selectionColor, shape = RoundedCornerShape(10.dp))
                }
                startDate != null && endDate != null && (day.date > startDate && day.date < endDate) -> {
                    textColor(Color.Black)
                    padding()
                        .background(color = continuousSelectionColor)
                }
                day.date == endDate -> {
                    textColor(darkGrey)
                    padding()
                        .background(
                            color = continuousSelectionColor,
                        )
                        .background(color = selectionColor, shape = RoundedCornerShape(10.dp))
                }
                else -> {
                    //Цвет дат предыдущего месяца по умолчанию
                    textColor(darkGrey.copy(alpha = 0.4f))
                    this
                }
            }
        }
        //Следующий месяц
        DayPosition.OutDate -> {
            when {
                startDate == day.date && endDate == null -> {
                    textColor(darkGrey)
                    padding()
                        .background(color = selectionColor, shape = RoundedCornerShape(10.dp))
                }
                day.date == startDate -> {
                    textColor(darkGrey)
                    padding()
                        .background(
                            color = continuousSelectionColor,
                        )
                        .background(color = selectionColor, shape = RoundedCornerShape(10.dp))
                }
                startDate != null && endDate != null && (day.date > startDate && day.date < endDate) -> {
                    textColor(Color.Black)
                    padding()
                        .background(color = continuousSelectionColor)
                }
                day.date == endDate -> {
                    textColor(darkGrey)
                    padding()
                        .background(
                            color = continuousSelectionColor,
                        )
                        .background(color = selectionColor, shape = RoundedCornerShape(10.dp))
                }
                else -> {
                    //Цвет дат следующего месяца по умолчанию
                    textColor(darkGrey.copy(alpha = 0.7f))
                    this
                }
            }
        }
    }
}