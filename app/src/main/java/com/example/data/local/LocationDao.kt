package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Location
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for the Location entity providing queries and mutations
 * for coordinates, villa/housing details, and property metadata.
 */
@Dao
interface LocationDao {

  @Query("SELECT * FROM locations ORDER BY createdAt DESC")
  fun getAllLocations(): Flow<List<Location>>

  @Query("SELECT * FROM locations WHERE id = :id")
  fun getLocationById(id: Long): Flow<Location?>

  @Query("SELECT * FROM locations WHERE areaName = :areaName ORDER BY name ASC")
  fun getLocationsByArea(areaName: String): Flow<List<Location>>

  @Query("""
    SELECT * FROM locations 
    WHERE name LIKE '%' || :query || '%'
       OR areaName LIKE '%' || :query || '%'
       OR villaNumber LIKE '%' || :query || '%'
       OR address LIKE '%' || :query || '%'
       OR contactPerson LIKE '%' || :query || '%'
       OR contactPhone LIKE '%' || :query || '%'
    ORDER BY createdAt DESC
  """)
  fun searchLocations(query: String): Flow<List<Location>>

  @Query("SELECT * FROM locations WHERE propertyType = :propertyType ORDER BY name ASC")
  fun getLocationsByPropertyType(propertyType: String): Flow<List<Location>>

  @Query("SELECT COUNT(*) FROM locations")
  suspend fun getCount(): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertLocation(location: Location): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(locations: List<Location>)

  @Update
  suspend fun updateLocation(location: Location)

  @Delete
  suspend fun deleteLocation(location: Location)

  @Query("DELETE FROM locations WHERE id = :id")
  suspend fun deleteLocationById(id: Long)
}
