package com.example

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.AppDatabase
import com.example.data.repository.AccommodationRepository
import com.example.ui.components.AccommodationCard
import com.example.ui.components.AccommodationDetailsSheet
import com.example.ui.components.AddEditAccommodationDialog
import com.example.ui.components.AdminLoginDialog
import com.example.ui.components.HeaderBar
import com.example.ui.components.WhatsAppGroupDialog
import com.example.ui.components.ZawitcoCompanyLogo
import com.example.ui.theme.DarkGreen
import com.example.ui.theme.LightGreen
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
import com.example.util.GpsLocationHelper

class MainActivity : ComponentActivity() {

  private val viewModel: AccommodationViewModel by viewModels {
    val db = AppDatabase.getDatabase(applicationContext, rememberCoroutineScopeOrGlobal())
    val repository = AccommodationRepository(db.accommodationDao())
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

  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val accommodations by viewModel.accommodations.collectAsStateWithLifecycle()
  val selectedAccommodation by viewModel.selectedAccommodation.collectAsStateWithLifecycle()
  val isAddEditDialogOpen by viewModel.isAddEditDialogOpen.collectAsStateWithLifecycle()
  val editingAccommodation by viewModel.editingAccommodation.collectAsStateWithLifecycle()
  val groundingState by viewModel.groundingState.collectAsStateWithLifecycle()

  val isAdmin by viewModel.isAdmin.collectAsStateWithLifecycle()
  val adminPin by viewModel.adminPin.collectAsStateWithLifecycle()
  val showAdminDialog by viewModel.showAdminDialog.collectAsStateWithLifecycle()

  val globalWhatsAppUrl by viewModel.globalWhatsAppUrl.collectAsStateWithLifecycle()
  val showWhatsAppDialog by viewModel.showWhatsAppDialog.collectAsStateWithLifecycle()

  val userGpsCoordinates by viewModel.userGpsCoordinates.collectAsStateWithLifecycle()
  val isGpsSortActive by viewModel.isGpsSortActive.collectAsStateWithLifecycle()
  val isGpsLocating by viewModel.isGpsLocating.collectAsStateWithLifecycle()

  fun requestUserGps() {
    viewModel.setGpsLocating(true)
    GpsLocationHelper.getCurrentCoordinates(
      context = context,
      onSuccess = { lat, lng ->
        viewModel.setUserGpsCoordinates(lat, lng)
        if (!isGpsSortActive) {
          viewModel.toggleGpsSort()
        }
        Toast.makeText(context, "GPS location active: ${String.format("%.4f", lat)}, ${String.format("%.4f", lng)}", Toast.LENGTH_SHORT).show()
      },
      onError = { error ->
        viewModel.setGpsLocating(false)
        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
      }
    )
  }

  val locationPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions()
  ) { permissions ->
    val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    if (granted) {
      requestUserGps()
    } else {
      viewModel.setGpsLocating(false)
      Toast.makeText(context, "Location permission denied.", Toast.LENGTH_SHORT).show()
    }
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = MaterialTheme.colorScheme.background,
    topBar = {
      Column(modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)) {
        HeaderBar(
          isAdmin = isAdmin,
          onAdminClick = { viewModel.openAdminDialog() },
          onWhatsAppClick = { viewModel.openWhatsAppDialog() },
          onAddNewClick = { viewModel.openAddDialog() }
        )
      }
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // Search Bar and GPS Quick Action Section
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
                  tint = Slate400
                )
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(16.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ZawitcoBlue,
            unfocusedBorderColor = Slate200,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("search_input")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Quick Tools Row: GPS Nearby Filter & WhatsApp Group Shortcut
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // GPS Distance Sorting Chip
          FilterChip(
            selected = isGpsSortActive,
            onClick = {
              if (userGpsCoordinates == null) {
                if (GpsLocationHelper.hasLocationPermission(context)) {
                  requestUserGps()
                } else {
                  locationPermissionLauncher.launch(
                    arrayOf(
                      Manifest.permission.ACCESS_FINE_LOCATION,
                      Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                  )
                }
              } else {
                viewModel.toggleGpsSort()
              }
            },
            label = {
              if (isGpsLocating) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  CircularProgressIndicator(modifier = Modifier.size(12.dp), strokeWidth = 2.dp)
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Detecting GPS...")
                }
              } else {
                Text(
                  text = if (isGpsSortActive) "Nearest by GPS (Active)" else "Sort by GPS Proximity",
                  fontSize = 12.sp,
                  fontWeight = if (isGpsSortActive) FontWeight.Bold else FontWeight.Medium
                )
              }
            },
            leadingIcon = {
              Icon(
                imageVector = if (isGpsSortActive) Icons.Filled.NearMe else Icons.Default.MyLocation,
                contentDescription = "GPS Sort",
                tint = if (isGpsSortActive) ZawitcoBlue else Slate500,
                modifier = Modifier.size(16.dp)
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = ZawitcoLightBlue,
              selectedLabelColor = ZawitcoBlue
            ),
            border = FilterChipDefaults.filterChipBorder(
              borderColor = if (isGpsSortActive) ZawitcoBlue else Slate200,
              enabled = true,
              selected = isGpsSortActive
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("gps_sort_filter_chip")
          )

          // WhatsApp Group Community Quick Badge
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
                text = "WhatsApp Group",
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
              text = if (searchQuery.isNotBlank()) "No Matching Accommodations" else "No Accommodations Yet",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = Slate800
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = if (searchQuery.isNotBlank()) {
                "Try searching for another area, room number, or contact phone."
              } else {
                "Zawitco properties and staff units will appear here once added by an administrator."
              },
              fontSize = 14.sp,
              color = Slate500,
              modifier = Modifier.padding(horizontal = 24.dp),
              lineHeight = 20.sp
            )
            if (searchQuery.isBlank()) {
              Spacer(modifier = Modifier.height(18.dp))
              Button(
                onClick = { viewModel.openAddDialog() },
                colors = ButtonDefaults.buttonColors(containerColor = if (isAdmin) ZawitcoOrange else ZawitcoBlue),
                shape = RoundedCornerShape(12.dp)
              ) {
                Icon(Icons.Default.Add, contentDescription = null, Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(if (isAdmin) "Add Accommodation" else "Admin Login to Add", fontWeight = FontWeight.Bold)
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
            AccommodationCard(
              accommodation = item,
              isAdmin = isAdmin,
              userGpsCoordinates = userGpsCoordinates,
              onViewDetails = { viewModel.selectAccommodation(item) },
              onEdit = { viewModel.openEditDialog(item) },
              onDelete = { viewModel.deleteAccommodation(item) }
            )
          }
        }
      }
    }
  }

  // Admin Login / Settings Dialog
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

  // WhatsApp Group Dialog
  if (showWhatsAppDialog) {
    WhatsAppGroupDialog(
      currentGroupUrl = globalWhatsAppUrl,
      isAdmin = isAdmin,
      onUpdateGroupUrl = { newUrl -> viewModel.updateGlobalWhatsAppUrl(newUrl) },
      onDismiss = { viewModel.closeWhatsAppDialog() }
    )
  }

  // Add / Edit Modal Dialog (Admin Only)
  if (isAddEditDialogOpen) {
    AddEditAccommodationDialog(
      initialItem = editingAccommodation,
      onDismiss = { viewModel.closeAddEditDialog() },
      onSave = { id, area, villa, floor, room, worker, owner, mapsUrl, lat, lng, img, notes, whatsapp ->
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
          notes = notes,
          whatsappGroupUrl = whatsapp
        )
      },
      onSaveImageToStorage = { uri ->
        viewModel.saveImageUriToInternalStorage(context, uri)
      }
    )
  }

  // Details Sheet Dialog
  selectedAccommodation?.let { item ->
    AccommodationDetailsSheet(
      accommodation = item,
      groundingState = groundingState,
      onDismiss = { viewModel.clearSelectedAccommodation() },
      onFetchGrounding = { queryType, customPrompt ->
        viewModel.fetchMapsGrounding(item, queryType, customPrompt)
      }
    )
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  MyApplicationTheme { Greeting("Android") }
}
