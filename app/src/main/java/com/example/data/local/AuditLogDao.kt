package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.AuditLog
import kotlinx.coroutines.flow.Flow

@Dao
interface AuditLogDao {

  @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
  fun getAllLogs(): Flow<List<AuditLog>>

  @Query("SELECT * FROM audit_logs WHERE actionType LIKE '%' || :filter || '%' OR entityType LIKE '%' || :filter || '%' OR entityIdentifier LIKE '%' || :filter || '%' OR adminUsername LIKE '%' || :filter || '%' ORDER BY timestamp DESC")
  fun searchLogs(filter: String): Flow<List<AuditLog>>

  @Query("SELECT COUNT(*) FROM audit_logs")
  fun getTotalLogsCount(): Flow<Int>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertLog(log: AuditLog): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(logs: List<AuditLog>)

  @Query("DELETE FROM audit_logs")
  suspend fun clearLogs()
}
