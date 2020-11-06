package arunkbabu90.filimibeat.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface MovieDao {
    @Query("SELECT * FROM PopularMovies")
    fun getPopularMovies(): Flowable<MoviePopular>

    @Query("SELECT * FROM TopRatedMovies")
    fun getRatedMovies(): Flowable<MovieTopRated>

    @Query("SELECT * FROM NowPlayingMovies")
    fun getNowPlayingMovies(): Flowable<MovieNowPlaying>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPopularMovies(vararg movies: MoviePopular): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRatedMovies(vararg movies: MovieTopRated): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNowPlayingMovies(vararg movies: MovieNowPlaying): Completable

    @Query("DELETE FROM PopularMovies")
    fun clearPopularMovies()

    @Query("DELETE FROM TopRatedMovies")
    fun clearRatedMovies()

    @Query("DELETE FROM NowPlayingMovies")
    fun clearNowPlayingMovies()
}