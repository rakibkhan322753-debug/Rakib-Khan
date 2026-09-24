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
import androidx.compose.material.icons.filled.MeetingRoom
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
import com.example.ui.theme.ZawitcoDarkOrange
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
    totalWorkers: Int,
    totalCapacity: Int,
    activeWorkers: Int,
    accommodationLocationUrl: String,
    storeLocationUrl: String,
    storeCode: String,
    storeName: String,
    workerPhone: String,
    workerPhone2: String,
    ownerName: String,
    ownerPhone: String,
    ownerBankName: String,
    ownerIban: String,
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

  var totalWorkers by remember { mutableStateOf((initialItem?.totalWorkers ?: 0).toString()) }
  var totalCapacity by remember { mutableStateOf((initialItem?.totalCapacity ?: 0).toString()) }
  var activeWorkers by remember { mutableStateOf((initialItem?.activeWorkers ?: 0).toString()) }

  var accommodationLocationUrl by remember { mutableStateOf(initialItem?.accommodationLocationUrl ?: initialItem?.googleMapsUrl ?: "") }
  var storeLocationUrl by remember { mutableStateOf(initialItem?.storeLocationUrl ?: "") }
  var storeCode by remember { mutableStateOf(initialItem?.storeCode ?: "") }
  var storeName by remember { mutableStateOf(initialItem?.storeName ?: "") }

  var workerPhone by remember { mutableStateOf(initialItem?.workerPhone ?: "") }
  var workerPhone2 by remember { mutableStateOf(initialItem?.workerPhone2 ?: "") }

  var ownerName by remember { mutableStateOf(initialItem?.ownerName ?: "") }
  var ownerPhone by remember { mutableStateOf(initialItem?.ownerPhone ?: "") }
  var ownerBankName by remember { mutableStateOf(initialItem?.ownerBankName ?: "") }
  var ownerIban by remember { mutableStateOf(initialItem?.ownerIban ?: "") }

  var billingPictureUri by remember { mutableStateOf(initialItem?.billingPictureUri ?: initialItem?.buildingImageUri) }
  var doorPictureUri by remember { mutableStateOf(initialItem?.doorPictureUri) }

  var notes by remember { mutableStateOf(initialItem?.notes ?: "") }
  var whatsappGroupUrl by remember { mutableStateOf(initialItem?.whatsappGroupUrl ?: "") }

  var errorMessage by remember { mutableStateOf<String?>(null) }

  val billingPhotoPicker = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      val savedPath = onSaveImageToStorage(uri)
      billingPictureUri = savedPath ?: uri.toString()
    }
  }

  val doorPhotoPicker = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      val savedPath = onSaveImageToStorage(uri)
      doorPictureUri = savedPath ?: uri.toString()
    }
  }

  val isEditing = initialItem != null

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.94f)
        .clip(RoundedCornerShape(24.dp)),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(20.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = if (isEditing) "Edit Accommodation" else "Add New Housing Unit",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = ZawitcoBlue
            )
            Text(
              text = "Full specs & owner details",
              fontSize = 12.sp,
              color = Slate600
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(36.dp).background(Slate100, CircleShape)
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Black, modifier = Modifier.size(18.dp))
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2 MANDATORY PHOTOS PICKERS
        Text(
          text = "2 MANDATORY PICTURES",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = Slate600,
          letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Billing Picture
          Box(
            modifier = Modifier
              .weight(1f)
              .height(120.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFFFFF7ED))
              .border(1.dp, Color(0xFFFFEDD5), RoundedCornerShape(12.dp))
              .clickable {
                billingPhotoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
              },
            contentAlignment = Alignment.Center
          ) {
            if (!billingPictureUri.isNullOrBlank()) {
              AsyncImage(
                model = billingPictureUri,
                contentDescription = "Billing Photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()
              )
            } else {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Description, contentDescription = null, tint = ZawitcoOrange, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.height(4.dp))
                Text("1. Billing Picture", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ZawitcoOrange)
                Text("Tap to upload", fontSize = 9.sp, color = Slate400)
              }
            }
          }

          // Door Picture
          Box(
            modifier = Modifier
              .weight(1f)
              .height(120.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(ZawitcoLightBlue)
              .border(1.dp, Slate200, RoundedCornerShape(12.dp))
              .clickable {
                doorPhotoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
              },
            contentAlignment = Alignment.Center
          ) {
            if (!doorPictureUri.isNullOrBlank()) {
              AsyncImage(
                model = doorPictureUri,
                contentDescription = "Door Photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()
              )
            } else {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.MeetingRoom, contentDescription = null, tint = ZawitcoBlue, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.height(4.dp))
                Text("2. Door Picture", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ZawitcoBlue)
                Text("Tap to upload", fontSize = 9.sp, color = Slate400)
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Basic Info
        OutlinedTextField(
          value = areaName,
          onValueChange = { areaName = it },
          label = { Text("Area / District / Location Name *") },
          placeholder = { Text("e.g. Al Olaya District, Riyadh") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = villaNumber,
            onValueChange = { villaNumber = it },
            label = { Text("Villa Number") },
            placeholder = { Text("14B") },
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
          )
          OutlinedTextField(
            value = floorNumber,
            onValueChange = { floorNumber = it },
            label = { Text("Floor") },
            placeholder = { Text("2nd Floor") },
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
          )
          OutlinedTextField(
            value = roomNumber,
            onValueChange = { roomNumber = it },
            label = { Text("Room(s)") },
            placeholder = { Text("201-203") },
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Worker Counts
        Text(
          text = "WORKER OCCUPANCY & CAPACITY",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = ZawitcoDarkOrange,
          letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = activeWorkers,
            onValueChange = { activeWorkers = it.filter { c -> c.isDigit() } },
            label = { Text("Active Workers") },
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
          )
          OutlinedTextField(
            value = totalWorkers,
            onValueChange = { totalWorkers = it.filter { c -> c.isDigit() } },
            label = { Text("Total Workers") },
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
          )
          OutlinedTextField(
            value = totalCapacity,
            onValueChange = { totalCapacity = it.filter { c -> c.isDigit() } },
            label = { Text("Total Capacity") },
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Worker Phones
        Text(
          text = "WORKER PHONE NUMBERS",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = Slate600,
          letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = workerPhone,
            onValueChange = { workerPhone = it },
            label = { Text("Worker Phone 1 (Primary)") },
            placeholder = { Text("+966 50...") },
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
          )
          OutlinedTextField(
            value = workerPhone2,
            onValueChange = { workerPhone2 = it },
            label = { Text("Worker Phone 2 (Secondary)") },
            placeholder = { Text("+966 55...") },
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Store & Location Details
        Text(
          text = "STORE & MAP LOCATIONS",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = Slate600,
          letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
          value = accommodationLocationUrl,
          onValueChange = { accommodationLocationUrl = it },
          label = { Text("Accommodation Google Maps URL") },
          placeholder = { Text("https://maps.google.com/?q=...") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = storeCode,
            onValueChange = { storeCode = it },
            label = { Text("Store Code") },
            placeholder = { Text("ST-101") },
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
          )
          OutlinedTextField(
            value = storeName,
            onValueChange = { storeName = it },
            label = { Text("Store Name") },
            placeholder = { Text("Central Store") },
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = storeLocationUrl,
          onValueChange = { storeLocationUrl = it },
          label = { Text("Store Location Google Maps URL") },
          placeholder = { Text("https://maps.google.com/?q=...") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // House Owner Details
        Text(
          text = "HOUSE OWNER & BANK ACCOUNT DETAILS",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF0F766E),
          letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = ownerName,
            onValueChange = { ownerName = it },
            label = { Text("Owner Name") },
            placeholder = { Text("Sheikh Abdullah...") },
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
          )
          OutlinedTextField(
            value = ownerPhone,
            onValueChange = { ownerPhone = it },
            label = { Text("Owner Phone") },
            placeholder = { Text("+966 55...") },
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = ownerBankName,
          onValueChange = { ownerBankName = it },
          label = { Text("Bank Name / Account Details") },
          placeholder = { Text("Al Rajhi Bank / SNB") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = ownerIban,
          onValueChange = { ownerIban = it },
          label = { Text("House Owner IBAN Number") },
          placeholder = { Text("SA4480000456608010123456") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Notes & WhatsApp
        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Property Notes") },
          placeholder = { Text("Additional notes...") },
          modifier = Modifier.fillMaxWidth(),
          minLines = 2,
          shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = whatsappGroupUrl,
          onValueChange = { whatsappGroupUrl = it },
          label = { Text("WhatsApp Group URL (Optional)") },
          placeholder = { Text("https://chat.whatsapp.com/...") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp)
        )

        if (errorMessage != null) {
          Spacer(modifier = Modifier.height(8.dp))
          Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          Button(
            onClick = onDismiss,
            colors = ButtonDefaults.buttonColors(containerColor = Slate100, contentColor = Color.Black),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(1f).height(46.dp)
          ) {
            Text("Cancel", fontWeight = FontWeight.Bold)
          }

          Button(
            onClick = {
              if (areaName.isBlank()) {
                errorMessage = "Area / Location name is required."
                return@Button
              }
              onSave(
                initialItem?.id ?: 0L,
                areaName.trim(),
                villaNumber.trim(),
                floorNumber.trim(),
                roomNumber.trim(),
                totalWorkers.toIntOrNull() ?: 0,
                totalCapacity.toIntOrNull() ?: 0,
                activeWorkers.toIntOrNull() ?: 0,
                accommodationLocationUrl.trim(),
                storeLocationUrl.trim(),
                storeCode.trim(),
                storeName.trim(),
                workerPhone.trim(),
                workerPhone2.trim(),
                ownerName.trim(),
                ownerPhone.trim(),
                ownerBankName.trim(),
                ownerIban.trim(),
                accommodationLocationUrl.trim(),
                null,
                null,
                billingPictureUri,
                billingPictureUri,
                doorPictureUri,
                notes.trim(),
                whatsappGroupUrl.trim(),
                storeName.trim().ifBlank { storeCode.trim() }
              )
            },
            colors = ButtonDefaults.buttonColors(containerColor = ZawitcoBlue, contentColor = Color.White),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(2f).height(46.dp)
          ) {
            Icon(Icons.Outlined.Check, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Save Housing Unit", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
