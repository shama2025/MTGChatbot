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
        val regex = """(Card name (?<cardName>[\w\s]+))""".toRegex()
        val result = regex.find("Card name elsha of the infinite")
        val cardName = result!!.groups["cardName"]?.value

        util.getCardData(MainActivity(),cardName)
        util.getCardRuleData(MainActivity(),"c0728027-a1ec-4814-87c4-10c3baced0e0")
        util.getCardSetData(MainActivity(), "5a645837-b050-449f-ac90-1e7ccbf45031")
    }

    override fun onGetCardData(data: Card?) {

        /**
         * data?.ruling_uri => If there isn't one save null
         *      => https://api.scryfall.com/cards/{id = c0728027-a1ec-4814-87c4-10c3baced0e0}/rulings
         * data?.set_uri => Check if null and if is save null
         */

        // Will need to save the ruling uri for later.
        // Will need to save the set uri for later.


        val manaCost = formatManaCost(data?.manaCost)
        val manaColor = formatManaColor(data?.colors)
        val output = "The card ${data?.name} has a base power of ${data?.power} and a base toughness of ${data?.toughness}. " +
                "It has a mana cost of ${manaCost} and is in the color identity of ${manaColor}." + " ${data?.name} has the ability ${data?.oracleText}."
        Log.i(TAG, output)
    }

    /**
     * This function will format the mana cost to make it readable
     */
    private fun formatManaCost(manaCost: String?): String {
        val manaCostFormat = mutableListOf<String>()
        val splitCost = manaCost.toString().split('{', '}')
        var output = ""

        splitCost.forEach { cost ->
            if (cost.isNotBlank()) {
                manaCostFormat.add(cost)
            }
        }

        manaCostFormat.forEachIndexed { index, cost ->
            val isLast = index == manaCostFormat.lastIndex
            if (isLast) {
                when (cost.uppercase()) {
                    "R" -> output += " and Red"
                    "B" -> output += " and Black"
                    "U" -> output += " and Blue"
                    "W" -> output += " and White"
                    "G" -> output += " and Green"
                    else -> {
                        // If it's a number (colorless mana)
                        output += cost.toIntOrNull()?.let { " and $it colorless" }
                            ?: "Colorless/Generic/Any Colors"
                    }
                }
            } else {
                when (cost.uppercase()) {
                    "R" -> output += " Red,"
                    "B" -> output += " Black,"
                    "U" -> output += " Blue,"
                    "W" -> output += " White,"
                    "G" -> output += " Green,"
                    else -> {
                        // If it's a number (colorless mana)
                        output += cost.toIntOrNull()?.let { "$it Colorless/Generic/Any Colors," }
                    }
                }
            }
        }
        return output
    }

    /**
     * This function will format the mana color to make it more readable
     */
    private fun formatManaColor(manaColor: List<String>?): String {
        /**
         * colors":["R","U","W"]) Will need to setup condition where R = Red, B = Black, U = Blue, W = White, G = Green, and empty [] = Colorless
         */

        if (manaColor == null || manaColor.isEmpty()) {
            return "Colorless"
        }

        var output = ""

        manaColor.forEachIndexed { index, color ->
            val isLast = index == manaColor.lastIndex
            if (isLast) {
                when (color.uppercase()) {
                    "R" -> output += "and Red"
                    "B" -> output += "and Black"
                    "U" -> output += "and Blue"
                    "W" -> output += "and White"
                    "G" -> output += "and Green"
                }
            } else {
                when (color.uppercase()) {
                    "R" -> output += "Red, "
                    "B" -> output += "Black, "
                    "U" -> output += "Blue, "
                    "G" -> output += "Green, "
                    "W" -> output += "White, "
                }
            }
        }
        return output
    }


    override fun onGetCardRuleData(data: Rulings?) {

        Log.i(TAG, "Data from API for card rules data: $data")

        if(data == null){
            // Will need to save this
           // "There are no available rules"
        }

        var output = "The rulings are: "

        data?.moreData?.forEach{ruling ->
            output += ruling.comment
        }
        Log.i(TAG,output)
    }

    override fun onGetCardSetData(data: CardSet?) {
        Log.i(TAG, "Data from API for card set data: $data" )

        if(data == null){
            // "There are no listed sets
        }

        val output = "This card is from the set: ${data?.name}."
        Log.i(TAG,output)
    }
}
