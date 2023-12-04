package com.evtimov.ui.widgets

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerColors
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.evtimov.ui.R
import com.evtimov.ui.theme.LocalSbTypography
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Composable
fun LogoCircle(modifier: Modifier = Modifier) {
    Image(
        contentScale = ContentScale.Crop,
        modifier = modifier
            .clip(CircleShape),
        painter = painterResource(id = R.drawable.ic_hairstyle_3),
        contentDescription = "Splash icon."
    )
}

@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    text: String
) {
    androidx.compose.material.Card(
        modifier = modifier
            .fillMaxWidth()
            .height(125.dp)
            .padding(horizontal = 8.dp),
        backgroundColor = Color(0xFFffbc9c),
        shape = RoundedCornerShape(bottomEnd = 24.dp, bottomStart = 24.dp),
        elevation = 14.dp
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = LocalSbTypography.current.displaySmall,
                color = Color.White
            )
        }
    }
}

@Composable
fun LogoWithTitle(
    modifier: Modifier = Modifier,
    expanded: Boolean = true,
    titleText: String = "Sibella"
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(shape = RoundedCornerShape(40.dp))
            .wrapContentWidth()
            .height(100.dp)
            .background(Color(0xFFFEBC9A))
    ) {
        LogoCircle(
            modifier = Modifier
                .width(155.dp)
                .fillMaxHeight()
        )
        Text(
            modifier = Modifier
                .padding(horizontal = if (expanded) 10.dp else 0.dp)
                .animateContentSize(),
            text = titleText,
            color = Color(0xFFF1E8DC),
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp,
            fontFamily = FontFamily.Serif
        )
    }
}

@Composable
fun LoadingWidget(modifier: Modifier = Modifier) {
    Box(modifier = modifier.background(Color.Transparent), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            color = Color.Gray,
            modifier = Modifier.size(105.dp),
            strokeWidth = 8.dp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickView(
    modifier: Modifier = Modifier,
    selectedEventDateUi: String,
    selectedEventDate: LocalDateTime,
    onDateSelected: (LocalDateTime) -> Unit
) {
    val state = rememberDatePickerState().apply {
        displayMode = DisplayMode.Picker
        this.setSelection(selectedEventDate.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli())
    }
    val openDialog = remember { mutableStateOf(false) }
    PickerEvent(
        modifier = modifier,
        icon = R.drawable.ic_baseline_calendar_month_24,
        selectedUi = selectedEventDateUi,
        onClick = { openDialog.value = true }
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
                            Instant.ofEpochMilli(state.selectedDateMillis!!).atZone(
                                ZoneId.systemDefault()
                            ).toLocalDateTime()
                        )
                    }
                ) {
                    androidx.compose.material3.Text(text = stringResource(id = R.string.action_ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { openDialog.value = false }
                ) {
                    androidx.compose.material3.Text(text = stringResource(id = R.string.action_cancel))
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = Color.White,
                titleContentColor = Color(0xFFBF91FF)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickView(
    modifier: Modifier = Modifier,
    selectedEventTimeUi: String,
    selectedEventDate: LocalDateTime,
    onTimeSelected: (Int, Int) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = selectedEventDate.hour,
        initialMinute = selectedEventDate.minute,
        is24Hour = true
    )
    val openDialog = remember { mutableStateOf(false) }

    PickerEvent(
        modifier = modifier,
        icon = R.drawable.ic_baseline_access_time_24,
        selectedUi = selectedEventTimeUi,
        onClick = { openDialog.value = true }
    )
    if (openDialog.value) {
        AlertDialog(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(size = 24.dp)
                ),
            onDismissRequest = { openDialog.value = false }
        ) {
            Column(
                modifier = Modifier
                    .background(
                        color = Color.Transparent
                    )
                    .padding(top = 16.dp, start = 12.dp, end = 12.dp, bottom = 0.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TimePicker(
                    state = timePickerState,
                    layoutType = TimePickerLayoutType.Vertical,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = Color(0xFFFFD8C5),
                        clockDialSelectedContentColor = Color.White,
                        clockDialUnselectedContentColor = Color(0xFFFF3A68),
                        selectorColor = Color(0xFFF77594),
                        timeSelectorSelectedContainerColor = Color(0xFFFFD8C5),
                        timeSelectorUnselectedContainerColor = Color(0xFFEEECEC),
                        timeSelectorSelectedContentColor = Color(0xFFFF3A68),
                        timeSelectorUnselectedContentColor = Color(0xFFFF3A68),
                        periodSelectorBorderColor = Color.Red
                    )
                )
                Row(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(onClick = { openDialog.value = false }) {
                        androidx.compose.material3.Text(text = stringResource(id = R.string.action_cancel))
                    }
                    TextButton(
                        onClick = {
                            openDialog.value = false
                            onTimeSelected(timePickerState.hour, timePickerState.minute)
                        }
                    ) {
                        androidx.compose.material3.Text(text = stringResource(id = R.string.action_ok))
                    }
                }
            }
        }
    }
}

@Composable
private fun PickerEvent(
    modifier: Modifier = Modifier,
    selectedUi: String,
    icon: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(75.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(56.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 12.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFffbc9c)
        )
    ) {
        Row(
            modifier = Modifier.clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .size(55.dp)
                    .padding(start = 24.dp, top = 12.dp, bottom = 12.dp, end = 0.dp),
                painter = painterResource(id = icon),
                contentDescription = "Event picker",
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(24.dp))
            Card(
                modifier = modifier.fillMaxSize(),
                shape = RoundedCornerShape(56.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        textAlign = TextAlign.Center,
                        text = selectedUi,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        style = LocalSbTypography.current.headlineSmall,
                    )
                }
            }
        }
    }
}