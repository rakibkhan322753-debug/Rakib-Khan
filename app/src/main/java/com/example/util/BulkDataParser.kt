package com.example.util

import com.example.data.model.Accommodation
import com.example.data.model.Station

data class BulkParseResult(
  val accommodations: List<Accommodation>,
  val stations: List<Station>,
  val errors: List<String>,
  val totalLinesProcessed: Int
)

object BulkDataParser {

  fun parseBulkCsv(rawText: String): BulkParseResult {
    val lines = rawText.lines().map { it.trim() }.filter { it.isNotBlank() }
    val accommodations = mutableListOf<Accommodation>()
    val stations = mutableListOf<Station>()
    val errors = mutableListOf<String>()

    if (lines.isEmpty()) {
      return BulkParseResult(emptyList(), emptyList(), listOf("Input file/text is empty"), 0)
    }

    var isStationMode = false
    var isAccommodationMode = false

    lines.forEachIndexed { index, line ->
      // Check for section headers
      val lower = line.lowercase()
      if (lower.startsWith("#") || lower.startsWith("//")) {
        // Comment line, ignore
        return@forEachIndexed
      }

      if (lower.contains("station") && (lower.contains("supervisor") || lower.contains("code") || lower.contains("hub"))) {
        isStationMode = true
        isAccommodationMode = false
        return@forEachIndexed
      }

      if ((lower.contains("area") && lower.contains("villa")) || lower.contains("accommodation")) {
        isAccommodationMode = true
        isStationMode = false
        return@forEachIndexed
      }

      val tokens = splitCsvLine(line)
      if (tokens.isEmpty()) return@forEachIndexed

      try {
        val firstTokenLower = tokens[0].lowercase().trim()

        if (firstTokenLower == "station" || isStationMode) {
          // Format 1 (Explicit prefix): Station, Name, Code, Area, Lat, Lng, Supervisor, Phone, Address
          // Format 2: Name, Code, Area, Lat, Lng, Supervisor, Phone, Address
          val offset = if (firstTokenLower == "station") 1 else 0
          val name = tokens.getOrNull(offset) ?: ""
          if (name.isNotBlank() && !name.equals("name", ignoreCase = true)) {
            val code = tokens.getOrNull(offset + 1) ?: ""
            val area = tokens.getOrNull(offset + 2) ?: ""
            val lat = tokens.getOrNull(offset + 3)?.toDoubleOrNull()
            val lng = tokens.getOrNull(offset + 4)?.toDoubleOrNull()
            val supervisor = tokens.getOrNull(offset + 5) ?: ""
            val phone = tokens.getOrNull(offset + 6) ?: ""
            val address = tokens.getOrNull(offset + 7) ?: ""

            stations.add(
              Station(
                name = name,
                code = code,
                areaName = area,
                latitude = lat,
                longitude = lng,
                supervisorName = supervisor,
                supervisorPhone = phone,
                address = address
              )
            )
          }
        } else {
          // Parse as Accommodation
          // Format: Area, Villa, Floor, Room, WorkerPhone, OwnerPhone, Lat, Lng, StationName, Notes
          val area = tokens.getOrNull(0) ?: ""
          if (area.isNotBlank() && !area.equals("area", ignoreCase = true) && !area.equals("areaName", ignoreCase = true)) {
            val villa = tokens.getOrNull(1) ?: ""
            val floor = tokens.getOrNull(2) ?: ""
            val room = tokens.getOrNull(3) ?: ""
            val workerPhone = tokens.getOrNull(4) ?: ""
            val ownerPhone = tokens.getOrNull(5) ?: ""
            val lat = tokens.getOrNull(6)?.toDoubleOrNull()
            val lng = tokens.getOrNull(7)?.toDoubleOrNull()
            val stationName = tokens.getOrNull(8) ?: ""
            val notes = tokens.getOrNull(9) ?: ""

            accommodations.add(
              Accommodation(
                areaName = area,
                villaNumber = villa,
                floorNumber = floor,
                roomNumber = room,
                workerPhone = workerPhone,
                ownerPhone = ownerPhone,
                latitude = lat,
                longitude = lng,
                stationName = stationName,
                notes = notes
              )
            )
          }
        }
      } catch (e: Exception) {
        errors.add("Line ${index + 1}: ${e.message}")
      }
    }

    return BulkParseResult(
      accommodations = accommodations,
      stations = stations,
      errors = errors,
      totalLinesProcessed = lines.size
    )
  }

  private fun splitCsvLine(line: String): List<String> {
    val result = mutableListOf<String>()
    val sb = java.lang.StringBuilder()
    var inQuotes = false

    for (i in line.indices) {
      val c = line[i]
      if (c == '\"') {
        inQuotes = !inQuotes
      } else if ((c == ',' || c == '\t') && !inQuotes) {
        result.add(sb.toString().trim())
        sb.setLength(0)
      } else {
        sb.append(c)
      }
    }
    result.add(sb.toString().trim())
    return result
  }

  fun getSampleAccommodationCsv(): String {
    return """
# ACCOMMODATIONS BULK DATA
# Area, Villa, Floor, Room, WorkerPhone, OwnerPhone, Latitude, Longitude, StationName, Notes
Al Olaya District, 14B, 2nd Floor, 201-203, +966 50 123 4567, +966 55 987 6543, 24.7136, 46.6753, Riyadh Central Hub, Executive Villa Unit
Al Malaz, 28, Ground Floor, 101-102, +966 54 321 0987, +966 56 654 3210, 24.6657, 46.7369, East Riyadh Station, Near Central Market
Al Sulaimaniya, 07, 1st Floor, 104-105, +966 53 888 2345, +966 50 444 8765, 24.6984, 46.7028, North Riyadh Depot, Fully Furnished
Al Yasmin, 12A, 1st Floor, 301-304, +966 55 777 1122, +966 50 888 9900, 24.8210, 46.6340, North Riyadh Depot, Northern Engineering Team
Al Nakheel, 45, Ground Floor, 101, +966 50 999 4433, +966 55 222 3344, 24.7450, 46.6210, Riyadh Central Hub, Modern compound apartment
    """.trimIndent()
  }

  fun getSampleStationCsv(): String {
    return """
# STATIONS BULK DATA
# Station, Name, Code, Area, Latitude, Longitude, Supervisor, Phone, Address
Station, Riyadh Central Hub, RC-01, Al Olaya, 24.7136, 46.6753, Eng. Ahmed Al-Zahrani, +966 50 111 2233, King Fahd Road Al Olaya
Station, East Riyadh Station, ER-02, Al Malaz, 24.6657, 46.7369, Eng. Khalid Al-Mutairi, +966 55 444 5566, Salah Al-Din Rd Al Malaz
Station, North Riyadh Depot, NR-03, Al Sulaimaniya, 24.6984, 46.7028, Eng. Tariq Al-Ghamdi, +966 54 777 8899, Prince Mutaib Rd
Station, West Industrial Hub, WI-04, Industrial City, 24.5820, 46.8520, Eng. Fahad Al-Dossari, +966 50 333 4455, Phase 2 Industrial Area
    """.trimIndent()
  }
}
