package com.example.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Accommodation
import com.example.data.model.AccommodationRequirement
import com.example.ui.theme.DarkGreen
import com.example.ui.theme.LightGreen
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.ZawitcoBlue
import com.example.ui.theme.ZawitcoLightBlue
import com.example.ui.theme.ZawitcoOrange

// Quick presets requested by the user: bed 🛌, mattress, gas stove, gas cylinder, electric stove, bicycle
val defaultRequirementItemPresets = listOf(
  "Bed 🛌",
  "Mattress",
  "Gas Stove",
  "Gas Cylinder",
  "Electric Stove",
  "Bicycle",
  "Air Conditioner (AC)",
  "Refrigerator",
  "Washing Machine",
  "Water Dispenser",
  "Fan",
  "Wardrobe / Cupboard"
)

@Composable
fun RequirementsDialog(
  accommodation: Accommodation,
  requirements: List<AccommodationRequirement>,
  isAdmin: Boolean,
  onDismiss: () -> Unit,
  onAddRequirement: (
    accommodationId: Long,
    itemName: String,
    quantity: Int,
    urgency: String,
    requestedBy: String,
    notes: String
  ) -> Unit,
  onUpdateStatus: (AccommodationRequirement, String) -> Unit,
  onDeleteRequirement: (AccommodationRequirement) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var showAddForm by remember { mutableStateOf(false) }

  var selectedItemName by remember { mutableStateOf("") }
  var quantityStr by remember { mutableStateOf("1") }
  var urgency by remember { mutableStateOf("Normal") }
  var requestedBy by remember { mutableStateOf("") }
  var notes by remember { mutableStateOf("") }
  var itemError by remember { mutableStateOf(false) }

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
          .padding(20.dp)
      ) {
        // Header
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
                .size(38.dp)
                .clip(CircleShape)
                .background(ZawitcoLightBlue),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.ShoppingBag,
                contentDescription = null,
                tint = ZawitcoBlue,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Accommodation Requirements",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = ZawitcoBlue
              )
              Text(
                text = "${accommodation.areaName} • Villa ${accommodation.villaNumber.ifBlank { "N/A" }}",
                fontSize = 12.sp,
                color = Slate600
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_requirements_dialog")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = Slate600
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Active Notification summary badge
        val pendingCount = requirements.count { it.status != "Fulfilled" }
        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          color = if (pendingCount > 0) Color(0xFFFFF7ED) else Slate100,
          border = BorderStroke(1.dp, if (pendingCount > 0) Color(0xFFFED7AA) else Slate200)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = null,
                tint = if (pendingCount > 0) ZawitcoOrange else Slate500,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (pendingCount > 0) "$pendingCount item(s) currently requested" else "All requirements fulfilled",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (pendingCount > 0) Color(0xFF9A3412) else Slate700
              )
            }

            Button(
              onClick = { showAddForm = !showAddForm },
              colors = ButtonDefaults.buttonColors(
                containerColor = ZawitcoOrange,
                contentColor = Color.White
              ),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.testTag("toggle_add_requirement_form_button")
            ) {
              Icon(
                imageVector = if (showAddForm) Icons.Default.Close else Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (showAddForm) "Cancel" else "Request Item",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Form to add a new requirement
        if (showAddForm) {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Slate100),
            border = BorderStroke(1.dp, Slate200)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
            ) {
              Text(
                text = "Add New Required Item",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = ZawitcoBlue
              )

              Spacer(modifier = Modifier.height(8.dp))

              // Quick Preset Chips (Bed 🛌, Mattress, Gas stove, Gas cylinder, Electric stove, Bicycle...)
              Text(
                text = "Quick Choose Common Items:",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Slate600
              )
              Spacer(modifier = Modifier.height(6.dp))
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                defaultRequirementItemPresets.forEach { preset ->
                  val isSelected = selectedItemName.equals(preset, ignoreCase = true)
                  Surface(
                    onClick = {
                      selectedItemName = preset
                      itemError = false
                    },
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) ZawitcoLightBlue else Color.White,
                    border = BorderStroke(1.dp, if (isSelected) ZawitcoBlue else Slate200)
                  ) {
                    Text(
                      text = preset,
                      fontSize = 11.sp,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                      color = if (isSelected) ZawitcoBlue else Slate700,
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Custom Item Name Input
              OutlinedTextField(
                value = selectedItemName,
                onValueChange = {
                  selectedItemName = it
                  if (it.isNotBlank()) itemError = false
                },
                label = { Text("Item Name *") },
                placeholder = { Text("e.g. Bed 🛌, Mattress, Gas Cylinder...") },
                isError = itemError,
                supportingText = if (itemError) {
                  { Text("Item name is required") }
                } else null,
                singleLine = true,
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("input_requirement_item_name")
              )

              Spacer(modifier = Modifier.height(8.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                OutlinedTextField(
                  value = quantityStr,
                  onValueChange = { quantityStr = it },
                  label = { Text("Quantity") },
                  singleLine = true,
                  modifier = Modifier
                    .weight(1f)
                    .testTag("input_requirement_quantity")
                )

                // Urgency selector
                OutlinedTextField(
                  value = urgency,
                  onValueChange = { urgency = it },
                  label = { Text("Urgency (Urgent/Normal)") },
                  singleLine = true,
                  modifier = Modifier
                    .weight(1.3f)
                    .testTag("input_requirement_urgency")
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              OutlinedTextField(
                value = requestedBy,
                onValueChange = { requestedBy = it },
                label = { Text("Requested By / Room / Name") },
                placeholder = { Text("e.g. Room 201, Mohammad, Supervisor") },
                singleLine = true,
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("input_requirement_requester")
              )

              Spacer(modifier = Modifier.height(8.dp))

              OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Details & Specifications") },
                placeholder = { Text("e.g. Needs 2 single size wooden beds") },
                maxLines = 2,
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("input_requirement_notes")
              )

              Spacer(modifier = Modifier.height(12.dp))

              Button(
                onClick = {
                  if (selectedItemName.isBlank()) {
                    itemError = true
                    return@Button
                  }
                  val qty = quantityStr.toIntOrNull() ?: 1
                  onAddRequirement(
                    accommodation.id,
                    selectedItemName.trim(),
                    qty,
                    urgency.trim(),
                    requestedBy.trim(),
                    notes.trim()
                  )
                  Toast.makeText(context, "Requirement added and notified!", Toast.LENGTH_SHORT).show()
                  selectedItemName = ""
                  quantityStr = "1"
                  requestedBy = ""
                  notes = ""
                  showAddForm = false
                },
                colors = ButtonDefaults.buttonColors(
                  containerColor = ZawitcoBlue,
                  contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(44.dp)
                  .testTag("submit_requirement_button")
              ) {
                Icon(imageVector = Icons.Outlined.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Submit Requirement", fontWeight = FontWeight.Bold)
              }
            }
          }
          Spacer(modifier = Modifier.height(14.dp))
        }

        // Requirements List
        if (requirements.isEmpty()) {
          Box(
            modifier = Modifier
              .weight(1f)
              .fillMaxWidth(),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Default.ShoppingBag,
                contentDescription = null,
                tint = Slate400,
                modifier = Modifier.size(40.dp)
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "No requirements currently recorded.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Slate500
              )
              Text(
                text = "Tap 'Request Item' to request beds, mattresses, gas, or appliances.",
                fontSize = 12.sp,
                color = Slate400
              )
            }
          }
        } else {
          LazyColumn(
            modifier = Modifier
              .weight(1f)
              .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(requirements, key = { it.id }) { req ->
              val isFulfilled = req.status == "Fulfilled"

              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("requirement_item_${req.id}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                  containerColor = if (isFulfilled) Slate100 else Color.White
                ),
                border = BorderStroke(1.dp, if (isFulfilled) Slate200 else ZawitcoLightBlue)
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = req.itemName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (isFulfilled) Slate500 else ZawitcoBlue
                      )
                      Spacer(modifier = Modifier.width(8.dp))
                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(6.dp))
                          .background(
                            if (req.urgency.contains("Urgent", true)) Color(0xFFFEE2E2) else ZawitcoLightBlue
                          )
                          .padding(horizontal = 6.dp, vertical = 2.dp)
                      ) {
                        Text(
                          text = "Qty: ${req.quantity} • ${req.urgency}",
                          fontSize = 10.sp,
                          fontWeight = FontWeight.Bold,
                          color = if (req.urgency.contains("Urgent", true)) Color(0xFFB91C1C) else ZawitcoBlue
                        )
                      }
                    }

                    if (req.requestedBy.isNotBlank()) {
                      Text(
                        text = "Requested by: ${req.requestedBy}",
                        fontSize = 12.sp,
                        color = Slate600
                      )
                    }

                    if (req.notes.isNotBlank()) {
                      Text(
                        text = "Notes: ${req.notes}",
                        fontSize = 12.sp,
                        color = Slate500
                      )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = "Status: ${req.status}",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.SemiBold,
                      color = if (isFulfilled) DarkGreen else ZawitcoOrange
                    )
                  }

                  // Action Buttons
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!isFulfilled) {
                      IconButton(
                        onClick = { onUpdateStatus(req, "Fulfilled") },
                        modifier = Modifier
                          .size(34.dp)
                          .background(LightGreen, CircleShape)
                          .testTag("fulfill_requirement_button_${req.id}")
                      ) {
                        Icon(
                          imageVector = Icons.Default.CheckCircle,
                          contentDescription = "Mark as Fulfilled",
                          tint = DarkGreen,
                          modifier = Modifier.size(18.dp)
                        )
                      }
                    }

                    if (isAdmin) {
                      Spacer(modifier = Modifier.width(6.dp))
                      IconButton(
                        onClick = { onDeleteRequirement(req) },
                        modifier = Modifier
                          .size(34.dp)
                          .testTag("delete_requirement_button_${req.id}")
                      ) {
                        Icon(
                          imageVector = Icons.Default.Delete,
                          contentDescription = "Delete",
                          tint = Color(0xFFEF4444),
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
    }
  }
}
