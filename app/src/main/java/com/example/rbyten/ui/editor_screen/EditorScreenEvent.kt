package com.example.rbyten.ui.editor_screen

sealed class EditorScreenEvent {
    data class OnTitleChange(val title: String) : EditorScreenEvent()
    data class OnDescriptionChange(val description: String) : EditorScreenEvent()
    data class OnAddTaskClick(val title: String): EditorScreenEvent()
    data class OnAddTaskSerialClick(val parentTask: EditorScreenViewModel.Task): EditorScreenEvent()
    data class OnAddTaskParallelClick(val parallelTask: EditorScreenViewModel.Task): EditorScreenEvent()
    data class OnTaskDeleteClick(val task: EditorScreenViewModel.Task): EditorScreenEvent()
    //data class OnWidgetMenuStateChange(val task: EditorScreenViewModel.Task): EditorScreenEvent()
    data class OnAddWidget(val task: EditorScreenViewModel.Task, val widgetType: String): EditorScreenEvent()

    data class OnTextFieldWidgetChange(val task:EditorScreenViewModel.Task, val widget: EditorScreenViewModel.TextFieldWidget, val text: String): EditorScreenEvent()
    data class OnTextFieldWidgetDelete(val task: EditorScreenViewModel.Task, val widget: Int): EditorScreenEvent()

    data class OnListWidgetAddItem(val task: EditorScreenViewModel.Task, val widget: EditorScreenViewModel.ListWidget): EditorScreenEvent()
    data class OnListWidgetDeleteItem(val task: EditorScreenViewModel.Task, val widget: EditorScreenViewModel.ListWidget, val itemIndex: Int): EditorScreenEvent()
    data class OnListWidgetItemChange(val task: EditorScreenViewModel.Task, val item: EditorScreenViewModel.ListWidgetItem, val isChecked: Boolean, val text: String): EditorScreenEvent()
    data class OnListWidgetDelete(val task: EditorScreenViewModel.Task, val widgetIndex: Int): EditorScreenEvent()

    object OnSaveBp : EditorScreenEvent()
}
