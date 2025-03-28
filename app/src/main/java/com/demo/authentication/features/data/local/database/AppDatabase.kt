package com.demo.authentication.features.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.demo.authentication.features.data.local.dao.UserDao
import com.demo.authentication.features.data.local.entity.UserEntity


@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao() : UserDao
}