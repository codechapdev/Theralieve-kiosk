package com.theralievedi

import android.content.Context
import com.theralieve.utils.IoTManager
//import com.theralieve.utils.StripeConnectionTokenProvider
//import com.theralieve.utils.StripeTerminalPaymentManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
//    @Provides
//    @Singleton
//    fun provideStripePaymentManager(
//        @ApplicationContext context: Context,
//        stripeConnectionTokenProvider: StripeConnectionTokenProvider
//    ): StripeTerminalPaymentManager {
//        return StripeTerminalPaymentManager(context,stripeConnectionTokenProvider)
//    }


        @Provides
        @Singleton
        fun provideIoTManager(
            @ApplicationContext context: Context
        ): IoTManager {
            return IoTManager(context)
        }
}
