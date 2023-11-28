package com.evtimov.ui.widgets

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.evtimov.ui.R

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
fun LogoWithTitle(modifier: Modifier = Modifier, expanded: Boolean = true, titleText: String = "Sibella") {
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
    Box(modifier = modifier.background(Color(0x669C9C9E)), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            color = Color.Yellow,
            modifier = Modifier.size(75.dp),
            strokeWidth = 6.dp
        )
    }
}