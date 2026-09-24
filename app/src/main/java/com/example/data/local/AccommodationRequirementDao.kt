package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AccommodationRequirement
import kotlinx.coroutines.flow.Flow

@Dao
interface AccommodationRequirementDao {

  @Query("SELECT * FROM accommodation_requirements WHERE accommodationId = :accId ORDER BY createdAt DESC")
  fun getRequirementsForAccommodation(accId: Long): Flow<List<AccommodationRequirement>>

  @Query("SELECT * FROM accommodation_requirements ORDER BY createdAt DESC")
  fun getAllRequirements(): Flow<List<AccommodationRequirement>>

  @Query("SELECT COUNT(*) FROM accommodation_requirements")
  fun getTotalRequirementsCount(): Flow<Int>

  @Query("SELECT COUNT(*) FROM accommodation_requirements WHERE accommodationId = :accId AND status != 'Fulfilled'")
  fun getActiveRequirementsCountForAcc(accId: Long): Flow<Int>

  @Query("SELECT * FROM accommodation_requirements WHERE status != 'Fulfilled' ORDER BY createdAt DESC")
  suspend fun getPendingRequirementsList(): List<AccommodationRequirement>

  @Query("SELECT COUNT(*) FROM accommodation_requirements WHERE status != 'Fulfilled'")
  suspend fun getPendingRequirementsCountSync(): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRequirement(req: AccommodationRequirement): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(requirements: List<AccommodationRequirement>)

  @Update
  suspend fun updateRequirement(req: AccommodationRequirement)

  @Delete
  suspend fun deleteRequirement(req: AccommodationRequirement)

  @Query("DELETE FROM accommodation_requirements WHERE id = :id")
  suspend fun deleteById(id: Long)
}
