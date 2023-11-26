package com.example.sibellabeauty.create

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.domain.Outcome
import com.example.sibellabeauty.R
import com.example.sibellabeauty.theme.AppTheme
import com.example.sibellabeauty.widgets.LoadingWidget

class CreateEventActivity : AppCompatActivity() {

    private val viewModel: CreateEventViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val outcome by viewModel.addEventOutcome.collectAsStateWithLifecycle()
                when (outcome) {
                    is Outcome.Failure -> Toast.makeText(
                        this,
                        (outcome as Outcome.Failure<String>).error,
                        Toast.LENGTH_SHORT
                    ).show()

                    is Outcome.Success -> {
                        Toast.makeText(
                            this,
                            (outcome as Outcome.Success<String>).data,
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                    else -> {}
                }
                CreateEventScreen(outcome is Outcome.Loading)
            }
        }
    }

    @Composable
    fun CreateEventScreen(loadingState: Boolean = false) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8DEB8))
        ) {
            val (loading, input, date, time, duration, button) = createRefs()

            ClientProcedureInput(modifier = Modifier.constrainAs(input) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            })
            DatePickerEvent(modifier = Modifier.constrainAs(date) {
                top.linkTo(input.bottom, 24.dp)
                start.linkTo(input.start)
                end.linkTo(input.end)
                width = Dimension.fillToConstraints
            })
            TimePickerEvent(modifier = Modifier.constrainAs(time) {
                top.linkTo(date.bottom, 24.dp)
                start.linkTo(input.start)
                end.linkTo(input.end)
                width = Dimension.fillToConstraints
            })
            ProcedureDurationSpinner(modifier = Modifier.constrainAs(duration) {
                top.linkTo(time.bottom, 24.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            })
            ReadyButton(modifier = Modifier.constrainAs(button) {
                top.linkTo(duration.bottom, 24.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            })
            LoadingScreen(loading = loadingState, modifier = Modifier.constrainAs(loading) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            })
        }
    }

    @Composable
    fun ClientProcedureInput(modifier: Modifier = Modifier) {
        val clientName = viewModel.clientName
        val procedure = viewModel.procedureName

        Column(
            modifier = modifier
                .padding(24.dp)
                .wrapContentHeight()
        ) {
            OutlinedTextField(
                value = clientName.value,
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 18.sp
                ),
                onValueChange = { viewModel.setClientName(it) },
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
                value = procedure.value,
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 18.sp
                ),
                onValueChange = { viewModel.setProcedure(it) },
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
    fun ReadyButton(modifier: Modifier = Modifier) {
        val enabled = viewModel.enableCreateButton

        Button(
            modifier = modifier.padding(40.dp),
            onClick = {
                viewModel.createEvent()
            },
            shape = RoundedCornerShape(20.dp),
            enabled = enabled.value
        ) {
            Text(text = "Ready")
        }
    }

    @Composable
    fun DatePickerEvent(modifier: Modifier = Modifier) {
        val selectedEventDate = viewModel.selectedEventDateUi.value

        Box(
            contentAlignment = Alignment.Center, modifier = modifier
                .wrapContentHeight()
                .padding(horizontal = 24.dp)
        ) {
            ExtendedFloatingActionButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(75.dp),
                onClick = { openDatePicker() },
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
                text = { Text(selectedEventDate, color = Color.White) },
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
    fun TimePickerEvent(modifier: Modifier = Modifier) {
        val selectedEventTime = viewModel.selectedEventTimeUi.value

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
                onClick = { openTimePicker() },
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
                text = { Text(selectedEventTime, color = Color.White) },
                backgroundColor = Color(0xFF7342F0)
            )
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun ProcedureDurationSpinner(modifier: Modifier = Modifier) {
        val options = viewModel.procedureDurations.keys
        var expanded by remember {
            mutableStateOf(false)
        }
        val selectedOptionText = viewModel.duration

        ExposedDropdownMenuBox(
            modifier = modifier,
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            TextField(
                value = selectedOptionText.value,
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
                            viewModel.setDuration(selectionOption)
                            expanded = false
                        }
                    ) {
                        Text(text = selectionOption)
                    }
                }
            }
        }
    }

    private fun openTimePicker() {
        val currentTime = viewModel.selectedEventDate.value

        val mTimePickerDialog = TimePickerDialog(
            this,
            { _, mHour: Int, mMinute: Int ->
                viewModel.setSelectedTime(mHour, mMinute)
            },
            currentTime.hour,
            currentTime.minute,
            true
        )

        mTimePickerDialog.show()
    }

    private fun openDatePicker() {
        val currentDate = viewModel.selectedEventDate.value

        val dialog = DatePickerDialog(
            this,
            { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
                viewModel.setSelectedDate(mYear, mMonth, mDayOfMonth)
            }, currentDate.year, currentDate.monthValue - 1, currentDate.dayOfMonth
        )
        dialog.datePicker.minDate = System.currentTimeMillis() - 1000

        dialog.show()
    }
}