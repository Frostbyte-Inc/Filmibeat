package arunkbabu90.filimibeat.data.model

import com.google.gson.annotations.SerializedName

class Review(val id: String = "",
             val author: String = "",
             val content: String = "",
             @SerializedName("updated_at") val updatedAt: String = "") {

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + author.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + updatedAt.hashCode()
        return result
    }
}