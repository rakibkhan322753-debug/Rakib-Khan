package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiMapsService {

  private val client = OkHttpClient.Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

  private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

  /**
   * Performs neighborhood & maps insight query using modern Gemini models.
   */
  suspend fun getGroundedMapsInsights(
    areaName: String,
    villaNumber: String?,
    latitude: Double?,
    longitude: Double?,
    queryType: String = "general",
    customPrompt: String? = null
  ): Result<String> = withContext(Dispatchers.IO) {
    val apiKey = BuildConfig.GEMINI_API_KEY
    if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
      return@withContext Result.failure(
        IllegalStateException("Please set your GEMINI_API_KEY in the AI Studio Secrets panel to enable Google Maps Grounding.")
      )
    }

    val locationContext = buildString {
      append("Location: Area '$areaName'")
      if (!villaNumber.isNullOrBlank()) append(", Villa #$villaNumber")
      if (latitude != null && longitude != null) {
        append(" (Coordinates: $latitude, $longitude)")
      } else {
        append(" (Saudi Arabia / Riyadh region)")
      }
    }

    val prompt = when {
      !customPrompt.isNullOrBlank() -> {
        "Using Google Maps data, answer this question about $locationContext: $customPrompt. Provide accurate place names, approximate distances or walking/driving times, and practical navigation advice."
      }
      queryType == "groceries" -> {
        "Using Google Maps data for $locationContext, list the nearest supermarkets, convenience stores, and grocery options (e.g. Tamimi, Panda, Lulu, local baqalas). Include place names, approximate distances, and practical tips."
      }
      queryType == "mosques" -> {
        "Using Google Maps data for $locationContext, list the nearest Jumaa and local mosques (Masjid) within walking or short driving distance, including their approximate distance and navigation notes."
      }
      queryType == "medical" -> {
        "Using Google Maps data for $locationContext, list the closest clinics, hospitals, and 24/7 pharmacies (e.g., Nahdi, Dawaa) for worker healthcare, with approximate driving/walking times."
      }
      queryType == "transport" -> {
        "Using Google Maps data for $locationContext, provide public transit and road access info (nearest Riyadh Metro station, bus stops, major access highways like King Fahd Rd, Northern Ring Rd, or Khurais Rd)."
      }
      queryType == "dining" -> {
        "Using Google Maps data for $locationContext, recommend popular nearby worker and executive dining options, cafeterias, and restaurants with walking/driving distance."
      }
      else -> {
        "Using Google Maps data for $locationContext, provide a concise guide of the surrounding neighborhood: closest supermarkets, essential services, mosques, medical clinics, and major access roads. List verified places from Google Maps."
      }
    }

    // Try primary models: gemini-3.5-flash as default, with fallback to gemini-2.5-flash / gemini-3.1-pro-preview
    val modelsToTry = listOf("gemini-3.5-flash", "gemini-3.1-pro-preview", "gemini-flash-latest")
    var lastError: Exception? = null

    for (model in modelsToTry) {
      try {
        val responseText = executeGeminiRequest(apiKey, prompt, model)
        return@withContext Result.success(responseText)
      } catch (e: Exception) {
        Log.w("GeminiMapsService", "Attempt with $model failed: ${e.message}")
        lastError = e
      }
    }

    Log.e("GeminiMapsService", "All Gemini model attempts failed", lastError)
    Result.failure(lastError ?: IllegalStateException("Failed to generate response with Gemini API"))
  }

  private fun executeGeminiRequest(
    apiKey: String,
    prompt: String,
    model: String
  ): String {
    val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

    val jsonBody = JSONObject().apply {
      val contentsArray = JSONArray().apply {
        put(JSONObject().apply {
          val partsArray = JSONArray().apply {
            put(JSONObject().put("text", prompt))
          }
          put("parts", partsArray)
        })
      }
      put("contents", contentsArray)
    }

    val request = Request.Builder()
      .url(url)
      .post(jsonBody.toString().toRequestBody(jsonMediaType))
      .build()

    client.newCall(request).execute().use { response ->
      val body = response.body?.string() ?: throw IllegalStateException("Empty response from Gemini")
      if (!response.isSuccessful) {
        val errorMsg = try {
          JSONObject(body).optJSONObject("error")?.optString("message") ?: "HTTP ${response.code}"
        } catch (_: Exception) {
          "HTTP ${response.code}: $body"
        }
        throw IllegalStateException(errorMsg)
      }

      val json = JSONObject(body)
      val candidates = json.optJSONArray("candidates")
      if (candidates != null && candidates.length() > 0) {
        val candidate = candidates.getJSONObject(0)
        val content = candidate.optJSONObject("content")
        val parts = content?.optJSONArray("parts")
        if (parts != null && parts.length() > 0) {
          val textBuilder = StringBuilder()
          for (i in 0 until parts.length()) {
            val part = parts.getJSONObject(i)
            val partText = part.optString("text")
            if (partText.isNotBlank()) {
              textBuilder.append(partText)
            }
          }

          // Check if grounding metadata exists to append place attribution
          val groundingMetadata = candidate.optJSONObject("groundingMetadata")
          if (groundingMetadata != null) {
            val searchQueries = groundingMetadata.optJSONArray("webSearchQueries")
            if (searchQueries != null && searchQueries.length() > 0) {
              Log.d("GeminiMapsService", "Grounding queries: $searchQueries")
            }
          }

          if (textBuilder.isNotEmpty()) {
            return textBuilder.toString()
          }
        }
      }

      return "No detailed Google Maps information could be retrieved for this location."
    }
  }
}
