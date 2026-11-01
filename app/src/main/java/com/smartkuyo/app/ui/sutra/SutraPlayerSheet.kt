package com.smartkuyo.app.ui.sutra

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.smartkuyo.app.R
import com.smartkuyo.app.domain.model.Religion
import com.smartkuyo.app.domain.model.SutraType
import com.smartkuyo.app.ui.theme.GoldDark
import com.smartkuyo.app.ui.theme.GoldLight

@Composable
fun SutraPlayerSheet(
    modifier: Modifier = Modifier,
    viewModel: SutraPlayerViewModel = hiltViewModel()
) {
    // uiStateを購読してgetDeceasedUseCaseのFlowを起動し、お経リストを読み込む
    @Suppress("UNUSED_VARIABLE")
    val trigger by viewModel.uiState.collectAsState()
    val uiState by viewModel.playerState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .padding(24.dp)
    ) {
        val sheetTitle = when (uiState.denomination?.religion) {
            Religion.BUDDHISM -> "御経"
            Religion.CHRISTIANITY -> "お祈り・讃美歌"
            Religion.SHINTO -> "祝詞"
            else -> "御経・お祈り"
        }
        Text(
            text = sheetTitle,
            color = GoldLight,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        uiState.denomination?.let { denomination ->
            Text(
                text = denomination.displayNameJa,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 再生コントロール
        uiState.selectedSutra?.let { sutra ->
            val hasAudio = hasAudioResource(sutra)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        GoldLight.copy(alpha = 0.1f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = GoldLight,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = sutra.displayNameJa,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = sutra.description,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                    if (hasAudio) {
                        IconButton(onClick = { viewModel.togglePlayback() }) {
                            Icon(
                                imageVector = if (uiState.isPlaying) Icons.Default.Pause
                                else Icons.Default.PlayArrow,
                                contentDescription = if (uiState.isPlaying) "一時停止" else "再生",
                                tint = GoldLight,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        IconButton(onClick = { viewModel.stop() }) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "停止",
                                tint = GoldDark,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.MusicOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // 再生プログレスバー
                if (hasAudio && uiState.duration > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = {
                            uiState.currentPosition.toFloat() / uiState.duration.toFloat()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = GoldLight,
                        trackColor = GoldLight.copy(alpha = 0.2f),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatTime(uiState.currentPosition),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                        Text(
                            text = formatTime(uiState.duration),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }
                }

                if (!hasAudio) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "音声データは今後追加予定",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // お経リスト
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.availableSutras) { sutra ->
                SutraListItem(
                    sutra = sutra,
                    isSelected = uiState.selectedSutra == sutra,
                    hasAudio = hasAudioResource(sutra),
                    onClick = { viewModel.selectSutra(sutra) }
                )
            }
        }
    }
}

private fun hasAudioResource(sutra: SutraType): Boolean {
    return when (sutra) {
        SutraType.HANNYA_SHINGYO,
        SutraType.KOMYO_SHINGON,
        SutraType.MITSUGONINHOTSUROZANGEMON,
        SutraType.SHUSHOGI,
        SutraType.SHUSHOGI_PART2,
        SutraType.DAIHI_SHU,
        SutraType.JODOJI_SHINGYO,
        SutraType.AVE_MARIA,
        SutraType.AMAZING_GRACE -> true
        else -> false
    }
}

private fun formatTime(ms: Int): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

@Composable
private fun SutraListItem(
    sutra: SutraType,
    isSelected: Boolean,
    hasAudio: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) GoldLight.copy(alpha = 0.15f)
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (hasAudio) Icons.Default.MusicNote else Icons.Default.MusicOff,
            contentDescription = null,
            tint = if (isSelected) GoldLight
                   else if (hasAudio) MaterialTheme.colorScheme.onSurfaceVariant
                   else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = sutra.displayNameJa,
                color = if (isSelected) GoldLight else MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            Text(
                text = sutra.description,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
        if (hasAudio) {
            Text(
                text = "♪",
                color = GoldLight.copy(alpha = 0.5f),
                fontSize = 14.sp
            )
        }
    }
}
