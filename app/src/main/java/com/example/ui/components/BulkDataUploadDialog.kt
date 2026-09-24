package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.data.model.Accommodation
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.ZawitcoBlue
import com.example.ui.theme.ZawitcoDarkOrange
import com.example.ui.theme.ZawitcoLightBlue
import com.example.ui.theme.ZawitcoOrange
import com.example.util.BulkDataParser
import com.example.util.BulkParseResult
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
fun BulkDataUploadDialog(
  onDismiss: () -> Unit,
  onImportData: (String, (BulkParseResult) -> Unit) -> Unit,
  onImportSingle: ((Accommodation, () -> Unit) -> Unit)? = null,
  onAnalysisReady: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()

  // Tabs: 0: Automated Google Sheets, 1: Single Accommodation Import, 2: Bulk CSV Upload
  var selectedTab by remember { mutableIntStateOf(0) }

  // Google Sheets tab state
  var sheetUrl by remember { mutableStateOf(BulkDataParser.DEFAULT_GOOGLE_SHEETS_URL) }
  var isFetchingSheet by remember { mutableStateOf(false) }
  var sheetFetchMessage by remember { mutableStateOf<String?>(null) }
  var rawCsvText by remember { mutableStateOf("") }

  // Single Accommodation tab state
  var singleRawInput by remember { mutableStateOf("") }
  var parsedSingleItem by remember { mutableStateOf<Accommodation?>(null) }
  var singleSuccessMessage by remember { mutableStateOf<String?>(null) }

  // Bulk general state
  var isProcessing by remember { mutableStateOf(false) }
  var parseResult by remember { mutableStateOf<BulkParseResult?>(null) }

  // File Picker for CSV
  val filePickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri: Uri? ->
    if (uri != null) {
      try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val reader = BufferedReader(InputStreamReader(inputStream))
        rawCsvText = reader.readText()
        reader.close()
        Toast.makeText(context, "File loaded successfully!", Toast.LENGTH_SHORT).show()
      } catch (e: Exception) {
        Toast.makeText(context, "Error reading file: ${e.message}", Toast.LENGTH_LONG).show()
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
        .fillMaxHeight(0.94f)
        .clip(RoundedCornerShape(24.dp)),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
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
                .size(40.dp)
                .clip(CircleShape)
                .background(ZawitcoLightBlue),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.CloudDownload,
                contentDescription = null,
                tint = ZawitcoBlue,
                modifier = Modifier.size(22.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Accommodation Data Import",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ZawitcoBlue
              )
              Text(
                text = "Google Sheets & Single Housing Import",
                fontSize = 12.sp,
                color = Slate600
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(36.dp)
              .background(Slate100, CircleShape)
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = Color.Black,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Navigation Tabs: Google Sheets vs Single Import vs Bulk CSV
        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = Color(0xFFF1F5F9),
          contentColor = ZawitcoBlue,
          indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
              Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
              color = ZawitcoBlue
            )
          },
          modifier = Modifier.clip(RoundedCornerShape(10.dp))
        ) {
          Tab(
            selected = selectedTab == 0,
            onClick = { selectedTab = 0 },
            text = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Google Sheets", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }
          )

          Tab(
            selected = selectedTab == 1,
            onClick = { selectedTab = 1 },
            text = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Single Import", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }
          )

          Tab(
            selected = selectedTab == 2,
            onClick = { selectedTab = 2 },
            text = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Bulk CSV File", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Scrollable Tab Content
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .verticalScroll(rememberScrollState())
        ) {
          when (selectedTab) {
            // ==========================================
            // TAB 0: AUTOMATED GOOGLE SHEETS IMPORT
            // ==========================================
            0 -> {
              Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF0FDF4),
                border = BorderStroke(1.dp, Color(0xFFBBF7D0))
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "AUTOMATED GOOGLE SHEETS IMPORT",
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color(0xFF166534)
                    )
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "Tracks location names, accommodation & store Google Map links, store codes, worker counts (active, total, capacity), worker phones 1 & 2, and house owner details with IBAN.",
                    fontSize = 11.sp,
                    color = Color.Black,
                    lineHeight = 15.sp
                  )
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              Text(text = "Google Sheets URL", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate700)
              Spacer(modifier = Modifier.height(4.dp))

              OutlinedTextField(
                value = sheetUrl,
                onValueChange = { sheetUrl = it },
                placeholder = { Text("https://docs.google.com/spreadsheets/d/...") },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = ZawitcoBlue,
                  unfocusedBorderColor = Slate200,
                  focusedTextColor = Color.Black,
                  unfocusedTextColor = Color.Black
                )
              )

              Spacer(modifier = Modifier.height(8.dp))

              // Automated Sync & Quick Action Buttons
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Button(
                  onClick = {
                    coroutineScope.launch {
                      isFetchingSheet = true
                      sheetFetchMessage = "Fetching sheet data from Google Server..."
                      val fetchResult = BulkDataParser.fetchGoogleSheetCsv(sheetUrl)
                      fetchResult.fold(
                        onSuccess = { content ->
                          rawCsvText = content
                          sheetFetchMessage = "Sheet data retrieved successfully! Ready to import."
                          isFetchingSheet = false
                        },
                        onFailure = { err ->
                          sheetFetchMessage = err.message
                          isFetchingSheet = false
                        }
                      )
                    }
                  },
                  enabled = !isFetchingSheet && sheetUrl.isNotBlank(),
                  colors = ButtonDefaults.buttonColors(containerColor = ZawitcoBlue, contentColor = Color.White),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.weight(1.3f).height(42.dp)
                ) {
                  if (isFetchingSheet) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Fetching...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                  } else {
                    Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Fetch Automated", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                  }
                }

                OutlinedButton(
                  onClick = {
                    try {
                      val intent = Intent(Intent.ACTION_VIEW, Uri.parse(sheetUrl))
                      context.startActivity(intent)
                    } catch (e: Exception) {
                      Toast.makeText(context, "Could not open browser", Toast.LENGTH_SHORT).show()
                    }
                  },
                  shape = RoundedCornerShape(8.dp),
                  border = BorderStroke(1.dp, Slate200),
                  modifier = Modifier.weight(1f).height(42.dp)
                ) {
                  Icon(Icons.Default.OpenInBrowser, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Open Sheet", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }

                Button(
                  onClick = {
                    rawCsvText = BulkDataParser.getSampleGoogleSheetsData()
                    sheetFetchMessage = "Loaded sample dataset from Google Sheet structure!"
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = ZawitcoDarkOrange, contentColor = Color.White),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.weight(1f).height(42.dp)
                ) {
                  Text("Demo Data", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
              }

              if (sheetFetchMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = if (sheetFetchMessage?.contains("successfully", ignoreCase = true) == true) Color(0xFFD1FAE5) else Color(0xFFFEF3C7),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Text(
                    text = sheetFetchMessage!!,
                    fontSize = 11.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(8.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              Text(text = "Sheet Data / Paste Rows", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate700)
              Spacer(modifier = Modifier.height(4.dp))

              OutlinedTextField(
                value = rawCsvText,
                onValueChange = { rawCsvText = it },
                placeholder = {
                  Text(
                    text = "Location Name, Villa, Floor, Room, Total Worker, Active Workers, Capacity, Accommodation Location, Store Location, Store Code, Store Name, Worker Phone 1, Worker Phone 2, Owner Name, Owner Phone, Bank Name, IBAN...\n\n(Paste cells or rows copied from Google Sheets)",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Slate400
                  )
                },
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = ZawitcoBlue,
                  unfocusedBorderColor = Slate200,
                  focusedTextColor = Color.Black,
                  unfocusedTextColor = Color.Black,
                  focusedContainerColor = Color(0xFFFAFAFA),
                  unfocusedContainerColor = Color(0xFFFAFAFA)
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                  fontFamily = FontFamily.Monospace,
                  fontSize = 11.sp,
                  color = Color.Black
                ),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(160.dp)
                  .testTag("google_sheets_raw_input")
              )
            }

            // ==========================================
            // TAB 1: SINGLE ACCOMMODATION DATA IMPORT
            // ==========================================
            1 -> {
              Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFEFF6FF),
                border = BorderStroke(1.dp, Color(0xFFBFDBFE))
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = ZawitcoBlue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "SINGLE ACCOMMODATION DATA IMPORT",
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = ZawitcoBlue
                    )
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "Paste a single row copied from Google Sheets or key-value text from WhatsApp/SMS. All fields will be parsed and mapped instantly.",
                    fontSize = 11.sp,
                    color = Color.Black
                  )
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(
                  onClick = {
                    singleRawInput = BulkDataParser.getSampleSingleAccommodationData()
                    parsedSingleItem = BulkDataParser.parseSingleAccommodation(singleRawInput)
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = Slate100, contentColor = Color.Black),
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Text("Paste Sample Single Row", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
              }

              Spacer(modifier = Modifier.height(6.dp))

              OutlinedTextField(
                value = singleRawInput,
                onValueChange = {
                  singleRawInput = it
                  parsedSingleItem = BulkDataParser.parseSingleAccommodation(it)
                },
                placeholder = {
                  Text(
                    text = "Paste single row or text block, e.g.:\nLocation: Al Sahafa\nVilla: 19A\nTotal Workers: 10\nActive Workers: 9\nAccommodation Location: https://maps.google.com/...\nStore Location: https://maps.google.com/...\nStore Code: ST-105\nWorker Phone 1: +96650...\nWorker Phone 2: +96655...\nOwner Name: Bandar\nIBAN: SA77...",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Slate400
                  )
                },
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = ZawitcoBlue,
                  unfocusedBorderColor = Slate200,
                  focusedTextColor = Color.Black,
                  unfocusedTextColor = Color.Black,
                  focusedContainerColor = Color(0xFFFAFAFA),
                  unfocusedContainerColor = Color(0xFFFAFAFA)
                ),
                textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Color.Black),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(160.dp)
                  .testTag("single_import_input")
              )

              // Live parsed preview of Single Accommodation
              parsedSingleItem?.let { item ->
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = Color(0xFFFFF7ED),
                  border = BorderStroke(1.dp, Color(0xFFFFEDD5)),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(10.dp)) {
                    Text(text = "DETECTED SPECS PREVIEW", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = ZawitcoDarkOrange)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "• Location: ${item.areaName}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ZawitcoBlue)
                    Text(text = "• Villa #${item.villaNumber} • Floor: ${item.floorNumber} • Room: ${item.roomNumber}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ZawitcoDarkOrange)
                    Text(text = "• Workers: ${item.activeWorkers} Active / ${item.totalWorkers} Total (Cap: ${item.totalCapacity})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ZawitcoDarkOrange)
                    if (item.storeCode.isNotBlank()) Text(text = "• Store: ${item.storeName} (${item.storeCode})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ZawitcoDarkOrange)
                    if (item.workerPhone.isNotBlank() || item.workerPhone2.isNotBlank()) Text(text = "• Phones: ${item.workerPhone}  ${item.workerPhone2}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ZawitcoDarkOrange)
                    if (item.ownerName.isNotBlank() || item.ownerIban.isNotBlank()) Text(text = "• Owner: ${item.ownerName} • IBAN: ${item.ownerIban}", fontSize = 11.sp, color = Color.Black)
                  }
                }
              }

              if (singleSuccessMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(text = singleSuccessMessage!!, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              Button(
                onClick = {
                  val item = parsedSingleItem
                  if (item != null) {
                    onImportSingle?.invoke(item) {
                      singleSuccessMessage = "Accommodation in '${item.areaName}' imported successfully!"
                      singleRawInput = ""
                      parsedSingleItem = null
                    }
                  } else {
                    Toast.makeText(context, "Please enter accommodation details to import.", Toast.LENGTH_SHORT).show()
                  }
                },
                enabled = parsedSingleItem != null,
                colors = ButtonDefaults.buttonColors(containerColor = ZawitcoBlue, contentColor = Color.White),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(46.dp).testTag("import_single_button")
              ) {
                Icon(Icons.Outlined.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Import This Accommodation", fontWeight = FontWeight.Bold)
              }
            }

            // ==========================================
            // TAB 2: BULK CSV FILE UPLOAD
            // ==========================================
            2 -> {
              Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Slate100,
                border = BorderStroke(1.dp, Slate200)
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FileUpload, contentDescription = null, tint = ZawitcoBlue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "BULK CSV / TSV FILE UPLOAD", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                  }
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "Upload a CSV file directly from your device storage or paste multiple lines of data.",
                    fontSize = 11.sp,
                    color = Slate600
                  )
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                  onClick = { filePickerLauncher.launch("*/*") },
                  colors = ButtonDefaults.buttonColors(containerColor = ZawitcoBlue, contentColor = Color.White),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.weight(1f).height(42.dp)
                ) {
                  Icon(Icons.Outlined.FileOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Select File", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                  onClick = { rawCsvText = BulkDataParser.getSampleGoogleSheetsData() },
                  colors = ButtonDefaults.buttonColors(containerColor = Slate200, contentColor = Color.Black),
                  shape = RoundedCornerShape(8.dp),
                  modifier = Modifier.weight(1f).height(42.dp)
                ) {
                  Text("Load Sample CSV", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              OutlinedTextField(
                value = rawCsvText,
                onValueChange = { rawCsvText = it },
                placeholder = {
                  Text(
                    text = "Paste CSV/TSV contents here...",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Slate400
                  )
                },
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = ZawitcoBlue,
                  unfocusedBorderColor = Slate200,
                  focusedTextColor = Color.Black,
                  unfocusedTextColor = Color.Black,
                  focusedContainerColor = Color(0xFFFAFAFA),
                  unfocusedContainerColor = Color(0xFFFAFAFA)
                ),
                textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Color.Black),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(180.dp)
                  .testTag("bulk_csv_input")
              )
            }
          }

          // Parsing Results Banner if imported
          parseResult?.let { res ->
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              color = if (res.errors.isEmpty()) Color(0xFFD1FAE5) else Color(0xFFFEF3C7)
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (res.errors.isEmpty()) Color(0xFF047857) else Color(0xFFB45309),
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Successfully imported ${res.accommodations.size} housing accommodations!",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (res.errors.isEmpty()) Color(0xFF065F46) else Color(0xFF92400E)
                  )
                }
                if (res.errors.isNotEmpty()) {
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "Notes: ${res.errors.take(2).joinToString("; ")}",
                    fontSize = 11.sp,
                    color = Color(0xFFB45309)
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bottom Action Buttons for Tabs 0 & 2
        if (selectedTab != 1) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Button(
              onClick = onDismiss,
              colors = ButtonDefaults.buttonColors(containerColor = Slate100, contentColor = Color.Black),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.weight(1f).height(48.dp)
            ) {
              Text("Close", fontWeight = FontWeight.Bold)
            }

            Button(
              onClick = {
                if (rawCsvText.isNotBlank()) {
                  isProcessing = true
                  onImportData(rawCsvText) { result ->
                    parseResult = result
                    isProcessing = false
                    Toast.makeText(context, "Imported ${result.accommodations.size} accommodations!", Toast.LENGTH_SHORT).show()
                  }
                }
              },
              enabled = rawCsvText.isNotBlank() && !isProcessing,
              colors = ButtonDefaults.buttonColors(containerColor = ZawitcoBlue, contentColor = Color.White),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .weight(2f)
                .height(48.dp)
                .testTag("process_bulk_data_button")
            ) {
              Icon(Icons.Default.CloudDownload, contentDescription = null, tint = Color.White)
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (isProcessing) "Importing Data..." else "Import All Accommodations",
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }
        }
      }
    }
  }
}
