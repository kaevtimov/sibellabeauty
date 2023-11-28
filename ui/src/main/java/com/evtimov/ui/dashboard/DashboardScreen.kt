package com.evtimov.ui.dashboard

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.evtimov.ui.R
import com.evtimov.ui.theme.LocalSbColors
import com.evtimov.ui.theme.LocalSbTypography
import com.evtimov.ui.utils.EnlargingWidget
import com.evtimov.ui.utils.Pulsating
import com.evtimov.ui.widgets.LoadingWidget
import com.example.domain.event.Event
import java.time.LocalDate

@Composable
fun DashboardScreen(
    onNavigateLogin: () -> Unit,
    onCreateEvent: () -> Unit
) {
    val viewModel: DashboardViewModel = hiltViewModel<DashboardViewModel>().apply {
        observeLifecycleEvents(LocalLifecycleOwner.current.lifecycle)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Content(
        uiState = uiState,
        onNavigateLogin = onNavigateLogin,
        onDateSelected = { viewModel.setSelectedDate(it) },
        onRemoveEvent = { viewModel.removeEvent(it) },
        onPrevDay = { viewModel.onPrevDay() },
        onNextDay = { viewModel.onNextDay() },
        onLogout = { viewModel.logout() },
        onCreateEvent = onCreateEvent
    )
}

@Composable
fun Content(
    uiState: DashboardUiState,
    onNavigateLogin: () -> Unit,
    onDateSelected: (String) -> Unit,
    onRemoveEvent: (Event) -> Unit,
    onPrevDay: () -> Unit,
    onNextDay: () -> Unit,
    onLogout: () -> Unit,
    onCreateEvent: () -> Unit
) {
    if (uiState.loggedInUser == null) {
//        onNavigateLogin()
    }
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFffbc9c))
    ) {
        val (loading, topDateNav, events, addEventBtn, logoutBtn) = createRefs()

        TopDayNavigation(
            date = uiState.selectedDate,
            modifier = Modifier.constrainAs(topDateNav) {
                top.linkTo(parent.top, 20.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            onDateSelected = onDateSelected,
            onPrevDay = onPrevDay,
            onNextDay = onNextDay
        )
        DashboardContent(
            events = uiState.events,
            modifier = Modifier
                .constrainAs(events) {
                    top.linkTo(topDateNav.bottom)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    height = Dimension.fillToConstraints
                },
            onRemoveEvent = onRemoveEvent
        )
        Pulsating(modifier = Modifier
            .constrainAs(addEventBtn) {
                bottom.linkTo(parent.bottom, 34.dp)
                end.linkTo(parent.end, 34.dp)
            }) {
            FloatingActionButton(
                modifier = Modifier.size(60.dp),
                onClick = { onCreateEvent() },
                backgroundColor = Color(0xFF356421)
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_baseline_person_add_24),
                    contentDescription = "Add event",
                    tint = Color(0xFFF0AA20)
                )
            }
        }
        FloatingActionButton(
            modifier = Modifier
                .size(60.dp)
                .constrainAs(logoutBtn) {
                    bottom.linkTo(parent.bottom, 34.dp)
                    start.linkTo(parent.start, 34.dp)
                },
            onClick = { onLogout() },
            backgroundColor = Color(0xFFFF5722)
        ) {
            Icon(
                painterResource(id = R.drawable.ic_baseline_logout_24),
                contentDescription = "Add event",
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

private fun Context.openDatePicker(
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

@Composable
fun TopDayNavigation(
    date: String,
    modifier: Modifier = Modifier,
    onDateSelected: (String) -> Unit,
    onPrevDay: () -> Unit,
    onNextDay: () -> Unit
) {
    val context = LocalContext.current
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(20.dp)
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
        ExtendedFloatingActionButton(
            modifier = Modifier
                .wrapContentWidth()
                .height(60.dp),
            onClick = {
                context.openDatePicker(
                    selectedDate = date,
                    onDateSelected = onDateSelected
                )
            },
            icon = {
                Icon(
                    painterResource(id = R.drawable.ic_baseline_calendar_month_24),
                    contentDescription = "Date",
                    tint = Color.Black
                )
            },
            text = { Text(text = date, color = Color.Black, fontWeight = FontWeight.Bold) },
            backgroundColor = Color(0xFFE9E8E8)
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DashboardContent(
    events: List<Event>,
    modifier: Modifier = Modifier,
    onRemoveEvent: (Event) -> Unit
) {
    if (events.isEmpty()) {
        EmptyContent()
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp, 12.dp))
                .background(Color(0xFFFBE5FD))
        ) {
            LazyColumn(contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp)) {
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
                            EventListItem(event = it, dismissState = dismissState)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EventListItem(event: Event, dismissState: DismissState) {
    val context = LocalContext.current

    EnlargingWidget(content = {
        Card(
            elevation = animateDpAsState(
                if (dismissState.dismissDirection != null) 8.dp else 6.dp
            ).value,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            backgroundColor = Color(0xFFF8F8F8),
            shape = RoundedCornerShape(corner = CornerSize(16.dp))
        ) {
            ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
                val (image, eventDescription, time) = createRefs()

                FloatingActionButton(
                    modifier = Modifier
                        .size(70.dp)
                        .constrainAs(image) {
                            this.start.linkTo(parent.start, 12.dp)
                            bottom.linkTo(parent.bottom, 12.dp)
                            top.linkTo(parent.top, 12.dp)
                        },
                    onClick = { },
                    backgroundColor = Color(0xFF4B4185)
                ) {
                    Icon(
                        painterResource(id = R.drawable.ic_baseline_content_cut_24),
                        contentDescription = "Scissors",
                        tint = Color(0xFFffbc9c)
                    )
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .constrainAs(eventDescription) {
                            this.start.linkTo(image.end, 12.dp)
                            bottom.linkTo(parent.bottom, 12.dp)
                            top.linkTo(parent.top, 12.dp)
                            end.linkTo(time.start, 8.dp)
                            height = Dimension.fillToConstraints
                            width = Dimension.fillToConstraints
                        }) {
                    Text(
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        text = "Name: ${event.name}",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        text = "Procedure: ${event.procedure}",
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
                        text = "${event.timeLapseString}",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }, onLongClick = {
        Toast.makeText(context, "Editing event", Toast.LENGTH_SHORT).show()
    })
}

@Composable
private fun EmptyContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "No items for today",
            modifier = Modifier.padding(horizontal = 6.dp),
            color = LocalSbColors.current.neutral700,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun <viewModel : LifecycleObserver> viewModel.observeLifecycleEvents(lifecycle: Lifecycle) {
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(this@observeLifecycleEvents)
        onDispose {
            lifecycle.removeObserver(this@observeLifecycleEvents)
        }
    }
}
