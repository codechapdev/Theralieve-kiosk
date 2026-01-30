package com.codechaps.therajet.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.codechaps.therajet.data.local.dao.EquipmentDao
import com.codechaps.therajet.data.local.dao.PlanDao
import com.codechaps.therajet.data.local.entity.EquipmentEntity
import com.codechaps.therajet.data.local.entity.PlanEntity

@Database(
    entities = [PlanEntity::class, EquipmentEntity::class],
    version = 5,
    exportSchema = false
)
abstract class TheraJetDatabase : RoomDatabase() {
    abstract fun planDao(): PlanDao
    abstract fun equipmentDao(): EquipmentDao
    
    companion object {
        const val DATABASE_NAME = "therajet_database"
        
        // Migration from version 1 to 2: Add status and statusUpdatedAt columns to equipment table
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add status column (nullable text)
                database.execSQL("ALTER TABLE equipment ADD COLUMN status TEXT")
                // Add statusUpdatedAt column (nullable text)
                database.execSQL("ALTER TABLE equipment ADD COLUMN statusUpdatedAt TEXT")
            }
        }

        // Migration from version 2 to 3: Add frequency and frequencyLimit columns to plans table
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE plans ADD COLUMN frequency TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE plans ADD COLUMN frequencyLimit TEXT NOT NULL DEFAULT ''")
            }
        }
    }
}

