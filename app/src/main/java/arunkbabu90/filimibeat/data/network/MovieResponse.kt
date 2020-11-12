package arunkbabu90.filimibeat.data.network

import arunkbabu90.filimibeat.data.database.Movie
import com.google.gson.annotations.SerializedName

data class MovieResponse(val page: Int,
                         @SerializedName("results") val movies: List<Movie>,
                         @SerializedName("total_pages") val totalPages: Int,
                         @SerializedName("total_results") val totalMovies: Int)