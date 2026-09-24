package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserAccount
import com.example.ui.theme.DarkGreen
import com.example.ui.theme.LightGreen
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.ZawitcoBlue
import com.example.ui.theme.ZawitcoDarkOrange
import com.example.ui.theme.ZawitcoLightBlue
import com.example.ui.theme.ZawitcoOrange

private val WhatsAppColor = Color(0xFF25D366)

@Composable
fun HeaderBar(
  isAdmin: Boolean,
  currentUser: UserAccount?,
  isSearchActive: Boolean = false,
  searchQuery: String = "",
  onToggleSearch: () -> Unit = {},
  onSearchQueryChange: (String) -> Unit = {},
  onAdminClick: () -> Unit,
  onWhatsAppClick: () -> Unit,
  onExportExcelClick: () -> Unit,
  onBulkUploadClick: () -> Unit,
  onCloudBackupClick: () -> Unit,
  onUserAccountsClick: () -> Unit,
  onAddNewClick: () -> Unit,
  onLogout: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    color = MaterialTheme.colorScheme.surface,
    shadowElevation = 3.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Official Zawitco Company Logo & Brand Name
        ZawitcoCompanyLogo(
          height = 36.dp,
          showSubtext = true
        )

        // Action controls on top right
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
          // ==========================================
          // SEARCH ICON ON TOP RIGHT (Requirement 2)
          // ==========================================
          IconButton(
            onClick = onToggleSearch,
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(if (isSearchActive) ZawitcoOrange else ZawitcoLightBlue)
              .testTag("header_search_icon_button")
          ) {
            Icon(
              imageVector = if (isSearchActive) Icons.Default.Clear else Icons.Default.Search,
              contentDescription = "Search Accommodations",
              tint = if (isSearchActive) Color.White else ZawitcoBlue,
              modifier = Modifier.size(19.dp)
            )
          }

          // Export Excel Button
          IconButton(
            onClick = onExportExcelClick,
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(Color(0xFFDCFCE7))
              .testTag("header_export_excel_button")
          ) {
            Icon(
              imageVector = Icons.Default.TableChart,
              contentDescription = "Export Excel File",
              tint = Color(0xFF15803D),
              modifier = Modifier.size(18.dp)
            )
          }

          // Data Import Button (Google Sheets & Single Housing Import) - ADMIN ONLY
          if (isAdmin) {
            IconButton(
              onClick = onBulkUploadClick,
              modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(ZawitcoLightBlue)
                .testTag("header_bulk_upload_button")
            ) {
              Icon(
                imageVector = Icons.Default.CloudDownload,
                contentDescription = "Import Data (Google Sheets / Bulk)",
                tint = ZawitcoBlue,
                modifier = Modifier.size(18.dp)
              )
            }
          }

          // Google Cloud / Firebase Cloud Server Backup & Sync Button
          IconButton(
            onClick = onCloudBackupClick,
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(Color(0xFFE0F2FE))
              .testTag("header_cloud_backup_button")
          ) {
            Icon(
              imageVector = Icons.Default.CloudSync,
              contentDescription = "Google Cloud / Firebase Backup & Sync",
              tint = ZawitcoBlue,
              modifier = Modifier.size(19.dp)
            )
          }

          // User Accounts Management (Admin Only)
          if (isAdmin) {
            IconButton(
              onClick = onUserAccountsClick,
              modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFEDD5))
                .testTag("header_user_accounts_button")
            ) {
              Icon(
                imageVector = Icons.Default.ManageAccounts,
                contentDescription = "User Accounts Management",
                tint = Color(0xFFC2410C),
                modifier = Modifier.size(18.dp)
              )
            }
          }

          // WhatsApp Community Link Button
          IconButton(
            onClick = onWhatsAppClick,
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(Color(0xFFE8F8F0))
              .testTag("header_whatsapp_button")
          ) {
            Icon(
              imageVector = Icons.Default.Group,
              contentDescription = "WhatsApp Group Link",
              tint = WhatsAppColor,
              modifier = Modifier.size(18.dp)
            )
          }

          // Role Badge / User Indicator with Read-Only indicator for Viewers
          Surface(
            onClick = onAdminClick,
            shape = RoundedCornerShape(8.dp),
            color = if (isAdmin) LightGreen else Slate100,
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              if (isAdmin) DarkGreen.copy(alpha = 0.4f) else Slate400.copy(alpha = 0.5f)
            ),
            modifier = Modifier.testTag("header_admin_mode_button")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = if (isAdmin) Icons.Default.LockOpen else Icons.Default.Lock,
                contentDescription = null,
                tint = if (isAdmin) DarkGreen else Slate600,
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (isAdmin) "Admin" else "Viewer (Read-Only)",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isAdmin) DarkGreen else Slate700
              )
            }
          }

          // "Add" accommodation button (Admin only)
          if (isAdmin) {
            Button(
              onClick = onAddNewClick,
              colors = ButtonDefaults.buttonColors(
                containerColor = ZawitcoOrange,
                contentColor = Color.White
              ),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier
                .height(34.dp)
                .testTag("add_new_accommodation_button")
            ) {
              Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Accommodation",
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(2.dp))
              Text(
                text = "Add",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            }
          }

          // Full Sign Out Button: Returns to Login Interface
          IconButton(
            onClick = onLogout,
            modifier = Modifier
              .size(34.dp)
              .clip(CircleShape)
              .background(Slate100)
              .testTag("header_logout_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Logout,
              contentDescription = "Sign Out",
              tint = Color.Black,
              modifier = Modifier.size(17.dp)
            )
          }
        }
      }

      // ==========================================
      // EXPANDABLE SEARCH BAR (Only shows when search icon is clicked!)
      // ==========================================
      AnimatedVisibility(
        visible = isSearchActive,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
      ) {
        Column {
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = {
              Text(
                text = "Search by location, villa, store code, worker phone, or owner...",
                fontSize = 13.sp,
                color = Slate400
              )
            },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = ZawitcoBlue,
                modifier = Modifier.size(18.dp)
              )
            },
            trailingIcon = {
              if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { onSearchQueryChange("") }) {
                  Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear",
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = Color.Black,
              unfocusedTextColor = Color.Black,
              focusedBorderColor = ZawitcoBlue,
              unfocusedBorderColor = Slate200,
              focusedContainerColor = Color(0xFFFAFAFA),
              unfocusedContainerColor = Color(0xFFFAFAFA)
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("header_expandable_search_input")
          )
        }
      }
    }
  }
}
