package com.example.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import androidx.core.content.ContextCompat
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object GpsLocationHelper {

  fun hasLocationPermission(context: Context): Boolean {
    val fine = ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val coarse = ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    return fine || coarse
  }

  @SuppressLint("MissingPermission")
  fun getCurrentCoordinates(
    context: Context,
    onSuccess: (lat: Double, lng: Double) -> Unit,
    onError: (message: String) -> Unit
  ) {
    if (!hasLocationPermission(context)) {
      onError("GPS location permission not granted.")
      return
    }

    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    if (locationManager == null) {
      onError("Location service not available.")
      return
    }

    val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    if (!isGpsEnabled && !isNetworkEnabled) {
      onError("GPS is disabled. Please enable device location.")
      return
    }

    // Try last known location first for fast response
    var bestLocation: Location? = null
    if (isGpsEnabled) {
      bestLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
    }
    if (bestLocation == null && isNetworkEnabled) {
      bestLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
    }

    if (bestLocation != null) {
      onSuccess(bestLocation.latitude, bestLocation.longitude)
      return
    }

    // Otherwise request single location update
    val provider = if (isGpsEnabled) LocationManager.GPS_PROVIDER else LocationManager.NETWORK_PROVIDER
    val listener = object : LocationListener {
      override fun onLocationChanged(location: Location) {
        onSuccess(location.latitude, location.longitude)
        locationManager.removeUpdates(this)
      }

      @Deprecated("Deprecated in Java")
      override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
      override fun onProviderEnabled(provider: String) {}
      override fun onProviderDisabled(provider: String) {}
    }

    try {
      locationManager.requestSingleUpdate(provider, listener, Looper.getMainLooper())
    } catch (e: Exception) {
      onError(e.localizedMessage ?: "Failed to retrieve GPS location.")
    }
  }

  /**
   * Calculates distance between two coordinates in kilometers using Haversine formula
   */
  fun calculateDistanceKm(
    lat1: Double,
    lon1: Double,
    lat2: Double,
    lon2: Double
  ): Double {
    val earthRadiusKm = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val rLat1 = Math.toRadians(lat1)
    val rLat2 = Math.toRadians(lat2)

    val a = sin(dLat / 2) * sin(dLat / 2) +
        cos(rLat1) * cos(rLat2) *
        sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadiusKm * c
  }

  /**
   * Formats distance into readable String (e.g., "650 m away" or "3.2 km away")
   */
  fun formatDistance(distanceKm: Double): String {
    return if (distanceKm < 1.0) {
      val meters = (distanceKm * 1000).toInt()
      "$meters m away"
    } else {
      val formatted = String.format("%.1f", distanceKm)
      "$formatted km away"
    }
  }
}
