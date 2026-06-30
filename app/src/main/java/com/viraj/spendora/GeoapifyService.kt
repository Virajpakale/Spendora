package com.viraj.spendora

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoapifyService {

    @GET("v1/geocode/reverse")
    fun getPlaceDetails(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("apiKey") apiKey: String
    ): Call<GeoapifyResponse>
}