package com.lloydsbyte.giftlist.ui

import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

/**
 * Tiny wrapper around [View.performHapticFeedback] so UI code reads like
 * `haptics.tick()` instead of repeating constants. Uses platform-level
 * feedback constants (not the restricted Compose [HapticFeedbackType] set),
 * so each action can have a distinct, recognizable feel on minSdk 33+.
 */
class HapticController(private val view: View) {
    /** Light tap for key presses / digit entry. */
    fun tick() = view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)

    /** Medium click for toggles (arrived / wrapped). */
    fun toggle() = view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)

    /** Strong positive confirm for successful actions (unlock, save). */
    fun confirm() = view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)

    /** Strong negative buzz for errors (wrong passcode). */
    fun reject() = view.performHapticFeedback(HapticFeedbackConstants.REJECT)
}

@Composable
fun rememberHapticController(): HapticController {
    val view = LocalView.current
    return remember(view) { HapticController(view) }
}
