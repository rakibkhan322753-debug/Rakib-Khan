package com.example.data.cloud

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.Accommodation
import com.example.data.model.AccommodationReport
import com.example.data.model.AccommodationRequirement
import com.example.data.model.Station
import com.example.data.model.UserAccount
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CloudBackupSummary(
  val accommodationsCount: Int = 0,
  val requirementsCount: Int = 0,
  val reportsCount: Int = 0,
  val stationsCount: Int = 0,
  val usersCount: Int = 0,
  val timestamp: Long = System.currentTimeMillis(),
  val formattedDate: String = "",
  val backedUpBy: String = "Administrator",
  val serverName: String = "Google Cloud Server (Firestore)"
)

data class CloudRestoreData(
  val accommodations: List<Accommodation>,
  val requirements: List<AccommodationRequirement>,
  val reports: List<AccommodationReport>,
  val stations: List<Station>,
  val users: List<UserAccount>,
  val summary: CloudBackupSummary?
)

object FirebaseCloudBackupService {

  private const val TAG = "FirebaseCloudBackup"
  private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

  private fun getFirestoreInstance(context: Context): FirebaseFirestore {
    return try {
      val databaseId = try {
        context.getString(R.string.firestore_database_id)
      } catch (e: Exception) {
        ""
      }

      val app = FirebaseApp.getInstance()
      if (databaseId.isNotBlank() && databaseId != "(default)") {
        FirebaseFirestore.getInstance(app, databaseId)
      } else {
        FirebaseFirestore.getInstance(app)
      }
    } catch (e: Exception) {
      Log.e(TAG, "Falling back to default Firestore: ${e.message}")
      FirebaseFirestore.getInstance()
    }
  }

  suspend fun backupAllToCloud(
    context: Context,
    accommodations: List<Accommodation>,
    requirements: List<AccommodationRequirement>,
    reports: List<AccommodationReport>,
    stations: List<Station>,
    users: List<UserAccount>,
    backedUpBy: String
  ): Result<CloudBackupSummary> {
    return try {
      val db = getFirestoreInstance(context)
      val now = System.currentTimeMillis()
      val formattedNow = dateFormat.format(Date(now))

      // 1. Backup Accommodations
      accommodations.chunked(400).forEach { chunk ->
        val batch = db.batch()
        chunk.forEach { acc ->
          val docRef = db.collection("cloud_accommodations").document(acc.id.toString())
          val data = mapOf(
            "id" to acc.id,
            "areaName" to acc.areaName,
            "villaNumber" to acc.villaNumber,
            "floorNumber" to acc.floorNumber,
            "roomNumber" to acc.roomNumber,
            "workerPhone" to acc.workerPhone,
            "ownerPhone" to acc.ownerPhone,
            "googleMapsUrl" to acc.googleMapsUrl,
            "latitude" to (acc.latitude ?: 0.0),
            "longitude" to (acc.longitude ?: 0.0),
            "buildingImageUri" to (acc.buildingImageUri ?: ""),
            "billingPictureUri" to (acc.billingPictureUri ?: ""),
            "doorPictureUri" to (acc.doorPictureUri ?: ""),
            "notes" to acc.notes,
            "whatsappGroupUrl" to acc.whatsappGroupUrl,
            "stationName" to acc.stationName,
            "createdAt" to acc.createdAt
          )
          batch.set(docRef, data, SetOptions.merge())
        }
        batch.commit().await()
      }

      // 2. Backup Requirements
      requirements.chunked(400).forEach { chunk ->
        val batch = db.batch()
        chunk.forEach { req ->
          val docRef = db.collection("cloud_requirements").document(req.id.toString())
          val data = mapOf(
            "id" to req.id,
            "accommodationId" to req.accommodationId,
            "itemName" to req.itemName,
            "quantity" to req.quantity,
            "urgency" to req.urgency,
            "status" to req.status,
            "requestedBy" to req.requestedBy,
            "notes" to req.notes,
            "createdAt" to req.createdAt
          )
          batch.set(docRef, data, SetOptions.merge())
        }
        batch.commit().await()
      }

      // 3. Backup Reports
      reports.chunked(400).forEach { chunk ->
        val batch = db.batch()
        chunk.forEach { rep ->
          val docRef = db.collection("cloud_reports").document(rep.id.toString())
          val data = mapOf(
            "id" to rep.id,
            "accommodationId" to rep.accommodationId,
            "issueCategory" to rep.issueCategory,
            "title" to rep.title,
            "description" to rep.description,
            "severity" to rep.severity,
            "status" to rep.status,
            "reportedBy" to rep.reportedBy,
            "reporterPhone" to rep.reporterPhone,
            "createdAt" to rep.createdAt
          )
          batch.set(docRef, data, SetOptions.merge())
        }
        batch.commit().await()
      }

      // 4. Backup Stations
      stations.chunked(400).forEach { chunk ->
        val batch = db.batch()
        chunk.forEach { st ->
          val docRef = db.collection("cloud_stations").document(st.id.toString())
          val data = mapOf(
            "id" to st.id,
            "name" to st.name,
            "code" to st.code,
            "areaName" to st.areaName,
            "city" to st.city,
            "latitude" to (st.latitude ?: 0.0),
            "longitude" to (st.longitude ?: 0.0),
            "address" to st.address,
            "supervisorName" to st.supervisorName,
            "supervisorPhone" to st.supervisorPhone,
            "notes" to st.notes,
            "createdAt" to st.createdAt
          )
          batch.set(docRef, data, SetOptions.merge())
        }
        batch.commit().await()
      }

      // 5. Backup Users
      users.chunked(400).forEach { chunk ->
        val batch = db.batch()
        chunk.forEach { u ->
          val docRef = db.collection("cloud_users").document(u.id.toString())
          val data = mapOf(
            "id" to u.id,
            "username" to u.username,
            "passwordHash" to u.passwordHash,
            "fullName" to u.fullName,
            "role" to u.role,
            "assignedStation" to u.assignedStation,
            "phone" to u.phone,
            "createdAt" to u.createdAt
          )
          batch.set(docRef, data, SetOptions.merge())
        }
        batch.commit().await()
      }

      // 6. Record Cloud Metadata / Latest Backup
      val summary = CloudBackupSummary(
        accommodationsCount = accommodations.size,
        requirementsCount = requirements.size,
        reportsCount = reports.size,
        stationsCount = stations.size,
        usersCount = users.size,
        timestamp = now,
        formattedDate = formattedNow,
        backedUpBy = backedUpBy,
        serverName = "Google Cloud Firestore (${db.app.options.projectId})"
      )

      val metaData = mapOf(
        "accommodationsCount" to summary.accommodationsCount,
        "requirementsCount" to summary.requirementsCount,
        "reportsCount" to summary.reportsCount,
        "stationsCount" to summary.stationsCount,
        "usersCount" to summary.usersCount,
        "timestamp" to summary.timestamp,
        "formattedDate" to summary.formattedDate,
        "backedUpBy" to summary.backedUpBy,
        "serverName" to summary.serverName
      )
      db.collection("cloud_metadata").document("latest_backup")
        .set(metaData, SetOptions.merge()).await()

      Result.success(summary)
    } catch (e: Exception) {
      Log.e(TAG, "Error backing up to cloud: ${e.message}", e)
      Result.failure(e)
    }
  }

  suspend fun restoreAllFromCloud(context: Context): Result<CloudRestoreData> {
    return try {
      val db = getFirestoreInstance(context)

      // Fetch accommodations
      val accDocs = db.collection("cloud_accommodations").get().await()
      val accommodations = accDocs.documents.mapNotNull { doc ->
        try {
          Accommodation(
            id = doc.getLong("id") ?: 0L,
            areaName = doc.getString("areaName") ?: "",
            villaNumber = doc.getString("villaNumber") ?: "",
            floorNumber = doc.getString("floorNumber") ?: "",
            roomNumber = doc.getString("roomNumber") ?: "",
            workerPhone = doc.getString("workerPhone") ?: "",
            ownerPhone = doc.getString("ownerPhone") ?: "",
            googleMapsUrl = doc.getString("googleMapsUrl") ?: "",
            latitude = doc.getDouble("latitude")?.takeIf { it != 0.0 },
            longitude = doc.getDouble("longitude")?.takeIf { it != 0.0 },
            buildingImageUri = doc.getString("buildingImageUri")?.takeIf { it.isNotBlank() },
            billingPictureUri = doc.getString("billingPictureUri")?.takeIf { it.isNotBlank() },
            doorPictureUri = doc.getString("doorPictureUri")?.takeIf { it.isNotBlank() },
            notes = doc.getString("notes") ?: "",
            whatsappGroupUrl = doc.getString("whatsappGroupUrl") ?: "",
            stationName = doc.getString("stationName") ?: "",
            createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
          )
        } catch (e: Exception) {
          null
        }
      }

      // Fetch requirements
      val reqDocs = db.collection("cloud_requirements").get().await()
      val requirements = reqDocs.documents.mapNotNull { doc ->
        try {
          AccommodationRequirement(
            id = doc.getLong("id") ?: 0L,
            accommodationId = doc.getLong("accommodationId") ?: 0L,
            itemName = doc.getString("itemName") ?: "",
            quantity = doc.getLong("quantity")?.toInt() ?: 1,
            urgency = doc.getString("urgency") ?: "Normal",
            status = doc.getString("status") ?: "Pending",
            requestedBy = doc.getString("requestedBy") ?: "",
            notes = doc.getString("notes") ?: "",
            createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
          )
        } catch (e: Exception) {
          null
        }
      }

      // Fetch reports
      val repDocs = db.collection("cloud_reports").get().await()
      val reports = repDocs.documents.mapNotNull { doc ->
        try {
          AccommodationReport(
            id = doc.getLong("id") ?: 0L,
            accommodationId = doc.getLong("accommodationId") ?: 0L,
            issueCategory = doc.getString("issueCategory") ?: "Other",
            title = doc.getString("title") ?: "",
            description = doc.getString("description") ?: "",
            severity = doc.getString("severity") ?: "Medium",
            status = doc.getString("status") ?: "Open",
            reportedBy = doc.getString("reportedBy") ?: "",
            reporterPhone = doc.getString("reporterPhone") ?: "",
            createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
          )
        } catch (e: Exception) {
          null
        }
      }

      // Fetch stations
      val stationDocs = db.collection("cloud_stations").get().await()
      val stations = stationDocs.documents.mapNotNull { doc ->
        try {
          Station(
            id = doc.getLong("id") ?: 0L,
            name = doc.getString("name") ?: "",
            code = doc.getString("code") ?: "",
            areaName = doc.getString("areaName") ?: "",
            city = doc.getString("city") ?: "Riyadh",
            latitude = doc.getDouble("latitude")?.takeIf { it != 0.0 },
            longitude = doc.getDouble("longitude")?.takeIf { it != 0.0 },
            address = doc.getString("address") ?: "",
            supervisorName = doc.getString("supervisorName") ?: "",
            supervisorPhone = doc.getString("supervisorPhone") ?: "",
            notes = doc.getString("notes") ?: "",
            createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
          )
        } catch (e: Exception) {
          null
        }
      }

      // Fetch users
      val userDocs = db.collection("cloud_users").get().await()
      val users = userDocs.documents.mapNotNull { doc ->
        try {
          UserAccount(
            id = doc.getLong("id") ?: 0L,
            username = doc.getString("username") ?: "",
            passwordHash = doc.getString("passwordHash") ?: "Zawitco",
            fullName = doc.getString("fullName") ?: "",
            role = doc.getString("role") ?: "USER",
            assignedStation = doc.getString("assignedStation") ?: "",
            phone = doc.getString("phone") ?: "",
            createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
          )
        } catch (e: Exception) {
          null
        }
      }

      // Fetch metadata
      val metaDoc = try {
        db.collection("cloud_metadata").document("latest_backup").get().await()
      } catch (e: Exception) {
        null
      }

      val summary = if (metaDoc != null && metaDoc.exists()) {
        CloudBackupSummary(
          accommodationsCount = metaDoc.getLong("accommodationsCount")?.toInt() ?: accommodations.size,
          requirementsCount = metaDoc.getLong("requirementsCount")?.toInt() ?: requirements.size,
          reportsCount = metaDoc.getLong("reportsCount")?.toInt() ?: reports.size,
          stationsCount = metaDoc.getLong("stationsCount")?.toInt() ?: stations.size,
          usersCount = metaDoc.getLong("usersCount")?.toInt() ?: users.size,
          timestamp = metaDoc.getLong("timestamp") ?: System.currentTimeMillis(),
          formattedDate = metaDoc.getString("formattedDate") ?: "",
          backedUpBy = metaDoc.getString("backedUpBy") ?: "Administrator",
          serverName = metaDoc.getString("serverName") ?: "Google Cloud Firestore"
        )
      } else null

      Result.success(
        CloudRestoreData(
          accommodations = accommodations,
          requirements = requirements,
          reports = reports,
          stations = stations,
          users = users,
          summary = summary
        )
      )
    } catch (e: Exception) {
      Log.e(TAG, "Error restoring from cloud: ${e.message}", e)
      Result.failure(e)
    }
  }

  suspend fun fetchLatestCloudBackupMetadata(context: Context): CloudBackupSummary? {
    return try {
      val db = getFirestoreInstance(context)
      val doc = db.collection("cloud_metadata").document("latest_backup").get().await()
      if (doc.exists()) {
        CloudBackupSummary(
          accommodationsCount = doc.getLong("accommodationsCount")?.toInt() ?: 0,
          requirementsCount = doc.getLong("requirementsCount")?.toInt() ?: 0,
          reportsCount = doc.getLong("reportsCount")?.toInt() ?: 0,
          stationsCount = doc.getLong("stationsCount")?.toInt() ?: 0,
          usersCount = doc.getLong("usersCount")?.toInt() ?: 0,
          timestamp = doc.getLong("timestamp") ?: 0L,
          formattedDate = doc.getString("formattedDate") ?: "",
          backedUpBy = doc.getString("backedUpBy") ?: "Administrator",
          serverName = doc.getString("serverName") ?: "Google Cloud Firestore"
        )
      } else null
    } catch (e: Exception) {
      null
    }
  }

  // ==========================================
  // OFFLINE JSON BACKUP EXPORT / IMPORT
  // ==========================================
  fun buildJsonBackup(
    accommodations: List<Accommodation>,
    requirements: List<AccommodationRequirement>,
    reports: List<AccommodationReport>,
    stations: List<Station>,
    users: List<UserAccount>,
    exportedBy: String = "Administrator"
  ): String {
    val root = JSONObject()
    root.put("version", 1)
    root.put("exportTimestamp", System.currentTimeMillis())
    root.put("exportDate", dateFormat.format(Date()))
    root.put("exportedBy", exportedBy)
    root.put("app", "Zawitco Housing Management System")

    // Accommodations
    val accArray = JSONArray()
    accommodations.forEach { acc ->
      val obj = JSONObject()
      obj.put("id", acc.id)
      obj.put("areaName", acc.areaName)
      obj.put("villaNumber", acc.villaNumber)
      obj.put("floorNumber", acc.floorNumber)
      obj.put("roomNumber", acc.roomNumber)
      obj.put("workerPhone", acc.workerPhone)
      obj.put("ownerPhone", acc.ownerPhone)
      obj.put("googleMapsUrl", acc.googleMapsUrl)
      obj.put("latitude", acc.latitude ?: JSONObject.NULL)
      obj.put("longitude", acc.longitude ?: JSONObject.NULL)
      obj.put("billingPictureUri", acc.billingPictureUri ?: JSONObject.NULL)
      obj.put("doorPictureUri", acc.doorPictureUri ?: JSONObject.NULL)
      obj.put("notes", acc.notes)
      obj.put("whatsappGroupUrl", acc.whatsappGroupUrl)
      obj.put("stationName", acc.stationName)
      obj.put("createdAt", acc.createdAt)
      accArray.put(obj)
    }
    root.put("accommodations", accArray)

    // Requirements
    val reqArray = JSONArray()
    requirements.forEach { req ->
      val obj = JSONObject()
      obj.put("id", req.id)
      obj.put("accommodationId", req.accommodationId)
      obj.put("itemName", req.itemName)
      obj.put("quantity", req.quantity)
      obj.put("urgency", req.urgency)
      obj.put("status", req.status)
      obj.put("requestedBy", req.requestedBy)
      obj.put("notes", req.notes)
      obj.put("createdAt", req.createdAt)
      reqArray.put(obj)
    }
    root.put("requirements", reqArray)

    // Reports
    val repArray = JSONArray()
    reports.forEach { rep ->
      val obj = JSONObject()
      obj.put("id", rep.id)
      obj.put("accommodationId", rep.accommodationId)
      obj.put("issueCategory", rep.issueCategory)
      obj.put("title", rep.title)
      obj.put("description", rep.description)
      obj.put("severity", rep.severity)
      obj.put("status", rep.status)
      obj.put("reportedBy", rep.reportedBy)
      obj.put("reporterPhone", rep.reporterPhone)
      obj.put("createdAt", rep.createdAt)
      repArray.put(obj)
    }
    root.put("reports", repArray)

    // Stations
    val stArray = JSONArray()
    stations.forEach { st ->
      val obj = JSONObject()
      obj.put("id", st.id)
      obj.put("name", st.name)
      obj.put("code", st.code)
      obj.put("areaName", st.areaName)
      obj.put("city", st.city)
      obj.put("latitude", st.latitude ?: JSONObject.NULL)
      obj.put("longitude", st.longitude ?: JSONObject.NULL)
      obj.put("address", st.address)
      obj.put("supervisorName", st.supervisorName)
      obj.put("supervisorPhone", st.supervisorPhone)
      obj.put("notes", st.notes)
      obj.put("createdAt", st.createdAt)
      stArray.put(obj)
    }
    root.put("stations", stArray)

    // Users
    val userArray = JSONArray()
    users.forEach { u ->
      val obj = JSONObject()
      obj.put("id", u.id)
      obj.put("username", u.username)
      obj.put("passwordHash", u.passwordHash)
      obj.put("fullName", u.fullName)
      obj.put("role", u.role)
      obj.put("assignedStation", u.assignedStation)
      obj.put("phone", u.phone)
      obj.put("createdAt", u.createdAt)
      userArray.put(obj)
    }
    root.put("users", userArray)

    return root.toString(2)
  }

  fun parseJsonBackup(jsonString: String): CloudRestoreData? {
    return try {
      val root = JSONObject(jsonString)

      val accommodations = mutableListOf<Accommodation>()
      val accArray = root.optJSONArray("accommodations")
      if (accArray != null) {
        for (i in 0 until accArray.length()) {
          val obj = accArray.getJSONObject(i)
          accommodations.add(
            Accommodation(
              id = obj.optLong("id", 0L),
              areaName = obj.optString("areaName", ""),
              villaNumber = obj.optString("villaNumber", ""),
              floorNumber = obj.optString("floorNumber", ""),
              roomNumber = obj.optString("roomNumber", ""),
              workerPhone = obj.optString("workerPhone", ""),
              ownerPhone = obj.optString("ownerPhone", ""),
              googleMapsUrl = obj.optString("googleMapsUrl", ""),
              latitude = if (obj.has("latitude") && !obj.isNull("latitude")) obj.optDouble("latitude") else null,
              longitude = if (obj.has("longitude") && !obj.isNull("longitude")) obj.optDouble("longitude") else null,
              billingPictureUri = if (obj.has("billingPictureUri") && !obj.isNull("billingPictureUri")) obj.optString("billingPictureUri") else null,
              doorPictureUri = if (obj.has("doorPictureUri") && !obj.isNull("doorPictureUri")) obj.optString("doorPictureUri") else null,
              notes = obj.optString("notes", ""),
              whatsappGroupUrl = obj.optString("whatsappGroupUrl", ""),
              stationName = obj.optString("stationName", ""),
              createdAt = obj.optLong("createdAt", System.currentTimeMillis())
            )
          )
        }
      }

      val requirements = mutableListOf<AccommodationRequirement>()
      val reqArray = root.optJSONArray("requirements")
      if (reqArray != null) {
        for (i in 0 until reqArray.length()) {
          val obj = reqArray.getJSONObject(i)
          requirements.add(
            AccommodationRequirement(
              id = obj.optLong("id", 0L),
              accommodationId = obj.optLong("accommodationId", 0L),
              itemName = obj.optString("itemName", ""),
              quantity = obj.optInt("quantity", 1),
              urgency = obj.optString("urgency", "Normal"),
              status = obj.optString("status", "Pending"),
              requestedBy = obj.optString("requestedBy", ""),
              notes = obj.optString("notes", ""),
              createdAt = obj.optLong("createdAt", System.currentTimeMillis())
            )
          )
        }
      }

      val reports = mutableListOf<AccommodationReport>()
      val repArray = root.optJSONArray("reports")
      if (repArray != null) {
        for (i in 0 until repArray.length()) {
          val obj = repArray.getJSONObject(i)
          reports.add(
            AccommodationReport(
              id = obj.optLong("id", 0L),
              accommodationId = obj.optLong("accommodationId", 0L),
              issueCategory = obj.optString("issueCategory", "Other"),
              title = obj.optString("title", ""),
              description = obj.optString("description", ""),
              severity = obj.optString("severity", "Medium"),
              status = obj.optString("status", "Open"),
              reportedBy = obj.optString("reportedBy", ""),
              reporterPhone = obj.optString("reporterPhone", ""),
              createdAt = obj.optLong("createdAt", System.currentTimeMillis())
            )
          )
        }
      }

      val stations = mutableListOf<Station>()
      val stArray = root.optJSONArray("stations")
      if (stArray != null) {
        for (i in 0 until stArray.length()) {
          val obj = stArray.getJSONObject(i)
          stations.add(
            Station(
              id = obj.optLong("id", 0L),
              name = obj.optString("name", ""),
              code = obj.optString("code", ""),
              areaName = obj.optString("areaName", ""),
              city = obj.optString("city", "Riyadh"),
              latitude = if (obj.has("latitude") && !obj.isNull("latitude")) obj.optDouble("latitude") else null,
              longitude = if (obj.has("longitude") && !obj.isNull("longitude")) obj.optDouble("longitude") else null,
              address = obj.optString("address", ""),
              supervisorName = obj.optString("supervisorName", ""),
              supervisorPhone = obj.optString("supervisorPhone", ""),
              notes = obj.optString("notes", ""),
              createdAt = obj.optLong("createdAt", System.currentTimeMillis())
            )
          )
        }
      }

      val users = mutableListOf<UserAccount>()
      val userArray = root.optJSONArray("users")
      if (userArray != null) {
        for (i in 0 until userArray.length()) {
          val obj = userArray.getJSONObject(i)
          users.add(
            UserAccount(
              id = obj.optLong("id", 0L),
              username = obj.optString("username", ""),
              passwordHash = obj.optString("passwordHash", "Zawitco"),
              fullName = obj.optString("fullName", ""),
              role = obj.optString("role", "USER"),
              assignedStation = obj.optString("assignedStation", ""),
              phone = obj.optString("phone", ""),
              createdAt = obj.optLong("createdAt", System.currentTimeMillis())
            )
          )
        }
      }

      CloudRestoreData(
        accommodations = accommodations,
        requirements = requirements,
        reports = reports,
        stations = stations,
        users = users,
        summary = CloudBackupSummary(
          accommodationsCount = accommodations.size,
          requirementsCount = requirements.size,
          reportsCount = reports.size,
          stationsCount = stations.size,
          usersCount = users.size,
          timestamp = root.optLong("exportTimestamp", System.currentTimeMillis()),
          formattedDate = root.optString("exportDate", ""),
          backedUpBy = root.optString("exportedBy", "Administrator")
        )
      )
    } catch (e: Exception) {
      Log.e(TAG, "Error parsing JSON backup: ${e.message}")
      null
    }
  }
}
