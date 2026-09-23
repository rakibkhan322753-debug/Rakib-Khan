package com.example.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.Accommodation
import com.example.data.model.AccommodationReport
import com.example.data.model.AccommodationRequirement
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExcelExporter {

  private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
  private val fileTimestampFormat = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault())

  private fun escapeCsv(value: Any?): String {
    if (value == null) return ""
    val str = value.toString().replace("\"", "\"\"")
    return if (str.contains(",") || str.contains("\"") || str.contains("\n") || str.contains("\r")) {
      "\"$str\""
    } else {
      str
    }
  }

  fun buildRequirementsCsvString(
    requirements: List<AccommodationRequirement>,
    accommodations: List<Accommodation>
  ): String {
    val accMap = accommodations.associateBy { it.id }
    val sb = StringBuilder()
    // Headers
    sb.append("Requirement ID,Accommodation Area,Villa Number,Floor,Room,Item Name,Quantity,Urgency,Status,Requested By,Notes,Date Created\n")
    requirements.forEach { req ->
      val acc = accMap[req.accommodationId]
      sb.append(escapeCsv(req.id)).append(",")
      sb.append(escapeCsv(acc?.areaName ?: "ID: ${req.accommodationId}")).append(",")
      sb.append(escapeCsv(acc?.villaNumber ?: "")).append(",")
      sb.append(escapeCsv(acc?.floorNumber ?: "")).append(",")
      sb.append(escapeCsv(acc?.roomNumber ?: "")).append(",")
      sb.append(escapeCsv(req.itemName)).append(",")
      sb.append(escapeCsv(req.quantity)).append(",")
      sb.append(escapeCsv(req.urgency)).append(",")
      sb.append(escapeCsv(req.status)).append(",")
      sb.append(escapeCsv(req.requestedBy)).append(",")
      sb.append(escapeCsv(req.notes)).append(",")
      sb.append(escapeCsv(dateFormat.format(Date(req.createdAt)))).append("\n")
    }
    return sb.toString()
  }

  fun buildReportsCsvString(
    reports: List<AccommodationReport>,
    accommodations: List<Accommodation>
  ): String {
    val accMap = accommodations.associateBy { it.id }
    val sb = StringBuilder()
    // Headers
    sb.append("Report ID,Accommodation Area,Villa Number,Floor,Room,Issue Category,Issue Title,Severity,Status,Reported By,Reporter Phone,Description,Date Reported\n")
    reports.forEach { rep ->
      val acc = accMap[rep.accommodationId]
      sb.append(escapeCsv(rep.id)).append(",")
      sb.append(escapeCsv(acc?.areaName ?: "ID: ${rep.accommodationId}")).append(",")
      sb.append(escapeCsv(acc?.villaNumber ?: "")).append(",")
      sb.append(escapeCsv(acc?.floorNumber ?: "")).append(",")
      sb.append(escapeCsv(acc?.roomNumber ?: "")).append(",")
      sb.append(escapeCsv(rep.issueCategory)).append(",")
      sb.append(escapeCsv(rep.title)).append(",")
      sb.append(escapeCsv(rep.severity)).append(",")
      sb.append(escapeCsv(rep.status)).append(",")
      sb.append(escapeCsv(rep.reportedBy)).append(",")
      sb.append(escapeCsv(rep.reporterPhone)).append(",")
      sb.append(escapeCsv(rep.description)).append(",")
      sb.append(escapeCsv(dateFormat.format(Date(rep.createdAt)))).append("\n")
    }
    return sb.toString()
  }

  fun buildAccommodationsCsvString(accommodations: List<Accommodation>): String {
    val sb = StringBuilder()
    sb.append("ID,Area Name,Villa Number,Floor Number,Room Number,Assigned Station,Latitude,Longitude,Worker Phone,Owner Phone,Notes\n")
    accommodations.forEach { acc ->
      sb.append(escapeCsv(acc.id)).append(",")
      sb.append(escapeCsv(acc.areaName)).append(",")
      sb.append(escapeCsv(acc.villaNumber)).append(",")
      sb.append(escapeCsv(acc.floorNumber)).append(",")
      sb.append(escapeCsv(acc.roomNumber)).append(",")
      sb.append(escapeCsv(acc.stationName)).append(",")
      sb.append(escapeCsv(acc.latitude ?: "")).append(",")
      sb.append(escapeCsv(acc.longitude ?: "")).append(",")
      sb.append(escapeCsv(acc.workerPhone)).append(",")
      sb.append(escapeCsv(acc.ownerPhone)).append(",")
      sb.append(escapeCsv(acc.notes)).append("\n")
    }
    return sb.toString()
  }

  fun buildCombinedCsvString(
    requirements: List<AccommodationRequirement>,
    reports: List<AccommodationReport>,
    accommodations: List<Accommodation>
  ): String {
    val sb = StringBuilder()
    sb.append("\uFEFF") // UTF-8 BOM for Excel
    sb.append("ZAWITCO HOUSING MANAGEMENT SYSTEM - COMPLETE EXCEL EXPORT\n")
    sb.append("Generated on:,").append(escapeCsv(dateFormat.format(Date()))).append("\n\n")

    sb.append("=== 1. HOUSING UNITS & LOCATIONS DIRECTORY ===\n")
    sb.append(buildAccommodationsCsvString(accommodations))
    sb.append("\n\n")

    sb.append("=== 2. HOUSING REQUIREMENTS LIST ===\n")
    sb.append(buildRequirementsCsvString(requirements, accommodations))
    sb.append("\n\n")

    sb.append("=== 3. MAINTENANCE & ISSUE REPORTS LIST ===\n")
    sb.append(buildReportsCsvString(reports, accommodations))

    return sb.toString()
  }

  fun exportToExcelFile(
    context: Context,
    filenamePrefix: String,
    csvContent: String
  ): File? {
    return try {
      val exportDir = File(context.cacheDir, "excel_exports").apply { mkdirs() }
      val fileName = "${filenamePrefix}_${fileTimestampFormat.format(Date())}.csv"
      val file = File(exportDir, fileName)

      FileOutputStream(file).use { fos ->
        // Write UTF-8 BOM so Microsoft Excel automatically recognizes Arabic & UTF-8 formatting
        fos.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))
        OutputStreamWriter(fos, StandardCharsets.UTF_8).use { writer ->
          writer.write(csvContent)
          writer.flush()
        }
      }
      file
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }

  fun shareExcelFile(context: Context, file: File, chooserTitle: String = "Export Excel File") {
    try {
      val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
      )
      val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, file.name)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }
      context.startActivity(Intent.createChooser(intent, chooserTitle))
    } catch (e: Exception) {
      Toast.makeText(context, "Error sharing file: ${e.message}", Toast.LENGTH_LONG).show()
    }
  }
}
