package com.smartkuyo.app.ui.ihai.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartkuyo.app.ui.theme.GoldDark
import java.time.LocalDate
import java.time.chrono.JapaneseChronology
import java.time.chrono.JapaneseDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DeathDateText(
    dateOfDeath: LocalDate,
    modifier: Modifier = Modifier
) {
    val formattedDate = remember(dateOfDeath) {
        formatDeathDate(dateOfDeath)
    }

    // 縦書き表示
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        formattedDate.forEach { char ->
            Text(
                text = char.toString(),
                color = GoldDark,
                fontSize = 16.sp,
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun formatDeathDate(date: LocalDate): String {
    return try {
        // 和暦変換を試みる（明治6年/1873年以降）
        if (date.year >= 1873) {
            val japaneseDate = JapaneseDate.from(date)
            val formatter = DateTimeFormatter.ofPattern("GGGGy年M月d日", Locale.JAPAN)
                .withChronology(JapaneseChronology.INSTANCE)
            formatter.format(japaneseDate)
        } else {
            // 和暦以前は西暦で表示
            "${date.year}年${date.monthValue}月${date.dayOfMonth}日"
        }
    } catch (_: Exception) {
        // フォールバック: 西暦表示
        "${date.year}年${date.monthValue}月${date.dayOfMonth}日"
    }
}
