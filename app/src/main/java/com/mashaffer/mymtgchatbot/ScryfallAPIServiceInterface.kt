package com.mashaffer.mymtgchatbot

import Card
import Rulings
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ScryfallAPIServiceInterface {
    // Route for getting card data
    @GET("/cards/named?")
    suspend fun getCardGenData(@Query("cardname") cardname: String): Response<Card>

    // Route to get card ruling
    @GET("/cards/")
    suspend fun getCardRuleData(@Query("cardID") cardID: String): Response<Rulings>

}