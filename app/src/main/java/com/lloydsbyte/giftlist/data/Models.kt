package com.lloydsbyte.giftlist.data

import java.util.UUID

/** Default list names seeded on first launch (editable in Settings). */
val DEFAULT_LIST_NAMES: List<String> = listOf("Alex", "Alyssa")

/** Fixed default passcode that hides the lists. */
const val DEFAULT_PASSCODE = "2026"

/**
 * A single gift on someone's list.
 *
 * [listName] is the tab/recipient the item belongs to, [toName] is who the gift is for
 * (defaults to the list name), [fromName] who is giving it.
 */
data class GiftItem(
    val id: String,
    val listName: String,
    val toName: String,
    val fromName: String,
    val itemName: String,
    val shipperName: String,
    val arrived: Boolean,
    val wrapped: Boolean,
) {
    companion object {
        fun newId(): String = UUID.randomUUID().toString()
    }
}

/** The screen currently shown once the app is unlocked. */
enum class Screen { List, AddItem, Settings }

data class UiState(
    val isLocked: Boolean = true,
    val screen: Screen = Screen.List,
    val listNames: List<String> = DEFAULT_LIST_NAMES,
    val activeList: String = "",
    val items: List<GiftItem> = emptyList(),
    val editingItemId: String? = null,
) {
    /** The item currently being edited, if any. */
    val editing: GiftItem? get() = editingItemId?.let { id -> items.firstOrNull { it.id == id } }
}
