package com.example.githubuser.AlarmReceiver

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.githubuser.MainActivity
import com.example.githubuser.R
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    companion object{
        const val TYPE_DAILY = "Daily Reminder"
        const val EXTRA_MESSAGE = "message"
        const val EXTRA_TYPE = "type"

        private const val ID_DAILY = 100
        private const val TIME_DAILY = "09:00"
    }

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val message = intent.getStringExtra(EXTRA_MESSAGE)
        showAlarmNotification(context, message)
    }

    fun setDailyReminder(context: Context, type: String, message: String){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra(EXTRA_MESSAGE, message)
        intent.putExtra(EXTRA_TYPE, type)
        val timeArray = TIME_DAILY.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val calender = Calendar.getInstance()
        calender.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]))
        calender.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]))
        calender.set(Calendar.SECOND, 0)
        val pendingIntent = PendingIntent.getBroadcast(
                context, ID_DAILY, intent, PendingIntent.FLAG_ONE_SHOT
        )
        alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calender.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
        )
        Toast.makeText(context, "Daily Reminder is ON", Toast.LENGTH_SHORT).show()
    }

    fun cancelAlarm(context: Context){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val requestCode = ID_DAILY
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0)
        pendingIntent.cancel()
        alarmManager.cancel(pendingIntent)
        Toast.makeText(context, "Daily Reminder is OFF", Toast.LENGTH_SHORT).show()
    }

    private fun showAlarmNotification(context: Context, message: String?) {
        val channelId = "GU"
        val channelName = "daily notification"

        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
                context, 0,
                intent, PendingIntent.FLAG_ONE_SHOT
        )

        val notificationManagerCompat =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
                .setContentTitle("Daily Reminder")
                .setContentText(message)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            builder.setChannelId(channelId)
            notificationManagerCompat.createNotificationChannel(channel)
        }

        val notification = builder.build()
        notificationManagerCompat.notify(100, notification)
    }
}