package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Accommodation
import com.example.data.model.AccommodationReport
import com.example.data.model.AccommodationRequirement
import com.example.data.model.Location
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [
    Accommodation::class,
    Location::class,
    AccommodationRequirement::class,
    AccommodationReport::class
  ],
  version = 4,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

  abstract fun accommodationDao(): AccommodationDao
  abstract fun locationDao(): LocationDao
  abstract fun requirementDao(): AccommodationRequirementDao
  abstract fun reportDao(): AccommodationReportDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "zawitco_accommodation.db"
        )
          .addCallback(DatabaseCallback(scope))
          .fallbackToDestructiveMigration(dropAllTables = true)
          .build()
        INSTANCE = instance
        instance
      }
    }

    private class DatabaseCallback(
      private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          scope.launch(Dispatchers.IO) {
            populateInitialData(
              database.accommodationDao(),
              database.locationDao(),
              database.requirementDao(),
              database.reportDao()
            )
          }
        }
      }

      suspend fun populateInitialData(
        dao: AccommodationDao,
        locDao: LocationDao,
        reqDao: AccommodationRequirementDao,
        reportDao: AccommodationReportDao
      ) {
        val initialAccommodations = listOf(
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
            notes = "Executive villa unit with dedicated parking and proximity to commercial center."
          )
        )
        dao.insertAll(initialAccommodations)

        val initialLocations = listOf(
          Location(
            name = "Al Olaya Executive Villa",
            areaName = "Al Olaya District",
            propertyType = "Villa",
            latitude = 24.7136,
            longitude = 46.6753,
            address = "King Fahd Road, Al Olaya, Riyadh",
            villaNumber = "14B",
            floorNumber = "2nd Floor",
            roomNumber = "201, 202, 203",
            totalRooms = 3,
            capacity = 8,
            contactPerson = "Abdullah Al-Mansoor",
            contactPhone = "+966 50 123 4567",
            googleMapsUrl = "https://maps.google.com/?q=24.7136,46.6753",
            notes = "Primary executive villa for project engineers."
          ),
          Location(
            name = "Al Malaz Staff Residence",
            areaName = "Al Malaz",
            propertyType = "Staff Housing",
            latitude = 24.6657,
            longitude = 46.7369,
            address = "Salah Al-Din Al-Ayyubi Rd, Al Malaz, Riyadh",
            villaNumber = "28",
            floorNumber = "Ground Floor",
            roomNumber = "101, 102",
            totalRooms = 4,
            capacity = 12,
            contactPerson = "Tariq Mahmood",
            contactPhone = "+966 54 321 0987",
            googleMapsUrl = "https://maps.google.com/?q=24.6657,46.7369",
            notes = "Dedicated staff housing near central transit and services."
          ),
          Location(
            name = "Al Sulaimaniya Villa Unit",
            areaName = "Al Sulaimaniya",
            propertyType = "Villa",
            latitude = 24.6984,
            longitude = 46.7028,
            address = "Prince Mutaib bin Abdulaziz Rd, Al Sulaimaniya, Riyadh",
            villaNumber = "07",
            floorNumber = "1st Floor",
            roomNumber = "104, 105",
            totalRooms = 2,
            capacity = 6,
            contactPerson = "Sultan Al-Otaibi",
            contactPhone = "+966 53 888 2345",
            googleMapsUrl = "https://maps.google.com/?q=24.6984,46.7028",
            notes = "Furnished villa unit with private parking space."
          )
        )
        locDao.insertAll(initialLocations)

        // Seed sample requirement & report so notification and data display are immediately visible & testable
        val initialRequirements = listOf(
          AccommodationRequirement(
            accommodationId = 1L,
            itemName = "Bed 🛌",
            quantity = 2,
            urgency = "Urgent",
            status = "Pending",
            requestedBy = "Room 201 occupants",
            notes = "Two single beds required for new project staff"
          ),
          AccommodationRequirement(
            accommodationId = 1L,
            itemName = "Gas Cylinder",
            quantity = 1,
            urgency = "Normal",
            status = "Pending",
            requestedBy = "Supervisor",
            notes = "Kitchen refill required"
          )
        )
        reqDao.insertAll(initialRequirements)

        val initialReports = listOf(
          AccommodationReport(
            accommodationId = 1L,
            issueCategory = "AC/Cooling",
            title = "AC cooling issue in room 202",
            description = "Master split AC is blowing warm air, needs technician gas check or filter clean.",
            severity = "High",
            status = "Open",
            reportedBy = "Abdullah Al-Mansoor",
            reporterPhone = "+966 50 123 4567"
          )
        )
        reportDao.insertAll(initialReports)
      }
    }
  }
}
