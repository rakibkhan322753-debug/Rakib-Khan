package com.example.util

import android.util.Log
import com.example.data.model.Accommodation
import com.example.data.model.Station
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

data class BulkParseResult(
  val accommodations: List<Accommodation>,
  val stations: List<Station>,
  val errors: List<String>,
  val totalLinesProcessed: Int,
  val detectedFormat: String = "Auto"
)

object BulkDataParser {

  private const val TAG = "BulkDataParser"

  /**
   * Main Google Sheets public URL provided by user
   */
  const val DEFAULT_GOOGLE_SHEETS_URL =
    "https://docs.google.com/spreadsheets/d/1QrR7-DtC6Z1_ezRunxbfyBPlaSEF_PocY2Bxcf95ZwU/edit?usp=drivesdk"

  /**
   * Attempts to fetch CSV content from a Google Sheets URL.
   * If the sheet requires authentication (private/restricted), returns a failure with clear explanation.
   */
  suspend fun fetchGoogleSheetCsv(sheetUrl: String): Result<String> = withContext(Dispatchers.IO) {
    try {
      val cleanUrl = transformToCsvExportUrl(sheetUrl)
      val url = URL(cleanUrl)
      val connection = url.openConnection() as HttpURLConnection
      connection.requestMethod = "GET"
      connection.instanceFollowRedirects = true
      connection.connectTimeout = 12000
      connection.readTimeout = 12000
      connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Android) ZawitcoHousing/1.0")

      val responseCode = connection.responseCode
      if (responseCode in 200..299) {
        val contentType = connection.contentType ?: ""
        val reader = BufferedReader(InputStreamReader(connection.inputStream))
        val text = reader.readText()
        reader.close()

        // Check if Google redirected to a sign-in or HTML error page
        if (text.contains("ServiceLogin") || text.contains("accounts.google.com") || (contentType.contains("text/html") && text.contains("<html"))) {
          Result.failure(
            Exception(
              "Google Sheets requires permission (Sign-in required). Please open the sheet, copy the rows, and paste them here directly, or change share settings to 'Anyone with link'."
            )
          )
        } else {
          Result.success(text)
        }
      } else {
        Result.failure(Exception("HTTP $responseCode returned from Google server."))
      }
    } catch (e: Exception) {
      Log.e(TAG, "Error fetching Google Sheets CSV: ${e.message}", e)
      Result.failure(e)
    }
  }

  fun transformToCsvExportUrl(inputUrl: String): String {
    val trimmed = inputUrl.trim()
    val sheetIdRegex = Regex("/d/([a-zA-Z0-9-_]+)")
    val match = sheetIdRegex.find(trimmed)
    val sheetId = match?.groupValues?.getOrNull(1)

    return if (sheetId != null) {
      "https://docs.google.com/spreadsheets/d/$sheetId/export?format=csv"
    } else {
      trimmed
    }
  }

  /**
   * Intelligently parses bulk CSV/TSV data copied from Google Sheets, Excel, or CSV files.
   * Tracks location names, accommodation & store map links, store codes, worker counts,
   * phone numbers, and house owner bank details & IBAN.
   */
  fun parseBulkCsv(rawText: String): BulkParseResult {
    val lines = rawText.lines().map { it.trim() }.filter { it.isNotBlank() }
    val accommodations = mutableListOf<Accommodation>()
    val stations = mutableListOf<Station>()
    val errors = mutableListOf<String>()

    if (lines.isEmpty()) {
      return BulkParseResult(emptyList(), emptyList(), listOf("Input data is empty"), 0)
    }

    var headerIndex = -1
    var headerColumns: List<String>? = null

    // Find header line if present
    for (i in 0 until minOf(5, lines.size)) {
      val row = splitCsvOrTsv(lines[i])
      val lowerRow = row.map { it.lowercase() }
      if (lowerRow.any { it.contains("area") || it.contains("location") || it.contains("villa") || it.contains("worker") || it.contains("store") || it.contains("موقع") || it.contains("سكن") }) {
        headerIndex = i
        headerColumns = lowerRow
        break
      }
    }

    val dataLines = if (headerIndex >= 0) lines.drop(headerIndex + 1) else lines

    dataLines.forEachIndexed { index, line ->
      if (line.startsWith("#") || line.startsWith("//")) return@forEachIndexed

      val tokens = splitCsvOrTsv(line)
      if (tokens.isEmpty()) return@forEachIndexed

      try {
        val accommodation = if (headerColumns != null) {
          parseAccommodationWithHeaders(tokens, headerColumns)
        } else {
          parseAccommodationPositional(tokens)
        }

        if (accommodation != null && accommodation.areaName.isNotBlank()) {
          accommodations.add(accommodation)
        }
      } catch (e: Exception) {
        errors.add("Row ${index + 1}: ${e.message}")
      }
    }

    return BulkParseResult(
      accommodations = accommodations,
      stations = stations,
      errors = errors,
      totalLinesProcessed = dataLines.size,
      detectedFormat = if (headerColumns != null) "Google Sheets / Header-Mapped" else "Positional Auto-Detection"
    )
  }

  /**
   * Parse a single accommodation row or multi-line block.
   */
  fun parseSingleAccommodation(input: String): Accommodation? {
    val trimmed = input.trim()
    if (trimmed.isBlank()) return null

    // If multi-line with key-value pairs (e.g. Area: ..., Villa: ...)
    if (trimmed.contains(":") && trimmed.lines().size > 1) {
      return parseKeyValueBlock(trimmed)
    }

    // Single line CSV / TSV / Google Sheets row
    val tokens = splitCsvOrTsv(trimmed.lines().firstOrNull() ?: trimmed)
    return parseAccommodationPositional(tokens)
  }

  private fun parseKeyValueBlock(block: String): Accommodation {
    var area = ""
    var villa = ""
    var floor = ""
    var room = ""
    var totalWorkers = 0
    var totalCapacity = 0
    var activeWorkers = 0
    var accMapUrl = ""
    var storeMapUrl = ""
    var storeCode = ""
    var storeName = ""
    var workerPhone1 = ""
    var workerPhone2 = ""
    var ownerName = ""
    var ownerPhone = ""
    var ownerBank = ""
    var ownerIban = ""
    var notes = ""

    block.lines().forEach { line ->
      val parts = line.split(":", limit = 2)
      if (parts.size == 2) {
        val key = parts[0].trim().lowercase()
        val value = parts[1].trim()

        when {
          key.contains("area") || key.contains("location") || key.contains("district") || key.contains("موقع") -> area = value
          key.contains("villa") || key.contains("فيلا") -> villa = value
          key.contains("floor") || key.contains("دور") -> floor = value
          key.contains("room") || key.contains("غرفة") -> room = value
          key.contains("active") || key.contains("نشط") -> activeWorkers = value.filter { it.isDigit() }.toIntOrNull() ?: 0
          key.contains("capacity") || key.contains("سعة") -> totalCapacity = value.filter { it.isDigit() }.toIntOrNull() ?: 0
          key.contains("total") || key.contains("worker count") || key.contains("عمال") -> totalWorkers = value.filter { it.isDigit() }.toIntOrNull() ?: 0
          key.contains("store location") || key.contains("store map") || key.contains("خريطة المتجر") -> storeMapUrl = value
          key.contains("store code") || key.contains("كود المتجر") -> storeCode = value
          key.contains("store") || key.contains("متجر") || key.contains("station") -> storeName = value
          key.contains("map") || key.contains("google") || key.contains("خريطة") -> accMapUrl = value
          key.contains("phone 2") || key.contains("phone2") || key.contains("هاتف 2") -> workerPhone2 = value
          key.contains("worker phone") || key.contains("phone 1") || key.contains("phone") || key.contains("هاتف") -> workerPhone1 = value
          key.contains("owner name") || key.contains("المالك") -> ownerName = value
          key.contains("owner phone") || key.contains("هاتف المالك") -> ownerPhone = value
          key.contains("iban") || key.contains("ايبان") || key.contains("آيبان") -> ownerIban = value
          key.contains("bank") || key.contains("بنك") || key.contains("حساب") -> ownerBank = value
          key.contains("note") || key.contains("ملاحظات") -> notes = value
        }
      }
    }

    if (totalWorkers == 0 && activeWorkers > 0) totalWorkers = activeWorkers
    if (totalCapacity == 0 && totalWorkers > 0) totalCapacity = totalWorkers

    val (lat, lng) = extractLatLngFromUrl(accMapUrl)

    return Accommodation(
      areaName = if (area.isNotBlank()) area else "Imported Housing Unit",
      villaNumber = villa,
      floorNumber = floor,
      roomNumber = room,
      totalWorkers = totalWorkers,
      totalCapacity = totalCapacity,
      activeWorkers = activeWorkers,
      accommodationLocationUrl = accMapUrl,
      storeLocationUrl = storeMapUrl,
      storeCode = storeCode,
      storeName = storeName,
      workerPhone = workerPhone1,
      workerPhone2 = workerPhone2,
      ownerName = ownerName,
      ownerPhone = ownerPhone,
      ownerBankName = ownerBank,
      ownerIban = ownerIban,
      googleMapsUrl = accMapUrl,
      latitude = lat,
      longitude = lng,
      stationName = storeName.ifBlank { storeCode },
      notes = notes
    )
  }

  private fun parseAccommodationWithHeaders(
    tokens: List<String>,
    headers: List<String>
  ): Accommodation? {
    if (tokens.isEmpty()) return null

    var area = ""
    var villa = ""
    var floor = ""
    var room = ""
    var totalWorkers = 0
    var totalCapacity = 0
    var activeWorkers = 0
    var accMapUrl = ""
    var storeMapUrl = ""
    var storeCode = ""
    var storeName = ""
    var workerPhone1 = ""
    var workerPhone2 = ""
    var ownerName = ""
    var ownerPhone = ""
    var ownerBank = ""
    var ownerIban = ""
    var notes = ""

    tokens.forEachIndexed { i, token ->
      val h = headers.getOrNull(i) ?: ""
      val valStr = token.trim()
      if (valStr.isBlank()) return@forEachIndexed

      when {
        h.contains("area") || h.contains("location name") || h.contains("district") || h.contains("اسم الموقع") -> area = valStr
        h.contains("villa") || h.contains("فيلا") -> villa = valStr
        h.contains("floor") || h.contains("دور") -> floor = valStr
        h.contains("room") || h.contains("غرفة") -> room = valStr
        h.contains("active") || h.contains("نشط") -> activeWorkers = valStr.filter { it.isDigit() }.toIntOrNull() ?: 0
        h.contains("capacity") || h.contains("سعة") -> totalCapacity = valStr.filter { it.isDigit() }.toIntOrNull() ?: 0
        (h.contains("total") && h.contains("worker")) || h.contains("إجمالي العمال") -> totalWorkers = valStr.filter { it.isDigit() }.toIntOrNull() ?: 0
        h.contains("store location") || h.contains("store map") || h.contains("رابط المتجر") -> storeMapUrl = valStr
        h.contains("store code") || h.contains("كود المتجر") -> storeCode = valStr
        h.contains("store name") || h.contains("store") || h.contains("متجر") || h.contains("station") -> storeName = valStr
        h.contains("accommodation location") || h.contains("map link") || h.contains("google map") || h.contains("رابط السكن") || h.contains("رابط الموقع") -> accMapUrl = valStr
        h.contains("worker phone 2") || h.contains("phone 2") || h.contains("هاتف 2") -> workerPhone2 = valStr
        h.contains("worker phone") || h.contains("phone 1") || h.contains("هاتف العامل") -> workerPhone1 = valStr
        h.contains("owner name") || h.contains("اسم المالك") -> ownerName = valStr
        h.contains("owner phone") || h.contains("هاتف المالك") -> ownerPhone = valStr
        h.contains("iban") || h.contains("آيبان") || h.contains("ايبان") -> ownerIban = valStr
        h.contains("bank") || h.contains("حساب بنكي") || h.contains("اسم البنك") -> ownerBank = valStr
        h.contains("note") || h.contains("ملاحظات") -> notes = valStr
        // Fallback checks by value content if header didn't catch it
        valStr.startsWith("http") && accMapUrl.isBlank() -> accMapUrl = valStr
        valStr.startsWith("http") && storeMapUrl.isBlank() -> storeMapUrl = valStr
        (valStr.startsWith("SA", ignoreCase = true) || valStr.length in 20..34) && ownerIban.isBlank() -> ownerIban = valStr
      }
    }

    if (area.isBlank()) {
      area = tokens.getOrNull(0) ?: "Housing Unit"
    }

    if (totalWorkers == 0 && activeWorkers > 0) totalWorkers = activeWorkers
    if (totalCapacity == 0 && totalWorkers > 0) totalCapacity = totalWorkers

    val (lat, lng) = extractLatLngFromUrl(accMapUrl)

    return Accommodation(
      areaName = area,
      villaNumber = villa,
      floorNumber = floor,
      roomNumber = room,
      totalWorkers = totalWorkers,
      totalCapacity = totalCapacity,
      activeWorkers = activeWorkers,
      accommodationLocationUrl = accMapUrl,
      storeLocationUrl = storeMapUrl,
      storeCode = storeCode,
      storeName = storeName,
      workerPhone = workerPhone1,
      workerPhone2 = workerPhone2,
      ownerName = ownerName,
      ownerPhone = ownerPhone,
      ownerBankName = ownerBank,
      ownerIban = ownerIban,
      googleMapsUrl = accMapUrl,
      latitude = lat,
      longitude = lng,
      stationName = storeName.ifBlank { storeCode },
      notes = notes
    )
  }

  private fun parseAccommodationPositional(tokens: List<String>): Accommodation? {
    if (tokens.isEmpty()) return null

    // Collect all Map links, Phone numbers, IBANs, numbers
    var area = ""
    var villa = ""
    var floor = ""
    var room = ""
    var totalWorkers = 0
    var totalCapacity = 0
    var activeWorkers = 0
    var accMapUrl = ""
    var storeMapUrl = ""
    var storeCode = ""
    var storeName = ""
    var workerPhone1 = ""
    var workerPhone2 = ""
    var ownerName = ""
    var ownerPhone = ""
    var ownerBank = ""
    var ownerIban = ""
    var notes = ""

    // Positional standard order from Google Sheets:
    // 0: Area/Location Name
    // 1: Villa
    // 2: Floor
    // 3: Room
    // 4: Total Worker
    // 5: Total Capacity
    // 6: Active Workers
    // 7: Accommodation Map Link
    // 8: Store Location Link
    // 9: Store Code
    // 10: Store Name
    // 11: Worker Phone 1
    // 12: Worker Phone 2
    // 13: Owner Name
    // 14: Owner Phone
    // 15: Owner Bank Name
    // 16: Owner IBAN

    val mapUrls = mutableListOf<String>()
    val phones = mutableListOf<String>()
    val numbers = mutableListOf<Int>()

    tokens.forEachIndexed { idx, raw ->
      val t = raw.trim()
      if (t.isBlank()) return@forEachIndexed

      if (t.startsWith("http", ignoreCase = true) || t.contains("maps.google") || t.contains("goo.gl")) {
        mapUrls.add(t)
      } else if (t.startsWith("SA", ignoreCase = true) && t.length >= 15) {
        ownerIban = t
      } else if (isPhoneNumber(t)) {
        phones.add(t)
      } else if (t.all { it.isDigit() } && t.length <= 4) {
        numbers.add(t.toInt())
      }
    }

    area = tokens.getOrNull(0) ?: "Housing Unit"
    if (area.equals("area", ignoreCase = true) || area.equals("location", ignoreCase = true)) return null

    // Assign map URLs
    accMapUrl = mapUrls.getOrNull(0) ?: ""
    storeMapUrl = mapUrls.getOrNull(1) ?: ""

    // Assign phones
    workerPhone1 = phones.getOrNull(0) ?: ""
    workerPhone2 = phones.getOrNull(1) ?: ""
    ownerPhone = phones.getOrNull(2) ?: ""

    // Assign counts
    if (numbers.isNotEmpty()) {
      totalWorkers = numbers.getOrNull(0) ?: 0
      totalCapacity = numbers.getOrNull(1) ?: totalWorkers
      activeWorkers = numbers.getOrNull(2) ?: totalWorkers
    }

    // Extract other tokens by heuristic
    if (tokens.size >= 4) {
      villa = tokens.getOrNull(1)?.takeIf { !it.startsWith("http") && !isPhoneNumber(it) } ?: ""
      floor = tokens.getOrNull(2)?.takeIf { !it.startsWith("http") && !isPhoneNumber(it) } ?: ""
      room = tokens.getOrNull(3)?.takeIf { !it.startsWith("http") && !isPhoneNumber(it) } ?: ""
    }

    // Try finding store code (e.g. ST-101, RC-02, etc.)
    tokens.forEach { token ->
      val upper = token.uppercase().trim()
      if (upper.matches(Regex("^[A-Z]{2,4}-?[0-9]{2,4}$")) && storeCode.isBlank()) {
        storeCode = upper
      } else if (token.contains("Bank", ignoreCase = true) || token.contains("بنك", ignoreCase = true) || token.contains("SNB", ignoreCase = true) || token.contains("Rajhi", ignoreCase = true)) {
        ownerBank = token
      }
    }

    val (lat, lng) = extractLatLngFromUrl(accMapUrl)

    return Accommodation(
      areaName = area,
      villaNumber = villa,
      floorNumber = floor,
      roomNumber = room,
      totalWorkers = totalWorkers,
      totalCapacity = totalCapacity,
      activeWorkers = activeWorkers,
      accommodationLocationUrl = accMapUrl,
      storeLocationUrl = storeMapUrl,
      storeCode = storeCode,
      storeName = storeName,
      workerPhone = workerPhone1,
      workerPhone2 = workerPhone2,
      ownerName = ownerName,
      ownerPhone = ownerPhone,
      ownerBankName = ownerBank,
      ownerIban = ownerIban,
      googleMapsUrl = accMapUrl,
      latitude = lat,
      longitude = lng,
      stationName = storeName.ifBlank { storeCode },
      notes = notes
    )
  }

  private fun isPhoneNumber(text: String): Boolean {
    val digits = text.filter { it.isDigit() }
    return digits.length in 8..15 && (text.startsWith("+") || text.startsWith("05") || text.startsWith("966") || text.contains("-") || text.contains(" "))
  }

  private fun extractLatLngFromUrl(url: String): Pair<Double?, Double?> {
    if (url.isBlank()) return Pair(null, null)
    try {
      // Look for ?q=lat,lng or @lat,lng
      val regex1 = Regex("[?&]q=(-?\\d+\\.\\d+),(-?\\d+\\.\\d+)")
      val match1 = regex1.find(url)
      if (match1 != null) {
        val lat = match1.groupValues[1].toDoubleOrNull()
        val lng = match1.groupValues[2].toDoubleOrNull()
        return Pair(lat, lng)
      }

      val regex2 = Regex("@(-?\\d+\\.\\d+),(-?\\d+\\.\\d+)")
      val match2 = regex2.find(url)
      if (match2 != null) {
        val lat = match2.groupValues[1].toDoubleOrNull()
        val lng = match2.groupValues[2].toDoubleOrNull()
        return Pair(lat, lng)
      }
    } catch (e: Exception) {
      // Ignore
    }
    return Pair(null, null)
  }

  fun splitCsvOrTsv(line: String): List<String> {
    // Check if TSV (tab separated)
    if (line.contains("\t")) {
      return line.split("\t").map { cleanToken(it) }
    }

    // CSV with quote handling
    val tokens = mutableListOf<String>()
    var current = StringBuilder()
    var inQuotes = false

    for (i in line.indices) {
      val c = line[i]
      if (c == '\"') {
        inQuotes = !inQuotes
      } else if (c == ',' && !inQuotes) {
        tokens.add(cleanToken(current.toString()))
        current = StringBuilder()
      } else {
        current.append(c)
      }
    }
    tokens.add(cleanToken(current.toString()))
    return tokens
  }

  private fun cleanToken(raw: String): String {
    var s = raw.trim()
    if (s.startsWith("\"") && s.endsWith("\"") && s.length >= 2) {
      s = s.substring(1, s.length - 1).trim()
    }
    return s.replace("\"\"", "\"")
  }

  /**
   * Rich Google Sheets CSV sample data including all fields:
   * Total Worker, Total Capacity, Active Workers, Accommodation Location, Store Location,
   * Store Code, Worker Phone 1, Worker Phone 2, House Owner Details & IBAN.
   */
  fun getSampleGoogleSheetsData(): String {
    return """
Location Name,Villa Number,Floor,Room,Total Worker,Total Capacity,Active Workers,Accommodation Location,Store Location,Store Code,Store Name,Worker Phone 1,Worker Phone 2,Owner Name,Owner Phone,Owner Bank Name,Owner IBAN
Al Olaya District,14B,2nd Floor,201-203,14,16,12,https://maps.google.com/?q=24.7136,46.6753,https://maps.google.com/?q=24.7180,46.6800,ST-101,Olaya Central Store,+966501234567,+966509981122,Sheikh Abdullah Al-Mansoor,+966559876543,Al Rajhi Bank,SA4480000456608010123456
Al Malaz,Villa 28,Ground,101-102,10,12,9,https://maps.google.com/?q=24.6657,46.7369,https://maps.google.com/?q=24.6700,46.7400,ST-102,Malaz Logistics Store,+966543210987,+966548876655,Abu Fahad Al-Otaibi,+966566543210,Saudi National Bank (SNB),SA1210000001234567890123
Al Sulaimaniya,Villa 07,1st Floor,104-105,8,10,7,https://maps.google.com/?q=24.6984,46.7028,https://maps.google.com/?q=24.7000,46.7050,ST-103,Sulaimaniya Hub Store,+966538882345,+966537774433,Eng. Tariq Al-Ghamdi,+966504448765,Riyad Bank,SA5520000009876543210987
Al Yasmin,Villa 52,2nd Floor,205-207,12,14,11,https://maps.google.com/?q=24.8122,46.6433,https://maps.google.com/?q=24.8150,46.6450,ST-104,Yasmin Express Store,+966591112233,+966592223344,Fahad Bin Nasser,+966507778899,Banque Saudi Fransi,SA3350000002345678901234
    """.trimIndent()
  }

  fun getSampleSingleAccommodationData(): String {
    return """
Location: Al Sahafa District
Villa: 19A
Floor: 1st Floor
Room: 101, 102
Total Workers: 10
Total Capacity: 12
Active Workers: 9
Accommodation Location: https://maps.google.com/?q=24.8010,46.6420
Store Location: https://maps.google.com/?q=24.8050,46.6450
Store Code: ST-105
Store Name: Sahafa Prime Store
Worker Phone 1: +966 58 444 3322
Worker Phone 2: +966 58 555 4433
Owner Name: Bandar Al-Harbi
Owner Phone: +966 55 222 1100
Owner Bank: Alinma Bank
Owner IBAN: SA7705000000123456789012
Notes: Modern housing unit with all required amenities
    """.trimIndent()
  }
}
