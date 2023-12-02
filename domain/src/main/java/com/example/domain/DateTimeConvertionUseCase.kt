package com.example.domain

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class DateTimeConvertionUseCase @Inject constructor() {

    fun nextDay(fromDate: String) = LocalDate.parse(
        fromDate,
        DateTimeFormatter.ofPattern(LOCAL_DATE_FORMATTER)
    )
        .plusDays(ONE_DAY_IN_MILLIS).format(
            DateTimeFormatter.ofPattern(
                LOCAL_DATE_FORMATTER
            )
        )

    fun previousDay(fromDate: String) = LocalDate.parse(
        fromDate,
        DateTimeFormatter.ofPattern(LOCAL_DATE_FORMATTER)
    )
        .minusDays(ONE_DAY_IN_MILLIS).format(
            DateTimeFormatter.ofPattern(
                LOCAL_DATE_FORMATTER
            )
        )

    fun toRawServerDate(serverDateString: String): LocalDateTime = LocalDateTime.parse(
        serverDateString,
        DateTimeFormatter.ofPattern(SERVER_DATE_TIME_FORMATTER)
    )

    fun toCurrentUiDate(): String = LocalDateTime.now().format(
        DateTimeFormatter.ofPattern(
            LOCAL_DATE_FORMATTER
        )
    )

    fun toCurrentUiDateTime(): String = LocalDateTime.now().format(
        DateTimeFormatter.ofPattern(
            LOCAL_TIME_FORMATTER
        )
    )

    fun toUiDate(date: LocalDateTime): String = date.format(
        DateTimeFormatter.ofPattern(
            LOCAL_DATE_FORMATTER
        )
    )

    fun toUiDateTime(date: LocalDateTime): String = date.format(
        DateTimeFormatter.ofPattern(
            LOCAL_TIME_FORMATTER
        )
    )

    fun toServerDateTime(date: LocalDateTime): String = date.format(
        DateTimeFormatter.ofPattern(
            SERVER_DATE_TIME_FORMATTER
        )
    )

    companion object {
        private const val SERVER_DATE_TIME_FORMATTER = "yyyy-MM-dd HH:mm"
        private const val LOCAL_DATE_FORMATTER = "dd-MM-yyyy"
        private const val LOCAL_TIME_FORMATTER = "HH:mm"
        private const val ONE_DAY_IN_MILLIS = 1L
    }
}