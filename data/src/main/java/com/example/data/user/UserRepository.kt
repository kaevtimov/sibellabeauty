package com.example.data.user

import com.example.common.di.DeviceManagement
import com.example.common.di.SecureStore
import com.example.common.di.USER_KEY_VALUE
import com.example.data.FirebaseResponse
import kotlinx.coroutines.tasks.await
import com.google.firebase.database.DatabaseReference
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject


class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val firebaseDatabase: DatabaseReference,
    private val secureStore: SecureStore,
    private val deviceManagement: DeviceManagement
) : IUserRepository {

    override suspend fun register(user: UserFb): FirebaseResponse<Any> {
        firebaseDatabase.keepSynced(true)
        return safeCall {
            val keyRef: DatabaseReference = firebaseDatabase.push()
            val key = keyRef.key
            user.id = key
            firebaseDatabase.child("users").child(key!!).setValue(user).await()
            FirebaseResponse.Success(null)
        }
    }

    override suspend fun checkUsernameUnique(username: String): Boolean {
        return getAllUsers().firstOrNull { it.username == username } == null
    }

    override suspend fun getAllUsers(): List<UserFb> {
        var users = emptyList<UserFb>()
        try {
            users = firebaseDatabase.child("users").get().await().children.mapNotNull { doc ->
                doc.getValue(UserFb::class.java)
            }
        } catch (exception: Exception) {

        }
        return users
    }

    override suspend fun getLoggedInUserForDevice(): UserFb? {
        val userJson = secureStore.getString(USER_KEY_VALUE, "")
        val loggedInUser = Gson().fromJson<UserFb>(userJson, object : TypeToken<UserFb?>() {}.type) ?: getAllUsers().firstOrNull {
            it.loginState == true
                    && it.logInDeviceIds?.split(USER_DEVICE_IDS_SPLIT_DELIMETER)?.contains(
                deviceManagement.getDeviceId()
            ) == true
        }
        return loggedInUser
    }

    override suspend fun loginUser(user: UserFb): FirebaseResponse<Any> {
        firebaseDatabase.keepSynced(true)
        val newDeviceIdsValue = user.logInDeviceIds.plus("|${deviceManagement.getDeviceId()}")
        secureStore.putString(USER_KEY_VALUE, Gson().toJson(user))
        return safeCall {
            firebaseDatabase.child("users").child(user.id!!).child("loginState").setValue(true)
                .await()
            firebaseDatabase.child("users").child(user.id!!).child("logInDeviceIds").setValue(newDeviceIdsValue)
                .await()
            FirebaseResponse.Success(null)
        }
    }

    override suspend fun logoutUser(): FirebaseResponse<Any> {
        val userJson = secureStore.getString(USER_KEY_VALUE, "") ?: return FirebaseResponse.Error("Error.")
        val user = Gson().fromJson<UserFb>(userJson, object : TypeToken<UserFb?>() {}.type) ?: return FirebaseResponse.Error("Error.")
        val newDeviceIdsValue = user.logInDeviceIds?.replace("|${deviceManagement.getDeviceId()}", "")
        firebaseDatabase.keepSynced(true)
        return safeCall {
            firebaseDatabase.child("users").child(user.id!!).child("loginState").setValue(false)
                .await()
            firebaseDatabase.child("users").child(user.id!!).child("logInDeviceIds").setValue(newDeviceIdsValue)
                .await()
            secureStore.remove(USER_KEY_VALUE)
            FirebaseResponse.Success(null)
        }
    }

    override suspend fun getUserByCredentials(username: String, password: String): UserFb? {
        return getAllUsers().firstOrNull { it.username == username && it.password == password }
    }

    companion object {
        private const val USER_DEVICE_IDS_SPLIT_DELIMETER = "|"
    }
}

inline fun <T> safeCall(action: () -> FirebaseResponse<T>): FirebaseResponse<T> {
    return try {
        action()
    } catch (e: Exception) {
        FirebaseResponse.Error(e.message ?: "An unknown Error Occurred")
    }
}