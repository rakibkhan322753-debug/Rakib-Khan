package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.ZawitcoBlue

private val WhatsAppGreen = Color(0xFF25D366)
private val WhatsAppDarkGreen = Color(0xFF128C7E)
private val WhatsAppLightGreen = Color(0xFFE8F8F0)

@Composable
fun WhatsAppGroupDialog(
  currentGroupUrl: String,
  isAdmin: Boolean,
  onUpdateGroupUrl: (String) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var isEditing by remember { mutableStateOf(false) }
  var editedUrl by remember { mutableStateOf(currentGroupUrl) }

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
          .padding(22.dp)
      ) {
        // Header
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
                .background(WhatsAppLightGreen),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Group,
                contentDescription = null,
                tint = WhatsAppGreen,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "WhatsApp Community",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Slate800
              )
              Text(
                text = "Zawitco Housing Group",
                fontSize = 12.sp,
                color = WhatsAppDarkGreen,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_whatsapp_dialog")) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate500)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "Join our official WhatsApp group for instant accommodation announcements, urgent maintenance requests, and direct communication with building supervisors.",
          fontSize = 13.sp,
          color = Slate600,
          lineHeight = 19.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isEditing && isAdmin) {
          OutlinedTextField(
            value = editedUrl,
            onValueChange = { editedUrl = it },
            label = { Text("WhatsApp Group Invite URL") },
            placeholder = { Text("https://chat.whatsapp.com/...") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(10.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
          ) {
            OutlinedButton(
              onClick = {
                isEditing = false
                editedUrl = currentGroupUrl
              },
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("Cancel")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
              onClick = {
                if (editedUrl.isNotBlank()) {
                  onUpdateGroupUrl(editedUrl.trim())
                  isEditing = false
                  Toast.makeText(context, "WhatsApp group link updated", Toast.LENGTH_SHORT).show()
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = WhatsAppDarkGreen),
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("Save Link")
            }
          }
        } else {
          // Link Display Box
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .border(1.dp, Slate200, RoundedCornerShape(12.dp)),
            color = Color(0xFFF8FAFC)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "Group Invite Link",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Slate500
                )
                Text(
                  text = currentGroupUrl.ifBlank { "No link configured yet" },
                  fontSize = 13.sp,
                  color = Slate800,
                  maxLines = 1
                )
              }

              if (isAdmin) {
                IconButton(onClick = { isEditing = true }) {
                  Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit link",
                    tint = ZawitcoBlue,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Action buttons: Open in WhatsApp & Copy Link
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedButton(
              onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Zawitco WhatsApp Group", currentGroupUrl)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Link copied to clipboard", Toast.LENGTH_SHORT).show()
              },
              modifier = Modifier
                .weight(1f)
                .testTag("copy_whatsapp_link_button"),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(Icons.Default.ContentCopy, contentDescription = null, Modifier.size(16.dp))
              Spacer(Modifier.width(6.dp))
              Text("Copy Link")
            }

            Button(
              onClick = {
                try {
                  val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentGroupUrl))
                  context.startActivity(intent)
                } catch (e: Exception) {
                  Toast.makeText(context, "Could not open WhatsApp link.", Toast.LENGTH_SHORT).show()
                }
              },
              colors = ButtonDefaults.buttonColors(
                containerColor = WhatsAppGreen,
                contentColor = Color.White
              ),
              modifier = Modifier
                .weight(1.3f)
                .testTag("join_whatsapp_button"),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, Modifier.size(16.dp))
              Spacer(Modifier.width(6.dp))
              Text("Open WhatsApp", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}
