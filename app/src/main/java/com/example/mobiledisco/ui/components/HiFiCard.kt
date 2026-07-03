package com.example.mobiledisco.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mobiledisco.ui.theme.HiFiColors
import com.example.mobiledisco.ui.theme.HiFiDimensions

@Composable
fun HiFiCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(HiFiDimensions.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = HiFiColors.BrushedMetal
        ),
        border = BorderStroke(
            HiFiDimensions.BorderWidth,
            MaterialTheme.colorScheme.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(HiFiDimensions.CardPadding),
            content = content
        )
    }
}
