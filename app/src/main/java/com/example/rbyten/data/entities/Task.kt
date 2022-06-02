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
    val content: String
)

/*val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "DROP TABLE Task;"
        )
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `Task` (`id` INTEGER NOT NULL, `blueprintId` INTEGER NOT NULL DEFAULT -1, `title` TEXT NOT NULL, `content` TEXT NOT NULL, PRIMARY KEY(`id`, `blueprintId`));"
        )
    }
}*/
