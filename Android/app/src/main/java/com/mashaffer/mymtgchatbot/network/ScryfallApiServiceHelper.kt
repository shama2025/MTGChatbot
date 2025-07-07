package com.mashaffer.mymtgchatbot.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ScryfallApiServiceHelper {

    private const val BASE_URL = "https://api.scryfall.com/"

    /**
     * Default timeout values for network operations (in seconds)
     */
    private const val CONNECT_TIMEOUT = 30L
    private const val READ_TIMEOUT = 30L
    private const val WRITE_TIMEOUT = 30L

    /**
     * Lazily initialized Retrofit instance to avoid creating it until needed
     * and ensure it's only created once
     */
    private val retrofit: Retrofit by lazy {
        createRetrofitInstance()
    }

    /**
     * Creates and configures a new OkHttpClient instance with appropriate timeouts
     * and common headers for API requests
     *
     * @return Configured OkHttpClient instance
     */
    private fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Creates and configures a new Retrofit instance with the base URL,
     * GSON converter factory, and OkHttpClient
     *
     * @return Configured Retrofit instance
     */
    private fun createRetrofitInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Returns the singleton Retrofit instance for making API calls
     *
     * @return Configured Retrofit instance
     */
    fun getInstance(): Retrofit {
        return retrofit
    }

}