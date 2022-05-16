package com.example.rbyten.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Blueprint::class],
    version = 1
)

abstract class BlueprintDatabase: RoomDatabase() {
    abstract val dao: BlueprintDao
}