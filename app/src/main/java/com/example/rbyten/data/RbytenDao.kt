package com.example.rbyten.data

import androidx.room.*
import com.example.rbyten.data.entities.Blueprint
import com.example.rbyten.data.entities.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface BlueprintDao {
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)
    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM task WHERE id = :id")
    suspend fun getTaskById(id: Int): Task?
    @Query("SELECT * FROM task")
    fun getTasks(): Flow<List<Task>>
}