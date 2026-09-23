package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.UserAccount
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600
import com.example.ui.theme.ZawitcoBlue
import com.example.ui.theme.ZawitcoLightBlue
import com.example.ui.theme.ZawitcoOrange

@Composable
fun UserAccountManagementDialog(
  users: List<UserAccount>,
  stations: List<String>,
  onDismiss: () -> Unit,
  onCreateUser: (username: String, pass: String, fullName: String, role: String, station: String, phone: String, onResult: (Boolean, String) -> Unit) -> Unit,
  onDeleteUser: (UserAccount) -> Unit,
  modifier: Modifier = Modifier
) {
  var showCreateForm by remember { mutableStateOf(false) }

  var fullName by remember { mutableStateOf("") }
  var username by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var selectedRole by remember { mutableStateOf("USER") }
  var assignedStation by remember { mutableStateOf(stations.firstOrNull() ?: "Riyadh Central Hub") }
  var phone by remember { mutableStateOf("") }

  var feedbackMessage by remember { mutableStateOf<String?>(null) }
  var isErrorFeedback by remember { mutableStateOf(false) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.92f)
        .clip(RoundedCornerShape(24.dp))
        .testTag("user_management_dialog"),
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
                imageVector = Icons.Default.ManageAccounts,
                contentDescription = null,
                tint = ZawitcoBlue,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "User Accounts (Admin Only)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
              )
              Text(
                text = "Create and manage system logins for staff",
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

        // Feedback Banner
        feedbackMessage?.let { msg ->
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 12.dp),
            shape = RoundedCornerShape(10.dp),
            color = if (isErrorFeedback) Color(0xFFFEE2E2) else Color(0xFFD1FAE5)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = msg,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isErrorFeedback) Color(0xFF991B1B) else Color(0xFF065F46)
              )
            }
          }
        }

        // Toggle Button: Create User vs View Users
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Total Registered Users: ${users.size}",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
          )

          Button(
            onClick = { showCreateForm = !showCreateForm },
            colors = ButtonDefaults.buttonColors(
              containerColor = if (showCreateForm) Color.Black else ZawitcoOrange
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("toggle_create_user_button")
          ) {
            Icon(
              imageVector = if (showCreateForm) Icons.Default.Close else Icons.Default.Add,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (showCreateForm) "Hide Form" else "Create New User",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // CREATE NEW USER FORM
        AnimatedVisibility(visible = showCreateForm) {
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
                text = "New User Account Details",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
              )

              Spacer(modifier = Modifier.height(10.dp))

              OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name", color = Color.Black) },
                placeholder = { Text("e.g. Mohammed Al-Rashid", color = Slate400) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = ZawitcoBlue) },
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
                modifier = Modifier.fillMaxWidth().testTag("new_user_fullname")
              )

              Spacer(modifier = Modifier.height(8.dp))

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                  value = username,
                  onValueChange = { username = it },
                  label = { Text("Username", color = Color.Black) },
                  placeholder = { Text("e.g. rashid01", color = Slate400) },
                  leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = ZawitcoBlue) },
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
                  modifier = Modifier.weight(1f).testTag("new_user_username")
                )

                OutlinedTextField(
                  value = password,
                  onValueChange = { password = it },
                  label = { Text("Password", color = Color.Black) },
                  placeholder = { Text("Set password", color = Slate400) },
                  leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = ZawitcoOrange) },
                  singleLine = true,
                  shape = RoundedCornerShape(10.dp),
                  colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = ZawitcoOrange,
                    unfocusedBorderColor = Slate200,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                  ),
                  modifier = Modifier.weight(1f).testTag("new_user_password")
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              // Role Selector: USER, SUPERVISOR, ADMIN
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Role:",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                listOf("USER", "SUPERVISOR", "ADMIN").forEach { role ->
                  val isSelected = selectedRole == role
                  Surface(
                    onClick = { selectedRole = role },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) (if (role == "ADMIN") ZawitcoOrange else ZawitcoBlue) else Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Color.Transparent else Slate200),
                    modifier = Modifier.padding(end = 6.dp)
                  ) {
                    Text(
                      text = role,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (isSelected) Color.White else Color.Black,
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                  value = assignedStation,
                  onValueChange = { assignedStation = it },
                  label = { Text("Assigned Station", color = Color.Black) },
                  placeholder = { Text("e.g. Riyadh Central", color = Slate400) },
                  leadingIcon = { Icon(Icons.Default.Place, contentDescription = null, tint = ZawitcoBlue) },
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
                  value = phone,
                  onValueChange = { phone = it },
                  label = { Text("Phone", color = Color.Black) },
                  placeholder = { Text("+966 ...", color = Slate400) },
                  leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Slate600) },
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

              Spacer(modifier = Modifier.height(12.dp))

              Button(
                onClick = {
                  onCreateUser(username, password, fullName, selectedRole, assignedStation, phone) { success, msg ->
                    feedbackMessage = msg
                    isErrorFeedback = !success
                    if (success) {
                      fullName = ""
                      username = ""
                      password = ""
                      phone = ""
                      showCreateForm = false
                    }
                  }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ZawitcoBlue),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(44.dp)
                  .testTag("submit_create_user_button")
              ) {
                Text("Save and Create User Account", fontWeight = FontWeight.Bold, color = Color.White)
              }
            }
          }
        }

        // USERS LIST
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .weight(1f),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(users, key = { it.id }) { user ->
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
                      .size(38.dp)
                      .clip(CircleShape)
                      .background(if (user.role == "ADMIN") Color(0xFFFFF3EC) else ZawitcoLightBlue),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = if (user.role == "ADMIN") Icons.Default.Security else Icons.Default.Person,
                      contentDescription = null,
                      tint = if (user.role == "ADMIN") ZawitcoOrange else ZawitcoBlue,
                      modifier = Modifier.size(20.dp)
                    )
                  }

                  Spacer(modifier = Modifier.width(10.dp))

                  Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = user.fullName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.Black
                      )
                      Spacer(modifier = Modifier.width(6.dp))
                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (user.role == "ADMIN") Color(0xFFFFEDD5) else Color(0xFFE0F2FE)
                      ) {
                        Text(
                          text = user.role,
                          fontSize = 10.sp,
                          fontWeight = FontWeight.ExtraBold,
                          color = if (user.role == "ADMIN") Color(0xFFC2410C) else Color(0xFF0369A1),
                          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                      }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                      text = "Username: ${user.username} • Station: ${user.assignedStation.ifBlank { "All" }}",
                      fontSize = 12.sp,
                      color = Color.Black
                    )

                    if (user.phone.isNotBlank()) {
                      Text(
                        text = "Phone: ${user.phone}",
                        fontSize = 11.sp,
                        color = Color.Black
                      )
                    }
                  }
                }

                // Delete button (prevent deleting root admin)
                if (user.username != "admin") {
                  IconButton(
                    onClick = { onDeleteUser(user) },
                    modifier = Modifier.testTag("delete_user_${user.id}")
                  ) {
                    Icon(
                      imageVector = Icons.Default.Delete,
                      contentDescription = "Delete User",
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
}
