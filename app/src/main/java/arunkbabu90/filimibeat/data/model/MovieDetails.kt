package arunkbabu90.filimibeat.data.model

data class MovieDetails(val budget: Int,
                        val id: Int,
                        val popularity: Double,
                        val video: Boolean,
                        val revenue: Long,
                        val runtime: Int,
                        val status: String,
                        val tagline: String,
                        val title: String
)