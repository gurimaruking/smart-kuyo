package com.smartkuyo.app.ui.setup

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
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.smartkuyo.app.domain.model.Denomination
import com.smartkuyo.app.domain.model.Religion
import com.smartkuyo.app.ui.theme.GoldLight

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SetupScreen(
    onSetupComplete: () -> Unit,
    viewModel: SetupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            viewModel.addPhotoUri(it.toString())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            // edge-to-edge なので、ステータスバー・ナビバー・キーボードの分を空ける
            .safeDrawingPadding()
            .padding(24.dp)
    ) {
        // プログレスバー
        Text(
            text = "初期設定 (${uiState.currentStep + 1}/${uiState.totalSteps})",
            color = GoldLight,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { (uiState.currentStep + 1).toFloat() / uiState.totalSteps },
            modifier = Modifier.fillMaxWidth(),
            color = GoldLight,
        )
        Spacer(modifier = Modifier.height(24.dp))

        // ステップ内容
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            when (uiState.currentStep) {
                0 -> ReligionStep(uiState, viewModel)
                1 -> DeceasedInfoStep(uiState, viewModel)
                2 -> PhotoStep(uiState, viewModel) { photoPickerLauncher.launch(arrayOf("image/*")) }
                3 -> DisplaySettingsStep(uiState, viewModel)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ナビゲーションボタン
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (uiState.currentStep > 0) {
                OutlinedButton(onClick = { viewModel.previousStep() }) {
                    Text("戻る")
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            if (uiState.currentStep < uiState.totalSteps - 1) {
                Button(
                    onClick = { viewModel.nextStep() },
                    enabled = uiState.canProceed,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldLight)
                ) {
                    Text("次へ")
                }
            } else {
                Button(
                    onClick = { viewModel.completeSetup(onSetupComplete) },
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldLight)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    } else {
                        Text("完了")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ReligionStep(uiState: SetupUiState, viewModel: SetupViewModel) {
    Text(
        text = "宗教・宗派を選択してください",
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 16.sp
    )
    Spacer(modifier = Modifier.height(16.dp))

    // 宗教選択
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Religion.entries.forEach { religion ->
            val isSelected = uiState.selectedReligion == religion
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isSelected) GoldLight.copy(alpha = 0.2f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) GoldLight else MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { viewModel.selectReligion(religion) }
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = religion.displayNameJa,
                    color = if (isSelected) GoldLight else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }

    // 宗派選択
    uiState.selectedReligion?.let { religion ->
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "宗派を選択",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Denomination.getByReligion(religion).forEach { denomination ->
                val isSelected = uiState.selectedDenomination == denomination
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected) GoldLight.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) GoldLight else MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { viewModel.selectDenomination(denomination) }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = denomination.displayNameJa,
                        color = if (isSelected) GoldLight else MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeceasedInfoStep(uiState: SetupUiState, viewModel: SetupViewModel) {
    var yearText by remember(uiState.dateOfDeath) { mutableStateOf(uiState.dateOfDeath.year.toString()) }
    var monthText by remember(uiState.dateOfDeath) { mutableStateOf(uiState.dateOfDeath.monthValue.toString()) }
    var dayText by remember(uiState.dateOfDeath) { mutableStateOf(uiState.dateOfDeath.dayOfMonth.toString()) }
    var showDatePicker by remember { mutableStateOf(false) }

    Text(
        text = "故人の情報を入力してください",
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 16.sp
    )
    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = uiState.familyName,
        onValueChange = { viewModel.updateFamilyName(it) },
        label = { Text("姓") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
        value = uiState.givenName,
        onValueChange = { viewModel.updateGivenName(it) },
        label = { Text("名") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
    Spacer(modifier = Modifier.height(12.dp))

    val posthumousLabel = uiState.selectedDenomination?.posthumousNameLabel ?: "戒名"
    if (posthumousLabel.isNotBlank()) {
        OutlinedTextField(
            value = uiState.posthumousName,
            onValueChange = { viewModel.updatePosthumousName(it) },
            label = { Text(posthumousLabel) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))
    }

    // 没日（命日）- 年・月・日を個別入力
    Text(
        text = "没日（命日）",
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 12.sp
    )
    Spacer(modifier = Modifier.height(4.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = yearText,
            onValueChange = { input ->
                yearText = input.filter { it.isDigit() }
                updateDateFromFields(yearText, monthText, dayText, viewModel)
            },
            label = { Text("年") },
            modifier = Modifier.weight(1.2f),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = monthText,
            onValueChange = { input ->
                monthText = input.filter { it.isDigit() }
                updateDateFromFields(yearText, monthText, dayText, viewModel)
            },
            label = { Text("月") },
            modifier = Modifier.weight(0.8f),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = dayText,
            onValueChange = { input ->
                dayText = input.filter { it.isDigit() }
                updateDateFromFields(yearText, monthText, dayText, viewModel)
            },
            label = { Text("日") },
            modifier = Modifier.weight(0.8f),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        // 近代日付用カレンダーボタン
        IconButton(onClick = { showDatePicker = true }) {
            Icon(Icons.Default.CalendarMonth, contentDescription = "カレンダーから選択")
        }
    }
    Spacer(modifier = Modifier.height(12.dp))

    OutlinedTextField(
        value = uiState.ageAtDeath,
        onValueChange = { viewModel.updateAgeAtDeath(it) },
        label = { Text("享年") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )

    // 近代日付用ピッカーダイアログ
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = try {
                uiState.dateOfDeath
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
            } catch (_: Exception) { null }
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
                            viewModel.updateDateOfDeath(selectedDate)
                            yearText = selectedDate.year.toString()
                            monthText = selectedDate.monthValue.toString()
                            dayText = selectedDate.dayOfMonth.toString()
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

private fun updateDateFromFields(yearStr: String, monthStr: String, dayStr: String, viewModel: SetupViewModel) {
    val year = yearStr.toIntOrNull() ?: return
    val month = monthStr.toIntOrNull()?.coerceIn(1, 12) ?: return
    val day = dayStr.toIntOrNull()?.coerceIn(1, 31) ?: return
    if (year < 0) return
    try {
        val date = LocalDate.of(year, month, day.coerceAtMost(LocalDate.of(year, month, 1).lengthOfMonth()))
        viewModel.updateDateOfDeath(date)
    } catch (_: Exception) { }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PhotoStep(
    uiState: SetupUiState,
    viewModel: SetupViewModel,
    onAddPhoto: () -> Unit
) {
    Text(
        text = "遺影用の写真を選択してください",
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 16.sp
    )
    Text(
        text = "（後から設定画面で追加・変更できます）",
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 12.sp
    )
    Spacer(modifier = Modifier.height(16.dp))

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        uiState.photoUris.forEach { uri ->
            Box(modifier = Modifier.size(100.dp)) {
                AsyncImage(
                    model = uri,
                    contentDescription = "遺影写真",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                )
                IconButton(
                    onClick = { viewModel.removePhotoUri(uri) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "削除",
                        tint = GoldLight
                    )
                }
            }
        }

        // 追加ボタン
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(2.dp, GoldLight.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .clickable { onAddPhoto() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "写真を追加",
                tint = GoldLight,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun DisplaySettingsStep(uiState: SetupUiState, viewModel: SetupViewModel) {
    Text(
        text = "表示設定",
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 16.sp
    )
    Spacer(modifier = Modifier.height(16.dp))

    SettingSwitch(
        label = "自動切替",
        description = "位牌モードと遺影モードを自動で切り替えます",
        checked = uiState.autoSwitch,
        onCheckedChange = { viewModel.updateAutoSwitch(it) }
    )

    Spacer(modifier = Modifier.height(12.dp))

    SettingSwitch(
        label = "夜間非表示",
        description = "22:00〜6:00に画面を暗くします",
        checked = uiState.nightMode,
        onCheckedChange = { viewModel.updateNightMode(it) }
    )

    Spacer(modifier = Modifier.height(12.dp))

    SettingSwitch(
        label = "モノクロ遺影",
        description = "遺影写真をモノクロで表示します",
        checked = uiState.useMonochrome,
        onCheckedChange = { viewModel.updateUseMonochrome(it) }
    )
}

@Composable
private fun SettingSwitch(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
            Text(
                text = description,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
