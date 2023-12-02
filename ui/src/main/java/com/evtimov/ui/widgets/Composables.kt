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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.evtimov.ui.R
import com.evtimov.ui.theme.LocalSbTypography
import com.evtimov.ui.utils.openDatePicker
import com.evtimov.ui.utils.openTimePicker
import java.time.LocalDateTime

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

@Composable
fun DatePickerEvent(
    modifier: Modifier = Modifier,
    selectedEventDateUi: String,
    selectedEventDate: LocalDateTime,
    onDateSelected: (Int, Int, Int) -> Unit
) {
    val context = LocalContext.current
    PickerEvent(
        modifier = modifier,
        icon = R.drawable.ic_baseline_calendar_month_24,
        selectedUi = selectedEventDateUi
    ) {
        context.openDatePicker(
            currentEventDate = selectedEventDate,
            onDateSelected = onDateSelected
        )
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

    PickerEvent(
        modifier = modifier,
        icon = R.drawable.ic_baseline_access_time_24,
        selectedUi = selectedEventTimeUi
    ) {
        context.openTimePicker(
            currentEventDate = selectedEventDate,
            onTimeSelected = onTimeSelected
        )
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