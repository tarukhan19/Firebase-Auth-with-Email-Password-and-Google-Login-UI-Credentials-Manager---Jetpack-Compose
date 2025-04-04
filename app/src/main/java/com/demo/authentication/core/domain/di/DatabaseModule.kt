package com.demo.authentication.core.domain.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.demo.authentication.features.data.local.dao.UserDao
import com.demo.authentication.features.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    val migration_1_2 = object : Migration(1,2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Example: Adding a new column 'age' to the 'users' table
            database.execSQL("ALTER TABLE users ADD COLUMN age INTEGER DEFAULT 0 NOT NULL")
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "user_db").build()
           // .addMigrations(migration_1_2).build()
           // .fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideUserDao(appDatabase: AppDatabase) : UserDao {
        return appDatabase.userDao()
    }
}