package com.demo.userauth.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.demo.userauth.data.local.dao.UserDao


@Database(entities = [UserDao::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao() : UserDao
}