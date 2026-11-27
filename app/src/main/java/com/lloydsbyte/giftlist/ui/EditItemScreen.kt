package com.lloydsbyte.giftlist.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.lloydsbyte.giftlist.data.GiftItem
import com.lloydsbyte.giftlist.data.UiState

/**
 * Add or edit a gift as a sliding bottom sheet. When [UiState.editing] is null we are adding;
 * the "To" field is pre-filled with the active list (recipient) name and may be edited.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItemScreen(
    state: UiState,
    onSave: (GiftItem) -> Unit,
    onDelete: (String) -> Unit,
    onCancel: () -> Unit,
) {
    val editing = state.editing
    val defaultRecipient = state.activeList
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var itemName by rememberSaveable { mutableStateOf(editing?.itemName ?: "") }
    var toName by rememberSaveable { mutableStateOf(editing?.toName ?: defaultRecipient) }
    var fromName by rememberSaveable { mutableStateOf(editing?.fromName ?: "") }
    var shipper by rememberSaveable { mutableStateOf(editing?.shipperName ?: "") }
    var arrived by rememberSaveable { mutableStateOf(editing?.arrived ?: false) }
    var wrapped by rememberSaveable { mutableStateOf(editing?.wrapped ?: false) }
    var confirmDelete by remember { mutableStateOf(false) }

    if (confirmDelete && editing != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Delete gift?") },
            text = { Text("“${editing.itemName.ifBlank { "this gift" }}” will be removed from the list.") },
            confirmButton = {
                TextButton(onClick = { onDelete(editing.id) }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Cancel") } },
        )
    }

    ModalBottomSheet(onDismissRequest = onCancel, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 4.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    if (editing == null) "Add gift" else "Edit gift",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                )
                if (editing != null) {
                    IconButton(onClick = { confirmDelete = true }) {
                        Icon(Icons.Filled.DeleteOutline, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
                IconButton(onClick = onCancel) {
                    Icon(Icons.Filled.Close, contentDescription = "Close")
                }
            }

            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text("Item name") },
                leadingIcon = { Icon(Icons.Filled.Redeem, contentDescription = null) },
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = toName,
                onValueChange = { toName = it },
                label = { Text("To") },
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = fromName,
                onValueChange = { fromName = it },
                label = { Text("From") },
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = shipper,
                onValueChange = { shipper = it },
                label = { Text("Shipper") },
                supportingText = { Text("e.g. Amazon, FedEx, UPS") },
                leadingIcon = { Icon(Icons.Filled.LocalShipping, contentDescription = null) },
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Done,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                StatusToggle(
                    label = "Arrived",
                    selected = arrived,
                    onClick = { arrived = !arrived },
                    icon = Icons.Filled.Inventory2,
                    selectedColor = MaterialTheme.colorScheme.secondary,
                    onSelectedColor = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.weight(1f),
                )
                StatusToggle(
                    label = "Wrapped",
                    selected = wrapped,
                    onClick = { wrapped = !wrapped },
                    icon = Icons.Filled.Redeem,
                    selectedColor = MaterialTheme.colorScheme.primary,
                    onSelectedColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(2.dp))
            Button(
                onClick = {
                    onSave(
                        GiftItem(
                            id = editing?.id ?: GiftItem.newId(),
                            listName = editing?.listName ?: state.activeList,
                            toName = toName.trim(),
                            fromName = fromName.trim(),
                            itemName = itemName.trim(),
                            shipperName = shipper.trim(),
                            arrived = arrived,
                            wrapped = wrapped,
                        )
                    )
                },
                enabled = itemName.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(if (editing == null) "Add gift" else "Save gift", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun StatusToggle(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selectedColor: androidx.compose.ui.graphics.Color,
    onSelectedColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    val haptics = rememberHapticController()
    Surface(
        onClick = {
            haptics.toggle()
            onClick()
        },
        shape = RoundedCornerShape(16.dp),
        color = if (selected) selectedColor.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (selected) selectedColor else MaterialTheme.colorScheme.onSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) selectedColor else MaterialTheme.colorScheme.outline,
        ),
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
        }
    }
}
