package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.cloud.CloudBackupSummary
import com.example.ui.theme.DarkGreen
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.LightGreen
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.ZawitcoBlue
import com.example.ui.theme.ZawitcoLightBlue
import com.example.ui.theme.ZawitcoOrange

@Composable
fun CloudBackupDialog(
  localAccommodationsCount: Int,
  localRequirementsCount: Int,
  localReportsCount: Int,
  localStationsCount: Int,
  localUsersCount: Int,
  latestBackupInfo: CloudBackupSummary?,
  isSyncing: Boolean,
  statusMessage: String?,
  onBackupToCloud: (onResult: (Boolean, String) -> Unit) -> Unit,
  onRestoreFromCloud: (onResult: (Boolean, String) -> Unit) -> Unit,
  onExportJsonBackup: () -> String,
  onImportJsonBackup: (String, onResult: (Boolean, String) -> Unit) -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var localStatus by remember { mutableStateOf<String?>(statusMessage) }
  var isLocalSuccess by remember { mutableStateOf<Boolean?>(null) }
  var showRestoreConfirmDialog by remember { mutableStateOf(false) }
  var showImportJsonDialog by remember { mutableStateOf(false) }
  var importJsonInput by remember { mutableStateOf("") }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.92f)
        .clip(RoundedCornerShape(24.dp))
        .testTag("cloud_backup_dialog"),
      color = Color.White,
      shape = RoundedCornerShape(24.dp),
      shadowElevation = 16.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(20.dp)
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
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFFE0F2FE)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.CloudSync,
                contentDescription = null,
                tint = ZawitcoBlue,
                modifier = Modifier.size(26.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Google Cloud / Firebase",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = ZawitcoBlue
              )
              Text(
                text = "Cloud Server Backup & Data Storage",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(36.dp)
              .testTag("cloud_backup_close_button")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = Color.Black
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = Slate200)
        Spacer(modifier = Modifier.height(12.dp))

        // Scrollable content
        Column(
          modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
        ) {
          // Cloud Server Status Banner
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
            border = BorderStroke(1.dp, Color(0xFFBBF7D0))
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(Color(0xFFDCFCE7)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.CloudDone,
                  contentDescription = null,
                  tint = DarkGreen,
                  modifier = Modifier.size(20.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "Cloud Server Connected",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Box(
                    modifier = Modifier
                      .size(8.dp)
                      .clip(CircleShape)
                      .background(DarkGreen)
                  )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "Backend: Google Cloud Firestore\nProject: gen-lang-client-0179740022",
                  fontSize = 11.sp,
                  color = Color.Black,
                  lineHeight = 15.sp
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Local Database Stats Card
          Text(
            text = "CURRENT LOCAL DATABASE RECORDS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = ZawitcoBlue,
            letterSpacing = 0.5.sp
          )
          Spacer(modifier = Modifier.height(8.dp))

          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Slate100),
            border = BorderStroke(1.dp, Slate200)
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                DataStatBadge(
                  icon = Icons.Default.Home,
                  label = "Housing",
                  count = localAccommodationsCount,
                  tint = ZawitcoBlue
                )
                DataStatBadge(
                  icon = Icons.Default.ShoppingBag,
                  label = "Requirements",
                  count = localRequirementsCount,
                  tint = ZawitcoOrange
                )
                DataStatBadge(
                  icon = Icons.Default.ReportProblem,
                  label = "Reports",
                  count = localReportsCount,
                  tint = Color(0xFFDC2626)
                )
                DataStatBadge(
                  icon = Icons.Default.Hub,
                  label = "Stations",
                  count = localStationsCount,
                  tint = DarkGreen
                )
                DataStatBadge(
                  icon = Icons.Default.ManageAccounts,
                  label = "Users",
                  count = localUsersCount,
                  tint = Color(0xFF7C3AED)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Latest Cloud Backup Info
          Text(
            text = "LAST CLOUD SERVER BACKUP",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = ZawitcoBlue,
            letterSpacing = 0.5.sp
          )
          Spacer(modifier = Modifier.height(8.dp))

          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = ZawitcoLightBlue),
            border = BorderStroke(1.dp, ZawitcoBlue.copy(alpha = 0.3f))
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              if (latestBackupInfo != null) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "Backup Date & Time",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                  )
                  Text(
                    text = latestBackupInfo.formattedDate.ifBlank { "Recently synced" },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZawitcoBlue
                  )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = "Total Records Stored",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                  )
                  val total = latestBackupInfo.accommodationsCount +
                    latestBackupInfo.requirementsCount +
                    latestBackupInfo.reportsCount +
                    latestBackupInfo.stationsCount +
                    latestBackupInfo.usersCount
                  Text(
                    text = "$total items",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen
                  )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Housing: ${latestBackupInfo.accommodationsCount}  •  Reqs: ${latestBackupInfo.requirementsCount}  •  Reports: ${latestBackupInfo.reportsCount}  •  Stations: ${latestBackupInfo.stationsCount}  •  Users: ${latestBackupInfo.usersCount}",
                  fontSize = 10.sp,
                  color = Color.Black
                )
              } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Cloud,
                    contentDescription = null,
                    tint = Slate400,
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "No previous cloud backup metadata found. Tap 'Backup to Cloud Server' below to save all your data now.",
                    fontSize = 12.sp,
                    color = Color.Black,
                    lineHeight = 16.sp
                  )
                }
              }
            }
          }

          // Status message banner if any
          if (!localStatus.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(14.dp))
            val isSuccess = isLocalSuccess == true
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (isSuccess) Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
              ),
              border = BorderStroke(1.dp, if (isSuccess) DarkGreen else Color(0xFFEF4444))
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.ReportProblem,
                  contentDescription = null,
                  tint = if (isSuccess) DarkGreen else Color(0xFFDC2626),
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = localStatus.orEmpty(),
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = if (isSuccess) DarkGreen else Color(0xFFDC2626)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          // ==========================================
          // ACTION BUTTONS
          // ==========================================
          Text(
            text = "CLOUD SERVER ACTIONS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = ZawitcoBlue,
            letterSpacing = 0.5.sp
          )
          Spacer(modifier = Modifier.height(10.dp))

          // 1. BACKUP TO CLOUD SERVER BUTTON
          Button(
            onClick = {
              localStatus = "Connecting to Google Cloud and backing up all data..."
              isLocalSuccess = null
              onBackupToCloud { success, message ->
                localStatus = message
                isLocalSuccess = success
              }
            },
            enabled = !isSyncing,
            colors = ButtonDefaults.buttonColors(
              containerColor = ZawitcoBlue,
              contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("backup_to_cloud_button")
          ) {
            if (isSyncing) {
              CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text("Syncing to Cloud Server...", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            } else {
              Icon(
                imageVector = Icons.Default.CloudUpload,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Backup Now to Cloud Server",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // 2. RESTORE FROM CLOUD SERVER BUTTON
          OutlinedButton(
            onClick = { showRestoreConfirmDialog = true },
            enabled = !isSyncing,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.5.dp, DarkGreen),
            colors = ButtonDefaults.outlinedButtonColors(
              contentColor = DarkGreen
            ),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("restore_from_cloud_button")
          ) {
            Icon(
              imageVector = Icons.Default.CloudDownload,
              contentDescription = null,
              tint = DarkGreen,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Restore All Data from Cloud Server",
              fontWeight = FontWeight.Bold,
              color = DarkGreen,
              fontSize = 14.sp
            )
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Offline file backup actions
          Text(
            text = "OFFLINE BACKUP FILE ACTIONS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Slate600,
            letterSpacing = 0.5.sp
          )
          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // Export JSON
            OutlinedButton(
              onClick = {
                val json = onExportJsonBackup()
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Zawitco Housing Backup", json)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Full Backup copied to clipboard!", Toast.LENGTH_LONG).show()

                // Share intent
                try {
                  val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Zawitco Housing Database Backup")
                    putExtra(Intent.EXTRA_TEXT, json)
                  }
                  context.startActivity(Intent.createChooser(shareIntent, "Save / Share Database Backup"))
                } catch (e: Exception) {
                  // Ignore
                }
              },
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .weight(1f)
                .height(42.dp)
                .testTag("export_backup_file_button")
            ) {
              Icon(
                imageVector = Icons.Default.FileDownload,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text("Export File", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 12.sp)
            }

            // Import JSON
            OutlinedButton(
              onClick = { showImportJsonDialog = true },
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .weight(1f)
                .height(42.dp)
                .testTag("import_backup_file_button")
            ) {
              Icon(
                imageVector = Icons.Default.FileUpload,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text("Import File", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 12.sp)
            }
          }

          Spacer(modifier = Modifier.height(16.dp))
        }

        // Bottom Dismiss Button
        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(
            containerColor = Slate200,
            contentColor = Color.Black
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
        ) {
          Text("Done / Close", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp)
        }
      }
    }
  }

  // Restore Confirmation Dialog
  if (showRestoreConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showRestoreConfirmDialog = false },
      title = {
        Text("Restore from Cloud Server?", fontWeight = FontWeight.Bold, color = Color.Black)
      },
      text = {
        Text(
          text = "This will fetch all housing units, requirements, reports, stations, and users from Google Cloud Firestore and merge them into your local database.\n\nAre you sure you want to proceed?",
          color = Color.Black,
          fontSize = 13.sp
        )
      },
      confirmButton = {
        Button(
          onClick = {
            showRestoreConfirmDialog = false
            localStatus = "Restoring data from Google Cloud Server..."
            isLocalSuccess = null
            onRestoreFromCloud { success, message ->
              localStatus = message
              isLocalSuccess = success
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = DarkGreen, contentColor = Color.White)
        ) {
          Text("Yes, Restore", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showRestoreConfirmDialog = false }) {
          Text("Cancel", color = Color.Black)
        }
      }
    )
  }

  // Import JSON Backup Dialog
  if (showImportJsonDialog) {
    AlertDialog(
      onDismissRequest = { showImportJsonDialog = false },
      title = {
        Text("Import Database Backup JSON", fontWeight = FontWeight.Bold, color = Color.Black)
      },
      text = {
        Column {
          Text(
            text = "Paste your exported JSON backup text below to restore records:",
            fontSize = 12.sp,
            color = Color.Black
          )
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = importJsonInput,
            onValueChange = { importJsonInput = it },
            placeholder = { Text("Paste JSON backup data here...", fontSize = 11.sp, color = Slate400) },
            modifier = Modifier
              .fillMaxWidth()
              .height(180.dp),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = Color.Black,
              unfocusedTextColor = Color.Black
            )
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (importJsonInput.isNotBlank()) {
              onImportJsonBackup(importJsonInput) { success, message ->
                localStatus = message
                isLocalSuccess = success
                if (success) {
                  showImportJsonDialog = false
                }
              }
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = ZawitcoBlue, contentColor = Color.White)
        ) {
          Text("Import Now", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showImportJsonDialog = false }) {
          Text("Cancel", color = Color.Black)
        }
      }
    )
  }
}

@Composable
private fun DataStatBadge(
  icon: ImageVector,
  label: String,
  count: Int,
  tint: Color
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.padding(horizontal = 4.dp)
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = tint,
      modifier = Modifier.size(18.dp)
    )
    Spacer(modifier = Modifier.height(3.dp))
    Text(
      text = "$count",
      fontSize = 14.sp,
      fontWeight = FontWeight.Black,
      color = Color.Black
    )
    Text(
      text = label,
      fontSize = 9.sp,
      fontWeight = FontWeight.Medium,
      color = Slate600
    )
  }
}
