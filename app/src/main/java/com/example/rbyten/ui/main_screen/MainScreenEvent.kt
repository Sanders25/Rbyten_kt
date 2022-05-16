package com.example.rbyten.ui.mainscreen

import com.example.rbyten.data.Blueprint

sealed class MainScreenEvent {
    data class OnDeleteBpClick(val blueprint: Blueprint): MainScreenEvent()
    data class OnBpClick(val blueprint: Blueprint): MainScreenEvent()
    data class OnFavouriteClick(val blueprint:Blueprint, val isFavourite: Boolean):MainScreenEvent()

    object OnUndoDeleteClick: MainScreenEvent()
    object OnAddBpClick: MainScreenEvent()
    object OnNavigationClick: MainScreenEvent()
}