package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a geographic location and property record for villas and housing accommodations.
 */
@Entity(tableName = "locations")
data class Location(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val name: String,
  val areaName: String,
  val propertyType: String = "Villa", // e.g., Villa, Staff Housing, Apartment, Compound
  val latitude: Double,
  val longitude: Double,
  val address: String = "",
  val villaNumber: String = "",
  val floorNumber: String = "",
  val roomNumber: String = "",
  val totalRooms: Int = 1,
  val capacity: Int = 0,
  val contactPerson: String = "",
  val contactPhone: String = "",
  val googleMapsUrl: String = "",
  val notes: String = "",
  val createdAt: Long = System.currentTimeMillis()
)
