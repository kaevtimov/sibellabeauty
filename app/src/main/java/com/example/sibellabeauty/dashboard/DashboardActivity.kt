package com.example.sibellabeauty.dashboard

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.sibellabeauty.R
import com.example.sibellabeauty.SibellaBeautyApplication
import com.example.sibellabeauty.theme.AppTheme
import com.example.sibellabeauty.viewModelFactory
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.constraintlayout.compose.Dimension
import com.example.sibellabeauty.create.CreateEventActivity
import com.example.sibellabeauty.login.LoginActivity
import com.example.sibellabeauty.utils.EnlargingWidget
import com.example.sibellabeauty.utils.Pulsating
import com.example.sibellabeauty.widgets.LoadingWidget
import java.time.LocalDate

class DashboardActivity : AppCompatActivity() {

    private val viewModel: DashboardViewModel by viewModelFactory {
        DashboardViewModel(
            (application as SibellaBeautyApplication).usersRepo!!,
            (application as SibellaBeautyApplication).eventsRepo!!
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getLoggedInUser()
        setContent {
            AppTheme {
                DashboardScreen()
            }
        }
    }

    @Composable
    fun DashboardScreen() {
        val uiState by viewModel.uiState.collectAsState()

        if (uiState.loggedInUser == null) {
            startActivity(
                Intent(
                    this,
                    LoginActivity::class.java
                ).apply {
                    flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
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
                })
            DashboardContent(events = uiState.events, modifier = Modifier
                .constrainAs(events) {
                    top.linkTo(topDateNav.bottom)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    height = Dimension.fillToConstraints
                })
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
            LoadingScreen(modifier = Modifier.constrainAs(loading) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            })
        }
    }

    @SuppressLint("StateFlowValueCalledInComposition")
    @Composable
    fun LoadingScreen(modifier: Modifier = Modifier) {
        val loading = viewModel.uiState.value.isLoading
        if (loading == true) {
            LoadingWidget(modifier = modifier)
        }
    }

    private fun openDatePicker() {
        val currentDate = LocalDate.parse(viewModel.uiState.value.selectedDate)

        val dialog = DatePickerDialog(
            this,
            { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
                viewModel.setSelectedDate(LocalDate.of(mYear, mMonth + 1, mDayOfMonth).toString())
            }, currentDate.year, currentDate.monthValue - 1, currentDate.dayOfMonth
        )

        dialog.show()
    }

    @Composable
    fun TopDayNavigation(date: String, modifier: Modifier = Modifier) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(20.dp)
        ) {
            FloatingActionButton(
                modifier = Modifier.size(60.dp),
                onClick = { viewModel.onPrevDay() },
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
                onClick = { openDatePicker() },
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
                onClick = { viewModel.onNextDay() },
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
    fun DashboardContent(events: ArrayList<com.example.data.EventFb>, modifier: Modifier = Modifier) {
        if (events.isEmpty()) return
        Box(
            modifier = modifier
                .fillMaxHeight()
                .clip(RoundedCornerShape(12.dp, 12.dp))
                .background(Color(0xFFFBE5FD))
        ) {
            LazyColumn(contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp)) {
                items(
                    events,
                    { listItem: com.example.data.EventFb -> listItem.id!! })
                {
                    val dismissState = rememberDismissState()

                    if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                        viewModel.removeEvent(it)
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

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun EventListItem(event: com.example.data.EventFb, dismissState: DismissState) {
        EnlargingWidget(content =  {
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
            ){
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
        }, onLongClick = { editEvent() })
    }

    private fun editEvent() {
        Toast.makeText(this, "Editing event", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getEventsByDate()
    }

    private fun onLogout() {
        viewModel.logout()
    }

    private fun onCreateEvent() {
        startActivity(Intent(this, CreateEventActivity::class.java))
    }

}