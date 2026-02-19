package com.example.smartkuyo.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.smartkuyo.domain.model.TabletStyle
import com.example.smartkuyo.ui.theme.GoldLight
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onResetToSetup: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showResetDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("yyyy年M月d日") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            uiState.deceased?.let { deceased ->
                viewModel.addPhoto(deceased.id, it.toString(), uiState.photos.size)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("設定") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 故人情報セクション
            uiState.deceased?.let { deceased ->
                SettingsSection(title = "故人情報") {
                    var familyName by remember(deceased.id) { mutableStateOf(deceased.familyName) }
                    var givenName by remember(deceased.id) { mutableStateOf(deceased.givenName) }
                    var posthumousName by remember(deceased.id) {
                        mutableStateOf(deceased.posthumousName ?: "")
                    }

                    OutlinedTextField(
                        value = familyName,
                        onValueChange = {
                            familyName = it
                            viewModel.updateDeceased(deceased.copy(familyName = it))
                        },
                        label = { Text("姓") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = givenName,
                        onValueChange = {
                            givenName = it
                            viewModel.updateDeceased(deceased.copy(givenName = it))
                        },
                        label = { Text("名") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = posthumousName,
                        onValueChange = {
                            posthumousName = it
                            viewModel.updateDeceased(
                                deceased.copy(posthumousName = it.ifBlank { null })
                            )
                        },
                        label = { Text(deceased.denomination.posthumousNameLabel.ifBlank { "戒名" }) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // 没日（命日）
                    OutlinedTextField(
                        value = deceased.dateOfDeath.format(dateFormatter),
                        onValueChange = {},
                        label = { Text("没日（命日）") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true },
                        readOnly = true,
                        enabled = false,
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = "日付を選択")
                            }
                        }
                    )
                }
            }

            // 位牌デザインセクション
            SettingsSection(title = "位牌デザイン") {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TabletStyle.entries.forEach { style ->
                        val isSelected = uiState.preferences.tabletStyle == style
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) GoldLight.copy(alpha = 0.2f)
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) GoldLight
                                    else MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.updateTabletStyle(style) }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = style.displayNameJa,
                                color = if (isSelected) GoldLight
                                else MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // 写真管理セクション
            SettingsSection(title = "遺影写真") {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.photos.forEach { photo ->
                        Box(modifier = Modifier.size(80.dp)) {
                            AsyncImage(
                                model = photo.uri,
                                contentDescription = "遺影写真",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            IconButton(
                                onClick = { viewModel.deletePhoto(photo) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(20.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "削除",
                                    tint = GoldLight,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                2.dp,
                                GoldLight.copy(alpha = 0.5f),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { photoPickerLauncher.launch(arrayOf("image/*")) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "追加", tint = GoldLight)
                    }
                }
            }

            // 表示設定セクション
            SettingsSection(title = "表示設定") {
                SettingSwitchRow(
                    label = "自動切替",
                    checked = uiState.preferences.autoSwitchEnabled,
                    onCheckedChange = { viewModel.updateAutoSwitch(it) }
                )

                if (uiState.preferences.autoSwitchEnabled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    SliderSetting(
                        label = "位牌表示時間: ${uiState.preferences.ihaiDisplayMinutes}分",
                        value = uiState.preferences.ihaiDisplayMinutes.toFloat(),
                        range = 5f..120f,
                        onValueChange = { viewModel.updateIhaiMinutes(it.toInt()) }
                    )
                    SliderSetting(
                        label = "遺影表示時間: ${uiState.preferences.ieiDisplayMinutes}分",
                        value = uiState.preferences.ieiDisplayMinutes.toFloat(),
                        range = 5f..120f,
                        onValueChange = { viewModel.updateIeiMinutes(it.toInt()) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                SliderSetting(
                    label = "スライドショー間隔: ${uiState.preferences.slideshowIntervalSeconds}秒",
                    value = uiState.preferences.slideshowIntervalSeconds.toFloat(),
                    range = 3f..60f,
                    onValueChange = { viewModel.updateSlideshowInterval(it.toInt()) }
                )

                Spacer(modifier = Modifier.height(8.dp))
                SettingSwitchRow(
                    label = "モノクロ遺影",
                    checked = uiState.preferences.useMonochrome,
                    onCheckedChange = { viewModel.updateUseMonochrome(it) }
                )

                Spacer(modifier = Modifier.height(8.dp))
                SettingSwitchRow(
                    label = "画面常時表示",
                    checked = uiState.preferences.keepScreenOn,
                    onCheckedChange = { viewModel.updateKeepScreenOn(it) }
                )

                Spacer(modifier = Modifier.height(8.dp))
                SettingSwitchRow(
                    label = "全画面モード（供養道具を非表示）",
                    checked = uiState.preferences.fullScreenDisplay,
                    onCheckedChange = { viewModel.updateFullScreenDisplay(it) }
                )
            }

            // 夜間モードセクション
            SettingsSection(title = "夜間モード") {
                SettingSwitchRow(
                    label = "夜間非表示",
                    checked = uiState.preferences.nightModeEnabled,
                    onCheckedChange = { viewModel.updateNightMode(it) }
                )
                if (uiState.preferences.nightModeEnabled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "開始: ${uiState.preferences.nightModeStart}  終了: ${uiState.preferences.nightModeEnd}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            }

            // リセットセクション
            SettingsSection(title = "データ管理") {
                Text(
                    text = "すべてのデータを削除し、初期設定からやり直します。",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { showResetDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFE53935)
                    )
                ) {
                    Text("アプリをリセット")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // リセット確認ダイアログ
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("リセット確認") },
            text = {
                Text("すべてのデータ（故人情報・写真・設定）が削除されます。\nこの操作は取り消せません。")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetDialog = false
                        viewModel.resetApp { onResetToSetup() }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935)
                    )
                ) {
                    Text("リセットする")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("キャンセル")
                }
            }
        )
    }

    // 没日日付ピッカーダイアログ
    if (showDatePicker) {
        uiState.deceased?.let { deceased ->
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = deceased.dateOfDeath
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
            )
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val selectedDate = Instant.ofEpochMilli(millis)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                                viewModel.updateDeceased(deceased.copy(dateOfDeath = selectedDate))
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("決定")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("キャンセル")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                color = GoldLight,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun SettingSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SliderSetting(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
