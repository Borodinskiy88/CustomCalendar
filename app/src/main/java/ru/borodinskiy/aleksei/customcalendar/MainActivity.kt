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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import ru.borodinskiy.aleksei.customcalendar.ContinuousSelectionHelper.getSelection
import ru.borodinskiy.aleksei.customcalendar.ui.theme.CustomCalendarTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

//Выбранные дни
private val primaryColor = Color.Black.copy(alpha = 0.9f)
private val selectionColor = primaryColor

//Пространство между ними
private val continuousSelectionColor = Color.Black.copy(alpha = 0.3f)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CustomCalendarTheme {
                // Поверхностный контейнер, использующий цвет фона из темы.
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScaffoldSample()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldSample() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Top App Bar") })
        },
        content = { padding ->
            Box(modifier = Modifier.padding(padding)) {
                MainScreen()
            }
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier
                    .fillMaxHeight(0.1f)
                    .background(color = Color.White)
            ) { Text("Bottom App Bar") }
        }
    )
}

@Composable
fun MainScreen(
    dateSelected: (startDate: LocalDate, endDate: LocalDate) -> Unit = { _, _ -> }
) {

    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(24) }
    val endMonth = remember { currentMonth.plusMonths(24) }
    val today = remember { LocalDate.now() }
    var selection by remember { mutableStateOf(DateSelection()) }
    val daysOfWeek = remember { daysOfWeek() }

    //Visible
    var isChooseDate by remember { mutableStateOf(true) }

    Surface(
        color = Color.LightGray
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .background(Color.LightGray),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
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
                    .clip(RoundedCornerShape(20.dp, 20.dp, 20.dp, 20.dp))
                    .background(Color.White)
            ) {

                SimpleCalendarTitle(
                    modifier = Modifier
                        .padding(vertical = 4.dp, horizontal = 8.dp),
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
                        .padding(start = 12.dp, end = 12.dp, bottom = 8.dp)
                        .testTag("Calendar"),
                    state = state,
                    dayContent = { value ->
                        Day(
                            value,
                            today = today,
                            selection = selection,
                        ) { day ->
//                            if (day.position == (DayPosition.MonthDate ) &&
//                                (day.date == today || day.date.isAfter(today))
//                            )
                    //        {
                                isChooseDate = true
                                selection = getSelection(
                                    clickedDate = day.date,
                                    dateSelection = selection,
                                )
                    //        }
                        }
                    },
                    monthHeader = {
                        MonthHeader(daysOfWeek = daysOfWeek)
                    },
                )

                HelpStrings()
            }

            if (isChooseDate) {
                BottomPanel(
                    save = {
                        isChooseDate = false
                    },

                    close = {
                        isChooseDate = false
                    }
                )
            }
        }
    }
}

@Composable
fun BottomPanel(save: () -> Unit, close: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(top = 16.dp)
            .clip(RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp))
            .background(color = Color.White),
        verticalArrangement = Arrangement.Top
    ) {

        Row(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth(0.9f),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = "18 - ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = "22 октября 2023",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                modifier = Modifier
                    .padding(end = 16.dp),
                onClick = close

            ) {
                Icon(
                    painter = painterResource(
                        id = R.drawable.ic_close_24
                    ),
                    contentDescription = null
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.Center
        ) {

            Text(text = "5 дней", fontSize = 12.sp)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = 16.dp)
                .background(color = Color.White),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Menu()

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Button(
                    //Клик по кнопке
                    onClick = save,
                    modifier = Modifier
                        .padding(bottom = 8.dp, start = 12.dp, end = 12.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Сохранить",
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Menu() {
    val options = listOf("Выходной", "Больничный")
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(options[0]) }
// Мы хотим реагировать на нажатие/нажатие TextField, чтобы отобразить меню.
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier
//                .padding(top = 4.dp)
        ) {
            TextField(
                // Модификатор `menuAnchor` должен быть передан в текстовое поле для корректности.
                modifier = Modifier.menuAnchor(),
                readOnly = true,
                value = selectedOptionText,
                onValueChange = {},
                label = { Text("Запросить на смену") },

                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                options.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            selectedOptionText = selectionOption
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
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
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("ru", "RU")),
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun HelpStrings() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
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
                    fontSize = 12.sp
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
                    fontSize = 12.sp
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
                    fontSize = 12.sp
                )
            }
        }
        Row(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 4.dp),
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
                    fontSize = 12.sp
                )
            }
            Row(
                modifier = Modifier
                    .padding(top = 4.dp, start = 16.dp),
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
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun Day(
    day: CalendarDay,
    today: LocalDate,
    selection: DateSelection,
    onClick: (CalendarDay) -> Unit,
) {
    var textColor = Color.Black
    Box(
        modifier = Modifier
            .aspectRatio(1f) // This is important for square-sizing!
            .testTag("MonthDay")
            .padding(1.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                //Цвет выбранный
                color = Color.Yellow
            )
            .clickable(
//                enabled = day.position == DayPosition.MonthDate && day.date >= today,
                enabled = true,
//                showRipple = false,
                onClick = { onClick(day) },
            )
            .backgroundHighlight(
                day = day,
                today = today,
                selection = selection,
                selectionColor = selectionColor,
                continuousSelectionColor = continuousSelectionColor,
            ) { textColor = it },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}
//
//@Composable
//private fun Day(day: CalendarDay, isSelected: Boolean, onClick: (CalendarDay) -> Unit) {
//
//    Box(
//        modifier = Modifier
//            .aspectRatio(1f) // This is important for square-sizing!
//            .testTag("MonthDay")
//            .padding(1.dp)
//            .clip(RoundedCornerShape(10.dp))
//            .background(
//                //Цвет выбранный
//                color = if (isSelected) Color.LightGray else Color.Yellow
//            )
            // Отключить клики по inDates/outDated
//            .clickable(
//                enabled = day.position == DayPosition.MonthDate,
//                onClick = { onClick(day) },
//            ),
//        contentAlignment = Alignment.Center,
//    ) {
//        val textColor = when (day.position) {
//            // Color.Unspecified будет использовать цвет текста по умолчанию из текущей темы
//            DayPosition.MonthDate -> if (isSelected) Color.White else Color.Unspecified
//            DayPosition.InDate, DayPosition.OutDate -> Color.LightGray
//        }
//        Text(
//            text = day.date.dayOfMonth.toString(),
//            color = textColor,
//            fontSize = 16.sp,
//            fontWeight = FontWeight.Bold
//        )
//    }
//}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CustomCalendarTheme {
        ScaffoldSample()
    }
}