package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accommodations")
data class Accommodation(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val areaName: String,
  val villaNumber: String = "",
  val floorNumber: String = "",
  val roomNumber: String = "",
  val workerPhone: String = "",
  val ownerPhone: String = "",
  val googleMapsUrl: String = "",
  val latitude: Double? = null,
  val longitude: Double? = null,
  val buildingImageUri: String? = null,
  val billingPictureUri: String? = null, // 1. Billing Picture
  val doorPictureUri: String? = null,    // 2. Door Picture
  val notes: String = "",
  val whatsappGroupUrl: String = "",
  val stationName: String = "",          // Assigned or nearest station
  val createdAt: Long = System.currentTimeMillis()
)
