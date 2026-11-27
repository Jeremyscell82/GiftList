package com.lloydsbyte.giftlist.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lloydsbyte.giftlist.ui.theme.pineGradient
import kotlinx.coroutines.launch

/**
 * Full-screen 4-digit pin pad over a pine gradient. Auto-submits on the 4th digit and
 * shakes on a wrong code. Keys are frosted translucent circles over the gradient.
 */
@Composable
fun LockScreen(onUnlock: (String) -> Boolean) {
    var entry by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    val shake = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val haptics = rememberHapticController()

    fun submit(code: String) {
        if (onUnlock(code)) {
            haptics.confirm()
            return
        }
        error = true
        entry = ""
        haptics.reject()
        scope.launch {
            shake.animateTo(-18f, tween(45))
            shake.animateTo(18f, tween(45))
            shake.animateTo(-12f, tween(45))
            shake.animateTo(12f, tween(45))
            shake.animateTo(0f, tween(45))
        }
    }

    fun press(digit: String) {
        if (entry.length >= 4) return
        error = false
        entry += digit
        haptics.tick()
        if (entry.length == 4) submit(entry)
    }

    fun backspace() {
        if (entry.isNotEmpty()) {
            entry = entry.dropLast(1)
            error = false
            haptics.tick()
        }
    }

    val darkTheme = MaterialTheme.colorScheme.surface.luminance() < 0.5f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(pineGradient(darkTheme))
            .offset { IntOffset(shake.value.toInt(), 0) }
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // The "face" — a frosted circle with a lock icon, the app's identity on this screen.
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f))
                .border(1.dp, Color.White.copy(alpha = 0.35f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(34.dp))
        }

        Spacer(Modifier.height(16.dp))
        Text("Gift List", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        Text("Enter passcode", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)

        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            repeat(4) { i ->
                val filled = i < entry.length
                Box(
                    Modifier
                        .size(15.dp)
                        .clip(CircleShape)
                        .background(if (filled) Color.White else Color.Transparent)
                        .border(
                            width = 2.dp,
                            color = if (error) MaterialTheme.colorScheme.error
                            else Color.White.copy(alpha = 0.6f),
                            shape = CircleShape,
                        )
                )
            }
        }
        if (error) {
            Spacer(Modifier.height(10.dp))
            Text("Incorrect passcode", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
        }

        Spacer(Modifier.height(28.dp))
        val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9")
        keys.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                row.forEach { k -> PinKey(k) { press(k) } }
            }
            Spacer(Modifier.height(14.dp))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            Box(Modifier.size(68.dp)) // spacer to keep 0 centered
            PinKey("0") { press("0") }
            PinKey(icon = Icons.AutoMirrored.Filled.Backspace, onClick = ::backspace)
        }
    }
}

@Composable
private fun PinKey(label: String? = null, icon: androidx.compose.ui.graphics.vector.ImageVector? = null, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(68.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.12f))
            .border(1.dp, Color.White.copy(alpha = 0.18f), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        if (label != null) {
            Text(label, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Medium)
        } else if (icon != null) {
            Icon(icon, contentDescription = "Backspace", tint = Color.White.copy(alpha = 0.85f))
        }
    }
}

/** Tiny luminance helper so the gradient can match the active theme. */
private fun Color.luminance(): Float = 0.2126f * red + 0.7152f * green + 0.0722f * blue
