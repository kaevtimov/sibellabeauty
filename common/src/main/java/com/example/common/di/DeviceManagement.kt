package com.example.common.di

import com.google.firebase.installations.FirebaseInstallations
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private const val DEVICE_ID = "device-id"

@Singleton
class DeviceManagement @Inject constructor(
    private val secureStore: SecureStore
) {

    suspend fun generateInstallationId() {
        if (getDeviceId().isNullOrEmpty()) {
            runCatching {
                val deviceId = FirebaseInstallations.getInstance().id
                    .addOnFailureListener {
                        // TODO: log error
                    }
                    .await() ?: return
                setDeviceId(deviceId)
            }
        }
    }

    fun getDeviceId() = secureStore.getString(DEVICE_ID, null)

    private fun setDeviceId(deviceId: String?) = secureStore.putString(DEVICE_ID, deviceId)

    suspend fun resetInstallations() {
        runCatching {
            FirebaseInstallations.getInstance()
                .delete()
                .addOnFailureListener {

                }
                .await()
        }
    }
}