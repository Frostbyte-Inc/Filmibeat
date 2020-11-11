package arunkbabu90.filimibeat.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import arunkbabu90.filimibeat.data.api.IMG_SIZE_LARGE
import arunkbabu90.filimibeat.data.api.IMG_SIZE_MID
import arunkbabu90.filimibeat.data.api.POSTER_BASE_URL
import arunkbabu90.filimibeat.data.network.Company
import arunkbabu90.filimibeat.data.network.Genre
import com.google.gson.annotations.SerializedName

@Entity
data class MovieDetails(@PrimaryKey @ColumnInfo(name = "movieId") var id: Int?,
                        @ColumnInfo(name = "title") var title: String?,
                        @ColumnInfo(name = "overview") var overview: String?,
                        @ColumnInfo(name = "popularity") var popularity: Double?,
                        @ColumnInfo(name = "budget") var budget: Long?,
                        @ColumnInfo(name = "revenue") var revenue: Long?,
                        @ColumnInfo(name = "runtime") var runtime: Int?,
                        @ColumnInfo(name = "status") var status: String?,
                        @ColumnInfo(name = "video") var video: Boolean?,
                        @SerializedName("poster_path") @ColumnInfo(name = "posterPath") var posterPath: String?,
                        @SerializedName("backdrop_path") @ColumnInfo(name = "backdropPath") var backdropPath: String?,
                        @SerializedName("release_date") @ColumnInfo(name = "releaseDate") var releaseDate: String?,
                        @SerializedName("vote_average") @ColumnInfo(name = "rating") var rating: String?,
                        @SerializedName("original_language") @ColumnInfo(name = "language") var language: String?,
) {
    @Ignore
    var genres: List<Genre> = listOf()

    @SerializedName("production_companies")
    @Ignore
    var productionCompanies: List<Company> = listOf()

    val posterUrl: String
        get() = POSTER_BASE_URL + IMG_SIZE_MID + posterPath

    val backDropUrl: String
        get() = POSTER_BASE_URL + IMG_SIZE_LARGE + backdropPath

    val releaseYear: String
        get() {
            // Extract only the release Year from the Release Date. Like: 2018-02-01 To 2018
            val yrIndex = releaseDate?.indexOf("-")
            return if (yrIndex == -1) releaseDate ?: "" else releaseDate?.substring(0, yrIndex ?: -1) ?: ""
        }
}