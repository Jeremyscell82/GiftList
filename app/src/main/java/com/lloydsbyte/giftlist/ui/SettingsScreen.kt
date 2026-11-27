package com.lloydsbyte.giftlist.ui

import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    listNames: List<String>,
    onBack: () -> Unit,
    onReset: () -> Unit,
    onAddList: (String) -> Unit,
    onRenameList: (String, String) -> Unit,
    onDeleteList: (String) -> Unit,
) {
    val context = LocalContext.current
    var confirmReset by remember { mutableStateOf(false) }
    var showAddList by remember { mutableStateOf(false) }
    var renameTarget by remember { mutableStateOf<String?>(null) }
    var deleteTarget by remember { mutableStateOf<String?>(null) }

    val version = remember {
        runCatching {
            context.packageManager
                .getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0L))
                .versionName
        }.getOrNull() ?: "1.0"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.SemiBold) },
                actions = {
                    TextButton(onClick = onBack) { Text("Done") }
                },
            )
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            Column {
                Spacer(Modifier.height(8.dp))

                // ── Lists card ──
                SettingsCard(title = "Lists", icon = Icons.Filled.Inventory) {
                    if (listNames.isEmpty()) {
                        Text(
                            "No lists yet. Tap “Add list” to create one.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    } else {
                        listNames.forEach { name ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Dot()
                                    Spacer(Modifier.size(10.dp))
                                    Text(name, style = MaterialTheme.typography.bodyLarge)
                                }
                                Row {
                                    IconButton(onClick = { renameTarget = name }) {
                                        Icon(Icons.Filled.Edit, contentDescription = "Rename $name")
                                    }
                                    IconButton(onClick = { deleteTarget = name }) {
                                        Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete $name", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Row {
                        TextButton(onClick = { showAddList = true }) {
                            Icon(Icons.Outlined.AddCircle, contentDescription = null)
                            Spacer(Modifier.size(6.dp))
                            Text("Add list")
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ── Data card ──
                SettingsCard(title = "Data", icon = Icons.Filled.Storage) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("Reset all gifts", style = MaterialTheme.typography.bodyLarge)
                            Spacer(Modifier.height(2.dp))
                            Text(
                                "Removes every item from all lists.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        TextButton(onClick = { confirmReset = true }) {
                            Text("Reset", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            Text(
                "Gift List · Version $version",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }

    // region Dialogs
    if (showAddList) {
        ListNameDialog(
            title = "Add list",
            initial = "",
            confirmLabel = "Add",
            onConfirm = { onAddList(it); showAddList = false },
            onDismiss = { showAddList = false },
        )
    }
    renameTarget?.let { target ->
        ListNameDialog(
            title = "Rename list",
            initial = target,
            confirmLabel = "Save",
            onConfirm = { onRenameList(target, it); renameTarget = null },
            onDismiss = { renameTarget = null },
        )
    }
    deleteTarget?.let { target ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Delete \"$target\"?") },
            text = { Text("This deletes the list and all gifts saved under it.") },
            confirmButton = {
                TextButton(onClick = { onDeleteList(target); deleteTarget = null }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text("Cancel") }
            },
        )
    }
    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            title = { Text("Reset all data?") },
            text = { Text("This permanently deletes all gift items from all lists. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmReset = false
                    onReset()
                }) { Text("Reset", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { confirmReset = false }) { Text("Cancel") }
            },
        )
    }
    // endregion
}

@Composable
private fun SettingsCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Spacer(Modifier.size(8.dp))
                Text(title, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun Dot() {
    Box(
        modifier = Modifier
            .size(8.dp)
            .background(MaterialTheme.colorScheme.primary, androidx.compose.foundation.shape.CircleShape),
    )
}

@Composable
private fun ListNameDialog(
    title: String,
    initial: String,
    confirmLabel: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var value by remember { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = { value = it },
                label = { Text("List name") },
                singleLine = true,
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(value) }, enabled = value.isNotBlank()) {
                Text(confirmLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
