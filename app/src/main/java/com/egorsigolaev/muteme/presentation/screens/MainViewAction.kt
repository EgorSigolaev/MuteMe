package com.egorsigolaev.muteme.presentation.screens

sealed class MainViewAction {
    data class ShowError(val message: Any): MainViewAction()
}