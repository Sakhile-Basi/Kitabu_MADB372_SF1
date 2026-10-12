package com.stadio.kitabu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.stadio.kitabu.ui.navigation.KitabuNavHost
import com.stadio.kitabu.ui.theme.KitabuTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            KitabuTheme {
                KitabuNavHost()
            }
        }
    }
}