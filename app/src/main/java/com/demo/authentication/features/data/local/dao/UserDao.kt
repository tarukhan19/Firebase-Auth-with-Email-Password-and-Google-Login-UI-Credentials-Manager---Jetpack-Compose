package com.demo.authentication.features.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.demo.authentication.features.data.local.entity.UserEntity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT) // Prevent duplicate insertions
    suspend fun insertUser(userEntity: UserEntity)

    @Query("SELECT * FROM user where emailId = :emailId AND password = :password LIMIT 1")
    suspend fun loginUser(emailId: String, password: String): UserEntity?

    @Query("SELECT * FROM user WHERE emailId = :emailId OR phoneNumber = :phoneNumber LIMIT 1")
    suspend fun getUserByEmailOrPhone(emailId: String, phoneNumber: String) : UserEntity?

    @Query("DELETE FROM user")
    suspend fun deleteAllUsers()

    @Query("SELECT * FROM user")
    suspend fun getAllUsers(): List<UserEntity>
}