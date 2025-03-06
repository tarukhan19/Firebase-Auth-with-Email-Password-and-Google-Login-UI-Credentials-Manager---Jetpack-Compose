package com.demo.userauth.repository

import com.demo.userauth.data.local.dao.UserDao
import com.demo.userauth.data.local.entity.UserEntity
import javax.inject.Inject

class UserAuthRepo @Inject constructor(var userDao: UserDao) {
    suspend fun userRegister(userEntity: UserEntity) {

    }
}