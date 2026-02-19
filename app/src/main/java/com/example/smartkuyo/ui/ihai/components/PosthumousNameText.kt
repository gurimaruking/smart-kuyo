package com.example.smartkuyo.ui.ihai.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartkuyo.ui.theme.GoldLight

@Composable
fun PosthumousNameText(
    posthumousName: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 36.sp
) {
    // 縦書き表示: 各文字を縦に並べる
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        posthumousName.forEach { char ->
            Text(
                text = char.toString(),
                color = GoldLight,
                fontSize = fontSize,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}
