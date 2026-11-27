package com.lloydsbyte.giftlist.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lloydsbyte.giftlist.data.GiftItem
import com.lloydsbyte.giftlist.data.UiState
import com.lloydsbyte.giftlist.ui.theme.WrappedGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    state: UiState,
    onSelectList: (String) -> Unit,
    onAdd: () -> Unit,
    onEdit: (String) -> Unit,
    onToggleArrived: (String) -> Unit,
    onToggleWrapped: (String) -> Unit,
    onOpenSettings: () -> Unit,
) {
    val lists = state.listNames

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gift List", fontWeight = FontWeight.SemiBold) },
                actions = {
                    TextButton(onClick = onOpenSettings) { Text("Settings") }
                },
            )
        },
        floatingActionButton = {
            // No add button when there are no lists to attach a gift to.
            if (lists.isNotEmpty()) {
                FloatingActionButton(onClick = onAdd) {
                    Text("+", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            if (lists.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No lists yet.\nAdd them in Settings.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
                return@Column
            }

            val selectedIndex = lists.indexOf(state.activeList).coerceAtLeast(0)
            PrimaryTabRow(selectedTabIndex = selectedIndex) {
                lists.forEachIndexed { index, name ->
                    Tab(
                        selected = index == selectedIndex,
                        onClick = { onSelectList(name) },
                        text = { Text(name) },
                    )
                }
            }

            val listItems = state.items.filter { it.listName == state.activeList }
            if (listItems.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No gifts yet.\nTap + to add one.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(listItems, key = { it.id }) { item ->
                        GiftCard(
                            item = item,
                            onClick = { onEdit(item.id) },
                            onToggleArrived = { onToggleArrived(item.id) },
                            onToggleWrapped = { onToggleWrapped(item.id) },
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GiftCard(
    item: GiftItem,
    onClick: () -> Unit,
    onToggleArrived: () -> Unit,
    onToggleWrapped: () -> Unit,
) {
    ElevatedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(
                item.itemName.ifBlank { "(untitled)" },
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(Modifier.height(6.dp))
            if (item.toName.isNotBlank()) {
                Text("To: ${item.toName}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (item.fromName.isNotBlank()) {
                Text("From: ${item.fromName}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (item.shipperName.isNotBlank()) {
                Text("Shipper: ${item.shipperName}", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = item.arrived,
                    onClick = onToggleArrived,
                    label = { Text("Arrived") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                )
                FilterChip(
                    selected = item.wrapped,
                    onClick = onToggleWrapped,
                    label = { Text("Wrapped") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = WrappedGreen,
                        selectedLabelColor = Color.White,
                    ),
                )
            }
        }
    }
}
