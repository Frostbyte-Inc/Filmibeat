package arunkbabu90.filimibeat.data.model

data class Favourite(var movieId: String = "",
                     val title: String = "",
                     val posterPath: String = "",
                     val backdropPath: String = "",
                     val overview: String = "",
                     val year: String = "",
                     val rating: String = "",
                     val timestamp: Long = -1)