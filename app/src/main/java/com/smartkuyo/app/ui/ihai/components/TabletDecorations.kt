package com.smartkuyo.app.ui.ihai.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartkuyo.app.domain.model.Religion
import com.smartkuyo.app.ui.theme.BuddhistPurple
import com.smartkuyo.app.ui.theme.ChristianBlue
import com.smartkuyo.app.ui.theme.GoldLight
import com.smartkuyo.app.ui.theme.ShintoRed

@Composable
fun TabletDecoration(
    religion: Religion,
    modifier: Modifier = Modifier
) {
    when (religion) {
        Religion.BUDDHISM -> {
            // 蓮の花アイコン
            Icon(
                imageVector = Icons.Default.Spa,
                contentDescription = "蓮の花",
                tint = BuddhistPurple,
                modifier = modifier.size(32.dp)
            )
        }
        Religion.CHRISTIANITY -> {
            // 十字架テキスト
            Text(
                text = "✝",
                color = ChristianBlue,
                fontSize = 28.sp,
                textAlign = TextAlign.Center,
                modifier = modifier
            )
        }
        Religion.SHINTO -> {
            // 鳥居テキスト
            Text(
                text = "⛩",
                color = ShintoRed,
                fontSize = 28.sp,
                textAlign = TextAlign.Center,
                modifier = modifier
            )
        }
        Religion.NON_RELIGIOUS -> {
            // シンプルな装飾
            Text(
                text = "◆",
                color = GoldLight,
                fontSize = 20.sp,
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Center,
                modifier = modifier
            )
        }
    }
}
