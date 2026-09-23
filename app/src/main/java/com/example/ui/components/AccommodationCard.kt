package com.example.ui.components

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
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.NotificationsActive
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Accommodation
import com.example.ui.theme.DarkGreen
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.ZawitcoBlue
import com.example.ui.theme.ZawitcoLightBlue
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
  var showDeleteConfirm by remember { mutableStateOf(false) }

  val billingImg = accommodation.billingPictureUri ?: accommodation.buildingImageUri
  val doorImg = accommodation.doorPictureUri
  val hasPendingAlert = pendingRequirementsCount > 0 || pendingReportsCount > 0

  if (showDeleteConfirm) {
    AlertDialog(
      onDismissRequest = { showDeleteConfirm = false },
      title = { Text("Delete Accommodation", fontWeight = FontWeight.Bold) },
      text = {
        Text("Are you sure you want to remove '${accommodation.areaName}'? This action cannot be undone.")
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
          Text("Cancel")
        }
      }
    )
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("accommodation_card_${accommodation.id}"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    border = BorderStroke(1.dp, if (hasPendingAlert) Color(0xFFFDBA74) else Slate200),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // Top row: Area Name, Notification Badge, and Action buttons
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
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = accommodation.areaName,
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = ZawitcoBlue,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )

            // Notifications alert indicator if requirements or reports exist
            if (hasPendingAlert) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 2.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.NotificationsActive,
                  contentDescription = "Active Notifications",
                  tint = Color(0xFFEA580C),
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
                  color = Color(0xFFEA580C)
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
                .background(Color(0xFFE8F8F0)),
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
                tint = Slate500,
                modifier = Modifier.size(20.dp)
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
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }
      }

      // ==========================================
      // 2 PICTURES PREVIEW (1. Billing Picture, 2. Door Picture)
      // ==========================================
      Spacer(modifier = Modifier.height(12.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Billing Picture preview
        Box(
          modifier = Modifier
            .weight(1f)
            .height(100.dp)
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
                modifier = Modifier.size(22.dp)
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
            .height(100.dp)
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
                modifier = Modifier.size(22.dp)
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

      // Villa Info
      Text(
        text = if (accommodation.villaNumber.isNotBlank()) "Villa: #${accommodation.villaNumber}" else "Villa: N/A",
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = Slate600
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Floor & Room info badges
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(Slate100)
          .padding(horizontal = 10.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "Floor: ",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Slate700
          )
          Text(
            text = accommodation.floorNumber.ifBlank { "N/A" },
            fontSize = 12.sp,
            color = Slate600
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Outlined.MeetingRoom,
            contentDescription = null,
            tint = Slate500,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = "Room: ",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Slate700
          )
          Text(
            text = accommodation.roomNumber.ifBlank { "N/A" },
            fontSize = 12.sp,
            color = Slate600
          )
        }
      }

      // Contact quick preview
      if (accommodation.workerPhone.isNotBlank() || accommodation.ownerPhone.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 4.dp)
        ) {
          Icon(
            imageVector = Icons.Outlined.Phone,
            contentDescription = null,
            tint = Slate500,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = listOfNotNull(
              accommodation.workerPhone.takeIf { it.isNotBlank() }?.let { "Worker: $it" },
              accommodation.ownerPhone.takeIf { it.isNotBlank() }?.let { "Owner: $it" }
            ).joinToString("  •  "),
            fontSize = 11.sp,
            color = Slate500,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // "View Details" button
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(ZawitcoLightBlue)
          .clickable { onViewDetails() }
          .padding(vertical = 11.dp)
          .testTag("view_details_button_${accommodation.id}"),
        contentAlignment = Alignment.Center
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Text(
            text = "View Details",
            color = ZawitcoBlue,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
          Spacer(modifier = Modifier.width(6.dp))
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = ZawitcoBlue,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}
