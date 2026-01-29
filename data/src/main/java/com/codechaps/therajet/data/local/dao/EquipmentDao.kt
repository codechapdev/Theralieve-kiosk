package com.codechaps.therajet.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.codechaps.therajet.data.local.entity.EquipmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EquipmentDao {
    @Query("SELECT * FROM equipment")
    suspend fun getAllEquipment(): List<EquipmentEntity>

    @Query("SELECT * FROM equipment")
    fun getEquipmentsFlow(): Flow<List<EquipmentEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipment(equipment: List<EquipmentEntity>)
    
    @Query("DELETE FROM equipment")
    suspend fun deleteAllEquipment()
    
    @Query("UPDATE equipment SET status = :status, statusUpdatedAt = :updatedAt WHERE deviceName = :deviceName")
    suspend fun updateEquipmentStatus(deviceName: String, status: String, updatedAt: String?)
    
    @Query("UPDATE equipment SET status = :status, statusUpdatedAt = :updatedAt WHERE deviceName IN (:deviceNames)")
    suspend fun updateMultipleEquipmentStatus(deviceNames: List<String>, status: String, updatedAt: String?)
}

