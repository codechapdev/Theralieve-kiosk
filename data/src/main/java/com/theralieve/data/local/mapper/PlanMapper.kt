package com.theralieve.data.local.mapper

import com.theralieve.data.local.entity.PlanEntity
import com.theralieve.domain.model.Plan
import com.theralieve.domain.model.PlanDetail
import com.theralieve.domain.model.PlanEquipment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private val gson = Gson()

fun PlanEntity.toDomain(): Plan {
    val equipment = try {
        val listType = object : TypeToken<List<PlanEquipment>>() {}.type
        gson.fromJson<List<PlanEquipment>>(equipmentJson, listType) ?: emptyList()
    } catch (e: Exception) {
        emptyList()
    }
    
    val planDetail = PlanDetail(
        bullet_points = bulletPoints,
        created_date = createdDate,
        currency = currency,
        customer_id = customerId,
        discount = discount, // Not stored in entity
        discount_type = discountType, // Not stored in entity
        discount_validity = discountValidity, // Not stored in entity
        employee_discount = employeeDiscount, // Not stored in entity
        frequency = frequency,
        frequency_limit = frequencyLimit,
        gift_points = 0, // Not stored in entity
        id = id,
        image = image,
        introductory_plan = 0, // Not stored in entity
        is_for_employee = isForEmployee, // Not stored in entity
        is_gift = 0, // Not stored in entity
        is_vip_plan = isVipPlan, // Not stored in entity
        membership_type = membershipType,
        order_plan = "", // Not stored in entity
        plan_desc = planDesc,
        plan_name = planName,
        plan_price = planPrice,
        plan_type = planType,
        points = points,
        status = status,
        term = "", // Not stored in entity
        updated_date = updatedDate,
        validity = validity,
        vip_discount = 0, // Not stored in entity
        billing_price = billingPrice
    )
    
    return Plan(
        detail = planDetail,
        equipments = equipment
    )
}

fun Plan.toEntity(): PlanEntity {
    return PlanEntity(
        id = detail?.id?:0,
        planName = detail?.plan_name?:"",
        planPrice = detail?.plan_price?:"",
        validity = detail?.validity?:"",
        bulletPoints = detail?.bullet_points?:"",
        planDesc = detail?.plan_desc?:"",
        image = detail?.image?:"",
        customerId = detail?.customer_id?:"",
        planType = detail?.plan_type?:"",
        membershipType = detail?.membership_type?:"",
        currency = detail?.currency?:"",
        points = detail?.points?:0,
        status = detail?.status?:0,
        createdDate = detail?.created_date?:"",
        updatedDate = detail?.updated_date?:"",
        equipmentJson = gson.toJson(equipments),
        frequency = detail?.frequency?:"",
        frequencyLimit = detail?.frequency_limit?:"",
        discount = detail?.discount?:"",
        discountType = detail?.discount_type?:"",
        discountValidity = detail?.discount_validity?:"",
        employeeDiscount = detail?.employee_discount?:"",
        isForEmployee = detail?.is_for_employee?:0,
        isVipPlan = detail?.is_vip_plan?:0,
        billingPrice = detail?.billing_price
    )
}
