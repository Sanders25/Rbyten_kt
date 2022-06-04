package com.example.rbyten.data

import com.example.rbyten.data.entities.Blueprint
import com.example.rbyten.data.entities.Task
import kotlinx.coroutines.flow.Flow

class RbytenRepositoryImpl(private val dao: RbytenDao): RbytenRepository {
    override suspend fun insertBlueprint(blueprint: Blueprint) {
        dao.insertBlueprint(blueprint)
    }

    override suspend fun deleteBlueprint(blueprint: Blueprint) {
        dao.deleteBlueprint(blueprint)
    }

    override suspend fun getBlueprintById(id: Int): Blueprint? {
        return dao.getBlueprintById(id)

    }

    override fun getBlueprints(): Flow<List<Blueprint>> {
        return dao.getBlueprints()
    }

    override fun getFavouriteBlueprints(): Flow<List<Blueprint>> {
        return dao.getFavouriteBlueprints()
    }

    override suspend fun getBlueprintsList(): List<Blueprint> {
        return dao.getBlueprintsList()
    }

    override suspend fun insertTask(task: Task) {
        dao.insertTask(task)
    }

    override suspend fun deleteTask(id: Int, blueprintId: Int) {
        dao.deleteTask(id, blueprintId)
    }

    override suspend fun deleteAllTasks(blueprintId: Int) {
        dao.deleteAllTasks(blueprintId)
    }

    override suspend fun deleteChildrenOfTask(parentId: Int, blueprintId: Int) {
        dao.deleteChildrenOfTask(parentId, blueprintId)
    }

    override suspend fun getTaskById(id: Int, blueprintId: Int): Task? {
        return dao.getTaskById(id, blueprintId)
    }

    override fun getAllTasks(): Flow<List<Task>> {
        return dao.getAllTasks()
    }

    override suspend fun getTasksInBlueprint(id: Int): List<Task> {
        return dao.getTasksInBlueprint(id)
    }
}