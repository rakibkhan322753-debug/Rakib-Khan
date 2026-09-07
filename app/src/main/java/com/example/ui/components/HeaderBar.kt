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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Security
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
import com.example.ui.theme.Slate200
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
        .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Official Zawitco Company Logo & Brand Name
        ZawitcoCompanyLogo(
          height = 42.dp,
          showSubtext = true
        )

        // Action controls: WhatsApp link, Admin toggle, and Add button
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // WhatsApp Group Community Button
          IconButton(
            onClick = onWhatsAppClick,
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(Color(0xFFE8F8F0))
              .testTag("header_whatsapp_button")
          ) {
            Icon(
              imageVector = Icons.Default.Group,
              contentDescription = "WhatsApp Group Link",
              tint = WhatsAppColor,
              modifier = Modifier.size(20.dp)
            )
          }

          // Admin Access Status Badge / Button
          Surface(
            onClick = onAdminClick,
            shape = RoundedCornerShape(12.dp),
            color = if (isAdmin) LightGreen else ZawitcoLightBlue,
            modifier = Modifier.testTag("header_admin_mode_button")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = if (isAdmin) Icons.Default.LockOpen else Icons.Default.Security,
                contentDescription = if (isAdmin) "Admin Access Enabled" else "Admin Login",
                tint = if (isAdmin) DarkGreen else ZawitcoBlue,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (isAdmin) "Admin" else "Login",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isAdmin) DarkGreen else ZawitcoBlue
              )
            }
          }

          // "Add New" button - Admin full access only
          Button(
            onClick = onAddNewClick,
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isAdmin) ZawitcoOrange else Slate200,
              contentColor = if (isAdmin) Color.White else Slate600
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.testTag("add_new_accommodation_button")
          ) {
            Icon(
              imageVector = if (isAdmin) Icons.Default.Add else Icons.Default.Lock,
              contentDescription = "Add New Accommodation",
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Add",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }
        }
      }
    }
  }
}
