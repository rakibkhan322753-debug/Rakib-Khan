package com.example.ui.components

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.Accommodation
import com.example.ui.theme.DarkGreen
import com.example.ui.theme.LightGreen
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.ZawitcoBlue
import com.example.ui.theme.ZawitcoLightBlue
import com.example.ui.theme.ZawitcoOrange
import com.example.util.GpsLocationHelper

data class LocationPreset(
  val name: String,
  val lat: Double,
  val lng: Double
)

val commonSaudiPresets = listOf(
  LocationPreset("Al Olaya, Riyadh", 24.7136, 46.6753),
  LocationPreset("Al Malaz, Riyadh", 24.6657, 46.7369),
  LocationPreset("Al Sulaimaniya", 24.6984, 46.7028),
  LocationPreset("Al Yasmin, Riyadh", 24.8211, 46.6342),
  LocationPreset("Al Narjis, Riyadh", 24.8450, 46.6700),
  LocationPreset("Al Khobar Corniche", 26.2842, 50.2084),
  LocationPreset("Al Hamra, Jeddah", 21.5169, 39.1558)
)

@Composable
fun AddEditAccommodationDialog(
  initialItem: Accommodation?,
  onDismiss: () -> Unit,
  onSave: (
    id: Long,
    areaName: String,
    villaNumber: String,
    floorNumber: String,
    roomNumber: String,
    workerPhone: String,
    ownerPhone: String,
    googleMapsUrl: String,
    latitude: Double?,
    longitude: Double?,
    buildingImageUri: String?,
    notes: String,
    whatsappGroupUrl: String
  ) -> Unit,
  onSaveImageToStorage: (Uri) -> String?,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  var areaName by remember { mutableStateOf(initialItem?.areaName ?: "") }
  var villaNumber by remember { mutableStateOf(initialItem?.villaNumber ?: "") }
  var floorNumber by remember { mutableStateOf(initialItem?.floorNumber ?: "") }
  var roomNumber by remember { mutableStateOf(initialItem?.roomNumber ?: "") }
  var workerPhone by remember { mutableStateOf(initialItem?.workerPhone ?: "") }
  var ownerPhone by remember { mutableStateOf(initialItem?.ownerPhone ?: "") }
  var googleMapsUrl by remember { mutableStateOf(initialItem?.googleMapsUrl ?: "") }
  var latitudeStr by remember { mutableStateOf(initialItem?.latitude?.toString() ?: "") }
  var longitudeStr by remember { mutableStateOf(initialItem?.longitude?.toString() ?: "") }
  var imageUri by remember { mutableStateOf(initialItem?.buildingImageUri) }
  var notes by remember { mutableStateOf(initialItem?.notes ?: "") }
  var whatsappGroupUrl by remember { mutableStateOf(initialItem?.whatsappGroupUrl ?: "") }

  var areaNameError by remember { mutableStateOf(false) }
  var isGpsLoading by remember { mutableStateOf(false) }
  var gpsStatusMessage by remember { mutableStateOf<String?>(null) }

  fun fetchCurrentGpsLocation() {
    isGpsLoading = true
    gpsStatusMessage = "Detecting current coordinates..."
    GpsLocationHelper.getCurrentCoordinates(
      context = context,
      onSuccess = { lat, lng ->
        isGpsLoading = false
        latitudeStr = lat.toString()
        longitudeStr = lng.toString()
        googleMapsUrl = "https://maps.google.com/?q=$lat,$lng"
        gpsStatusMessage = "GPS coordinates locked: $lat, $lng"
        Toast.makeText(context, "Current GPS location locked!", Toast.LENGTH_SHORT).show()
      },
      onError = { err ->
        isGpsLoading = false
        gpsStatusMessage = "GPS Error: $err"
        Toast.makeText(context, err, Toast.LENGTH_LONG).show()
      }
    )
  }

  val locationPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions()
  ) { permissions ->
    val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    if (granted) {
      fetchCurrentGpsLocation()
    } else {
      isGpsLoading = false
      gpsStatusMessage = "Location permission denied."
      Toast.makeText(context, "Location permission is required for GPS access.", Toast.LENGTH_SHORT).show()
    }
  }

  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { selectedUri: Uri? ->
    if (selectedUri != null) {
      val savedPath = onSaveImageToStorage(selectedUri)
      imageUri = savedPath ?: selectedUri.toString()
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.92f)
        .clip(RoundedCornerShape(24.dp)),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 8.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(20.dp)
      ) {
        // Dialog Header with Zawitco Brand
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          ZawitcoCompanyLogo(height = 36.dp, showSubtext = false)

          Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
            Text(
              text = if (initialItem == null) "Add Accommodation" else "Edit Accommodation",
              fontSize = 19.sp,
              fontWeight = FontWeight.Bold,
              color = ZawitcoBlue
            )
            Text(
              text = "Admin property management",
              fontSize = 12.sp,
              color = Slate600
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_add_edit_dialog")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = Slate600
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Building Image Upload Section
        Text(
          text = "Building Image",
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          color = Slate700
        )
        Spacer(modifier = Modifier.height(6.dp))

        if (!imageUri.isNullOrBlank()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(160.dp)
              .clip(RoundedCornerShape(12.dp))
          ) {
            AsyncImage(
              model = imageUri,
              contentDescription = "Selected building image",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxWidth()
            )
            IconButton(
              onClick = { imageUri = null },
              modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                .size(36.dp)
            ) {
              Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = "Remove image",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        } else {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(110.dp)
              .clip(RoundedCornerShape(12.dp))
              .border(1.dp, Slate200, RoundedCornerShape(12.dp))
              .background(Slate100)
              .clickable {
                photoPickerLauncher.launch(
                  PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
              }
              .testTag("pick_building_image_button"),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Outlined.AddAPhoto,
                contentDescription = null,
                tint = ZawitcoBlue,
                modifier = Modifier.size(30.dp)
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Select Building Photo",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = ZawitcoBlue
              )
              Text(
                text = "Tap to choose photo from gallery or camera",
                fontSize = 11.sp,
                color = Slate400
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Area Name & Villa Number
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = areaName,
            onValueChange = {
              areaName = it
              if (it.isNotBlank()) areaNameError = false
            },
            label = { Text("Area Name *") },
            isError = areaNameError,
            supportingText = if (areaNameError) {
              { Text("Area Name is required") }
            } else null,
            singleLine = true,
            modifier = Modifier
              .weight(1.3f)
              .testTag("input_area_name")
          )

          OutlinedTextField(
            value = villaNumber,
            onValueChange = { villaNumber = it },
            label = { Text("Villa Number") },
            placeholder = { Text("e.g. 14B") },
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("input_villa_number")
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Floor Number & Room Number
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = floorNumber,
            onValueChange = { floorNumber = it },
            label = { Text("Floor Number") },
            placeholder = { Text("e.g. 1st Floor") },
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("input_floor_number")
          )

          OutlinedTextField(
            value = roomNumber,
            onValueChange = { roomNumber = it },
            label = { Text("Room Number(s)") },
            placeholder = { Text("e.g. 101, 102") },
            singleLine = true,
            modifier = Modifier
              .weight(1.2f)
              .testTag("input_room_number")
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Worker's Phone & Owner's Phone
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = workerPhone,
            onValueChange = { workerPhone = it },
            label = { Text("Worker's Phone") },
            placeholder = { Text("+966 5...") },
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("input_worker_phone")
          )

          OutlinedTextField(
            value = ownerPhone,
            onValueChange = { ownerPhone = it },
            label = { Text("Owner's Phone") },
            placeholder = { Text("+966 5...") },
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("input_owner_phone")
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // WhatsApp Group Link input
        OutlinedTextField(
          value = whatsappGroupUrl,
          onValueChange = { whatsappGroupUrl = it },
          label = { Text("WhatsApp Group / Chat Link") },
          placeholder = { Text("https://chat.whatsapp.com/... or https://wa.me/...") },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Group,
              contentDescription = null,
              tint = Color(0xFF25D366),
              modifier = Modifier.size(20.dp)
            )
          },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_whatsapp_group_url")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Google Maps URL
        OutlinedTextField(
          value = googleMapsUrl,
          onValueChange = { googleMapsUrl = it },
          label = { Text("Google Maps URL (Paste Link)") },
          placeholder = { Text("https://maps.google.com/...") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_maps_url")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // GPS Access & Coordinates section
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Coordinates & GPS Location",
              fontSize = 14.sp,
              fontWeight = FontWeight.SemiBold,
              color = Slate700
            )
            Text(
              text = "Use device GPS or select a preset area",
              fontSize = 12.sp,
              color = Slate600
            )
          }

          // Live GPS acquisition button
          Button(
            onClick = {
              if (GpsLocationHelper.hasLocationPermission(context)) {
                fetchCurrentGpsLocation()
              } else {
                locationPermissionLauncher.launch(
                  arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                  )
                )
              }
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = ZawitcoBlue,
              contentColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("use_current_gps_button")
          ) {
            if (isGpsLoading) {
              CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.dp,
                modifier = Modifier.size(16.dp)
              )
            } else {
              Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text("Use GPS", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }

        if (gpsStatusMessage != null) {
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = gpsStatusMessage ?: "",
            fontSize = 12.sp,
            color = if (gpsStatusMessage?.contains("Error", true) == true) MaterialTheme.colorScheme.error else DarkGreen,
            fontWeight = FontWeight.Medium
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Preset Chips
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          commonSaudiPresets.forEach { preset ->
            val isSelected = areaName.contains(preset.name.split(",")[0].trim(), ignoreCase = true)
            Surface(
              onClick = {
                if (areaName.isBlank()) {
                  areaName = preset.name.split(",")[0].trim()
                }
                latitudeStr = preset.lat.toString()
                longitudeStr = preset.lng.toString()
                if (googleMapsUrl.isBlank()) {
                  googleMapsUrl = "https://maps.google.com/?q=${preset.lat},${preset.lng}"
                }
              },
              shape = RoundedCornerShape(20.dp),
              color = if (isSelected) ZawitcoLightBlue else Slate100,
              border = BorderStroke(1.dp, if (isSelected) ZawitcoBlue else Slate200)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Outlined.LocationOn,
                  contentDescription = null,
                  tint = if (isSelected) ZawitcoBlue else Slate600,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = preset.name,
                  fontSize = 12.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  color = if (isSelected) ZawitcoBlue else Slate700
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = latitudeStr,
            onValueChange = { latitudeStr = it },
            label = { Text("Latitude") },
            placeholder = { Text("24.7136") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )

          OutlinedTextField(
            value = longitudeStr,
            onValueChange = { longitudeStr = it },
            label = { Text("Longitude") },
            placeholder = { Text("46.6753") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Notes
        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Additional Notes & Housing Amenities") },
          placeholder = { Text("e.g. WiFi included, 3 ACs, near supermarket...") },
          maxLines = 3,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_notes")
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Save button
        Button(
          onClick = {
            if (areaName.isBlank()) {
              areaNameError = true
              return@Button
            }

            val lat = latitudeStr.toDoubleOrNull()
            val lng = longitudeStr.toDoubleOrNull()

            onSave(
              initialItem?.id ?: 0L,
              areaName,
              villaNumber,
              floorNumber,
              roomNumber,
              workerPhone,
              ownerPhone,
              googleMapsUrl,
              lat,
              lng,
              imageUri,
              notes,
              whatsappGroupUrl
            )
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = ZawitcoBlue,
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("save_accommodation_button")
        ) {
          Icon(
            imageVector = Icons.Outlined.Check,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Save Accommodation",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )
        }
      }
    }
  }
}
