package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Station
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600
import com.example.ui.theme.ZawitcoBlue
import com.example.ui.theme.ZawitcoLightBlue
import com.example.ui.theme.ZawitcoOrange

@Composable
fun StationManagementDialog(
  stations: List<Station>,
  onDismiss: () -> Unit,
  onSaveStation: (id: Long, name: String, code: String, area: String, city: String, lat: Double?, lng: Double?, address: String, supervisor: String, phone: String, notes: String) -> Unit,
  onDeleteStation: (Station) -> Unit,
  modifier: Modifier = Modifier
) {
  var showAddForm by remember { mutableStateOf(false) }

  var name by remember { mutableStateOf("") }
  var code by remember { mutableStateOf("") }
  var areaName by remember { mutableStateOf("") }
  var city by remember { mutableStateOf("Riyadh") }
  var latitudeStr by remember { mutableStateOf("") }
  var longitudeStr by remember { mutableStateOf("") }
  var address by remember { mutableStateOf("") }
  var supervisorName by remember { mutableStateOf("") }
  var supervisorPhone by remember { mutableStateOf("") }
  var notes by remember { mutableStateOf("") }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.92f)
        .clip(RoundedCornerShape(24.dp))
        .testTag("station_management_dialog"),
      color = Color.White,
      shape = RoundedCornerShape(24.dp),
      shadowElevation = 16.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(20.dp)
      ) {
        // Dialog Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(ZawitcoLightBlue),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Hub,
                contentDescription = null,
                tint = ZawitcoBlue,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Station Locations",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
              )
              Text(
                text = "Company hubs, depots, and maintenance stations",
                fontSize = 12.sp,
                color = Color.Black
              )
            }
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Black)
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Action Bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Total Stations: ${stations.size}",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
          )

          Button(
            onClick = { showAddForm = !showAddForm },
            colors = ButtonDefaults.buttonColors(
              containerColor = if (showAddForm) Color.Black else ZawitcoBlue
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("toggle_add_station_button")
          ) {
            Icon(
              imageVector = if (showAddForm) Icons.Default.Close else Icons.Default.Add,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (showAddForm) "Close Form" else "Add New Station",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ADD NEW STATION FORM
        AnimatedVisibility(visible = showAddForm) {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 14.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(
                text = "Station Location Details",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
              )

              Spacer(modifier = Modifier.height(10.dp))

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                  value = name,
                  onValueChange = { name = it },
                  label = { Text("Station Name *", color = Color.Black) },
                  placeholder = { Text("e.g. Riyadh Central Hub", color = Slate400) },
                  singleLine = true,
                  shape = RoundedCornerShape(10.dp),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = ZawitcoBlue,
                    unfocusedBorderColor = Slate200,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                  ),
                  modifier = Modifier.weight(2f).testTag("station_name_input")
                )

                OutlinedTextField(
                  value = code,
                  onValueChange = { code = it },
                  label = { Text("Station Code", color = Color.Black) },
                  placeholder = { Text("e.g. RC-01", color = Slate400) },
                  singleLine = true,
                  shape = RoundedCornerShape(10.dp),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = ZawitcoBlue,
                    unfocusedBorderColor = Slate200,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                  ),
                  modifier = Modifier.weight(1f)
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                  value = areaName,
                  onValueChange = { areaName = it },
                  label = { Text("District / Area", color = Color.Black) },
                  placeholder = { Text("e.g. Al Olaya", color = Slate400) },
                  singleLine = true,
                  shape = RoundedCornerShape(10.dp),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = ZawitcoBlue,
                    unfocusedBorderColor = Slate200,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                  ),
                  modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                  value = city,
                  onValueChange = { city = it },
                  label = { Text("City", color = Color.Black) },
                  placeholder = { Text("Riyadh", color = Slate400) },
                  singleLine = true,
                  shape = RoundedCornerShape(10.dp),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = ZawitcoBlue,
                    unfocusedBorderColor = Slate200,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                  ),
                  modifier = Modifier.weight(1f)
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                  value = latitudeStr,
                  onValueChange = { latitudeStr = it },
                  label = { Text("Latitude", color = Color.Black) },
                  placeholder = { Text("24.7136", color = Slate400) },
                  singleLine = true,
                  shape = RoundedCornerShape(10.dp),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = ZawitcoBlue,
                    unfocusedBorderColor = Slate200,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                  ),
                  modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                  value = longitudeStr,
                  onValueChange = { longitudeStr = it },
                  label = { Text("Longitude", color = Color.Black) },
                  placeholder = { Text("46.6753", color = Slate400) },
                  singleLine = true,
                  shape = RoundedCornerShape(10.dp),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = ZawitcoBlue,
                    unfocusedBorderColor = Slate200,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                  ),
                  modifier = Modifier.weight(1f)
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                  value = supervisorName,
                  onValueChange = { supervisorName = it },
                  label = { Text("Supervisor Name", color = Color.Black) },
                  placeholder = { Text("Eng. Ahmed", color = Slate400) },
                  singleLine = true,
                  shape = RoundedCornerShape(10.dp),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = ZawitcoBlue,
                    unfocusedBorderColor = Slate200,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                  ),
                  modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                  value = supervisorPhone,
                  onValueChange = { supervisorPhone = it },
                  label = { Text("Supervisor Phone", color = Color.Black) },
                  placeholder = { Text("+966 ...", color = Slate400) },
                  singleLine = true,
                  shape = RoundedCornerShape(10.dp),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = ZawitcoBlue,
                    unfocusedBorderColor = Slate200,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                  ),
                  modifier = Modifier.weight(1f)
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address / Physical Location", color = Color.Black) },
                placeholder = { Text("e.g. King Fahd Road, Al Olaya", color = Slate400) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedTextColor = Color.Black,
                  unfocusedTextColor = Color.Black,
                  focusedBorderColor = ZawitcoBlue,
                  unfocusedBorderColor = Slate200,
                  focusedContainerColor = Color.White,
                  unfocusedContainerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
              )

              Spacer(modifier = Modifier.height(12.dp))

              Button(
                onClick = {
                  if (name.isNotBlank()) {
                    val lat = latitudeStr.toDoubleOrNull()
                    val lng = longitudeStr.toDoubleOrNull()
                    onSaveStation(0L, name, code, areaName, city, lat, lng, address, supervisorName, supervisorPhone, notes)
                    name = ""
                    code = ""
                    areaName = ""
                    latitudeStr = ""
                    longitudeStr = ""
                    address = ""
                    supervisorName = ""
                    supervisorPhone = ""
                    showAddForm = false
                  }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ZawitcoBlue),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(44.dp)
                  .testTag("save_station_button")
              ) {
                Text("Save Station Location", fontWeight = FontWeight.Bold, color = Color.White)
              }
            }
          }
        }

        // STATIONS LIST
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .weight(1f),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(stations, key = { it.id }) { station ->
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
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
                      imageVector = Icons.Default.Hub,
                      contentDescription = null,
                      tint = ZawitcoBlue,
                      modifier = Modifier.size(22.dp)
                    )
                  }

                  Spacer(modifier = Modifier.width(10.dp))

                  Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = station.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.Black
                      )
                      if (station.code.isNotBlank()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                          shape = RoundedCornerShape(6.dp),
                          color = Color(0xFFEFF6FF)
                        ) {
                          Text(
                            text = station.code,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ZawitcoBlue,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                          )
                        }
                      }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                      text = "${station.areaName.ifBlank { "District unassigned" }} • ${station.city}",
                      fontSize = 12.sp,
                      color = Color.Black
                    )

                    if (station.latitude != null && station.longitude != null) {
                      Text(
                        text = "GPS: ${String.format("%.4f", station.latitude)}, ${String.format("%.4f", station.longitude)}",
                        fontSize = 11.sp,
                        color = Color(0xFF047857),
                        fontWeight = FontWeight.Medium
                      )
                    }

                    if (station.supervisorName.isNotBlank()) {
                      Text(
                        text = "Supervisor: ${station.supervisorName} (${station.supervisorPhone})",
                        fontSize = 11.sp,
                        color = Color.Black
                      )
                    }
                  }
                }

                IconButton(
                  onClick = { onDeleteStation(station) },
                  modifier = Modifier.testTag("delete_station_${station.id}")
                ) {
                  Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Station",
                    tint = Color(0xFFDC2626)
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
