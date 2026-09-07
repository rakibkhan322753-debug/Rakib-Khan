package com.example.data.repository

import com.example.data.local.AccommodationDao
import com.example.data.model.Accommodation
import kotlinx.coroutines.flow.Flow

class AccommodationRepository(private val dao: AccommodationDao) {

  val allAccommodations: Flow<List<Accommodation>> = dao.getAllAccommodations()

  fun getAccommodationById(id: Long): Flow<Accommodation?> = dao.getAccommodationById(id)

  fun searchAccommodations(query: String): Flow<List<Accommodation>> {
    return if (query.isBlank()) {
      dao.getAllAccommodations()
    } else {
      dao.searchAccommodations(query.trim())
    }
  }

  suspend fun insert(accommodation: Accommodation): Long = dao.insertAccommodation(accommodation)

  suspend fun update(accommodation: Accommodation) = dao.updateAccommodation(accommodation)

  suspend fun delete(accommodation: Accommodation) = dao.deleteAccommodation(accommodation)

  suspend fun deleteById(id: Long) = dao.deleteAccommodationById(id)

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
      dao.insertAll(defaultItems)
    }
  }
}
