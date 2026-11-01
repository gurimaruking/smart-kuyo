package com.smartkuyo.app.domain.model

import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class MemorialDate(
    val name: String,
    val date: LocalDate,
    val type: MemorialType,
    val daysFromNow: Long
) {
    val isPast: Boolean get() = daysFromNow < 0
    val isToday: Boolean get() = daysFromNow == 0L
    val isFuture: Boolean get() = daysFromNow > 0
}

enum class MemorialType(val displayNameJa: String) {
    SHONANOKA("初七日"),
    FUTANANOKA("二七日"),
    MINANOKA("三七日"),
    YONANOKA("四七日"),
    ITSUNANOKA("五七日"),
    MUNANOKA("六七日"),
    SHIJUKUNICHI("四十九日"),
    HYAKKANNICHI("百箇日"),
    TSUKIMEINICHI("月命日"),
    MEINICHI("命日"),
    ISSHUUKI("一周忌"),
    SANSHUUKI("三回忌"),
    NANASHUUKI("七回忌"),
    JUUSANSHUUKI("十三回忌"),
    JUUNANA_SHUUKI("十七回忌"),
    NIJUUSAN_SHUUKI("二十三回忌"),
    NIJUUNANA_SHUUKI("二十七回忌"),
    SANJUUSAN_SHUUKI("三十三回忌"),
    GOJUU_SHUUKI("五十回忌")
}

object MemorialDateCalculator {

    fun calculateAll(dateOfDeath: LocalDate): List<MemorialDate> {
        val today = LocalDate.now()
        val results = mutableListOf<MemorialDate>()

        // 初七日〜四十九日
        val chuinDays = listOf(
            7 to MemorialType.SHONANOKA,
            14 to MemorialType.FUTANANOKA,
            21 to MemorialType.MINANOKA,
            28 to MemorialType.YONANOKA,
            35 to MemorialType.ITSUNANOKA,
            42 to MemorialType.MUNANOKA,
            49 to MemorialType.SHIJUKUNICHI
        )
        chuinDays.forEach { (days, type) ->
            val date = dateOfDeath.plusDays(days.toLong())
            results.add(MemorialDate(
                name = type.displayNameJa,
                date = date,
                type = type,
                daysFromNow = ChronoUnit.DAYS.between(today, date)
            ))
        }

        // 百箇日
        val hyakkannichi = dateOfDeath.plusDays(100)
        results.add(MemorialDate(
            name = MemorialType.HYAKKANNICHI.displayNameJa,
            date = hyakkannichi,
            type = MemorialType.HYAKKANNICHI,
            daysFromNow = ChronoUnit.DAYS.between(today, hyakkannichi)
        ))

        // 年忌法要
        val annualMemorials = listOf(
            1 to MemorialType.ISSHUUKI,
            2 to MemorialType.SANSHUUKI, // 三回忌は満2年
            6 to MemorialType.NANASHUUKI,
            12 to MemorialType.JUUSANSHUUKI,
            16 to MemorialType.JUUNANA_SHUUKI,
            22 to MemorialType.NIJUUSAN_SHUUKI,
            26 to MemorialType.NIJUUNANA_SHUUKI,
            32 to MemorialType.SANJUUSAN_SHUUKI,
            49 to MemorialType.GOJUU_SHUUKI
        )
        annualMemorials.forEach { (years, type) ->
            val date = dateOfDeath.plusYears(years.toLong())
            results.add(MemorialDate(
                name = type.displayNameJa,
                date = date,
                type = type,
                daysFromNow = ChronoUnit.DAYS.between(today, date)
            ))
        }

        // 月命日（今後12ヶ月分）
        for (i in 1..12) {
            val monthlyDate = calculateMonthlyMemorial(dateOfDeath, today, i)
            if (monthlyDate != null) {
                results.add(MemorialDate(
                    name = "${MemorialType.TSUKIMEINICHI.displayNameJa}（${monthlyDate.monthValue}月）",
                    date = monthlyDate,
                    type = MemorialType.TSUKIMEINICHI,
                    daysFromNow = ChronoUnit.DAYS.between(today, monthlyDate)
                ))
            }
        }

        return results.sortedBy { it.date }
    }

    fun getNextMemorial(dateOfDeath: LocalDate): MemorialDate? {
        return calculateAll(dateOfDeath)
            .filter { it.daysFromNow >= 0 }
            .minByOrNull { it.daysFromNow }
    }

    fun getDaysSinceDeath(dateOfDeath: LocalDate): Long {
        return ChronoUnit.DAYS.between(dateOfDeath, LocalDate.now())
    }

    private fun calculateMonthlyMemorial(
        dateOfDeath: LocalDate,
        today: LocalDate,
        monthsAhead: Int
    ): LocalDate? {
        val dayOfDeath = dateOfDeath.dayOfMonth
        val targetMonth = today.plusMonths(monthsAhead.toLong())
        val maxDay = targetMonth.lengthOfMonth()
        val day = dayOfDeath.coerceAtMost(maxDay)
        val date = targetMonth.withDayOfMonth(day)
        // 命日と同じ月はスキップ（年忌で対応）
        return if (date.month == dateOfDeath.month && date.year != today.year) null else date
    }
}
