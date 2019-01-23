package de.tum.`in`.tumcampusapp.service

import android.content.Context
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.iid.FirebaseInstanceId
import de.tum.`in`.tumcampusapp.api.app.TUMCabeClient
import de.tum.`in`.tumcampusapp.api.app.model.DeviceUploadFcmToken
import de.tum.`in`.tumcampusapp.api.app.model.TUMCabeStatus
import de.tum.`in`.tumcampusapp.component.prefs.AppConfig
import de.tum.`in`.tumcampusapp.utils.Utils
import de.tum.`in`.tumcampusapp.utils.tryOrNull
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.Executors

object FcmTokenHandler {

    @JvmStatic
    fun checkSetup(context: Context) {
        val appConfig = AppConfig(context)
        val currentToken = appConfig.firebaseTokenId

        // If we failed, we need to re register
        if (currentToken == null) {
            registerInBackground(context)
        } else {
            // If the regId is not empty, we still need to check whether it was successfully sent to the
            // TCA server, because this can fail due to user not confirming their private key
            if (appConfig.firebaseRegIdSentToServer.not()) {
                sendTokenToBackend(context, currentToken)
            }

            // Update the reg id in steady intervals
            checkRegisterIdUpdate(context, currentToken)
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     *
     *
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private fun registerInBackground(context: Context) {
        val executor = Executors.newSingleThreadExecutor()
        val appConfig = AppConfig(context)

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(executor,
                OnSuccessListener { instanceIdResult ->
                    val token = instanceIdResult.token
                    appConfig.firebaseInstanceId = instanceIdResult.id
                    appConfig.firebaseTokenId = token

                    // Reset the lock in case we are updating and maybe failed
                    appConfig.firebaseRegIdSentToServer = false
                    appConfig.firebaseRegIdLastTransmission = Date().time

                    // Let the server know of our new registration ID
                    sendTokenToBackend(context, token)

                    Utils.log("FCM registration successful")
                })
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private fun sendTokenToBackend(context: Context, token: String?) {
        // Check if all parameters are present
        if (token == null || token.isEmpty()) {
            Utils.logv("Parameter missing for sending reg id")
            return
        }

        // Try to create the message
        val uploadToken = tryOrNull {
            DeviceUploadFcmToken.getDeviceUploadFcmToken(context, token)
        } ?: return

        val appConfig = AppConfig(context)

        TUMCabeClient
                .getInstance(context)
                .deviceUploadGcmToken(uploadToken, object : Callback<TUMCabeStatus> {
                    override fun onResponse(call: Call<TUMCabeStatus>, response: Response<TUMCabeStatus>) {
                        if (!response.isSuccessful) {
                            Utils.logv("Uploading FCM registration failed...")
                            return
                        }

                        val body = response.body() ?: return
                        Utils.logv("Success uploading FCM registration id: ${body.status}")

                        // Store in shared preferences the information that the GCM registration id
                        // was sent to the TCA server successfully
                        appConfig.firebaseRegIdSentToServer = true
                    }

                    override fun onFailure(call: Call<TUMCabeStatus>, t: Throwable) {
                        Utils.log(t, "Failure uploading FCM registration id")
                        appConfig.firebaseRegIdSentToServer = false
                    }
                })
    }

    /**
     * Helper function to check if we need to update the regid
     *
     * @param regId registration ID
     */
    private fun checkRegisterIdUpdate(context: Context, regId: String) {
        // Regularly (once a day) update the server with the reg id
        val appConfig = AppConfig(context)
        val lastTransmission = appConfig.firebaseRegIdLastTransmission
        val now = Date()
        if (now.time - 24 * 3600000 > lastTransmission) {
            sendTokenToBackend(context, regId)
        }
    }

}
