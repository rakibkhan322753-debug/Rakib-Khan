package com.example.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Accommodation
import com.example.data.model.AccommodationReport
import com.example.data.model.AccommodationRequirement
import com.example.data.repository.AccommodationRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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

sealed interface LoginResult {
  data class Success(val role: UserRole) : LoginResult
  data class Error(val message: String) : LoginResult
}

@OptIn(ExperimentalCoroutinesApi::class)
class AccommodationViewModel(
  private val repository: AccommodationRepository
) : ViewModel() {

  // Current session role. Default is LOGGED_OUT so app launches with Login interface
  private val _currentUserRole = MutableStateFlow<UserRole>(UserRole.LOGGED_OUT)
  val currentUserRole: StateFlow<UserRole> = _currentUserRole.asStateFlow()

  val isAdmin: StateFlow<Boolean> = _currentUserRole
    .map { it == UserRole.ADMIN }
    .stateIn(viewModelScope, SharingStarted.Eagerly, false)

  val isViewer: StateFlow<Boolean> = _currentUserRole
    .map { it == UserRole.VIEWER }
    .stateIn(viewModelScope, SharingStarted.Eagerly, false)

  // Passwords:
  // 1. Admin (322753)
  // 2. User (Zawitco)
  private val _adminPin = MutableStateFlow("322753")
  val adminPin: StateFlow<String> = _adminPin.asStateFlow()

  val userPassword: String = "Zawitco"

  // Quick dialogs state
  private val _showAdminDialog = MutableStateFlow(false)
  val showAdminDialog: StateFlow<Boolean> = _showAdminDialog.asStateFlow()

  private val _showWhatsAppDialog = MutableStateFlow(false)
  val showWhatsAppDialog: StateFlow<Boolean> = _showWhatsAppDialog.asStateFlow()

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

  // Accommodations list
  val accommodations: StateFlow<List<Accommodation>> = _searchQuery
    .flatMapLatest { query -> repository.searchAccommodations(query) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // All Requirements and Reports for active app notifications
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

  init {
    viewModelScope.launch {
      repository.ensureDefaultDataIfEmpty()
    }
  }

  fun onSearchQueryChange(query: String) {
    _searchQuery.value = query
  }

  // Main Authentication functions
  fun login(enteredPassword: String): LoginResult {
    val trimmed = enteredPassword.trim()
    if (trimmed.isEmpty()) {
      return LoginResult.Error("Please enter password to sign in.")
    }

    // Check Admin Password (default 322753)
    if (trimmed == _adminPin.value.trim()) {
      _currentUserRole.value = UserRole.ADMIN
      return LoginResult.Success(UserRole.ADMIN)
    }

    // Check User/Viewer Password (Zawitco, case-insensitive)
    if (trimmed.equals(userPassword, ignoreCase = true)) {
      _currentUserRole.value = UserRole.VIEWER
      return LoginResult.Success(UserRole.VIEWER)
    }

    return LoginResult.Error("Invalid password. Use '322753' for Admin or 'Zawitco' for User.")
  }

  fun logout() {
    _currentUserRole.value = UserRole.LOGGED_OUT
  }

  // Admin access functions
  fun openAdminDialog() {
    _showAdminDialog.value = true
  }

  fun closeAdminDialog() {
    _showAdminDialog.value = false
  }

  fun loginAsAdmin(enteredPin: String): Boolean {
    return if (enteredPin.trim() == _adminPin.value.trim()) {
      _currentUserRole.value = UserRole.ADMIN
      _showAdminDialog.value = false
      true
    } else {
      false
    }
  }

  fun logoutAdmin() {
    _currentUserRole.value = UserRole.VIEWER
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
    viewModelScope.launch {
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
    }
  }

  fun updateRequirementStatus(req: AccommodationRequirement, newStatus: String) {
    viewModelScope.launch {
      repository.updateRequirement(req.copy(status = newStatus))
    }
  }

  fun deleteRequirement(req: AccommodationRequirement) {
    if (!isAdmin.value) return
    viewModelScope.launch {
      repository.deleteRequirement(req)
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
    viewModelScope.launch {
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
    }
  }

  fun updateReportStatus(report: AccommodationReport, newStatus: String) {
    viewModelScope.launch {
      repository.updateReport(report.copy(status = newStatus))
    }
  }

  fun deleteReport(report: AccommodationReport) {
    if (!isAdmin.value) return
    viewModelScope.launch {
      repository.deleteReport(report)
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

  fun saveAccommodation(
    id: Long = 0,
    areaName: String,
    villaNumber: String,
    floorNumber: String,
    roomNumber: String,
    workerPhone: String,
    ownerPhone: String,
    googleMapsUrl: String,
    latitude: Double?,
    longitude: Double?,
    buildingImageUri: String?,
    billingPictureUri: String?,
    doorPictureUri: String?,
    notes: String,
    whatsappGroupUrl: String = ""
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
        workerPhone = workerPhone.trim(),
        ownerPhone = ownerPhone.trim(),
        googleMapsUrl = googleMapsUrl.trim(),
        latitude = latitude,
        longitude = longitude,
        buildingImageUri = buildingImageUri,
        billingPictureUri = billingPictureUri,
        doorPictureUri = doorPictureUri,
        notes = notes.trim(),
        whatsappGroupUrl = whatsappGroupUrl.trim()
      )

      if (id == 0L) {
        repository.insert(item)
      } else {
        repository.update(item)
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
      repository.delete(item)
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
