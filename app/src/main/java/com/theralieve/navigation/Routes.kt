package com.theralieve.navigation

object Routes {

    var forCreditPurchase = false
    const val WELCOME = "welcome"
    const val EQUIPMENT_LIST = "equipment_list"

    const val EQUIPMENT_LIST_CREDIT = "equipment_list_credit"
    const val EQUIPMENT_DETAIL = "equipment_detail"
    const val PLAN_DATA = "plan_data"
    const val CHECKOUT = "checkout"
    const val INFO_SINGLE_SESSION_BEFORE_CHECKOUT = "info_before_single_session_checkout"
    const val CHECKOUT_SINGLE_SESSION = "checkout_single_session"
    const val MEMBERSHIP_LIST = "membership_list"

    const val SELECTED_MEMBERSHIP = "selected_membership"
    const val MEMBERSHIP_DETAIL = "membership_detail"
    const val REGISTRATION = "registration"
    const val MEMBERSHIP_CHECKOUT = "membership_checkout"
    const val MEMBER_LOGIN = "member_login"

    const val CUSTOMER_LOGIN = "customer_login"

    const val PROFILE = "profile"

    // Add-on (Session/Credit) purchase flows (NEW)
    const val ADDON_PLAN_LIST_SESSION_PACK = "addon_plan_list_session_pack"
    const val ADDON_PLAN_LIST_CREDIT_PACK = "addon_plan_list_credit_pack"
    const val ADDON_PLAN_LIST_CREDIT_PLAN = "addon_plan_list_credit_plan"

    const val ADDON_PLAN_PURCHASE_PREVIEW = "addon_plan_purchase"
    const val ADDON_PLAN_DETAIL = "addon_plan_detail" // {type}
    const val ADDON_PLAN_CHECKOUT = "addon_plan_checkout" // {planId}

    const val ADDON_TYPE_SESSION = "session"
    const val ADDON_TYPE_CREDIT = "credit"

    const val SINGLE_SESSION_SCREEN = "single_session_screen"

    const val NEW_SEE_PLAN = "new_see_plans"

    // new screens
    // Credit Packs & Credit Plans

    const val CREDIT_PACK_LIST = "credit_pack_list"

    const val CREDIT_PLAN_LIST = "credit_plan_list"

    const val SESSION_PACK_LIST = "session_plan_list"


    fun registrationRoute(planId: String, isForEmployee: Boolean,memberNo:String?, employeeNo:String?,membershipType:String?,isRenew: Boolean): String {
        return "${REGISTRATION}/${planId}?isForEmployee=${isForEmployee}?memberNo=${memberNo}?employeeNo=${employeeNo}?membershipType=${membershipType}?isRenew=${isRenew}"
    }
    fun selectedMembershipRoute(planId: String, isForEmployee: Boolean,memberNo:String?, employeeNo:String?,membershipType:String?,isRenew: Boolean): String {
        return "${SELECTED_MEMBERSHIP}/${planId}?isForEmployee=${isForEmployee}?memberNo=${memberNo}?employeeNo=${employeeNo}?membershipType=${membershipType}?isRenew=${isRenew}"
    }

    fun membershipDetailRoute(planId: String, isForEmployee: Boolean,memberNo:String?, employeeNo:String?,membershipType:String?): String {
        return "${MEMBERSHIP_DETAIL}/${planId}?isForEmployee=${isForEmployee}?memberNo=${memberNo}?employeeNo=${employeeNo}?membershipType=${membershipType}"
    }

    fun addonPlanCheckoutRoute(planId: String, isForEmployee: Boolean,isRenew:Boolean): String {
        return "${ADDON_PLAN_CHECKOUT}/${planId}?isForEmployee=${isForEmployee}?isRenew=${isRenew}"
    }

    fun addonPlanCheckoutPreviewRoute(planId: String, isForEmployee: Boolean,isRenew:Boolean): String {
        return "${ADDON_PLAN_PURCHASE_PREVIEW}/${planId}?isForEmployee=${isForEmployee}?isRenew=${isRenew}"
    }
}
















