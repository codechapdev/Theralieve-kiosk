package com.theralieve

import android.app.Application
//import com.theralieve.utils.StripeConnectionTokenProvider
//import com.theralieve.utils.StripeTerminalPaymentManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class TheraJetApplication : Application() {
    @Inject
//    lateinit var stripePaymentManager: StripeTerminalPaymentManager

    override fun onCreate() {
        super.onCreate()
//        stripePaymentManager.initialize(this)
//        MobilePaymentsSdk.initialize(Secrets.SQUARE_APPLICATION_ID, this)
    }

}
















