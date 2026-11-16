package com.harmonia.music

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.media3.common.util.UnstableApi
import com.harmonia.music.ui.screens.MainScreen
import com.harmonia.music.ui.theme.HarmoniaTheme
import com.harmonia.music.ui.viewmodel.MainViewModel

@UnstableApi
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        MainViewModel.Factory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable 120 FPS / High Refresh Rate on supported displays
        enableHighRefreshRate()

        // Check initial permission state
        checkAudioPermission()

        setContent {
            HarmoniaTheme {
                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    val audioGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissions[Manifest.permission.READ_MEDIA_AUDIO] == true
                    } else {
                        permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true
                    }
                    viewModel.onAudioPermissionResult(audioGranted)
                }

                MainScreen(
                    viewModel = viewModel,
                    onRequestPermission = {
                        val permsToRequest = mutableListOf<String>()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permsToRequest.add(Manifest.permission.READ_MEDIA_AUDIO)
                            permsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            permsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                        permissionLauncher.launch(permsToRequest.toTypedArray())
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkAudioPermission()
    }

    private fun checkAudioPermission() {
        val audioPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        val isGranted = ContextCompat.checkSelfPermission(
            this,
            audioPermission
        ) == PackageManager.PERMISSION_GRANTED

        viewModel.onAudioPermissionResult(isGranted)
    }

    private fun enableHighRefreshRate() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val modes = display?.supportedModes ?: emptyArray()
                val highRateMode = modes.filter { it.refreshRate >= 89.0f }.maxByOrNull { it.refreshRate }
                if (highRateMode != null) {
                    val params = window.attributes
                    params.preferredDisplayModeId = highRateMode.modeId
                    window.attributes = params
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                @Suppress("DEPRECATION")
                val modes = windowManager.defaultDisplay?.supportedModes ?: emptyArray()
                val highRateMode = modes.filter { it.refreshRate >= 89.0f }.maxByOrNull { it.refreshRate }
                if (highRateMode != null) {
                    val params = window.attributes
                    params.preferredDisplayModeId = highRateMode.modeId
                    window.attributes = params
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
