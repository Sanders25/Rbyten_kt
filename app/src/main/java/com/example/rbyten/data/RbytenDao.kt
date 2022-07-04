package com.example.rbyten.data

import androidx.room.*
import com.example.rbyten.data.entities.Blueprint
import com.example.rbyten.data.entities.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface RbytenDao {
    // region Blueprint
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlueprint(blueprint: Blueprint)

    @Delete
    suspend fun deleteBlueprint(blueprint: Blueprint)

    @Query("SELECT * FROM blueprint WHERE id = :id")
    suspend fun getBlueprintById(id: Int): Blueprint?

    @Query("SELECT * FROM blueprint WHERE isFavourite = '1'")
    fun getFavouriteBlueprints(): Flow<List<Blueprint>>

    @Query("SELECT * FROM blueprint")
    fun getBlueprints(): Flow<List<Blueprint>>

    @Query("SELECT * FROM blueprint")
    fun getBlueprintsList(): List<Blueprint>
    // endregion

    // region Task
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Query("DELETE FROM task WHERE id = :id AND blueprintId = :blueprintId")
    suspend fun deleteTask(id: Int, blueprintId: Int)

    @Query("DELETE FROM task WHERE blueprintId = :blueprintId")
    suspend fun deleteAllTasks(blueprintId: Int)

    @Query("DELETE FROM task WHERE parentId = :parentId AND blueprintId = :blueprintId")
    suspend fun deleteChildrenOfTask(parentId: Int, blueprintId: Int)

    @Query("SELECT * FROM task WHERE id = :id AND blueprintId = :blueprintId")
    suspend fun getTaskById(id: Int, blueprintId: Int): Task?

    @Query("SELECT * FROM task")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE blueprintId = :id")
    suspend fun getTasksInBlueprint(id: Int): List<Task>

}