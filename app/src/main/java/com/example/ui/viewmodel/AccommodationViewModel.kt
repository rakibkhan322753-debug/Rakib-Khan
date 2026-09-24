package com.example.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.cloud.CloudBackupSummary
import com.example.data.cloud.FirebaseCloudBackupService
import com.example.data.model.Accommodation
import com.example.data.model.AccommodationReport
import com.example.data.model.AccommodationRequirement
import com.example.data.model.AuditLog
import com.example.data.model.ProjectProgressSummary
import com.example.data.model.Station
import com.example.data.model.UserAccount
import com.example.data.repository.AccommodationRepository
import com.example.util.BulkDataParser
import com.example.util.BulkParseResult
import com.example.util.ExcelExporter
import com.example.util.LocationAnalysisHelper
import com.example.util.LocationAnalysisReport
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

enum class UserRole {
  LOGGED_OUT,
  VIEWER,
  ADMIN
}

enum class MainViewTab {
  ACCOMMODATIONS,
  PROJECTS_DASHBOARD,
  ADMIN_AUDIT_LOGS
}

sealed interface LoginResult {
  data class Success(val role: UserRole, val username: String, val fullName: String) : LoginResult
  data class Error(val message: String) : LoginResult
}

@OptIn(ExperimentalCoroutinesApi::class)
class AccommodationViewModel(
  private val repository: AccommodationRepository
) : ViewModel() {

  // Current session role & logged in user profile
  private val _currentUserRole = MutableStateFlow<UserRole>(UserRole.LOGGED_OUT)
  val currentUserRole: StateFlow<UserRole> = _currentUserRole.asStateFlow()

  private val _currentLoggedUser = MutableStateFlow<UserAccount?>(null)
  val currentLoggedUser: StateFlow<UserAccount?> = _currentLoggedUser.asStateFlow()

  val isAdmin: StateFlow<Boolean> = _currentUserRole
    .map { it == UserRole.ADMIN }
    .stateIn(viewModelScope, SharingStarted.Eagerly, false)

  val isViewer: StateFlow<Boolean> = _currentUserRole
    .map { it == UserRole.VIEWER }
    .stateIn(viewModelScope, SharingStarted.Eagerly, false)

  // Root Master Admin PIN (322753)
  private val _adminPin = MutableStateFlow("322753")
  val adminPin: StateFlow<String> = _adminPin.asStateFlow()

  val userDefaultPassword: String = "Zawitco"

  // Active Main View Tab
  private val _selectedMainTab = MutableStateFlow(MainViewTab.ACCOMMODATIONS)
  val selectedMainTab: StateFlow<MainViewTab> = _selectedMainTab.asStateFlow()

  fun setMainTab(tab: MainViewTab) {
    _selectedMainTab.value = tab
  }

  // Quick dialogs state
  private val _showAdminDialog = MutableStateFlow(false)
  val showAdminDialog: StateFlow<Boolean> = _showAdminDialog.asStateFlow()

  private val _showWhatsAppDialog = MutableStateFlow(false)
  val showWhatsAppDialog: StateFlow<Boolean> = _showWhatsAppDialog.asStateFlow()

  private val _showUserManagementDialog = MutableStateFlow(false)
  val showUserManagementDialog: StateFlow<Boolean> = _showUserManagementDialog.asStateFlow()

  private val _showStationManagementDialog = MutableStateFlow(false)
  val showStationManagementDialog: StateFlow<Boolean> = _showStationManagementDialog.asStateFlow()

  private val _showBulkUploadDialog = MutableStateFlow(false)
  val showBulkUploadDialog: StateFlow<Boolean> = _showBulkUploadDialog.asStateFlow()

  private val _showExcelExportDialog = MutableStateFlow(false)
  val showExcelExportDialog: StateFlow<Boolean> = _showExcelExportDialog.asStateFlow()

  // Google Cloud / Firebase Cloud Server Backup & Sync States
  private val _showCloudBackupDialog = MutableStateFlow(false)
  val showCloudBackupDialog: StateFlow<Boolean> = _showCloudBackupDialog.asStateFlow()

  private val _isCloudSyncing = MutableStateFlow(false)
  val isCloudSyncing: StateFlow<Boolean> = _isCloudSyncing.asStateFlow()

  private val _cloudSyncStatusMessage = MutableStateFlow<String?>(null)
  val cloudSyncStatusMessage: StateFlow<String?> = _cloudSyncStatusMessage.asStateFlow()

  private val _latestCloudBackupInfo = MutableStateFlow<CloudBackupSummary?>(null)
  val latestCloudBackupInfo: StateFlow<CloudBackupSummary?> = _latestCloudBackupInfo.asStateFlow()

  // Requirements dialog state
  private val _selectedAccommodationForRequirements = MutableStateFlow<Accommodation?>(null)
  val selectedAccommodationForRequirements: StateFlow<Accommodation?> =
    _selectedAccommodationForRequirements.asStateFlow()

  // Reports dialog state
  private val _selectedAccommodationForReports = MutableStateFlow<Accommodation?>(null)
  val selectedAccommodationForReports: StateFlow<Accommodation?> =
    _selectedAccommodationForReports.asStateFlow()

  // Global WhatsApp Group URL
  private val _globalWhatsAppUrl = MutableStateFlow("https://chat.whatsapp.com/ZawitcoStaffHousing")
  val globalWhatsAppUrl: StateFlow<String> = _globalWhatsAppUrl.asStateFlow()

  // Search filter query
  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  // Selected station filter for accommodations
  private val _filterStationName = MutableStateFlow<String?>(null)
  val filterStationName: StateFlow<String?> = _filterStationName.asStateFlow()

  fun setFilterStationName(name: String?) {
    _filterStationName.value = name
  }

  // Accommodations list
  val accommodations: StateFlow<List<Accommodation>> = combine(
    _searchQuery.flatMapLatest { query -> repository.searchAccommodations(query) },
    _filterStationName
  ) { list, stationFilter ->
    if (stationFilter.isNullOrBlank()) {
      list
    } else {
      list.filter { it.stationName.equals(stationFilter, ignoreCase = true) }
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Stations list
  val allStations: StateFlow<List<Station>> = repository.allStations
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Registered Users list (Admin only)
  val allUsers: StateFlow<List<UserAccount>> = repository.allUsers
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // All Requirements and Reports for active app notifications and export
  val allRequirements: StateFlow<List<AccommodationRequirement>> = repository.getAllRequirements()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allReports: StateFlow<List<AccommodationReport>> = repository.getAllReports()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Pending counts for Notification Alert
  val pendingRequirementsCount: StateFlow<Int> = allRequirements
    .map { list -> list.count { it.status != "Fulfilled" } }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  val pendingReportsCount: StateFlow<Int> = allReports
    .map { list -> list.count { it.status != "Resolved" } }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  // Automatic Location & Station Analysis Report
  val locationAnalysisReport: StateFlow<LocationAnalysisReport> = combine(
    repository.allAccommodations,
    allStations
  ) { accs, stations ->
    LocationAnalysisHelper.analyzeLocations(accs, stations)
  }.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    LocationAnalysisReport(0, 0, 0, 0, null, emptyMap(), emptyList(), emptyList(), emptyList())
  )

  // Requirements for currently open requirements dialog
  val currentAccommodationRequirements: StateFlow<List<AccommodationRequirement>> =
    _selectedAccommodationForRequirements
      .flatMapLatest { acc ->
        if (acc == null) MutableStateFlow(emptyList())
        else repository.getRequirementsForAccommodation(acc.id)
      }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Reports for currently open reports dialog
  val currentAccommodationReports: StateFlow<List<AccommodationReport>> =
    _selectedAccommodationForReports
      .flatMapLatest { acc ->
        if (acc == null) MutableStateFlow(emptyList())
        else repository.getReportsForAccommodation(acc.id)
      }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Dialog & Details sheet states
  private val _selectedAccommodation = MutableStateFlow<Accommodation?>(null)
  val selectedAccommodation: StateFlow<Accommodation?> = _selectedAccommodation.asStateFlow()

  private val _isAddEditDialogOpen = MutableStateFlow(false)
  val isAddEditDialogOpen: StateFlow<Boolean> = _isAddEditDialogOpen.asStateFlow()

  private val _editingAccommodation = MutableStateFlow<Accommodation?>(null)
  val editingAccommodation: StateFlow<Accommodation?> = _editingAccommodation.asStateFlow()

  // ==========================================
  // AUDIT LOGGING SYSTEM (ADMIN ONLY)
  // ==========================================
  val allAuditLogs: StateFlow<List<AuditLog>> =
    repository.allAuditLogs
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _auditLogFilter = MutableStateFlow("")
  val auditLogFilter: StateFlow<String> = _auditLogFilter.asStateFlow()

  fun setAuditLogFilter(filter: String) {
    _auditLogFilter.value = filter
  }

  val filteredAuditLogs: StateFlow<List<AuditLog>> =
    combine(allAuditLogs, _auditLogFilter) { logs, filter ->
      if (filter.isBlank()) logs
      else logs.filter {
        it.actionType.contains(filter, ignoreCase = true) ||
        it.entityType.contains(filter, ignoreCase = true) ||
        it.entityIdentifier.contains(filter, ignoreCase = true) ||
        it.adminUsername.contains(filter, ignoreCase = true) ||
        it.details.contains(filter, ignoreCase = true)
      }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun clearAuditLogs() {
    if (!isAdmin.value) return
    viewModelScope.launch {
      val currentAdmin = _currentLoggedUser.value?.username ?: "admin"
      repository.clearAuditLogs()
      repository.recordAuditLog(
        actionType = "CLEAR_AUDIT_LOGS",
        entityType = "AuditSystem",
        identifier = "All Logs",
        adminUsername = currentAdmin,
        details = "Admin cleared audit history logs."
      )
    }
  }

  // ==========================================
  // PROJECTS & PRODUCTS PROGRESS ANALYSIS
  // (Keemart DS, Ninja, Warehouse DC, 9 Ground)
  // ==========================================
  private data class ProjectConfigDef(
    val name: String,
    val code: String,
    val category: String,
    val description: String,
    val colorHex: Long
  )

  val projectProgressList: StateFlow<List<ProjectProgressSummary>> =
    combine(accommodations, allRequirements, allReports) { accList, reqs, reps ->
      val projectConfigs = listOf(
        ProjectConfigDef(
          name = "Keemart DS",
          code = "KM-DS",
          category = "Supermarket Delivery & Retail Central Hubs",
          description = "Fast-moving retail staff fleet and fulfillment housing",
          colorHex = 0xFF2563EB
        ),
        ProjectConfigDef(
          name = "Ninja",
          code = "NJ-EX",
          category = "Rapid Dark-Store Grocery & Quick-Commerce",
          description = "E-commerce express dispatchers and quick-commerce riders housing",
          colorHex = 0xFF7C3AED
        ),
        ProjectConfigDef(
          name = "Warehouse DC",
          code = "WH-DC",
          category = "Regional Logistics & Central Distribution Center",
          description = "Heavy storage, pallet operations, and inbound hub workforce base",
          colorHex = 0xFFD97706
        ),
        ProjectConfigDef(
          name = "9 Ground",
          code = "9G-OPS",
          category = "Ground Operations & Fleet Support",
          description = "Store maintenance, route logistics, and on-ground field service units",
          colorHex = 0xFFDC2626
        )
      )

      projectConfigs.map { cfg ->
        val assignedAccs = accList.filter { acc ->
          acc.projectName.equals(cfg.name, ignoreCase = true) ||
          acc.storeName.contains(cfg.name, ignoreCase = true) ||
          acc.storeCode.contains(cfg.code.take(2), ignoreCase = true) ||
          acc.notes.contains(cfg.name, ignoreCase = true)
        }

        val totalUnits = assignedAccs.size
        val totalCapacity = assignedAccs.sumOf { it.totalCapacity }
        val activeWorkers = assignedAccs.sumOf { it.activeWorkers }
        val occupancyRate = if (totalCapacity > 0) ((activeWorkers.toFloat() / totalCapacity) * 100).toInt() else 0

        val accIds = assignedAccs.map { it.id }.toSet()
        val pendingReqs = reqs.count { it.accommodationId in accIds && it.status != "Fulfilled" }
        val openReps = reps.count { it.accommodationId in accIds && it.status != "Resolved" }

        val occupancyScore = if (totalUnits == 0) 70 else occupancyRate.coerceIn(0, 100)
        val penalty = (pendingReqs * 5 + openReps * 8).coerceAtMost(35)
        val progress = if (totalUnits == 0) 75 else (occupancyScore - penalty).coerceIn(25, 100)

        val statusLabel = when {
          progress >= 85 -> "Optimal"
          progress >= 70 -> "On Track"
          progress >= 50 -> "Needs Attention"
          else -> "Under Review"
        }

        ProjectProgressSummary(
          projectName = cfg.name,
          projectCode = cfg.code,
          category = cfg.category,
          description = cfg.description,
          themeColorHex = cfg.colorHex,
          totalUnits = totalUnits,
          totalCapacity = totalCapacity,
          activeWorkers = activeWorkers,
          occupancyRate = occupancyRate,
          pendingRequirements = pendingReqs,
          openReports = openReps,
          progressPercentage = progress,
          statusLabel = statusLabel,
          accommodations = assignedAccs
        )
      }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  init {
    viewModelScope.launch {
      repository.ensureDefaultDataIfEmpty()
    }
  }

  fun onSearchQueryChange(query: String) {
    _searchQuery.value = query
  }

  // ==========================================
  // SIGN IN & SIGN OUT FULL IMPLEMENTATION
  // ==========================================
  suspend fun loginWithCredentials(usernameInput: String, passwordInput: String): LoginResult {
    val uTrimmed = usernameInput.trim()
    val pTrimmed = passwordInput.trim()

    if (pTrimmed.isEmpty()) {
      return LoginResult.Error("Please enter your password.")
    }

    // 1. Root Master Admin PIN check (322753)
    if (pTrimmed == _adminPin.value.trim() && (uTrimmed.isEmpty() || uTrimmed.equals("admin", ignoreCase = true))) {
      _currentUserRole.value = UserRole.ADMIN
      _currentLoggedUser.value = UserAccount(
        username = "admin",
        passwordHash = "322753",
        fullName = "Zawitco General Administrator",
        role = "ADMIN"
      )
      return LoginResult.Success(UserRole.ADMIN, "admin", "Zawitco General Administrator")
    }

    // 2. Check Database User Accounts
    if (uTrimmed.isNotEmpty()) {
      val userAccount = repository.authenticateUser(uTrimmed, pTrimmed)
      if (userAccount != null) {
        val role = if (userAccount.role.equals("ADMIN", ignoreCase = true)) UserRole.ADMIN else UserRole.VIEWER
        _currentUserRole.value = role
        _currentLoggedUser.value = userAccount
        return LoginResult.Success(role, userAccount.username, userAccount.fullName)
      }
    }

    // 3. Fallback Standard User Passkey (Zawitco)
    if (pTrimmed.equals(userDefaultPassword, ignoreCase = true)) {
      _currentUserRole.value = UserRole.VIEWER
      _currentLoggedUser.value = UserAccount(
        username = if (uTrimmed.isNotBlank()) uTrimmed else "zawitco_user",
        passwordHash = "Zawitco",
        fullName = if (uTrimmed.isNotBlank()) uTrimmed else "Zawitco Staff Viewer",
        role = "USER"
      )
      return LoginResult.Success(UserRole.VIEWER, uTrimmed.ifBlank { "zawitco_user" }, "Zawitco Staff Viewer")
    }

    return LoginResult.Error("Invalid credentials. Please check username and password.")
  }

  fun login(enteredPassword: String): LoginResult {
    val trimmed = enteredPassword.trim()
    if (trimmed.isEmpty()) {
      return LoginResult.Error("Please enter password to sign in.")
    }

    // Check Admin PIN (322753)
    if (trimmed == _adminPin.value.trim()) {
      _currentUserRole.value = UserRole.ADMIN
      _currentLoggedUser.value = UserAccount(
        username = "admin",
        passwordHash = "322753",
        fullName = "Zawitco General Administrator",
        role = "ADMIN"
      )
      return LoginResult.Success(UserRole.ADMIN, "admin", "Zawitco Administrator")
    }

    // Check User/Viewer Password (Zawitco)
    if (trimmed.equals(userDefaultPassword, ignoreCase = true)) {
      _currentUserRole.value = UserRole.VIEWER
      _currentLoggedUser.value = UserAccount(
        username = "user",
        passwordHash = "Zawitco",
        fullName = "Zawitco Staff Viewer",
        role = "USER"
      )
      return LoginResult.Success(UserRole.VIEWER, "user", "Zawitco Staff Viewer")
    }

    return LoginResult.Error("Invalid password. Please check your credentials and try again.")
  }

  fun logout() {
    _currentUserRole.value = UserRole.LOGGED_OUT
    _currentLoggedUser.value = null
    _selectedMainTab.value = MainViewTab.ACCOMMODATIONS
  }

  // ==========================================
  // USER ACCOUNT CREATION & MANAGEMENT (ADMIN ONLY)
  // ==========================================
  fun openUserManagementDialog() {
    if (!isAdmin.value) {
      _showAdminDialog.value = true
      return
    }
    _showUserManagementDialog.value = true
  }

  fun closeUserManagementDialog() {
    _showUserManagementDialog.value = false
  }

  fun createUserAccount(
    username: String,
    password: String,
    fullName: String,
    role: String,
    assignedStation: String,
    phone: String,
    onResult: (Boolean, String) -> Unit
  ) {
    if (!isAdmin.value) {
      onResult(false, "Admin permission required.")
      return
    }
    val uTrimmed = username.trim().lowercase()
    val pTrimmed = password.trim()
    val nameTrimmed = fullName.trim()

    if (uTrimmed.isEmpty() || pTrimmed.isEmpty() || nameTrimmed.isEmpty()) {
      onResult(false, "Username, Password, and Full Name are required.")
      return
    }

    viewModelScope.launch {
      try {
        val user = UserAccount(
          username = uTrimmed,
          passwordHash = pTrimmed,
          fullName = nameTrimmed,
          role = role.uppercase(),
          assignedStation = assignedStation.trim(),
          phone = phone.trim()
        )
        repository.insertUser(user)
        onResult(true, "User account '$uTrimmed' created successfully!")
      } catch (e: Exception) {
        onResult(false, "Error creating account: ${e.message}")
      }
    }
  }

  fun deleteUserAccount(user: UserAccount) {
    if (!isAdmin.value) return
    viewModelScope.launch {
      repository.deleteUser(user)
    }
  }

  // ==========================================
  // STATION MANAGEMENT
  // ==========================================
  fun openStationManagementDialog() {
    if (!isAdmin.value) {
      _showAdminDialog.value = true
      return
    }
    _showStationManagementDialog.value = true
  }

  fun closeStationManagementDialog() {
    _showStationManagementDialog.value = false
  }

  fun saveStation(
    id: Long = 0,
    name: String,
    code: String,
    areaName: String,
    city: String = "Riyadh",
    latitude: Double?,
    longitude: Double?,
    address: String,
    supervisorName: String,
    supervisorPhone: String,
    notes: String
  ) {
    if (!isAdmin.value) return
    viewModelScope.launch {
      val station = Station(
        id = id,
        name = name.trim(),
        code = code.trim(),
        areaName = areaName.trim(),
        city = city.trim(),
        latitude = latitude,
        longitude = longitude,
        address = address.trim(),
        supervisorName = supervisorName.trim(),
        supervisorPhone = supervisorPhone.trim(),
        notes = notes.trim()
      )
      if (id == 0L) {
        repository.insertStation(station)
      } else {
        repository.updateStation(station)
      }
      closeStationManagementDialog()
    }
  }

  fun deleteStation(station: Station) {
    if (!isAdmin.value) return
    viewModelScope.launch {
      repository.deleteStation(station)
    }
  }

  // ==========================================
  // BULK DATA UPLOAD & AUTOMATIC LOCATION ANALYSIS
  // ==========================================
  fun openBulkUploadDialog() {
    if (!isAdmin.value) {
      _showAdminDialog.value = true
      return
    }
    _showBulkUploadDialog.value = true
  }

  fun closeBulkUploadDialog() {
    _showBulkUploadDialog.value = false
  }

  fun importBulkData(rawCsvText: String, onComplete: (BulkParseResult) -> Unit) {
    if (!isAdmin.value) {
      onComplete(BulkParseResult(emptyList(), emptyList(), listOf("Admin permission required"), 0))
      return
    }
    viewModelScope.launch {
      val parseResult = BulkDataParser.parseBulkCsv(rawCsvText)
      if (parseResult.accommodations.isNotEmpty()) {
        repository.insertAccommodations(parseResult.accommodations)
      }
      if (parseResult.stations.isNotEmpty()) {
        repository.insertStations(parseResult.stations)
      }
      if (parseResult.accommodations.isNotEmpty() || parseResult.stations.isNotEmpty()) {
        val currentAdmin = _currentLoggedUser.value?.username ?: "admin"
        repository.recordAuditLog(
          actionType = "BULK_IMPORT",
          entityType = "BulkData",
          identifier = "${parseResult.accommodations.size} units",
          adminUsername = currentAdmin,
          details = "Imported ${parseResult.accommodations.size} accommodations and ${parseResult.stations.size} stations via CSV/Google Sheets."
        )
      }
      onComplete(parseResult)
    }
  }

  // ==========================================
  // EXCEL EXPORT FUNCTIONS
  // ==========================================
  fun openExcelExportDialog() {
    _showExcelExportDialog.value = true
  }

  fun closeExcelExportDialog() {
    _showExcelExportDialog.value = false
  }

  fun exportRequirementsToExcel(context: Context) {
    val reqs = allRequirements.value
    val accs = accommodations.value
    val csv = ExcelExporter.buildRequirementsCsvString(reqs, accs)
    val file = ExcelExporter.exportToExcelFile(context, "Zawitco_Requirements", csv)
    if (file != null) {
      ExcelExporter.shareExcelFile(context, file, "Share Housing Requirements (Excel)")
    }
  }

  fun exportReportsToExcel(context: Context) {
    val reps = allReports.value
    val accs = accommodations.value
    val csv = ExcelExporter.buildReportsCsvString(reps, accs)
    val file = ExcelExporter.exportToExcelFile(context, "Zawitco_Maintenance_Reports", csv)
    if (file != null) {
      ExcelExporter.shareExcelFile(context, file, "Share Maintenance Reports (Excel)")
    }
  }

  fun exportAllDataToExcel(context: Context) {
    val reqs = allRequirements.value
    val reps = allReports.value
    val accs = accommodations.value
    val csv = ExcelExporter.buildCombinedCsvString(reqs, reps, accs)
    val file = ExcelExporter.exportToExcelFile(context, "Zawitco_Complete_Housing_Data", csv)
    if (file != null) {
      ExcelExporter.shareExcelFile(context, file, "Share Complete Housing Data (Excel)")
    }
  }

  // ==========================================
  // GOOGLE CLOUD / FIREBASE CLOUD SERVER BACKUP & SYNC
  // ==========================================
  fun openCloudBackupDialog(context: Context? = null) {
    _showCloudBackupDialog.value = true
    if (context != null) {
      refreshCloudBackupInfo(context)
    }
  }

  fun closeCloudBackupDialog() {
    _showCloudBackupDialog.value = false
    _cloudSyncStatusMessage.value = null
  }

  fun refreshCloudBackupInfo(context: Context) {
    viewModelScope.launch {
      val info = FirebaseCloudBackupService.fetchLatestCloudBackupMetadata(context)
      if (info != null) {
        _latestCloudBackupInfo.value = info
      }
    }
  }

  fun backupToCloudServer(context: Context, onResult: (Boolean, String) -> Unit) {
    viewModelScope.launch {
      _isCloudSyncing.value = true
      _cloudSyncStatusMessage.value = "Uploading database to Google Cloud Firestore..."
      try {
        val accs = repository.allAccommodations.first()
        val reqs = repository.getAllRequirements().first()
        val reps = repository.getAllReports().first()
        val stations = repository.allStations.first()
        val users = repository.allUsers.first()
        val backedUpBy = _currentLoggedUser.value?.fullName ?: "Administrator"

        val result = FirebaseCloudBackupService.backupAllToCloud(
          context = context,
          accommodations = accs,
          requirements = reqs,
          reports = reps,
          stations = stations,
          users = users,
          backedUpBy = backedUpBy
        )

        result.fold(
          onSuccess = { summary ->
            _latestCloudBackupInfo.value = summary
            val total = summary.accommodationsCount + summary.requirementsCount +
              summary.reportsCount + summary.stationsCount + summary.usersCount
            val msg = "Cloud Backup Complete! Successfully stored $total records on Google Cloud Firestore."
            _cloudSyncStatusMessage.value = msg
            onResult(true, msg)
          },
          onFailure = { error ->
            val msg = "Cloud Backup Failed: ${error.message}"
            _cloudSyncStatusMessage.value = msg
            onResult(false, msg)
          }
        )
      } catch (e: Exception) {
        val msg = "Cloud Backup Error: ${e.message}"
        _cloudSyncStatusMessage.value = msg
        onResult(false, msg)
      } finally {
        _isCloudSyncing.value = false
      }
    }
  }

  fun restoreFromCloudServer(context: Context, onResult: (Boolean, String) -> Unit) {
    if (!isAdmin.value) {
      onResult(false, "Administrator privileges required to restore cloud data and modify local records.")
      return
    }
    viewModelScope.launch {
      _isCloudSyncing.value = true
      _cloudSyncStatusMessage.value = "Fetching all records from Google Cloud Server..."
      try {
        val result = FirebaseCloudBackupService.restoreAllFromCloud(context)
        result.fold(
          onSuccess = { restoreData ->
            if (restoreData.accommodations.isNotEmpty()) {
              repository.insertAccommodations(restoreData.accommodations)
            }
            if (restoreData.requirements.isNotEmpty()) {
              repository.insertRequirements(restoreData.requirements)
            }
            if (restoreData.reports.isNotEmpty()) {
              repository.insertReports(restoreData.reports)
            }
            if (restoreData.stations.isNotEmpty()) {
              repository.insertStations(restoreData.stations)
            }
            if (restoreData.users.isNotEmpty()) {
              repository.insertUsers(restoreData.users)
            }
            if (restoreData.summary != null) {
              _latestCloudBackupInfo.value = restoreData.summary
            }
            val total = restoreData.accommodations.size + restoreData.requirements.size +
              restoreData.reports.size + restoreData.stations.size + restoreData.users.size
            val msg = "Restore Complete! Successfully synced $total records from Cloud Server into your local database."
            _cloudSyncStatusMessage.value = msg
            onResult(true, msg)
          },
          onFailure = { error ->
            val msg = "Restore from Cloud Failed: ${error.message}"
            _cloudSyncStatusMessage.value = msg
            onResult(false, msg)
          }
        )
      } catch (e: Exception) {
        val msg = "Restore Error: ${e.message}"
        _cloudSyncStatusMessage.value = msg
        onResult(false, msg)
      } finally {
        _isCloudSyncing.value = false
      }
    }
  }

  suspend fun exportJsonBackupString(): String {
    val accs = repository.allAccommodations.first()
    val reqs = repository.getAllRequirements().first()
    val reps = repository.getAllReports().first()
    val stations = repository.allStations.first()
    val users = repository.allUsers.first()
    val exportedBy = _currentLoggedUser.value?.fullName ?: "Administrator"
    return FirebaseCloudBackupService.buildJsonBackup(accs, reqs, reps, stations, users, exportedBy)
  }

  fun importJsonBackupString(jsonString: String, onResult: (Boolean, String) -> Unit) {
    if (!isAdmin.value) {
      onResult(false, "Administrator privileges required to import records.")
      return
    }
    viewModelScope.launch {
      val parsed = FirebaseCloudBackupService.parseJsonBackup(jsonString)
      if (parsed == null) {
        onResult(false, "Invalid JSON backup file format.")
        return@launch
      }
      try {
        if (parsed.accommodations.isNotEmpty()) repository.insertAccommodations(parsed.accommodations)
        if (parsed.requirements.isNotEmpty()) repository.insertRequirements(parsed.requirements)
        if (parsed.reports.isNotEmpty()) repository.insertReports(parsed.reports)
        if (parsed.stations.isNotEmpty()) repository.insertStations(parsed.stations)
        if (parsed.users.isNotEmpty()) repository.insertUsers(parsed.users)
        val total = parsed.accommodations.size + parsed.requirements.size +
          parsed.reports.size + parsed.stations.size + parsed.users.size
        onResult(true, "Successfully imported $total records from backup file.")
      } catch (e: Exception) {
        onResult(false, "Error saving imported records: ${e.message}")
      }
    }
  }

  // ==========================================
  // ADMIN ACCESS FUNCTIONS
  // ==========================================
  fun openAdminDialog() {
    _showAdminDialog.value = true
  }

  fun closeAdminDialog() {
    _showAdminDialog.value = false
  }

  fun loginAsAdmin(enteredPin: String): Boolean {
    return if (enteredPin.trim() == _adminPin.value.trim()) {
      _currentUserRole.value = UserRole.ADMIN
      _currentLoggedUser.value = UserAccount(
        username = "admin",
        passwordHash = "322753",
        fullName = "Zawitco General Administrator",
        role = "ADMIN"
      )
      _showAdminDialog.value = false
      true
    } else {
      false
    }
  }

  fun logoutAdmin() {
    _currentUserRole.value = UserRole.VIEWER
    _currentLoggedUser.value = UserAccount(
      username = "user",
      passwordHash = "Zawitco",
      fullName = "Zawitco Staff Viewer",
      role = "USER"
    )
  }

  fun updateAdminPin(newPin: String) {
    if (newPin.isNotBlank() && newPin.length >= 4) {
      _adminPin.value = newPin.trim()
    }
  }

  // WhatsApp group functions
  fun openWhatsAppDialog() {
    _showWhatsAppDialog.value = true
  }

  fun closeWhatsAppDialog() {
    _showWhatsAppDialog.value = false
  }

  fun updateGlobalWhatsAppUrl(newUrl: String) {
    if (newUrl.isNotBlank()) {
      _globalWhatsAppUrl.value = newUrl.trim()
    }
  }

  // Requirements dialog functions
  fun openRequirementsDialog(acc: Accommodation) {
    _selectedAccommodationForRequirements.value = acc
  }

  fun closeRequirementsDialog() {
    _selectedAccommodationForRequirements.value = null
  }

  fun addRequirement(
    accommodationId: Long,
    itemName: String,
    quantity: Int,
    urgency: String,
    requestedBy: String,
    notes: String
  ) {
    if (!isAdmin.value) return
    viewModelScope.launch {
      val currentAdmin = _currentLoggedUser.value?.username ?: "admin"
      repository.insertRequirement(
        AccommodationRequirement(
          accommodationId = accommodationId,
          itemName = itemName,
          quantity = quantity,
          urgency = urgency,
          status = "Pending",
          requestedBy = requestedBy,
          notes = notes
        )
      )
      repository.recordAuditLog(
        actionType = "ADD_REQUIREMENT",
        entityType = "Requirement",
        identifier = "$itemName (Qty: $quantity)",
        adminUsername = currentAdmin,
        details = "Created requirement for accommodation #$accommodationId (Urgency: $urgency, Requested by: $requestedBy)"
      )
    }
  }

  fun updateRequirementStatus(req: AccommodationRequirement, newStatus: String) {
    if (!isAdmin.value) return
    viewModelScope.launch {
      val currentAdmin = _currentLoggedUser.value?.username ?: "admin"
      repository.updateRequirement(req.copy(status = newStatus))
      repository.recordAuditLog(
        actionType = "FULFILL_REQUIREMENT",
        entityType = "Requirement",
        identifier = "${req.itemName} (#${req.id})",
        adminUsername = currentAdmin,
        details = "Updated status from '${req.status}' to '$newStatus'"
      )
    }
  }

  fun deleteRequirement(req: AccommodationRequirement) {
    if (!isAdmin.value) return
    viewModelScope.launch {
      val currentAdmin = _currentLoggedUser.value?.username ?: "admin"
      repository.deleteRequirement(req)
      repository.recordAuditLog(
        actionType = "DELETE_REQUIREMENT",
        entityType = "Requirement",
        identifier = "${req.itemName} (#${req.id})",
        adminUsername = currentAdmin,
        details = "Removed requirement for accommodation #${req.accommodationId}"
      )
    }
  }

  // Report issue dialog functions
  fun openReportsDialog(acc: Accommodation) {
    _selectedAccommodationForReports.value = acc
  }

  fun closeReportsDialog() {
    _selectedAccommodationForReports.value = null
  }

  fun addReport(
    accommodationId: Long,
    category: String,
    title: String,
    description: String,
    severity: String,
    reportedBy: String,
    reporterPhone: String
  ) {
    if (!isAdmin.value) return
    viewModelScope.launch {
      val currentAdmin = _currentLoggedUser.value?.username ?: "admin"
      repository.insertReport(
        AccommodationReport(
          accommodationId = accommodationId,
          issueCategory = category,
          title = title,
          description = description,
          severity = severity,
          status = "Open",
          reportedBy = reportedBy,
          reporterPhone = reporterPhone
        )
      )
      repository.recordAuditLog(
        actionType = "ADD_REPORT",
        entityType = "Report",
        identifier = "[$category] $title",
        adminUsername = currentAdmin,
        details = "Logged maintenance issue for accommodation #$accommodationId (Severity: $severity, Reported by: $reportedBy)"
      )
    }
  }

  fun updateReportStatus(report: AccommodationReport, newStatus: String) {
    if (!isAdmin.value) return
    viewModelScope.launch {
      val currentAdmin = _currentLoggedUser.value?.username ?: "admin"
      repository.updateReport(report.copy(status = newStatus))
      repository.recordAuditLog(
        actionType = "RESOLVE_REPORT",
        entityType = "Report",
        identifier = "${report.title} (#${report.id})",
        adminUsername = currentAdmin,
        details = "Updated maintenance report status to '$newStatus'"
      )
    }
  }

  fun deleteReport(report: AccommodationReport) {
    if (!isAdmin.value) return
    viewModelScope.launch {
      val currentAdmin = _currentLoggedUser.value?.username ?: "admin"
      repository.deleteReport(report)
      repository.recordAuditLog(
        actionType = "DELETE_REPORT",
        entityType = "Report",
        identifier = "${report.title} (#${report.id})",
        adminUsername = currentAdmin,
        details = "Deleted maintenance report ticket."
      )
    }
  }

  // Accommodation CRUD
  fun openAddDialog() {
    if (!isAdmin.value) {
      _showAdminDialog.value = true
      return
    }
    _editingAccommodation.value = null
    _isAddEditDialogOpen.value = true
  }

  fun openEditDialog(item: Accommodation) {
    if (!isAdmin.value) {
      _showAdminDialog.value = true
      return
    }
    _editingAccommodation.value = item
    _isAddEditDialogOpen.value = true
  }

  fun closeAddEditDialog() {
    _isAddEditDialogOpen.value = false
    _editingAccommodation.value = null
  }

  fun selectAccommodation(item: Accommodation) {
    _selectedAccommodation.value = item
  }

  fun clearSelectedAccommodation() {
    _selectedAccommodation.value = null
  }

  fun importSingleAccommodation(accommodation: Accommodation, onComplete: () -> Unit) {
    if (!isAdmin.value) {
      onComplete()
      return
    }
    viewModelScope.launch {
      repository.insert(accommodation)
      onComplete()
    }
  }

  fun saveAccommodation(
    id: Long = 0,
    areaName: String,
    villaNumber: String,
    floorNumber: String,
    roomNumber: String,
    totalWorkers: Int = 0,
    totalCapacity: Int = 0,
    activeWorkers: Int = 0,
    accommodationLocationUrl: String = "",
    storeLocationUrl: String = "",
    storeCode: String = "",
    storeName: String = "",
    workerPhone: String = "",
    workerPhone2: String = "",
    ownerName: String = "",
    ownerPhone: String = "",
    ownerBankName: String = "",
    ownerIban: String = "",
    googleMapsUrl: String = "",
    latitude: Double? = null,
    longitude: Double? = null,
    buildingImageUri: String? = null,
    billingPictureUri: String? = null,
    doorPictureUri: String? = null,
    notes: String = "",
    whatsappGroupUrl: String = "",
    stationName: String = "",
    projectName: String = ""
  ) {
    if (!isAdmin.value) {
      _showAdminDialog.value = true
      return
    }

    viewModelScope.launch {
      val item = Accommodation(
        id = id,
        areaName = areaName.trim(),
        villaNumber = villaNumber.trim(),
        floorNumber = floorNumber.trim(),
        roomNumber = roomNumber.trim(),
        totalWorkers = totalWorkers,
        totalCapacity = totalCapacity,
        activeWorkers = activeWorkers,
        accommodationLocationUrl = accommodationLocationUrl.trim().ifBlank { googleMapsUrl.trim() },
        storeLocationUrl = storeLocationUrl.trim(),
        storeCode = storeCode.trim(),
        storeName = storeName.trim(),
        workerPhone = workerPhone.trim(),
        workerPhone2 = workerPhone2.trim(),
        ownerName = ownerName.trim(),
        ownerPhone = ownerPhone.trim(),
        ownerBankName = ownerBankName.trim(),
        ownerIban = ownerIban.trim(),
        googleMapsUrl = googleMapsUrl.trim().ifBlank { accommodationLocationUrl.trim() },
        latitude = latitude,
        longitude = longitude,
        buildingImageUri = buildingImageUri,
        billingPictureUri = billingPictureUri,
        doorPictureUri = doorPictureUri,
        notes = notes.trim(),
        whatsappGroupUrl = whatsappGroupUrl.trim(),
        stationName = stationName.trim().ifBlank { storeName.trim().ifBlank { storeCode.trim() } },
        projectName = projectName.trim()
      )

      val currentAdmin = _currentLoggedUser.value?.username ?: "admin"
      if (id == 0L) {
        val newId = repository.insert(item)
        repository.recordAuditLog(
          actionType = "ADD_ACCOMMODATION",
          entityType = "Accommodation",
          identifier = "${item.areaName} - Villa ${item.villaNumber}",
          adminUsername = currentAdmin,
          details = "Created new housing unit record #$newId (Capacity: ${item.totalCapacity}, Active: ${item.activeWorkers}, Project: ${item.projectName.ifBlank { "General" }})"
        )
      } else {
        repository.update(item)
        repository.recordAuditLog(
          actionType = "EDIT_ACCOMMODATION",
          entityType = "Accommodation",
          identifier = "${item.areaName} - Villa ${item.villaNumber}",
          adminUsername = currentAdmin,
          details = "Modified housing unit details (Capacity: ${item.totalCapacity}, Active: ${item.activeWorkers}, Project: ${item.projectName.ifBlank { "General" }})"
        )
        if (_selectedAccommodation.value?.id == id) {
          _selectedAccommodation.value = item
        }
      }
      closeAddEditDialog()
    }
  }

  fun deleteAccommodation(item: Accommodation) {
    if (!isAdmin.value) {
      _showAdminDialog.value = true
      return
    }
    viewModelScope.launch {
      val currentAdmin = _currentLoggedUser.value?.username ?: "admin"
      repository.delete(item)
      repository.recordAuditLog(
        actionType = "DELETE_ACCOMMODATION",
        entityType = "Accommodation",
        identifier = "${item.areaName} - Villa ${item.villaNumber}",
        adminUsername = currentAdmin,
        details = "Permanently deleted accommodation #${item.id}."
      )
      if (_selectedAccommodation.value?.id == item.id) {
        _selectedAccommodation.value = null
      }
    }
  }

  fun saveImageUriToInternalStorage(context: Context, sourceUri: Uri): String? {
    return try {
      val imagesDir = File(context.filesDir, "accommodation_images").apply {
        if (!exists()) mkdirs()
      }
      val destFile = File(imagesDir, "img_${UUID.randomUUID()}.jpg")
      context.contentResolver.openInputStream(sourceUri)?.use { input ->
        FileOutputStream(destFile).use { output ->
          input.copyTo(output)
        }
      }
      Uri.fromFile(destFile).toString()
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }

  companion object {
    fun provideFactory(
      repository: AccommodationRepository
    ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
      @Suppress("UNCHECKED_CAST")
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AccommodationViewModel(repository) as T
      }
    }
  }
}
