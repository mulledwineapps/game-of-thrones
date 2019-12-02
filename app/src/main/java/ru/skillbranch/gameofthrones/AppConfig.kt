package ru.skillbranch.gameofthrones

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes

object AppConfig {
    val NEED_HOUSES = arrayOf(
        "House Stark of Winterfell",
        "House Lannister of Casterly Rock",
        "House Targaryen of King's Landing",
        "House Greyjoy of Pyke",
        "House Tyrell of Highgarden",
        "House Baratheon of Dragonstone",
        "House Nymeros Martell of Sunspear"
    )
    const val BASE_URL = "https://www.anapioficeandfire.com/api/"

    private val retrofit: Retrofit

    init {
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    interface JSONPlaceHolderApi {
        @GET("houses")
        fun getHouseWithName(@Query("name") name: String): Call<List<HouseRes>>
    }

    fun getJSONApi(): JSONPlaceHolderApi {
        return retrofit.create(JSONPlaceHolderApi::class.java)
    }
}