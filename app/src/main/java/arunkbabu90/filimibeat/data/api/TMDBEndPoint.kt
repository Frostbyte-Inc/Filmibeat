package arunkbabu90.filimibeat.data.api

import arunkbabu90.filimibeat.data.model.CastCrewResponse
import arunkbabu90.filimibeat.data.model.MovieDetails
import arunkbabu90.filimibeat.data.model.MovieResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBEndPoint {
    @GET("movie/popular")
    fun getPopularMovies(@Query("page") page: Int): Single<MovieResponse>

    @GET("movie/top_rated")
    fun getTopRatedMovies(@Query("page") page: Int): Single<MovieResponse>

    @GET("movie/now_playing")
    fun getNowPlayingMovies(@Query("page") page: Int, @Query("region") regionCode: String): Single<MovieResponse>

    @GET("movie/{movie_id}")
    fun getMovieDetails(@Path("movie_id") movieId: Int): Single<MovieDetails>

    @GET("search/movie")
    fun searchForMovie(@Query("query") searchTerm: String,
                       @Query("page") page: Int,
                       @Query("include_adult") adult: Boolean) : Single<MovieResponse>

    @GET("movie/{movie_id}/credits")
    fun getCastCrew(@Path("movie_id") movieId: Int): Single<CastCrewResponse>
}