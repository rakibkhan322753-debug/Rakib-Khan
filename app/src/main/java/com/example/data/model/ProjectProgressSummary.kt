package com.example.data.model

/**
 * Progress and operational performance analysis for key company projects/products:
 * 1. Keemart DS
 * 2. Ninja
 * 3. Warehouse DC
 * 4. 9 Ground
 */
data class ProjectProgressSummary(
  val projectName: String,
  val projectCode: String,
  val category: String,
  val description: String,
  val themeColorHex: Long,       // 0xFF...
  val totalUnits: Int,           // Assigned housing units / accommodations
  val totalCapacity: Int,        // Maximum bed spaces
  val activeWorkers: Int,        // Current active workforce accommodated
  val occupancyRate: Int,        // Percentage (0-100)
  val pendingRequirements: Int,  // Equipment & items needed
  val openReports: Int,          // Active maintenance issue reports
  val progressPercentage: Int,   // Overall readiness score (0-100)
  val statusLabel: String,       // "Optimal", "On Track", "Needs Attention", "Under Review"
  val accommodations: List<Accommodation>
)

val coreProjectNames = listOf(
  "Keemart DS",
  "Ninja",
  "Warehouse DC",
  "9 Ground"
)
