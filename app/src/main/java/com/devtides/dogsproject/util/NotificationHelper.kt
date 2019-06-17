package com.devtides.dogsproject.util

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.devtides.dogsproject.R
import com.devtides.dogsproject.view.MainActivity

class NotificationHelper(val context: Context) {

    private val CHANNEL_ID = "Dogs channel id"
    private val NOTIFICATION_ID = 123
    
    fun createDogNotification() {
        createNotificationChannel()

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val iconBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.dog)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.dog_icon)
            .setLargeIcon(iconBitmap)
            .setContentTitle("Dogs retrieved")
            .setContentText("This notification has some content")
//            .setStyle(
//                NotificationCompat.BigTextStyle()
//                    .bigText("Much longer text that cannot fit one line because it is too long " +
//                            "and will require multiple lines to display correctly so that the users " +
//                            "will have to expand the notification to see everything")
//            )
            .setStyle(NotificationCompat.BigPictureStyle()
                .bigPicture(iconBitmap)
                .bigLargeIcon(null))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_ID
            val descriptionText = "Channel description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}