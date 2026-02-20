package com.example.smartkuyo.ui.ihai

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartkuyo.ui.ihai.components.DeathDateText
import com.example.smartkuyo.ui.ihai.components.PosthumousNameText
import com.example.smartkuyo.ui.ihai.components.TabletDecoration
import com.example.smartkuyo.ui.ihai.components.TabletFrame
import com.example.smartkuyo.ui.theme.CreamWhite
import com.example.smartkuyo.ui.theme.GoldDark

@Composable
fun IhaiScreen(
    modifier: Modifier = Modifier,
    viewModel: IhaiViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val deceased = uiState.deceased ?: return

    TabletFrame(
        style = uiState.tabletStyle,
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左側: 命日（縦書き）
            DeathDateText(
                dateOfDeath = deceased.dateOfDeath
            )

            Spacer(modifier = Modifier.width(16.dp))

            // 中央: 宗派装飾 + 戒名/法名 + 俗名
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                TabletDecoration(religion = deceased.denomination.religion)

                Spacer(modifier = Modifier.height(16.dp))

                if (!deceased.posthumousName.isNullOrBlank()) {
                    PosthumousNameText(
                        posthumousName = deceased.posthumousName
                    )
                } else {
                    // 戒名がない場合は俗名を表示
                    PosthumousNameText(
                        posthumousName = deceased.fullName,
                        fontSize = 28.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 俗名（横書き）
                if (!deceased.posthumousName.isNullOrBlank()) {
                    Text(
                        text = "俗名 ${deceased.fullName}",
                        color = GoldDark,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Serif,
                        textAlign = TextAlign.Center
                    )
                }

                // 享年
                deceased.ageAtDeath?.let { age ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "享年${age}歳",
                        color = CreamWhite.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Serif,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 右側: 宗派名（縦書き）
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                deceased.denomination.displayNameJa.forEach { char ->
                    Text(
                        text = char.toString(),
                        color = GoldDark.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Serif
                    )
                }
            }
        }
    }
}
