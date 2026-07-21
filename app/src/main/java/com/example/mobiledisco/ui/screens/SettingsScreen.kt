package com.example.mobiledisco.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mobiledisco.ui.components.HiFiCard
import com.example.mobiledisco.ui.theme.HiFiColors
import com.example.mobiledisco.ui.theme.HiFiDimensions

@Composable
fun SettingsScreen(
    onLimparHistorico: () -> Unit,
    onLimparFavoritos: () -> Unit,
    onZerarEstatisticas: () -> Unit,
    onExportarBiblioteca: () -> Unit,
    onImportarBiblioteca: () -> Unit,
    onBack: () -> Unit
) {
    var showConfirmDialog by remember { mutableStateOf<ConfirmType?>(null) }

    BackHandler {
        onBack()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = HiFiColors.Walnut900
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(HiFiDimensions.Medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = HiFiColors.Ivory
                    )
                }
                
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = HiFiColors.Copper,
                    modifier = Modifier.size(32.dp)
                )

                Text(
                    text = "CONFIGURAÇÕES",
                    style = MaterialTheme.typography.headlineSmall,
                    color = HiFiColors.Ivory,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Seção Reprodução
            SettingsSectionTitle("Reprodução")
            HiFiCard(modifier = Modifier.padding(horizontal = HiFiDimensions.Medium)) {
                Column(modifier = Modifier.padding(HiFiDimensions.Medium)) {
                    Text("Opções de áudio em breve...", color = HiFiColors.Sand, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(HiFiDimensions.Large))

            // Seção Biblioteca
            SettingsSectionTitle("Biblioteca")
            HiFiCard(modifier = Modifier.padding(horizontal = HiFiDimensions.Medium)) {
                Column(modifier = Modifier.padding(HiFiDimensions.Medium)) {
                    SettingsButton("Limpar Histórico") { showConfirmDialog = ConfirmType.HISTORY }
                    SettingsButton("Limpar Favoritos") { showConfirmDialog = ConfirmType.FAVORITES }
                    SettingsButton("Zerar Estatísticas") { showConfirmDialog = ConfirmType.STATS }
                }
            }

            Spacer(modifier = Modifier.height(HiFiDimensions.Large))

            // Seção Backup
            SettingsSectionTitle("Backup")
            HiFiCard(modifier = Modifier.padding(horizontal = HiFiDimensions.Medium)) {
                Column(modifier = Modifier.padding(HiFiDimensions.Medium)) {
                    SettingsButton("Exportar Biblioteca", onClick = onExportarBiblioteca)
                    SettingsButton("Importar Biblioteca", onClick = onImportarBiblioteca)
                }
            }

            Spacer(modifier = Modifier.height(HiFiDimensions.Large))

            // Seção Sobre
            SettingsSectionTitle("Sobre")
            HiFiCard(modifier = Modifier.padding(horizontal = HiFiDimensions.Medium)) {
                Column(modifier = Modifier.padding(HiFiDimensions.Medium)) {
                    Text("Mobile Disco", color = HiFiColors.Ivory, style = MaterialTheme.typography.titleMedium)
                    Text("Versão 1.0", color = HiFiColors.Sand, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Um player de música focado na experiência de áudio Hi-Fi e organização por álbuns.", color = HiFiColors.Sand, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(HiFiDimensions.ExtraLarge))
        }

        showConfirmDialog?.let { type ->
            AlertDialog(
                onDismissRequest = { showConfirmDialog = null },
                containerColor = HiFiColors.Espresso,
                title = { Text("Confirmar", color = HiFiColors.Ivory) },
                text = { 
                    Text(
                        when (type) {
                            ConfirmType.HISTORY -> "Tem certeza que deseja limpar o histórico?"
                            ConfirmType.FAVORITES -> "Tem certeza que deseja limpar os favoritos?"
                            ConfirmType.STATS -> "Tem certeza que deseja zerar as estatísticas?"
                        },
                        color = HiFiColors.Sand
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        when (type) {
                            ConfirmType.HISTORY -> onLimparHistorico()
                            ConfirmType.FAVORITES -> onLimparFavoritos()
                            ConfirmType.STATS -> onZerarEstatisticas()
                        }
                        showConfirmDialog = null
                    }) {
                        Text("Confirmar", color = HiFiColors.Copper)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = null }) {
                        Text("Cancelar", color = HiFiColors.Sand)
                    }
                }
            )
        }
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = HiFiColors.Sand,
        letterSpacing = 2.sp,
        modifier = Modifier.padding(horizontal = HiFiDimensions.Medium, vertical = HiFiDimensions.Small)
    )
}

@Composable
fun SettingsButton(label: String, enabled: Boolean = true, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        Text(
            text = label,
            color = if (enabled) HiFiColors.Ivory else HiFiColors.SoftBrown,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

enum class ConfirmType {
    HISTORY, FAVORITES, STATS
}
