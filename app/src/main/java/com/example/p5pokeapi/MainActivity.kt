package com.example.p5pokeapi

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

data class Pokemon(
    val name: String,
    val imageUrl: String,
    val ability: String
)

class MainActivity() : AppCompatActivity() {
    private lateinit var pokeList: MutableList<Pokemon> // Change this line
    private lateinit var rvPokes: RecyclerView
    private lateinit var adapter: PetAdapter // Declare adapter variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvPokes = findViewById(R.id.poke_list)
        pokeList = mutableListOf()
        adapter = PetAdapter(pokeList) // Initialize adapter

        rvPokes.adapter = adapter // Set adapter
        rvPokes.layoutManager = LinearLayoutManager(this@MainActivity) // Set layout manager

        getNextPokemon()
    }

    private fun getNextPokemon() {
        val client = AsyncHttpClient()
        for (i in 1..30) { // Fetch 30 Pokemon
            val url = "https://pokeapi.co/api/v2/pokemon/$i" // Get each Pokemon by ID
            client.get(url, object : JsonHttpResponseHandler() {
                override fun onSuccess(
                    statusCode: Int,
                    headers: Array<Header>?,
                    response: JSONObject?
                ) {
                    val name = response?.getString("name")
                    val abilities = response?.getJSONArray("abilities")
                    val ability = abilities?.getJSONObject(0)?.getJSONObject("ability")?.getString("name")
                    val sprites = response?.getJSONObject("sprites")
                    val imageUrl = sprites?.getString("front_default")
                    if (imageUrl != null && name != null && ability != null) {
                        val pokemon = Pokemon(name, imageUrl, ability) // Create a Pokemon object
                        pokeList.add(pokemon) // Add the Pokemon object to the list
                        runOnUiThread { // Make sure to update UI on the main thread
                            adapter.notifyDataSetChanged() // Notify adapter of data change
                        }
                    }
                }

                override fun onFailure(
                    statusCode: Int,
                    headers: Array<Header>?,
                    throwable: Throwable,
                    errorResponse: JSONObject?
                ) {
                    Log.d("Pokemon Error", throwable?.message ?: "Unknown error")
                }
            })
        }
    }
}

