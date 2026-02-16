package com.theralieve.`data`.local

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.theralieve.`data`.local.dao.EquipmentDao
import com.theralieve.`data`.local.dao.EquipmentDao_Impl
import com.theralieve.`data`.local.dao.PlanDao
import com.theralieve.`data`.local.dao.PlanDao_Impl
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class TheraJetDatabase_Impl : TheraJetDatabase() {
  private val _planDao: Lazy<PlanDao> = lazy {
    PlanDao_Impl(this)
  }

  private val _equipmentDao: Lazy<EquipmentDao> = lazy {
    EquipmentDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(7, "73ad545e15e16117b08c043e2f1674c5", "c0bfabc3cd574cd5f2e8f37443ccc231") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `plans` (`id` INTEGER NOT NULL, `planName` TEXT NOT NULL, `planPrice` TEXT NOT NULL, `validity` TEXT NOT NULL, `bulletPoints` TEXT NOT NULL, `planDesc` TEXT NOT NULL, `image` TEXT NOT NULL, `customerId` TEXT NOT NULL, `planType` TEXT NOT NULL, `membershipType` TEXT NOT NULL, `currency` TEXT NOT NULL, `points` INTEGER NOT NULL, `status` INTEGER NOT NULL, `createdDate` TEXT NOT NULL, `updatedDate` TEXT NOT NULL, `equipmentJson` TEXT NOT NULL, `frequency` TEXT NOT NULL, `frequencyLimit` TEXT NOT NULL, `discount` TEXT, `discountType` TEXT, `discountValidity` TEXT, `employeeDiscount` TEXT, `isForEmployee` INTEGER NOT NULL, `isVipPlan` INTEGER NOT NULL, `billingPrice` TEXT, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `equipment` (`primaryKeyWithEquipmentId` TEXT NOT NULL, `equipmentId` INTEGER NOT NULL, `deviceName` TEXT NOT NULL, `equipmentCount` INTEGER NOT NULL, `equipmentName` TEXT NOT NULL, `equipmentPoint` TEXT NOT NULL, `equipmentPoints` INTEGER NOT NULL, `equipmentPrice` TEXT NOT NULL, `equipmentTime` TEXT NOT NULL, `image` TEXT NOT NULL, `isOneMinuteAccording` TEXT NOT NULL, `macAddress` TEXT NOT NULL, `equipmentDataJson` TEXT, `status` TEXT, `statusUpdatedAt` TEXT, `remainingBalance` TEXT, `sessionTime` TEXT, `planId` TEXT, PRIMARY KEY(`primaryKeyWithEquipmentId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '73ad545e15e16117b08c043e2f1674c5')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `plans`")
        connection.execSQL("DROP TABLE IF EXISTS `equipment`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
        val _columnsPlans: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsPlans.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlans.put("planName", TableInfo.Column("planName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlans.put("planPrice", TableInfo.Column("planPrice", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlans.put("validity", TableInfo.Column("validity", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlans.put("bulletPoints", TableInfo.Column("bulletPoints", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlans.put("planDesc", TableInfo.Column("planDesc", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlans.put("image", TableInfo.Column("image", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlans.put("customerId", TableInfo.Column("customerId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlans.put("planType", TableInfo.Column("planType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlans.put("membershipType", TableInfo.Column("membershipType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlans.put("currency", TableInfo.Column("currency", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlans.put("points", TableInfo.Column("points", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlans.put("status", TableInfo.Column("status", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlans.put("createdDate", TableInfo.Column("createdDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlans.put("updatedDate", TableInfo.Column("updatedDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlans.put("equipmentJson", TableInfo.Column("equipmentJson", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlans.put("frequency", TableInfo.Column("frequency", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlans.put("frequencyLimit", TableInfo.Column("frequencyLimit", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlans.put("discount", TableInfo.Column("discount", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlans.put("discountType", TableInfo.Column("discountType", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlans.put("discountValidity", TableInfo.Column("discountValidity", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlans.put("employeeDiscount", TableInfo.Column("employeeDiscount", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlans.put("isForEmployee", TableInfo.Column("isForEmployee", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlans.put("isVipPlan", TableInfo.Column("isVipPlan", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlans.put("billingPrice", TableInfo.Column("billingPrice", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysPlans: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesPlans: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoPlans: TableInfo = TableInfo("plans", _columnsPlans, _foreignKeysPlans, _indicesPlans)
        val _existingPlans: TableInfo = read(connection, "plans")
        if (!_infoPlans.equals(_existingPlans)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |plans(com.theralieve.data.local.entity.PlanEntity).
              | Expected:
              |""".trimMargin() + _infoPlans + """
              |
              | Found:
              |""".trimMargin() + _existingPlans)
        }
        val _columnsEquipment: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsEquipment.put("primaryKeyWithEquipmentId", TableInfo.Column("primaryKeyWithEquipmentId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEquipment.put("equipmentId", TableInfo.Column("equipmentId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEquipment.put("deviceName", TableInfo.Column("deviceName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEquipment.put("equipmentCount", TableInfo.Column("equipmentCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEquipment.put("equipmentName", TableInfo.Column("equipmentName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEquipment.put("equipmentPoint", TableInfo.Column("equipmentPoint", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEquipment.put("equipmentPoints", TableInfo.Column("equipmentPoints", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEquipment.put("equipmentPrice", TableInfo.Column("equipmentPrice", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEquipment.put("equipmentTime", TableInfo.Column("equipmentTime", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEquipment.put("image", TableInfo.Column("image", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEquipment.put("isOneMinuteAccording", TableInfo.Column("isOneMinuteAccording", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEquipment.put("macAddress", TableInfo.Column("macAddress", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEquipment.put("equipmentDataJson", TableInfo.Column("equipmentDataJson", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEquipment.put("status", TableInfo.Column("status", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEquipment.put("statusUpdatedAt", TableInfo.Column("statusUpdatedAt", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEquipment.put("remainingBalance", TableInfo.Column("remainingBalance", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEquipment.put("sessionTime", TableInfo.Column("sessionTime", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEquipment.put("planId", TableInfo.Column("planId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysEquipment: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesEquipment: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoEquipment: TableInfo = TableInfo("equipment", _columnsEquipment, _foreignKeysEquipment, _indicesEquipment)
        val _existingEquipment: TableInfo = read(connection, "equipment")
        if (!_infoEquipment.equals(_existingEquipment)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |equipment(com.theralieve.data.local.entity.EquipmentEntity).
              | Expected:
              |""".trimMargin() + _infoEquipment + """
              |
              | Found:
              |""".trimMargin() + _existingEquipment)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "plans", "equipment")
  }

  public override fun clearAllTables() {
    super.performClear(false, "plans", "equipment")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(PlanDao::class, PlanDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(EquipmentDao::class, EquipmentDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>): List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun planDao(): PlanDao = _planDao.value

  public override fun equipmentDao(): EquipmentDao = _equipmentDao.value
}
