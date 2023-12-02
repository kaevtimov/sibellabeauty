package com.evtimov.ui.create

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.evtimov.ui.R
import com.evtimov.ui.theme.LocalSbGradients
import com.evtimov.ui.theme.LocalSbTypography
import com.evtimov.ui.widgets.DatePickerEvent
import com.evtimov.ui.widgets.LoadingWidget
import com.evtimov.ui.widgets.SbBottomSheet
import com.evtimov.ui.widgets.SbBottomSheetValue
import com.evtimov.ui.widgets.SbSnackBarVisuals
import com.evtimov.ui.widgets.TimePickerEvent
import com.evtimov.ui.widgets.rememberSbBottomSheetState
import com.evtimov.ui.widgets.rememberVbSnackBarState
import kotlinx.coroutines.launch

@Composable
fun CreateEventScreen(
    onEventCreated: () -> Unit,
    navigateBack: () -> Unit
) {
    BackHandler {
        navigateBack()
    }
    val viewModel: CreateEventViewModel = hiltViewModel()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarState = rememberVbSnackBarState()
    val coroutineScope = rememberCoroutineScope()

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
    if (uiState.eventReady) onEventCreated()

    Content(
        state = uiState,
        onSetClientName = { viewModel.setClientName(it) },
        onSetProcedure = { viewModel.setProcedure(it) },
        onDateSelected = { year, month, day -> viewModel.setSelectedDate(year, month, day) },
        onTimeSelected = { hour, minutes -> viewModel.setSelectedTime(hour, minutes) },
        onSetDuration = { viewModel.setDuration(it) },
        onCreateEvent = { viewModel.createEvent() },
    )
}

@Composable
fun Content(
    state: CreateEventUiState,
    onSetClientName: (String) -> Unit,
    onSetProcedure: (String) -> Unit,
    onDateSelected: (Int, Int, Int) -> Unit,
    onTimeSelected: (Int, Int) -> Unit,
    onSetDuration: (String) -> Unit,
    onCreateEvent: () -> Unit
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
                width = Dimension.fillToConstraints
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
            enableButton = state.enableCreateButton,
            onClick = onCreateEvent
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
            .wrapContentHeight()
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = clientName,
            textStyle = LocalSbTypography.current.bodyLarge.copy(color = Color.Black),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color.Black,
                placeholderColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Black,
                backgroundColor = Color.White,
                cursorColor = Color.Black,
                focusedBorderColor = Color(0xFFFFEB3B),
                unfocusedBorderColor = Color(0xFFFFEB3B)
            ),
            shape = RoundedCornerShape(32.dp),
            onValueChange = { onSetClientName(it) },
            label = { Text(text = stringResource(id = R.string.create_client_name_label)) },
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = procedure,
            textStyle = LocalSbTypography.current.bodyLarge.copy(color = Color.Black),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color.Black,
                placeholderColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.Black,
                backgroundColor = Color.White,
                cursorColor = Color.Black,
                focusedBorderColor = Color(0xFFFFEB3B),
                unfocusedBorderColor = Color(0xFFFFEB3B)
            ),
            shape = RoundedCornerShape(32.dp),
            onValueChange = { onSetProcedure(it) },
            label = {
                Text(text = stringResource(id = R.string.create_procedure_label))
            },
        )
    }
}

@Composable
fun ReadyButton(
    modifier: Modifier = Modifier,
    enableButton: Boolean,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = { onClick() },
        shape = RoundedCornerShape(32.dp),
        enabled = enableButton,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color(0xFF72B14A),
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = 52.dp, vertical = 12.dp),
    ) {
        Text(
            text = stringResource(id = R.string.button_ready_label),
            style = LocalSbTypography.current.titleLarge
        )
    }
}

@Composable
fun LoadingScreen(loading: Boolean, modifier: Modifier = Modifier) {
    if (loading) {
        LoadingWidget(modifier = modifier)
    }
}

@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalComposeUiApi::class,
)
@Composable
fun ProcedureDurationSpinner(
    modifier: Modifier = Modifier,
    options: List<String>,
    duration: String,
    onSetDuration: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetState = rememberSbBottomSheetState()
    val keyboardController = LocalSoftwareKeyboardController.current

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(56.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = duration,
                readOnly = true,
                onValueChange = { },
                textStyle = LocalSbTypography.current.bodyMedium,
                label = {
                    Text(
                        text = stringResource(id = R.string.create_duration_label),
                        style = LocalSbTypography.current.labelSmall
                    )
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = bottomSheetState.currentValue == SbBottomSheetValue.Expanded,
                        onIconClick = {
                            coroutineScope.launch {
                                keyboardController?.hide()
                                bottomSheetState.show()
                            }
                        }
                    )
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    backgroundColor = Color.White,
                    textColor = Color.Black,
                    unfocusedLabelColor = Color.Black,
                    focusedLabelColor = Color.Black,
                    disabledLabelColor = Color.Black,
                    placeholderColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }
    }

    SbBottomSheet(state = bottomSheetState) {
        LazyColumn(
            contentPadding = PaddingValues(24.dp)
        ) {
            items(options) {
                DropdownMenuItem(
                    onClick = {
                        onSetDuration(it)
                        coroutineScope.launch {
                            bottomSheetState.dismiss()
                        }
                    }
                ) {
                    Text(text = it)
                }
            }
        }
    }
}