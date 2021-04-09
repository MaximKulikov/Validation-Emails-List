package ru.gavarent

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun GavarentTheme(content: @Composable () -> Unit) {
   MaterialTheme(
      typography = gavarentTypography(),
      content = content
   )
}

fun gavarentTypography(): Typography {
   return Typography(
      body1 = TextStyle(
         fontWeight = FontWeight.Normal,
         fontSize = 14.sp,
         letterSpacing = 0.5.sp
      ),
      body2 = TextStyle(
         fontWeight = FontWeight.Medium,
         fontSize = 12.sp,
      )
   )
}

