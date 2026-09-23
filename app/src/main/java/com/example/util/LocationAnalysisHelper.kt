package com.example.util

import com.example.data.model.Accommodation
import com.example.data.model.Station
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class AccommodationProximity(
  val accommodation: Accommodation,
  val nearestStation: Station?,
  val distanceKm: Double?,
  val hasValidCoordinates: Boolean
)

data class StationCoverage(
  val station: Station,
  val assignedCount: Int,
  val nearbyCountWithin5Km: Int,
  val nearbyCountWithin10Km: Int,
  val nearestAccommodations: List<AccommodationProximity>
)

data class LocationAnalysisReport(
  val totalAccommodations: Int,
  val accommodationsWithGps: Int,
  val accommodationsMissingGps: Int,
  val totalStations: Int,
  val averageDistanceKm: Double?,
  val areaDistribution: Map<String, Int>,
  val stationCoverages: List<StationCoverage>,
  val proximities: List<AccommodationProximity>,
  val insights: List<String>
)

object LocationAnalysisHelper {

  private const val EARTH_RADIUS_KM = 6371.0

  /**
   * Calculates Haversine distance in kilometers between two GPS coordinates.
   */
  fun calculateDistanceKm(
    lat1: Double,
    lon1: Double,
    lat2: Double,
    lon2: Double
  ): Double {
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
      cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
      sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return EARTH_RADIUS_KM * c
  }

  /**
   * Performs automatic analysis of all accommodations and stations.
   */
  fun analyzeLocations(
    accommodations: List<Accommodation>,
    stations: List<Station>
  ): LocationAnalysisReport {
    val totalAcc = accommodations.size
    val withGps = accommodations.filter { it.latitude != null && it.longitude != null }
    val withoutGps = accommodations.filter { it.latitude == null || it.longitude == null }

    val proximities = accommodations.map { acc ->
      if (acc.latitude != null && acc.longitude != null && stations.isNotEmpty()) {
        val validStations = stations.filter { it.latitude != null && it.longitude != null }
        val nearest = validStations.minByOrNull { st ->
          calculateDistanceKm(acc.latitude, acc.longitude, st.latitude!!, st.longitude!!)
        }
        val dist = if (nearest != null) {
          calculateDistanceKm(acc.latitude, acc.longitude, nearest.latitude!!, nearest.longitude!!)
        } else null

        AccommodationProximity(
          accommodation = acc,
          nearestStation = nearest,
          distanceKm = dist,
          hasValidCoordinates = true
        )
      } else {
        // Fallback matching by station name or unlinked
        val matchByName = stations.firstOrNull { it.name.equals(acc.stationName, ignoreCase = true) }
        AccommodationProximity(
          accommodation = acc,
          nearestStation = matchByName,
          distanceKm = null,
          hasValidCoordinates = false
        )
      }
    }

    // Station coverages
    val stationCoverages = stations.map { station ->
      val assigned = accommodations.count { it.stationName.equals(station.name, ignoreCase = true) }
      val nearby5 = proximities.count {
        it.nearestStation?.id == station.id && it.distanceKm != null && it.distanceKm <= 5.0
      }
      val nearby10 = proximities.count {
        it.nearestStation?.id == station.id && it.distanceKm != null && it.distanceKm <= 10.0
      }
      val closestProximities = proximities
        .filter { it.nearestStation?.id == station.id }
        .sortedBy { it.distanceKm ?: Double.MAX_VALUE }

      StationCoverage(
        station = station,
        assignedCount = assigned,
        nearbyCountWithin5Km = nearby5,
        nearbyCountWithin10Km = nearby10,
        nearestAccommodations = closestProximities
      )
    }

    // Average distance
    val distances = proximities.mapNotNull { it.distanceKm }
    val avgDist = if (distances.isNotEmpty()) distances.average() else null

    // Area distribution
    val areaDist = accommodations.groupingBy {
      it.areaName.ifBlank { "Unassigned Area" }
    }.eachCount()

    // Generate smart operational insights
    val insights = mutableListOf<String>()
    if (totalAcc == 0) {
      insights.add("No accommodations loaded yet. Use Bulk Upload to import property records.")
    } else {
      insights.add("Analyzed $totalAcc total housing units across ${areaDist.size} regional districts.")
      if (avgDist != null) {
        insights.add(String.format("Average distance to closest company station is %.1f km.", avgDist))
      }
      if (stations.isNotEmpty()) {
        val topStation = stationCoverages.maxByOrNull { it.nearestAccommodations.size }
        if (topStation != null) {
          insights.add("${topStation.station.name} serves ${topStation.nearestAccommodations.size} nearby accommodations.")
        }
      }
      if (withoutGps.isNotEmpty()) {
        insights.add("${withoutGps.size} accommodation(s) require GPS coordinate entry for exact distance routing.")
      } else {
        insights.add("100% of accommodations have validated GPS coordinates.")
      }
    }

    return LocationAnalysisReport(
      totalAccommodations = totalAcc,
      accommodationsWithGps = withGps.size,
      accommodationsMissingGps = withoutGps.size,
      totalStations = stations.size,
      averageDistanceKm = avgDist,
      areaDistribution = areaDist,
      stationCoverages = stationCoverages,
      proximities = proximities,
      insights = insights
    )
  }
}
