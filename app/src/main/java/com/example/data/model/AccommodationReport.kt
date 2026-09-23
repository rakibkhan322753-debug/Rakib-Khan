package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Report / Issue entry for an accommodation:
 * e.g., Water leakage, AC malfunction, Electrical fault, Door lock broken, Pest control, etc.
 */
@Entity(
  tableName = "accommodation_reports",
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
data class AccommodationReport(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val accommodationId: Long,
  val issueCategory: String,         // Plumbing, Electrical, AC/Cooling, Maintenance, Cleanliness, Other
  val title: String,
  val description: String,
  val severity: String = "Medium",   // High, Medium, Low
  val status: String = "Open",       // Open, Under Review, Resolved
  val reportedBy: String = "",       // Reporter contact / name
  val reporterPhone: String = "",
  val createdAt: Long = System.currentTimeMillis()
)
