package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.theme.DarkGreen
import com.example.ui.theme.LightGreen
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.ZawitcoBlue
import com.example.ui.theme.ZawitcoLightBlue
import com.example.ui.theme.ZawitcoOrange

private val WhatsAppColor = Color(0xFF25D366)

@Composable
fun HeaderBar(
  isAdmin: Boolean,
  onAdminClick: () -> Unit,
  onWhatsAppClick: () -> Unit,
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
          height = 38.dp,
          showSubtext = true
        )

        // Action controls: WhatsApp link, Admin status/toggle, Add button, and Logout button
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          // WhatsApp Group Community Button
          IconButton(
            onClick = onWhatsAppClick,
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(Color(0xFFE8F8F0))
              .testTag("header_whatsapp_button")
          ) {
            Icon(
              imageVector = Icons.Default.Group,
              contentDescription = "WhatsApp Group Link",
              tint = WhatsAppColor,
              modifier = Modifier.size(19.dp)
            )
          }

          // Role Status Badge (Admin or Viewer)
          Surface(
            onClick = onAdminClick,
            shape = RoundedCornerShape(10.dp),
            color = if (isAdmin) LightGreen else ZawitcoLightBlue,
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              if (isAdmin) DarkGreen.copy(alpha = 0.3f) else ZawitcoBlue.copy(alpha = 0.3f)
            ),
            modifier = Modifier.testTag("header_admin_mode_button")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = if (isAdmin) Icons.Default.LockOpen else Icons.Outlined.Visibility,
                contentDescription = if (isAdmin) "Admin Access Enabled" else "Viewer Mode",
                tint = if (isAdmin) DarkGreen else ZawitcoBlue,
                modifier = Modifier.size(15.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (isAdmin) "Admin" else "Viewer",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isAdmin) DarkGreen else ZawitcoBlue
              )
            }
          }

          // "Add New" button - Full CRUD only for admin
          Button(
            onClick = onAddNewClick,
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isAdmin) ZawitcoOrange else Slate200,
              contentColor = if (isAdmin) Color.White else Slate600
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .height(36.dp)
              .testTag("add_new_accommodation_button")
          ) {
            Icon(
              imageVector = if (isAdmin) Icons.Default.Add else Icons.Default.Lock,
              contentDescription = "Add New Accommodation",
              modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "Add",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }

          // Logout Button: Return to Login Screen
          IconButton(
            onClick = onLogout,
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(Slate100)
              .testTag("header_logout_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Logout,
              contentDescription = "Log Out to Login Interface",
              tint = Slate500,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }
  }
}
