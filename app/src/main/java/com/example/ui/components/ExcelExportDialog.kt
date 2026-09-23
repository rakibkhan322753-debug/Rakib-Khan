package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.model.AccommodationRequirement
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.ZawitcoBlue
import com.example.ui.theme.ZawitcoLightBlue
import com.example.ui.theme.ZawitcoOrange
import com.example.util.ExcelExporter

@Composable
fun ExcelExportDialog(
  requirements: List<AccommodationRequirement>,
  reports: List<AccommodationReport>,
  accommodations: List<Accommodation>,
  onDismiss: () -> Unit,
  onExportRequirements: () -> Unit,
  onExportReports: () -> Unit,
  onExportAll: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  fun copyToClipboard(label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = modifier
        .fillMaxWidth(0.92f)
        .clip(RoundedCornerShape(24.dp))
        .testTag("excel_export_dialog"),
      color = Color.White,
      shape = RoundedCornerShape(24.dp),
      shadowElevation = 16.dp
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
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(0xFFDCFCE7)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.TableChart,
                contentDescription = null,
                tint = Color(0xFF16A34A),
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Export Data as Excel",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
              )
              Text(
                text = "UTF-8 BOM Excel CSV (.csv / .xlsx compatible)",
                fontSize = 12.sp,
                color = Color.Black
              )
            }
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Black)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Data Summary Card
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
          border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "Requirements", fontSize = 11.sp, color = Color.Black)
              Text(text = "${requirements.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ZawitcoBlue)
              Text(text = "Records", fontSize = 10.sp, color = Color.Black)
            }
            Box(modifier = Modifier.width(1.dp).height(36.dp).background(Slate200))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "Issue Reports", fontSize = 11.sp, color = Color.Black)
              Text(text = "${reports.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ZawitcoOrange)
              Text(text = "Records", fontSize = 10.sp, color = Color.Black)
            }
            Box(modifier = Modifier.width(1.dp).height(36.dp).background(Slate200))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "Housing Units", fontSize = 11.sp, color = Color.Black)
              Text(text = "${accommodations.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
              Text(text = "Properties", fontSize = 10.sp, color = Color.Black)
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Option 1: Export Requirements
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
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
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Icon(Icons.Default.Description, contentDescription = null, tint = ZawitcoBlue, modifier = Modifier.size(24.dp))
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(text = "Requirements Data", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Black)
                Text(text = "${requirements.size} furniture & utility requests", fontSize = 11.sp, color = Color.Black)
              }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              IconButton(
                onClick = {
                  val csv = ExcelExporter.buildRequirementsCsvString(requirements, accommodations)
                  copyToClipboard("Requirements CSV", csv)
                }
              ) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy CSV", tint = Color.Black)
              }
              Button(
                onClick = onExportRequirements,
                colors = ButtonDefaults.buttonColors(containerColor = ZawitcoBlue),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("export_requirements_button")
              ) {
                Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Excel", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Option 2: Export Reports
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
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
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Icon(Icons.Default.Warning, contentDescription = null, tint = ZawitcoOrange, modifier = Modifier.size(24.dp))
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(text = "Maintenance Reports Data", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Black)
                Text(text = "${reports.size} maintenance issues & tickets", fontSize = 11.sp, color = Color.Black)
              }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              IconButton(
                onClick = {
                  val csv = ExcelExporter.buildReportsCsvString(reports, accommodations)
                  copyToClipboard("Reports CSV", csv)
                }
              ) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy CSV", tint = Color.Black)
              }
              Button(
                onClick = onExportReports,
                colors = ButtonDefaults.buttonColors(containerColor = ZawitcoOrange),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("export_reports_button")
              ) {
                Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Excel", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Option 3: Export Complete Combined Dataset
        Button(
          onClick = onExportAll,
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("export_all_excel_button")
        ) {
          Icon(Icons.Default.FileDownload, contentDescription = null, tint = Color.White)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Export All Data (Complete Excel File)",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color.White
          )
        }
      }
    }
  }
}
