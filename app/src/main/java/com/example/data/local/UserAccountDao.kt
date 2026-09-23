package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.UserAccount
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAccountDao {
  @Query("SELECT * FROM user_accounts ORDER BY fullName ASC")
  fun getAllUsers(): Flow<List<UserAccount>>

  @Query("SELECT * FROM user_accounts WHERE LOWER(username) = LOWER(:username) LIMIT 1")
  suspend fun findByUsername(username: String): UserAccount?

  @Query("SELECT * FROM user_accounts WHERE LOWER(username) = LOWER(:username) AND passwordHash = :password LIMIT 1")
  suspend fun authenticate(username: String, password: String): UserAccount?

  @Query("SELECT COUNT(*) FROM user_accounts")
  suspend fun getCount(): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(user: UserAccount): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(users: List<UserAccount>)

  @Update
  suspend fun update(user: UserAccount)

  @Delete
  suspend fun delete(user: UserAccount)

  @Query("DELETE FROM user_accounts WHERE id = :id")
  suspend fun deleteById(id: Long)
}
