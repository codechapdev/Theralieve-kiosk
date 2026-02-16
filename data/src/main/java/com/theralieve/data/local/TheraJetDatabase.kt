package com.theralieve.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.theralieve.data.local.dao.EquipmentDao
import com.theralieve.data.local.dao.PlanDao
import com.theralieve.data.local.entity.EquipmentEntity
import com.theralieve.data.local.entity.PlanEntity

@Database(
    entities = [PlanEntity::class, EquipmentEntity::class],
    version = 7,
    exportSchema = false
)
abstract class TheraJetDatabase : RoomDatabase() {
    abstract fun planDao(): PlanDao
    abstract fun equipmentDao(): EquipmentDao
    
    companion object {
        const val DATABASE_NAME = "therajet_database"

    }
}

