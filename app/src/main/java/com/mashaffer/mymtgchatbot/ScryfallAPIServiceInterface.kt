package com.mashaffer.mymtgchatbot

import Card
import Rulings
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface ScryfallAPIServiceInterface {
    // Route for getting card data
    @GET("/cards/named")
    suspend fun getCardGenData(@Query("exact") cardname: String): Response<Card>

    // Route to get card ruling
    @GET("/cards/{id}/rulings")
    suspend fun getCardRuleData(@Path ("id") id: String): Response<Rulings>

    // Route to get Card Set information
    @GET("/sets/{id}")
    suspend fun getCardSetData(@Path ("id") id:String): Response<CardSet>
}