package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.StationDao
import com.example.data.local.UserAccountDao
import com.example.data.model.Accommodation
import com.example.data.model.Station
import com.example.data.model.UserAccount
import com.example.util.BulkDataParser
import com.example.util.ExcelExporter
import com.example.util.LocationAnalysisHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class NewFeaturesUnitTest {

  private lateinit var database: AppDatabase
  private lateinit var userDao: UserAccountDao
  private lateinit var stationDao: StationDao

  @Before
  fun setup() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    userDao = database.userAccountDao()
    stationDao = database.stationDao()
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun testUserAccountCreationAndAuthentication() = runBlocking {
    val user = UserAccount(
      username = "test_staff",
      passwordHash = "Pass1234",
      fullName = "Test Engineer",
      role = "ADMIN",
      assignedStation = "Riyadh Central Hub"
    )
    val id = userDao.insert(user)
    assertTrue(id > 0)

    val authenticated = userDao.authenticate("test_staff", "Pass1234")
    assertNotNull(authenticated)
    assertEquals("test_staff", authenticated?.username)
    assertEquals("ADMIN", authenticated?.role)
  }

  @Test
  fun testStationHubDao() = runBlocking {
    val station = Station(
      name = "Dammam Industrial Hub",
      code = "DH-02",
      areaName = "Industrial Area 2",
      city = "Dammam",
      latitude = 26.4207,
      longitude = 50.0888
    )
    val id = stationDao.insert(station)
    assertTrue(id > 0)

    val list = stationDao.getAllStations().first()
    assertEquals(1, list.size)
    assertEquals("Dammam Industrial Hub", list[0].name)
    assertEquals(26.4207, list[0].latitude ?: 0.0, 0.0001)
  }

  @Test
  fun testBulkDataParser() {
    val sampleCsv = BulkDataParser.getSampleAccommodationCsv()
    val result = BulkDataParser.parseBulkCsv(sampleCsv)

    assertTrue(result.accommodations.isNotEmpty())
    val firstAcc: Accommodation = result.accommodations.first()
    assertTrue(firstAcc.areaName.contains("Olaya"))
    assertEquals("14B", firstAcc.villaNumber)
    assertEquals(24.7136, firstAcc.latitude ?: 0.0, 0.0001)
  }

  @Test
  fun testLocationAnalysisHelper() {
    val stations = listOf(
      Station(id = 1L, name = "Riyadh Hub", latitude = 24.7136, longitude = 46.6753, city = "Riyadh"),
      Station(id = 2L, name = "Jeddah Hub", latitude = 21.4858, longitude = 39.1925, city = "Jeddah")
    )
    val accommodations = listOf(
      Accommodation(
        id = 101L,
        areaName = "Al Olaya",
        villaNumber = "14B",
        latitude = 24.7150,
        longitude = 46.6770,
        stationName = "Riyadh Hub"
      )
    )

    val report = LocationAnalysisHelper.analyzeLocations(accommodations, stations)
    assertEquals(1, report.totalAccommodations)
    assertEquals(2, report.totalStations)
    assertEquals(1, report.accommodationsWithGps)
    val avgDist = report.averageDistanceKm
    assertNotNull(avgDist)
    assertTrue((avgDist ?: 999.0) < 5.0) // Very close to Riyadh Hub
  }

  @Test
  fun testExcelExporterBom() {
    val accommodations = listOf(
      Accommodation(id = 1L, areaName = "Al Olaya", villaNumber = "12")
    )
    val csv = ExcelExporter.buildCombinedCsvString(emptyList(), emptyList(), accommodations)
    assertTrue(csv.startsWith("\uFEFF")) // Validates UTF-8 BOM for Microsoft Excel
    assertTrue(csv.contains("Al Olaya"))
  }
}
