package arunkbabu90.filimibeat.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import arunkbabu90.filimibeat.data.api.TMDBClient
import arunkbabu90.filimibeat.data.repository.ReviewRepository
import arunkbabu90.filimibeat.databinding.ActivityReviewsBinding

class ReviewsActivity : AppCompatActivity() {
    private lateinit var repository: ReviewRepository
    private lateinit var binding: ActivityReviewsBinding

    private var movieId = -1

    companion object {
        const val REVIEW_MOVIE_ID_EXTRA_KEY = "reviewMovieIdExtraKey"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        movieId = intent.getIntExtra(REVIEW_MOVIE_ID_EXTRA_KEY, -1)

        val apiService = TMDBClient.getClient()
        repository = ReviewRepository(apiService)

        val lm = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.rvReviews.layoutManager = lm
        binding.rvReviews.setHasFixedSize(true)
    }
}