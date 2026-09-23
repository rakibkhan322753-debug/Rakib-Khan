package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate800
import com.example.ui.theme.ZawitcoBlue
import com.example.ui.theme.ZawitcoDarkBlue
import com.example.ui.theme.ZawitcoLightBlue
import com.example.ui.theme.ZawitcoOrange
import com.example.ui.viewmodel.AccommodationViewModel
import com.example.ui.viewmodel.LoginResult
import com.example.ui.viewmodel.UserRole
import kotlinx.coroutines.launch

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
  val coroutineScope = rememberCoroutineScope()

  var selectedTab by remember { mutableStateOf(LoginTab.USER_VIEWER) }
  var usernameInput by remember { mutableStateOf("") }
  var passwordInput by remember { mutableStateOf("") }
  var isPasswordVisible by remember { mutableStateOf(false) }
  var isSubmitting by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  fun executeLogin() {
    errorMessage = null
    isSubmitting = true
    coroutineScope.launch {
      val result = if (selectedTab == LoginTab.ADMIN) {
        viewModel.loginWithCredentials(usernameInput.ifBlank { "admin" }, passwordInput)
      } else {
        viewModel.loginWithCredentials(usernameInput, passwordInput)
      }
      isSubmitting = false

      when (result) {
        is LoginResult.Success -> {
          val roleDesc = if (result.role == UserRole.ADMIN) "Administrator (${result.fullName})" else "User (${result.fullName})"
          Toast.makeText(context, "Welcome! Signed in as $roleDesc", Toast.LENGTH_SHORT).show()
          onLoginSuccess(result.role)
        }
        is LoginResult.Error -> {
          errorMessage = result.message
        }
      }
    }
  }

  // Background matching the Zawitco Logo colors (Deep Corporate Zawitco Blue gradient)
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            Color(0xFF002B4D), // Deep Royal Navy
            ZawitcoDarkBlue,   // Zawitco Dark Blue
            ZawitcoBlue        // Zawitco Primary Brand Blue
          )
        )
      )
  ) {
    // Subtle decorative brand accent circles
    Box(
      modifier = Modifier
        .align(Alignment.TopEnd)
        .size(240.dp)
        .background(
          brush = Brush.radialGradient(
            colors = listOf(
              ZawitcoOrange.copy(alpha = 0.22f),
              Color.Transparent
            )
          ),
          shape = CircleShape
        )
    )

    Box(
      modifier = Modifier
        .align(Alignment.BottomStart)
        .size(280.dp)
        .background(
          brush = Brush.radialGradient(
            colors = listOf(
              ZawitcoLightBlue.copy(alpha = 0.15f),
              Color.Transparent
            )
          ),
          shape = CircleShape
        )
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.statusBars)
        .windowInsetsPadding(WindowInsets.navigationBars)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 22.dp, vertical = 20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {

      Spacer(modifier = Modifier.height(12.dp))

      // CORPORATE BRANDING HEADER MATCHING ZAWITCO LOGO
      Box(
        modifier = Modifier
          .fillMaxWidth(0.85f)
          .height(90.dp)
          .clip(RoundedCornerShape(18.dp))
          .background(Color.White)
          .border(1.5.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(18.dp))
          .shadow(elevation = 12.dp, shape = RoundedCornerShape(18.dp))
          .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
      ) {
        Image(
          painter = painterResource(id = R.drawable.zawitco_company_logo_1788754962037),
          contentDescription = "Zawitco Company Logo",
          contentScale = ContentScale.Fit,
          modifier = Modifier.fillMaxSize()
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Corporate Arabic & English Titles
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "زاوية كو",
          fontSize = 22.sp,
          fontWeight = FontWeight.Black,
          color = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "•",
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
          color = ZawitcoOrange
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "ZAWITCO",
          fontSize = 22.sp,
          fontWeight = FontWeight.Black,
          color = Color.White,
          letterSpacing = 1.sp
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "ACCOMMODATION & HOUSING PORTAL",
        fontSize = 12.sp,
        fontWeight = FontWeight.ExtraBold,
        color = ZawitcoOrange,
        letterSpacing = 1.3.sp
      )

      Spacer(modifier = Modifier.height(3.dp))

      Text(
        text = "نظام إدارة وتسكين موظفي الشركة والعقارات",
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFFD7E8F7)
      )

      Spacer(modifier = Modifier.height(20.dp))

      // MAIN LOGIN CARD
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .shadow(elevation = 16.dp, shape = RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(22.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {

          // ROLE ACCESS TABS (User vs Admin)
          Surface(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp)),
            color = Color(0xFFF1F5F9),
            shape = RoundedCornerShape(16.dp)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              // Tab 1: User Sign In
              val isViewerSelected = selectedTab == LoginTab.USER_VIEWER
              Surface(
                onClick = {
                  selectedTab = LoginTab.USER_VIEWER
                  errorMessage = null
                },
                modifier = Modifier
                  .weight(1f)
                  .height(46.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .testTag("tab_user_viewer"),
                color = if (isViewerSelected) Color.White else Color.Transparent,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = if (isViewerSelected) 3.dp else 0.dp
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
                    text = "Staff / User",
                    fontSize = 13.sp,
                    fontWeight = if (isViewerSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isViewerSelected) ZawitcoBlue else Color.Black
                  )
                }
              }

              // Tab 2: Administrator
              val isAdminSelected = selectedTab == LoginTab.ADMIN
              Surface(
                onClick = {
                  selectedTab = LoginTab.ADMIN
                  errorMessage = null
                },
                modifier = Modifier
                  .weight(1f)
                  .height(46.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .testTag("tab_admin"),
                color = if (isAdminSelected) Color.White else Color.Transparent,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = if (isAdminSelected) 3.dp else 0.dp
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
                    text = "Administrator",
                    fontSize = 13.sp,
                    fontWeight = if (isAdminSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isAdminSelected) ZawitcoOrange else Color.Black
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
                  "Admin Portal: Full CRUD control, station manager, bulk uploads, user creation."
                } else {
                  "Staff Portal: Access housing directory, station routes, maintenance & requirements."
                },
                fontSize = 12.sp,
                color = if (selectedTab == LoginTab.ADMIN) Color(0xFF9A3412) else Color(0xFF0C4A6E),
                lineHeight = 16.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // USERNAME INPUT FIELD (Only for user or optional for admin)
          if (selectedTab == LoginTab.USER_VIEWER) {
            OutlinedTextField(
              value = usernameInput,
              onValueChange = {
                usernameInput = it
                errorMessage = null
              },
              label = {
                Text(
                  text = "Username (Staff ID / Name)",
                  color = Color.Black
                )
              },
              placeholder = {
                Text(
                  text = "Enter username (e.g. staff1)",
                  color = Slate400
                )
              },
              leadingIcon = {
                Icon(
                  imageVector = Icons.Default.Person,
                  contentDescription = null,
                  tint = ZawitcoBlue
                )
              },
              singleLine = true,
              shape = RoundedCornerShape(14.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = ZawitcoBlue,
                unfocusedBorderColor = Slate200,
                focusedContainerColor = Color(0xFFF8FAFC),
                unfocusedContainerColor = Color(0xFFF8FAFC)
              ),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("login_username_input")
            )

            Spacer(modifier = Modifier.height(10.dp))
          }

          // PASSWORD INPUT FIELD (NO HINT PASSWORDS SHOWN)
          OutlinedTextField(
            value = passwordInput,
            onValueChange = {
              passwordInput = it
              errorMessage = null
            },
            label = {
              Text(
                text = if (selectedTab == LoginTab.ADMIN) "Admin Password / PIN" else "Password",
                color = Color.Black
              )
            },
            placeholder = {
              Text(
                text = "Enter password",
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
              onDone = { executeLogin() }
            ),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedTextColor = Color.Black,
              unfocusedTextColor = Color.Black,
              focusedBorderColor = if (selectedTab == LoginTab.ADMIN) ZawitcoOrange else ZawitcoBlue,
              focusedLabelColor = if (selectedTab == LoginTab.ADMIN) ZawitcoOrange else ZawitcoBlue,
              unfocusedBorderColor = Slate200,
              focusedContainerColor = Color(0xFFF8FAFC),
              unfocusedContainerColor = Color(0xFFF8FAFC)
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

          // MAIN SIGN IN BUTTON
          Button(
            onClick = { executeLogin() },
            enabled = !isSubmitting,
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
            if (isSubmitting) {
              CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text("Authenticating...", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
            } else {
              Text(
                text = if (selectedTab == LoginTab.ADMIN) "Sign In as Administrator" else "Sign In to Housing Portal",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.White
              )
              Spacer(modifier = Modifier.width(8.dp))
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(18.dp))

          // Security Policy Information
          Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFFF8FAFC),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Security,
                  contentDescription = null,
                  tint = ZawitcoBlue,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Security & Authorization",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.Black
                )
              }

              Spacer(modifier = Modifier.height(6.dp))

              Row(verticalAlignment = Alignment.Top) {
                Text("• ", fontWeight = FontWeight.Black, color = ZawitcoOrange, fontSize = 12.sp)
                Text(
                  text = "Admin Accounts: Full authority to create user logins, upload bulk records, and manage stations.",
                  fontSize = 11.sp,
                  color = Color.Black,
                  lineHeight = 15.sp
                )
              }

              Spacer(modifier = Modifier.height(5.dp))

              Row(verticalAlignment = Alignment.Top) {
                Text("• ", fontWeight = FontWeight.Black, color = ZawitcoBlue, fontSize = 12.sp)
                Text(
                  text = "User Accounts: Created and managed strictly by Administrators for authorized company staff.",
                  fontSize = 11.sp,
                  color = Color.Black,
                  lineHeight = 15.sp
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Corporate Footer
      Text(
        text = "Zawitco Real Estate & Staff Logistics Services",
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.White.copy(alpha = 0.9f)
      )
      Text(
        text = "المملكة العربية السعودية • شركة زاوية كو",
        fontSize = 10.sp,
        fontWeight = FontWeight.Normal,
        color = Color(0xFFBDD9F2)
      )

      Spacer(modifier = Modifier.height(12.dp))
    }
  }
}
