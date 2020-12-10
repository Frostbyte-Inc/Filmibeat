package arunkbabu90.filimibeat.data.model

import com.google.gson.annotations.SerializedName

class ReviewResponse(@SerializedName("results") val reviews: List<Review>,
                     @SerializedName("total_pages") val totalPages: Int = -1,
                     val page: Int = -1)