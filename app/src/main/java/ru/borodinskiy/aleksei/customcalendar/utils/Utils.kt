package ru.borodinskiy.aleksei.customcalendar.utils

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import com.kizitonwose.calendar.compose.CalendarLayoutInfo
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.core.CalendarMonth
import kotlinx.coroutines.flow.filterNotNull
import java.text.SimpleDateFormat
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
    return "${
        this.month.displayText(short = short).replaceFirstChar { it.uppercase() }
    } ${this.year}"
}

fun Month.displayText(short: Boolean = true): String {
    val style = if (short) TextStyle.SHORT else TextStyle.FULL
    return if (Locale.getDefault() != Locale("ru", "RU")) {
        localeMonth(getDisplayName(style, Locale.getDefault()))
    } else
        localeMonth(getDisplayName(style, Locale("ru", "RU")))
}

fun DayOfWeek.displayText(short: Boolean = true): String {
    val style = if (short) TextStyle.SHORT else TextStyle.FULL
    return if (Locale.getDefault() != Locale("ru", "RU")) {
        (getDisplayName(style, Locale.getDefault()))
    } else
        (getDisplayName(style, Locale("ru", "RU")))
}

@SuppressLint("SimpleDateFormat")
fun reformatDate(date: String): String {
    val dateObj = SimpleDateFormat("yyyy-MM-dd").parse(date)
    return if (Locale.getDefault() != Locale("ru", "RU")) {
        val reformatDate =
            dateObj?.let { SimpleDateFormat("d  MMMM", Locale.getDefault()).format(it) }
        reformatDate.toString()
    } else {
        val reformatDate = dateObj?.let { SimpleDateFormat("d  MMMM", Locale("ru")).format(it) }
        reformatDate.toString()
    }
}

fun localeMonth(month: String): String {
    if (month == "мая") {
        return "май"
    }
    return if (month.last() == 'я') {
        month.removeSuffix("я") + "ь"
    } else
        month.removeSuffix("а")
}

//Отключить мерцание
fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}

fun daysCountTitle(daysBetween: Long): String {
    return if ((daysBetween).toInt() == 11 ||
        (daysBetween).toInt() == 12 ||
        (daysBetween).toInt() == 13 ||
        (daysBetween).toInt() == 14
    ) "дней"
    else if ((daysBetween % 10).toInt() == 1 && (daysBetween).toInt() != 11) "день"
    else if ((daysBetween).toInt() == 1) "день"
    else if ((daysBetween % 10).toInt() == 2 ||
        (daysBetween % 10).toInt() == 3 ||
        (daysBetween % 10).toInt() % 10 == 4
    ) "дня"
    else "дней"
}
