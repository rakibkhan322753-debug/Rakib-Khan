package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * User account created by Administrator for staff, supervisors, and viewers.
 */
@Entity(
  tableName = "user_accounts",
  indices = [Index(value = ["username"], unique = true)]
)
data class UserAccount(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val username: String,         // Login username (e.g., "rakib", "supervisor1")
  val passwordHash: String,     // Password
  val fullName: String,         // Display full name
  val role: String = "USER",    // "USER", "SUPERVISOR", "ADMIN"
  val assignedStation: String = "",
  val phone: String = "",
  val isActive: Boolean = true,
  val createdAt: Long = System.currentTimeMillis()
)
