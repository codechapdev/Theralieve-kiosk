package com.theralieve.data.storage

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.theralieve.domain.model.Location
import com.theralieve.domain.model.UserProfile
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "therajet_preferences")

/**
 * Preference keys for persistent storage
 */
object PreferenceKeys {
    val CUSTOMER_ID = stringPreferencesKey("customer_id")
    val CUSTOMER_NAME = stringPreferencesKey("customer_name")
    val CUSTOMER_EMAIL = stringPreferencesKey("customer_email")
    val CUSTOMER_LOGGED_IN = booleanPreferencesKey("customer_logged_in")
    val CUSTOMER_TYPE = stringPreferencesKey("customer_type")
    val IS_FITNESS = booleanPreferencesKey("is_fitness")

    val MEMBER_ID = stringPreferencesKey("member_id")
    val MEMBER_NAME = stringPreferencesKey("member_name")
    val MEMBER_LAST_NAME = stringPreferencesKey("member_last_name")
    val MEMBER_USERNAME = stringPreferencesKey("member_username")
    val MEMBER_EMAIL = stringPreferencesKey("member_email")
    val MEMBER_MEMBERSHIP_TYPE = stringPreferencesKey("member_membership_type")
    val MEMBER_MEMBER_NUMBER = stringPreferencesKey("member_member_number")
    val MEMBER_EMPLOYEE_NUMBER = stringPreferencesKey("member_employee_number")
    val MEMBER_IMAGE = stringPreferencesKey("member_image")
    val MEMBER_CUSTOMER_ID = stringPreferencesKey("member_customer_id")
    val MEMBER_SQUARE_CUSTOMER_ID = stringPreferencesKey("member_square_customer_id")
    val MEMBER_LOGGED_IN = booleanPreferencesKey("member_logged_in")
    val MEMBER_VIP_DISCOUNT = stringPreferencesKey("applied_vip_discount")

    val LOCATION_DATA = stringPreferencesKey("location_data")
}

/**
 * Manages persistent storage for customer data
 */
class PreferenceManager(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            context.dataStore.data.first().asMap().forEach {
                Log.d("DATASTORE", "${it.key.name} = ${it.value}")
            }
        }
    }
    /**
     * Save customer login data
     */
    suspend fun saveCustomerData(
        customerId: String,
        name: String,
        email: String,
        customerType:String,
        isFitness:Boolean,
    ) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.CUSTOMER_ID] = customerId
            preferences[PreferenceKeys.CUSTOMER_NAME] = name
            preferences[PreferenceKeys.CUSTOMER_EMAIL] = email
            preferences[PreferenceKeys.CUSTOMER_LOGGED_IN] = true
            preferences[PreferenceKeys.CUSTOMER_TYPE] = customerType
            preferences[PreferenceKeys.IS_FITNESS] = isFitness
        }
    }

    suspend fun saveVipDiscount(vipDiscount:String){
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.MEMBER_VIP_DISCOUNT] = vipDiscount
        }
    }
    suspend fun getLoggedInUser(): UserProfile {
        val name = context.dataStore.data.first()[PreferenceKeys.MEMBER_NAME] ?: ""
        val email = context.dataStore.data.first()[PreferenceKeys.MEMBER_EMAIL] ?: ""
        val image = context.dataStore.data.first()[PreferenceKeys.MEMBER_IMAGE] ?: ""
        val username = context.dataStore.data.first()[PreferenceKeys.MEMBER_USERNAME] ?: ""
        val vipDiscount = context.dataStore.data.first()[PreferenceKeys.MEMBER_VIP_DISCOUNT] ?: "0"
        return UserProfile(name, email, username,image,vipDiscount)
    }
    /**
     * Get customer ID
     */
    suspend fun getCustomerId(): String? {
        return context.dataStore.data.first()[PreferenceKeys.CUSTOMER_ID]
    }

    /**
     * Get customer type
     */
    suspend fun getCustomerType(): String? {
        return context.dataStore.data.first()[PreferenceKeys.CUSTOMER_TYPE]
    }

    /**
     * Get isFitness flag
     */
    suspend fun getIsFitness(): Boolean {
        return context.dataStore.data.first()[PreferenceKeys.IS_FITNESS] ?: false
    }


    /**
     * Get customer name
     */
    suspend fun getCustomerName(): String? {
        return context.dataStore.data.first()[PreferenceKeys.CUSTOMER_NAME]
    }
    
    /**
     * Get customer email
     */
    suspend fun getCustomerEmail(): String? {
        return context.dataStore.data.first()[PreferenceKeys.CUSTOMER_EMAIL]
    }
    
    /**
     * Check if customer is logged in
     */
    suspend fun isCustomerLoggedIn(): Boolean {
        return context.dataStore.data.first()[PreferenceKeys.CUSTOMER_LOGGED_IN] ?: false
    }
    
    /**
     * Get customer logged in state as Flow
     */
    fun getCustomerLoggedInFlow(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferenceKeys.CUSTOMER_LOGGED_IN] ?: false
        }
    }
    
    /**
     * Clear customer data (logout)
     */
    suspend fun clearCustomerData() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferenceKeys.CUSTOMER_ID)
            preferences.remove(PreferenceKeys.CUSTOMER_NAME)
            preferences.remove(PreferenceKeys.CUSTOMER_EMAIL)
            preferences[PreferenceKeys.CUSTOMER_LOGGED_IN] = false
        }
    }
    
    /**
     * Save member data after registration or login
     */
    suspend fun saveMemberData(
        id: Int,
        name: String,
        lastName: String?,
        username: String,
        email: String,
        customerId: String,
        squareCustomerId: String,
        image: String,
        membershipType: String,
        memberNumber: String,
        employeeNumber: String,
        vipDiscount: String,
    ) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.MEMBER_ID] = id.toString()
            preferences[PreferenceKeys.MEMBER_NAME] = name
            preferences[PreferenceKeys.MEMBER_LAST_NAME] = lastName ?: ""
            preferences[PreferenceKeys.MEMBER_USERNAME] = username
            preferences[PreferenceKeys.MEMBER_EMAIL] = email
            preferences[PreferenceKeys.MEMBER_CUSTOMER_ID] = customerId
            preferences[PreferenceKeys.MEMBER_SQUARE_CUSTOMER_ID] = squareCustomerId
            preferences[PreferenceKeys.MEMBER_EMPLOYEE_NUMBER] = employeeNumber
            preferences[PreferenceKeys.MEMBER_MEMBER_NUMBER] = memberNumber
            preferences[PreferenceKeys.MEMBER_MEMBERSHIP_TYPE] = if(membershipType == "vip_member") "club_member" else membershipType
            preferences[PreferenceKeys.MEMBER_IMAGE] = image
            preferences[PreferenceKeys.MEMBER_LOGGED_IN] = true
            preferences[PreferenceKeys.MEMBER_VIP_DISCOUNT] = vipDiscount
        }
    }
    
    /**
     * Check if member is logged in
     */
    suspend fun isMemberLoggedIn(): Boolean {
        return context.dataStore.data.first()[PreferenceKeys.MEMBER_LOGGED_IN] ?: false
    }
    
    /**
     * Get member ID
     */
    suspend fun getMemberId(): String? {
        return context.dataStore.data.first()[PreferenceKeys.MEMBER_ID]
    }
    
    /**
     * Get member name
     */
    suspend fun getMemberName(): String? {
        val firstName = context.dataStore.data.first()[PreferenceKeys.MEMBER_NAME] ?: ""
        val lastName = context.dataStore.data.first()[PreferenceKeys.MEMBER_LAST_NAME] ?: ""
        return if (firstName.isNotEmpty() || lastName.isNotEmpty()) {
            "$firstName $lastName".trim()
        } else {
            null
        }
    }
    
    /**
     * Save or update member ID only
     * Useful when we get user_id from payment response
     */
    suspend fun saveMemberId(memberId: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.MEMBER_ID] = memberId
            // Ensure member is marked as logged in if ID is being saved
            preferences[PreferenceKeys.MEMBER_LOGGED_IN] = true
        }
    }

    /**
     * Get member square ID
     */
    suspend fun getMemberSquareId(): String? {
        return context.dataStore.data.first()[PreferenceKeys.MEMBER_SQUARE_CUSTOMER_ID]
    }

    suspend fun getMemberCustomerId(): String? {
        return context.dataStore.data.first()[PreferenceKeys.MEMBER_CUSTOMER_ID]
    }

    suspend fun getMemberMembershipType(): String? {
        return context.dataStore.data.first()[PreferenceKeys.MEMBER_MEMBERSHIP_TYPE]
    }

    suspend fun getMemberNumber(): String? {
        return context.dataStore.data.first()[PreferenceKeys.MEMBER_MEMBER_NUMBER]
    }

    suspend fun getEmployeeNumber(): String? {
        return context.dataStore.data.first()[PreferenceKeys.MEMBER_EMPLOYEE_NUMBER]
    }
    
    /**
     * Clear member data (logout)
     */
    suspend fun clearMemberData() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferenceKeys.MEMBER_ID)
            preferences.remove(PreferenceKeys.MEMBER_NAME)
            preferences.remove(PreferenceKeys.MEMBER_LAST_NAME)
            preferences.remove(PreferenceKeys.MEMBER_USERNAME)
            preferences.remove(PreferenceKeys.MEMBER_EMAIL)
            preferences.remove(PreferenceKeys.MEMBER_CUSTOMER_ID)
            preferences[PreferenceKeys.MEMBER_LOGGED_IN] = false
        }
    }
    
    /**
     * Save location data after customer login
     */
    suspend fun saveLocationData(locations: List<Location>) {
        val gson = Gson()
        val json = gson.toJson(locations)
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.LOCATION_DATA] = json
        }
    }
    
    /**
     * Get location data
     */
    suspend fun getLocationData(): List<Location>? {
        return try {
            val json = context.dataStore.data.first()[PreferenceKeys.LOCATION_DATA] ?: return null
            val gson = Gson()
            val type = object : TypeToken<List<Location>>() {}.type
            gson.fromJson<List<Location>>(json, type)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Clear location data
     */
    suspend fun clearLocationData() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferenceKeys.LOCATION_DATA)
        }
    }
}



