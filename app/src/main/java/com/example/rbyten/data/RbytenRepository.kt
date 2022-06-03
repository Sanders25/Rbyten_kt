package com.example.rbyten.data

import com.example.rbyten.data.entities.Blueprint
import com.example.rbyten.data.entities.Task
import kotlinx.coroutines.flow.Flow

interface RbytenRepository {
    suspend fun insertBlueprint(blueprint: Blueprint)
    suspend fun deleteBlueprint(blueprint: Blueprint)
    suspend fun getBlueprintById(id: Int): Blueprint?

    fun getBlueprints(): Flow<List<Blueprint>>
    fun getFavouriteBlueprints(): Flow<List<Blueprint>>
    suspend fun getBlueprintsList(): List<Blueprint>

    suspend fun insertTask(task: Task)
    suspend fun deleteTask(id: Int, blueprintId: Int)
    suspend fun deleteAllTasks(blueprintId: Int)
    suspend fun getTaskById(id: Int, blueprintId: Int): Task?

    fun getAllTasks(): Flow<List<Task>>
    suspend fun getTasksInBlueprint(id: Int): List<Task>
}