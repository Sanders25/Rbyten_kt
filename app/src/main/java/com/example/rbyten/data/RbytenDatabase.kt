package com.example.rbyten.data

import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.rbyten.data.entities.Blueprint
import com.example.rbyten.data.entities.Task
import java.util.*

@Database(
    version = 6,
    entities = [
        Blueprint::class,
        Task::class
    ],
    autoMigrations = [
        AutoMigration(from = 5, to = 6)
    ],
    exportSchema = true
)

//@ProvidedTypeConverter
/*class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}*/

//@TypeConverters(Converters::class)
abstract class RbytenDatabase : RoomDatabase() {
    abstract val rbytenDao: RbytenDao
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "DROP TABLE Task;"
        )
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `Task` (`id` INTEGER NOT NULL, `blueprintId` INTEGER NOT NULL DEFAULT -1, `title` TEXT NOT NULL, `content` TEXT NOT NULL, PRIMARY KEY(`id`, `blueprintId`));"
        )
    }
}
