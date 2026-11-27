package com.lloydsbyte.giftlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.lloydsbyte.giftlist.data.DEFAULT_PASSCODE
import com.lloydsbyte.giftlist.data.GiftItem
import com.lloydsbyte.giftlist.data.Screen
import com.lloydsbyte.giftlist.data.Storage
import com.lloydsbyte.giftlist.data.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Single source of truth for the app. Holds the [UiState] in a [StateFlow], survives
 * configuration changes, and persists every mutation through [Storage].
 */
class GiftViewModel(application: Application) : AndroidViewModel(application) {

    private val storage = Storage(application)

    private val _uiState = MutableStateFlow(loadInitial())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private fun loadInitial(): UiState {
        val names = storage.loadListNames()
        val items = storage.loadItems()
        return UiState(
            items = items,
            listNames = names,
            activeList = names.firstOrNull() ?: "",
        )
    }

    // region Lock
    fun unlock(code: String): Boolean {
        if (code != DEFAULT_PASSCODE) return false
        _uiState.update { it.copy(isLocked = false) }
        return true
    }

    /** Re-lock (called when the app is genuinely backgrounded, not on rotation). */
    fun lock() = _uiState.update { it.copy(isLocked = true) }
    // endregion

    // region Navigation
    fun selectList(name: String) = _uiState.update { it.copy(activeList = name) }
    fun openAdd() = _uiState.update { it.copy(screen = Screen.AddItem, editingItemId = null) }
    fun openEdit(id: String) = _uiState.update { it.copy(screen = Screen.AddItem, editingItemId = id) }
    fun openSettings() = _uiState.update { it.copy(screen = Screen.Settings) }
    fun backToList() = _uiState.update { it.copy(screen = Screen.List, editingItemId = null) }
    // endregion

    // region Lists
    fun addList(name: String) {
        val trimmed = name.trim()
        if (trimmed.isBlank()) return
        _uiState.update { state ->
            if (state.listNames.contains(trimmed)) return@update state
            val names = state.listNames + trimmed
            storage.saveListNames(names)
            state.copy(listNames = names, activeList = trimmed)
        }
    }

    fun renameList(old: String, new: String) {
        val trimmed = new.trim()
        if (trimmed.isBlank() || trimmed == old) return
        _uiState.update { state ->
            if (state.listNames.contains(trimmed)) return@update state
            val names = state.listNames.map { if (it == old) trimmed else it }
            // Keep gifts attached to the renamed list so they don't become orphaned.
            val items = state.items.map { if (it.listName == old) it.copy(listName = trimmed) else it }
            storage.saveListNames(names)
            storage.saveItems(items)
            state.copy(
                listNames = names,
                items = items,
                activeList = if (state.activeList == old) trimmed else state.activeList,
            )
        }
    }

    fun deleteList(name: String) {
        _uiState.update { state ->
            val names = state.listNames.filterNot { it == name }
            // Cascade: a list's gifts are linked by name, so removing the list removes its gifts.
            val items = state.items.filterNot { it.listName == name }
            storage.saveListNames(names)
            storage.saveItems(items)
            val newActive = if (state.activeList == name) names.firstOrNull() ?: "" else state.activeList
            state.copy(listNames = names, items = items, activeList = newActive)
        }
    }
    // endregion

    // region Items
    fun saveItem(item: GiftItem) {
        _uiState.update { state ->
            val items = state.items.toMutableList()
            val index = items.indexOfFirst { it.id == item.id }
            if (index >= 0) items[index] = item else items.add(item)
            storage.saveItems(items)
            state.copy(items = items, screen = Screen.List, editingItemId = null)
        }
    }

    fun deleteItem(id: String) {
        _uiState.update { state ->
            val items = state.items.filterNot { it.id == id }
            storage.saveItems(items)
            state.copy(items = items, screen = Screen.List, editingItemId = null)
        }
    }

    fun toggleArrived(id: String) = updateItem(id) { it.copy(arrived = !it.arrived) }
    fun toggleWrapped(id: String) = updateItem(id) { it.copy(wrapped = !it.wrapped) }

    private fun updateItem(id: String, transform: (GiftItem) -> GiftItem) {
        _uiState.update { state ->
            val items = state.items.map { if (it.id == id) transform(it) else it }
            storage.saveItems(items)
            state.copy(items = items)
        }
    }
    // endregion

    // region Settings
    fun resetAll() {
        storage.clear()
        _uiState.update { it.copy(items = emptyList(), screen = Screen.List, editingItemId = null) }
    }
    // endregion
}
