package com.getair.design

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import com.getair.app.ui.AirTvApp
import com.getair.design.fixtures.StaticData
import com.getair.design.player.DesignAndroidPlayerUiHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val playerHost = remember { DesignAndroidPlayerUiHost(this@MainActivity) }
            AirTvApp(
                presentation = StaticData.presentation,
                onExit = ::finish,
                playerHost = playerHost,
            )
        }
    }
}
