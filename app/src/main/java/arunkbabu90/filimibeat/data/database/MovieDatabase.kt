package arunkbabu90.filimibeat.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import arunkbabu90.filimibeat.data.model.Movie
import arunkbabu90.filimibeat.data.model.MovieDetails

@Database(entities = [Movie::class, MovieDetails::class], version = 1, exportSchema = false)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao

    companion object {
        @Volatile private var INSTANCE: MovieDatabase? = null

        fun getInstance(context: Context): MovieDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { movieDb -> INSTANCE = movieDb }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                MovieDatabase::class.java, "Movies.db")
                .build()
    }
}