package com.dormitorymanager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ReminderReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "duty_reminder_channel"
        const val NOTIFICATION_ID = 1
    }

    override fun onReceive(context: Context, intent: Intent) {
        val prefs = PreferencesHelper(context)
        if (!prefs.reminderEnabled) {
            return
        }

        val db = DatabaseHelper(context)
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val (dutyStudent, _) = db.getDutyByDate(today)

        val title = "值日提醒"
        val message = if (dutyStudent != null) {
            "今天是 ${dutyStudent.name} 值日！"
        } else {
            "今日值日提醒"
        }

        createNotificationChannel(context)
        showNotification(context, title, message)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "值日提醒"
            val descriptionText = "每日值日提醒通知"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(context: Context, title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    }
}
