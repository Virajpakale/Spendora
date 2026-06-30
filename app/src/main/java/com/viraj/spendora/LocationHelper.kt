package com.viraj.spendora

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LocationHelper(private val context: Context) {

    private val apiKey = "b7df8b3aa7794677a1f4b3b727d35959"

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(callback: (String) -> Unit) {
        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {

                val retrofit = Retrofit.Builder()
                    .baseUrl("https://api.geoapify.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val service = retrofit.create(GeoapifyService::class.java)

                service.getPlaceDetails(
                    location.latitude,
                    location.longitude,
                    apiKey
                ).enqueue(object : retrofit2.Callback<GeoapifyResponse> {

                    override fun onResponse(
                        call: retrofit2.Call<GeoapifyResponse>,
                        response: retrofit2.Response<GeoapifyResponse>
                    ) {
                        if (response.isSuccessful) {
                            val features = response.body()?.features

                            if (!features.isNullOrEmpty()) {
                                val place = features[0].properties

                                val placeName = place.name ?: ""
                                val address = place.formatted ?: ""

                                // Clean unwanted parts
                                val cleanAddress = address
                                    .replace(Regex("\\b\\d{6}\\b"), "") // remove pincode
                                    .replace("Maharashtra", "")
                                    .replace("MH", "")
                                    .replace("India", "")
                                    .replace(", ,", ",")
                                    .replace(",,", ",")
                                    .trim()
                                    .trim(',')

                                val finalLocation =
                                    if (placeName.isNotEmpty())
                                        "$placeName, $cleanAddress"
                                    else cleanAddress

                                callback(finalLocation)
                            } else {
                                callback("Unknown Location")
                            }
                        } else {
                            callback("Unknown Location")
                        }
                    }

                    override fun onFailure(
                        call: retrofit2.Call<GeoapifyResponse>,
                        t: Throwable
                    ) {
                        callback("Unknown Location")
                    }
                })

            } else {
                callback("Unknown Location")
            }
        }
    }
}