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

class UserAuthRepo @Inject constructor(private var userDao: UserDao) {
    fun userRegister(userEntity: UserEntity): Flow<Resource<String>> = flow {
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
}