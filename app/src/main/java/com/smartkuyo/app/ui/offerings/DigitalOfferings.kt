package com.smartkuyo.app.ui.offerings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Offering(
    val emoji: String,
    val name: String
)

val defaultOfferings = listOf(
    Offering("🌸", "花"),
    Offering("🕯️", "灯明"),
    Offering("🍵", "お茶"),
    Offering("🍚", "ご飯"),
    Offering("🍡", "お団子"),
    Offering("🍎", "果物"),
    Offering("🧄", "お菓子"),
    Offering("💐", "花束")
)

@Composable
fun DigitalOfferings(
    selectedOfferings: Set<String>,
    onToggleOffering: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "お供え物",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            defaultOfferings.take(4).forEach { offering ->
                OfferingItem(
                    offering = offering,
                    isSelected = offering.name in selectedOfferings,
                    onClick = { onToggleOffering(offering.name) }
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            defaultOfferings.drop(4).forEach { offering ->
                OfferingItem(
                    offering = offering,
                    isSelected = offering.name in selectedOfferings,
                    onClick = { onToggleOffering(offering.name) }
                )
            }
        }
    }
}

@Composable
private fun OfferingItem(
    offering: Offering,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(4.dp)
    ) {
        androidx.compose.material3.IconButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp)
        ) {
            Text(
                text = offering.emoji,
                fontSize = if (isSelected) 32.sp else 24.sp,
                textAlign = TextAlign.Center
            )
        }
        Text(
            text = offering.name,
            color = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp
        )
    }
}

@Composable
fun OfferingsDisplay(
    selectedOfferings: Set<String>,
    modifier: Modifier = Modifier
) {
    if (selectedOfferings.isEmpty()) return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        selectedOfferings.forEach { name ->
            defaultOfferings.find { it.name == name }?.let { offering ->
                Text(
                    text = offering.emoji,
                    fontSize = 28.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}
