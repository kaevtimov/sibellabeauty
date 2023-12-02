package com.evtimov.ui.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.DatePicker
import java.time.LocalDate
import java.time.LocalDateTime

fun Context.openDatePicker(
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    val currentDate = LocalDate.parse(selectedDate)

    val dialog = DatePickerDialog(
        this,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            onDateSelected(LocalDate.of(mYear, mMonth + 1, mDayOfMonth).toString())
        }, currentDate.year, currentDate.monthValue - 1, currentDate.dayOfMonth
    )

    dialog.show()
}

fun Context.openDatePicker(
    currentEventDate: LocalDateTime,
    onDateSelected: (Int, Int, Int) -> Unit
) {
    val dialog = DatePickerDialog(
        this,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            onDateSelected(mYear, mMonth, mDayOfMonth)
        }, currentEventDate.year, currentEventDate.monthValue - 1, currentEventDate.dayOfMonth
    )
    dialog.datePicker.minDate = System.currentTimeMillis() - 1000

    dialog.show()
}

fun Context.openTimePicker(
    currentEventDate: LocalDateTime,
    onTimeSelected: (Int, Int) -> Unit
) {
    val mTimePickerDialog = TimePickerDialog(
        this,
        { _, mHour: Int, mMinute: Int ->
            onTimeSelected(mHour, mMinute)
        },
        currentEventDate.hour,
        currentEventDate.minute,
        true
    )

    mTimePickerDialog.show()
}