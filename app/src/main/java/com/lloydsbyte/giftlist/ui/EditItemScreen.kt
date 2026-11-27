package com.lloydsbyte.giftlist.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lloydsbyte.giftlist.data.GiftItem
import com.lloydsbyte.giftlist.data.UiState

/**
 * Add or edit a gift. When [UiState.editing] is null we are adding; the "To" field is
 * pre-filled with the active list (recipient) name and may be edited.
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

    var itemName by rememberSaveable { mutableStateOf(editing?.itemName ?: "") }
    var toName by rememberSaveable { mutableStateOf(editing?.toName ?: defaultRecipient) }
    var fromName by rememberSaveable { mutableStateOf(editing?.fromName ?: "") }
    var shipper by rememberSaveable { mutableStateOf(editing?.shipperName ?: "") }
    var arrived by rememberSaveable { mutableStateOf(editing?.arrived ?: false) }
    var wrapped by rememberSaveable { mutableStateOf(editing?.wrapped ?: false) }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    TextButton(onClick = onCancel) { Text("Cancel") }
                },
                title = { Text(if (editing == null) "Add Gift" else "Edit Gift") },
                actions = {
                    if (editing != null) {
                        TextButton(onClick = { onDelete(editing.id) }) {
                            Text("Delete", color = MaterialTheme.colorScheme.error)
                        }
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text("Item name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = toName,
                onValueChange = { toName = it },
                label = { Text("To") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = fromName,
                onValueChange = { fromName = it },
                label = { Text("From") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = shipper,
                onValueChange = { shipper = it },
                label = { Text("Shipper") },
                supportingText = { Text("e.g. Amazon, FedEx, UPS") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    ToggleRow(label = "Arrived", checked = arrived) { arrived = it }
                    ToggleRow(label = "Wrapped", checked = wrapped) { wrapped = it }
                }
            }

            Spacer(Modifier.height(4.dp))
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
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (editing == null) "Add gift" else "Save", fontWeight = FontWeight.Medium, fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
