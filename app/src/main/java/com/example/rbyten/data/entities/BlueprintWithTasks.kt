package com.example.rbyten.data.entities

import androidx.room.Embedded
import androidx.room.Relation

data class BlueprintWithTasks(
    @Embedded val blueprint: Blueprint,
    @Relation(
        parentColumn = "id",
        entityColumn = "blueprintId"
    )
    val tasks: List<Task>
)
