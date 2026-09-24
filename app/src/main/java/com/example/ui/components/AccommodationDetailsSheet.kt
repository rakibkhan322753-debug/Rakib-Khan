package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.Accommodation
import com.example.ui.theme.DarkGreen
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.ZawitcoBlue
import com.example.ui.theme.ZawitcoDarkOrange
import com.example.ui.theme.ZawitcoLightBlue
import com.example.ui.theme.ZawitcoOrange

@Composable
fun AccommodationDetailsSheet(
  accommodation: Accommodation,
  requirementsCount: Int = 0,
  reportsCount: Int = 0,
  isAdmin: Boolean = false,
  onEdit: (() -> Unit)? = null,
  onDelete: (() -> Unit)? = null,
  onOpenRequirements: () -> Unit,
  onOpenReports: () -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var showDeleteConfirm by remember { mutableStateOf(false) }

  val billingImg = accommodation.billingPictureUri ?: accommodation.buildingImageUri
  val doorImg = accommodation.doorPictureUri

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
      tonalElevation = 8.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        // Top Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(ZawitcoLightBlue),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Outlined.Home,
                contentDescription = null,
                tint = ZawitcoBlue,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "Accommodation Profile",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ZawitcoBlue
              )
              Text(
                text = "Zawitco Housing System",
                fontSize = 11.sp,
                color = Slate500
              )
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            if (isAdmin) {
              if (onEdit != null) {
                IconButton(
                  onClick = {
                    onDismiss()
                    onEdit()
                  },
                  modifier = Modifier
                    .size(36.dp)
                    .background(Slate100, CircleShape)
                ) {
                  Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit Accommodation",
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                  )
                }
                Spacer(modifier = Modifier.width(6.dp))
              }

              if (onDelete != null) {
                IconButton(
                  onClick = { showDeleteConfirm = true },
                  modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFFFEE2E2), CircleShape)
                ) {
                  Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete Accommodation",
                    tint = Color(0xFFDC2626),
                    modifier = Modifier.size(18.dp)
                  )
                }
                Spacer(modifier = Modifier.width(6.dp))
              }
            }

            IconButton(
              onClick = onDismiss,
              modifier = Modifier
                .size(36.dp)
                .background(Slate100, CircleShape)
                .testTag("details_close_button")
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.Black,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }

        // ==========================================
        // 2 MANDATORY PICTURES DISPLAY
        // ==========================================
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // 1. BILLING PICTURE
            Card(
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = Slate100),
              border = BorderStroke(1.dp, Slate200)
            ) {
              Column {
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(Color(0xFFFFF7ED)),
                  contentAlignment = Alignment.Center
                ) {
                  if (!billingImg.isNullOrBlank()) {
                    AsyncImage(
                      model = billingImg,
                      contentDescription = "1. Billing Picture",
                      contentScale = ContentScale.Crop,
                      modifier = Modifier.fillMaxWidth()
                    )
                  } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = ZawitcoOrange,
                        modifier = Modifier.size(30.dp)
                      )
                      Spacer(modifier = Modifier.height(4.dp))
                      Text(
                        text = "No Billing Photo",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Slate500
                      )
                    }
                  }
                }
                Text(
                  text = "1. Billing Picture",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = ZawitcoOrange,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
              }
            }

            // 2. DOOR PICTURE
            Card(
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = Slate100),
              border = BorderStroke(1.dp, Slate200)
            ) {
              Column {
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(ZawitcoLightBlue),
                  contentAlignment = Alignment.Center
                ) {
                  if (!doorImg.isNullOrBlank()) {
                    AsyncImage(
                      model = doorImg,
                      contentDescription = "2. Door Picture",
                      contentScale = ContentScale.Crop,
                      modifier = Modifier.fillMaxWidth()
                    )
                  } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Icon(
                        imageVector = Icons.Default.MeetingRoom,
                        contentDescription = null,
                        tint = ZawitcoBlue,
                        modifier = Modifier.size(30.dp)
                      )
                      Spacer(modifier = Modifier.height(4.dp))
                      Text(
                        text = "No Door Photo",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Slate500
                      )
                    }
                  }
                }
                Text(
                  text = "2. Door Picture",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = ZawitcoBlue,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ==========================================
        // REQUIREMENTS & REPORT ACTIONS
        // ==========================================
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Surface(
              onClick = onOpenRequirements,
              shape = RoundedCornerShape(12.dp),
              color = if (requirementsCount > 0) Color(0xFFFFF7ED) else ZawitcoLightBlue,
              border = BorderStroke(1.dp, if (requirementsCount > 0) Color(0xFFFED7AA) else ZawitcoBlue.copy(alpha = 0.3f)),
              modifier = Modifier.weight(1f)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = null,
                    tint = ZawitcoBlue,
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Column {
                    Text(
                      text = "Requirements",
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = ZawitcoBlue
                    )
                    Text(
                      text = "Bed, gas, stove...",
                      fontSize = 10.sp,
                      color = Slate600
                    )
                  }
                }

                if (requirementsCount > 0) {
                  Box(
                    modifier = Modifier
                      .clip(CircleShape)
                      .background(ZawitcoOrange)
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(
                      text = "$requirementsCount",
                      color = Color.White,
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp
                    )
                  }
                }
              }
            }

            Surface(
              onClick = onOpenReports,
              shape = RoundedCornerShape(12.dp),
              color = if (reportsCount > 0) Color(0xFFFFF1F2) else Color(0xFFFEF2F2),
              border = BorderStroke(1.dp, if (reportsCount > 0) Color(0xFFFECDD3) else Color(0xFFFCA5A5)),
              modifier = Modifier.weight(1f)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.ReportProblem,
                    contentDescription = null,
                    tint = Color(0xFFDC2626),
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Column {
                    Text(
                      text = "Report Issue",
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFFB91C1C)
                    )
                    Text(
                      text = "Maintenance, AC...",
                      fontSize = 10.sp,
                      color = Slate600
                    )
                  }
                }

                if (reportsCount > 0) {
                  Box(
                    modifier = Modifier
                      .clip(CircleShape)
                      .background(Color(0xFFDC2626))
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(
                      text = "$reportsCount",
                      color = Color.White,
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp
                    )
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Property Information Header
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
        ) {
          Text(
            text = accommodation.areaName,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = ZawitcoBlue
          )

          Text(
            text = if (accommodation.villaNumber.isNotBlank()) "Villa #${accommodation.villaNumber}" else "Villa: N/A",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = ZawitcoDarkOrange
          )

          Spacer(modifier = Modifier.height(10.dp))

          // ==========================================
          // WORKERS CAPACITY & ACTIVE STATS
          // ==========================================
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
            border = BorderStroke(1.dp, Color(0xFFFFEDD5))
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Groups,
                  contentDescription = null,
                  tint = ZawitcoDarkOrange,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "WORKER OCCUPANCY & CAPACITY",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = ZawitcoDarkOrange,
                  letterSpacing = 0.8.sp
                )
              }
              Spacer(modifier = Modifier.height(8.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(text = "Active Workers", fontSize = 11.sp, color = Slate600)
                  Text(
                    text = "${accommodation.activeWorkers}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = ZawitcoDarkOrange
                  )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(text = "Total Workers", fontSize = 11.sp, color = Slate600)
                  Text(
                    text = "${accommodation.totalWorkers}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                  )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(text = "Total Capacity", fontSize = 11.sp, color = Slate600)
                  Text(
                    text = "${accommodation.totalCapacity}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = ZawitcoBlue
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // ==========================================
          // LOCATION & STORE DETAILS + MAPS
          // ==========================================
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
            border = BorderStroke(1.dp, Color(0xFFBBF7D0))
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Outlined.Place,
                  contentDescription = null,
                  tint = Color(0xFF16A34A),
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "ACCOMMODATION & STORE LOCATION",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.Black,
                  letterSpacing = 0.8.sp
                )
              }

              if (accommodation.storeCode.isNotBlank() || accommodation.storeName.isNotBlank() || accommodation.stationName.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = listOfNotNull(
                    accommodation.storeName.takeIf { it.isNotBlank() }?.let { "Store: $it" },
                    accommodation.storeCode.takeIf { it.isNotBlank() }?.let { "Code: $it" },
                    accommodation.stationName.takeIf { it.isNotBlank() && it != accommodation.storeName }?.let { "Station: $it" }
                  ).joinToString(" • "),
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = ZawitcoDarkOrange
                )
              }

              // Accommodation Google Map Link
              val accMap = accommodation.accommodationLocationUrl.ifBlank { accommodation.googleMapsUrl }
              if (accMap.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                  onClick = {
                    try {
                      val intent = Intent(Intent.ACTION_VIEW, Uri.parse(accMap))
                      context.startActivity(intent)
                    } catch (e: Exception) {
                      Toast.makeText(context, "Could not open map link", Toast.LENGTH_SHORT).show()
                    }
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = ZawitcoBlue),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.fillMaxWidth().height(40.dp)
                ) {
                  Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Open Accommodation in Google Maps", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
              }

              // Store Google Map Link
              if (accommodation.storeLocationUrl.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Button(
                  onClick = {
                    try {
                      val intent = Intent(Intent.ACTION_VIEW, Uri.parse(accommodation.storeLocationUrl))
                      context.startActivity(intent)
                    } catch (e: Exception) {
                      Toast.makeText(context, "Could not open store map link", Toast.LENGTH_SHORT).show()
                    }
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = ZawitcoDarkOrange),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.fillMaxWidth().height(40.dp)
                ) {
                  Icon(Icons.Default.Storefront, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Open Store Location in Google Maps", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Floor & Room Cards
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Card(
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = Slate100)
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text(
                  text = "FLOOR NUMBER",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = Slate500,
                  letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = accommodation.floorNumber.ifBlank { "N/A" },
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold,
                  color = ZawitcoDarkOrange
                )
              }
            }

            Card(
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = Slate100)
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text(
                  text = "ROOM NUMBER(S)",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = Slate500,
                  letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = accommodation.roomNumber.ifBlank { "N/A" },
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold,
                  color = ZawitcoDarkOrange
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // ==========================================
          // WORKER PHONE NUMBERS (1 & 2)
          // ==========================================
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Slate100)
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text(
                text = "WORKER PHONE NUMBERS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Slate500,
                letterSpacing = 0.8.sp
              )

              // Worker Phone 1
              Spacer(modifier = Modifier.height(6.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(text = "Worker 1 (Primary)", fontSize = 11.sp, color = Slate600)
                  Text(
                    text = accommodation.workerPhone.ifBlank { "N/A" },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZawitcoDarkOrange
                  )
                }
                if (accommodation.workerPhone.isNotBlank()) {
                  Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                      onClick = {
                        val smsIntent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:${accommodation.workerPhone}"))
                        context.startActivity(smsIntent)
                      },
                      modifier = Modifier.size(34.dp).background(Slate200, CircleShape)
                    ) {
                      Icon(Icons.Outlined.Sms, contentDescription = "SMS", tint = Slate700, modifier = Modifier.size(16.dp))
                    }
                    Button(
                      onClick = {
                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${accommodation.workerPhone}"))
                        context.startActivity(dialIntent)
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen, contentColor = Color.White),
                      shape = RoundedCornerShape(8.dp)
                    ) {
                      Icon(Icons.Outlined.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("Call", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                  }
                }
              }

              // Worker Phone 2
              if (accommodation.workerPhone2.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Worker 2 (Secondary)", fontSize = 11.sp, color = Slate600)
                    Text(
                      text = accommodation.workerPhone2,
                      fontSize = 14.sp,
                      fontWeight = FontWeight.Bold,
                      color = ZawitcoDarkOrange
                    )
                  }
                  Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                      onClick = {
                        val smsIntent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:${accommodation.workerPhone2}"))
                        context.startActivity(smsIntent)
                      },
                      modifier = Modifier.size(34.dp).background(Slate200, CircleShape)
                    ) {
                      Icon(Icons.Outlined.Sms, contentDescription = "SMS", tint = Slate700, modifier = Modifier.size(16.dp))
                    }
                    Button(
                      onClick = {
                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${accommodation.workerPhone2}"))
                        context.startActivity(dialIntent)
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen, contentColor = Color.White),
                      shape = RoundedCornerShape(8.dp)
                    ) {
                      Icon(Icons.Outlined.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("Call", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // ==========================================
          // HOUSE OWNER DETAILS (NAME, PHONE, BANK & IBAN)
          // ==========================================
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
            border = BorderStroke(1.dp, Slate200)
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.AccountBalance,
                  contentDescription = null,
                  tint = Color(0xFF0F766E),
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "HOUSE OWNER & BANK DETAILS",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.Black,
                  letterSpacing = 0.8.sp
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              // Owner Name & Phone
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(text = "Owner Name", fontSize = 10.sp, color = Slate500, fontWeight = FontWeight.Bold)
                  Text(
                    text = accommodation.ownerName.ifBlank { "House Owner" },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                  )
                  if (accommodation.ownerPhone.isNotBlank()) {
                    Text(
                      text = "Phone: ${accommodation.ownerPhone}",
                      fontSize = 12.sp,
                      color = Slate700
                    )
                  }
                }

                if (accommodation.ownerPhone.isNotBlank()) {
                  Button(
                    onClick = {
                      val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${accommodation.ownerPhone}"))
                      context.startActivity(dialIntent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreen, contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp)
                  ) {
                    Icon(Icons.Outlined.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Call Owner", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                  }
                }
              }

              // Bank Name
              if (accommodation.ownerBankName.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Bank Name / Account Details", fontSize = 10.sp, color = Slate500, fontWeight = FontWeight.Bold)
                Text(
                  text = accommodation.ownerBankName,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.Black
                )
              }

              // IBAN Number with 1-Click Copy
              if (accommodation.ownerIban.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = Color(0xFFF1F5F9),
                  border = BorderStroke(1.dp, Slate200),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(text = "IBAN NUMBER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate500)
                      Text(
                        text = accommodation.ownerIban,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color.Black
                      )
                    }

                    IconButton(
                      onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Owner IBAN", accommodation.ownerIban)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "IBAN copied to clipboard!", Toast.LENGTH_SHORT).show()
                      },
                      modifier = Modifier.size(36.dp)
                    ) {
                      Icon(Icons.Default.ContentCopy, contentDescription = "Copy IBAN", tint = ZawitcoBlue, modifier = Modifier.size(18.dp))
                    }
                  }
                }
              }
            }
          }

          // Optional Notes
          if (accommodation.notes.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = ZawitcoLightBlue),
              border = BorderStroke(1.dp, ZawitcoBlue.copy(alpha = 0.2f))
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text(
                  text = "PROPERTY NOTES",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = ZawitcoBlue,
                  letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = accommodation.notes,
                  fontSize = 13.sp,
                  color = Slate700,
                  lineHeight = 18.sp
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // WhatsApp Button
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
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .testTag("details_whatsapp_group_button")
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (accommodation.whatsappGroupUrl.isNotBlank()) "Join Building WhatsApp Group" else "Chat on WhatsApp",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
            }
            Spacer(modifier = Modifier.height(12.dp))
          }
        }
      }
    }
  }

  if (showDeleteConfirm) {
    AlertDialog(
      onDismissRequest = { showDeleteConfirm = false },
      title = {
        Text("Delete Accommodation?", fontWeight = FontWeight.Bold, color = Color.Black)
      },
      text = {
        Text(
          "Are you sure you want to delete accommodation in ${accommodation.areaName} (Villa ${accommodation.villaNumber}, Room ${accommodation.roomNumber})? This cannot be undone.",
          color = Color.Black,
          fontSize = 13.sp
        )
      },
      confirmButton = {
        Button(
          onClick = {
            showDeleteConfirm = false
            onDismiss()
            onDelete?.invoke()
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444), contentColor = Color.White)
        ) {
          Text("Delete", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteConfirm = false }) {
          Text("Cancel", color = Color.Black)
        }
      }
    )
  }
}
