package com.example.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Background BroadcastReceiver invoked by AlarmManager every 24 hours.
 * Inspects all pending housing requirements and unresolved maintenance reports,
 * and posts a system notification reminding staff to process them.
 */
class DailyReminderReceiver : BroadcastReceiver() {

  companion object {
    private const val TAG = "DailyReminderReceiver"
    const val CHANNEL_ID = "zawitco_daily_reminders"
    const val NOTIFICATION_ID = 2001
  }

  override fun onReceive(context: Context, intent: Intent?) {
    val action = intent?.action
    Log.d(TAG, "Daily reminder broadcast received with action: $action")

    if (action == Intent.ACTION_BOOT_COMPLETED) {
      DailyReminderScheduler.scheduleDailyReminder(context)
      return
    }

    val isManualTest = intent?.getBooleanExtra("isManualTest", false) ?: false

    val pendingResult = goAsync()
    CoroutineScope(Dispatchers.IO).launch {
      try {
        checkAndPostReminder(context, isManualTest)
      } catch (e: Exception) {
        Log.e(TAG, "Error executing background reminder check", e)
      } finally {
        pendingResult.finish()
      }
    }
  }

  private suspend fun checkAndPostReminder(context: Context, isManualTest: Boolean) {
    val db = AppDatabase.getInstance(context)
    val pendingRequirements = db.requirementDao().getPendingRequirementsList()
    val openReports = db.reportDao().getOpenReportsList()

    val reqCount = pendingRequirements.size
    val repCount = openReports.size
    val totalPending = reqCount + repCount

    Log.d(TAG, "Daily check: $reqCount pending requirements, $repCount open reports (Total: $totalPending)")

    if (totalPending == 0 && !isManualTest) {
      Log.d(TAG, "No pending requirements or open reports. Daily reminder skipped.")
      return
    }

    createNotificationChannel(context)

    // Build notification intent to open MainActivity
    val openAppIntent = Intent(context, MainActivity::class.java).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val contentPendingIntent = PendingIntent.getActivity(
      context,
      0,
      openAppIntent,
      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val title = if (totalPending > 0) {
      "⚠️ Zawitco: $totalPending Pending Issue${if (totalPending > 1) "s" else ""}"
    } else {
      "✅ Zawitco Housing: All Up to Date"
    }

    val summaryText = when {
      reqCount > 0 && repCount > 0 -> "$reqCount requirements pending & $repCount maintenance issues open"
      reqCount > 0 -> "$reqCount accommodation requirements awaiting fulfillment"
      repCount > 0 -> "$repCount maintenance issue reports open for action"
      else -> "No pending requirements or open reports."
    }

    // Expandable big text showing top pending items
    val bigText = buildString {
      append(summaryText)
      append("\n\n")
      if (pendingRequirements.isNotEmpty()) {
        append("📋 Pending Requirements (${pendingRequirements.size}):\n")
        pendingRequirements.take(4).forEach { req ->
          append(" • ${req.itemName} (Qty: ${req.quantity}) - Urgency: ${req.urgency}\n")
        }
        if (pendingRequirements.size > 4) {
          append(" • +${pendingRequirements.size - 4} more requirements\n")
        }
        append("\n")
      }
      if (openReports.isNotEmpty()) {
        append("🔧 Open Maintenance Issues (${openReports.size}):\n")
        openReports.take(4).forEach { rep ->
          append(" • [${rep.issueCategory}] ${rep.title} - Severity: ${rep.severity}\n")
        }
        if (openReports.size > 4) {
          append(" • +${openReports.size - 4} more reports\n")
        }
      }
      append("\nTap to open the app and take action.")
    }

    val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
      .setSmallIcon(android.R.drawable.ic_popup_reminder)
      .setContentTitle(title)
      .setContentText(summaryText)
      .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
      .setContentIntent(contentPendingIntent)
      .setAutoCancel(true)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .setCategory(NotificationCompat.CATEGORY_REMINDER)
      .setDefaults(NotificationCompat.DEFAULT_ALL)

    // Check notification permission on Android 13+ (API 33+)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      val hasPermission = ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.POST_NOTIFICATIONS
      ) == PackageManager.PERMISSION_GRANTED
      if (!hasPermission) {
        Log.w(TAG, "POST_NOTIFICATIONS permission not granted. Cannot post notification.")
        return
      }
    }

    try {
      NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notificationBuilder.build())
      Log.d(TAG, "Daily reminder notification posted successfully.")
    } catch (e: SecurityException) {
      Log.e(TAG, "SecurityException while posting notification", e)
    }
  }

  private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(
        CHANNEL_ID,
        "Daily Maintenance & Requirement Reminders",
        NotificationManager.IMPORTANCE_HIGH
      ).apply {
        description = "Daily 24h background reminders for all pending housing requirements and open maintenance reports"
        enableLights(true)
        enableVibration(true)
      }
      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(channel)
    }
  }
}
