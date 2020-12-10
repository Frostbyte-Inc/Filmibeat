package arunkbabu90.filimibeat.data.model

import com.google.gson.annotations.SerializedName

class Review(val author: String = "",
             val content: String = "",
             @SerializedName("updated_at") val updatedAt: String = "")