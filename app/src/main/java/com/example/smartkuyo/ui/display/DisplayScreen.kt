package com.example.smartkuyo.ui.display

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartkuyo.MainActivity
import com.example.smartkuyo.domain.model.DisplayMode
import com.example.smartkuyo.ui.bell.BellButton
import com.example.smartkuyo.ui.bell.BellSoundManager
import com.example.smartkuyo.ui.candle.CandleEffect
import com.example.smartkuyo.ui.common.FullScreenMode
import com.example.smartkuyo.ui.common.KeepScreenOn
import com.example.smartkuyo.ui.common.NightModeOverlay
import com.example.smartkuyo.ui.counter.MemorialCounterSheet
import com.example.smartkuyo.ui.ihai.IhaiScreen
import com.example.smartkuyo.ui.iei.IeiScreen
import com.example.smartkuyo.ui.incense.IncenseEffect
import com.example.smartkuyo.ui.sutra.SutraPlayerSheet
import com.example.smartkuyo.ui.theme.GoldLight
import kotlinx.coroutines.delay

enum class BottomSheetContent {
    NONE, SUTRA, MEMORIAL_COUNTER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayScreen(
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DisplayViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showOverlay by remember { mutableStateOf(false) }
    var bottomSheetContent by remember { mutableStateOf(BottomSheetContent.NONE) }
    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current
    val activity = context as? MainActivity
    var isScreenPinned by remember { mutableStateOf(activity?.isScreenPinned() ?: false) }

    // おりんのSoundPoolを初期化（Compositionから独立）
    BellSoundManager.init(context)

    if (uiState.keepScreenOn) {
        KeepScreenOn()
    }
    FullScreenMode()

    // 画面固定状態を定期的にチェック
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            isScreenPinned = activity?.isScreenPinned() ?: false
        }
    }

    // 8秒後にオーバーレイを非表示
    LaunchedEffect(showOverlay) {
        if (showOverlay) {
            delay(8000)
            showOverlay = false
        }
    }

    val isActiveMode = uiState.currentMode != DisplayMode.OFF
    val showTools = isActiveMode && !uiState.fullScreenDisplay

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(uiState.currentMode) {
                detectTapGestures { offset ->
                    val centerX = size.width / 2f
                    val centerY = size.height / 2f
                    val dx = offset.x - centerX
                    val dy = offset.y - centerY
                    val radius = size.width.coerceAtMost(size.height) * 0.2f
                    val isCenterTap = (dx * dx + dy * dy) < (radius * radius)
                    if (isCenterTap && isActiveMode) {
                        viewModel.toggleMode()
                    } else {
                        showOverlay = !showOverlay
                    }
                }
            }
    ) {
        // メインコンテンツ
        when (uiState.currentMode) {
            DisplayMode.IHAI -> IhaiScreen()
            DisplayMode.IEI -> IeiScreen()
            DisplayMode.OFF -> NightModeOverlay()
        }

        // === 常時表示の供養ツール（fullScreenDisplayがfalseの場合のみ） ===

        // ろうそく（左右、位牌・遺影共通、タップ式）
        CandleEffect(
            visible = showTools,
            modifier = Modifier.fillMaxSize()
        )

        // お線香（中央下、位牌・遺影共通、タップ式）
        IncenseEffect(
            visible = showTools,
            modifier = Modifier.fillMaxSize()
        )

        // おりん（右下に常時表示）
        if (showTools) {
            BellButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 16.dp)
            )
        }

        // ===== オーバーレイメニュー（タップで表示） =====
        AnimatedVisibility(
            visible = showOverlay,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                // 上部: 画面切替・設定・固定
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(top = 48.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // 位牌モード
                    IconButton(
                        onClick = {
                            viewModel.switchMode(DisplayMode.IHAI)
                            showOverlay = false
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                if (uiState.currentMode == DisplayMode.IHAI)
                                    GoldLight.copy(alpha = 0.3f)
                                else Color.Transparent,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.TableChart,
                            contentDescription = "位牌モード",
                            tint = GoldLight,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // 遺影モード
                    IconButton(
                        onClick = {
                            viewModel.switchMode(DisplayMode.IEI)
                            showOverlay = false
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                if (uiState.currentMode == DisplayMode.IEI)
                                    GoldLight.copy(alpha = 0.3f)
                                else Color.Transparent,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "遺影モード",
                            tint = GoldLight,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // 設定
                    IconButton(
                        onClick = {
                            showOverlay = false
                            onNavigateToSettings()
                        },
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "設定",
                            tint = GoldLight,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // 画面固定
                    IconButton(
                        onClick = {
                            if (isScreenPinned) {
                                activity?.stopScreenPin()
                            } else {
                                activity?.startScreenPin()
                            }
                            isScreenPinned = activity?.isScreenPinned() ?: false
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                if (isScreenPinned) GoldLight.copy(alpha = 0.3f)
                                else Color.Transparent,
                                CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = if (isScreenPinned) Icons.Default.Lock
                                          else Icons.Default.LockOpen,
                            contentDescription = if (isScreenPinned) "画面固定解除"
                                                  else "画面を固定",
                            tint = if (isScreenPinned) Color(0xFFFF9800) else GoldLight,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // 画面固定中インジケーター
                if (isScreenPinned) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 112.dp)
                            .background(
                                Color(0xFF1B5E20).copy(alpha = 0.8f),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "画面固定中",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // 下部: お経・法要カレンダー
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 48.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // お経
                    IconButton(
                        onClick = {
                            bottomSheetContent = BottomSheetContent.SUTRA
                            showOverlay = false
                        },
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "御経",
                            tint = GoldLight,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // 法要カレンダー
                    IconButton(
                        onClick = {
                            bottomSheetContent = BottomSheetContent.MEMORIAL_COUNTER
                            showOverlay = false
                        },
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "法要カレンダー",
                            tint = GoldLight,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        // BottomSheet
        if (bottomSheetContent != BottomSheetContent.NONE) {
            ModalBottomSheet(
                onDismissRequest = { bottomSheetContent = BottomSheetContent.NONE },
                sheetState = sheetState
            ) {
                when (bottomSheetContent) {
                    BottomSheetContent.SUTRA -> SutraPlayerSheet()
                    BottomSheetContent.MEMORIAL_COUNTER -> MemorialCounterSheet()
                    BottomSheetContent.NONE -> {}
                }
            }
        }
    }
}
