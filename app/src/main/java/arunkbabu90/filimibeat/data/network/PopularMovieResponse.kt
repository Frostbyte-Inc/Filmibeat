package arunkbabu90.filimibeat.data.network

import arunkbabu90.filimibeat.data.database.MoviePopular
import com.google.gson.annotations.SerializedName

data class PopularMovieResponse(val page: Int,
                                @SerializedName("results") val movies: List<MoviePopular>,
                                @SerializedName("total_pages") val totalPages: Int,
                                @SerializedName("total_results") val totalMovies: Int)