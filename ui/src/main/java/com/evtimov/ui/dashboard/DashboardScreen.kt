package com.evtimov.ui.dashboard

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.evtimov.ui.R
import com.evtimov.ui.theme.LocalSbColors
import com.evtimov.ui.theme.LocalSbGradients
import com.evtimov.ui.utils.observeLifecycleEvents
import com.evtimov.ui.widgets.LoadingWidget
import com.evtimov.ui.widgets.SbSnackBar
import com.evtimov.ui.widgets.SbSnackBarVisuals
import com.evtimov.ui.widgets.rememberVbSnackBarState
import com.example.domain.model.Event
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Composable
fun DashboardScreen(
    onNavigateLogin: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onCreateEvent: () -> Unit,
    onEditEvent: (String) -> Unit,
) {
    val snackbarState = rememberVbSnackBarState()
    val coroutineScope = rememberCoroutineScope()
    val viewModel: DashboardViewModel = hiltViewModel<DashboardViewModel>().apply {
        observeLifecycleEvents(LocalLifecycleOwner.current.lifecycle)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = uiState.navigateToLogin) {
        if (uiState.navigateToLogin == true) {
            onNavigateLogin()
            viewModel.onFinishNavigate()
        }
    }

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
        uiState = uiState,
        onNavigateLogin = onNavigateLogin,
        onDateSelected = { viewModel.setSelectedDate(it) },
        onRemoveEvent = { viewModel.removeEvent(it) },
        onPrevDay = { viewModel.onPrevDay() },
        onNextDay = { viewModel.onNextDay() },
        onSettings = { onNavigateToSettings() },
        onCreateEvent = onCreateEvent,
        onEditEvent = onEditEvent
    )
    SbSnackBar(snackBarHostState = snackbarState)
}

@Composable
fun Content(
    uiState: DashboardUiState,
    onNavigateLogin: () -> Unit,
    onDateSelected: (LocalDateTime) -> Unit,
    onRemoveEvent: (Event) -> Unit,
    onPrevDay: () -> Unit,
    onNextDay: () -> Unit,
    onSettings: () -> Unit,
    onCreateEvent: () -> Unit,
    onEditEvent: (String) -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalSbGradients.current.gradientBackgroundVerticalLight)
            .padding(start = 32.dp, end = 32.dp)
    ) {
        val (loading, topDateNav, events, addEventBtn, logoutBtn) = createRefs()

        DashboardContent(
            events = uiState.events,
            modifier = Modifier
                .constrainAs(events) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    height = Dimension.fillToConstraints
                },
            onRemoveEvent = onRemoveEvent,
            onEditEvent = onEditEvent
        )
        TopDayNavigation(
            date = uiState.selectedDate,
            dateUi = uiState.selectedDateUi,
            modifier = Modifier.constrainAs(topDateNav) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            onDateSelected = onDateSelected,
            onPrevDay = onPrevDay,
            onNextDay = onNextDay
        )
        FloatingActionButton(
            modifier = Modifier
                .size(60.dp)
                .constrainAs(addEventBtn) {
                    bottom.linkTo(parent.bottom, 66.dp)
                    end.linkTo(parent.end)
                },
            onClick = { onCreateEvent() },
            backgroundColor = Color(0xFFE9E8E8)
        ) {
            Icon(
                painterResource(id = R.drawable.ic_baseline_person_add_24),
                contentDescription = "Add event",
                tint = Color(0xFF090703)
            )
        }
        FloatingActionButton(
            modifier = Modifier
                .size(60.dp)
                .constrainAs(logoutBtn) {
                    bottom.linkTo(parent.bottom, 66.dp)
                    start.linkTo(parent.start)
                },
            onClick = { onSettings() },
            backgroundColor = Color.White
        ) {
            Icon(
                painterResource(id = R.drawable.ic_settings),
                contentDescription = null,
                tint = Color(0xFF090703)
            )
        }
        LoadingScreen(
            isLoading = uiState.isLoading,
            modifier = Modifier.constrainAs(loading) {
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
fun LoadingScreen(
    modifier: Modifier = Modifier,
    isLoading: Boolean?
) {
    if (isLoading == true) {
        LoadingWidget(modifier = modifier)
    }
}

@Composable
fun TopDayNavigation(
    date: LocalDateTime,
    dateUi: String,
    modifier: Modifier = Modifier,
    onDateSelected: (LocalDateTime) -> Unit,
    onPrevDay: () -> Unit,
    onNextDay: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color.Transparent)
            .padding(top = 32.dp)
            .statusBarsPadding()
    ) {
        FloatingActionButton(
            modifier = Modifier.size(60.dp),
            onClick = { onPrevDay() },
            backgroundColor = Color(0xFFE9E8E8)
        ) {
            Icon(
                painterResource(id = R.drawable.ic_baseline_chevron_left_24),
                contentDescription = "Arrow back",
                tint = Color.Black
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        TopBarDatePickView(
            date = date,
            dateUi = dateUi,
            onDateSelected = onDateSelected
        )
        Spacer(modifier = Modifier.width(20.dp))
        FloatingActionButton(
            modifier = Modifier.size(60.dp),
            onClick = { onNextDay() },
            backgroundColor = Color(0xFFE9E8E8)
        ) {
            Icon(
                painterResource(id = R.drawable.ic_baseline_chevron_right_24),
                contentDescription = "Arrow forward",
                tint = Color.Black
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBarDatePickView(
    date: LocalDateTime,
    dateUi: String,
    onDateSelected: (LocalDateTime) -> Unit
) {
    val state = rememberDatePickerState().apply {
        displayMode = DisplayMode.Picker
        this.setSelection(date.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli())
    }
    val openDialog = remember { mutableStateOf(false) }
    ExtendedFloatingActionButton(
        modifier = Modifier
            .wrapContentWidth()
            .height(60.dp),
        onClick = { openDialog.value = true },
        icon = {
            Icon(
                painterResource(id = R.drawable.ic_baseline_calendar_month_24),
                contentDescription = "Date",
                tint = Color.Black
            )
        },
        text = { Text(text = dateUi, color = Color.Black, fontWeight = FontWeight.Bold) },
        backgroundColor = Color(0xFFE9E8E8)
    )
    if (openDialog.value) {
        DatePickerDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                        onDateSelected(
                            LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(state.selectedDateMillis!!),
                                ZoneId.systemDefault()
                            )
                        )
                    }
                ) {
                    androidx.compose.material3.Text(stringResource(id = R.string.action_ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                    }
                ) {
                    androidx.compose.material3.Text(stringResource(id = R.string.action_cancel))
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = Color.White
            )
        ) {
            DatePicker(
                state = state,
                colors = DatePickerDefaults.colors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFFF59B70),
                    headlineContentColor = Color(0xFFF59B70),
                    subheadContentColor = Color(0xFFFF3A68),
                    weekdayContentColor = Color(0xFFFF3A68),
                    yearContentColor = Color(0xFFFF3A68),
                    currentYearContentColor = Color(0xFFFF3A68),
                    selectedYearContentColor = Color(0xFFFF3A68),
                    dayContentColor = Color(0xFFFF3A68),
                    todayContentColor = Color(0xFFFF3A68),
                    dayInSelectionRangeContentColor = Color(0xFFFF3A68),
                    selectedDayContentColor = Color.White,
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DashboardContent(
    events: List<Event>,
    modifier: Modifier = Modifier,
    onRemoveEvent: (Event) -> Unit,
    onEditEvent: (String) -> Unit
) {
    if (events.isEmpty()) {
        EmptyContent()
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
        ) {
            LazyColumn(contentPadding = PaddingValues(vertical = 10.dp)) {
                item {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .statusBarsPadding()
                    )
                }
                items(events) {
                    val dismissState = rememberDismissState()

                    if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                        onRemoveEvent(it)
                    }
                    SwipeToDismiss(
                        state = dismissState,
                        directions = setOf(DismissDirection.EndToStart),
                        dismissThresholds = { direction ->
                            FractionalThreshold(if (direction == DismissDirection.EndToStart) 0.1f else 0.05f)
                        },
                        background = {
                            val color by animateColorAsState(
                                when (dismissState.targetValue) {
                                    DismissValue.Default -> Color.Transparent
                                    else -> Color.Red
                                }
                            )
                            val alignment = Alignment.CenterEnd
                            val icon = Icons.Default.Delete

                            val scale by animateFloatAsState(
                                if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
                            )

                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(color)
                                    .padding(horizontal = Dp(20f)),
                                contentAlignment = alignment
                            ) {
                                Icon(
                                    icon,
                                    contentDescription = "Delete Icon",
                                    modifier = Modifier.scale(scale)
                                )
                            }
                        },
                        dismissContent = {
                            EventListItem(
                                event = it,
                                dismissState = dismissState,
                                onEditEvent = onEditEvent
                            )
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EventListItem(
    event: Event,
    dismissState: DismissState,
    onEditEvent: (String) -> Unit
) {
    Card(
        elevation = animateDpAsState(
            if (dismissState.dismissDirection != null) 8.dp else 6.dp
        ).value,
        modifier = Modifier
            .padding(vertical = 6.dp)
            .fillMaxWidth()
            .height(70.dp),
        backgroundColor = Color(0xFFF8F8F8),
        shape = RoundedCornerShape(corner = CornerSize(16.dp))
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onEditEvent(event.id.orEmpty()) }
        ) {
            val (eventDescription, time) = createRefs()
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .constrainAs(eventDescription) {
                        start.linkTo(parent.start, 12.dp)
                        bottom.linkTo(parent.bottom, 12.dp)
                        top.linkTo(parent.top, 12.dp)
                        end.linkTo(time.start, 8.dp)
                        height = Dimension.fillToConstraints
                        width = Dimension.fillToConstraints
                    }) {
                Text(
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    text = stringResource(id = R.string.dashboard_name_label, event.name.orEmpty()),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    text = stringResource(
                        id = R.string.dashboard_procedure_label,
                        event.procedure.orEmpty()
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Row(modifier = Modifier
                .constrainAs(time) {
                    this.start.linkTo(eventDescription.end)
                    bottom.linkTo(parent.bottom)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    height = Dimension.fillToConstraints
                    width = Dimension.wrapContent
                }
                .background(Color(0xFFffbc9c)),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.padding(horizontal = 6.dp),
                    text = "${event.durationUi}",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun EmptyContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(id = R.string.dashboard_no_items),
            modifier = Modifier.padding(horizontal = 6.dp),
            color = LocalSbColors.current.neutral700,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
