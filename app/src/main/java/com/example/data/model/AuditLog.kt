package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Audit log entry that securely records every addition, modification, deletion,
 * bulk upload, and cloud sync operation conducted by Admin accounts.
 */
@Entity(tableName = "audit_logs")
data class AuditLog(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val actionType: String,         // "ADD_ACCOMMODATION", "EDIT_ACCOMMODATION", "DELETE_ACCOMMODATION", "ADD_REQUIREMENT", "FULFILL_REQUIREMENT", "DELETE_REQUIREMENT", "ADD_REPORT", "RESOLVE_REPORT", "DELETE_REPORT", "BULK_IMPORT", "CLOUD_BACKUP", "CLOUD_RESTORE"
  val entityType: String,         // "Accommodation", "Requirement", "Report", "BulkData", "CloudBackup", "Station", "UserAccount"
  val entityIdentifier: String,   // E.g., "Al Malaz - Villa 14", "Mattress (Qty: 2)", "AC Leakage #5"
  val adminUsername: String,      // Username of the Admin who performed the action
  val details: String,            // Human-readable summary of what was changed
  val timestamp: Long = System.currentTimeMillis()
)
