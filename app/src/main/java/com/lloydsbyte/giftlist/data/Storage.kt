package com.lloydsbyte.giftlist.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * Persists the gift items and list names to SharedPreferences as JSON.
 *
 * Tiny dataset, zero extra dependencies (uses the built-in org.json that ships with Android),
 * and [android.content.SharedPreferences.edit] writes to disk asynchronously via [apply].
 *
 * [DEFAULT_LIST_NAMES] is returned only when nothing is saved yet, after which a user edit
 * persists the (possibly changed) list of names.
 */
class Storage(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun loadListNames(): List<String> {
        val raw = prefs.getString(KEY_LISTS, null) ?: return DEFAULT_LIST_NAMES
        return runCatching {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { arr.getString(it) }
        }.getOrDefault(DEFAULT_LIST_NAMES)
    }

    fun saveListNames(names: List<String>) {
        val arr = JSONArray()
        names.forEach { arr.put(it) }
        prefs.edit().putString(KEY_LISTS, arr.toString()).apply()
    }

    fun loadItems(): List<GiftItem> {
        val raw = prefs.getString(KEY_ITEMS, null) ?: return emptyList()
        return runCatching {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                GiftItem(
                    id = o.getString("id"),
                    listName = o.getString("listName"),
                    toName = o.optString("toName"),
                    fromName = o.optString("fromName"),
                    itemName = o.optString("itemName"),
                    shipperName = o.optString("shipperName"),
                    arrived = o.optBoolean("arrived"),
                    wrapped = o.optBoolean("wrapped"),
                )
            }
        }.getOrDefault(emptyList())
    }

    fun saveItems(items: List<GiftItem>) {
        val arr = JSONArray()
        items.forEach { item ->
            arr.put(
                JSONObject().apply {
                    put("id", item.id)
                    put("listName", item.listName)
                    put("toName", item.toName)
                    put("fromName", item.fromName)
                    put("itemName", item.itemName)
                    put("shipperName", item.shipperName)
                    put("arrived", item.arrived)
                    put("wrapped", item.wrapped)
                }
            )
        }
        prefs.edit().putString(KEY_ITEMS, arr.toString()).apply()
    }

    /** Clears gift items only; list names (user config) are intentionally kept. */
    fun clear() = prefs.edit().remove(KEY_ITEMS).apply()

    private companion object {
        const val PREFS_NAME = "gift_list_prefs"
        const val KEY_ITEMS = "items"
        const val KEY_LISTS = "lists"
    }
}
