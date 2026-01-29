package com.codechaps.therajet.data.di

import android.content.Context
import androidx.room.Room
import com.codechaps.therajet.data.api.ApiClient
import com.codechaps.therajet.data.api.ApiService
import com.codechaps.therajet.data.local.TheraJetDatabase
import com.codechaps.therajet.data.repository.AuthRepositoryImpl
import com.codechaps.therajet.data.repository.EquipmentRepositoryImpl
import com.codechaps.therajet.data.repository.PaymentRepositoryImpl
import com.codechaps.therajet.domain.repository.AuthRepository
import com.codechaps.therajet.domain.repository.EquipmentRepository
import com.codechaps.therajet.domain.repository.PaymentRepository
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
        )
        .addMigrations(
            TheraJetDatabase.MIGRATION_1_2,
            TheraJetDatabase.MIGRATION_2_3
        )
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

