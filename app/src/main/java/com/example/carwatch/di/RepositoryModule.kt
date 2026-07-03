package com.example.carwatch.di

import com.example.carwatch.data.repository.CarWatchRepositoryImpl
import com.example.carwatch.data.repository.MockAuthRepository
import com.example.carwatch.data.repository.MockCarWatchRepository
import com.example.carwatch.domain.repository.AuthRepository
import com.example.carwatch.domain.repository.CarWatchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCarWatchRepository(
        repositoryImpl: CarWatchRepositoryImpl
    ): CarWatchRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        mockAuthRepository: MockAuthRepository
    ): AuthRepository
}
