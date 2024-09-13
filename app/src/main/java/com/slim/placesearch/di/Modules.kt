package com.slim.placesearch.di

import android.content.Context
import com.slim.placesearch.MainViewModel
import com.slim.placesearch.SearchManager
import com.slim.placesearch.data.repository.Repository
import com.slim.placesearch.data.repository.RepositoryImpl
import com.slim.placesearch.data.repository.api.PlacesApi
import com.slim.placesearch.data.repository.api.PlacesApi.Companion.BASE_URL
import com.slim.placesearch.data.local.SharedPrefs
import com.slim.placesearch.data.local.SharedPrefsImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val placesModule = module {
    single{
        val client = OkHttpClient()
        val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val clientBuilder: OkHttpClient.Builder = client.newBuilder()
            .addInterceptor(interceptor)
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "{API_KEY}")
                    .build()
                chain.proceed(newRequest)
            }

        Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(clientBuilder.build())
            .build()
            .create(PlacesApi::class.java)
    }

    single<Repository> { RepositoryImpl(get()) }

    single { SearchManager(get()) }

    single { androidContext().getSharedPreferences("default", Context.MODE_PRIVATE) }

    single<SharedPrefs>{ SharedPrefsImpl(get()) }

    viewModel { MainViewModel(get(), get(), get()) }
}