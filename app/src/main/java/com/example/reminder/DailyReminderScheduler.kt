package com.example.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.util.Calendar

/**
 * Schedules background recurring daily reminders (every 24 hours)
 * using Android's AlarmManager to check for outstanding housing requirements
 * and open maintenance reports.
 */
object DailyReminderScheduler {
  private const val TAG = "DailyReminderScheduler"
  const val ACTION_DAILY_REMINDER = "com.example.action.DAILY_REMINDER"
  const val REQUEST_CODE_DAILY_REMINDER = 5001

  /**
   * Schedule or update the daily recurring reminder alarm every 24 hours.
   */
  fun scheduleDailyReminder(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
    val intent = Intent(context, DailyReminderReceiver::class.java).apply {
      action = ACTION_DAILY_REMINDER
    }
    val pendingIntent = PendingIntent.getBroadcast(
      context,
      REQUEST_CODE_DAILY_REMINDER,
      intent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // Set alarm for 9:00 AM daily
    val calendar = Calendar.getInstance().apply {
      set(Calendar.HOUR_OF_DAY, 9)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
      // If 9 AM already passed today, set target for tomorrow 9 AM
      if (timeInMillis <= System.currentTimeMillis()) {
        add(Calendar.DAY_OF_YEAR, 1)
      }
    }

    val intervalMillis = AlarmManager.INTERVAL_DAY

    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        alarmManager.setInexactRepeating(
          AlarmManager.RTC_WAKEUP,
          calendar.timeInMillis,
          intervalMillis,
          pendingIntent
        )
      } else {
        alarmManager.setRepeating(
          AlarmManager.RTC_WAKEUP,
          calendar.timeInMillis,
          intervalMillis,
          pendingIntent
        )
      }
      Log.d(TAG, "Daily reminder alarm scheduled for ${calendar.time} (every 24 hours)")
    } catch (e: Exception) {
      Log.e(TAG, "Failed to schedule daily reminder", e)
    }
  }

  /**
   * Immediately triggers the check and notification in the background (used for testing and on-demand sync).
   */
  fun triggerImmediateCheck(context: Context) {
    val intent = Intent(context, DailyReminderReceiver::class.java).apply {
      action = ACTION_DAILY_REMINDER
      putExtra("isManualTest", true)
    }
    context.sendBroadcast(intent)
  }

  /**
   * Cancels the scheduled daily reminder.
   */
  fun cancelDailyReminder(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
    val intent = Intent(context, DailyReminderReceiver::class.java).apply {
      action = ACTION_DAILY_REMINDER
    }
    val pendingIntent = PendingIntent.getBroadcast(
      context,
      REQUEST_CODE_DAILY_REMINDER,
      intent,
      PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
    )
    if (pendingIntent != null) {
      alarmManager.cancel(pendingIntent)
      pendingIntent.cancel()
      Log.d(TAG, "Daily reminder alarm cancelled")
    }
  }
}
