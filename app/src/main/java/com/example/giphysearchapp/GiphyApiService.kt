package com.example.giphysearchapp

import retrofit2.http.GET
import retrofit2.http.Query

class GiphyResponse {
    interface GiphyApiService {

        @GET("gifs/search")
        suspend fun searchGifs(
            @Query("api_key") apiKey: String,
            @Query("q") query: String,
            @Query("limit") limit: Int,
            @Query("offset") offset: Int
        ): GiphyRessponse
    }

}
