package com.theralieve.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.theralieve.data.local.entity.PlanEntity

@Dao
interface PlanDao {
    @Query("SELECT * FROM plans WHERE customerId = :customerId")
    suspend fun getPlans(customerId: String): List<PlanEntity>

    @Query("SELECT * FROM plans WHERE id = :planId")
    suspend fun getPlan(planId: String): PlanEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlans(plans: List<PlanEntity>)
    
    @Query("DELETE FROM plans WHERE customerId = :customerId")
    suspend fun deletePlans(customerId: String)
    
    @Query("SELECT * FROM plans")
    suspend fun getAllPlans(): List<PlanEntity>
}
















