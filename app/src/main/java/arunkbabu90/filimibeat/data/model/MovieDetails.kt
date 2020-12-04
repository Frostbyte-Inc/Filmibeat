package arunkbabu90.filimibeat.data.model

import com.google.gson.annotations.SerializedName

data class MovieDetails(var movieId: Long,
                        var title: String,
                        var overview: String,
                        var popularity: Double,
                        var budget: Long,
                        var revenue: Long,
                        var runtime: Int,
                        var status: String,
                        var video: Boolean,
                        var genres: List<Genre> = listOf(),
                        @SerializedName("production_companies") var companies: List<Company> = listOf(),
                        @SerializedName("poster_path") var posterPath: String,
                        @SerializedName("backdrop_path") var backdropPath: String,
                        @SerializedName("release_date") var releaseDate: String,
                        @SerializedName("vote_average") var rating: String,
                        @SerializedName("original_language")var language: String,
) {
    val releaseYear: String
        get() {
            // Extract only the release Year from the Release Date. Like: 2018-02-01 To 2018
            val yrIndex = releaseDate.indexOf("-")
            return if (yrIndex == -1) releaseDate else releaseDate.substring(0, yrIndex)
        }
}