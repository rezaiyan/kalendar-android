package com.alirezaiyan.kalendar.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.alirezaiyan.kalendar.R
import com.google.firebase.messaging.FirebaseMessaging

/**
 * Helper class for testing Firebase Cloud Messaging
 */
object NotificationTestHelper {

    private const val TAG = "NotificationTestHelper"

    /**
     * Get the current FCM token for testing purposes
     */
    fun getFCMToken(callback: (String?) -> Unit) {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    callback(null)
                    return@addOnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                Log.d(TAG, "FCM Registration Token: $token")
                callback(token)
            }
    }

    /**
     * Create a test notification channel
     */
    fun createTestNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "test_channel",
                "Test Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Test notifications for Kalendar app"
                enableLights(true)
                lightColor = Color.parseColor("#FF6200EE")
                enableVibration(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Show a test notification
     */
    fun showTestNotification(context: Context, title: String, message: String) {
        createTestNotificationChannel(context)

        val notificationBuilder = NotificationCompat.Builder(context, "test_channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setColor(Color.parseColor("#FF6200EE"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}