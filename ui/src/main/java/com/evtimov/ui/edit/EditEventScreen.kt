package com.evtimov.ui.edit

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.evtimov.ui.create.ClientProcedureInput
import com.evtimov.ui.create.LoadingScreen
import com.evtimov.ui.create.ProcedureDurationSpinner
import com.evtimov.ui.create.ReadyButton
import com.evtimov.ui.theme.LocalSbGradients
import com.evtimov.ui.widgets.DatePickerEvent
import com.evtimov.ui.widgets.SbSnackBarVisuals
import com.evtimov.ui.widgets.TimePickerEvent
import com.evtimov.ui.widgets.rememberVbSnackBarState
import kotlinx.coroutines.launch

@Composable
fun EditEventScreen(
    onEventEdit: () -> Unit,
    navigateBack: () -> Unit
) {
    BackHandler {
        navigateBack()
    }
    val viewModel: EditEventViewModel = hiltViewModel()
    val snackbarState = rememberVbSnackBarState()
    val coroutineScope = rememberCoroutineScope()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.eventReady) onEventEdit()

    LaunchedEffect(key1 = uiState.error) {
        uiState.error?.let {
            coroutineScope.launch {
                snackbarState.showSnackBar(
                    SbSnackBarVisuals(message = it)
                )
            }
            viewModel.consumeError()
        }
    }

    Content(
        state = uiState,
        onEditEvent = { viewModel.editEvent() },
        onSetClientName = { viewModel.setClientName(it) },
        onSetProcedure = { viewModel.setProcedure(it) },
        onSetDuration = { viewModel.setDuration(it) },
        onTimeSelected = { hour, minute ->
            viewModel.setSelectedTime(hour, minute)
        },
        onDateSelected = { year, month, day ->
            viewModel.setSelectedDate(year, month, day)
        }
    )
}

@Composable
private fun Content(
    state: EditEventUiState,
    onSetClientName: (String) -> Unit,
    onSetProcedure: (String) -> Unit,
    onDateSelected: (Int, Int, Int) -> Unit,
    onTimeSelected: (Int, Int) -> Unit,
    onSetDuration: (String) -> Unit,
    onEditEvent: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalSbGradients.current.gradientBackgroundVerticalLight)
            .navigationBarsPadding()
            .statusBarsPadding()
            .padding(all = 32.dp)
    ) {
        val (loading, input, date, time, duration, button) = createRefs()

        ClientProcedureInput(
            modifier = Modifier.constrainAs(input) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            clientName = state.clientName,
            procedure = state.procedureName,
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
            selectedEventDate = state.selectedEventDate,
            selectedEventDateUi = state.selectedEventDateUi,
            onDateSelected = onDateSelected
        )
        TimePickerEvent(
            modifier = Modifier.constrainAs(time) {
                top.linkTo(date.bottom, 24.dp)
                start.linkTo(input.start)
                end.linkTo(input.end)
                width = Dimension.fillToConstraints
            },
            selectedEventDate = state.selectedEventDate,
            selectedEventTimeUi = state.selectedEventTimeUi,
            onTimeSelected = onTimeSelected
        )
        ProcedureDurationSpinner(
            modifier = Modifier.constrainAs(duration) {
                top.linkTo(time.bottom, 24.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            options = state.procedureDurations,
            duration = state.duration,
            onSetDuration = onSetDuration
        )
        ReadyButton(
            modifier = Modifier.constrainAs(button) {
                top.linkTo(duration.bottom, 24.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            enableButton = true,
            onClick = onEditEvent
        )
        LoadingScreen(
            loading = state.isLoading,
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
