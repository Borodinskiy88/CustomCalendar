package ru.borodinskiy.aleksei.customcalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.res.stringResource
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
import ru.borodinskiy.aleksei.customcalendar.utils.ContinuousSelectionHelper.getSelection
import ru.borodinskiy.aleksei.customcalendar.enumeration.StatusShift
import ru.borodinskiy.aleksei.customcalendar.ui.theme.CustomCalendarTheme
import ru.borodinskiy.aleksei.customcalendar.ui.theme.headGray
import ru.borodinskiy.aleksei.customcalendar.ui.theme.blueLink
import ru.borodinskiy.aleksei.customcalendar.ui.theme.dark
import ru.borodinskiy.aleksei.customcalendar.ui.theme.darkGrey
import ru.borodinskiy.aleksei.customcalendar.ui.theme.gray
import ru.borodinskiy.aleksei.customcalendar.ui.theme.grayFirstShift
import ru.borodinskiy.aleksei.customcalendar.ui.theme.graySecondShift
import ru.borodinskiy.aleksei.customcalendar.ui.theme.lightGrayBlue
import ru.borodinskiy.aleksei.customcalendar.ui.theme.red
import ru.borodinskiy.aleksei.customcalendar.ui.theme.redShade2
import ru.borodinskiy.aleksei.customcalendar.ui.theme.yellow
import ru.borodinskiy.aleksei.customcalendar.utils.DateSelection
import ru.borodinskiy.aleksei.customcalendar.utils.SimpleCalendarTitle
import ru.borodinskiy.aleksei.customcalendar.utils.backgroundHighlight
import ru.borodinskiy.aleksei.customcalendar.utils.daysCountTitle
import ru.borodinskiy.aleksei.customcalendar.utils.displayText
import ru.borodinskiy.aleksei.customcalendar.utils.noRippleClickable
import ru.borodinskiy.aleksei.customcalendar.utils.reformatDate
import ru.borodinskiy.aleksei.customcalendar.utils.rememberFirstMostVisibleMonth
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

//Выбранные дни, начальный и конечный, цвет бекграунда
private val primaryColor = headGray.copy(alpha = 0.9f)
private var selectionColor = primaryColor

//Пространство между ними, цвет тени
private var continuousSelectionColor = headGray.copy(alpha = 0.3f)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CustomCalendarTheme {
                
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalendarScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    dateSelected: (startDate: LocalDate, endDate: LocalDate) -> Unit = { _, _ -> }
) {

    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(24) }
    val endMonth = remember { currentMonth.plusMonths(24) }
    val today = remember { LocalDate.now() }
    var selection by remember { mutableStateOf(DateSelection()) }
    val daysOfWeek = remember { daysOfWeek(DayOfWeek.MONDAY) }

    var isShowBottomPanel by remember { mutableStateOf(true) }
    //Смена фона
    var setBackground by remember { mutableStateOf(yellow) }

    var statusShift by remember { mutableStateOf(StatusShift.READY_FROM_WORK) }
    //Выбор в меню
    var chooseMenuColor by remember { mutableStateOf(Color.Transparent) }

    var primaryColor by remember { mutableStateOf(primaryColor) }




    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.calendar),
                        color = dark,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        },
        content = { padding ->
            Box(modifier = Modifier.padding(padding)) {
                Surface(
                    color = lightGrayBlue
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                            .background(lightGrayBlue),
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

                        val visibleMonth =
                            rememberFirstMostVisibleMonth(state, viewportPercent = 90f)

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
                                        //смена фона
                                        backgrounds = setBackground,
                                        status = statusShift,
                                        value,
                                        today = today,
                                        selection = selection,
                                    ) { day ->
                                        //Выбирать можно с сегодняшнего дня
                                        if (day.position == (DayPosition.MonthDate) &&
                                            (day.date == today || day.date.isAfter(today)) ||
                                            (day.position == (DayPosition.OutDate))
                                        ) {
                                            isShowBottomPanel = true
                                            selection = getSelection(
                                                clickedDate = day.date,
                                                dateSelection = selection,
                                            )
                                        }
                                    }
                                },
                                monthHeader = {
                                    DayOfWeek(daysOfWeek = daysOfWeek)
                                },
                            )

                            HelpStrings()
                        }

                        if (isShowBottomPanel) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 16.dp)
                                    .clip(RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp))
                                    .background(color = Color.White),
                            ) {

                                Row(
                                    modifier = Modifier
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
                                            text =
                                            if (selection.startDate != null)
                                                reformatDate(selection.startDate.toString())
                                            else "",
                                            color = dark,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            modifier = Modifier.padding(start = 4.dp),
                                            text =
                                            if (selection.endDate != null)
                                                " - " + reformatDate(selection.endDate.toString())
                                            else "",
                                            color = dark,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    IconButton(
                                        modifier = Modifier
                                            .padding(end = 16.dp),
                                        onClick = {
                                            isShowBottomPanel = false
                                            //При крестике стираем выбранные даты
                                            selection = DateSelection()
                                        }

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
                                        .fillMaxWidth(0.9f),
                                    horizontalArrangement = Arrangement.Center,
                                )
                                {
                                    val daysBetween = selection.daysBetween?.plus(1)
                                    val text =
                                        if (daysBetween == null) {
                                            ""
                                        } else {
                                            "$daysBetween ${daysCountTitle(daysBetween)}"
                                        }

                                    Text(text = text, color = dark, fontSize = 12.sp)
                                }

                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(top = 24.dp, start = 12.dp, end = 12.dp)
                                        .background(color = Color.White),
                                    verticalArrangement = Arrangement.SpaceAround,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {

                                    val options = listOf(
                                        stringResource(R.string.first_shift),
                                        stringResource(R.string.second_shift),
                                        stringResource(R.string.day_off_from_work),
                                        stringResource(R.string.ready_for_work),
                                        stringResource(R.string.sick)
                                    )
                                    var expanded by remember { mutableStateOf(false) }
                                    var selectedOptionText by remember { mutableStateOf(options[0]) }

                                    ExposedDropdownMenuBox(
                                        expanded = expanded,
                                        onExpandedChange = {
                                            expanded = !expanded
                                        }
                                    ) {
                                        TextField(
                                            readOnly = true,
                                            value = selectedOptionText,
                                            onValueChange = { },
                                            label = { Text(stringResource(R.string.request_a_shift)) },
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .menuAnchor(),
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(
                                                    expanded = expanded
                                                )
                                            },
                                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                                                focusedTextColor = dark,
                                                unfocusedTextColor = dark,
                                                focusedLabelColor = headGray,
                                                unfocusedLabelColor = headGray,
                                                focusedContainerColor = gray,
                                                unfocusedContainerColor = gray,
                                                focusedBorderColor = Color.Transparent,
                                                unfocusedBorderColor = Color.Transparent,
                                            )
                                        )
                                        ExposedDropdownMenu(
                                            modifier = Modifier.background(color = Color.White),
                                            expanded = expanded,
                                            onDismissRequest = {
                                                expanded = false
                                            }
                                        ) {

                                            DropdownMenuItem(
                                                text = { Text(stringResource(R.string.first_shift)) },
                                                leadingIcon = {
                                                    Icon(
                                                        painter = painterResource(id = R.drawable.ic_circle_6),
                                                        contentDescription = null,
                                                        tint = grayFirstShift
                                                    )
                                                },
                                                onClick = {
                                                    selectedOptionText = options[0]
                                                    chooseMenuColor = grayFirstShift

                                                    statusShift = StatusShift.FIRST_SHIFT
                                                    expanded = false
                                                })

                                            DropdownMenuItem(
                                                text = { Text(stringResource(R.string.second_shift)) },
                                                leadingIcon = {
                                                    Icon(
                                                        painter = painterResource(id = R.drawable.ic_circle_6),
                                                        contentDescription = null,
                                                        tint = graySecondShift
                                                    )
                                                },
                                                onClick = {
                                                    selectedOptionText = options[1]
                                                    chooseMenuColor = graySecondShift

                                                    statusShift = StatusShift.SECOND_SHIFT
                                                    expanded = false
                                                })

                                            DropdownMenuItem(
                                                text = { Text(stringResource(R.string.day_off_from_work)) },
                                                leadingIcon = {
                                                    Icon(
                                                        painter = painterResource(id = R.drawable.ic_circle_6),
                                                        contentDescription = null,
                                                        tint = redShade2
                                                    )
                                                },
                                                onClick = {
                                                    selectedOptionText = options[2]
                                                    chooseMenuColor = redShade2

                                                    statusShift = StatusShift.DAY_OFF
                                                    expanded = false
                                                })

                                            DropdownMenuItem(
                                                text = { Text(stringResource(R.string.ready_for_work)) },
                                                leadingIcon = {
                                                    Icon(
                                                        painter = painterResource(id = R.drawable.ic_circle_6),
                                                        contentDescription = null,
                                                        tint = yellow
                                                    )
                                                },
                                                onClick = {
                                                    selectedOptionText = options[3]
                                                    chooseMenuColor = yellow

                                                    statusShift = StatusShift.READY_FROM_WORK
                                                    expanded = false
                                                })

                                            DropdownMenuItem(
                                                text = { Text(stringResource(R.string.sick)) },
                                                leadingIcon = {
                                                    Icon(
                                                        painter = painterResource(id = R.drawable.ic_circle_6),
                                                        contentDescription = null,
                                                        tint = blueLink
                                                    )
                                                },
                                                onClick = {
                                                    selectedOptionText = options[4]
                                                    chooseMenuColor = blueLink

                                                    statusShift = StatusShift.SICK
                                                    expanded = false
                                                })

                                        }
                                    }

                                    Button(
                                        onClick = {

//                                                CoroutineScope(Dispatchers.IO).launch {
//                                                    controller.saveShift(
//                                                        shift = Shift(
//                                                            end = selection.endDate.toString(),
//                                                            start = selection.startDate.toString(),
//                                                            status = statusShift
//                                                        )
//                                                    )
//                                                }

                                            isShowBottomPanel = false
                                            setBackground = chooseMenuColor

                                            val (startDate, endDate) = selection
                                            if (startDate != null && endDate != null) {
                                                dateSelected(startDate, endDate)
                                            }

                                            selection = DateSelection(
                                                startDate = startDate,
                                                endDate = endDate,
                                            )
                                            selectionColor = chooseMenuColor
                                            continuousSelectionColor = Color.Transparent

                                        },
                                        enabled = selection.startDate != null,
                                        modifier = Modifier
                                            .height(40.dp)
                                            .fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.background,
                                            MaterialTheme.colorScheme.primaryContainer,
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    ) {
                                        //Сбрасываем последний выбор цвета
                                        setBackground = Color.Transparent
                                        selectionColor = primaryColor
                                        continuousSelectionColor = Color.Black.copy(alpha = 0.3f)
//                                            selection = DateSelection()
                                        Text(
                                            text = stringResource(R.string.string),
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun DayOfWeek(daysOfWeek: List<DayOfWeek>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("MonthHeader"),
    ) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier
                    .weight(1f),
                color = when (dayOfWeek) {
                    DayOfWeek.SUNDAY -> red
                    DayOfWeek.SATURDAY -> red
                    else -> darkGrey
                },
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                text = dayOfWeek.displayText(),
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
            horizontalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.padding(end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_circle_6),
                    contentDescription = null,
                    tint = grayFirstShift
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = stringResource(R.string.first_shift),
                    fontSize = 12.sp
                )
            }
            Row(
                modifier = Modifier.padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_circle_6),
                    contentDescription = null,
                    tint = graySecondShift
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = stringResource(R.string.second_shift),
                    fontSize = 12.sp
                )
            }
            Row(
                modifier = Modifier.padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_circle_6),
                    contentDescription = null,
                    tint = redShade2
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = stringResource(R.string.day_off_from_work),
                    fontSize = 12.sp
                )
            }
        }
        Row(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_circle_6),
                    contentDescription = null,
                    tint = yellow
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = stringResource(R.string.ready_for_work),
                    fontSize = 12.sp
                )
            }
            Row(
                modifier = Modifier
                    .padding(top = 4.dp, start = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_circle_6),
                    contentDescription = null,
                    tint = blueLink
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = stringResource(R.string.sick),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun Day(

    backgrounds: Color,
    status: StatusShift,
    day: CalendarDay,
    today: LocalDate,
    selection: DateSelection,
    onClick: (CalendarDay) -> Unit,
) {
    var textColor = darkGrey

    //TODO!!!!!!!!!! Интервал выбранных дней!!!!!
    val selectionDays = selection.startDate != null && selection.endDate != null &&
            day.date > selection.startDate && day.date < selection.endDate


    Box(
        modifier = Modifier
            .aspectRatio(1f) // Форма рамки каждой даты, 1 - квадрат!
            .testTag("MonthDay")
            .padding(2.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(

                if (selectionDays) {
                    when (status) {
                        StatusShift.FIRST_SHIFT -> grayFirstShift
                        StatusShift.SECOND_SHIFT -> graySecondShift
                        StatusShift.DAY_OFF -> redShade2
                        StatusShift.SICK -> blueLink
                        else -> yellow
                    }
                } else yellow

            )
            .noRippleClickable {
                onClick(day)
            }
            .backgroundHighlight(
                day = day,
                today = today,
                selection = selection,
                selectionColor = selectionColor,
                continuousSelectionColor = continuousSelectionColor,
            ) {
                textColor = it

            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            color = textColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CalendarScreen()
}