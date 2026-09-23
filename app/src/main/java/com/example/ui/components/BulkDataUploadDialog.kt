package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderOpen
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600
import com.example.ui.theme.ZawitcoBlue
import com.example.ui.theme.ZawitcoLightBlue
import com.example.ui.theme.ZawitcoOrange
import com.example.util.BulkDataParser
import com.example.util.BulkParseResult
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
fun BulkDataUploadDialog(
  onDismiss: () -> Unit,
  onImportData: (rawCsv: String, onComplete: (BulkParseResult) -> Unit) -> Unit,
  onAnalysisReady: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var rawCsvText by remember { mutableStateOf("") }
  var fileNameSelected by remember { mutableStateOf<String?>(null) }
  var parseResult by remember { mutableStateOf<BulkParseResult?>(null) }
  var isProcessing by remember { mutableStateOf(false) }

  // Document Picker for CSV / text file upload
  val filePickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri: Uri? ->
    if (uri != null) {
      try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val text = reader.readText()
        reader.close()
        rawCsvText = text
        fileNameSelected = uri.lastPathSegment ?: "Uploaded_File.csv"
      } catch (e: Exception) {
        rawCsvText = "Error reading file: ${e.message}"
      }
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.92f)
        .clip(RoundedCornerShape(24.dp))
        .testTag("bulk_upload_dialog"),
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
                .size(42.dp)
                .clip(CircleShape)
                .background(ZawitcoLightBlue),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.CloudUpload,
                contentDescription = null,
                tint = ZawitcoBlue,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Bulk Data File Upload",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
              )
              Text(
                text = "Import Accommodations & Stations with auto-analysis",
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

        Column(
          modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
        ) {
          // Upload File Card
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
          ) {
            Column(
              modifier = Modifier.padding(16.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "Select CSV, TSV, or Text Data File",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Upload property rows, coordinates, and station hub records",
                fontSize = 12.sp,
                color = Color.Black
              )

              Spacer(modifier = Modifier.height(12.dp))

              Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Button(
                  onClick = { filePickerLauncher.launch("*/*") },
                  colors = ButtonDefaults.buttonColors(containerColor = ZawitcoBlue),
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier.testTag("choose_file_button")
                ) {
                  Icon(Icons.Default.FolderOpen, contentDescription = null, tint = Color.White)
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Choose File from Device", fontWeight = FontWeight.Bold, color = Color.White)
                }
              }

              if (fileNameSelected != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Description, contentDescription = null, tint = ZawitcoBlue, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "Loaded: $fileNameSelected",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZawitcoBlue
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Quick Templates Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Or Paste Bulk CSV Data:",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = Color.Black
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              OutlinedButton(
                onClick = { rawCsvText = BulkDataParser.getSampleAccommodationCsv() },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("sample_acc_csv_button")
              ) {
                Text("Sample Housing", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
              }

              OutlinedButton(
                onClick = { rawCsvText = BulkDataParser.getSampleStationCsv() },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("sample_stn_csv_button")
              ) {
                Text("Sample Stations", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Raw CSV Text Field
          OutlinedTextField(
            value = rawCsvText,
            onValueChange = { rawCsvText = it },
            placeholder = {
              Text(
                text = "Area, Villa, Floor, Room, WorkerPhone, OwnerPhone, Latitude, Longitude, StationName, Notes\n\nAl Olaya, 14B, 2nd, 201, +96650..., +96655..., 24.7136, 46.6753, Riyadh Central Hub, notes...",
                color = Slate400,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
              )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = Color.Black,
              unfocusedTextColor = Color.Black,
              focusedBorderColor = ZawitcoBlue,
              unfocusedBorderColor = Slate200,
              focusedContainerColor = Color(0xFFFAFAFA),
              unfocusedContainerColor = Color(0xFFFAFAFA)
            ),
            textStyle = androidx.compose.ui.text.TextStyle(
              fontFamily = FontFamily.Monospace,
              fontSize = 12.sp,
              color = Color.Black
            ),
            modifier = Modifier
              .fillMaxWidth()
              .height(180.dp)
              .testTag("bulk_csv_input")
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Parsing Results Banner
          parseResult?.let { res ->
            Surface(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              color = if (res.errors.isEmpty()) Color(0xFFD1FAE5) else Color(0xFFFEF3C7)
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = if (res.errors.isEmpty()) Color(0xFF047857) else Color(0xFFB45309),
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Parsed ${res.accommodations.size} Accommodations and ${res.stations.size} Stations successfully!",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (res.errors.isEmpty()) Color(0xFF065F46) else Color(0xFF92400E)
                  )
                }
                if (res.errors.isNotEmpty()) {
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "Warnings: ${res.errors.joinToString("; ")}",
                    fontSize = 11.sp,
                    color = Color(0xFFB45309)
                  )
                }
              }
            }
            Spacer(modifier = Modifier.height(10.dp))
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = onDismiss,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f)
          ) {
            Text("Cancel", fontWeight = FontWeight.Bold, color = Color.Black)
          }

          Button(
            onClick = {
              if (rawCsvText.isNotBlank()) {
                isProcessing = true
                onImportData(rawCsvText) { result ->
                  parseResult = result
                  isProcessing = false
                  // Auto launch analysis screen after importing
                  onAnalysisReady()
                }
              }
            },
            enabled = rawCsvText.isNotBlank() && !isProcessing,
            colors = ButtonDefaults.buttonColors(containerColor = ZawitcoBlue),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(2f)
              .height(48.dp)
              .testTag("process_bulk_data_button")
          ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (isProcessing) "Analyzing Data..." else "Process & Analyze Location Data",
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }
      }
    }
  }
}
