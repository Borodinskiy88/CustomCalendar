package ru.borodinskiy.aleksei.customcalendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import java.time.LocalDate

//Обрезка тени в левой стороне начального значения, и правой стороне конечного значения
private class HalfSizeShape(private val clipStart: Boolean) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val half = size.width / 2f
        val offset = if (layoutDirection == LayoutDirection.Ltr) {
            if (clipStart) Offset(half, 0f) else Offset.Zero
        } else {
            if (clipStart) Offset.Zero else Offset(half, 0f)
        }
        //Между двумя точками прямоугольная рамка
        return Outline.Rectangle(Rect(offset, Size(half, size.height)))
    }
}
fun Modifier.backgroundHighlight(
    day: CalendarDay,
    today: LocalDate,
    selection: DateSelection,
    selectionColor: Color,
    continuousSelectionColor: Color,
    textColor: (Color) -> Unit,
): Modifier = composed {
    val (startDate, endDate) = selection
    val padding = 4.dp
    //day.position - выбранный день
    when (day.position) {
        //Этот месяц, прошедшие дня
        DayPosition.MonthDate -> {
            when {
                //day.date - даты
                day.date.isBefore(today) -> {
                    //Даты прошедших дней
                    textColor(Color.LightGray)
                    this
                }
                //если есть начальная дата и нет конечной
                startDate == day.date && endDate == null -> {
                    textColor(Color.White)
                    padding(padding)
                        //цвет и форма рамки выбранного дня
                        .background(color = selectionColor, shape = RoundedCornerShape(10.dp)
                        )
                }
                //Начальня дата
                day.date == startDate -> {
                    textColor(Color.White)
                    padding(vertical = padding)
                        .background(
                            color = continuousSelectionColor,
                            shape = HalfSizeShape(clipStart = true),
                        )
                        .padding(horizontal = padding)
                        .background(color = selectionColor, shape = RoundedCornerShape(10.dp))
                }
                //Пространство между начальным и конечным днем заполняется прозрачной рамкой
                startDate != null && endDate != null && (day.date > startDate && day.date < endDate) -> {
                    textColor(Color.Black)
                    padding(vertical = padding)
                        .background(color = continuousSelectionColor)
                }
                //Конечная дата
                day.date == endDate -> {
                    textColor(Color.White)
                    padding(vertical = padding)
                        .background(
                            color = continuousSelectionColor,
                            shape = HalfSizeShape(clipStart = false),
                        )
                        .padding(horizontal = padding)
                        .background(color = selectionColor, shape = RoundedCornerShape(10.dp))
                }
                day.date == today -> {
                    //Белый круг фона за сегодняшнее число
                   textColor(Color.Black)
                    padding(10.dp)
                        .background(color = Color.White, shape = RoundedCornerShape(10.dp))

                }
                else -> {
                    textColor(Color.Black)
                    this
                }
            }
        }
        DayPosition.InDate -> {
            //День предыдущего месяца
            textColor(Color.LightGray)
//            if (startDate != null && endDate != null &&
//                ContinuousSelectionHelper.isInDateBetweenSelection(day.date, startDate, endDate)
//            ) {
//                padding(vertical = padding)
//                    .background(color = continuousSelectionColor)
//            }
//            else
                this
        }
        DayPosition.OutDate -> {
            when {
                startDate == day.date && endDate == null -> {
                    textColor(Color.White)
                    padding(padding)
                        .background(color = selectionColor, shape = RoundedCornerShape(10.dp))
                }
                //Начальная дата
                day.date == startDate -> {
                    textColor(Color.White)
                    padding(vertical = padding)
                        .background(
                            color = continuousSelectionColor,
                            shape = HalfSizeShape(clipStart = true),
                        )
                        .padding(horizontal = padding)
                        .background(color = selectionColor, shape = RoundedCornerShape(10.dp))
                }
                startDate != null && endDate != null && (day.date > startDate && day.date < endDate) -> {
                    textColor(Color.Black)
                    padding(vertical = padding)
                        .background(color = continuousSelectionColor)
                }
                //Конечная дата
                day.date == endDate -> {
                    textColor(Color.White)
                    padding(vertical = padding)
                        .background(
                            color = continuousSelectionColor,
                            shape = HalfSizeShape(clipStart = false),
                        )
                        .padding(horizontal = padding)
                        .background(color = selectionColor, shape = RoundedCornerShape(10.dp))
                }
                else -> {
                    //Цвет дат следующего месяца по умолчанию
                    textColor(Color.DarkGray)
                    this
                }
            }
        }
    }
}
