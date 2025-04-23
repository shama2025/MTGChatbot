package com.mashaffer.mymtgchatbot

// Data class for card set data
data class CardSet(
    val `object`: String,
    val id: String,
    val code: String,
    val tcgplayer_id: Int,
    val name: String,
    val uri: String,
    val scryfall_uri: String,
    val search_uri: String,
    val released_at: String,
    val set_type: String,
    val card_count: Int,
    val printed_size: Int,
    val digital: Boolean,
    val nonfoil_only: Boolean,
    val foil_only: Boolean,
    val icon_svg_uri: String
)
