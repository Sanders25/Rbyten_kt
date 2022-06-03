package com.example.rbyten.di

import android.app.Application
import androidx.room.Room
import com.example.rbyten.data.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideRbytenDatabase(app: Application): RbytenDatabase {
        return Room.databaseBuilder(
            app,
            RbytenDatabase::class.java,
            "rbyten_db"
        )/*.fallbackToDestructiveMigration().addMigrations(MIGRATION_8_9)*///.addTypeConverter(Converters())
            .build()
    }

    @Provides
    @Singleton
    fun provideRbytenRepository(db:RbytenDatabase): RbytenRepository {
        return RbytenRepositoryImpl(db.rbytenDao)
    }

}