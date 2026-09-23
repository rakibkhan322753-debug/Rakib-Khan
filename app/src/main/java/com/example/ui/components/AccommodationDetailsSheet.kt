package com.example.ui.components

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Sms
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
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.ZawitcoBlue
import com.example.ui.theme.ZawitcoLightBlue
import com.example.ui.theme.ZawitcoOrange

@Composable
fun AccommodationDetailsSheet(
  accommodation: Accommodation,
  requirementsCount: Int = 0,
  reportsCount: Int = 0,
  onOpenRequirements: () -> Unit,
  onOpenReports: () -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  val billingImg = accommodation.billingPictureUri ?: accommodation.buildingImageUri
  val doorImg = accommodation.doorPictureUri

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
        // Top Bar with Brand Logo and Close Button
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
        ) {
          ZawitcoCompanyLogo(height = 36.dp, showSubtext = true)

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .align(Alignment.CenterEnd)
              .background(Slate100, CircleShape)
              .size(36.dp)
              .testTag("close_details_dialog")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = Slate700
            )
          }
        }

        // ==========================================
        // 2 PICTURES SECTION (1. Billing Picture & 2. Door Picture)
        // ==========================================
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
        ) {
          Text(
            text = "Accommodation Photos (2 Pictures)",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Slate700
          )
          Spacer(modifier = Modifier.height(8.dp))

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

        Spacer(modifier = Modifier.height(16.dp))

        // ==========================================
        // REQUIREMENTS & REPORT ACTIONS WITH NOTIFICATION BADGES
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
            // Requirements Button
            Surface(
              onClick = onOpenRequirements,
              shape = RoundedCornerShape(12.dp),
              color = if (requirementsCount > 0) Color(0xFFFFF7ED) else ZawitcoLightBlue,
              border = BorderStroke(1.dp, if (requirementsCount > 0) Color(0xFFFED7AA) else ZawitcoBlue.copy(alpha = 0.3f)),
              modifier = Modifier
                .weight(1f)
                .testTag("details_open_requirements_button")
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

            // Report Option Button
            Surface(
              onClick = onOpenReports,
              shape = RoundedCornerShape(12.dp),
              color = if (reportsCount > 0) Color(0xFFFFF1F2) else Color(0xFFFEF2F2),
              border = BorderStroke(1.dp, if (reportsCount > 0) Color(0xFFFECDD3) else Color(0xFFFCA5A5)),
              modifier = Modifier
                .weight(1f)
                .testTag("details_open_reports_button")
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
                      text = "Report problem",
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

        Spacer(modifier = Modifier.height(16.dp))

        // Property Information
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
        ) {
          Text(
            text = accommodation.areaName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = ZawitcoBlue
          )

          Text(
            text = if (accommodation.villaNumber.isNotBlank()) "Villa #${accommodation.villaNumber}" else "Villa: N/A",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Slate600
          )

          Spacer(modifier = Modifier.height(14.dp))

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
                  color = Slate800
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
                  color = Slate800
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Worker's Phone
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Slate100)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "WORKER'S PHONE",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = Slate500,
                  letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = accommodation.workerPhone.ifBlank { "N/A" },
                  fontSize = 14.sp,
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
                      .size(34.dp)
                      .background(Slate200, CircleShape)
                  ) {
                    Icon(
                      imageVector = Icons.Outlined.Sms,
                      contentDescription = "SMS Worker",
                      tint = Slate700,
                      modifier = Modifier.size(16.dp)
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
                    shape = RoundedCornerShape(8.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Outlined.Call,
                      contentDescription = null,
                      modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Call", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Owner's Phone
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Slate100)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "OWNER'S PHONE",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = Slate500,
                  letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = accommodation.ownerPhone.ifBlank { "N/A" },
                  fontSize = 14.sp,
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
                      .size(34.dp)
                      .background(Slate200, CircleShape)
                  ) {
                    Icon(
                      imageVector = Icons.Outlined.Sms,
                      contentDescription = "SMS Owner",
                      tint = Slate700,
                      modifier = Modifier.size(16.dp)
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
                    shape = RoundedCornerShape(8.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Outlined.Call,
                      contentDescription = null,
                      modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Call", fontWeight = FontWeight.Bold, fontSize = 12.sp)
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

          Spacer(modifier = Modifier.height(14.dp))

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
            Spacer(modifier = Modifier.height(8.dp))
          }

          // Direct Google Maps link if provided
          if (accommodation.googleMapsUrl.isNotBlank()) {
            OutlinedButton(
              onClick = {
                try {
                  val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(accommodation.googleMapsUrl))
                  context.startActivity(mapIntent)
                } catch (e: Exception) {
                  Toast.makeText(context, "Could not open maps link.", Toast.LENGTH_SHORT).show()
                }
              },
              shape = RoundedCornerShape(12.dp),
              border = BorderStroke(1.dp, ZawitcoOrange),
              modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
            ) {
              Icon(
                imageVector = Icons.Outlined.Map,
                contentDescription = null,
                tint = ZawitcoOrange,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Open Location in Google Maps",
                fontWeight = FontWeight.Bold,
                color = ZawitcoOrange,
                fontSize = 13.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))
        }
      }
    }
  }
}
