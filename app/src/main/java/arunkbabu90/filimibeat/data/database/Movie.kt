package arunkbabu90.filimibeat.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import arunkbabu90.filimibeat.data.api.IMG_SIZE_LARGE
import arunkbabu90.filimibeat.data.api.IMG_SIZE_MID
import arunkbabu90.filimibeat.data.api.POSTER_BASE_URL
import com.google.gson.annotations.SerializedName

@Entity
data class Movie(@PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var Id: Int,
                 @SerializedName("id") @ColumnInfo(name = "movieId") var movieId: Long,
                 @SerializedName("poster_path") @ColumnInfo(name = "posterPath") var posterPath: String,
                 @SerializedName("backdrop_path") @ColumnInfo(name = "backdropPath") var backdropPath: String,
                 @SerializedName("title") @ColumnInfo(name = "title") var title: String,
                 @SerializedName("vote_average") @ColumnInfo(name = "rating") var rating: String,
                 @SerializedName("overview") @ColumnInfo(name = "overview") var overview: String,
                 @SerializedName("release_date") @ColumnInfo(name = "releaseDate") var releaseDate: String
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