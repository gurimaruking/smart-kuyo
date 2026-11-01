package com.smartkuyo.app.ui.ihai

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartkuyo.app.ui.ihai.components.PosthumousNameText
import com.smartkuyo.app.ui.ihai.components.TabletFrame

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
        // 戒名/法名のみ中央に表示（実際の位牌と同じ）
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (!deceased.posthumousName.isNullOrBlank()) {
                PosthumousNameText(
                    posthumousName = deceased.posthumousName,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // 戒名がない場合は俗名を表示
                PosthumousNameText(
                    posthumousName = deceased.fullName,
                    fontSize = 28.sp,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
