package com.codechaps.therajet.`data`.local.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.codechaps.therajet.`data`.local.entity.PlanEntity
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class PlanDao_Impl(
  __db: RoomDatabase,
) : PlanDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfPlanEntity: EntityInsertAdapter<PlanEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfPlanEntity = object : EntityInsertAdapter<PlanEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `plans` (`id`,`planName`,`planPrice`,`validity`,`bulletPoints`,`planDesc`,`image`,`customerId`,`planType`,`membershipType`,`currency`,`points`,`status`,`createdDate`,`updatedDate`,`equipmentJson`,`frequency`,`frequencyLimit`,`discount`,`discountType`,`discountValidity`,`employeeDiscount`,`isForEmployee`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: PlanEntity) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindText(2, entity.planName)
        statement.bindText(3, entity.planPrice)
        statement.bindText(4, entity.validity)
        statement.bindText(5, entity.bulletPoints)
        statement.bindText(6, entity.planDesc)
        statement.bindText(7, entity.image)
        statement.bindText(8, entity.customerId)
        statement.bindText(9, entity.planType)
        statement.bindText(10, entity.membershipType)
        statement.bindText(11, entity.currency)
        statement.bindLong(12, entity.points.toLong())
        statement.bindLong(13, entity.status.toLong())
        statement.bindText(14, entity.createdDate)
        statement.bindText(15, entity.updatedDate)
        statement.bindText(16, entity.equipmentJson)
        statement.bindText(17, entity.frequency)
        statement.bindText(18, entity.frequencyLimit)
        val _tmpDiscount: String? = entity.discount
        if (_tmpDiscount == null) {
          statement.bindNull(19)
        } else {
          statement.bindText(19, _tmpDiscount)
        }
        val _tmpDiscountType: String? = entity.discountType
        if (_tmpDiscountType == null) {
          statement.bindNull(20)
        } else {
          statement.bindText(20, _tmpDiscountType)
        }
        val _tmpDiscountValidity: String? = entity.discountValidity
        if (_tmpDiscountValidity == null) {
          statement.bindNull(21)
        } else {
          statement.bindText(21, _tmpDiscountValidity)
        }
        val _tmpEmployeeDiscount: String? = entity.employeeDiscount
        if (_tmpEmployeeDiscount == null) {
          statement.bindNull(22)
        } else {
          statement.bindText(22, _tmpEmployeeDiscount)
        }
        statement.bindLong(23, entity.isForEmployee.toLong())
      }
    }
  }

  public override suspend fun insertPlans(plans: List<PlanEntity>): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfPlanEntity.insert(_connection, plans)
  }

  public override suspend fun getPlans(customerId: String): List<PlanEntity> {
    val _sql: String = "SELECT * FROM plans WHERE customerId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, customerId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPlanName: Int = getColumnIndexOrThrow(_stmt, "planName")
        val _columnIndexOfPlanPrice: Int = getColumnIndexOrThrow(_stmt, "planPrice")
        val _columnIndexOfValidity: Int = getColumnIndexOrThrow(_stmt, "validity")
        val _columnIndexOfBulletPoints: Int = getColumnIndexOrThrow(_stmt, "bulletPoints")
        val _columnIndexOfPlanDesc: Int = getColumnIndexOrThrow(_stmt, "planDesc")
        val _columnIndexOfImage: Int = getColumnIndexOrThrow(_stmt, "image")
        val _columnIndexOfCustomerId: Int = getColumnIndexOrThrow(_stmt, "customerId")
        val _columnIndexOfPlanType: Int = getColumnIndexOrThrow(_stmt, "planType")
        val _columnIndexOfMembershipType: Int = getColumnIndexOrThrow(_stmt, "membershipType")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfPoints: Int = getColumnIndexOrThrow(_stmt, "points")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCreatedDate: Int = getColumnIndexOrThrow(_stmt, "createdDate")
        val _columnIndexOfUpdatedDate: Int = getColumnIndexOrThrow(_stmt, "updatedDate")
        val _columnIndexOfEquipmentJson: Int = getColumnIndexOrThrow(_stmt, "equipmentJson")
        val _columnIndexOfFrequency: Int = getColumnIndexOrThrow(_stmt, "frequency")
        val _columnIndexOfFrequencyLimit: Int = getColumnIndexOrThrow(_stmt, "frequencyLimit")
        val _columnIndexOfDiscount: Int = getColumnIndexOrThrow(_stmt, "discount")
        val _columnIndexOfDiscountType: Int = getColumnIndexOrThrow(_stmt, "discountType")
        val _columnIndexOfDiscountValidity: Int = getColumnIndexOrThrow(_stmt, "discountValidity")
        val _columnIndexOfEmployeeDiscount: Int = getColumnIndexOrThrow(_stmt, "employeeDiscount")
        val _columnIndexOfIsForEmployee: Int = getColumnIndexOrThrow(_stmt, "isForEmployee")
        val _result: MutableList<PlanEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: PlanEntity
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpPlanName: String
          _tmpPlanName = _stmt.getText(_columnIndexOfPlanName)
          val _tmpPlanPrice: String
          _tmpPlanPrice = _stmt.getText(_columnIndexOfPlanPrice)
          val _tmpValidity: String
          _tmpValidity = _stmt.getText(_columnIndexOfValidity)
          val _tmpBulletPoints: String
          _tmpBulletPoints = _stmt.getText(_columnIndexOfBulletPoints)
          val _tmpPlanDesc: String
          _tmpPlanDesc = _stmt.getText(_columnIndexOfPlanDesc)
          val _tmpImage: String
          _tmpImage = _stmt.getText(_columnIndexOfImage)
          val _tmpCustomerId: String
          _tmpCustomerId = _stmt.getText(_columnIndexOfCustomerId)
          val _tmpPlanType: String
          _tmpPlanType = _stmt.getText(_columnIndexOfPlanType)
          val _tmpMembershipType: String
          _tmpMembershipType = _stmt.getText(_columnIndexOfMembershipType)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpPoints: Int
          _tmpPoints = _stmt.getLong(_columnIndexOfPoints).toInt()
          val _tmpStatus: Int
          _tmpStatus = _stmt.getLong(_columnIndexOfStatus).toInt()
          val _tmpCreatedDate: String
          _tmpCreatedDate = _stmt.getText(_columnIndexOfCreatedDate)
          val _tmpUpdatedDate: String
          _tmpUpdatedDate = _stmt.getText(_columnIndexOfUpdatedDate)
          val _tmpEquipmentJson: String
          _tmpEquipmentJson = _stmt.getText(_columnIndexOfEquipmentJson)
          val _tmpFrequency: String
          _tmpFrequency = _stmt.getText(_columnIndexOfFrequency)
          val _tmpFrequencyLimit: String
          _tmpFrequencyLimit = _stmt.getText(_columnIndexOfFrequencyLimit)
          val _tmpDiscount: String?
          if (_stmt.isNull(_columnIndexOfDiscount)) {
            _tmpDiscount = null
          } else {
            _tmpDiscount = _stmt.getText(_columnIndexOfDiscount)
          }
          val _tmpDiscountType: String?
          if (_stmt.isNull(_columnIndexOfDiscountType)) {
            _tmpDiscountType = null
          } else {
            _tmpDiscountType = _stmt.getText(_columnIndexOfDiscountType)
          }
          val _tmpDiscountValidity: String?
          if (_stmt.isNull(_columnIndexOfDiscountValidity)) {
            _tmpDiscountValidity = null
          } else {
            _tmpDiscountValidity = _stmt.getText(_columnIndexOfDiscountValidity)
          }
          val _tmpEmployeeDiscount: String?
          if (_stmt.isNull(_columnIndexOfEmployeeDiscount)) {
            _tmpEmployeeDiscount = null
          } else {
            _tmpEmployeeDiscount = _stmt.getText(_columnIndexOfEmployeeDiscount)
          }
          val _tmpIsForEmployee: Int
          _tmpIsForEmployee = _stmt.getLong(_columnIndexOfIsForEmployee).toInt()
          _item = PlanEntity(_tmpId,_tmpPlanName,_tmpPlanPrice,_tmpValidity,_tmpBulletPoints,_tmpPlanDesc,_tmpImage,_tmpCustomerId,_tmpPlanType,_tmpMembershipType,_tmpCurrency,_tmpPoints,_tmpStatus,_tmpCreatedDate,_tmpUpdatedDate,_tmpEquipmentJson,_tmpFrequency,_tmpFrequencyLimit,_tmpDiscount,_tmpDiscountType,_tmpDiscountValidity,_tmpEmployeeDiscount,_tmpIsForEmployee)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getPlan(planId: String): PlanEntity? {
    val _sql: String = "SELECT * FROM plans WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, planId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPlanName: Int = getColumnIndexOrThrow(_stmt, "planName")
        val _columnIndexOfPlanPrice: Int = getColumnIndexOrThrow(_stmt, "planPrice")
        val _columnIndexOfValidity: Int = getColumnIndexOrThrow(_stmt, "validity")
        val _columnIndexOfBulletPoints: Int = getColumnIndexOrThrow(_stmt, "bulletPoints")
        val _columnIndexOfPlanDesc: Int = getColumnIndexOrThrow(_stmt, "planDesc")
        val _columnIndexOfImage: Int = getColumnIndexOrThrow(_stmt, "image")
        val _columnIndexOfCustomerId: Int = getColumnIndexOrThrow(_stmt, "customerId")
        val _columnIndexOfPlanType: Int = getColumnIndexOrThrow(_stmt, "planType")
        val _columnIndexOfMembershipType: Int = getColumnIndexOrThrow(_stmt, "membershipType")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfPoints: Int = getColumnIndexOrThrow(_stmt, "points")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCreatedDate: Int = getColumnIndexOrThrow(_stmt, "createdDate")
        val _columnIndexOfUpdatedDate: Int = getColumnIndexOrThrow(_stmt, "updatedDate")
        val _columnIndexOfEquipmentJson: Int = getColumnIndexOrThrow(_stmt, "equipmentJson")
        val _columnIndexOfFrequency: Int = getColumnIndexOrThrow(_stmt, "frequency")
        val _columnIndexOfFrequencyLimit: Int = getColumnIndexOrThrow(_stmt, "frequencyLimit")
        val _columnIndexOfDiscount: Int = getColumnIndexOrThrow(_stmt, "discount")
        val _columnIndexOfDiscountType: Int = getColumnIndexOrThrow(_stmt, "discountType")
        val _columnIndexOfDiscountValidity: Int = getColumnIndexOrThrow(_stmt, "discountValidity")
        val _columnIndexOfEmployeeDiscount: Int = getColumnIndexOrThrow(_stmt, "employeeDiscount")
        val _columnIndexOfIsForEmployee: Int = getColumnIndexOrThrow(_stmt, "isForEmployee")
        val _result: PlanEntity?
        if (_stmt.step()) {
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpPlanName: String
          _tmpPlanName = _stmt.getText(_columnIndexOfPlanName)
          val _tmpPlanPrice: String
          _tmpPlanPrice = _stmt.getText(_columnIndexOfPlanPrice)
          val _tmpValidity: String
          _tmpValidity = _stmt.getText(_columnIndexOfValidity)
          val _tmpBulletPoints: String
          _tmpBulletPoints = _stmt.getText(_columnIndexOfBulletPoints)
          val _tmpPlanDesc: String
          _tmpPlanDesc = _stmt.getText(_columnIndexOfPlanDesc)
          val _tmpImage: String
          _tmpImage = _stmt.getText(_columnIndexOfImage)
          val _tmpCustomerId: String
          _tmpCustomerId = _stmt.getText(_columnIndexOfCustomerId)
          val _tmpPlanType: String
          _tmpPlanType = _stmt.getText(_columnIndexOfPlanType)
          val _tmpMembershipType: String
          _tmpMembershipType = _stmt.getText(_columnIndexOfMembershipType)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpPoints: Int
          _tmpPoints = _stmt.getLong(_columnIndexOfPoints).toInt()
          val _tmpStatus: Int
          _tmpStatus = _stmt.getLong(_columnIndexOfStatus).toInt()
          val _tmpCreatedDate: String
          _tmpCreatedDate = _stmt.getText(_columnIndexOfCreatedDate)
          val _tmpUpdatedDate: String
          _tmpUpdatedDate = _stmt.getText(_columnIndexOfUpdatedDate)
          val _tmpEquipmentJson: String
          _tmpEquipmentJson = _stmt.getText(_columnIndexOfEquipmentJson)
          val _tmpFrequency: String
          _tmpFrequency = _stmt.getText(_columnIndexOfFrequency)
          val _tmpFrequencyLimit: String
          _tmpFrequencyLimit = _stmt.getText(_columnIndexOfFrequencyLimit)
          val _tmpDiscount: String?
          if (_stmt.isNull(_columnIndexOfDiscount)) {
            _tmpDiscount = null
          } else {
            _tmpDiscount = _stmt.getText(_columnIndexOfDiscount)
          }
          val _tmpDiscountType: String?
          if (_stmt.isNull(_columnIndexOfDiscountType)) {
            _tmpDiscountType = null
          } else {
            _tmpDiscountType = _stmt.getText(_columnIndexOfDiscountType)
          }
          val _tmpDiscountValidity: String?
          if (_stmt.isNull(_columnIndexOfDiscountValidity)) {
            _tmpDiscountValidity = null
          } else {
            _tmpDiscountValidity = _stmt.getText(_columnIndexOfDiscountValidity)
          }
          val _tmpEmployeeDiscount: String?
          if (_stmt.isNull(_columnIndexOfEmployeeDiscount)) {
            _tmpEmployeeDiscount = null
          } else {
            _tmpEmployeeDiscount = _stmt.getText(_columnIndexOfEmployeeDiscount)
          }
          val _tmpIsForEmployee: Int
          _tmpIsForEmployee = _stmt.getLong(_columnIndexOfIsForEmployee).toInt()
          _result = PlanEntity(_tmpId,_tmpPlanName,_tmpPlanPrice,_tmpValidity,_tmpBulletPoints,_tmpPlanDesc,_tmpImage,_tmpCustomerId,_tmpPlanType,_tmpMembershipType,_tmpCurrency,_tmpPoints,_tmpStatus,_tmpCreatedDate,_tmpUpdatedDate,_tmpEquipmentJson,_tmpFrequency,_tmpFrequencyLimit,_tmpDiscount,_tmpDiscountType,_tmpDiscountValidity,_tmpEmployeeDiscount,_tmpIsForEmployee)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllPlans(): List<PlanEntity> {
    val _sql: String = "SELECT * FROM plans"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPlanName: Int = getColumnIndexOrThrow(_stmt, "planName")
        val _columnIndexOfPlanPrice: Int = getColumnIndexOrThrow(_stmt, "planPrice")
        val _columnIndexOfValidity: Int = getColumnIndexOrThrow(_stmt, "validity")
        val _columnIndexOfBulletPoints: Int = getColumnIndexOrThrow(_stmt, "bulletPoints")
        val _columnIndexOfPlanDesc: Int = getColumnIndexOrThrow(_stmt, "planDesc")
        val _columnIndexOfImage: Int = getColumnIndexOrThrow(_stmt, "image")
        val _columnIndexOfCustomerId: Int = getColumnIndexOrThrow(_stmt, "customerId")
        val _columnIndexOfPlanType: Int = getColumnIndexOrThrow(_stmt, "planType")
        val _columnIndexOfMembershipType: Int = getColumnIndexOrThrow(_stmt, "membershipType")
        val _columnIndexOfCurrency: Int = getColumnIndexOrThrow(_stmt, "currency")
        val _columnIndexOfPoints: Int = getColumnIndexOrThrow(_stmt, "points")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCreatedDate: Int = getColumnIndexOrThrow(_stmt, "createdDate")
        val _columnIndexOfUpdatedDate: Int = getColumnIndexOrThrow(_stmt, "updatedDate")
        val _columnIndexOfEquipmentJson: Int = getColumnIndexOrThrow(_stmt, "equipmentJson")
        val _columnIndexOfFrequency: Int = getColumnIndexOrThrow(_stmt, "frequency")
        val _columnIndexOfFrequencyLimit: Int = getColumnIndexOrThrow(_stmt, "frequencyLimit")
        val _columnIndexOfDiscount: Int = getColumnIndexOrThrow(_stmt, "discount")
        val _columnIndexOfDiscountType: Int = getColumnIndexOrThrow(_stmt, "discountType")
        val _columnIndexOfDiscountValidity: Int = getColumnIndexOrThrow(_stmt, "discountValidity")
        val _columnIndexOfEmployeeDiscount: Int = getColumnIndexOrThrow(_stmt, "employeeDiscount")
        val _columnIndexOfIsForEmployee: Int = getColumnIndexOrThrow(_stmt, "isForEmployee")
        val _result: MutableList<PlanEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: PlanEntity
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpPlanName: String
          _tmpPlanName = _stmt.getText(_columnIndexOfPlanName)
          val _tmpPlanPrice: String
          _tmpPlanPrice = _stmt.getText(_columnIndexOfPlanPrice)
          val _tmpValidity: String
          _tmpValidity = _stmt.getText(_columnIndexOfValidity)
          val _tmpBulletPoints: String
          _tmpBulletPoints = _stmt.getText(_columnIndexOfBulletPoints)
          val _tmpPlanDesc: String
          _tmpPlanDesc = _stmt.getText(_columnIndexOfPlanDesc)
          val _tmpImage: String
          _tmpImage = _stmt.getText(_columnIndexOfImage)
          val _tmpCustomerId: String
          _tmpCustomerId = _stmt.getText(_columnIndexOfCustomerId)
          val _tmpPlanType: String
          _tmpPlanType = _stmt.getText(_columnIndexOfPlanType)
          val _tmpMembershipType: String
          _tmpMembershipType = _stmt.getText(_columnIndexOfMembershipType)
          val _tmpCurrency: String
          _tmpCurrency = _stmt.getText(_columnIndexOfCurrency)
          val _tmpPoints: Int
          _tmpPoints = _stmt.getLong(_columnIndexOfPoints).toInt()
          val _tmpStatus: Int
          _tmpStatus = _stmt.getLong(_columnIndexOfStatus).toInt()
          val _tmpCreatedDate: String
          _tmpCreatedDate = _stmt.getText(_columnIndexOfCreatedDate)
          val _tmpUpdatedDate: String
          _tmpUpdatedDate = _stmt.getText(_columnIndexOfUpdatedDate)
          val _tmpEquipmentJson: String
          _tmpEquipmentJson = _stmt.getText(_columnIndexOfEquipmentJson)
          val _tmpFrequency: String
          _tmpFrequency = _stmt.getText(_columnIndexOfFrequency)
          val _tmpFrequencyLimit: String
          _tmpFrequencyLimit = _stmt.getText(_columnIndexOfFrequencyLimit)
          val _tmpDiscount: String?
          if (_stmt.isNull(_columnIndexOfDiscount)) {
            _tmpDiscount = null
          } else {
            _tmpDiscount = _stmt.getText(_columnIndexOfDiscount)
          }
          val _tmpDiscountType: String?
          if (_stmt.isNull(_columnIndexOfDiscountType)) {
            _tmpDiscountType = null
          } else {
            _tmpDiscountType = _stmt.getText(_columnIndexOfDiscountType)
          }
          val _tmpDiscountValidity: String?
          if (_stmt.isNull(_columnIndexOfDiscountValidity)) {
            _tmpDiscountValidity = null
          } else {
            _tmpDiscountValidity = _stmt.getText(_columnIndexOfDiscountValidity)
          }
          val _tmpEmployeeDiscount: String?
          if (_stmt.isNull(_columnIndexOfEmployeeDiscount)) {
            _tmpEmployeeDiscount = null
          } else {
            _tmpEmployeeDiscount = _stmt.getText(_columnIndexOfEmployeeDiscount)
          }
          val _tmpIsForEmployee: Int
          _tmpIsForEmployee = _stmt.getLong(_columnIndexOfIsForEmployee).toInt()
          _item = PlanEntity(_tmpId,_tmpPlanName,_tmpPlanPrice,_tmpValidity,_tmpBulletPoints,_tmpPlanDesc,_tmpImage,_tmpCustomerId,_tmpPlanType,_tmpMembershipType,_tmpCurrency,_tmpPoints,_tmpStatus,_tmpCreatedDate,_tmpUpdatedDate,_tmpEquipmentJson,_tmpFrequency,_tmpFrequencyLimit,_tmpDiscount,_tmpDiscountType,_tmpDiscountValidity,_tmpEmployeeDiscount,_tmpIsForEmployee)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deletePlans(customerId: String) {
    val _sql: String = "DELETE FROM plans WHERE customerId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, customerId)
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
