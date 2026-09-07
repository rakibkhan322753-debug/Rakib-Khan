package com.example.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Accommodation
import com.example.data.remote.GeminiMapsService
import com.example.data.repository.AccommodationRepository
import com.example.util.GpsLocationHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

sealed interface GroundingUiState {
  object Idle : GroundingUiState
  object Loading : GroundingUiState
  data class Success(val content: String, val queryType: String) : GroundingUiState
  data class Error(val message: String) : GroundingUiState
}

@OptIn(ExperimentalCoroutinesApi::class)
class AccommodationViewModel(
  private val repository: AccommodationRepository,
  private val mapsService: GeminiMapsService = GeminiMapsService()
) : ViewModel() {

  // Admin Access Control: False = Viewer mode (read-only), True = Admin mode (full access)
  private val _isAdmin = MutableStateFlow(false)
  val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

  private val _adminPin = MutableStateFlow("1234")
  val adminPin: StateFlow<String> = _adminPin.asStateFlow()

  private val _showAdminDialog = MutableStateFlow(false)
  val showAdminDialog: StateFlow<Boolean> = _showAdminDialog.asStateFlow()

  // WhatsApp Community Group settings
  private val _globalWhatsAppUrl = MutableStateFlow("https://chat.whatsapp.com/ZawitcoAccommodationCommunity")
  val globalWhatsAppUrl: StateFlow<String> = _globalWhatsAppUrl.asStateFlow()

  private val _showWhatsAppDialog = MutableStateFlow(false)
  val showWhatsAppDialog: StateFlow<Boolean> = _showWhatsAppDialog.asStateFlow()

  // Live GPS tracking & sorting
  private val _userGpsCoordinates = MutableStateFlow<Pair<Double, Double>?>(null)
  val userGpsCoordinates: StateFlow<Pair<Double, Double>?> = _userGpsCoordinates.asStateFlow()

  private val _isGpsSortActive = MutableStateFlow(false)
  val isGpsSortActive: StateFlow<Boolean> = _isGpsSortActive.asStateFlow()

  private val _isGpsLocating = MutableStateFlow(false)
  val isGpsLocating: StateFlow<Boolean> = _isGpsLocating.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  val accommodations: StateFlow<List<Accommodation>> = combine(
    _searchQuery.flatMapLatest { repository.searchAccommodations(it) },
    _userGpsCoordinates,
    _isGpsSortActive
  ) { list, userGps, sortActive ->
    if (sortActive && userGps != null) {
      list.sortedBy { item ->
        if (item.latitude != null && item.longitude != null) {
          GpsLocationHelper.calculateDistanceKm(userGps.first, userGps.second, item.latitude, item.longitude)
        } else {
          Double.MAX_VALUE
        }
      }
    } else {
      list
    }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  private val _selectedAccommodation = MutableStateFlow<Accommodation?>(null)
  val selectedAccommodation: StateFlow<Accommodation?> = _selectedAccommodation.asStateFlow()

  private val _isAddEditDialogOpen = MutableStateFlow(false)
  val isAddEditDialogOpen: StateFlow<Boolean> = _isAddEditDialogOpen.asStateFlow()

  private val _editingAccommodation = MutableStateFlow<Accommodation?>(null)
  val editingAccommodation: StateFlow<Accommodation?> = _editingAccommodation.asStateFlow()

  private val _groundingState = MutableStateFlow<GroundingUiState>(GroundingUiState.Idle)
  val groundingState: StateFlow<GroundingUiState> = _groundingState.asStateFlow()

  init {
    viewModelScope.launch {
      repository.ensureDefaultDataIfEmpty()
    }
  }

  fun onSearchQueryChange(query: String) {
    _searchQuery.value = query
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
      _isAdmin.value = true
      true
    } else {
      false
    }
  }

  fun logoutAdmin() {
    _isAdmin.value = false
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

  // GPS Location functions
  fun setUserGpsCoordinates(latitude: Double, longitude: Double) {
    _userGpsCoordinates.value = Pair(latitude, longitude)
    _isGpsLocating.value = false
  }

  fun setGpsLocating(locating: Boolean) {
    _isGpsLocating.value = locating
  }

  fun toggleGpsSort() {
    _isGpsSortActive.value = !_isGpsSortActive.value
  }

  fun openAddDialog() {
    if (!_isAdmin.value) {
      // Prompt for Admin login if attempting to add data without permissions
      _showAdminDialog.value = true
      return
    }
    _editingAccommodation.value = null
    _isAddEditDialogOpen.value = true
  }

  fun openEditDialog(item: Accommodation) {
    if (!_isAdmin.value) {
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
    _groundingState.value = GroundingUiState.Idle
  }

  fun clearSelectedAccommodation() {
    _selectedAccommodation.value = null
    _groundingState.value = GroundingUiState.Idle
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
    notes: String,
    whatsappGroupUrl: String = ""
  ) {
    // Only admins are permitted to input and modify records
    if (!_isAdmin.value) {
      _showAdminDialog.value = true
      return
    }

    viewModelScope.launch {
      val resolvedMapsUrl = when {
        googleMapsUrl.isNotBlank() -> googleMapsUrl.trim()
        latitude != null && longitude != null -> "https://maps.google.com/?q=$latitude,$longitude"
        else -> ""
      }

      val item = Accommodation(
        id = id,
        areaName = areaName.trim(),
        villaNumber = villaNumber.trim(),
        floorNumber = floorNumber.trim(),
        roomNumber = roomNumber.trim(),
        workerPhone = workerPhone.trim(),
        ownerPhone = ownerPhone.trim(),
        googleMapsUrl = resolvedMapsUrl,
        latitude = latitude,
        longitude = longitude,
        buildingImageUri = buildingImageUri,
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
    if (!_isAdmin.value) {
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

  fun fetchMapsGrounding(
    accommodation: Accommodation,
    queryType: String = "general",
    customPrompt: String? = null
  ) {
    viewModelScope.launch {
      _groundingState.value = GroundingUiState.Loading
      val result = mapsService.getGroundedMapsInsights(
        areaName = accommodation.areaName,
        villaNumber = accommodation.villaNumber,
        latitude = accommodation.latitude,
        longitude = accommodation.longitude,
        queryType = queryType,
        customPrompt = customPrompt
      )

      result.onSuccess { content ->
        _groundingState.value = GroundingUiState.Success(content, queryType)
      }.onFailure { ex ->
        _groundingState.value = GroundingUiState.Error(
          ex.localizedMessage ?: "Failed to retrieve Google Maps information."
        )
      }
    }
  }

  /**
   * Helper to persist chosen image into app internal storage
   */
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
