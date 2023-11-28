package com.evtimov.ui.create

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.evtimov.ui.R
import com.evtimov.ui.widgets.LoadingWidget
import com.example.domain.Outcome
import java.time.LocalDateTime

@Composable
fun CreateEventScreen(
    onEventCreated: () -> Unit
) {
    val viewModel: CreateEventViewModel = hiltViewModel()

    val outcome by viewModel.addEventOutcome.collectAsStateWithLifecycle()
    val context = LocalContext.current

    when (outcome) {
        is Outcome.Failure -> Toast.makeText(
            context,
            (outcome as Outcome.Failure<String>).error,
            Toast.LENGTH_SHORT
        ).show()

        is Outcome.Success -> {
            Toast.makeText(
                context,
                (outcome as Outcome.Success<String>).data,
                Toast.LENGTH_SHORT
            ).show()
            onEventCreated()
        }

        else -> {}
    }
    Content(
        loadingState = outcome is Outcome.Loading,
        clientName = viewModel.clientName.value,
        procedure = viewModel.procedureName.value,
        selectedEventDate = viewModel.selectedEventDate.value,
        onSetClientName = { viewModel.setClientName(it) },
        onSetProcedure = { viewModel.setProcedure(it) },
        onDateSelected = { year, month, day -> viewModel.setSelectedDate(year, month, day) },
        selectedEventTimeUi = viewModel.selectedEventTimeUi.value,
        onTimeSelected = { hour, minutes -> viewModel.setSelectedTime(hour, minutes) },
        options = viewModel.procedureDurations.keys.toList(),
        durationUi = viewModel.duration.value,
        onSetDuration = { viewModel.setDuration(it) },
        buttonEnabled = viewModel.enableCreateButton.value,
        onCreateEvent = { viewModel.createEvent() },
        selectedEventDateUi = viewModel.selectedEventDateUi.value
    )
}

@Composable
fun Content(
    loadingState: Boolean = false,
    clientName: String,
    procedure: String,
    selectedEventDate: LocalDateTime,
    selectedEventDateUi: String,
    onSetClientName: (String) -> Unit,
    onSetProcedure: (String) -> Unit,
    onDateSelected: (Int, Int, Int) -> Unit,
    selectedEventTimeUi: String,
    onTimeSelected: (Int, Int) -> Unit,
    options: List<String>,
    durationUi: String,
    onSetDuration: (String) -> Unit,
    buttonEnabled: Boolean,
    onCreateEvent: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8DEB8))
    ) {
        val (loading, input, date, time, duration, button) = createRefs()

        ClientProcedureInput(
            modifier = Modifier.constrainAs(input) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            clientName = clientName,
            procedure = procedure,
            onSetClientName = onSetClientName,
            onSetProcedure = onSetProcedure
        )
        DatePickerEvent(
            modifier = Modifier.constrainAs(date) {
                top.linkTo(input.bottom, 24.dp)
                start.linkTo(input.start)
                end.linkTo(input.end)
                width = Dimension.fillToConstraints
            },
            selectedEventDate = selectedEventDate,
            selectedEventDateUi = selectedEventDateUi,
            onDateSelected = onDateSelected
        )
        TimePickerEvent(
            modifier = Modifier.constrainAs(time) {
                top.linkTo(date.bottom, 24.dp)
                start.linkTo(input.start)
                end.linkTo(input.end)
                width = Dimension.fillToConstraints
            },
            selectedEventDate = selectedEventDate,
            selectedEventTimeUi = selectedEventTimeUi,
            onTimeSelected = onTimeSelected
        )
        ProcedureDurationSpinner(
            modifier = Modifier.constrainAs(duration) {
                top.linkTo(time.bottom, 24.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            options = options,
            duration = durationUi,
            onSetDuration = onSetDuration
        )
        ReadyButton(
            modifier = Modifier.constrainAs(button) {
                top.linkTo(duration.bottom, 24.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            enableButton = buttonEnabled,
            onCreateEvent = onCreateEvent
        )
        LoadingScreen(
            loading = loadingState,
            modifier = Modifier.constrainAs(loading) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
        )
    }
}

@Composable
fun ClientProcedureInput(
    modifier: Modifier = Modifier,
    clientName: String,
    procedure: String,
    onSetClientName: (String) -> Unit,
    onSetProcedure: (String) -> Unit,
) {
    Column(
        modifier = modifier
            .padding(24.dp)
            .wrapContentHeight()
    ) {
        OutlinedTextField(
            value = clientName,
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 18.sp
            ),
            onValueChange = { onSetClientName(it) },
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
            value = procedure,
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 18.sp
            ),
            onValueChange = { onSetProcedure(it) },
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
fun ReadyButton(
    modifier: Modifier = Modifier,
    enableButton: Boolean,
    onCreateEvent: () -> Unit
) {
    Button(
        modifier = modifier.padding(40.dp),
        onClick = { onCreateEvent() },
        shape = RoundedCornerShape(20.dp),
        enabled = enableButton
    ) {
        Text(text = "Ready")
    }
}

@Composable
fun DatePickerEvent(
    modifier: Modifier = Modifier,
    selectedEventDateUi: String,
    selectedEventDate: LocalDateTime,
    onDateSelected: (Int, Int, Int) -> Unit
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
                context.openDatePicker(
                    currentEventDate = selectedEventDate,
                    onDateSelected = onDateSelected
                )
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
            text = { Text(selectedEventDateUi, color = Color.White) },
            backgroundColor = Color(0xFF7342F0)
        )
    }
}

@Composable
fun LoadingScreen(loading: Boolean, modifier: Modifier = Modifier) {
    if (loading) {
        LoadingWidget(modifier = modifier)
    }
}

@Composable
fun TimePickerEvent(
    modifier: Modifier = Modifier,
    selectedEventTimeUi: String,
    selectedEventDate: LocalDateTime,
    onTimeSelected: (Int, Int) -> Unit
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
                context.openTimePicker(
                    currentEventDate = selectedEventDate,
                    onTimeSelected = onTimeSelected
                )
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
            text = { Text(selectedEventTimeUi, color = Color.White) },
            backgroundColor = Color(0xFF7342F0)
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProcedureDurationSpinner(
    modifier: Modifier = Modifier,
    options: List<String>,
    duration: String,
    onSetDuration: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        TextField(
            value = duration,
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
                        onSetDuration(selectionOption)
                        expanded = false
                    }
                ) {
                    Text(text = selectionOption)
                }
            }
        }
    }
}

private fun Context.openTimePicker(
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

private fun Context.openDatePicker(
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