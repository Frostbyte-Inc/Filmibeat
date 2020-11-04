package arunkbabu90.filimibeat.data.database

import arunkbabu90.filimibeat.data.network.*
import com.google.gson.annotations.SerializedName

data class MovieDetails(val id: Int,
                        val overview: String,
                        val popularity: Double,
                        val budget: Long,
                        val revenue: Long,
                        val runtime: Int,
                        val status: String,
                        val video: Boolean, val genres: List<Genre>,
                        @SerializedName("poster_path") private val posterPath: String,
                        @SerializedName("backdrop_path") private val backdropPath: String,
                        @SerializedName("production_companies") val productionCompanies: List<Company>,
                        @SerializedName("release_date") val releaseDate: String,
                        @SerializedName("vote_average") val rating: String,
                        @SerializedName("original_language") val language: String,
) {
    val posterUrl: String
        get() = POSTER_BASE_URL + IMG_SIZE_MID + posterPath

    val backDropUrl: String
        get() = POSTER_BASE_URL + IMG_SIZE_LARGE + backdropPath

    val releaseYear: String
        get() {
            // Extract only the release Year from the Release Date. Like: 2018-02-01 To 2018
            val yrIndex = releaseDate.indexOf("-")
            return if (yrIndex == -1) releaseDate else releaseDate.substring(0, yrIndex)
        }
}