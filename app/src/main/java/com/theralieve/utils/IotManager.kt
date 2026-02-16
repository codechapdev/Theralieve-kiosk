package com.theralieve.utils

import android.content.Context
import android.util.Log
//import com.amazonaws.mobileconnectors.iot.AWSIotKeystoreHelper
//import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback
import com.theralieve.domain.model.DeviceFiles
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

class IoTManager(private val context: Context) {

    private val _connectionState =
        MutableStateFlow<IotConnectionState>(IotConnectionState.Idle)
    val connectionState: StateFlow<IotConnectionState> = _connectionState


    private val TAG = "IoTManager"
//    private lateinit var mqttManager: AWSIotMqttManager

    private val keystoreName = "iot_keystore"
    private val keystorePassword = "keystore_password"
    private val certificateId = "deviceCert"

    private val clientId = "theralieve-client-" + UUID.randomUUID()
    private val iotEndpoint = "alv0crxgf0c54-ats.iot.eu-north-1.amazonaws.com"

    fun connect(
        deviceFiles: DeviceFiles,
        handle: (String) -> Unit
    ) {
//        AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus
//        mqttManager = AWSIotMqttManager(clientId, iotEndpoint).apply {
//            keepAlive = 60
//        }
//
//        val keystorePath = context.filesDir.absolutePath
//
//        try {
//            // Save certificate + key into keystore using dynamic data from API
//            if (!AWSIotKeystoreHelper.isKeystorePresent(keystorePath, keystoreName)) {
//                AWSIotKeystoreHelper.saveCertificateAndPrivateKey(
//                    certificateId,
//                    deviceFiles.certificate,
//                    deviceFiles.private_key,
//                    keystorePath,
//                    keystoreName,
//                    keystorePassword
//                )
//            }
//
//            // Load KeyStore
//            val keyStore = AWSIotKeystoreHelper.getIotKeystore(
//                certificateId,
//                keystorePath,
//                keystoreName,
//                keystorePassword
//            )
//
//            if (keyStore == null) {
//                Log.e(TAG, "Failed to load keystore")
//                return
//            }
//
//            // Connect MQTT
//            mqttManager.connect(keyStore) { status, throwable ->
//                Log.d(TAG, "Connection Status: $status")
//                throwable?.let { Log.e(TAG, "Connect error", it) }
//                handle(status)
//            }
//
//        } catch (e: Exception) {
//            Log.e(TAG, "Error setting up keystore or connection", e)
//        }
    }

    fun subscribe(topic: String, callback: (String) -> Unit) {
//        mqttManager.subscribeToTopic(topic, AWSIotMqttQos.QOS0) { _, data ->
//            val msg = String(data, Charsets.UTF_8)
//            callback(msg)
//        }
    }

    fun publish(topic: String, payload: String) {
//        try {
//            mqttManager.publishString(payload, topic, AWSIotMqttQos.QOS0)
//            Log.d(TAG, "Published to $topic: $payload")
//        } catch (e: Exception) {
//            Log.e(TAG, "Publish error", e)
//        }
    }

    fun disconnect() {
//        try {
//            mqttManager.disconnect()
//            Log.d(TAG, "Disconnected from IoT")
//        } catch (e: Exception) {
//            Log.e(TAG, "Disconnect error", e)
//        }
    }

    sealed interface IotConnectionState {
        object Idle : IotConnectionState
        object Connecting : IotConnectionState
        object Connected : IotConnectionState
        data class Error(val message: String) : IotConnectionState
    }

}



