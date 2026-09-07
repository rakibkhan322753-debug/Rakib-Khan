package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Accommodation
import kotlinx.coroutines.flow.Flow

@Dao
interface AccommodationDao {

  @Query("SELECT * FROM accommodations ORDER BY createdAt DESC")
  fun getAllAccommodations(): Flow<List<Accommodation>>

  @Query("SELECT * FROM accommodations WHERE id = :id")
  fun getAccommodationById(id: Long): Flow<Accommodation?>

  @Query("""
    SELECT * FROM accommodations 
    WHERE areaName LIKE '%' || :query || '%' 
       OR villaNumber LIKE '%' || :query || '%'
       OR floorNumber LIKE '%' || :query || '%'
       OR roomNumber LIKE '%' || :query || '%'
       OR workerPhone LIKE '%' || :query || '%'
       OR ownerPhone LIKE '%' || :query || '%'
    ORDER BY createdAt DESC
  """)
  fun searchAccommodations(query: String): Flow<List<Accommodation>>

  @Query("SELECT COUNT(*) FROM accommodations")
  suspend fun getCount(): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAccommodation(accommodation: Accommodation): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(accommodations: List<Accommodation>)

  @Update
  suspend fun updateAccommodation(accommodation: Accommodation)

  @Delete
  suspend fun deleteAccommodation(accommodation: Accommodation)

  @Query("DELETE FROM accommodations WHERE id = :id")
  suspend fun deleteAccommodationById(id: Long)
}
