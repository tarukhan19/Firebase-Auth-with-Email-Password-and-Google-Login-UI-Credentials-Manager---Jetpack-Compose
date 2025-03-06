package com.demo.userauth.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.demo.userauth.data.local.dao.UserDao
import com.demo.userauth.data.local.entity.UserEntity


@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao() : UserDao
}