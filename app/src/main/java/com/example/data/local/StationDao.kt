package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Station
import kotlinx.coroutines.flow.Flow

@Dao
interface StationDao {
  @Query("SELECT * FROM stations ORDER BY name ASC")
  fun getAllStations(): Flow<List<Station>>

  @Query("SELECT * FROM stations WHERE id = :id LIMIT 1")
  fun getStationById(id: Long): Flow<Station?>

  @Query("SELECT COUNT(*) FROM stations")
  suspend fun getCount(): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(station: Station): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(stations: List<Station>)

  @Update
  suspend fun update(station: Station)

  @Delete
  suspend fun delete(station: Station)

  @Query("DELETE FROM stations WHERE id = :id")
  suspend fun deleteById(id: Long)
}
