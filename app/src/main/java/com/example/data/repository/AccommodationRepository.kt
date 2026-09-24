package com.example.data.repository

import com.example.data.local.AccommodationDao
import com.example.data.local.AccommodationReportDao
import com.example.data.local.AccommodationRequirementDao
import com.example.data.local.AuditLogDao
import com.example.data.local.StationDao
import com.example.data.local.UserAccountDao
import com.example.data.model.Accommodation
import com.example.data.model.AccommodationReport
import com.example.data.model.AccommodationRequirement
import com.example.data.model.AuditLog
import com.example.data.model.Station
import com.example.data.model.UserAccount
import kotlinx.coroutines.flow.Flow

class AccommodationRepository(
  private val dao: AccommodationDao,
  private val reqDao: AccommodationRequirementDao,
  private val reportDao: AccommodationReportDao,
  private val userDao: UserAccountDao,
  private val stationDao: StationDao,
  private val auditDao: AuditLogDao
) {

  val allAccommodations: Flow<List<Accommodation>> = dao.getAllAccommodations()
  val allUsers: Flow<List<UserAccount>> = userDao.getAllUsers()
  val allStations: Flow<List<Station>> = stationDao.getAllStations()
  val allAuditLogs: Flow<List<AuditLog>> = auditDao.getAllLogs()

  fun searchAuditLogs(filter: String): Flow<List<AuditLog>> = auditDao.searchLogs(filter)

  suspend fun recordAuditLog(
    actionType: String,
    entityType: String,
    identifier: String,
    adminUsername: String,
    details: String
  ) {
    val log = AuditLog(
      actionType = actionType,
      entityType = entityType,
      entityIdentifier = identifier,
      adminUsername = adminUsername.ifBlank { "admin" },
      details = details,
      timestamp = System.currentTimeMillis()
    )
    auditDao.insertLog(log)
  }

  suspend fun clearAuditLogs() = auditDao.clearLogs()

  fun getAccommodationById(id: Long): Flow<Accommodation?> = dao.getAccommodationById(id)

  fun searchAccommodations(query: String): Flow<List<Accommodation>> {
    return if (query.isBlank()) {
      dao.getAllAccommodations()
    } else {
      dao.searchAccommodations(query.trim())
    }
  }

  suspend fun insert(accommodation: Accommodation): Long = dao.insertAccommodation(accommodation)

  suspend fun insertAccommodations(items: List<Accommodation>) = dao.insertAll(items)

  suspend fun update(accommodation: Accommodation) = dao.updateAccommodation(accommodation)

  suspend fun delete(accommodation: Accommodation) = dao.deleteAccommodation(accommodation)

  suspend fun deleteById(id: Long) = dao.deleteAccommodationById(id)

  // ==========================================
  // USER ACCOUNT OPERATIONS (ADMIN ONLY)
  // ==========================================
  suspend fun authenticateUser(username: String, password: String): UserAccount? =
    userDao.authenticate(username, password)

  suspend fun insertUser(user: UserAccount): Long = userDao.insert(user)

  suspend fun insertUsers(users: List<UserAccount>) = userDao.insertAll(users)

  suspend fun updateUser(user: UserAccount) = userDao.update(user)

  suspend fun deleteUser(user: UserAccount) = userDao.delete(user)

  suspend fun deleteUserById(id: Long) = userDao.deleteById(id)

  // ==========================================
  // STATION OPERATIONS
  // ==========================================
  suspend fun insertStation(station: Station): Long = stationDao.insert(station)

  suspend fun insertStations(stations: List<Station>) = stationDao.insertAll(stations)

  suspend fun updateStation(station: Station) = stationDao.update(station)

  suspend fun deleteStation(station: Station) = stationDao.delete(station)

  suspend fun deleteStationById(id: Long) = stationDao.deleteById(id)

  // ==========================================
  // REQUIREMENTS OPERATIONS
  // ==========================================
  fun getRequirementsForAccommodation(accId: Long): Flow<List<AccommodationRequirement>> =
    reqDao.getRequirementsForAccommodation(accId)

  fun getAllRequirements(): Flow<List<AccommodationRequirement>> =
    reqDao.getAllRequirements()

  fun getTotalRequirementsCount(): Flow<Int> =
    reqDao.getTotalRequirementsCount()

  fun getActiveRequirementsCountForAcc(accId: Long): Flow<Int> =
    reqDao.getActiveRequirementsCountForAcc(accId)

  suspend fun insertRequirement(req: AccommodationRequirement): Long =
    reqDao.insertRequirement(req)

  suspend fun insertRequirements(reqs: List<AccommodationRequirement>) =
    reqDao.insertAll(reqs)

  suspend fun updateRequirement(req: AccommodationRequirement) =
    reqDao.updateRequirement(req)

  suspend fun deleteRequirement(req: AccommodationRequirement) =
    reqDao.deleteRequirement(req)

  suspend fun deleteRequirementById(id: Long) =
    reqDao.deleteById(id)

  // ==========================================
  // REPORTS OPERATIONS
  // ==========================================
  fun getReportsForAccommodation(accId: Long): Flow<List<AccommodationReport>> =
    reportDao.getReportsForAccommodation(accId)

  fun getAllReports(): Flow<List<AccommodationReport>> =
    reportDao.getAllReports()

  fun getTotalReportsCount(): Flow<Int> =
    reportDao.getTotalReportsCount()

  fun getActiveReportsCountForAcc(accId: Long): Flow<Int> =
    reportDao.getActiveReportsCountForAcc(accId)

  suspend fun insertReport(report: AccommodationReport): Long =
    reportDao.insertReport(report)

  suspend fun insertReports(reports: List<AccommodationReport>) =
    reportDao.insertAll(reports)

  suspend fun updateReport(report: AccommodationReport) =
    reportDao.updateReport(report)

  suspend fun deleteReport(report: AccommodationReport) =
    reportDao.deleteReport(report)

  suspend fun deleteReportById(id: Long) =
    reportDao.deleteById(id)

  suspend fun ensureDefaultDataIfEmpty() {
    if (dao.getCount() == 0) {
      val defaultItems = listOf(
        Accommodation(
          areaName = "Al Olaya District",
          villaNumber = "14B",
          floorNumber = "2nd Floor",
          roomNumber = "201, 202, 203",
          workerPhone = "+966 50 123 4567",
          ownerPhone = "+966 55 987 6543",
          googleMapsUrl = "https://maps.google.com/?q=24.7136,46.6753",
          latitude = 24.7136,
          longitude = 46.6753,
          stationName = "Riyadh Central Hub",
          notes = "Prime central location near King Fahd Road. Fully furnished with high-speed internet."
        ),
        Accommodation(
          areaName = "Al Malaz",
          villaNumber = "28",
          floorNumber = "Ground Floor",
          roomNumber = "101, 102",
          workerPhone = "+966 54 321 0987",
          ownerPhone = "+966 56 654 3210",
          googleMapsUrl = "https://maps.google.com/?q=24.6657,46.7369",
          latitude = 24.6657,
          longitude = 46.7369,
          stationName = "East Riyadh Station",
          notes = "Spacious staff accommodation, close to public transport and Malaz central market."
        ),
        Accommodation(
          areaName = "Al Sulaimaniya",
          villaNumber = "07",
          floorNumber = "1st Floor",
          roomNumber = "104, 105",
          workerPhone = "+966 53 888 2345",
          ownerPhone = "+966 50 444 8765",
          googleMapsUrl = "https://maps.google.com/?q=24.6984,46.7028",
          latitude = 24.6984,
          longitude = 46.7028,
          stationName = "North Riyadh Depot",
          notes = "Executive villa unit with dedicated parking and proximity to commercial center."
        )
      )
      dao.insertAll(defaultItems)
    }

    if (stationDao.getCount() == 0) {
      val defaultStations = listOf(
        Station(
          name = "Riyadh Central Hub",
          code = "RC-01",
          areaName = "Al Olaya District",
          city = "Riyadh",
          latitude = 24.7136,
          longitude = 46.6753,
          address = "King Fahd Road, Al Olaya, Riyadh",
          supervisorName = "Eng. Ahmed Al-Zahrani",
          supervisorPhone = "+966 50 111 2233"
        ),
        Station(
          name = "East Riyadh Station",
          code = "ER-02",
          areaName = "Al Malaz",
          city = "Riyadh",
          latitude = 24.6657,
          longitude = 46.7369,
          address = "Salah Al-Din Road, Al Malaz, Riyadh",
          supervisorName = "Eng. Khalid Al-Mutairi",
          supervisorPhone = "+966 55 444 5566"
        )
      )
      stationDao.insertAll(defaultStations)
    }

    if (userDao.getCount() == 0) {
      val defaultUsers = listOf(
        UserAccount(
          username = "admin",
          passwordHash = "322753",
          fullName = "Zawitco General Admin",
          role = "ADMIN",
          assignedStation = "Riyadh Central Hub"
        ),
        UserAccount(
          username = "staff1",
          passwordHash = "Zawitco",
          fullName = "Field Operations Staff",
          role = "USER",
          assignedStation = "East Riyadh Station"
        )
      )
      userDao.insertAll(defaultUsers)
    }
  }
}
