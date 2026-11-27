package com.lloydsbyte.giftlist.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.lloydsbyte.giftlist.GiftViewModel
import com.lloydsbyte.giftlist.data.Screen

/**
 * Top-level router. The lock screen takes priority. The add/edit [Screen] renders as a
 * bottom sheet *over* the list, so the list always sits underneath it.
 */
@Composable
fun AppRoot(viewModel: GiftViewModel) {
    val state by viewModel.uiState.collectAsState()

    when {
        state.isLocked -> LockScreen(onUnlock = viewModel::unlock)

        state.screen == Screen.Settings -> SettingsScreen(
            listNames = state.listNames,
            onBack = viewModel::backToList,
            onReset = viewModel::resetAll,
            onAddList = viewModel::addList,
            onRenameList = viewModel::renameList,
            onDeleteList = viewModel::deleteList,
        )

        // List is always on screen; the edit sheet floats above it when active.
        else -> {
            ListScreen(
                state = state,
                onSelectList = viewModel::selectList,
                onAdd = viewModel::openAdd,
                onEdit = viewModel::openEdit,
                onToggleArrived = viewModel::toggleArrived,
                onToggleWrapped = viewModel::toggleWrapped,
                onOpenSettings = viewModel::openSettings,
            )
            if (state.screen == Screen.AddItem) {
                EditItemScreen(
                    state = state,
                    onSave = viewModel::saveItem,
                    onDelete = viewModel::deleteItem,
                    onCancel = viewModel::backToList,
                )
            }
        }
    }
}
