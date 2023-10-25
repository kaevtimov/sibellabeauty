package com.example.data.user

import com.example.data.FirebaseResponse
import com.example.data.SharedPrefsManager
import com.example.sibellabeauty.Constants
import com.example.sibellabeauty.utils.DeviceUtils
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.tasks.await


class UserRepository(private val userDao: UserDao) : IUserRepository {

    private val userFirebaseRef =
        FirebaseDatabase.getInstance(Constants.FIREBASE_DATABASE_URL).reference

    override suspend fun register(user: UserFb): FirebaseResponse<Any>? {
        userFirebaseRef.keepSynced(true)
        return safeCall {
            val keyRef: DatabaseReference = userFirebaseRef.push()
            val key = keyRef.key
            user.id = key
            userFirebaseRef.child("users").child(key!!).setValue(user).await()
            FirebaseResponse.Success(null)
        }
    }

    override suspend fun checkUsernameUnique(username: String): Boolean {
        return getAllUsers().firstOrNull { it.username == username } == null
    }

    override suspend fun getAllUsers(): List<UserFb> {
        var users = emptyList<UserFb>()
        try {
            users = userFirebaseRef.child("users").get().await().children.mapNotNull { doc ->
                doc.getValue(UserFb::class.java)
            }
        } catch (exception: Exception) {

        }
        return users
    }

    override suspend fun getLoggedInUserForDevice(): UserFb? {
        val userJson = SharedPrefsManager.getLoggedInUser()
        val loggedInUser = Gson().fromJson<UserFb>(userJson, object : TypeToken<UserFb?>() {}.type) ?: getAllUsers().firstOrNull {
            it.loginState == true
                    && it.logInDeviceIds?.split(Constants.USER_DEVICE_IDS_SPLIT_DELIMETER)?.contains(
                DeviceUtils.getDeviceId()
            ) == true
        }
        return loggedInUser
    }

    override suspend fun loginUser(user: UserFb): FirebaseResponse<Any> {
        userFirebaseRef.keepSynced(true)
        val newDeviceIdsValue = user.logInDeviceIds.plus("|${DeviceUtils.getDeviceId()}")
        return safeCall {
            userFirebaseRef.child("users").child(user.id!!).child("loginState").setValue(true)
                .await()
            userFirebaseRef.child("users").child(user.id!!).child("logInDeviceIds").setValue(newDeviceIdsValue)
                .await()
            FirebaseResponse.Success(null)
        }
    }

    override suspend fun logoutUser(): FirebaseResponse<Any>? {
        val userJson = SharedPrefsManager.getLoggedInUser() ?: return null
        val user = Gson().fromJson<UserFb>(userJson, object : TypeToken<UserFb?>() {}.type) ?: return null
        val newDeviceIdsValue = user.logInDeviceIds?.replace("|${DeviceUtils.getDeviceId()}", "")
        userFirebaseRef.keepSynced(true)
        return safeCall {
            userFirebaseRef.child("users").child(user.id!!).child("loginState").setValue(false)
                .await()
            userFirebaseRef.child("users").child(user.id!!).child("logInDeviceIds").setValue(newDeviceIdsValue)
                .await()
            SharedPrefsManager.logoutUser()
            FirebaseResponse.Success(null)
        }
    }

    override suspend fun getUserByCredentials(username: String, password: String): UserFb? {
        return getAllUsers().firstOrNull { it.username == username && it.password == password }
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(dao: UserDao): UserRepository? {
            return instance ?: synchronized(UserRepository::class.java) {
                if (instance == null) {
                    instance = UserRepository(dao)
                }
                return instance
            }
        }
    }
}

inline fun <T> safeCall(action: () -> FirebaseResponse<T>): FirebaseResponse<T> {
    return try {
        action()
    } catch (e: Exception) {
        FirebaseResponse.Error(e.message ?: "An unknown Error Occurred")
    }
}