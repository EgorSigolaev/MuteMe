package com.egorsigolaev.muteme.presentation.screens.main.models

sealed class MainViewEvent {
    class OnRequestPermissionsResult(val requestCode: Int, val permissions: Array<String>, val grantResults: IntArray): MainViewEvent()
}