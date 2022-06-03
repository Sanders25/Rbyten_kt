package com.example.rbyten.data.entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Entity(primaryKeys = ["id", "blueprintId"])
data class Task(
    @NonNull
    val id: Int,
    @ColumnInfo(name = "blueprintId", defaultValue = "-1")
    @NonNull
    val blueprintId: Int,
    val title: String,
    val content: String,
    @ColumnInfo(name = "parentId", defaultValue = "-1")
    val parentId: Int
)
