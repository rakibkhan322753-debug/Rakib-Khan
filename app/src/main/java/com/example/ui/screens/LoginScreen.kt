package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.DarkGreen
import com.example.ui.theme.LightGreen
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.ZawitcoBlue
import com.example.ui.theme.ZawitcoLightBlue
import com.example.ui.theme.ZawitcoOrange
import com.example.ui.viewmodel.AccommodationViewModel
import com.example.ui.viewmodel.LoginResult
import com.example.ui.viewmodel.UserRole

enum class LoginTab {
  USER_VIEWER,
  ADMIN
}

@Composable
fun LoginScreen(
  viewModel: AccommodationViewModel,
  onLoginSuccess: (UserRole) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var selectedTab by remember { mutableStateOf(LoginTab.USER_VIEWER) }
  var passwordInput by remember { mutableStateOf("") }
  var isPasswordVisible by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  fun executeLogin(passwordToTry: String) {
    errorMessage = null
    when (val result = viewModel.login(passwordToTry)) {
      is LoginResult.Success -> {
        val roleDesc = if (result.role == UserRole.ADMIN) "Administrator (Full Access)" else "User (Viewer Mode)"
        Toast.makeText(context, "Welcome! Signed in as $roleDesc", Toast.LENGTH_SHORT).show()
        onLoginSuccess(result.role)
      }
      is LoginResult.Error -> {
        errorMessage = result.message
      }
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            Color(0xFFF0F7FD),
            Color(0xFFFAFCFF),
            Color(0xFFFFFFFF)
          )
        )
      )
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.statusBars)
        .windowInsetsPadding(WindowInsets.navigationBars)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, vertical = 20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {

      Spacer(modifier = Modifier.height(10.dp))

      // ==========================================
      // CORPORATE BRANDING HEADER WITH UPLOADED LOGO
      // ==========================================
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .shadow(elevation = 6.dp, shape = RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200.copy(alpha = 0.8f))
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Uploaded Zawitco Company Logo Ribbon & Typography
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(84.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(Color.White)
              .padding(4.dp),
            contentAlignment = Alignment.Center
          ) {
            Image(
              painter = painterResource(id = R.drawable.zawitco_company_logo_1788754962037),
              contentDescription = "Zawitco Company Logo",
              contentScale = ContentScale.Fit,
              modifier = Modifier.fillMaxSize()
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Corporate Arabic & English Titles
          Row(
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "زاوية كو",
              fontSize = 21.sp,
              fontWeight = FontWeight.Black,
              color = ZawitcoBlue
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "•",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = ZawitcoOrange
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "ZAWITCO",
              fontSize = 21.sp,
              fontWeight = FontWeight.Black,
              color = ZawitcoBlue,
              letterSpacing = 1.sp
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "ACCOMMODATION & HOUSING PORTAL",
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color = ZawitcoOrange,
            letterSpacing = 1.2.sp
          )

          Spacer(modifier = Modifier.height(2.dp))

          Text(
            text = "نظام إدارة وتسكين موظفي الشركة والعقارات",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Slate600
          )
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // ==========================================
      // ROLE ACCESS TABS (Viewer vs Admin)
      // ==========================================
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp)),
        color = Slate100,
        shape = RoundedCornerShape(16.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          // Tab 1: User / Viewer
          val isViewerSelected = selectedTab == LoginTab.USER_VIEWER
          Surface(
            modifier = Modifier
              .weight(1f)
              .height(46.dp)
              .clip(RoundedCornerShape(12.dp))
              .clickable {
                selectedTab = LoginTab.USER_VIEWER
                errorMessage = null
              }
              .testTag("tab_user_viewer"),
            color = if (isViewerSelected) Color.White else Color.Transparent,
            shape = RoundedCornerShape(12.dp),
            shadowElevation = if (isViewerSelected) 2.dp else 0.dp
          ) {
            Row(
              modifier = Modifier.fillMaxSize(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = Icons.Outlined.Visibility,
                contentDescription = null,
                tint = if (isViewerSelected) ZawitcoBlue else Slate500,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "User (Viewer)",
                fontSize = 13.sp,
                fontWeight = if (isViewerSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isViewerSelected) ZawitcoBlue else Slate600
              )
            }
          }

          // Tab 2: Administrator
          val isAdminSelected = selectedTab == LoginTab.ADMIN
          Surface(
            modifier = Modifier
              .weight(1f)
              .height(46.dp)
              .clip(RoundedCornerShape(12.dp))
              .clickable {
                selectedTab = LoginTab.ADMIN
                errorMessage = null
              }
              .testTag("tab_admin"),
            color = if (isAdminSelected) Color.White else Color.Transparent,
            shape = RoundedCornerShape(12.dp),
            shadowElevation = if (isAdminSelected) 2.dp else 0.dp
          ) {
            Row(
              modifier = Modifier.fillMaxSize(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = Icons.Outlined.AdminPanelSettings,
                contentDescription = null,
                tint = if (isAdminSelected) ZawitcoOrange else Slate500,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Admin",
                fontSize = 13.sp,
                fontWeight = if (isAdminSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isAdminSelected) ZawitcoOrange else Slate600
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Tab description banner
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (selectedTab == LoginTab.ADMIN) Color(0xFFFFF4EC) else ZawitcoLightBlue
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = if (selectedTab == LoginTab.ADMIN) Icons.Default.Security else Icons.Default.Info,
            contentDescription = null,
            tint = if (selectedTab == LoginTab.ADMIN) ZawitcoOrange else ZawitcoBlue,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = if (selectedTab == LoginTab.ADMIN) {
              "Administrator Mode: Full control to add, edit, and delete staff accommodations."
            } else {
              "Viewer Mode: Read-only access to browse housing, view contacts, and GPS navigation."
            },
            fontSize = 12.sp,
            color = if (selectedTab == LoginTab.ADMIN) Color(0xFF9A3412) else Color(0xFF0C4A6E),
            lineHeight = 16.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // ==========================================
      // PASSWORD INPUT FIELD
      // ==========================================
      OutlinedTextField(
        value = passwordInput,
        onValueChange = {
          passwordInput = it
          errorMessage = null
        },
        label = {
          Text(
            text = if (selectedTab == LoginTab.ADMIN) "Admin Password (Passkey: 322753)" else "User Password (Passcode: Zawitco)"
          )
        },
        placeholder = {
          Text(
            text = if (selectedTab == LoginTab.ADMIN) "Enter 322753" else "Enter Zawitco",
            color = Slate400
          )
        },
        leadingIcon = {
          Icon(
            imageVector = if (selectedTab == LoginTab.ADMIN) Icons.Default.Key else Icons.Default.Lock,
            contentDescription = null,
            tint = if (selectedTab == LoginTab.ADMIN) ZawitcoOrange else ZawitcoBlue
          )
        },
        trailingIcon = {
          IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
            Icon(
              imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.RemoveRedEye,
              contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
              tint = Slate500
            )
          }
        },
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
          keyboardType = if (selectedTab == LoginTab.ADMIN) KeyboardType.NumberPassword else KeyboardType.Password,
          imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
          onDone = { executeLogin(passwordInput) }
        ),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = if (selectedTab == LoginTab.ADMIN) ZawitcoOrange else ZawitcoBlue,
          focusedLabelColor = if (selectedTab == LoginTab.ADMIN) ZawitcoOrange else ZawitcoBlue,
          unfocusedBorderColor = Slate200,
          focusedContainerColor = Color.White,
          unfocusedContainerColor = Color.White
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("login_password_input")
      )

      // Error message display
      AnimatedVisibility(
        visible = errorMessage != null,
        enter = fadeIn(),
        exit = fadeOut()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
        ) {
          Text(
            text = errorMessage ?: "",
            color = Color(0xFFDC2626),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // ==========================================
      // MAIN SIGN IN BUTTON
      // ==========================================
      Button(
        onClick = { executeLogin(passwordInput) },
        colors = ButtonDefaults.buttonColors(
          containerColor = if (selectedTab == LoginTab.ADMIN) ZawitcoOrange else ZawitcoBlue,
          contentColor = Color.White
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("login_submit_button")
      ) {
        Text(
          text = if (selectedTab == LoginTab.ADMIN) "Log In as Administrator" else "Log In as User (Viewer)",
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowForward,
          contentDescription = null,
          modifier = Modifier.size(18.dp)
        )
      }

      Spacer(modifier = Modifier.height(18.dp))

      // ==========================================
      // QUICK ONE-TAP TEST CHIPS
      // ==========================================
      Text(
        text = "Quick Access Credentials:",
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = Slate500
      )

      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Quick User Button
        Surface(
          onClick = {
            selectedTab = LoginTab.USER_VIEWER
            passwordInput = "Zawitco"
            executeLogin("Zawitco")
          },
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFFF1F5F9),
          border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
          modifier = Modifier
            .weight(1f)
            .testTag("quick_login_user")
        ) {
          Row(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Outlined.Visibility,
              contentDescription = null,
              tint = ZawitcoBlue,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
              Text(
                text = "User (Viewer)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Slate800
              )
              Text(
                text = "Pass: Zawitco",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = ZawitcoBlue
              )
            }
          }
        }

        // Quick Admin Button
        Surface(
          onClick = {
            selectedTab = LoginTab.ADMIN
            passwordInput = "322753"
            executeLogin("322753")
          },
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFFFFF7ED),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFED7AA)),
          modifier = Modifier
            .weight(1f)
            .testTag("quick_login_admin")
        ) {
          Row(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Default.Security,
              contentDescription = null,
              tint = ZawitcoOrange,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
              Text(
                text = "Admin (Full)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Slate800
              )
              Text(
                text = "Pass: 322753",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = ZawitcoOrange
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // ==========================================
      // SECURITY PRIVILEGES SUMMARY CARD
      // ==========================================
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Security,
              contentDescription = null,
              tint = ZawitcoBlue,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Role-Based Security Policy",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = Slate800
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Row(verticalAlignment = Alignment.Top) {
            Text("• ", fontWeight = FontWeight.Black, color = ZawitcoOrange)
            Text(
              text = "Admin (322753): Full managerial authorization to add, update, delete listings, and update building WhatsApp URLs.",
              fontSize = 11.sp,
              color = Slate600,
              lineHeight = 15.sp
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Row(verticalAlignment = Alignment.Top) {
            Text("• ", fontWeight = FontWeight.Black, color = ZawitcoBlue)
            Text(
              text = "User (Zawitco): Read-only viewer privileges to search accommodations, sort by GPS proximity, view rooms, and contact staff.",
              fontSize = 11.sp,
              color = Slate600,
              lineHeight = 15.sp
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Corporate Footer
      Text(
        text = "Zawitco Real Estate & Staff Logistics Services",
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = Slate500
      )
      Text(
        text = "المملكة العربية السعودية • شركة زاوية كو",
        fontSize = 10.sp,
        fontWeight = FontWeight.Normal,
        color = Slate400
      )

      Spacer(modifier = Modifier.height(10.dp))
    }
  }
}
