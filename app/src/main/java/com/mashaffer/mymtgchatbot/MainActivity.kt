package com.mashaffer.mymtgchatbot

import Card
import Rulings
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity(),ScryfallCallback {

    companion object{
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.main_activity)

        val util: Util = Util()
        util.getCardData(MainActivity(),"elsha of the infinite")
        util.getCardRuleData(MainActivity(),"c0728027-a1ec-4814-87c4-10c3baced0e0")
        util.getCardSetData(MainActivity(), "5a645837-b050-449f-ac90-1e7ccbf45031")
    }

    override fun onGetCardData(data: Card?) {
        Log.i(TAG, "Data from API for gen card data: $data")
    }

    override fun onGetCardRuleData(data: Rulings?) {
        Log.i(TAG, "Data from API for card rules data: $data")
    }

    override fun onGetCardSetData(data: CardSet?) {
        Log.i(TAG, "Data from API for card set data: $data" )
    }
}
