package com.example.rbyten.ui.mainscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rbyten.data.Blueprint
import com.example.rbyten.data.RbytenRepository
import com.example.rbyten.navigation.Routes
import com.example.rbyten.util.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainScreenViewModel @Inject constructor(
    private val repository: RbytenRepository,
) : ViewModel() {

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var deletedBlueprint: Blueprint? = null

    val blueprints = repository.getBlueprints()

    fun onEvent(event: MainScreenEvent) {
        when (event) {
            is MainScreenEvent.OnBpClick -> {
                sendUiEvent(UiEvent.Navigate(Routes.EDITOR_SCREEN + "?bpId=${event.blueprint.id}"))
            }
            is MainScreenEvent.OnAddBpClick -> {

            }
            is MainScreenEvent.OnDeleteBpClick -> {
                viewModelScope.launch {
                    deletedBlueprint = event.blueprint
                    repository.deleteBlueprint(event.blueprint)
                    sendUiEvent(UiEvent.ShowSnackbar(
                        message = "Чертёж удалён",
                        action = "Вернуть"
                    ))
                }
            }
            is MainScreenEvent.OnUndoDeleteClick -> {
                deletedBlueprint?.let { blueprint ->
                viewModelScope.launch {
                    repository.insertBlueprint(blueprint)
                }

                }
            }
            is MainScreenEvent.OnNavigationClick -> {
                sendUiEvent(UiEvent.Navigate(Routes.SETTINGS_SCREEN))
            }
            is MainScreenEvent.OnFavouriteClick -> {
                viewModelScope.launch {
                    repository.insertBlueprint(
                        event.blueprint.copy(
                            isFavourite = event.isFavourite
                        )
                    )
                }
            }
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}