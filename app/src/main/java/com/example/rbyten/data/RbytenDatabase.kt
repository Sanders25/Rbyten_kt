package com.example.rbyten.data

import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.rbyten.data.entities.Blueprint
import com.example.rbyten.data.entities.Task
import java.util.*

@Database(
    version = 11,
    entities = [
        Blueprint::class,
        Task::class
    ],
    autoMigrations = [
        AutoMigration(from = 10, to = 11)
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

val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "DROP TABLE Task;"
        )
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `Task` (`id` INTEGER NOT NULL, `blueprintId` INTEGER NOT NULL DEFAULT -1, `title` TEXT NOT NULL, `content` TEXT NOT NULL, PRIMARY KEY(`id`, `blueprintId`), FOREIGN KEY(`blueprintId`) REFERENCES `Blueprint`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )"
        )
    }
}

val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "DROP TABLE Task;"
        )
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `Task` (`id` INTEGER NOT NULL, `blueprintId` INTEGER NOT NULL DEFAULT -1, `title` TEXT NOT NULL, `content` TEXT NOT NULL, PRIMARY KEY(`id`, `blueprintId`))",
        )
    }
}
