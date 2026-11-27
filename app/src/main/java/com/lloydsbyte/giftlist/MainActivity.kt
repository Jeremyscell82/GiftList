package com.lloydsbyte.giftlist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.lloydsbyte.giftlist.ui.AppRoot
import com.lloydsbyte.giftlist.ui.theme.GiftListTheme

class MainActivity : ComponentActivity() {

    private val viewModel: GiftViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GiftListTheme {
                AppRoot(viewModel = viewModel)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // Re-lock only when the app is actually backgrounded, never during a config change
        // (e.g. rotation) so the lists stay hidden without locking on every rotation.
        if (!isChangingConfigurations) {
            viewModel.lock()
        }
    }
}
