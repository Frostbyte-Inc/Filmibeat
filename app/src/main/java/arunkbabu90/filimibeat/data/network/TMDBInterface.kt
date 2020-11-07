package arunkbabu90.filimibeat.data.network

import arunkbabu90.filimibeat.data.database.MovieDetails
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBInterface {
    @GET("movie/popular")
    fun getPopularMovies(@Query("page") page: Int): Single<PopularMovieResponse>

    @GET("movie/top_rated")
    fun getTopRatedMovies(@Query("page") page: Int): Single<RatedMovieResponse>

    @GET("movie/now_playing")
    fun getNowPlayingMovies(@Query("page") page: Int): Single<NowPlayingMovieResponse>

    @GET("movie/{movie_id}")
    fun getMovieDetails(@Path("movie_id") movieId: Int): Single<MovieDetails>
}