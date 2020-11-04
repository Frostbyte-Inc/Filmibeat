package arunkbabu90.filimibeat.data.network

import arunkbabu90.filimibeat.data.database.MovieTopRated
import com.google.gson.annotations.SerializedName

data class RatedMovieResponse (val page: Int,
                               @SerializedName("results") val movies: List<MovieTopRated>,
                               @SerializedName("total_pages") val totalPages: Int,
                               @SerializedName("total_results") val totalMovies: Int)