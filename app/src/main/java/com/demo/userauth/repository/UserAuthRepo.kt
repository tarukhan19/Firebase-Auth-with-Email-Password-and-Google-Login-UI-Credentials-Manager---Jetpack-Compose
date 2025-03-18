package com.demo.userauth.repository

import com.demo.userauth.data.local.dao.UserDao
import com.demo.userauth.data.local.entity.UserEntity
import com.demo.userauth.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/*
*This function userRegister(userEntity: UserEntity) is a Cold Flow that performs user registration
 and emits different states of the process.

* Returns a Flow of Resource<String>, meaning it emits different states of the signup process
 (Loading, Success, or Error).

* Flow is cold, meaning it executes only when collected.

* Moves execution to the IO thread for better performance.Prevents blocking the main thread (Asynchronous Execution),
 ensuring a smooth UI experience.

* */

class UserAuthRepo @Inject constructor(private var userDao: UserDao) {
    // Registration
    fun userDatabaseRegister(userEntity: UserEntity): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        val existingUser =
            userDao.getUserByEmailOrPhone(userEntity.emailId, userEntity.phoneNumber)
        when {
            existingUser?.emailId == userEntity.emailId -> emit(Resource.Error("Email Id already exists"))
            existingUser?.phoneNumber == userEntity.phoneNumber -> emit(Resource.Error("Phone Number already exist"))
            else -> {
                userDao.insertUser(userEntity)
                emit(Resource.Success("Registration successful"))
            }
        }
    }.catch { e ->
        emit(Resource.Error("Signup failed: ${e.localizedMessage}"))
    }.flowOn(Dispatchers.IO)

    // Login
    fun userDatabaseLogin(emailId: String, password: String): Flow<Resource<String>> = flow {

        emit(Resource.Loading())

        val user = userDao.loginUser(emailId = emailId, password = password)
        when {
            user?.emailId == null -> emit(Resource.Error("User not found"))
            user.password != password -> emit(Resource.Error("Password is incorrect"))
            else -> {
                emit(Resource.Success("Login successful"))
            }
        }
    }.catch { e ->
        emit(Resource.Error("Login Failed: ${e.localizedMessage}"))
    }.flowOn(Dispatchers.IO)
}