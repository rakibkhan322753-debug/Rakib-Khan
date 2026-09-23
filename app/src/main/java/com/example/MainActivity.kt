package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.AppDatabase
import com.example.data.repository.AccommodationRepository
import com.example.ui.components.AccommodationCard
import com.example.ui.components.AccommodationDetailsSheet
import com.example.ui.components.AddEditAccommodationDialog
import com.example.ui.components.AdminLoginDialog
import com.example.ui.components.BulkDataUploadDialog
import com.example.ui.components.CloudBackupDialog
import com.example.ui.components.ExcelExportDialog
import com.example.ui.components.HeaderBar
import com.example.ui.components.LocationAnalyticsView
import com.example.ui.components.NotificationAlertBanner
import com.example.ui.components.ReportIssueDialog
import com.example.ui.components.RequirementsDialog
import com.example.ui.components.StationManagementDialog
import com.example.ui.components.UserAccountManagementDialog
import com.example.ui.components.WhatsAppGroupDialog
import com.example.ui.components.ZawitcoCompanyLogo
import com.example.ui.screens.LoginScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800
import com.example.ui.theme.ZawitcoBlue
import com.example.ui.theme.ZawitcoLightBlue
import com.example.ui.theme.ZawitcoOrange
import com.example.ui.viewmodel.AccommodationViewModel
import com.example.ui.viewmodel.MainViewTab
import com.example.ui.viewmodel.UserRole

class MainActivity : ComponentActivity() {

  private val viewModel: AccommodationViewModel by viewModels {
    val db = AppDatabase.getDatabase(applicationContext, rememberCoroutineScopeOrGlobal())
    val repository = AccommodationRepository(
      dao = db.accommodationDao(),
      reqDao = db.requirementDao(),
      reportDao = db.reportDao(),
      userDao = db.userAccountDao(),
      stationDao = db.stationDao()
    )
    AccommodationViewModel.provideFactory(repository)
  }

  private fun rememberCoroutineScopeOrGlobal() = kotlinx.coroutines.CoroutineScope(
    kotlinx.coroutines.Dispatchers.IO + kotlinx.coroutines.SupervisorJob()
  )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        ZawitcoAccommodationApp(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun ZawitcoAccommodationApp(
  viewModel: AccommodationViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  val currentUserRole by viewModel.currentUserRole.collectAsStateWithLifecycle()
  val currentLoggedUser by viewModel.currentLoggedUser.collectAsStateWithLifecycle()
  val selectedMainTab by viewModel.selectedMainTab.collectAsStateWithLifecycle()

  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val filterStationName by viewModel.filterStationName.collectAsStateWithLifecycle()
  val accommodations by viewModel.accommodations.collectAsStateWithLifecycle()
  val allStations by viewModel.allStations.collectAsStateWithLifecycle()
  val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
  val locationAnalysisReport by viewModel.locationAnalysisReport.collectAsStateWithLifecycle()

  val selectedAccommodation by viewModel.selectedAccommodation.collectAsStateWithLifecycle()
  val isAddEditDialogOpen by viewModel.isAddEditDialogOpen.collectAsStateWithLifecycle()
  val editingAccommodation by viewModel.editingAccommodation.collectAsStateWithLifecycle()

  val allRequirements by viewModel.allRequirements.collectAsStateWithLifecycle()
  val allReports by viewModel.allReports.collectAsStateWithLifecycle()
  val pendingRequirementsCount by viewModel.pendingRequirementsCount.collectAsStateWithLifecycle()
  val pendingReportsCount by viewModel.pendingReportsCount.collectAsStateWithLifecycle()

  val selectedAccForReq by viewModel.selectedAccommodationForRequirements.collectAsStateWithLifecycle()
  val currentRequirements by viewModel.currentAccommodationRequirements.collectAsStateWithLifecycle()

  val selectedAccForRep by viewModel.selectedAccommodationForReports.collectAsStateWithLifecycle()
  val currentReports by viewModel.currentAccommodationReports.collectAsStateWithLifecycle()

  val isAdmin by viewModel.isAdmin.collectAsStateWithLifecycle()
  val adminPin by viewModel.adminPin.collectAsStateWithLifecycle()
  val showAdminDialog by viewModel.showAdminDialog.collectAsStateWithLifecycle()

  val showUserManagementDialog by viewModel.showUserManagementDialog.collectAsStateWithLifecycle()
  val showStationManagementDialog by viewModel.showStationManagementDialog.collectAsStateWithLifecycle()
  val showBulkUploadDialog by viewModel.showBulkUploadDialog.collectAsStateWithLifecycle()
  val showExcelExportDialog by viewModel.showExcelExportDialog.collectAsStateWithLifecycle()

  val showCloudBackupDialog by viewModel.showCloudBackupDialog.collectAsStateWithLifecycle()
  val isCloudSyncing by viewModel.isCloudSyncing.collectAsStateWithLifecycle()
  val cloudSyncStatusMessage by viewModel.cloudSyncStatusMessage.collectAsStateWithLifecycle()
  val latestCloudBackupInfo by viewModel.latestCloudBackupInfo.collectAsStateWithLifecycle()

  val globalWhatsAppUrl by viewModel.globalWhatsAppUrl.collectAsStateWithLifecycle()
  val showWhatsAppDialog by viewModel.showWhatsAppDialog.collectAsStateWithLifecycle()

  // 1st screen on app launch: Full Sign-In Interface
  if (currentUserRole == UserRole.LOGGED_OUT) {
    LoginScreen(
      viewModel = viewModel,
      onLoginSuccess = { /* Automatically navigates to home screen */ },
      modifier = modifier
    )
    return
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = MaterialTheme.colorScheme.background,
    topBar = {
      Column(modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)) {
        HeaderBar(
          isAdmin = isAdmin,
          currentUser = currentLoggedUser,
          onAdminClick = { viewModel.openAdminDialog() },
          onWhatsAppClick = { viewModel.openWhatsAppDialog() },
          onExportExcelClick = { viewModel.openExcelExportDialog() },
          onBulkUploadClick = { viewModel.openBulkUploadDialog() },
          onCloudBackupClick = { viewModel.openCloudBackupDialog(context) },
          onUserAccountsClick = { viewModel.openUserManagementDialog() },
          onAddNewClick = { viewModel.openAddDialog() },
          onLogout = { viewModel.logout() }
        )

        // Main Navigation Tabs: Housing Units vs Station & Location Analytics
        Surface(
          modifier = Modifier.fillMaxWidth(),
          color = Color.White,
          shadowElevation = 2.dp
        ) {
          TabRow(
            selectedTabIndex = if (selectedMainTab == MainViewTab.ACCOMMODATIONS) 0 else 1,
            containerColor = Color.White,
            contentColor = ZawitcoBlue,
            indicator = { tabPositions ->
              val activeIndex = if (selectedMainTab == MainViewTab.ACCOMMODATIONS) 0 else 1
              TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[activeIndex]),
                color = if (activeIndex == 0) ZawitcoBlue else ZawitcoOrange
              )
            }
          ) {
            Tab(
              selected = selectedMainTab == MainViewTab.ACCOMMODATIONS,
              onClick = { viewModel.setMainTab(MainViewTab.ACCOMMODATIONS) },
              modifier = Modifier.testTag("tab_accommodations"),
              text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    tint = if (selectedMainTab == MainViewTab.ACCOMMODATIONS) ZawitcoBlue else Color.Black,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Housing Units (${accommodations.size})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedMainTab == MainViewTab.ACCOMMODATIONS) ZawitcoBlue else Color.Black
                  )
                }
              }
            )

            Tab(
              selected = selectedMainTab == MainViewTab.STATIONS_ANALYTICS,
              onClick = { viewModel.setMainTab(MainViewTab.STATIONS_ANALYTICS) },
              modifier = Modifier.testTag("tab_stations_analytics"),
              text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Hub,
                    contentDescription = null,
                    tint = if (selectedMainTab == MainViewTab.STATIONS_ANALYTICS) ZawitcoOrange else Color.Black,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Stations & Analytics",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedMainTab == MainViewTab.STATIONS_ANALYTICS) ZawitcoOrange else Color.Black
                  )
                }
              }
            )
          }
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      if (selectedMainTab == MainViewTab.STATIONS_ANALYTICS) {
        // AUTOMATED GEOSPATIAL & STATION ANALYTICS VIEW
        LocationAnalyticsView(
          report = locationAnalysisReport,
          activeStationFilter = filterStationName,
          onFilterByStation = { stName ->
            viewModel.setFilterStationName(stName)
            if (stName != null) {
              viewModel.setMainTab(MainViewTab.ACCOMMODATIONS)
            }
          },
          onOpenBulkUpload = { viewModel.openBulkUploadDialog() },
          onOpenStationManager = { viewModel.openStationManagementDialog() },
          isAdmin = isAdmin
        )
      } else {
        // HOUSING UNITS DIRECTORY VIEW
        Column(modifier = Modifier.fillMaxSize()) {
          // Search & Filter Bar
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 8.dp)
          ) {
            OutlinedTextField(
              value = searchQuery,
              onValueChange = { viewModel.onSearchQueryChange(it) },
              placeholder = {
                Text(
                  text = "Search area, villa, floor, room, or phone...",
                  fontSize = 14.sp,
                  color = Slate400
                )
              },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Default.Search,
                  contentDescription = "Search icon",
                  tint = ZawitcoBlue
                )
              },
              trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                  IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                    Icon(
                      imageVector = Icons.Default.Clear,
                      contentDescription = "Clear search",
                      tint = Color.Black
                    )
                  }
                }
              },
              singleLine = true,
              shape = RoundedCornerShape(16.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = ZawitcoBlue,
                unfocusedBorderColor = Slate200,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
              ),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("search_input")
            )

            // Station Filter Pill if active
            if (!filterStationName.isNullOrBlank()) {
              Spacer(modifier = Modifier.height(6.dp))
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFEFF6FF),
                border = androidx.compose.foundation.BorderStroke(1.dp, ZawitcoBlue)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "Filtering: $filterStationName",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  IconButton(
                    onClick = { viewModel.setFilterStationName(null) },
                    modifier = Modifier.size(16.dp)
                  ) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear filter", tint = Color.Black, modifier = Modifier.size(12.dp))
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // NOTIFICATIONS ALERT BANNER
            NotificationAlertBanner(
              activeRequirementsCount = pendingRequirementsCount,
              activeReportsCount = pendingReportsCount,
              onOpenRequirements = {
                accommodations.firstOrNull()?.let { firstAcc ->
                  viewModel.openRequirementsDialog(firstAcc)
                }
              },
              onOpenReports = {
                accommodations.firstOrNull()?.let { firstAcc ->
                  viewModel.openReportsDialog(firstAcc)
                }
              }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Action Row: Export to Excel Anytime + WhatsApp Community
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Export to Excel Button
              Surface(
                onClick = { viewModel.openExcelExportDialog() },
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFDCFCE7),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC)),
                modifier = Modifier.testTag("quick_export_excel_banner_button")
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.TableChart,
                    contentDescription = null,
                    tint = Color(0xFF15803D),
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(5.dp))
                  Text(
                    text = "Export Excel (Req & Reports)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                  )
                }
              }

              // WhatsApp Community Group link
              Surface(
                onClick = { viewModel.openWhatsAppDialog() },
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFE8F8F0),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFB7EBCB)),
                modifier = Modifier.testTag("quick_whatsapp_group_button")
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = "WhatsApp Group",
                    tint = Color(0xFF25D366),
                    modifier = Modifier.size(15.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "Zawitco Group",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF128C7E)
                  )
                }
              }
            }
          }

          // Accommodation List / Grid
          if (accommodations.isEmpty()) {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
              contentAlignment = Alignment.Center
            ) {
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.testTag("empty_state_view")
              ) {
                ZawitcoCompanyLogo(height = 48.dp, showSubtext = false)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                  text = if (searchQuery.isNotBlank() || filterStationName != null) "No Matching Housing Units" else "No Accommodations Loaded",
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.Black
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = if (searchQuery.isNotBlank() || filterStationName != null) {
                    "Try clearing search or filters to see all available units."
                  } else {
                    "Upload a bulk data file or add new housing units to view records here."
                  },
                  fontSize = 14.sp,
                  color = Color.Black,
                  modifier = Modifier.padding(horizontal = 24.dp),
                  lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(18.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                  Button(
                    onClick = { viewModel.openBulkUploadDialog() },
                    colors = ButtonDefaults.buttonColors(containerColor = ZawitcoBlue),
                    shape = RoundedCornerShape(12.dp)
                  ) {
                    Text("Bulk Data Upload", fontWeight = FontWeight.Bold, color = Color.White)
                  }
                  if (filterStationName != null) {
                    OutlinedButton(
                      onClick = { viewModel.setFilterStationName(null) },
                      shape = RoundedCornerShape(12.dp)
                    ) {
                      Text("Clear Filter", fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                  }
                }
              }
            }
          } else {
            LazyVerticalGrid(
              columns = GridCells.Adaptive(minSize = 330.dp),
              contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp),
              horizontalArrangement = Arrangement.spacedBy(16.dp),
              verticalArrangement = Arrangement.spacedBy(16.dp),
              modifier = Modifier
                .fillMaxSize()
                .testTag("accommodations_grid")
            ) {
              items(
                items = accommodations,
                key = { it.id }
              ) { item ->
                val itemReqCount = remember(allRequirements, item.id) {
                  allRequirements.count { it.accommodationId == item.id && it.status != "Fulfilled" }
                }
                val itemRepCount = remember(allReports, item.id) {
                  allReports.count { it.accommodationId == item.id && it.status != "Resolved" }
                }

                AccommodationCard(
                  accommodation = item,
                  isAdmin = isAdmin,
                  pendingRequirementsCount = itemReqCount,
                  pendingReportsCount = itemRepCount,
                  onViewDetails = { viewModel.selectAccommodation(item) },
                  onEdit = { viewModel.openEditDialog(item) },
                  onDelete = { viewModel.deleteAccommodation(item) }
                )
              }
            }
          }
        }
      }
    }
  }

  // ==========================================
  // DIALOGS & OVERLAYS
  // ==========================================

  // 1. User Accounts Management Dialog (Admin Only)
  if (showUserManagementDialog) {
    UserAccountManagementDialog(
      users = allUsers,
      stations = allStations.map { it.name },
      onDismiss = { viewModel.closeUserManagementDialog() },
      onCreateUser = { u, p, name, role, stn, phone, onResult ->
        viewModel.createUserAccount(u, p, name, role, stn, phone, onResult)
      },
      onDeleteUser = { user -> viewModel.deleteUserAccount(user) }
    )
  }

  // 2. Station Management Dialog (Admin Only)
  if (showStationManagementDialog) {
    StationManagementDialog(
      stations = allStations,
      onDismiss = { viewModel.closeStationManagementDialog() },
      onSaveStation = { id, name, code, area, city, lat, lng, addr, sup, phone, notes ->
        viewModel.saveStation(id, name, code, area, city, lat, lng, addr, sup, phone, notes)
      },
      onDeleteStation = { station -> viewModel.deleteStation(station) }
    )
  }

  // 3. Bulk Data File Upload Dialog with Auto-Analysis
  if (showBulkUploadDialog) {
    BulkDataUploadDialog(
      onDismiss = { viewModel.closeBulkUploadDialog() },
      onImportData = { rawText, onComplete ->
        viewModel.importBulkData(rawText, onComplete)
      },
      onAnalysisReady = {
        viewModel.closeBulkUploadDialog()
        viewModel.setMainTab(MainViewTab.STATIONS_ANALYTICS)
      }
    )
  }

  // 4. Excel Export Dialog
  if (showExcelExportDialog) {
    ExcelExportDialog(
      requirements = allRequirements,
      reports = allReports,
      accommodations = accommodations,
      onDismiss = { viewModel.closeExcelExportDialog() },
      onExportRequirements = { viewModel.exportRequirementsToExcel(context) },
      onExportReports = { viewModel.exportReportsToExcel(context) },
      onExportAll = { viewModel.exportAllDataToExcel(context) }
    )
  }

  // 5. Admin Login Dialog
  if (showAdminDialog) {
    AdminLoginDialog(
      isAdmin = isAdmin,
      currentPin = adminPin,
      onLogin = { pin -> viewModel.loginAsAdmin(pin) },
      onLogout = { viewModel.logoutAdmin() },
      onChangePin = { newPin -> viewModel.updateAdminPin(newPin) },
      onDismiss = { viewModel.closeAdminDialog() }
    )
  }

  // 6. WhatsApp Group Dialog
  if (showWhatsAppDialog) {
    WhatsAppGroupDialog(
      currentGroupUrl = globalWhatsAppUrl,
      isAdmin = isAdmin,
      onUpdateGroupUrl = { newUrl -> viewModel.updateGlobalWhatsAppUrl(newUrl) },
      onDismiss = { viewModel.closeWhatsAppDialog() }
    )
  }

  // 7. Add / Edit Accommodation Dialog (Admin Only)
  if (isAddEditDialogOpen) {
    AddEditAccommodationDialog(
      initialItem = editingAccommodation,
      onDismiss = { viewModel.closeAddEditDialog() },
      onSave = { id, area, villa, floor, room, worker, owner, mapsUrl, lat, lng, img, billingImg, doorImg, notes, whatsapp, stName ->
        viewModel.saveAccommodation(
          id = id,
          areaName = area,
          villaNumber = villa,
          floorNumber = floor,
          roomNumber = room,
          workerPhone = worker,
          ownerPhone = owner,
          googleMapsUrl = mapsUrl,
          latitude = lat,
          longitude = lng,
          buildingImageUri = img,
          billingPictureUri = billingImg,
          doorPictureUri = doorImg,
          notes = notes,
          whatsappGroupUrl = whatsapp,
          stationName = stName
        )
      },
      onSaveImageToStorage = { uri ->
        viewModel.saveImageUriToInternalStorage(context, uri)
      }
    )
  }

  // 8. Details Sheet Dialog
  selectedAccommodation?.let { item ->
    val itemReqCount = allRequirements.count { it.accommodationId == item.id && it.status != "Fulfilled" }
    val itemRepCount = allReports.count { it.accommodationId == item.id && it.status != "Resolved" }

    AccommodationDetailsSheet(
      accommodation = item,
      requirementsCount = itemReqCount,
      reportsCount = itemRepCount,
      isAdmin = isAdmin,
      onEdit = { viewModel.openEditDialog(item) },
      onDelete = { viewModel.deleteAccommodation(item) },
      onOpenRequirements = { viewModel.openRequirementsDialog(item) },
      onOpenReports = { viewModel.openReportsDialog(item) },
      onDismiss = { viewModel.clearSelectedAccommodation() }
    )
  }

  // 9. Requirements Dialog
  selectedAccForReq?.let { acc ->
    RequirementsDialog(
      accommodation = acc,
      requirements = currentRequirements,
      isAdmin = isAdmin,
      onDismiss = { viewModel.closeRequirementsDialog() },
      onAddRequirement = { accId, item, qty, urgency, reqBy, notes ->
        viewModel.addRequirement(accId, item, qty, urgency, reqBy, notes)
      },
      onUpdateStatus = { req, status ->
        viewModel.updateRequirementStatus(req, status)
      },
      onDeleteRequirement = { req ->
        viewModel.deleteRequirement(req)
      }
    )
  }

  // 10. Issue / Report Dialog
  selectedAccForRep?.let { acc ->
    ReportIssueDialog(
      accommodation = acc,
      reports = currentReports,
      isAdmin = isAdmin,
      onDismiss = { viewModel.closeReportsDialog() },
      onAddReport = { accId, cat, title, desc, sev, repBy, phone ->
        viewModel.addReport(accId, cat, title, desc, sev, repBy, phone)
      },
      onUpdateStatus = { rep, status ->
        viewModel.updateReportStatus(rep, status)
      },
      onDeleteReport = { rep ->
        viewModel.deleteReport(rep)
      }
    )
  }

  // 11. Google Cloud / Firebase Cloud Server Backup & Storage Dialog
  if (showCloudBackupDialog) {
    CloudBackupDialog(
      localAccommodationsCount = accommodations.size,
      localRequirementsCount = allRequirements.size,
      localReportsCount = allReports.size,
      localStationsCount = allStations.size,
      localUsersCount = allUsers.size,
      latestBackupInfo = latestCloudBackupInfo,
      isSyncing = isCloudSyncing,
      statusMessage = cloudSyncStatusMessage,
      onBackupToCloud = { onResult ->
        viewModel.backupToCloudServer(context, onResult)
      },
      onRestoreFromCloud = { onResult ->
        viewModel.restoreFromCloudServer(context, onResult)
      },
      onExportJsonBackup = {
        kotlinx.coroutines.runBlocking { viewModel.exportJsonBackupString() }
      },
      onImportJsonBackup = { json, onResult ->
        viewModel.importJsonBackupString(json, onResult)
      },
      onDismiss = { viewModel.closeCloudBackupDialog() }
    )
  }
}
