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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.os.Build
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import com.example.reminder.DailyReminderScheduler
import com.example.data.local.AppDatabase
import com.example.data.repository.AccommodationRepository
import com.example.ui.components.AccommodationCard
import com.example.ui.components.AccommodationDetailsSheet
import com.example.ui.components.AddEditAccommodationDialog
import com.example.ui.components.AdminLoginDialog
import com.example.ui.components.BulkDataUploadDialog
import com.example.ui.components.CloudBackupDialog
import com.example.ui.components.DashboardAndProjectsView
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
      stationDao = db.stationDao(),
      auditDao = db.auditLogDao()
    )
    AccommodationViewModel.provideFactory(repository)
  }

  private fun rememberCoroutineScopeOrGlobal() = kotlinx.coroutines.CoroutineScope(
    kotlinx.coroutines.Dispatchers.IO + kotlinx.coroutines.SupervisorJob()
  )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    // Schedule background daily alarm manager service (triggers daily notification reminder)
    DailyReminderScheduler.scheduleDailyReminder(this)
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

  // Notification permission launcher for Android 13+ (API 33+)
  val notificationPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      DailyReminderScheduler.scheduleDailyReminder(context)
    }
  }

  LaunchedEffect(Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      val hasPermission = ContextCompat.checkSelfPermission(
        context,
        android.Manifest.permission.POST_NOTIFICATIONS
      ) == PackageManager.PERMISSION_GRANTED
      if (!hasPermission) {
        notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
      }
    }
  }

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

  val projectProgressList by viewModel.projectProgressList.collectAsStateWithLifecycle()
  val allAuditLogs by viewModel.allAuditLogs.collectAsStateWithLifecycle()

  var isSearchActive by remember { mutableStateOf(false) }

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
          isSearchActive = isSearchActive,
          searchQuery = searchQuery,
          onToggleSearch = {
            isSearchActive = !isSearchActive
            if (!isSearchActive) {
              viewModel.onSearchQueryChange("")
            }
          },
          onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
          onAdminClick = { viewModel.openAdminDialog() },
          onWhatsAppClick = { viewModel.openWhatsAppDialog() },
          onExportExcelClick = { viewModel.openExcelExportDialog() },
          onBulkUploadClick = { viewModel.openBulkUploadDialog() },
          onCloudBackupClick = { viewModel.openCloudBackupDialog(context) },
          onUserAccountsClick = { viewModel.openUserManagementDialog() },
          onAddNewClick = { viewModel.openAddDialog() },
          onLogout = { viewModel.logout() }
        )

        // Main Navigation Switcher: [Housing Units] [Projects & Products Dashboard] [🔒 Admin Audit Log (if Admin)]
        TabRow(
          selectedTabIndex = when (selectedMainTab) {
            MainViewTab.ACCOMMODATIONS -> 0
            MainViewTab.PROJECTS_DASHBOARD -> 1
            MainViewTab.ADMIN_AUDIT_LOGS -> 2
          },
          containerColor = Color.White,
          contentColor = ZawitcoBlue,
          indicator = { tabPositions ->
            val index = when (selectedMainTab) {
              MainViewTab.ACCOMMODATIONS -> 0
              MainViewTab.PROJECTS_DASHBOARD -> 1
              MainViewTab.ADMIN_AUDIT_LOGS -> 2
            }
            if (index < tabPositions.size) {
              TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                color = ZawitcoBlue,
                height = 3.dp
              )
            }
          }
        ) {
          Tab(
            selected = selectedMainTab == MainViewTab.ACCOMMODATIONS,
            onClick = { viewModel.setMainTab(MainViewTab.ACCOMMODATIONS) },
            text = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Housing Units", fontWeight = FontWeight.Bold, fontSize = 12.sp)
              }
            },
            modifier = Modifier.testTag("nav_tab_housing_units")
          )
          Tab(
            selected = selectedMainTab == MainViewTab.PROJECTS_DASHBOARD,
            onClick = { viewModel.setMainTab(MainViewTab.PROJECTS_DASHBOARD) },
            text = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.TrendingUp, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Projects & Dashboard", fontWeight = FontWeight.Bold, fontSize = 12.sp)
              }
            },
            modifier = Modifier.testTag("nav_tab_projects_dashboard")
          )
          if (isAdmin) {
            Tab(
              selected = selectedMainTab == MainViewTab.ADMIN_AUDIT_LOGS,
              onClick = { viewModel.setMainTab(MainViewTab.ADMIN_AUDIT_LOGS) },
              text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color(0xFFDC2626))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("🔒 Audit Logs", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFDC2626))
                }
              },
              modifier = Modifier.testTag("nav_tab_admin_audit_logs")
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
      // HOUSING UNITS DIRECTORY VIEW (Station & Analysis option closed per user request)
      Column(modifier = Modifier.fillMaxSize()) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
          // Station Filter Pill if active
          if (!filterStationName.isNullOrBlank()) {
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
            Spacer(modifier = Modifier.height(6.dp))
          }

          // Viewer Read-Only Notice Banner
          if (!isAdmin) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = Color(0xFFF8FAFC),
              border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Lock,
                  contentDescription = "Read Only Access",
                  tint = Slate600,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Read-Only Mode: You are signed in as Viewer. Adding, modifying, or deleting records requires Administrator access.",
                  fontSize = 10.5.sp,
                  color = Slate600,
                  lineHeight = 14.sp
                )
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
                  if (isAdmin) {
                    Button(
                      onClick = { viewModel.openBulkUploadDialog() },
                      colors = ButtonDefaults.buttonColors(containerColor = ZawitcoBlue),
                      shape = RoundedCornerShape(12.dp)
                    ) {
                      Text("Bulk Data Upload", fontWeight = FontWeight.Bold, color = Color.White)
                    }
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

  // 3. Bulk Data File Upload & Single Accommodation Import Dialog
  if (showBulkUploadDialog) {
    BulkDataUploadDialog(
      onDismiss = { viewModel.closeBulkUploadDialog() },
      onImportData = { rawText, onComplete ->
        viewModel.importBulkData(rawText, onComplete)
      },
      onImportSingle = { acc, onComplete ->
        viewModel.importSingleAccommodation(acc, onComplete)
      },
      onAnalysisReady = {
        viewModel.closeBulkUploadDialog()
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
      onSave = { id, area, villa, floor, room, totalWorkers, totalCap, activeWorkers, accLoc, storeLoc, storeCode, storeName, wPhone1, wPhone2, oName, oPhone, oBank, oIban, mapsUrl, lat, lng, img, billingImg, doorImg, notes, whatsapp, stName ->
        viewModel.saveAccommodation(
          id = id,
          areaName = area,
          villaNumber = villa,
          floorNumber = floor,
          roomNumber = room,
          totalWorkers = totalWorkers,
          totalCapacity = totalCap,
          activeWorkers = activeWorkers,
          accommodationLocationUrl = accLoc,
          storeLocationUrl = storeLoc,
          storeCode = storeCode,
          storeName = storeName,
          workerPhone = wPhone1,
          workerPhone2 = wPhone2,
          ownerName = oName,
          ownerPhone = oPhone,
          ownerBankName = oBank,
          ownerIban = oIban,
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
