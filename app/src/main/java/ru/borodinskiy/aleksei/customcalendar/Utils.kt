package ru.borodinskiy.aleksei.customcalendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.CalendarLayoutInfo
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.core.CalendarMonth
import kotlinx.coroutines.flow.filterNotNull
import java.time.DayOfWeek
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale



    @Composable
    fun rememberFirstMostVisibleMonth(
        state: CalendarState,
        viewportPercent: Float = 50f,
    ): CalendarMonth {
        val visibleMonth = remember(state) { mutableStateOf(state.firstVisibleMonth) }
        LaunchedEffect(state) {
            snapshotFlow { state.layoutInfo.firstMostVisibleMonth(viewportPercent) }
                .filterNotNull()
                .collect { month -> visibleMonth.value = month }
        }
        return visibleMonth.value
    }

    @Composable
    fun SimpleCalendarTitle(
        modifier: Modifier,
        currentMonth: YearMonth,
        goToPrevious: () -> Unit,
        goToNext: () -> Unit,
    ) {
        Row(
            modifier = modifier.height(40.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CalendarNavigationIcon(
                icon = painterResource(id = R.drawable.ic_arrow_left_24),
                contentDescription = "Previous",
                onClick = goToPrevious,
            )
            Text(
                modifier = Modifier
                    .weight(1f)
                    .testTag("MonthTitle"),
                text = currentMonth.displayText(),
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
            )
            CalendarNavigationIcon(
                icon = painterResource(id = R.drawable.ic_arrow_right_24),
                contentDescription = "Next",
                onClick = goToNext,
            )
        }
    }

    @Composable
    fun CalendarNavigationIcon(
        icon: Painter,
        contentDescription: String,
        onClick: () -> Unit,
    ) = Box(
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .clip(shape = CircleShape)
            .clickable(role = Role.Button, onClick = onClick),
    ) {
        Icon(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .align(Alignment.Center),
            painter = icon,
            contentDescription = contentDescription,
        )
    }

    fun CalendarLayoutInfo.firstMostVisibleMonth(viewportPercent: Float = 50f): CalendarMonth? {
        return if (visibleMonthsInfo.isEmpty()) {
            null
        } else {
            val viewportSize = (viewportEndOffset + viewportStartOffset) * viewportPercent / 100f
            visibleMonthsInfo.firstOrNull { itemInfo ->
                if (itemInfo.offset < 0) {
                    itemInfo.offset + itemInfo.size >= viewportSize
                } else {
                    itemInfo.size - itemInfo.offset >= viewportSize
                }
            }?.month
        }
    }

    fun YearMonth.displayText(short: Boolean = false): String {
        return "${this.month.displayText(short = short)} ${this.year}"
    }

    fun Month.displayText(short: Boolean = true): String {
        val style = if (short) TextStyle.SHORT else TextStyle.FULL
        return getDisplayName(style, Locale.ENGLISH)
    }

    fun DayOfWeek.displayText(uppercase: Boolean = false): String {
        return getDisplayName(TextStyle.SHORT, Locale.ENGLISH).let { value ->
            if (uppercase) value.uppercase(Locale.ENGLISH) else value
        }
    }
