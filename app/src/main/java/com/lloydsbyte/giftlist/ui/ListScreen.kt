package com.lloydsbyte.giftlist.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lloydsbyte.giftlist.data.GiftItem
import com.lloydsbyte.giftlist.data.UiState
import kotlinx.coroutines.launch

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
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Gift List", fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                actions = {
                    androidx.compose.material3.IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            if (lists.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = onAdd,
                    icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                    text = { Text("Add gift") },
                )
            }
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            if (lists.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyState(
                        icon = Icons.Filled.CardGiftcard,
                        title = "No lists yet",
                        body = "Add them in Settings to get started.",
                    )
                }
                return@Column
            }

            val coroutineScope = rememberCoroutineScope()
            val pagerState = rememberPagerState(
                initialPage = lists.indexOf(state.activeList).coerceIn(0, lists.lastIndex),
                pageCount = { lists.size },
            )
            // Swipe -> ViewModel: when the pager settles on a page, make it the active list
            // (so the "Add gift" button attaches the new item to the right list).
            LaunchedEffect(pagerState.currentPage, lists) {
                val name = lists.getOrNull(pagerState.currentPage) ?: return@LaunchedEffect
                if (name != state.activeList) onSelectList(name)
            }
            // ViewModel -> swipe: if the active list changed elsewhere (e.g. a new list was
            // added in Settings and became active), bring the pager to it.
            LaunchedEffect(state.activeList, lists) {
                val target = lists.indexOf(state.activeList)
                if (target in lists.indices && target != pagerState.currentPage) {
                    pagerState.animateScrollToPage(target)
                }
            }

            PrimaryTabRow(selectedTabIndex = pagerState.currentPage.coerceIn(0, lists.lastIndex)) {
                lists.forEachIndexed { index, name ->
                    Tab(
                        selected = index == pagerState.currentPage,
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                        text = { Text(name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    )
                }
            }

            // Swipe between lists; each page shows that list's gifts.
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { pageIndex ->
                val pageListName = lists[pageIndex]
                val listItems = state.items.filter { it.listName == pageListName }
                if (listItems.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        EmptyState(
                            icon = Icons.Filled.CardGiftcard,
                            title = "Nothing here yet",
                            body = "Tap “Add gift” to add the first one.",
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 112.dp),
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GiftCard(
    item: GiftItem,
    onClick: () -> Unit,
    onToggleArrived: () -> Unit,
    onToggleWrapped: () -> Unit,
) {
    val shape = RoundedCornerShape(22.dp)
    ElevatedCard(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = shape) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Leading status badge: green if wrapped, red if arrived, neutral otherwise.
            StatusBadge(arrived = item.arrived, wrapped = item.wrapped)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    item.itemName.ifBlank { "(untitled)" },
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(2.dp))
                val meta = listOfNotNull(
                    item.toName.takeIf { it.isNotBlank() }?.let { "To: $it" },
                    item.fromName.takeIf { it.isNotBlank() }?.let { "From: $it" },
                    item.shipperName.takeIf { it.isNotBlank() }?.let { "via $it" },
                ).joinToString("  ·  ")
                if (meta.isNotEmpty()) {
                    Text(
                        meta,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ChipRow(label = "Arrived", selected = item.arrived, onClick = onToggleArrived)
                    ChipRow(label = "Wrapped", selected = item.wrapped, onClick = onToggleWrapped)
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(arrived: Boolean, wrapped: Boolean) {
    val (color, icon) = when {
        wrapped -> MaterialTheme.colorScheme.primary to Icons.Filled.CardGiftcard
        arrived -> MaterialTheme.colorScheme.secondary to Icons.Filled.Inventory2
        else -> MaterialTheme.colorScheme.surfaceVariant to Icons.Filled.CardGiftcard
    }
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = if (wrapped || arrived) 0.18f else 0.6f)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(22.dp),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChipRow(label: String, selected: Boolean, onClick: () -> Unit) {
    val haptics = rememberHapticController()
    val (container, content) = when (label) {
        "Arrived" -> MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.onSecondary
        else -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
    }
    FilterChip(
        selected = selected,
        onClick = {
            haptics.toggle()
            onClick()
        },
        label = { Text(label) },
        leadingIcon = {
            AnimatedVisibility(visible = selected) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                }
            }
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = container,
            selectedLabelColor = content,
        ),
    )
}

@Composable
private fun EmptyState(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, body: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(32.dp),
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(34.dp))
        }
        Spacer(Modifier.height(14.dp))
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(2.dp))
        Text(
            body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
