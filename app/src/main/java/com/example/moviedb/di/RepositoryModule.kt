package com.example.moviedb.di

import com.example.moviedb.data.repository.MovieRepositoryImpl
import com.example.moviedb.domain.MovieRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindRepository(impl: MovieRepositoryImpl): MovieRepository
}