package com.example.domain

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class DateTimeUseCase @Inject constructor() {

    fun fromServerToDateUi(dateTimeUiServer: String): String {
        val rawServerDate = serverStringToServerDate(dateTimeUiServer)
        return toUiDate(rawServerDate)
    }

    fun fromServerToTimeUi(dateTimeUiServer: String): String {
        val rawServerDate = serverStringToServerDate(dateTimeUiServer)
        return toUiTime(rawServerDate)
    }

    fun formatTimeLapseUi(dateRaw: LocalDateTime, duration: Long): String {
        val end = dateRaw.plus(duration, ChronoUnit.MILLIS)
        return "${toUiTime(dateRaw)}-${toUiTime(end)}"
    }

    fun formatTimeLapseUi(startMillis: Long, duration: Long): String {
        val endMillis = startMillis + duration
        return "${toUiTime(LocalDateTime.ofInstant(
            Instant.ofEpochMilli(startMillis),
            ZoneId.systemDefault()
        ))}-${toUiTime(LocalDateTime.ofInstant(
            Instant.ofEpochMilli(endMillis),
            ZoneId.systemDefault()
        ))}"
    }

    fun nextDay(fromUiDate: String) = LocalDate.parse(
        fromUiDate,
        DateTimeFormatter.ofPattern(LOCAL_DATE_FORMATTER)
    )
        .plusDays(ONE_DAY_IN_MILLIS).format(
            DateTimeFormatter.ofPattern(
                LOCAL_DATE_FORMATTER
            )
        )

    fun previousDay(fromUiDate: String) = LocalDate.parse(
        fromUiDate,
        DateTimeFormatter.ofPattern(LOCAL_DATE_FORMATTER)
    )
        .minusDays(ONE_DAY_IN_MILLIS).format(
            DateTimeFormatter.ofPattern(
                LOCAL_DATE_FORMATTER
            )
        )

    fun serverStringToServerDate(serverDateString: String): LocalDateTime = LocalDateTime.parse(
        serverDateString,
        DateTimeFormatter.ofPattern(SERVER_DATE_TIME_FORMATTER)
    )

    fun toRawDateTime(dateString: String): LocalDateTime = LocalDateTime.parse(
        dateString,
        DateTimeFormatter.ofPattern(SERVER_DATE_TIME_FORMATTER)
    )

    fun toRawDate(dateString: String): LocalDate = LocalDate.parse(
        dateString,
        DateTimeFormatter.ofPattern(LOCAL_DATE_FORMATTER)
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

    fun toUiTime(date: LocalDateTime): String = date.format(
        DateTimeFormatter.ofPattern(
            LOCAL_TIME_FORMATTER
        )
    )

    fun toServerDateTimeString(date: LocalDateTime): String = date.format(
        DateTimeFormatter.ofPattern(
            SERVER_DATE_TIME_FORMATTER
        )
    )

    fun toServerDateTimeString(millis: Long): String = toServerDateTimeString(
        LocalDateTime.ofInstant(
            Instant.ofEpochMilli(millis),
            ZoneId.systemDefault()
        )
    )

    fun getPeriodStart(serverDateString: String) = LocalDateTime.parse(
        serverDateString,
        DateTimeFormatter.ofPattern(SERVER_DATE_TIME_FORMATTER)
    )?.atZone(ZoneId.systemDefault())
        ?.toInstant()?.toEpochMilli() ?: 0L

    fun getPeriodEnd(period: String, duration: Long) = getPeriodStart(period) + duration

    companion object {
        private const val SERVER_DATE_TIME_FORMATTER = "yyyy-MM-dd HH:mm"
        private const val LOCAL_DATE_FORMATTER = "dd-MM-yyyy"
        private const val LOCAL_TIME_FORMATTER = "HH:mm"
        private const val ONE_DAY_IN_MILLIS = 1L
    }
}