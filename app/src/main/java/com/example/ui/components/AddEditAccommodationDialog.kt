package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.ZawitcoBlue
import com.example.ui.theme.ZawitcoLightBlue
import com.example.ui.theme.ZawitcoOrange

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
    billingPictureUri: String?,
    doorPictureUri: String?,
    notes: String,
    whatsappGroupUrl: String,
    stationName: String
  ) -> Unit,
  onSaveImageToStorage: (Uri) -> String?,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  var areaName by remember { mutableStateOf(initialItem?.areaName ?: "") }
  var villaNumber by remember { mutableStateOf(initialItem?.villaNumber ?: "") }
  var floorNumber by remember { mutableStateOf(initialItem?.floorNumber ?: "") }
  var roomNumber by remember { mutableStateOf(initialItem?.roomNumber ?: "") }
  var stationName by remember { mutableStateOf(initialItem?.stationName ?: "") }
  var workerPhone by remember { mutableStateOf(initialItem?.workerPhone ?: "") }
  var ownerPhone by remember { mutableStateOf(initialItem?.ownerPhone ?: "") }
  var googleMapsUrl by remember { mutableStateOf(initialItem?.googleMapsUrl ?: "") }
  var latitudeStr by remember { mutableStateOf(initialItem?.latitude?.toString() ?: "") }
  var longitudeStr by remember { mutableStateOf(initialItem?.longitude?.toString() ?: "") }
  var billingPictureUri by remember {
    mutableStateOf(initialItem?.billingPictureUri ?: initialItem?.buildingImageUri)
  }
  var doorPictureUri by remember { mutableStateOf(initialItem?.doorPictureUri) }
  var notes by remember { mutableStateOf(initialItem?.notes ?: "") }
  var whatsappGroupUrl by remember { mutableStateOf(initialItem?.whatsappGroupUrl ?: "") }

  var areaNameError by remember { mutableStateOf(false) }

  // Activity Result Launchers for Billing Picture and Door Picture
  val billingPhotoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { selectedUri: Uri? ->
    if (selectedUri != null) {
      val savedPath = onSaveImageToStorage(selectedUri)
      billingPictureUri = savedPath ?: selectedUri.toString()
    }
  }

  val doorPhotoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { selectedUri: Uri? ->
    if (selectedUri != null) {
      val savedPath = onSaveImageToStorage(selectedUri)
      doorPictureUri = savedPath ?: selectedUri.toString()
    }
  }

  val defaultTextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.Black,
    unfocusedTextColor = Color.Black,
    focusedBorderColor = ZawitcoBlue,
    unfocusedBorderColor = Slate200,
    focusedLabelColor = ZawitcoBlue,
    unfocusedLabelColor = Color.Black,
    focusedContainerColor = Color.White,
    unfocusedContainerColor = Color.White
  )

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.92f)
        .clip(RoundedCornerShape(24.dp)),
      color = Color.White,
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
              color = Color.Black
            )
            Text(
              text = "Zawitco property & photos record",
              fontSize = 12.sp,
              color = Color.Black
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_add_edit_dialog")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = Color.Black
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ==========================================
        // 2 REQUIRED PICTURES (1. Billing Picture, 2. Door Picture)
        // ==========================================
        Text(
          text = "Required Accommodation Photos (2 Pictures)",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = Color.Black
        )
        Text(
          text = "Every accommodation record requires 1. Billing Picture and 2. Door Picture",
          fontSize = 12.sp,
          color = Color.Black
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // 1. BILLING PICTURE UPLOAD CARD
          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                tint = ZawitcoOrange,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "1. Billing Picture",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
              )
            }
            Spacer(modifier = Modifier.height(6.dp))

            if (!billingPictureUri.isNullOrBlank()) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(130.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .border(1.dp, Slate200, RoundedCornerShape(12.dp))
              ) {
                AsyncImage(
                  model = billingPictureUri,
                  contentDescription = "Billing Photo",
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.fillMaxWidth()
                )
                IconButton(
                  onClick = { billingPictureUri = null },
                  modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                    .size(28.dp)
                    .testTag("remove_billing_picture_button")
                ) {
                  Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Remove billing picture",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                  )
                }
              }
            } else {
              Surface(
                onClick = {
                  billingPhotoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                  )
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(130.dp)
                  .testTag("add_billing_picture_button"),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFFF7ED),
                border = BorderStroke(1.5.dp, ZawitcoOrange.copy(alpha = 0.5f))
              ) {
                Column(
                  modifier = Modifier.padding(8.dp),
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.Center
                ) {
                  Icon(
                    imageVector = Icons.Outlined.AddAPhoto,
                    contentDescription = null,
                    tint = ZawitcoOrange,
                    modifier = Modifier.size(28.dp)
                  )
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = "Upload Billing Photo",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                  )
                  Text(
                    text = "Invoice / Bill paper",
                    fontSize = 10.sp,
                    color = Color.Black
                  )
                }
              }
            }
          }

          // 2. DOOR PICTURE UPLOAD CARD
          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.MeetingRoom,
                contentDescription = null,
                tint = ZawitcoBlue,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "2. Door Picture",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
              )
            }
            Spacer(modifier = Modifier.height(6.dp))

            if (!doorPictureUri.isNullOrBlank()) {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(130.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .border(1.dp, Slate200, RoundedCornerShape(12.dp))
              ) {
                AsyncImage(
                  model = doorPictureUri,
                  contentDescription = "Door Photo",
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.fillMaxWidth()
                )
                IconButton(
                  onClick = { doorPictureUri = null },
                  modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                    .size(28.dp)
                    .testTag("remove_door_picture_button")
                ) {
                  Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Remove door picture",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                  )
                }
              }
            } else {
              Surface(
                onClick = {
                  doorPhotoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                  )
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(130.dp)
                  .testTag("add_door_picture_button"),
                shape = RoundedCornerShape(12.dp),
                color = ZawitcoLightBlue,
                border = BorderStroke(1.5.dp, ZawitcoBlue.copy(alpha = 0.5f))
              ) {
                Column(
                  modifier = Modifier.padding(8.dp),
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.Center
                ) {
                  Icon(
                    imageVector = Icons.Outlined.PhotoCamera,
                    contentDescription = null,
                    tint = ZawitcoBlue,
                    modifier = Modifier.size(28.dp)
                  )
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = "Upload Door Photo",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                  )
                  Text(
                    text = "Room/Villa entrance",
                    fontSize = 10.sp,
                    color = Color.Black
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ==========================================
        // TEXT DATA FIELDS
        // ==========================================
        Text(
          text = "Accommodation Information",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = Color.Black
        )

        Spacer(modifier = Modifier.height(10.dp))

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
            label = { Text("Area Name *", color = Color.Black) },
            placeholder = { Text("e.g. Al Olaya District", color = Slate400) },
            isError = areaNameError,
            supportingText = if (areaNameError) {
              { Text("Area Name is required", color = Color.Red) }
            } else null,
            singleLine = true,
            colors = defaultTextFieldColors,
            modifier = Modifier
              .weight(1.3f)
              .testTag("input_area_name")
          )

          OutlinedTextField(
            value = villaNumber,
            onValueChange = { villaNumber = it },
            label = { Text("Villa Number", color = Color.Black) },
            placeholder = { Text("e.g. 14B", color = Slate400) },
            singleLine = true,
            colors = defaultTextFieldColors,
            modifier = Modifier
              .weight(1f)
              .testTag("input_villa_number")
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Floor Number, Room Number & Station Name
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = floorNumber,
            onValueChange = { floorNumber = it },
            label = { Text("Floor Number", color = Color.Black) },
            placeholder = { Text("e.g. 2nd Floor", color = Slate400) },
            singleLine = true,
            colors = defaultTextFieldColors,
            modifier = Modifier
              .weight(1f)
              .testTag("input_floor_number")
          )

          OutlinedTextField(
            value = roomNumber,
            onValueChange = { roomNumber = it },
            label = { Text("Room Number(s)", color = Color.Black) },
            placeholder = { Text("e.g. 201, 202", color = Slate400) },
            singleLine = true,
            colors = defaultTextFieldColors,
            modifier = Modifier
              .weight(1.2f)
              .testTag("input_room_number")
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Assigned Station Hub
        OutlinedTextField(
          value = stationName,
          onValueChange = { stationName = it },
          label = { Text("Assigned Station Hub", color = Color.Black) },
          placeholder = { Text("e.g. Riyadh Central Hub, Dammam Hub", color = Slate400) },
          leadingIcon = {
            Icon(Icons.Default.Hub, contentDescription = null, tint = ZawitcoBlue)
          },
          singleLine = true,
          colors = defaultTextFieldColors,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_station_name")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Coordinates: Latitude & Longitude
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = latitudeStr,
            onValueChange = { latitudeStr = it },
            label = { Text("Latitude", color = Color.Black) },
            placeholder = { Text("24.7136", color = Slate400) },
            singleLine = true,
            colors = defaultTextFieldColors,
            modifier = Modifier
              .weight(1f)
              .testTag("input_latitude")
          )

          OutlinedTextField(
            value = longitudeStr,
            onValueChange = { longitudeStr = it },
            label = { Text("Longitude", color = Color.Black) },
            placeholder = { Text("46.6753", color = Slate400) },
            singleLine = true,
            colors = defaultTextFieldColors,
            modifier = Modifier
              .weight(1f)
              .testTag("input_longitude")
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
            label = { Text("Worker's Phone", color = Color.Black) },
            placeholder = { Text("+966 5...", color = Slate400) },
            singleLine = true,
            colors = defaultTextFieldColors,
            modifier = Modifier
              .weight(1f)
              .testTag("input_worker_phone")
          )

          OutlinedTextField(
            value = ownerPhone,
            onValueChange = { ownerPhone = it },
            label = { Text("Owner's Phone", color = Color.Black) },
            placeholder = { Text("+966 5...", color = Slate400) },
            singleLine = true,
            colors = defaultTextFieldColors,
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
          label = { Text("WhatsApp Group / Contact Link", color = Color.Black) },
          placeholder = { Text("https://chat.whatsapp.com/... or https://wa.me/...", color = Slate400) },
          singleLine = true,
          colors = defaultTextFieldColors,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_whatsapp_group_url")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Google Maps URL (Direct link if available)
        OutlinedTextField(
          value = googleMapsUrl,
          onValueChange = { googleMapsUrl = it },
          label = { Text("Google Maps URL (Optional Link)", color = Color.Black) },
          placeholder = { Text("https://maps.google.com/...", color = Slate400) },
          singleLine = true,
          colors = defaultTextFieldColors,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_maps_url")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Notes & Amenities
        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Accommodation Notes & Amenities", color = Color.Black) },
          placeholder = { Text("e.g. WiFi included, 3 ACs, near supermarket...", color = Slate400) },
          maxLines = 3,
          colors = defaultTextFieldColors,
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

            val lat = latitudeStr.toDoubleOrNull() ?: initialItem?.latitude
            val lng = longitudeStr.toDoubleOrNull() ?: initialItem?.longitude

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
              billingPictureUri ?: initialItem?.buildingImageUri,
              billingPictureUri,
              doorPictureUri,
              notes,
              whatsappGroupUrl,
              stationName
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
