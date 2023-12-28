package com.example.giphysearchapp

data class GiphyRessponse(
    val data: List<GifItem>
)



data class GifItem(
    val id: String,
    val title: String,
    val images: GifImages
)

data class GifImages(
    val fixed_height: GifImage
)

data class GifImage(
    val url: String
)
