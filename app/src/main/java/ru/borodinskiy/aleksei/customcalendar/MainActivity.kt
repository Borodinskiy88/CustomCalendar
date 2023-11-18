package ru.borodinskiy.aleksei.customcalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap.Companion.Square
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import kotlinx.coroutines.launch
import ru.borodinskiy.aleksei.customcalendar.ui.theme.CustomCalendarTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CustomCalendarTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}
//var isChooseDate = false
@Composable
fun MainScreen(adjacentMonths: Long = 500) {
    val today = remember { LocalDate.now() }
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(adjacentMonths) }
    val endMonth = remember { currentMonth.plusMonths(adjacentMonths) }
    val selections = remember { mutableStateListOf<CalendarDay>() }
    val daysOfWeek = remember { daysOfWeek() }

    //Visible
    var isChooseDate = false

    Surface(
        color = Color.Black
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .background(Color.Blue),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            val state = rememberCalendarState(
                startMonth = startMonth,
                endMonth = endMonth,
                firstVisibleMonth = currentMonth,
                firstDayOfWeek = daysOfWeek.first(),
            )
            val coroutineScope = rememberCoroutineScope()

            val visibleMonth = rememberFirstMostVisibleMonth(state, viewportPercent = 90f)

            Column(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(10))
                    .background(Color.Green)
            ) {

                SimpleCalendarTitle(
                    modifier = Modifier
                        .padding(vertical = 10.dp, horizontal = 8.dp),
                    currentMonth = visibleMonth.yearMonth,
                    goToPrevious = {
                        coroutineScope.launch {
                            state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.previousMonth)
                        }
                    },
                    goToNext = {
                        coroutineScope.launch {
                            state.animateScrollToMonth(state.firstVisibleMonth.yearMonth.nextMonth)
                        }
                    },
                )
                HorizontalCalendar(
                    modifier = Modifier
                        .testTag("Calendar"),
                    state = state,
                    dayContent = { day ->
                        Day(day, isSelected = selections.contains(day)) { clicked ->

                            if (selections.contains(clicked)) {
                                selections.remove(clicked)
                            } else {
                                selections.add(clicked)
                            }
                        }
                    },
                    monthHeader = {
                        MonthHeader(daysOfWeek = daysOfWeek)
                    },
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, start = 8.dp, end = 8.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier.padding(end = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_circle_12),
                                contentDescription = null,
                                tint = Color.LightGray
                            )
                            Text(
                                modifier = Modifier.padding(start = 4.dp),
                                text = "1 смена",
                            )
                        }
                        Row(
                            modifier = Modifier.padding(start = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_circle_12),
                                contentDescription = null,
                                tint = Color.DarkGray
                            )
                            Text(
                                modifier = Modifier.padding(start = 4.dp),
                                text = "2 смена",
                            )
                        }
                        Row(
                            modifier = Modifier.padding(start = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_circle_12),
                                contentDescription = null,
                                tint = Color.Red
                            )
                            Text(
                                modifier = Modifier.padding(start = 4.dp),
                                text = "Выходной",
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_circle_12),
                                contentDescription = null,
                                tint = Color.Yellow
                            )
                            Text(
                                modifier = Modifier.padding(start = 4.dp),
                                text = "Готов к подработке",
                            )
                        }
                        Row(
                            modifier = Modifier
                                .padding(top = 16.dp, start = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_circle_12),
                                contentDescription = null,
                                tint = Color.Blue
                            )
                            Text(
                                modifier = Modifier.padding(start = 4.dp),
                                text = "Больничный",
                            )
                        }
                    }
//
                }
            }

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.05f)
                    .background(color = Color.Yellow)
            )

            if (isChooseDate) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .background(color = Color.Green),
                    verticalArrangement = Arrangement.Bottom
                ) {

                    Row {

                    }
                }
            }

        }
    }
}

@Composable
private fun Menu() {

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(color = Color.Black),
        verticalArrangement = Arrangement.Bottom
    ) {

    }
}

@Composable
private fun MonthHeader(daysOfWeek: List<DayOfWeek>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("MonthHeader"),
    ) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun Day(day: CalendarDay, isSelected: Boolean, onClick: (CalendarDay) -> Unit) {

    Box(
        modifier = Modifier
            .aspectRatio(1f) // This is important for square-sizing!
            .testTag("MonthDay")
            .padding(1.dp)
            //Todo форма квадратов
//            .clip(CircleShape)
//            .clickable { isChooseDate =! isChooseDate }
            .clip(RoundedCornerShape(10.dp))
            .background(
                color = if (isSelected) Color.LightGray else Color.Yellow

            )
            // Отключить клики по inDates/outDated
            .clickable(
                enabled = day.position == DayPosition.MonthDate,
                onClick = { onClick(day) },
            ),
        contentAlignment = Alignment.Center,
    ) {
        val textColor = when (day.position) {
            // Color.Unspecified будет использовать цвет текста по умолчанию из текущей темы
            DayPosition.MonthDate -> if (isSelected) Color.White else Color.Unspecified
            DayPosition.InDate, DayPosition.OutDate -> Color.LightGray
        }
        Text(
            text = day.date.dayOfMonth.toString(),
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CustomCalendarTheme {
        MainScreen()
    }
}