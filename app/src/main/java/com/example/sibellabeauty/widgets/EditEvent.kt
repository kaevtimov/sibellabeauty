package com.example.sibellabeauty.widgets

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.DatePicker
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.sibellabeauty.Constants
import com.example.sibellabeauty.R
import java.time.LocalDateTime

data class EditUiState(
    var clientName: String? = null,
    var procedure: String? = null,
    var selectedEventDateUi: String? = null,
    var selectedEventTimeUi: String? = null,
    var duration: String? = null,
    var selectedEventDate: LocalDateTime? = null
)

@Composable
fun EditEventContent(
    modifier: Modifier = Modifier,
    uiState: EditUiState? = null,
    setClientName: (String?) -> Unit,
    setProcedure: (String?) -> Unit,
    onEventReady: () -> Unit,
    setDuration: (String?) -> Unit,
    setSelectedTime: (Int, Int) -> Unit,
    setSelectedDate: (Int, Int, Int) -> Unit,
    onClose: () -> Unit
) {
    Column(modifier = modifier, content = {
//        val (closeIcon, input, date, time, duration, button) = createRefs()

        FloatingActionButton(
            modifier = Modifier
                .size(40.dp),
//                .constrainAs(closeIcon) {
//                    top.linkTo(parent.top, 12.dp)
//                    start.linkTo(parent.start, 12.dp)
//                },
            onClick = { onClose() },
            backgroundColor = Color(0xFFCACACA)
        ) {
            Icon(
                modifier = modifier
                    .width(24.dp)
                    .height(24.dp),
                painter = painterResource(id = R.drawable.ic_baseline_close_24),
                contentDescription = "Close",
                tint = Color(0xFF000000)
            )
        }
        ClientProcedureInput(modifier = Modifier/*.constrainAs(input) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }*/, clientName = uiState?.clientName,
            procedure = uiState?.procedure,
            setClientName = { setClientName(it) },
            setProcedure = { setProcedure(it) })
        DatePickerEvent(
            modifier = Modifier/*.constrainAs(date) {
                top.linkTo(input.bottom, 24.dp)
                start.linkTo(input.start)
                end.linkTo(input.end)
                width = Dimension.fillToConstraints
            }*/,
            selectedEventDateUi = uiState?.selectedEventDateUi,
            selectedEventDate = uiState?.selectedEventDate,
            setSelectedDate = { year, month, day ->
                setSelectedDate(year, month, day)
            })
        TimePickerEvent(
            modifier = Modifier/*.constrainAs(time) {
                top.linkTo(date.bottom, 24.dp)
                start.linkTo(input.start)
                end.linkTo(input.end)
                width = Dimension.fillToConstraints
            }*/,
            selectedEventTimeUi = uiState?.selectedEventTimeUi,
            selectedEventDate = uiState?.selectedEventDate,
            setSelectedTime = { hour, minutes ->
                setSelectedTime(hour, minutes)
            })
        ProcedureDurationSpinner(modifier = Modifier/*.constrainAs(duration) {
            top.linkTo(time.bottom, 24.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }*/, duration = uiState?.duration) { setDuration(it) }
        ReadyButton(modifier = Modifier/*.constrainAs(button) {
            top.linkTo(duration.bottom, 24.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }*/) { onEventReady() }
    })
}

@Composable
fun ClientProcedureInput(
    modifier: Modifier = Modifier,
    clientName: String?,
    procedure: String?,
    setClientName: (String?) -> Unit,
    setProcedure: (String?) -> Unit
) {
    Column(
        modifier = modifier
            .padding(24.dp)
            .wrapContentHeight()
    ) {
        OutlinedTextField(
            value = clientName ?: "",
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 18.sp
            ),
            onValueChange = { setClientName(it) },
            label = { Text(text = "Client name") },
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_baseline_add_reaction_24),
                    contentDescription = "username_icon"
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF498EF7),
                unfocusedBorderColor = Color(0xFF5E82EE)
            )
        )
        OutlinedTextField(
            value = procedure ?: "",
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 18.sp
            ),
            onValueChange = { setProcedure(it) },
            label = { Text(text = "Procedure") },
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_round_airline_seat_recline_normal_24),
                    contentDescription = "password_icon"
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF498EF7),
                unfocusedBorderColor = Color(0xFF5E82EE)
            )
        )
    }
}

@Composable
fun ReadyButton(modifier: Modifier = Modifier, readyEvent: () -> Unit) {
    Button(
        modifier = modifier.padding(40.dp),
        onClick = { readyEvent() },
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(text = "Ready")
    }
}

@Composable
fun DatePickerEvent(
    modifier: Modifier = Modifier,
    selectedEventDateUi: String?,
    selectedEventDate: LocalDateTime?,
    setSelectedDate: (Int, Int, Int) -> Unit
) {
    val context = LocalContext.current
    Box(
        contentAlignment = Alignment.Center, modifier = modifier
            .wrapContentHeight()
            .padding(horizontal = 24.dp)
    ) {
        ExtendedFloatingActionButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp),
            onClick = {
                openDatePicker(selectedEventDate ?: LocalDateTime.now(), context) { year, month, day ->
                    setSelectedDate(year, month, day)
                }
            },
            icon = {
                Icon(
                    modifier = Modifier
                        .width(55.dp)
                        .height(55.dp),
                    painter = painterResource(id = R.drawable.ic_baseline_calendar_month_24),
                    contentDescription = "Date calendar",
                    tint = Color.White
                )
            },
            text = { Text(selectedEventDateUi ?: "", color = Color.White) },
            backgroundColor = Color(0xFF7342F0)
        )
    }
}

@Composable
fun TimePickerEvent(
    modifier: Modifier = Modifier,
    selectedEventTimeUi: String?,
    selectedEventDate: LocalDateTime?,
    setSelectedTime: (Int, Int) -> Unit
) {
    val context = LocalContext.current
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .wrapContentHeight()
            .padding(horizontal = 24.dp)
    ) {
        ExtendedFloatingActionButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp),
            onClick = {
                openTimePicker(selectedEventDate ?: LocalDateTime.now(), context, setSelectedTime = { hour, minute ->
                    setSelectedTime(hour, minute)
                })
            },
            icon = {
                Icon(
                    modifier = Modifier
                        .width(55.dp)
                        .height(55.dp),
                    painter = painterResource(id = R.drawable.ic_baseline_access_time_24),
                    contentDescription = "Time calendar",
                    tint = Color.White
                )
            },
            text = { Text(selectedEventTimeUi ?: "", color = Color.White) },
            backgroundColor = Color(0xFF7342F0)
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProcedureDurationSpinner(
    modifier: Modifier = Modifier,
    duration: String?,
    setDuration: (String?) -> Unit
) {
    val options = Constants.procedureDurations.keys
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        TextField(
            value = duration ?: "",
            readOnly = true,
            onValueChange = { },
            label = { Text("Duration") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    onClick = {
                        setDuration(selectionOption)
                        expanded = false
                    }
                ) {
                    Text(text = selectionOption)
                }
            }
        }
    }
}

private fun openTimePicker(
    selectedEventDate: LocalDateTime,
    context: Context,
    setSelectedTime: (Int, Int) -> Unit
) {
    val mTimePickerDialog = TimePickerDialog(
        context,
        { _, mHour: Int, mMinute: Int ->
            setSelectedTime(mHour, mMinute)
        },
        selectedEventDate.hour,
        selectedEventDate.minute,
        true
    )

    mTimePickerDialog.show()
}

private fun openDatePicker(
    selectedEventDate: LocalDateTime,
    context: Context,
    setSelectedDate: (Int, Int, Int) -> Unit
) {
    val dialog = DatePickerDialog(
        context,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            setSelectedDate(mYear, mMonth, mDayOfMonth)
        }, selectedEventDate.year, selectedEventDate.monthValue - 1, selectedEventDate.dayOfMonth
    )
    dialog.datePicker.minDate = System.currentTimeMillis() - 1000

    dialog.show()
}