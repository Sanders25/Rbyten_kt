package com.example.rbyten.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.time.LocalDateTime
import java.util.*

@Entity
data class Blueprint(
    val title: String,
    val description: String?,
    val isFavourite: Boolean,
    @ColumnInfo(name = "lastEdited", defaultValue = "")
    val lastEdited: String,
    val background: Int,
    @PrimaryKey(autoGenerate = true)
    //@ColumnInfo(name = "id", defaultValue = "0")
    val id: Int? = null
)

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE Blueprint ADD COLUMN background INTEGER NOT NULL DEFAULT ''")
    }
}
