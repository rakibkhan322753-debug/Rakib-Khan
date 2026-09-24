package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accommodations")
data class Accommodation(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val areaName: String,                    // Area / District Name
  val villaNumber: String = "",            // Villa Number
  val floorNumber: String = "",            // Floor Number
  val roomNumber: String = "",             // Room Number
  val totalWorkers: Int = 0,               // Total workers
  val totalCapacity: Int = 0,              // Total capacity
  val activeWorkers: Int = 0,              // Active workers
  val accommodationLocationUrl: String = "", // Accommodation Google Map Link
  val storeLocationUrl: String = "",       // Store Google Map Link
  val storeCode: String = "",              // Store Code
  val storeName: String = "",              // Store Name
  val workerPhone: String = "",            // Worker phone 1
  val workerPhone2: String = "",           // Worker phone 2
  val ownerName: String = "",              // House Owner Name
  val ownerPhone: String = "",             // House Owner Phone
  val ownerBankName: String = "",          // House Owner Bank Name
  val ownerIban: String = "",              // House Owner IBAN Number
  val googleMapsUrl: String = "",          // Compatibility Map Link
  val latitude: Double? = null,
  val longitude: Double? = null,
  val buildingImageUri: String? = null,
  val billingPictureUri: String? = null,   // 1. Billing Picture
  val doorPictureUri: String? = null,      // 2. Door Picture
  val notes: String = "",
  val whatsappGroupUrl: String = "",
  val stationName: String = "",            // Assigned station / hub
  val projectName: String = "",            // Core Project: Keemart DS, Ninja, Warehouse DC, 9 Ground
  val createdAt: Long = System.currentTimeMillis()
)
