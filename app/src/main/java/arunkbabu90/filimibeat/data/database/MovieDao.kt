package arunkbabu90.filimibeat.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface MovieDao {
    @Query("SELECT * FROM Movie")
    fun getAllMovies(): Flowable<List<Movie>>

    @Query("SELECT movieId FROM MovieDetails WHERE movieId = :id")
    fun getMovieDetails(id: Int): Flowable<MovieDetails>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovies(vararg movies: Movie): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovieDetails(movieDetails: MovieDetails): Completable

    @Query("DELETE FROM Movie")
    fun clearAllMovies()
}