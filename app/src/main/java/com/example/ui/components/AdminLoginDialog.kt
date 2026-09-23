package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.DarkGreen
import com.example.ui.theme.LightGreen
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.ZawitcoBlue
import com.example.ui.theme.ZawitcoLightBlue
import com.example.ui.theme.ZawitcoOrange

@Composable
fun AdminLoginDialog(
  isAdmin: Boolean,
  currentPin: String,
  onLogin: (enteredPin: String) -> Boolean,
  onLogout: () -> Unit,
  onChangePin: (newPin: String) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  var pinInput by remember { mutableStateOf("") }
  var isError by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf("") }
  var isChangingPin by remember { mutableStateOf(false) }
  var newPinInput by remember { mutableStateOf("") }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      modifier = modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(24.dp)),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 8.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Header row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isAdmin) LightGreen else ZawitcoLightBlue),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (isAdmin) Icons.Default.LockOpen else Icons.Default.Security,
                contentDescription = null,
                tint = if (isAdmin) DarkGreen else ZawitcoBlue,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = if (isAdmin) "Admin Mode Active" else "Admin Authentication",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isAdmin) DarkGreen else ZawitcoBlue
              )
              Text(
                text = if (isAdmin) "Full CRUD and management access" else "Restricted data entry & edits",
                fontSize = 12.sp,
                color = Slate500
              )
            }
          }

          IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_admin_dialog")) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate500)
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (isAdmin) {
          // Already Admin View
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(LightGreen.copy(alpha = 0.5f))
              .padding(16.dp)
          ) {
            Column {
              Text(
                text = "✓ Full Access Granted",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "You currently have administrator privileges to add new accommodations, edit information, delete listings, and update settings.",
                fontSize = 13.sp,
                color = Slate700,
                lineHeight = 18.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          if (!isChangingPin) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              OutlinedButton(
                onClick = { isChangingPin = true },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
              ) {
                Icon(Icons.Default.Key, contentDescription = null, Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Change PIN")
              }

              Button(
                onClick = {
                  onLogout()
                  onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier
                  .weight(1f)
                  .testTag("admin_logout_button"),
                shape = RoundedCornerShape(12.dp)
              ) {
                Icon(Icons.Default.Lock, contentDescription = null, Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Lock Admin")
              }
            }
          } else {
            // Change PIN Section
            OutlinedTextField(
              value = newPinInput,
              onValueChange = { newPinInput = it },
              label = { Text("Enter New Security PIN") },
              placeholder = { Text("e.g. 4-6 digits") },
              singleLine = true,
              keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Done
              ),
              modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.End
            ) {
              OutlinedButton(
                onClick = {
                  isChangingPin = false
                  newPinInput = ""
                },
                shape = RoundedCornerShape(10.dp)
              ) {
                Text("Cancel")
              }
              Spacer(modifier = Modifier.width(8.dp))
              Button(
                onClick = {
                  if (newPinInput.isNotBlank() && newPinInput.length >= 4) {
                    onChangePin(newPinInput.trim())
                    isChangingPin = false
                    newPinInput = ""
                    onDismiss()
                  }
                },
                enabled = newPinInput.length >= 4,
                colors = ButtonDefaults.buttonColors(containerColor = ZawitcoBlue),
                shape = RoundedCornerShape(10.dp)
              ) {
                Text("Save New PIN")
              }
            }
          }

        } else {
          // Login Form
          Text(
            text = "Only authorized administrators can add, edit, or delete accommodation listings and configure housing settings.",
            fontSize = 13.sp,
            color = Slate700,
            lineHeight = 18.sp
          )

          Spacer(modifier = Modifier.height(16.dp))

          OutlinedTextField(
            value = pinInput,
            onValueChange = {
              pinInput = it
              isError = false
              errorMessage = ""
            },
            label = { Text("Admin Security PIN / Password") },
            placeholder = { Text("Enter admin PIN") },
            singleLine = true,
            isError = isError,
            supportingText = if (isError) {
              { Text(errorMessage, color = MaterialTheme.colorScheme.error) }
            } else null,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
              keyboardType = KeyboardType.NumberPassword,
              imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
              onDone = {
                val success = onLogin(pinInput)
                if (success) {
                  onDismiss()
                } else {
                  isError = true
                  errorMessage = "Incorrect PIN. Please try again."
                }
              }
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("admin_pin_input")
          )

          Spacer(modifier = Modifier.height(18.dp))

          Button(
            onClick = {
              val success = onLogin(pinInput)
              if (success) {
                onDismiss()
              } else {
                isError = true
                errorMessage = "Incorrect PIN. Please try again."
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = ZawitcoOrange),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("submit_admin_pin_button")
          ) {
            Icon(Icons.Default.LockOpen, contentDescription = null, Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Unlock Admin Access", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
