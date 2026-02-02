package com.theralieve.data.di

import android.content.Context
import androidx.room.Room
import com.theralieve.data.api.ApiClient
import com.theralieve.data.api.ApiService
import com.theralieve.data.local.TheraJetDatabase
import com.theralieve.data.repository.AuthRepositoryImpl
import com.theralieve.data.repository.EquipmentRepositoryImpl
import com.theralieve.data.repository.PaymentRepositoryImpl
import com.theralieve.domain.repository.AuthRepository
import com.theralieve.domain.repository.EquipmentRepository
import com.theralieve.domain.repository.PaymentRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    
    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return ApiClient.apiService
    }
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TheraJetDatabase {
        return Room.databaseBuilder(
            context,
            TheraJetDatabase::class.java,
            TheraJetDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration(true)
        .build()
    }
    
    @Provides
    @Singleton
    fun provideAuthRepository(
        apiService: ApiService,
        database: TheraJetDatabase
    ): AuthRepository {
        return AuthRepositoryImpl(apiService, database)
    }

    @Provides
    @Singleton
    fun provideEquipmentRepository(
        apiService: ApiService,
        database: TheraJetDatabase
    ): EquipmentRepository {
        return EquipmentRepositoryImpl(apiService, database)
    }

    @Provides
    @Singleton
    fun providePaymentRepository(
        apiService: ApiService
    ): PaymentRepository {
        return PaymentRepositoryImpl(apiService)
    }

}

