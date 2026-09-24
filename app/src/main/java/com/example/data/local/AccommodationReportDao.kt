package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AccommodationReport
import kotlinx.coroutines.flow.Flow

@Dao
interface AccommodationReportDao {

  @Query("SELECT * FROM accommodation_reports WHERE accommodationId = :accId ORDER BY createdAt DESC")
  fun getReportsForAccommodation(accId: Long): Flow<List<AccommodationReport>>

  @Query("SELECT * FROM accommodation_reports ORDER BY createdAt DESC")
  fun getAllReports(): Flow<List<AccommodationReport>>

  @Query("SELECT COUNT(*) FROM accommodation_reports")
  fun getTotalReportsCount(): Flow<Int>

  @Query("SELECT COUNT(*) FROM accommodation_reports WHERE accommodationId = :accId AND status != 'Resolved'")
  fun getActiveReportsCountForAcc(accId: Long): Flow<Int>

  @Query("SELECT * FROM accommodation_reports WHERE status != 'Resolved' ORDER BY createdAt DESC")
  suspend fun getOpenReportsList(): List<AccommodationReport>

  @Query("SELECT COUNT(*) FROM accommodation_reports WHERE status != 'Resolved'")
  suspend fun getOpenReportsCountSync(): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertReport(report: AccommodationReport): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(reports: List<AccommodationReport>)

  @Update
  suspend fun updateReport(report: AccommodationReport)

  @Delete
  suspend fun deleteReport(report: AccommodationReport)

  @Query("DELETE FROM accommodation_reports WHERE id = :id")
  suspend fun deleteById(id: Long)
}
