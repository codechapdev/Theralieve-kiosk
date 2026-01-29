package com.codechaps.therajet.`data`.local.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.codechaps.therajet.`data`.local.entity.EquipmentEntity
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlin.text.StringBuilder
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class EquipmentDao_Impl(
  __db: RoomDatabase,
) : EquipmentDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfEquipmentEntity: EntityInsertAdapter<EquipmentEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfEquipmentEntity = object : EntityInsertAdapter<EquipmentEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `equipment` (`equipmentId`,`deviceName`,`equipmentCount`,`equipmentName`,`equipmentPoint`,`equipmentPoints`,`equipmentPrice`,`equipmentTime`,`image`,`isOneMinuteAccording`,`macAddress`,`equipmentDataJson`,`status`,`statusUpdatedAt`,`remainingBalance`,`sessionTime`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: EquipmentEntity) {
        statement.bindLong(1, entity.equipmentId.toLong())
        statement.bindText(2, entity.deviceName)
        statement.bindLong(3, entity.equipmentCount.toLong())
        statement.bindText(4, entity.equipmentName)
        statement.bindText(5, entity.equipmentPoint)
        statement.bindLong(6, entity.equipmentPoints.toLong())
        statement.bindText(7, entity.equipmentPrice)
        statement.bindText(8, entity.equipmentTime)
        statement.bindText(9, entity.image)
        statement.bindText(10, entity.isOneMinuteAccording)
        statement.bindText(11, entity.macAddress)
        val _tmpEquipmentDataJson: String? = entity.equipmentDataJson
        if (_tmpEquipmentDataJson == null) {
          statement.bindNull(12)
        } else {
          statement.bindText(12, _tmpEquipmentDataJson)
        }
        val _tmpStatus: String? = entity.status
        if (_tmpStatus == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpStatus)
        }
        val _tmpStatusUpdatedAt: String? = entity.statusUpdatedAt
        if (_tmpStatusUpdatedAt == null) {
          statement.bindNull(14)
        } else {
          statement.bindText(14, _tmpStatusUpdatedAt)
        }
        val _tmpRemainingBalance: String? = entity.remainingBalance
        if (_tmpRemainingBalance == null) {
          statement.bindNull(15)
        } else {
          statement.bindText(15, _tmpRemainingBalance)
        }
        val _tmpSessionTime: String? = entity.sessionTime
        if (_tmpSessionTime == null) {
          statement.bindNull(16)
        } else {
          statement.bindText(16, _tmpSessionTime)
        }
      }
    }
  }

  public override suspend fun insertEquipment(equipment: List<EquipmentEntity>): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfEquipmentEntity.insert(_connection, equipment)
  }

  public override suspend fun getAllEquipment(): List<EquipmentEntity> {
    val _sql: String = "SELECT * FROM equipment"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfEquipmentId: Int = getColumnIndexOrThrow(_stmt, "equipmentId")
        val _columnIndexOfDeviceName: Int = getColumnIndexOrThrow(_stmt, "deviceName")
        val _columnIndexOfEquipmentCount: Int = getColumnIndexOrThrow(_stmt, "equipmentCount")
        val _columnIndexOfEquipmentName: Int = getColumnIndexOrThrow(_stmt, "equipmentName")
        val _columnIndexOfEquipmentPoint: Int = getColumnIndexOrThrow(_stmt, "equipmentPoint")
        val _columnIndexOfEquipmentPoints: Int = getColumnIndexOrThrow(_stmt, "equipmentPoints")
        val _columnIndexOfEquipmentPrice: Int = getColumnIndexOrThrow(_stmt, "equipmentPrice")
        val _columnIndexOfEquipmentTime: Int = getColumnIndexOrThrow(_stmt, "equipmentTime")
        val _columnIndexOfImage: Int = getColumnIndexOrThrow(_stmt, "image")
        val _columnIndexOfIsOneMinuteAccording: Int = getColumnIndexOrThrow(_stmt, "isOneMinuteAccording")
        val _columnIndexOfMacAddress: Int = getColumnIndexOrThrow(_stmt, "macAddress")
        val _columnIndexOfEquipmentDataJson: Int = getColumnIndexOrThrow(_stmt, "equipmentDataJson")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfStatusUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "statusUpdatedAt")
        val _columnIndexOfRemainingBalance: Int = getColumnIndexOrThrow(_stmt, "remainingBalance")
        val _columnIndexOfSessionTime: Int = getColumnIndexOrThrow(_stmt, "sessionTime")
        val _result: MutableList<EquipmentEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: EquipmentEntity
          val _tmpEquipmentId: Int
          _tmpEquipmentId = _stmt.getLong(_columnIndexOfEquipmentId).toInt()
          val _tmpDeviceName: String
          _tmpDeviceName = _stmt.getText(_columnIndexOfDeviceName)
          val _tmpEquipmentCount: Int
          _tmpEquipmentCount = _stmt.getLong(_columnIndexOfEquipmentCount).toInt()
          val _tmpEquipmentName: String
          _tmpEquipmentName = _stmt.getText(_columnIndexOfEquipmentName)
          val _tmpEquipmentPoint: String
          _tmpEquipmentPoint = _stmt.getText(_columnIndexOfEquipmentPoint)
          val _tmpEquipmentPoints: Int
          _tmpEquipmentPoints = _stmt.getLong(_columnIndexOfEquipmentPoints).toInt()
          val _tmpEquipmentPrice: String
          _tmpEquipmentPrice = _stmt.getText(_columnIndexOfEquipmentPrice)
          val _tmpEquipmentTime: String
          _tmpEquipmentTime = _stmt.getText(_columnIndexOfEquipmentTime)
          val _tmpImage: String
          _tmpImage = _stmt.getText(_columnIndexOfImage)
          val _tmpIsOneMinuteAccording: String
          _tmpIsOneMinuteAccording = _stmt.getText(_columnIndexOfIsOneMinuteAccording)
          val _tmpMacAddress: String
          _tmpMacAddress = _stmt.getText(_columnIndexOfMacAddress)
          val _tmpEquipmentDataJson: String?
          if (_stmt.isNull(_columnIndexOfEquipmentDataJson)) {
            _tmpEquipmentDataJson = null
          } else {
            _tmpEquipmentDataJson = _stmt.getText(_columnIndexOfEquipmentDataJson)
          }
          val _tmpStatus: String?
          if (_stmt.isNull(_columnIndexOfStatus)) {
            _tmpStatus = null
          } else {
            _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          }
          val _tmpStatusUpdatedAt: String?
          if (_stmt.isNull(_columnIndexOfStatusUpdatedAt)) {
            _tmpStatusUpdatedAt = null
          } else {
            _tmpStatusUpdatedAt = _stmt.getText(_columnIndexOfStatusUpdatedAt)
          }
          val _tmpRemainingBalance: String?
          if (_stmt.isNull(_columnIndexOfRemainingBalance)) {
            _tmpRemainingBalance = null
          } else {
            _tmpRemainingBalance = _stmt.getText(_columnIndexOfRemainingBalance)
          }
          val _tmpSessionTime: String?
          if (_stmt.isNull(_columnIndexOfSessionTime)) {
            _tmpSessionTime = null
          } else {
            _tmpSessionTime = _stmt.getText(_columnIndexOfSessionTime)
          }
          _item = EquipmentEntity(_tmpEquipmentId,_tmpDeviceName,_tmpEquipmentCount,_tmpEquipmentName,_tmpEquipmentPoint,_tmpEquipmentPoints,_tmpEquipmentPrice,_tmpEquipmentTime,_tmpImage,_tmpIsOneMinuteAccording,_tmpMacAddress,_tmpEquipmentDataJson,_tmpStatus,_tmpStatusUpdatedAt,_tmpRemainingBalance,_tmpSessionTime)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getEquipmentsFlow(): Flow<List<EquipmentEntity>> {
    val _sql: String = "SELECT * FROM equipment"
    return createFlow(__db, false, arrayOf("equipment")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfEquipmentId: Int = getColumnIndexOrThrow(_stmt, "equipmentId")
        val _columnIndexOfDeviceName: Int = getColumnIndexOrThrow(_stmt, "deviceName")
        val _columnIndexOfEquipmentCount: Int = getColumnIndexOrThrow(_stmt, "equipmentCount")
        val _columnIndexOfEquipmentName: Int = getColumnIndexOrThrow(_stmt, "equipmentName")
        val _columnIndexOfEquipmentPoint: Int = getColumnIndexOrThrow(_stmt, "equipmentPoint")
        val _columnIndexOfEquipmentPoints: Int = getColumnIndexOrThrow(_stmt, "equipmentPoints")
        val _columnIndexOfEquipmentPrice: Int = getColumnIndexOrThrow(_stmt, "equipmentPrice")
        val _columnIndexOfEquipmentTime: Int = getColumnIndexOrThrow(_stmt, "equipmentTime")
        val _columnIndexOfImage: Int = getColumnIndexOrThrow(_stmt, "image")
        val _columnIndexOfIsOneMinuteAccording: Int = getColumnIndexOrThrow(_stmt, "isOneMinuteAccording")
        val _columnIndexOfMacAddress: Int = getColumnIndexOrThrow(_stmt, "macAddress")
        val _columnIndexOfEquipmentDataJson: Int = getColumnIndexOrThrow(_stmt, "equipmentDataJson")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfStatusUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "statusUpdatedAt")
        val _columnIndexOfRemainingBalance: Int = getColumnIndexOrThrow(_stmt, "remainingBalance")
        val _columnIndexOfSessionTime: Int = getColumnIndexOrThrow(_stmt, "sessionTime")
        val _result: MutableList<EquipmentEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: EquipmentEntity
          val _tmpEquipmentId: Int
          _tmpEquipmentId = _stmt.getLong(_columnIndexOfEquipmentId).toInt()
          val _tmpDeviceName: String
          _tmpDeviceName = _stmt.getText(_columnIndexOfDeviceName)
          val _tmpEquipmentCount: Int
          _tmpEquipmentCount = _stmt.getLong(_columnIndexOfEquipmentCount).toInt()
          val _tmpEquipmentName: String
          _tmpEquipmentName = _stmt.getText(_columnIndexOfEquipmentName)
          val _tmpEquipmentPoint: String
          _tmpEquipmentPoint = _stmt.getText(_columnIndexOfEquipmentPoint)
          val _tmpEquipmentPoints: Int
          _tmpEquipmentPoints = _stmt.getLong(_columnIndexOfEquipmentPoints).toInt()
          val _tmpEquipmentPrice: String
          _tmpEquipmentPrice = _stmt.getText(_columnIndexOfEquipmentPrice)
          val _tmpEquipmentTime: String
          _tmpEquipmentTime = _stmt.getText(_columnIndexOfEquipmentTime)
          val _tmpImage: String
          _tmpImage = _stmt.getText(_columnIndexOfImage)
          val _tmpIsOneMinuteAccording: String
          _tmpIsOneMinuteAccording = _stmt.getText(_columnIndexOfIsOneMinuteAccording)
          val _tmpMacAddress: String
          _tmpMacAddress = _stmt.getText(_columnIndexOfMacAddress)
          val _tmpEquipmentDataJson: String?
          if (_stmt.isNull(_columnIndexOfEquipmentDataJson)) {
            _tmpEquipmentDataJson = null
          } else {
            _tmpEquipmentDataJson = _stmt.getText(_columnIndexOfEquipmentDataJson)
          }
          val _tmpStatus: String?
          if (_stmt.isNull(_columnIndexOfStatus)) {
            _tmpStatus = null
          } else {
            _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          }
          val _tmpStatusUpdatedAt: String?
          if (_stmt.isNull(_columnIndexOfStatusUpdatedAt)) {
            _tmpStatusUpdatedAt = null
          } else {
            _tmpStatusUpdatedAt = _stmt.getText(_columnIndexOfStatusUpdatedAt)
          }
          val _tmpRemainingBalance: String?
          if (_stmt.isNull(_columnIndexOfRemainingBalance)) {
            _tmpRemainingBalance = null
          } else {
            _tmpRemainingBalance = _stmt.getText(_columnIndexOfRemainingBalance)
          }
          val _tmpSessionTime: String?
          if (_stmt.isNull(_columnIndexOfSessionTime)) {
            _tmpSessionTime = null
          } else {
            _tmpSessionTime = _stmt.getText(_columnIndexOfSessionTime)
          }
          _item = EquipmentEntity(_tmpEquipmentId,_tmpDeviceName,_tmpEquipmentCount,_tmpEquipmentName,_tmpEquipmentPoint,_tmpEquipmentPoints,_tmpEquipmentPrice,_tmpEquipmentTime,_tmpImage,_tmpIsOneMinuteAccording,_tmpMacAddress,_tmpEquipmentDataJson,_tmpStatus,_tmpStatusUpdatedAt,_tmpRemainingBalance,_tmpSessionTime)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAllEquipment() {
    val _sql: String = "DELETE FROM equipment"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateEquipmentStatus(
    deviceName: String,
    status: String,
    updatedAt: String?,
  ) {
    val _sql: String = "UPDATE equipment SET status = ?, statusUpdatedAt = ? WHERE deviceName = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        _argIndex = 2
        if (updatedAt == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, updatedAt)
        }
        _argIndex = 3
        _stmt.bindText(_argIndex, deviceName)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateMultipleEquipmentStatus(
    deviceNames: List<String>,
    status: String,
    updatedAt: String?,
  ) {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("UPDATE equipment SET status = ")
    _stringBuilder.append("?")
    _stringBuilder.append(", statusUpdatedAt = ")
    _stringBuilder.append("?")
    _stringBuilder.append(" WHERE deviceName IN (")
    val _inputSize: Int = deviceNames.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        _argIndex = 2
        if (updatedAt == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, updatedAt)
        }
        _argIndex = 3
        for (_item: String in deviceNames) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
