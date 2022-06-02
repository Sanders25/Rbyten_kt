package com.example.rbyten.ui.main_screen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rbyten.data.RbytenRepository
import com.example.rbyten.data.entities.Blueprint
import com.example.rbyten.data.entities.Task
import com.example.rbyten.navigation.Routes
import com.example.rbyten.ui.editor_screen.EditorScreenViewModel
import com.example.rbyten.ui.theme.Intro
import com.example.rbyten.ui.theme.MintTheme
import com.example.rbyten.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val repository: RbytenRepository,
) : ViewModel() {

    var title by mutableStateOf("")
        private set

    var description by mutableStateOf("")
        private set

    var bgColor by mutableStateOf(MintTheme.BackgroundWhiteColor.toArgb())
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

/*    inner class LastEditedBlueprint(_blueprint: Blueprint) {
        val blueprint = _blueprint
        var tasksCount = 0
        val tasksCompleted = 0
        val deadline = ""

        init {
            suspend {
                val tasksInBlueprint = repository.getTasksInBlueprint(_blueprint.id!!)
                tasksCount = tasksInBlueprint.count()
            }
        }
    }*/

    inner class LastEditedBlueprintsList(_blueprintList: MutableList<Blueprint>) {
        val blueprintList = _blueprintList
        lateinit var blueprint: Blueprint
        var tasksCount = 0
        val tasksCompleted = 0
        val deadline = ""

        init {
            suspend {
                delay(500)
                blueprintList.sortedBy { blueprint -> LocalDateTime.parse(blueprint.lastEdited) }
                blueprint = blueprintList.last()
                //tasksCount = repository.getTasksInBlueprint(blueprint.id!!).count()
            }
        }
    }

/*    init {
        val tasksInBlueprint = getTasksInBp(_blueprint.id!!)
        tasksCount = tasksInBlueprint.count()
        Log.e("MYTAG", "works: $tasksInBlueprint...$tasksCount")
    }
}

private fun getTasksInBp(id: Int): List<Task> {
    var output: List<Task> = listOf()

    viewModelScope.launch {
        output = repository.getTasksInBlueprint(id)
    }
    return output
}*/


    private var blueprintsList = mutableListOf<Blueprint>()

    //: Blueprint? = null
    private var deletedBlueprint: Blueprint? = null

    val blueprints = repository.getBlueprints()
    val favouriteBlueprints = repository.getFavouriteBlueprints()
    val lastEditedBlueprints = repository.getBlueprints().map { list ->
        if (list.isNotEmpty()) {

            blueprintsList = list.toMutableList()




            list.sortedBy { LocalDateTime.parse(it.lastEdited) }
            //lastEditedBlueprint = LastEditedBlueprint(list.last())
            list.filter { blueprint ->
                LocalDateTime.parse(blueprint.lastEdited).isAfter(LocalDateTime.now().minusWeeks(1))
            }
        } else
            listOf()
    }
    //var lastEditedBlueprints: List<Blueprint> = emptyList()
    var lastEditedBlueprint = LastEditedBlueprintsList(blueprintsList)
    var currentIntroData = IntroData(Color.White, Color.White, "", Decoration.Sun)

    init {
        currentIntroData = setIntroData()
        //populateLastEditedList()
    }


    fun onEvent(event: MainScreenEvent) {
        when (event) {
            is MainScreenEvent.OnBpClick -> {
                sendUiEvent(UiEvent.Navigate(Routes.EDITOR_SCREEN + "?bpId=${event.blueprint.id}"))
            }
            is MainScreenEvent.OnAddBpClick -> {
                viewModelScope.launch {
                    if (title.isBlank()) {
                        sendUiEvent(UiEvent.ShowSnackbar(
                            message = "Название не может быть пустым"
                        ))
                        return@launch
                    }
                    repository.insertBlueprint(
                        blueprint = Blueprint(
                            title = title,
                            description = description,
                            background = bgColor,
                            isFavourite = false,
                            lastEdited = LocalDateTime.now().toString()
                        )
                    )
                }
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
            is MainScreenEvent.OnBackgroundChange -> bgColor = event.background
            is MainScreenEvent.OnDescriptionChange -> description = event.description
            is MainScreenEvent.OnTitleChange -> title = event.title
            MainScreenEvent.OnLoad -> {
            }
        }
    }

    enum class Decoration {
        Stars,
        Sun
    }

    data class IntroData(
        val TopColor: Color,
        val BottomColor: Color,
        val greeting: String,
        val decoration: Decoration,
        val currentTime: Int = 0,
    )

/*    private fun populateLastEditedList() {
        suspend {
            lastEditedBlueprints = repository.getBlueprintsList()
            lastEditedBlueprints.filter { blueprint ->
                LocalDateTime.parse(blueprint.lastEdited).isAfter(LocalDateTime.now().minusWeeks(1))
            }
        }

        //lastEditedBlueprint = list.maxByOrNull { LocalDate.parse(it.lastEdited) }!!
    }*/

    private fun setIntroData(): IntroData {

        var currentTime = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        val morning = 6
        val midday = 12
        val evening = 18
        val midnight = 22


        return when (currentTime) {
            in evening..midnight -> IntroData(Intro.EveningTop,
                Intro.EveningBottom,
                "Добрый вечер",
                Decoration.Stars,
                currentTime)
            in midday..evening -> IntroData(Intro.MiddayTop,
                Intro.MiddayBottom,
                "Добрый день",
                Decoration.Sun,
                currentTime)
            in morning..midday -> IntroData(Intro.MorningTop,
                Intro.MorningBottom,
                "Доброе утро",
                Decoration.Stars,
                currentTime)
            else -> IntroData(Intro.MidnightTop,
                Intro.MidnightBottom,
                "Доброй ночи",
                Decoration.Stars,
                currentTime)
        }

    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}