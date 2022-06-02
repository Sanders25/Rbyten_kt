package com.example.rbyten.ui.main_screen

import com.example.rbyten.data.entities.Blueprint

sealed class MainScreenEvent {
    data class OnTitleChange(val title: String):MainScreenEvent()
    data class OnDescriptionChange(val description: String): MainScreenEvent()
    data class OnBackgroundChange(val background: Int): MainScreenEvent()
    data class OnBpClick(val blueprint: Blueprint): MainScreenEvent()
    data class OnFavouriteClick(val blueprint:Blueprint, val isFavourite: Boolean):MainScreenEvent()
    data class OnDeleteBpClick(val blueprint: Blueprint): MainScreenEvent()

    object OnAddBpClick: MainScreenEvent()
    object OnLoad: MainScreenEvent()
    object OnUndoDeleteClick: MainScreenEvent()
    object OnNavigationClick: MainScreenEvent()
}