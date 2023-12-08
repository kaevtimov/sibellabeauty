package com.example.data.user

import android.util.Log
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

    private val userDatabase = firebaseDatabase.child("users")
    private val userFromCache: UserFb?
        get() {
            val userJson = secureStore.getString(USER_KEY_VALUE, "")
            return Gson().fromJson(userJson, object : TypeToken<UserFb?>() {}.type)
        }
    private val currentDeviceId: String?
        get() = deviceManagement.getDeviceId()

    override suspend fun register(user: UserFb): FirebaseResponse<Any> {
        firebaseDatabase.keepSynced(true)
        return safeCall {
            val keyRef: DatabaseReference = firebaseDatabase.push()
            val key = keyRef.key
            user.id = key

            userDatabase.child(key!!).setValue(user).await()

            FirebaseResponse.Success(Unit)
        }
    }

    override suspend fun getAllUsers(): List<UserFb> {
        var users: List<UserFb> = emptyList()
        try {
            users = userDatabase
                .get()
                .await().children.mapNotNull { doc -> doc.getValue(UserFb::class.java) }
        } catch (exception: Exception) {
            Log.e("UserRepository", exception.message.orEmpty())
        }
        return users
    }

    override suspend fun getLoggedInUserForDevice(): UserFb? {
        val loggedInUser: UserFb? =
            userFromCache ?: getAllUsers().firstOrNull {
                it.loginState == true &&
                        it.logInDeviceIds?.split(USER_DEVICE_IDS_SPLIT_DELIMETER)
                            ?.contains(currentDeviceId) == true
            }
        return loggedInUser
    }

    override suspend fun loginUser(user: UserFb): FirebaseResponse<Any> {
        firebaseDatabase.keepSynced(true)
        val newDeviceIdsValue = user.logInDeviceIds.plus("|$currentDeviceId")
        secureStore.putString(USER_KEY_VALUE, Gson().toJson(user))
        return safeCall {
            userDatabase.child(user.id!!).child("loginState").setValue(true)
                .await()
            userDatabase.child(user.id!!).child("logInDeviceIds").setValue(newDeviceIdsValue)
                .await()
            FirebaseResponse.Success(Unit)
        }
    }

    override suspend fun logoutUser(): FirebaseResponse<Any> {
        val loggedInUser = userFromCache
            ?: return FirebaseResponse.Error("Error while logging out! User cannot be retrieved from cache.")
        val newDeviceIdsValue = loggedInUser.logInDeviceIds?.replace("|$currentDeviceId", "")

        firebaseDatabase.keepSynced(true)

        return safeCall {
            userDatabase.child(loggedInUser.id!!).child("loginState").setValue(false)
                .await()
            userDatabase.child(loggedInUser.id!!).child("logInDeviceIds")
                .setValue(newDeviceIdsValue)
                .await()
            secureStore.remove(USER_KEY_VALUE)
            FirebaseResponse.Success(Unit)
        }
    }

    override suspend fun getUserByCredentials(username: String, password: String): UserFb? =
        getAllUsers().firstOrNull { it.username == username && it.password == password }

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