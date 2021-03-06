package com.example.rbyten.ui.editor_screen

import android.graphics.Color
import androidx.compose.runtime.*
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rbyten.data.RbytenRepository
import com.example.rbyten.data.entities.Blueprint
import com.example.rbyten.util.UiEvent
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

@HiltViewModel
class EditorScreenViewModel @Inject constructor(
    private val repository: RbytenRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    var blueprint by mutableStateOf<Blueprint?>(null)
        private set
    var blueprintId by mutableStateOf(0)
        private set
    var blueprintTitle by mutableStateOf("")
        private set
    var blueprintDescription by mutableStateOf("")
        private set
    var blueprintIsFavourite by mutableStateOf(false)
        private set
    var blueprintLastEdited by mutableStateOf("")

    private val cachedTasks = mutableStateListOf<Task>()
    val displayableCachedTasks: List<Task> = cachedTasks

    enum class WidgetMenuState {
        Idle, Pressed
    }

    var widgetMenuState by mutableStateOf(WidgetMenuState.Idle)

    class Task(_title: String) {
        var id = count.incrementAndGet()
        var parentId: Int = -1
        var children: MutableList<Task> = mutableStateListOf()
        var title: String = _title
        var content: MutableList<Widget<Any>> = mutableStateListOf()
        var isMenuOpen: Boolean = false

        companion object {
            var count: AtomicInteger = AtomicInteger(0)
        }

        var idCounter = Companion
    }

    data class Widget<T>(
        val widgetType: String,
        val content: T,
    )

    data class TextFieldWidget(var text: String)
    data class ListWidget(val itemList: MutableList<ListWidgetItem> = mutableStateListOf())
    data class ListWidgetItem(var isChecked: Boolean, var text: String)


    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        blueprintId = savedStateHandle.get<Int>("bpId")!!
        // ???????? ???????????? ????????????????????
        if (blueprintId != -1) {
            viewModelScope.launch {
                repository.getBlueprintById(blueprintId)?.let { blueprint ->
                    blueprintTitle = blueprint.title
                    blueprintDescription = blueprint.description ?: ""
                    blueprintIsFavourite = blueprint.isFavourite
                    blueprintLastEdited = blueprint.lastEdited
                    this@EditorScreenViewModel.blueprint = blueprint
                    loadTasksFromDatabase()
                }
            }
        }
    }

    fun onEvent(event: EditorScreenEvent) {
        when (event) {
            is EditorScreenEvent.OnTitleChange -> blueprintTitle = event.title
            is EditorScreenEvent.OnDescriptionChange -> blueprintDescription = event.description
/*            is EditorScreenEvent.OnSaveBp -> {
                viewModelScope.launch {
                    if (blueprintTitle.isBlank()) {
                        sendUiEvent(UiEvent.ShowSnackbar(
                            message = "???????????????? ???? ?????????? ???????? ????????????"
                        ))
                        return@launch
                    }
                    repository.insertBlueprint(
                        Blueprint(
                            title = blueprintTitle,
                            description = blueprintDescription,
                            isFavourite = blueprint?.isFavourite ?: false,
                            id = blueprint?.id,
                            background = blueprint?.background
                                ?: AzureTheme.SurfaceLightColor.toArgb()
                        )
                    )
                    //sendUiEvent(UiEvent.PopBackStack)
                }
            }*/
            is EditorScreenEvent.OnAddTaskClick -> {
                viewModelScope.launch {
                    if (event.title.isBlank()) {
                        sendUiEvent(UiEvent.ShowSnackbar(
                            message = "???????????????? ???? ?????????? ???????? ????????????"
                        ))
                        return@launch
                    }
                    val newTask = Task(event.title)
                    cachedTasks.add(newTask)
                    //val addedTask = cachedTasks.last()
                    writeToDatabase(newTask)
                }
            }
/*            is EditorScreenEvent.OnWidgetMenuStateChange -> {
                Log.d("DEBUG", "Changed")
                event.task.isMenuOpen = ! event.task.isMenuOpen
                widgetMenuState = when (widgetMenuState){
                    WidgetMenuState.Idle -> WidgetMenuState.Pressed
                    WidgetMenuState.Pressed -> WidgetMenuState.Idle
                }
            }*/
            is EditorScreenEvent.OnAddWidget -> {
                when (event.widgetType) {
                    "text" -> {
                        event.task.content.add(Widget(event.widgetType,
                            TextFieldWidget("")))
                        writeToDatabase(event.task)
                    }
                    "list" -> {
                        event.task.content.add(Widget(event.widgetType, ListWidget()))
                        writeToDatabase(event.task)
                    }
                    //"image" -> event.task.content.add(Widget(ImageWidget(null)))
                }
            }
            is EditorScreenEvent.OnListWidgetAddItem -> {
                event.widget.itemList.add(ListWidgetItem(false, ""))
                writeToDatabase(event.task)
            }
            is EditorScreenEvent.OnListWidgetDelete -> {
                event.task.content.removeAt(event.widgetIndex)
                writeToDatabase(event.task)
            }
            is EditorScreenEvent.OnListWidgetDeleteItem -> {
                event.widget.itemList.removeAt(event.itemIndex)
                writeToDatabase(event.task)
            }
            is EditorScreenEvent.OnListWidgetItemChange -> {
                event.item.isChecked = event.isChecked
                event.item.text = event.text
                writeToDatabase(event.task)
            }
            is EditorScreenEvent.OnTextFieldWidgetChange -> {
                event.widget.text = event.text
                writeToDatabase(event.task)
            }
            is EditorScreenEvent.OnTextFieldWidgetDelete -> {
                event.task.content.removeAt(event.widget)
                writeToDatabase(event.task)
            }
            is EditorScreenEvent.OnTaskDeleteClick -> {
                if (cachedTasks.isNotEmpty() && event.task.children.isNotEmpty())
                    cachedTasks.removeIf {
                        it.parentId == event.task.id
                    }//.children.remove(event.task)
                cachedTasks.remove(event.task)
                viewModelScope.launch {
                    repository.deleteChildrenOfTask(event.task.id, blueprint?.id!!)
                    repository.deleteTask(event.task.id, blueprint?.id!!)
                }
            }
            is EditorScreenEvent.OnAddTaskParallelClick -> {
                viewModelScope.launch {
                    val newTask = Task(event.parallelTask.title + " (??????????????????)")
                    newTask.parentId = event.parallelTask.parentId
                    cachedTasks.add(newTask)
                    cachedTasks.first {
                        it.id == newTask.parentId
                    }.children.add(newTask)
                    writeToDatabase(newTask)
                }
            }
            is EditorScreenEvent.OnAddTaskSerialClick -> {
                viewModelScope.launch {
                    val newTask = Task(event.parentTask.title + " (??????????????????)")
                    newTask.parentId = event.parentTask.id
                    cachedTasks.add(newTask)
                    event.parentTask.children.add(newTask)
                    writeToDatabase(newTask)
                }
            }
            EditorScreenEvent.OnSaveBp -> {
            }
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    private fun writeToDatabase(_task: Task) {

        blueprintLastEdited = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString()
        val taskEntity = com.example.rbyten.data.entities.Task(
            blueprintId = blueprint!!.id!!,
            title = _task.title,
            id = _task.id,
            parentId = _task.parentId,
            content = jsonifyTask(_task)
        )

        viewModelScope.launch {
            repository.insertBlueprint(Blueprint(
                blueprintTitle,
                blueprintDescription,
                blueprintIsFavourite,
                blueprintLastEdited,
                background = Color.WHITE,
                blueprintId
            ))
            repository.insertTask(
                task = taskEntity
            )
        }
    }

    data class LoadedWidgets(
        val lists: MutableList<ListWidget>,
        val texts: MutableList<TextFieldWidget>,
    )

    private suspend fun loadTasksFromDatabase() {
        viewModelScope.launch {
            repository.getTasksInBlueprint(blueprint?.id!!).forEach { task ->
                val newTask = Task(task.title)
                newTask.id = task.id
                newTask.parentId = task.parentId

                val loadedWidgets = jsonToTask(task.content)

                loadedWidgets.texts.forEach {
                    newTask.content.add(Widget("text", it))
                }
                loadedWidgets.lists.forEach {
                    newTask.content.add(Widget("list", it))
                }

                cachedTasks.add(
                    newTask
                )
                newTask.idCounter.count.set(cachedTasks.maxOf { it.id })
            }

            cachedTasks.forEach { task ->
                cachedTasks.forEach { innerTask ->
                    if (innerTask.parentId == task.id)
                        task.children.add(innerTask)
                }
            }

        }
    }

    private fun jsonToTask(content: String): LoadedWidgets {//: List<Widget<ListWidget>> {
        val gson = Gson()

        val rawContent: List<Widget<Any>> =
            gson.fromJson(content, object : TypeToken<List<Widget<Any>>>() {}.type)

        var serializedText: String
        var serializedList: String
        var deserializedText: TextFieldWidget
        var deserializedList: ListWidget

        val loadedWidgets = LoadedWidgets(mutableListOf(), mutableListOf())

        rawContent.forEach { widget ->
            when (widget.widgetType) {
                "text" -> {
                    serializedText = gson.toJson(widget.content)
                    deserializedText =
                        gson.fromJson(serializedText, object : TypeToken<TextFieldWidget>() {}.type)
                    loadedWidgets.texts.add(deserializedText)
                }
                "list" -> {
                    serializedList = gson.toJson(widget.content)
                    deserializedList =
                        gson.fromJson(serializedList, object : TypeToken<ListWidget>() {}.type)
                    loadedWidgets.lists.add(deserializedList)
                }
            }
        }

        return loadedWidgets
    }

    private fun jsonifyTask(task: Task): String {
        val gsonPretty = GsonBuilder().setPrettyPrinting().create()

        val json = gsonPretty.toJson(task.content)
        return json
    }
}