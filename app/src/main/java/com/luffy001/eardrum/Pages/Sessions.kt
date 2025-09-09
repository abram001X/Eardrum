package com.luffy001.eardrum.Pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun SessionsPages(pager: PagerState) {

    Row(
        Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.White)
    ) {
        repeat(pager.pageCount) { it ->
            BoxSession(it, pager.currentPage == it, pager)
        }
    }
}

@Composable
fun BoxSession(session: Int, isSession: Boolean, pagerState: PagerState) {
    val coroutine = rememberCoroutineScope()
    val totalWidth = LocalConfiguration.current.screenWidthDp.dp
    val modifier = if(!isSession) {
        Modifier
            .width(totalWidth * 0.5f)
            .fillMaxHeight()
            .clickable(onClick = { coroutine.launch{ pagerState.scrollToPage(session) } })
    }else {
        Modifier
            .width(totalWidth * 0.5f)
            .fillMaxHeight()
            .clickable(onClick = {coroutine.launch{ pagerState.scrollToPage(session) }})
            .drawBehind {
                drawLine(
                    color = Color.Blue,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 2.dp.toPx()
                )
            }
    }
    val content = Alignment.Center
    val colorCurrent = if(isSession) Color.Blue else Color.Black
    val sessionString = if (session == 0) "Canciones" else "Descargar"


    Box(modifier = modifier, contentAlignment = content) {
        Text(text = sessionString, fontSize = 22.sp, color = colorCurrent)
    }
}