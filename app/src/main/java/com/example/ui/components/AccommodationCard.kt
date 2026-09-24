package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Accommodation
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.ZawitcoBlue
import com.example.ui.theme.ZawitcoDarkOrange
import com.example.ui.theme.ZawitcoLightBlue
import com.example.ui.theme.ZawitcoLightOrange
import com.example.ui.theme.ZawitcoOrange

@Composable
fun AccommodationCard(
  accommodation: Accommodation,
  isAdmin: Boolean,
  pendingRequirementsCount: Int = 0,
  pendingReportsCount: Int = 0,
  onViewDetails: () -> Unit,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var showDeleteConfirm by remember { mutableStateOf(false) }

  val billingImg = accommodation.billingPictureUri ?: accommodation.buildingImageUri
  val doorImg = accommodation.doorPictureUri
  val hasPendingAlert = pendingRequirementsCount > 0 || pendingReportsCount > 0

  if (showDeleteConfirm) {
    AlertDialog(
      onDismissRequest = { showDeleteConfirm = false },
      title = { Text("Delete Accommodation", fontWeight = FontWeight.Bold, color = Color.Black) },
      text = {
        Text("Are you sure you want to remove '${accommodation.areaName}'? This action cannot be undone.", color = Color.Black)
      },
      confirmButton = {
        TextButton(
          onClick = {
            showDeleteConfirm = false
            onDelete()
          },
          modifier = Modifier.testTag("confirm_delete_button")
        ) {
          Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteConfirm = false }) {
          Text("Cancel", color = Color.Black)
        }
      }
    )
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("accommodation_card_${accommodation.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = BorderStroke(1.dp, Slate200)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // ==========================================
      // HEADER: Accommodation Name (BLUE) & Admin Controls
      // ==========================================
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f)
        ) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .clip(CircleShape)
              .background(ZawitcoLightBlue),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Outlined.Home,
              contentDescription = null,
              tint = ZawitcoBlue,
              modifier = Modifier.size(22.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            // Accommodation Name: BLUE FONT COLOR
            Text(
              text = accommodation.areaName,
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = ZawitcoBlue,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )

            // Store code & location font color: ORANGE
            val storeDisplay = when {
              accommodation.storeCode.isNotBlank() && accommodation.storeName.isNotBlank() ->
                "Store: ${accommodation.storeName} (${accommodation.storeCode})"
              accommodation.storeCode.isNotBlank() -> "Store Code: ${accommodation.storeCode}"
              accommodation.storeName.isNotBlank() -> "Store: ${accommodation.storeName}"
              accommodation.stationName.isNotBlank() -> "Store/Hub: ${accommodation.stationName}"
              else -> ""
            }

            if (storeDisplay.isNotBlank()) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Storefront,
                  contentDescription = null,
                  tint = ZawitcoDarkOrange,
                  modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = storeDisplay,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = ZawitcoDarkOrange
                )
              }
            }

            // Notifications alert indicator if requirements or reports exist
            if (hasPendingAlert) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.NotificationsActive,
                  contentDescription = "Active Notifications",
                  tint = Color(0xFFDC2626),
                  modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = listOfNotNull(
                    if (pendingRequirementsCount > 0) "$pendingRequirementsCount req" else null,
                    if (pendingReportsCount > 0) "$pendingReportsCount report" else null
                  ).joinToString(" • "),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFDC2626)
                )
              }
            }
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          if (accommodation.whatsappGroupUrl.isNotBlank()) {
            Box(
              modifier = Modifier
                .padding(end = 4.dp)
                .size(28.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8F8F0))
                .clickable {
                  try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(accommodation.whatsappGroupUrl))
                    context.startActivity(intent)
                  } catch (e: Exception) {
                    // Ignore
                  }
                },
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "WA",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF25D366)
              )
            }
          }

          if (isAdmin) {
            IconButton(
              onClick = onEdit,
              modifier = Modifier
                .size(36.dp)
                .testTag("edit_accommodation_button_${accommodation.id}")
            ) {
              Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Edit Accommodation",
                tint = Color.Black,
                modifier = Modifier.size(18.dp)
              )
            }

            IconButton(
              onClick = { showDeleteConfirm = true },
              modifier = Modifier
                .size(36.dp)
                .testTag("delete_accommodation_button_${accommodation.id}")
            ) {
              Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = "Delete Accommodation",
                tint = Color(0xFFEF4444),
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }

      // ==========================================
      // 2 PICTURES PREVIEW (1. Billing Picture, 2. Door Picture)
      // ==========================================
      Spacer(modifier = Modifier.height(10.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Billing Picture preview
        Box(
          modifier = Modifier
            .weight(1f)
            .height(95.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFFFF7ED))
            .border(1.dp, Slate200, RoundedCornerShape(10.dp)),
          contentAlignment = Alignment.Center
        ) {
          if (!billingImg.isNullOrBlank()) {
            AsyncImage(
              model = billingImg,
              contentDescription = "Billing Photo",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxWidth()
            )
          } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                tint = ZawitcoOrange,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "1. Billing",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = ZawitcoOrange
              )
            }
          }
        }

        // Door Picture preview
        Box(
          modifier = Modifier
            .weight(1f)
            .height(95.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(ZawitcoLightBlue)
            .border(1.dp, Slate200, RoundedCornerShape(10.dp)),
          contentAlignment = Alignment.Center
        ) {
          if (!doorImg.isNullOrBlank()) {
            AsyncImage(
              model = doorImg,
              contentDescription = "Door Photo",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxWidth()
            )
          } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Default.MeetingRoom,
                contentDescription = null,
                tint = ZawitcoBlue,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "2. Door",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = ZawitcoBlue
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // ==========================================
      // VILLA DETAILS (FONT SET ORANGE COLOR)
      // ==========================================
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFFFF7ED),
        border = BorderStroke(1.dp, Color(0xFFFFEDD5))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 7.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Outlined.Home,
              contentDescription = null,
              tint = ZawitcoDarkOrange,
              modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (accommodation.villaNumber.isNotBlank()) "Villa: #${accommodation.villaNumber}" else "Villa: N/A",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = ZawitcoDarkOrange
            )
          }

          Text(
            text = "Floor: ${accommodation.floorNumber.ifBlank { "N/A" }}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = ZawitcoDarkOrange
          )

          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Outlined.MeetingRoom,
              contentDescription = null,
              tint = ZawitcoDarkOrange,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "Room: ${accommodation.roomNumber.ifBlank { "N/A" }}",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = ZawitcoDarkOrange
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // ==========================================
      // WORKERS NUMBER & CAPACITY (FONT SET ORANGE COLOR)
      // ==========================================
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFFFF1EB),
        border = BorderStroke(1.dp, Color(0xFFFFDFC9))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 7.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Groups,
              contentDescription = null,
              tint = ZawitcoDarkOrange,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Active Workers: ",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = ZawitcoDarkOrange
            )
            Text(
              text = "${accommodation.activeWorkers}",
              fontSize = 13.sp,
              fontWeight = FontWeight.Black,
              color = ZawitcoDarkOrange
            )
          }

          Text(
            text = "Total: ${accommodation.totalWorkers}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = ZawitcoDarkOrange
          )

          Text(
            text = "Capacity: ${accommodation.totalCapacity}",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = ZawitcoDarkOrange
          )
        }
      }

      // ==========================================
      // WORKER PHONES 1 & 2 (FONT SET ORANGE COLOR)
      // ==========================================
      val wPhone1 = accommodation.workerPhone
      val wPhone2 = accommodation.workerPhone2

      if (wPhone1.isNotBlank() || wPhone2.isNotBlank()) {
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 4.dp)
        ) {
          Icon(
            imageVector = Icons.Outlined.Phone,
            contentDescription = null,
            tint = ZawitcoDarkOrange,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = listOfNotNull(
              wPhone1.takeIf { it.isNotBlank() }?.let { "Worker 1: $it" },
              wPhone2.takeIf { it.isNotBlank() }?.let { "Worker 2: $it" }
            ).joinToString("  •  "),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = ZawitcoDarkOrange,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      // ==========================================
      // MAP QUICK ACCESS LINKS (Accommodation & Store)
      // ==========================================
      val accMapLink = accommodation.accommodationLocationUrl.ifBlank { accommodation.googleMapsUrl }
      val storeMapLink = accommodation.storeLocationUrl

      if (accMapLink.isNotBlank() || storeMapLink.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          if (accMapLink.isNotBlank()) {
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFE0F2FE))
                .clickable {
                  try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(accMapLink))
                    context.startActivity(intent)
                  } catch (e: Exception) {
                    // Ignore
                  }
                }
                .padding(vertical = 6.dp, horizontal = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.LocationOn,
                  contentDescription = null,
                  tint = ZawitcoBlue,
                  modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Housing Map",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = ZawitcoBlue
                )
              }
            }
          }

          if (storeMapLink.isNotBlank()) {
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFFFF7ED))
                .clickable {
                  try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(storeMapLink))
                    context.startActivity(intent)
                  } catch (e: Exception) {
                    // Ignore
                  }
                }
                .padding(vertical = 6.dp, horizontal = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Storefront,
                  contentDescription = null,
                  tint = ZawitcoDarkOrange,
                  modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Store Map",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = ZawitcoDarkOrange
                )
              }
            }
          }
        }
      }

      // ==========================================
      // HOUSE OWNER QUICK SUMMARY (IBAN / PHONE)
      // ==========================================
      if (accommodation.ownerName.isNotBlank() || accommodation.ownerPhone.isNotBlank() || accommodation.ownerIban.isNotBlank()) {
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 4.dp)
        ) {
          Icon(
            imageVector = Icons.Default.AccountBalance,
            contentDescription = null,
            tint = Color(0xFF0F766E),
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = listOfNotNull(
              accommodation.ownerName.takeIf { it.isNotBlank() }?.let { "Owner: $it" },
              accommodation.ownerPhone.takeIf { it.isNotBlank() }?.let { "Ph: $it" },
              accommodation.ownerIban.takeIf { it.isNotBlank() }?.let { "IBAN: ${it.take(10)}..." }
            ).joinToString(" • "),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // "View Details" button
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(ZawitcoLightBlue)
          .clickable { onViewDetails() }
          .padding(vertical = 10.dp)
          .testTag("view_details_button_${accommodation.id}"),
        contentAlignment = Alignment.Center
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Text(
            text = "View Details & Full Specs",
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
          Spacer(modifier = Modifier.width(6.dp))
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(15.dp)
          )
        }
      }
    }
  }
}
