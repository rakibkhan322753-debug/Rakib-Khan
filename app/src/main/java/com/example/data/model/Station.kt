package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a Station / Company Hub location with geospatial data.
 */
@Entity(tableName = "stations")
data class Station(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val name: String,             // e.g. "Riyadh Central Station", "Olaya Hub"
  val code: String = "",        // e.g. "STN-01"
  val areaName: String = "",    // e.g. "Al Olaya District"
  val city: String = "Riyadh",
  val latitude: Double? = null,
  val longitude: Double? = null,
  val address: String = "",
  val supervisorName: String = "",
  val supervisorPhone: String = "",
  val capacityLimit: Int = 50,
  val notes: String = "",
  val createdAt: Long = System.currentTimeMillis()
)
