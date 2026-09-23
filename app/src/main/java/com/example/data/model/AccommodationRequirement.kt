package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Requirement entry for an accommodation:
 * e.g., Bed, Mattress, Gas stove, Gas cylinder, Electric stove, Bicycle, AC, etc.
 */
@Entity(
  tableName = "accommodation_requirements",
  foreignKeys = [
    ForeignKey(
      entity = Accommodation::class,
      parentColumns = ["id"],
      childColumns = ["accommodationId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index(value = ["accommodationId"])]
)
data class AccommodationRequirement(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val accommodationId: Long,
  val itemName: String,             // e.g. Bed, Mattress, Gas Stove, Gas Cylinder, Electric Stove, Bicycle
  val quantity: Int = 1,
  val urgency: String = "Normal",    // Urgent, Normal, Low
  val status: String = "Pending",    // Pending, In Progress, Fulfilled
  val requestedBy: String = "",      // Worker / Tenant name
  val notes: String = "",
  val createdAt: Long = System.currentTimeMillis()
)
