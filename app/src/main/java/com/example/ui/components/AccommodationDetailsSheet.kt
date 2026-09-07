package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.LocalDining
import androidx.compose.material.icons.outlined.LocalGroceryStore
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Mosque
import androidx.compose.material.icons.outlined.Navigation
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material.icons.outlined.Train
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.LightGreen
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.ZawitcoBlue
import com.example.ui.theme.ZawitcoDarkBlue
import com.example.ui.theme.ZawitcoLightBlue
import com.example.ui.theme.ZawitcoLightOrange
import com.example.ui.theme.ZawitcoOrange
import com.example.ui.viewmodel.GroundingUiState

@Composable
fun AccommodationDetailsSheet(
  accommodation: Accommodation,
  groundingState: GroundingUiState,
  onDismiss: () -> Unit,
  onFetchGrounding: (queryType: String, customPrompt: String?) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var selectedQueryType by remember { mutableStateOf("general") }
  var customQueryText by remember { mutableStateOf("") }

  // Automatically fetch initial Maps grounding if idle
  LaunchedEffect(accommodation.id) {
    if (groundingState is GroundingUiState.Idle) {
      onFetchGrounding("general", null)
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
      ) {
        // Building Image Header or Hero Banner
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(if (!accommodation.buildingImageUri.isNullOrBlank()) 220.dp else 130.dp)
            .background(ZawitcoLightBlue)
        ) {
          if (!accommodation.buildingImageUri.isNullOrBlank()) {
            AsyncImage(
              model = accommodation.buildingImageUri,
              contentDescription = "Building Image",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxWidth()
            )
          } else {
            Column(
              modifier = Modifier.align(Alignment.Center),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                imageVector = Icons.Outlined.Place,
                contentDescription = null,
                tint = ZawitcoBlue,
                modifier = Modifier.size(44.dp)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Zawitco Accommodation",
                color = ZawitcoBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          // Brand Logo Badge on top-left
          Box(
            modifier = Modifier
              .align(Alignment.TopStart)
              .padding(12.dp)
          ) {
            ZawitcoCompanyLogo(height = 36.dp, showSubtext = false)
          }

          // Close button on top-right
          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .align(Alignment.TopEnd)
              .padding(12.dp)
              .background(Color.Black.copy(alpha = 0.5f), CircleShape)
              .size(38.dp)
              .testTag("close_details_dialog")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = Color.White
            )
          }
        }

        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
        ) {
          // Area Name & Villa Number
          Text(
            text = accommodation.areaName,
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            color = ZawitcoBlue
          )

          Text(
            text = if (accommodation.villaNumber.isNotBlank()) "Villa #${accommodation.villaNumber}" else "Villa: N/A",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = Slate600
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Floor & Room Cards (2 Columns)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Card(
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = Slate100)
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Text(
                  text = "FLOOR NUMBER",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Slate500,
                  letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = accommodation.floorNumber.ifBlank { "N/A" },
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold,
                  color = Slate800
                )
              }
            }

            Card(
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = Slate100)
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Text(
                  text = "ROOM NUMBER(S)",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Slate500,
                  letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = accommodation.roomNumber.ifBlank { "N/A" },
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold,
                  color = Slate800
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Contact Cards with Call and SMS action buttons
          // Worker's Phone
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Slate100)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "WORKER'S PHONE",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Slate500,
                  letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = accommodation.workerPhone.ifBlank { "N/A" },
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold,
                  color = Slate800
                )
              }

              if (accommodation.workerPhone.isNotBlank()) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  IconButton(
                    onClick = {
                      val smsIntent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:${accommodation.workerPhone}"))
                      context.startActivity(smsIntent)
                    },
                    modifier = Modifier
                      .size(36.dp)
                      .background(Slate200, CircleShape)
                  ) {
                    Icon(
                      imageVector = Icons.Outlined.Sms,
                      contentDescription = "SMS Worker",
                      tint = Slate700,
                      modifier = Modifier.size(18.dp)
                    )
                  }

                  Button(
                    onClick = {
                      val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${accommodation.workerPhone}"))
                      context.startActivity(dialIntent)
                    },
                    colors = ButtonDefaults.buttonColors(
                      containerColor = EmeraldGreen,
                      contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("call_worker_button")
                  ) {
                    Icon(
                      imageVector = Icons.Outlined.Call,
                      contentDescription = null,
                      modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Call", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Owner's Phone
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Slate100)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "OWNER'S PHONE",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Slate500,
                  letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = accommodation.ownerPhone.ifBlank { "N/A" },
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold,
                  color = Slate800
                )
              }

              if (accommodation.ownerPhone.isNotBlank()) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  IconButton(
                    onClick = {
                      val smsIntent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:${accommodation.ownerPhone}"))
                      context.startActivity(smsIntent)
                    },
                    modifier = Modifier
                      .size(36.dp)
                      .background(Slate200, CircleShape)
                  ) {
                    Icon(
                      imageVector = Icons.Outlined.Sms,
                      contentDescription = "SMS Owner",
                      tint = Slate700,
                      modifier = Modifier.size(18.dp)
                    )
                  }

                  Button(
                    onClick = {
                      val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${accommodation.ownerPhone}"))
                      context.startActivity(dialIntent)
                    },
                    colors = ButtonDefaults.buttonColors(
                      containerColor = EmeraldGreen,
                      contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("call_owner_button")
                  ) {
                    Icon(
                      imageVector = Icons.Outlined.Call,
                      contentDescription = null,
                      modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Call", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                  }
                }
              }
            }
          }

          // Optional Notes
          if (accommodation.notes.isNotBlank()) {
            Spacer(modifier = Modifier.height(14.dp))
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = ZawitcoLightBlue),
              border = BorderStroke(1.dp, ZawitcoBlue.copy(alpha = 0.2f))
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Text(
                  text = "PROPERTY NOTES",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = ZawitcoBlue,
                  letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = accommodation.notes,
                  fontSize = 14.sp,
                  color = Slate700,
                  lineHeight = 20.sp
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // WhatsApp Group or Chat Button
          val whatsappLink = when {
            accommodation.whatsappGroupUrl.isNotBlank() -> accommodation.whatsappGroupUrl
            accommodation.workerPhone.isNotBlank() -> {
              val cleanPhone = accommodation.workerPhone.replace(Regex("[^0-9]"), "")
              "https://wa.me/$cleanPhone"
            }
            else -> null
          }

          if (whatsappLink != null) {
            Button(
              onClick = {
                try {
                  val waIntent = Intent(Intent.ACTION_VIEW, Uri.parse(whatsappLink))
                  context.startActivity(waIntent)
                } catch (e: Exception) {
                  Toast.makeText(context, "Could not open WhatsApp link.", Toast.LENGTH_SHORT).show()
                }
              },
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF25D366),
                contentColor = Color.White
              ),
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("details_whatsapp_group_button")
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = if (accommodation.whatsappGroupUrl.isNotBlank()) "Join Building WhatsApp Group" else "Chat on WhatsApp",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
            }

            Spacer(modifier = Modifier.height(10.dp))
          }

          // "Open in Google Maps" and "GPS Navigate"
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Button(
              onClick = {
                val mapUri = when {
                  accommodation.googleMapsUrl.isNotBlank() -> Uri.parse(accommodation.googleMapsUrl)
                  accommodation.latitude != null && accommodation.longitude != null -> {
                    Uri.parse("geo:${accommodation.latitude},${accommodation.longitude}?q=${accommodation.latitude},${accommodation.longitude}(${Uri.encode(accommodation.areaName)})")
                  }
                  else -> Uri.parse("geo:0,0?q=${Uri.encode(accommodation.areaName + ", Saudi Arabia")}")
                }

                try {
                  val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                  context.startActivity(mapIntent)
                } catch (e: Exception) {
                  Toast.makeText(context, "Could not open maps application.", Toast.LENGTH_SHORT).show()
                }
              },
              colors = ButtonDefaults.buttonColors(
                containerColor = ZawitcoOrange,
                contentColor = Color.White
              ),
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier
                .weight(1.2f)
                .height(50.dp)
                .testTag("open_in_google_maps_button")
            ) {
              Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Maps",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
            }

            Button(
              onClick = {
                val navUri = if (accommodation.latitude != null && accommodation.longitude != null) {
                  Uri.parse("google.navigation:q=${accommodation.latitude},${accommodation.longitude}")
                } else if (accommodation.googleMapsUrl.isNotBlank()) {
                  Uri.parse(accommodation.googleMapsUrl)
                } else {
                  Uri.parse("geo:0,0?q=${Uri.encode(accommodation.areaName)}")
                }

                try {
                  val navIntent = Intent(Intent.ACTION_VIEW, navUri)
                  context.startActivity(navIntent)
                } catch (e: Exception) {
                  Toast.makeText(context, "Could not open navigation.", Toast.LENGTH_SHORT).show()
                }
              },
              colors = ButtonDefaults.buttonColors(
                containerColor = ZawitcoBlue,
                contentColor = Color.White
              ),
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier
                .weight(1.3f)
                .height(50.dp)
                .testTag("gps_navigation_button")
            ) {
              Icon(
                imageVector = Icons.Outlined.Navigation,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "GPS Navigate",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(24.dp))

          // ==========================================
          // GOOGLE MAPS GROUNDING SECTION (Gemini 2.5 Flash)
          // ==========================================
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .testTag("google_maps_grounding_section"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Slate50),
            border = BorderStroke(1.dp, Slate200)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
            ) {
              // Section Header
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(34.dp)
                      .clip(CircleShape)
                      .background(ZawitcoBlue),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = Icons.Default.AutoAwesome,
                      contentDescription = null,
                      tint = Color.White,
                      modifier = Modifier.size(18.dp)
                    )
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      text = "Google Maps Insights",
                      fontWeight = FontWeight.Bold,
                      fontSize = 16.sp,
                      color = ZawitcoBlue
                    )
                    Text(
                      text = "Grounded with Gemini & Google Maps",
                      fontSize = 11.sp,
                      color = Slate500
                    )
                  }
                }

                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = ZawitcoLightOrange
                ) {
                  Text(
                    text = "Maps Grounded",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZawitcoOrange,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              // Category filter chips
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                val chips = listOf(
                  "general" to "Overview",
                  "groceries" to "Supermarkets",
                  "mosques" to "Mosques",
                  "medical" to "Clinics",
                  "transport" to "Metro & Roads",
                  "dining" to "Restaurants"
                )

                chips.forEach { (type, label) ->
                  val isSelected = selectedQueryType == type
                  FilterChip(
                    selected = isSelected,
                    onClick = {
                      selectedQueryType = type
                      customQueryText = ""
                      onFetchGrounding(type, null)
                    },
                    label = { Text(label, fontSize = 12.sp) },
                    leadingIcon = {
                      when (type) {
                        "groceries" -> Icon(Icons.Outlined.LocalGroceryStore, null, Modifier.size(14.dp))
                        "mosques" -> Icon(Icons.Outlined.Place, null, Modifier.size(14.dp))
                        "medical" -> Icon(Icons.Outlined.LocalHospital, null, Modifier.size(14.dp))
                        "transport" -> Icon(Icons.Outlined.Train, null, Modifier.size(14.dp))
                        "dining" -> Icon(Icons.Outlined.LocalDining, null, Modifier.size(14.dp))
                        else -> Icon(Icons.Outlined.Place, null, Modifier.size(14.dp))
                      }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                      selectedContainerColor = ZawitcoBlue,
                      selectedLabelColor = Color.White,
                      selectedLeadingIconColor = Color.White
                    )
                  )
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              // Grounding State Presentation
              when (groundingState) {
                is GroundingUiState.Loading -> {
                  Box(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                  ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = ZawitcoBlue,
                        strokeWidth = 3.dp
                      )
                      Spacer(modifier = Modifier.height(10.dp))
                      Text(
                        text = "Grounding with Google Maps data...",
                        fontSize = 13.sp,
                        color = Slate600
                      )
                    }
                  }
                }

                is GroundingUiState.Success -> {
                  Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Slate200)
                  ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                      Text(
                        text = groundingState.content,
                        fontSize = 14.sp,
                        color = Slate800,
                        lineHeight = 22.sp
                      )
                    }
                  }
                }

                is GroundingUiState.Error -> {
                  Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFEF2F2),
                    border = BorderStroke(1.dp, Color(0xFFFCA5A5))
                  ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                      Text(
                        text = "Notice:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFFB91C1C)
                      )
                      Spacer(modifier = Modifier.height(4.dp))
                      Text(
                        text = groundingState.message,
                        fontSize = 13.sp,
                        color = Color(0xFF7F1D1D)
                      )
                      Spacer(modifier = Modifier.height(8.dp))
                      OutlinedButton(
                        onClick = { onFetchGrounding(selectedQueryType, customQueryText.ifBlank { null }) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFB91C1C))
                      ) {
                        Text("Retry Query")
                      }
                    }
                  }
                }

                GroundingUiState.Idle -> {
                  Text(
                    text = "Tap a category above or ask a location question below to get verified Google Maps answers.",
                    fontSize = 13.sp,
                    color = Slate500
                  )
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              // Ask Google Maps Question
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
              ) {
                OutlinedTextField(
                  value = customQueryText,
                  onValueChange = { customQueryText = it },
                  placeholder = { Text("Ask about this location...", fontSize = 13.sp) },
                  singleLine = true,
                  modifier = Modifier
                    .weight(1f)
                    .testTag("custom_maps_query_input")
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                  onClick = {
                    if (customQueryText.isNotBlank()) {
                      onFetchGrounding("custom", customQueryText)
                    }
                  },
                  modifier = Modifier
                    .size(46.dp)
                    .background(ZawitcoBlue, RoundedCornerShape(10.dp))
                    .testTag("send_maps_query_button")
                ) {
                  Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Ask",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
