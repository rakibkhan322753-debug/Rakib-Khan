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
import com.example.data.model.Station
import com.example.data.model.UserAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [
    Accommodation::class,
    Location::class,
    AccommodationRequirement::class,
    AccommodationReport::class,
    UserAccount::class,
    Station::class
  ],
  version = 6,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

  abstract fun accommodationDao(): AccommodationDao
  abstract fun locationDao(): LocationDao
  abstract fun requirementDao(): AccommodationRequirementDao
  abstract fun reportDao(): AccommodationReportDao
  abstract fun userAccountDao(): UserAccountDao
  abstract fun stationDao(): StationDao

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
              database.reportDao(),
              database.userAccountDao(),
              database.stationDao()
            )
          }
        }
      }

      suspend fun populateInitialData(
        dao: AccommodationDao,
        locDao: LocationDao,
        reqDao: AccommodationRequirementDao,
        reportDao: AccommodationReportDao,
        userDao: UserAccountDao,
        stationDao: StationDao
      ) {
        // Initial Accommodations
        val initialAccommodations = listOf(
          Accommodation(
            areaName = "Al Olaya District",
            villaNumber = "14B",
            floorNumber = "2nd Floor",
            roomNumber = "201, 202, 203",
            totalWorkers = 14,
            totalCapacity = 16,
            activeWorkers = 12,
            accommodationLocationUrl = "https://maps.google.com/?q=24.7136,46.6753",
            storeLocationUrl = "https://maps.google.com/?q=24.7180,46.6800",
            storeCode = "ST-101",
            storeName = "Olaya Central Store",
            workerPhone = "+966 50 123 4567",
            workerPhone2 = "+966 50 998 1122",
            ownerName = "Sheikh Abdullah Al-Mansoor",
            ownerPhone = "+966 55 987 6543",
            ownerBankName = "Al Rajhi Bank",
            ownerIban = "SA4480000456608010123456",
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
            totalWorkers = 10,
            totalCapacity = 12,
            activeWorkers = 9,
            accommodationLocationUrl = "https://maps.google.com/?q=24.6657,46.7369",
            storeLocationUrl = "https://maps.google.com/?q=24.6700,46.7400",
            storeCode = "ST-102",
            storeName = "Malaz Logistics Store",
            workerPhone = "+966 54 321 0987",
            workerPhone2 = "+966 54 887 6655",
            ownerName = "Abu Fahad Al-Otaibi",
            ownerPhone = "+966 56 654 3210",
            ownerBankName = "Saudi National Bank (SNB)",
            ownerIban = "SA1210000001234567890123",
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
            totalWorkers = 8,
            totalCapacity = 10,
            activeWorkers = 7,
            accommodationLocationUrl = "https://maps.google.com/?q=24.6984,46.7028",
            storeLocationUrl = "https://maps.google.com/?q=24.7000,46.7050",
            storeCode = "ST-103",
            storeName = "Sulaimaniya Hub Store",
            workerPhone = "+966 53 888 2345",
            workerPhone2 = "+966 53 777 4433",
            ownerName = "Eng. Tariq Al-Ghamdi",
            ownerPhone = "+966 50 444 8765",
            ownerBankName = "Riyad Bank",
            ownerIban = "SA5520000009876543210987",
            googleMapsUrl = "https://maps.google.com/?q=24.6984,46.7028",
            latitude = 24.6984,
            longitude = 46.7028,
            stationName = "North Riyadh Depot",
            notes = "Executive villa unit with dedicated parking and proximity to commercial center."
          )
        )
        dao.insertAll(initialAccommodations)

        // Initial Stations
        val initialStations = listOf(
          Station(
            name = "Riyadh Central Hub",
            code = "RC-01",
            areaName = "Al Olaya District",
            city = "Riyadh",
            latitude = 24.7136,
            longitude = 46.6753,
            address = "King Fahd Road, Al Olaya, Riyadh",
            supervisorName = "Eng. Ahmed Al-Zahrani",
            supervisorPhone = "+966 50 111 2233",
            capacityLimit = 60,
            notes = "Main central operations station."
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
            supervisorPhone = "+966 55 444 5566",
            capacityLimit = 40,
            notes = "Logistics depot serving eastern residential staff units."
          ),
          Station(
            name = "North Riyadh Depot",
            code = "NR-03",
            areaName = "Al Sulaimaniya",
            city = "Riyadh",
            latitude = 24.6984,
            longitude = 46.7028,
            address = "Prince Mutaib bin Abdulaziz Rd, Al Sulaimaniya",
            supervisorName = "Eng. Tariq Al-Ghamdi",
            supervisorPhone = "+966 54 777 8899",
            capacityLimit = 35,
            notes = "Northern engineering and supervisor depot."
          )
        )
        stationDao.insertAll(initialStations)

        // Initial User Accounts
        val initialUsers = listOf(
          UserAccount(
            username = "admin",
            passwordHash = "322753",
            fullName = "Zawitco General Admin",
            role = "ADMIN",
            assignedStation = "Riyadh Central Hub",
            phone = "+966 50 000 0001"
          ),
          UserAccount(
            username = "staff1",
            passwordHash = "Zawitco",
            fullName = "Field Operations Staff",
            role = "USER",
            assignedStation = "East Riyadh Station",
            phone = "+966 55 123 4567"
          )
        )
        userDao.insertAll(initialUsers)

        // Initial Locations
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

        // Initial Requirements
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

        // Initial Reports
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
