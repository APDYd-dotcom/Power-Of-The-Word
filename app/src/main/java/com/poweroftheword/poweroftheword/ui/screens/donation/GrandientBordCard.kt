package com.poweroftheword.poweroftheword.ui.screens.donation

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

//@Composable
//fun GradientBorderCard(
//    borderColor: Color,
//    content: @Composable ColumnScope.() -> Unit
//) {
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(
//                brush = Brush.linearGradient(
//                    listOf(borderColor, borderColor.copy(alpha = 0.3f))
//                ),
//                shape = RoundedCornerShape(18.dp)
//            )
//            .padding(1.5.dp) // border thickness
//    ) {
//        Column(
//            modifier = Modifier
//                .background(Color(0xFF1E2635), RoundedCornerShape(18.dp))
//                .padding(16.dp)
//        ) {
//            content()
//        }
//    }
//}