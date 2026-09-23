package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate600
import com.example.ui.theme.ZawitcoBlue
import com.example.ui.theme.ZawitcoLightBlue
import com.example.ui.theme.ZawitcoOrange
import com.example.util.LocationAnalysisReport

@Composable
fun LocationAnalyticsView(
  report: LocationAnalysisReport,
  activeStationFilter: String?,
  onFilterByStation: (String?) -> Unit,
  onOpenBulkUpload: () -> Unit,
  onOpenStationManager: () -> Unit,
  isAdmin: Boolean,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp)
      .testTag("location_analytics_view"),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Action & Title Bar
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = ZawitcoBlue,
              modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Geospatial & Station Analytics",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = Color.Black
            )
          }
          Text(
            text = "Automated proximity calculations & hub coverage",
            fontSize = 12.sp,
            color = Color.Black
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Button(
            onClick = onOpenBulkUpload,
            colors = ButtonDefaults.buttonColors(containerColor = ZawitcoBlue),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("analytics_bulk_upload_button")
          ) {
            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Bulk Upload", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
          }

          if (isAdmin) {
            Button(
              onClick = onOpenStationManager,
              colors = ButtonDefaults.buttonColors(containerColor = ZawitcoOrange),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.testTag("analytics_add_station_button")
            ) {
              Icon(Icons.Default.AddLocation, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Stations", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
          }
        }
      }
    }

    // Active Filter Indicator
    if (!activeStationFilter.isNullOrBlank()) {
      item {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFFEFF6FF),
          border = androidx.compose.foundation.BorderStroke(1.dp, ZawitcoBlue),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.FilterAlt, contentDescription = null, tint = ZawitcoBlue, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Filtered by Station: $activeStationFilter",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
              )
            }
            OutlinedButton(
              onClick = { onFilterByStation(null) },
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.height(32.dp)
            ) {
              Icon(Icons.Default.FilterAltOff, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Clear Filter", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
          }
        }
      }
    }

    // 4 KPI Metrics
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        AnalyticsMetricCard(
          title = "Total Housing",
          value = "${report.totalAccommodations}",
          subtitle = "Units loaded",
          icon = Icons.Default.Home,
          iconTint = ZawitcoBlue,
          modifier = Modifier.weight(1f)
        )
        AnalyticsMetricCard(
          title = "Station Hubs",
          value = "${report.totalStations}",
          subtitle = "Active depots",
          icon = Icons.Default.Hub,
          iconTint = ZawitcoOrange,
          modifier = Modifier.weight(1f)
        )
      }
    }

    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        val coveragePercent = if (report.totalAccommodations > 0) {
          (report.accommodationsWithGps * 100) / report.totalAccommodations
        } else 0
        AnalyticsMetricCard(
          title = "GPS Validated",
          value = "$coveragePercent%",
          subtitle = "${report.accommodationsWithGps} of ${report.totalAccommodations} units",
          icon = Icons.Default.LocationOn,
          iconTint = Color(0xFF10B981),
          modifier = Modifier.weight(1f)
        )
        AnalyticsMetricCard(
          title = "Avg Distance",
          value = if (report.averageDistanceKm != null) String.format("%.1f km", report.averageDistanceKm) else "N/A",
          subtitle = "To nearest station",
          icon = Icons.Default.DirectionsCar,
          iconTint = Color(0xFF8B5CF6),
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Smart Insights Card
    if (report.insights.isNotEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
          border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ZawitcoBlue, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Automated Operational Insights",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
              )
            }
            Spacer(modifier = Modifier.height(10.dp))
            report.insights.forEach { insight ->
              Row(
                modifier = Modifier.padding(vertical = 3.dp),
                verticalAlignment = Alignment.Top
              ) {
                Text(text = "•", fontWeight = FontWeight.Bold, color = ZawitcoBlue, modifier = Modifier.padding(end = 6.dp))
                Text(
                  text = insight,
                  fontSize = 12.sp,
                  color = Color.Black,
                  fontWeight = FontWeight.Normal
                )
              }
            }
          }
        }
      }
    }

    // Stations Coverage Section Header
    item {
      Text(
        text = "Station Coverage & Assigned Housing Units",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black
      )
    }

    // Station Coverage Cards
    items(report.stationCoverages, key = { it.station.id }) { coverage ->
      val st = coverage.station
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(ZawitcoLightBlue),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.Hub, contentDescription = null, tint = ZawitcoBlue, modifier = Modifier.size(20.dp))
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = st.name,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.Black
                )
                Text(
                  text = "${st.areaName} • Code: ${st.code.ifBlank { "N/A" }}",
                  fontSize = 11.sp,
                  color = Color.Black
                )
              }
            }

            Button(
              onClick = { onFilterByStation(st.name) },
              colors = ButtonDefaults.buttonColors(
                containerColor = if (activeStationFilter == st.name) Color.Black else ZawitcoBlue
              ),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.height(34.dp)
            ) {
              Text(
                text = if (activeStationFilter == st.name) "Selected" else "Filter",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Station Coverage Stats Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFFF1F5F9),
              modifier = Modifier.weight(1f)
            ) {
              Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(text = "Assigned", fontSize = 10.sp, color = Color.Black)
                Text(text = "${coverage.assignedCount}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
              }
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFFF1F5F9),
              modifier = Modifier.weight(1f)
            ) {
              Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(text = "Within 5 km", fontSize = 10.sp, color = Color.Black)
                Text(text = "${coverage.nearbyCountWithin5Km}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
              }
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color(0xFFF1F5F9),
              modifier = Modifier.weight(1f)
            ) {
              Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(text = "Within 10 km", fontSize = 10.sp, color = Color.Black)
                Text(text = "${coverage.nearbyCountWithin10Km}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
              }
            }
          }

          if (st.supervisorName.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Supervisor: ${st.supervisorName} (${st.supervisorPhone})",
              fontSize = 11.sp,
              color = Color.Black
            )
          }
        }
      }
    }

    // Regional Housing Breakdown Section Header
    item {
      Text(
        text = "Accommodations Proximity to Stations",
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black
      )
    }

    // Accommodation Proximity List
    items(report.proximities, key = { it.accommodation.id }) { item ->
      val acc = item.accommodation
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "${acc.areaName} - Villa ${acc.villaNumber}",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = ZawitcoBlue
            )
            Text(
              text = "Floor: ${acc.floorNumber} • Room: ${acc.roomNumber}",
              fontSize = 11.sp,
              color = Color.Black
            )
            if (acc.stationName.isNotBlank()) {
              Text(
                text = "Assigned: ${acc.stationName}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = ZawitcoBlue
              )
            }
          }

          Column(horizontalAlignment = Alignment.End) {
            if (item.distanceKm != null) {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (item.distanceKm <= 5.0) Color(0xFFD1FAE5) else Color(0xFFFEF3C7)
              ) {
                Text(
                  text = String.format("%.1f km", item.distanceKm),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = if (item.distanceKm <= 5.0) Color(0xFF065F46) else Color(0xFF92400E),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "to ${item.nearestStation?.name ?: "station"}",
                fontSize = 10.sp,
                color = Color.Black
              )
            } else {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFFFEE2E2)
              ) {
                Text(
                  text = "No GPS",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF991B1B),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun AnalyticsMetricCard(
  title: String,
  value: String,
  subtitle: String,
  icon: ImageVector,
  iconTint: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = value,
        fontSize = 20.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color.Black
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = subtitle,
        fontSize = 11.sp,
        color = Color.Black
      )
    }
  }
}
