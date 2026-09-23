package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Accommodation
import com.example.data.model.AccommodationReport
import com.example.ui.theme.DarkGreen
import com.example.ui.theme.LightGreen
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.ZawitcoBlue
import com.example.ui.theme.ZawitcoLightBlue
import com.example.ui.theme.ZawitcoOrange

val issueCategories = listOf(
  "AC / Cooling",
  "Plumbing / Water",
  "Electrical / Power",
  "Door & Lock",
  "Appliance Broken",
  "Cleaning / Pest",
  "Other"
)

@Composable
fun ReportIssueDialog(
  accommodation: Accommodation,
  reports: List<AccommodationReport>,
  isAdmin: Boolean,
  onDismiss: () -> Unit,
  onAddReport: (
    accommodationId: Long,
    category: String,
    title: String,
    description: String,
    severity: String,
    reportedBy: String,
    reporterPhone: String
  ) -> Unit,
  onUpdateStatus: (AccommodationReport, String) -> Unit,
  onDeleteReport: (AccommodationReport) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var showAddForm by remember { mutableStateOf(false) }

  var selectedCategory by remember { mutableStateOf(issueCategories[0]) }
  var title by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var severity by remember { mutableStateOf("Medium") }
  var reportedBy by remember { mutableStateOf("") }
  var reporterPhone by remember { mutableStateOf("") }
  var titleError by remember { mutableStateOf(false) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.92f)
        .clip(RoundedCornerShape(24.dp)),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 8.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color(0xFFFEE2E2)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.ReportProblem,
                contentDescription = null,
                tint = Color(0xFFDC2626),
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Report Accommodation Issue",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF991B1B)
              )
              Text(
                text = "${accommodation.areaName} • Villa ${accommodation.villaNumber.ifBlank { "N/A" }}",
                fontSize = 12.sp,
                color = Slate600
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_report_issue_dialog")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = Slate600
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Active Notification summary badge
        val openReportsCount = reports.count { it.status != "Resolved" }
        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          color = if (openReportsCount > 0) Color(0xFFFFF1F2) else Slate100,
          border = BorderStroke(1.dp, if (openReportsCount > 0) Color(0xFFFECDD3) else Slate200)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.NotificationsActive,
                contentDescription = null,
                tint = if (openReportsCount > 0) Color(0xFFE11D48) else Slate500,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (openReportsCount > 0) "$openReportsCount active issue(s) under review" else "No open issues reported",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (openReportsCount > 0) Color(0xFF9F1239) else Slate700
              )
            }

            Button(
              onClick = { showAddForm = !showAddForm },
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFDC2626),
                contentColor = Color.White
              ),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.testTag("toggle_add_report_form_button")
            ) {
              Icon(
                imageVector = if (showAddForm) Icons.Default.Close else Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (showAddForm) "Cancel" else "Report Issue",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Form to add a new report
        if (showAddForm) {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Slate100),
            border = BorderStroke(1.dp, Slate200)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
            ) {
              Text(
                text = "Submit Maintenance / Issue Report",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF991B1B)
              )

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = "Select Category:",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Slate600
              )
              Spacer(modifier = Modifier.height(6.dp))
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                issueCategories.forEach { category ->
                  val isSelected = selectedCategory == category
                  Surface(
                    onClick = { selectedCategory = category },
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) Color(0xFFFEE2E2) else Color.White,
                    border = BorderStroke(1.dp, if (isSelected) Color(0xFFEF4444) else Slate200)
                  ) {
                    Text(
                      text = category,
                      fontSize = 11.sp,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                      color = if (isSelected) Color(0xFFB91C1C) else Slate700,
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              OutlinedTextField(
                value = title,
                onValueChange = {
                  title = it
                  if (it.isNotBlank()) titleError = false
                },
                label = { Text("Issue Title *") },
                placeholder = { Text("e.g. Water leak in bathroom, AC not cooling...") },
                isError = titleError,
                supportingText = if (titleError) {
                  { Text("Issue title is required") }
                } else null,
                singleLine = true,
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("input_report_title")
              )

              Spacer(modifier = Modifier.height(8.dp))

              OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description & Location within Accommodation") },
                placeholder = { Text("e.g. Room 202, under sink tap pipe is leaking water") },
                maxLines = 3,
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("input_report_description")
              )

              Spacer(modifier = Modifier.height(8.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                OutlinedTextField(
                  value = reportedBy,
                  onValueChange = { reportedBy = it },
                  label = { Text("Your Name / Room") },
                  placeholder = { Text("e.g. Tariq / 102") },
                  singleLine = true,
                  modifier = Modifier
                    .weight(1f)
                    .testTag("input_report_reporter_name")
                )

                OutlinedTextField(
                  value = reporterPhone,
                  onValueChange = { reporterPhone = it },
                  label = { Text("Phone Number") },
                  placeholder = { Text("+966 5...") },
                  singleLine = true,
                  modifier = Modifier
                    .weight(1f)
                    .testTag("input_report_reporter_phone")
                )
              }

              Spacer(modifier = Modifier.height(12.dp))

              Button(
                onClick = {
                  if (title.isBlank()) {
                    titleError = true
                    return@Button
                  }
                  onAddReport(
                    accommodation.id,
                    selectedCategory,
                    title.trim(),
                    description.trim(),
                    severity,
                    reportedBy.trim(),
                    reporterPhone.trim()
                  )
                  Toast.makeText(context, "Issue reported and notified to administration!", Toast.LENGTH_SHORT).show()
                  title = ""
                  description = ""
                  reportedBy = ""
                  reporterPhone = ""
                  showAddForm = false
                },
                colors = ButtonDefaults.buttonColors(
                  containerColor = Color(0xFFDC2626),
                  contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(44.dp)
                  .testTag("submit_report_button")
              ) {
                Icon(imageVector = Icons.Outlined.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Submit Report", fontWeight = FontWeight.Bold)
              }
            }
          }
          Spacer(modifier = Modifier.height(14.dp))
        }

        // Reports List
        if (reports.isEmpty()) {
          Box(
            modifier = Modifier
              .weight(1f)
              .fillMaxWidth(),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Default.ReportProblem,
                contentDescription = null,
                tint = Slate400,
                modifier = Modifier.size(40.dp)
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "No issues reported for this accommodation.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Slate500
              )
              Text(
                text = "If any maintenance is needed, tap 'Report Issue' above.",
                fontSize = 12.sp,
                color = Slate400
              )
            }
          }
        } else {
          LazyColumn(
            modifier = Modifier
              .weight(1f)
              .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(reports, key = { it.id }) { report ->
              val isResolved = report.status == "Resolved"

              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("report_item_${report.id}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                  containerColor = if (isResolved) Slate100 else Color.White
                ),
                border = BorderStroke(1.dp, if (isResolved) Slate200 else Color(0xFFFECDD3))
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = report.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (isResolved) Slate500 else Color(0xFF991B1B)
                      )
                      Spacer(modifier = Modifier.width(8.dp))
                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(6.dp))
                          .background(Color(0xFFFEE2E2))
                          .padding(horizontal = 6.dp, vertical = 2.dp)
                      ) {
                        Text(
                          text = report.issueCategory,
                          fontSize = 10.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color(0xFFB91C1C)
                        )
                      }
                    }

                    if (report.description.isNotBlank()) {
                      Text(
                        text = report.description,
                        fontSize = 12.sp,
                        color = Slate600
                      )
                    }

                    if (report.reportedBy.isNotBlank()) {
                      Text(
                        text = "Reported by: ${report.reportedBy} ${if (report.reporterPhone.isNotBlank()) "(${report.reporterPhone})" else ""}",
                        fontSize = 11.sp,
                        color = Slate500
                      )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = "Status: ${report.status}",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.SemiBold,
                      color = if (isResolved) DarkGreen else Color(0xFFE11D48)
                    )
                  }

                  // Action Buttons
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!isResolved) {
                      IconButton(
                        onClick = { onUpdateStatus(report, "Resolved") },
                        modifier = Modifier
                          .size(34.dp)
                          .background(LightGreen, CircleShape)
                          .testTag("resolve_report_button_${report.id}")
                      ) {
                        Icon(
                          imageVector = Icons.Default.CheckCircle,
                          contentDescription = "Mark as Resolved",
                          tint = DarkGreen,
                          modifier = Modifier.size(18.dp)
                        )
                      }
                    }

                    if (isAdmin) {
                      Spacer(modifier = Modifier.width(6.dp))
                      IconButton(
                        onClick = { onDeleteReport(report) },
                        modifier = Modifier
                          .size(34.dp)
                          .testTag("delete_report_button_${report.id}")
                      ) {
                        Icon(
                          imageVector = Icons.Default.Delete,
                          contentDescription = "Delete",
                          tint = Color(0xFFEF4444),
                          modifier = Modifier.size(18.dp)
                        )
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
