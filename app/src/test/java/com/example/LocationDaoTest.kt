package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.LocationDao
import com.example.data.model.Location
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
class LocationDaoTest {

  private lateinit var database: AppDatabase
  private lateinit var locationDao: LocationDao

  @Before
  fun setup() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    locationDao = database.locationDao()
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun testInsertAndRetrieveLocation() = runBlocking {
    val location = Location(
      name = "Al Olaya Villa 14B",
      areaName = "Al Olaya",
      propertyType = "Villa",
      latitude = 24.7136,
      longitude = 46.6753,
      villaNumber = "14B",
      floorNumber = "2nd Floor",
      roomNumber = "201",
      totalRooms = 3,
      capacity = 6,
      contactPerson = "Abdullah",
      contactPhone = "+966 50 123 4567"
    )

    val id = locationDao.insertLocation(location)
    assertTrue(id > 0)

    val retrieved = locationDao.getLocationById(id).first()
    assertNotNull(retrieved)
    assertEquals("Al Olaya Villa 14B", retrieved?.name)
    assertEquals(24.7136, retrieved?.latitude ?: 0.0, 0.0001)
    assertEquals(46.6753, retrieved?.longitude ?: 0.0, 0.0001)
    assertEquals("14B", retrieved?.villaNumber)
  }

  @Test
  fun testSearchLocations() = runBlocking {
    val loc1 = Location(
      name = "Malaz Housing 1",
      areaName = "Al Malaz",
      propertyType = "Staff Housing",
      latitude = 24.6657,
      longitude = 46.7369,
      villaNumber = "28"
    )
    val loc2 = Location(
      name = "Sulaimaniya Villa",
      areaName = "Al Sulaimaniya",
      propertyType = "Villa",
      latitude = 24.6984,
      longitude = 46.7028,
      villaNumber = "07"
    )
    locationDao.insertAll(listOf(loc1, loc2))

    val searchResults = locationDao.searchLocations("Malaz").first()
    assertEquals(1, searchResults.size)
    assertEquals("Malaz Housing 1", searchResults[0].name)
  }
}
