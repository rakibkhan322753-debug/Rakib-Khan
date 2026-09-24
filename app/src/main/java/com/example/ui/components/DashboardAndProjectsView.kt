package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Accommodation
import com.example.data.model.AuditLog
import com.example.data.model.ProjectProgressSummary
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
import com.example.ui.theme.ZawitcoDarkOrange
import com.example.ui.theme.ZawitcoLightBlue
import com.example.ui.theme.ZawitcoOrange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardAndProjectsView(
  projects: List<ProjectProgressSummary>,
  auditLogs: List<AuditLog>,
  isAdmin: Boolean,
  onSelectAccommodation: (Accommodation) -> Unit,
  onFilterByProject: (String) -> Unit,
  onClearAuditLogs: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  var selectedProjectFilter by remember { mutableStateOf<String?>(null) }
  var auditSearchText by remember { mutableStateOf("") }
  var selectedAuditCategory by remember { mutableStateOf("ALL") }

  Column(modifier = modifier.fillMaxSize()) {
    // Top Tabs: Projects Analysis & Admin Audit Logs (visible only if Admin)
    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = Color.White,
      contentColor = ZawitcoBlue,
      indicator = { tabPositions ->
        if (selectedTab < tabPositions.size) {
          TabRowDefaults.SecondaryIndicator(
            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
            color = ZawitcoBlue,
            height = 3.dp
          )
        }
      }
    ) {
      Tab(
        selected = selectedTab == 0,
        onClick = { selectedTab = 0 },
        text = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.TrendingUp,
              contentDescription = null,
              modifier = Modifier.size(16.dp),
              tint = if (selectedTab == 0) ZawitcoBlue else Slate500
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Projects & Products Progress",
              fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
              fontSize = 13.sp
            )
          }
        },
        modifier = Modifier.testTag("tab_projects_progress")
      )

      if (isAdmin) {
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (selectedTab == 1) Color(0xFFDC2626) else Slate500
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "🔒 Admin Audit Log",
                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                fontSize = 13.sp,
                color = if (selectedTab == 1) Color(0xFFDC2626) else Slate600
              )
            }
          },
          modifier = Modifier.testTag("tab_admin_audit_log")
        )
      }
    }

    if (selectedTab == 0) {
      // PROJECTS & PRODUCTS PROGRESS DASHBOARD
      ProjectsDashboardContent(
        projects = projects,
        selectedProjectFilter = selectedProjectFilter,
        onSelectProjectFilter = { selectedProjectFilter = it },
        onSelectAccommodation = onSelectAccommodation,
        onFilterByProject = onFilterByProject
      )
    } else if (isAdmin) {
      // SECURE ADMIN AUDIT LOG VIEW
      AdminAuditLogsContent(
        logs = auditLogs,
        searchQuery = auditSearchText,
        onSearchChange = { auditSearchText = it },
        categoryFilter = selectedAuditCategory,
        onCategoryFilterChange = { selectedAuditCategory = it },
        onClearLogs = onClearAuditLogs
      )
    }
  }
}

@Composable
private fun ProjectsDashboardContent(
  projects: List<ProjectProgressSummary>,
  selectedProjectFilter: String?,
  onSelectProjectFilter: (String?) -> Unit,
  onSelectAccommodation: (Accommodation) -> Unit,
  onFilterByProject: (String) -> Unit
) {
  val displayedProjects = if (selectedProjectFilter == null) {
    projects
  } else {
    projects.filter { it.projectName.equals(selectedProjectFilter, ignoreCase = true) }
  }

  val totalUnits = projects.sumOf { it.totalUnits }
  val totalWorkers = projects.sumOf { it.activeWorkers }
  val totalCapacity = projects.sumOf { it.totalCapacity }
  val totalRequirements = projects.sumOf { it.pendingRequirements }
  val totalReports = projects.sumOf { it.openReports }
  val overallAvgProgress = if (projects.isNotEmpty()) (projects.sumOf { it.progressPercentage } / projects.size) else 0

  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header Overview KPI Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(1.dp, Color(0xFF334155))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "ZAWITCO OPERATIONS DASHBOARD",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ZawitcoOrange,
                letterSpacing = 1.sp
              )
              Text(
                text = "Core Products & Projects Analysis",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFF1E293B)
            ) {
              Text(
                text = "4 Core Projects",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF93C5FD),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Holistic Readiness Metric
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Overall Operational Readiness Score",
              fontSize = 12.sp,
              color = Slate400
            )
            Text(
              text = "$overallAvgProgress%",
              fontSize = 15.sp,
              fontWeight = FontWeight.Black,
              color = Color(0xFF34D399)
            )
          }

          Spacer(modifier = Modifier.height(6.dp))
          LinearProgressIndicator(
            progress = { overallAvgProgress / 100f },
            modifier = Modifier
              .fillMaxWidth()
              .height(8.dp)
              .clip(RoundedCornerShape(4.dp)),
            color = Color(0xFF10B981),
            trackColor = Color(0xFF334155)
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Metrics Grid
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            KpiMetricBox(
              label = "Housing Units",
              value = "$totalUnits Units",
              subtext = "Active villas",
              modifier = Modifier.weight(1f)
            )
            KpiMetricBox(
              label = "Workforce",
              value = "$totalWorkers / $totalCapacity",
              subtext = if (totalCapacity > 0) "${(totalWorkers * 100) / totalCapacity}% capacity" else "0%",
              modifier = Modifier.weight(1.2f)
            )
            KpiMetricBox(
              label = "Open Issues",
              value = "${totalRequirements + totalReports}",
              subtext = "$totalRequirements req / $totalReports rep",
              valueColor = if (totalRequirements + totalReports > 0) Color(0xFFF87171) else Color(0xFF34D399),
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    }

    // Projects Selector Chips
    item {
      Column {
        Text(
          text = "FILTER BY STRATEGIC PROJECT",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = Slate600,
          letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          FilterChip(
            selected = selectedProjectFilter == null,
            onClick = { onSelectProjectFilter(null) },
            label = { Text("All 4 Projects (${projects.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = ZawitcoBlue,
              selectedLabelColor = Color.White
            )
          )
          projects.forEach { proj ->
            val isSel = selectedProjectFilter.equals(proj.projectName, ignoreCase = true)
            FilterChip(
              selected = isSel,
              onClick = {
                onSelectProjectFilter(if (isSel) null else proj.projectName)
              },
              label = {
                Text(
                  "${proj.projectName} (${proj.totalUnits})",
                  fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                  fontSize = 11.sp
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = Color(proj.themeColorHex),
                selectedLabelColor = Color.White
              )
            )
          }
        }
      }
    }

    // Project Cards
    items(displayedProjects, key = { it.projectName }) { project ->
      ProjectDetailCard(
        project = project,
        onSelectAccommodation = onSelectAccommodation,
        onFilterMainScreen = { onFilterByProject(project.projectName) }
      )
    }
  }
}

@Composable
private fun KpiMetricBox(
  label: String,
  value: String,
  subtext: String,
  valueColor: Color = Color.White,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(10.dp),
    color = Color(0xFF1E293B),
    border = BorderStroke(1.dp, Color(0xFF334155))
  ) {
    Column(
      modifier = Modifier.padding(10.dp),
      horizontalAlignment = Alignment.Start
    ) {
      Text(text = label, fontSize = 10.sp, color = Slate400)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Black, color = valueColor)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = subtext, fontSize = 9.sp, color = Slate500)
    }
  }
}

@Composable
private fun ProjectDetailCard(
  project: ProjectProgressSummary,
  onSelectAccommodation: (Accommodation) -> Unit,
  onFilterMainScreen: () -> Unit
) {
  var isExpanded by remember { mutableStateOf(false) }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("project_card_${project.projectCode}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = BorderStroke(1.dp, Color(project.themeColorHex).copy(alpha = 0.3f))
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header: Project Name, Code, and Status
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(Color(project.themeColorHex)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = project.projectCode.take(2),
              color = Color.White,
              fontWeight = FontWeight.Black,
              fontSize = 12.sp
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = project.projectName,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Slate900
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(project.themeColorHex).copy(alpha = 0.1f)
              ) {
                Text(
                  text = project.projectCode,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(project.themeColorHex),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
            Text(
              text = project.category,
              fontSize = 11.sp,
              color = Slate500,
              maxLines = 1
            )
          }
        }

        // Status Badge
        val statusBg = when (project.statusLabel) {
          "Optimal" -> LightGreen
          "On Track" -> Color(0xFFE0E7FF)
          "Needs Attention" -> Color(0xFFFEF3C7)
          else -> Color(0xFFFEE2E2)
        }
        val statusColor = when (project.statusLabel) {
          "Optimal" -> DarkGreen
          "On Track" -> Color(0xFF4338CA)
          "Needs Attention" -> Color(0xFFB45309)
          else -> Color(0xFFB91C1C)
        }
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = statusBg
        ) {
          Text(
            text = "${project.statusLabel} • ${project.progressPercentage}%",
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold,
            color = statusColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = project.description,
        fontSize = 12.sp,
        color = Slate600
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Progress Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = "Operational Progress & Readiness", fontSize = 11.sp, color = Slate500)
        Text(text = "${project.progressPercentage}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(project.themeColorHex))
      }
      Spacer(modifier = Modifier.height(4.dp))
      LinearProgressIndicator(
        progress = { project.progressPercentage / 100f },
        modifier = Modifier
          .fillMaxWidth()
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp)),
        color = Color(project.themeColorHex),
        trackColor = Slate100
      )

      Spacer(modifier = Modifier.height(14.dp))

      // 4 Metrics Grid
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        ProjectMetricItem(
          label = "Housing Units",
          value = "${project.totalUnits} Villas",
          color = ZawitcoBlue,
          modifier = Modifier.weight(1f)
        )
        ProjectMetricItem(
          label = "Staff Occupancy",
          value = "${project.activeWorkers} / ${project.totalCapacity} (${project.occupancyRate}%)",
          color = DarkGreen,
          modifier = Modifier.weight(1.3f)
        )
        ProjectMetricItem(
          label = "Requirements",
          value = "${project.pendingRequirements} Pending",
          color = if (project.pendingRequirements > 0) ZawitcoOrange else Slate500,
          modifier = Modifier.weight(1.1f)
        )
        ProjectMetricItem(
          label = "Maintenance",
          value = "${project.openReports} Open",
          color = if (project.openReports > 0) Color(0xFFDC2626) else DarkGreen,
          modifier = Modifier.weight(1f)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Action row: Expand assigned units & filter button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          onClick = { isExpanded = !isExpanded },
          shape = RoundedCornerShape(8.dp),
          color = Slate100
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (isExpanded) "Hide Accommodations" else "View ${project.accommodations.size} Housing Units",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Slate700
            )
            Icon(
              imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
              contentDescription = null,
              modifier = Modifier.size(14.dp),
              tint = Slate700
            )
          }
        }

        Surface(
          onClick = onFilterMainScreen,
          shape = RoundedCornerShape(8.dp),
          color = Color(project.themeColorHex).copy(alpha = 0.1f)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Filter on Home",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(project.themeColorHex)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = null,
              modifier = Modifier.size(12.dp),
              tint = Color(project.themeColorHex)
            )
          }
        }
      }

      // Expandable Accommodations List
      AnimatedVisibility(visible = isExpanded) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
        ) {
          if (project.accommodations.isEmpty()) {
            Text(
              text = "No accommodations assigned to this project yet. Add or assign a housing unit to '${project.projectName}'.",
              fontSize = 11.sp,
              color = Slate500,
              modifier = Modifier.padding(vertical = 8.dp)
            )
          } else {
            project.accommodations.forEach { acc ->
              Surface(
                onClick = { onSelectAccommodation(acc) },
                shape = RoundedCornerShape(8.dp),
                color = Slate50,
                border = BorderStroke(1.dp, Slate200),
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp)
              ) {
                Row(
                  modifier = Modifier.padding(10.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.Home,
                      contentDescription = null,
                      tint = Color(project.themeColorHex),
                      modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                      Text(
                        text = "${acc.areaName} - Villa ${acc.villaNumber.ifBlank { "N/A" }}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate800
                      )
                      Text(
                        text = "Store: ${acc.storeName.ifBlank { acc.storeCode.ifBlank { "N/A" } }}",
                        fontSize = 10.5.sp,
                        color = Slate500
                      )
                    }
                  }
                  Text(
                    text = "${acc.activeWorkers} workers",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkGreen
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

@Composable
private fun ProjectMetricItem(
  label: String,
  value: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(8.dp),
    color = Slate100
  ) {
    Column(
      modifier = Modifier.padding(8.dp),
      horizontalAlignment = Alignment.Start
    ) {
      Text(text = label, fontSize = 9.sp, color = Slate500)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color, maxLines = 1)
    }
  }
}

// ==========================================
// SECURE ADMIN AUDIT LOGS COMPONENT
// ==========================================
@Composable
private fun AdminAuditLogsContent(
  logs: List<AuditLog>,
  searchQuery: String,
  onSearchChange: (String) -> Unit,
  categoryFilter: String,
  onCategoryFilterChange: (String) -> Unit,
  onClearLogs: () -> Unit
) {
  val filteredLogs = remember(logs, searchQuery, categoryFilter) {
    logs.filter { log ->
      val matchesCategory = when (categoryFilter) {
        "ALL" -> true
        "ADD" -> log.actionType.contains("ADD", ignoreCase = true)
        "EDIT" -> log.actionType.contains("EDIT", ignoreCase = true) || log.actionType.contains("UPDATE", ignoreCase = true)
        "DELETE" -> log.actionType.contains("DELETE", ignoreCase = true)
        "CLOUD" -> log.actionType.contains("CLOUD", ignoreCase = true) || log.actionType.contains("BULK", ignoreCase = true)
        else -> true
      }
      val matchesSearch = if (searchQuery.isBlank()) true else {
        log.actionType.contains(searchQuery, ignoreCase = true) ||
        log.entityType.contains(searchQuery, ignoreCase = true) ||
        log.entityIdentifier.contains(searchQuery, ignoreCase = true) ||
        log.adminUsername.contains(searchQuery, ignoreCase = true) ||
        log.details.contains(searchQuery, ignoreCase = true)
      }
      matchesCategory && matchesSearch
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp)
  ) {
    // Security Banner
    Surface(
      shape = RoundedCornerShape(12.dp),
      color = Color(0xFFFEF2F2),
      border = BorderStroke(1.dp, Color(0xFFFECACA)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.Security,
          contentDescription = null,
          tint = Color(0xFFDC2626),
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "SECURE AUDIT LOGGING SYSTEM",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFB91C1C)
          )
          Text(
            text = "Records all additions, modifications, and deletions made by Admin accounts. Visible strictly to Administrators.",
            fontSize = 10.5.sp,
            color = Color(0xFF7F1D1D)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Search and Clear Action Row
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchChange,
        placeholder = { Text("Search action, villa, admin, or detail...", fontSize = 12.sp) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { onSearchChange("") }, modifier = Modifier.size(20.dp)) {
              Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(14.dp))
            }
          }
        },
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .weight(1f)
          .height(50.dp)
      )

      if (logs.isNotEmpty()) {
        Surface(
          onClick = onClearLogs,
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFFFEE2E2),
          border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
          modifier = Modifier.height(50.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.DeleteForever, contentDescription = "Clear History", tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Clear", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Filter Chips: All, Additions, Edits, Deletions, Cloud & Bulk
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      listOf(
        "ALL" to "All Logs (${logs.size})",
        "ADD" to "Additions",
        "EDIT" to "Modifications",
        "DELETE" to "Deletions",
        "CLOUD" to "Cloud & Bulk"
      ).forEach { (cat, label) ->
        val isSel = categoryFilter == cat
        FilterChip(
          selected = isSel,
          onClick = { onCategoryFilterChange(cat) },
          label = { Text(label, fontSize = 10.5.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFF1E293B),
            selectedLabelColor = Color.White
          )
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Audit Log List
    if (filteredLogs.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(Icons.Default.History, contentDescription = null, tint = Slate400, modifier = Modifier.size(36.dp))
          Spacer(modifier = Modifier.height(8.dp))
          Text("No audit log events found", fontWeight = FontWeight.Bold, color = Slate600, fontSize = 13.sp)
          Text("Actions performed by Admin will be logged here.", fontSize = 11.sp, color = Slate500)
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(filteredLogs, key = { it.id }) { log ->
          AuditLogItemCard(log = log)
        }
      }
    }
  }
}

@Composable
private fun AuditLogItemCard(log: AuditLog) {
  val (actionBg, actionColor, actionIcon) = when {
    log.actionType.contains("ADD", true) -> Triple(LightGreen, DarkGreen, Icons.Default.AddCircle)
    log.actionType.contains("EDIT", true) || log.actionType.contains("FULFILL", true) || log.actionType.contains("RESOLVE", true) ->
      Triple(ZawitcoLightBlue, ZawitcoBlue, Icons.Default.Edit)
    log.actionType.contains("DELETE", true) -> Triple(Color(0xFFFEE2E2), Color(0xFFDC2626), Icons.Default.Delete)
    log.actionType.contains("CLOUD", true) -> Triple(Color(0xFFEDE9FE), Color(0xFF7C3AED), Icons.Default.CloudDone)
    log.actionType.contains("BULK", true) -> Triple(Color(0xFFFEF3C7), Color(0xFFD97706), Icons.Default.CloudUpload)
    else -> Triple(Slate100, Slate700, Icons.Default.Info)
  }

  val formattedDate = remember(log.timestamp) {
    SimpleDateFormat("MMM dd, yyyy • hh:mm:ss a", Locale.getDefault()).format(Date(log.timestamp))
  }

  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.dp, Slate200)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = actionBg
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(actionIcon, contentDescription = null, tint = actionColor, modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = log.actionType,
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold,
                color = actionColor
              )
            }
          }
          Spacer(modifier = Modifier.width(6.dp))
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = Slate100
          ) {
            Text(
              text = log.entityType,
              fontSize = 9.5.sp,
              fontWeight = FontWeight.SemiBold,
              color = Slate600,
              modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
            )
          }
        }

        // Admin Username pill
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = Color(0xFFF1F5F9)
        ) {
          Text(
            text = "👤 ${log.adminUsername}",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Slate700,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = log.entityIdentifier,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = Slate900
      )

      if (log.details.isNotBlank()) {
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = log.details,
          fontSize = 11.5.sp,
          color = Slate600
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "Time: $formattedDate",
        fontSize = 10.sp,
        color = Slate400
      )
    }
  }
}
