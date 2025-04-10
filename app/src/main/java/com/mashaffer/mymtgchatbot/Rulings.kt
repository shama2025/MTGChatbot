import com.google.gson.annotations.SerializedName

data class Rulings(
    @SerializedName("object") val `object`: String,
    @SerializedName("has_more") val hasMore: Boolean,
    @SerializedName("data") val moreData: List<Ruling>
) {
    data class Ruling(
        @SerializedName("object") val `object`: String,
        @SerializedName("oracle_id") val oracleId: String,
        @SerializedName("source") val source: String,
        @SerializedName("published_at") val publishedAt: String,
        @SerializedName("comment") val comment: String
    )
}
