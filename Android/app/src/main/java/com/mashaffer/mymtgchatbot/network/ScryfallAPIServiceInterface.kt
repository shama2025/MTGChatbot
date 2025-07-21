package com.mashaffer.mymtgchatbot.network

import com.mashaffer.mymtgchatbot.model.Card
import Rulings
import com.mashaffer.mymtgchatbot.model.CardSet
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ScryfallAPIServiceInterface {
    // Route for getting card data
    @GET("/cards/named")
    suspend fun getCardGenData(@Query("fuzzy") cardname: String?): Response<Card>

    // Route to get card ruling
    @GET("/cards/{id}/rulings")
    suspend fun getCardRuleData(@Path ("id") id: String): Response<Rulings>

    // Route to get com.mashaffer.mymtgchatbot.model.Card Set information
    @GET("/sets/{id}")
    suspend fun getCardSetData(@Path ("id") id:String): Response<CardSet>
}