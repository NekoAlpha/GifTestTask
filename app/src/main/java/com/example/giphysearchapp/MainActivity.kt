package com.example.giphysearchapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.giphysearchapp.ui.theme.GiphySearchAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*

class MainActivity : AppCompatActivity() {

    val apiService: GiphyApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.giphy.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(GiphyApiService::class.java)
    }

    private val gifList: MutableList<String> = mutableListOf()
    private lateinit var adapter: GifAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val editTextSearch: EditText = findViewById(R.id.editTextSearch)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = GifAdapter(gifList)
        recyclerView.adapter = adapter

        editTextSearch.addTextChangedListener(object : TextWatcher {
            private var timer: Timer? = null

            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("TextWatcher", "Text changed: $s")//for debugging, to see if it works
                timer?.cancel()
                timer = Timer()
                timer?.schedule(object : TimerTask() {
                    override fun run() {
                        fetchGifs(s.toString())
                    }
                }, 300)
            }
        })
    }

    private fun fetchGifs(query: String) {
        Log.d("GiphySearch", "Fetching gifs for query: $query")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.searchGifs("oqtc1WmU8dPnjobZQhoToZg19HM51UBT", query, 35, 0)

                val gifItems = response.data.map { it.images.fixed_height.url }

                withContext(Dispatchers.Main) {
                    gifList.clear()
                    gifList.addAll(gifItems)
                    adapter.notifyDataSetChanged()
                }
            }catch (e: Exception) {
                Log.e("GiphySearch", "Error fetching GIFs", e)
                e.printStackTrace()
            }
        }
    }
}

interface GiphyApiService {

    @GET("gifs/search")
    suspend fun searchGifs(
        @Query("api_key") apiKey: String,
        @Query("q") query: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): GiphyRessponse
}

//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(text = "Hello $name!", modifier = modifier)
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    GiphySearchAppTheme {
//        Greeting("Android")
//    }
//}
