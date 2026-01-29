package com.codechaps.therajet.navigation

object Routes {
    const val WELCOME = "welcome"
    const val EQUIPMENT_LIST = "equipment_list"
    const val EQUIPMENT_DETAIL = "equipment_detail"
    const val CHECKOUT = "checkout"
    const val MEMBERSHIP_LIST = "membership_list"
    const val MEMBERSHIP_DETAIL = "membership_detail"
    const val REGISTRATION = "registration"
    const val MEMBERSHIP_CHECKOUT = "membership_checkout"
    const val MEMBER_LOGIN = "member_login"

    const val CUSTOMER_LOGIN = "customer_login"

    const val PROFILE = "profile"

    // Add-on (Session/Credit) purchase flows (NEW)
    const val ADDON_PLAN_LIST = "addon_plan_list" // {type}
    const val ADDON_PLAN_CHECKOUT = "addon_plan_checkout" // {planId}

    const val ADDON_TYPE_SESSION = "session"
    const val ADDON_TYPE_CREDIT = "credit"

    fun registrationRoute(planId: String, isForEmployee: Boolean,memberNo:String?, employeeNo:String?,membershipType:String?): String {
        return "${REGISTRATION}/${planId}?isForEmployee=${isForEmployee}?memberNo=${memberNo}?employeeNo=${employeeNo}?membershipType=${membershipType}"
    }

    fun membershipDetailRoute(planId: String, isForEmployee: Boolean,memberNo:String?, employeeNo:String?,membershipType:String?): String {
        return "${MEMBERSHIP_DETAIL}/${planId}?isForEmployee=${isForEmployee}?memberNo=${memberNo}?employeeNo=${employeeNo}?membershipType=${membershipType}"
    }

    fun addonPlanListRoute(type: String): String {
        // type must be one of ADDON_TYPE_SESSION / ADDON_TYPE_CREDIT
        return "${ADDON_PLAN_LIST}/${type}"
    }

    fun addonPlanCheckoutRoute(planId: String, isForEmployee: Boolean): String {
        return "${ADDON_PLAN_CHECKOUT}/${planId}?isForEmployee=${isForEmployee}"
    }
}
















