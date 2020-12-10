package arunkbabu90.filimibeat.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import arunkbabu90.filimibeat.R
import arunkbabu90.filimibeat.data.api.TMDBClient
import arunkbabu90.filimibeat.data.repository.NetworkState
import arunkbabu90.filimibeat.data.repository.ReviewRepository
import arunkbabu90.filimibeat.databinding.ActivityReviewsBinding
import arunkbabu90.filimibeat.ui.adapter.ReviewAdapter
import arunkbabu90.filimibeat.ui.viewmodel.ReviewViewModel

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

        window.navigationBarColor = ContextCompat.getColor(this, R.color.colorDarkBackgroundGrey1)

        val apiService = TMDBClient.getClient()
        repository = ReviewRepository(apiService)

        val lm = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val adapter = ReviewAdapter()
        binding.rvReviews.layoutManager = lm
        binding.rvReviews.setHasFixedSize(true)
        binding.rvReviews.adapter = adapter

        val viewModel = getViewModel()
        viewModel.fetchReviews(movieId).observe(this, { reviewPagedList ->
            if (reviewPagedList.size <= 0) {
                binding.tvReviewsErr.visibility = View.VISIBLE
                binding.tvReviewsErr.text = getString(R.string.no_reviews_found)
                adapter.submitList(reviewPagedList)
            } else {
                adapter.submitList(reviewPagedList)
                binding.tvReviewsErr.visibility = View.GONE
            }
        })

        viewModel.networkState.observe(this, { state ->
            binding.pbReviews.visibility =
                if (viewModel.isEmpty() && state == NetworkState.LOADING) View.VISIBLE else View.GONE

            if (state == NetworkState.ERROR)
                binding.tvReviewsErr.visibility = View.VISIBLE
            else
                binding.tvReviewsErr.visibility = View.GONE

            if (viewModel.isEmpty())
                binding.tvReviewsErr.visibility = View.VISIBLE
            else
                binding.tvReviewsErr.visibility = View.GONE

            if (!viewModel.isEmpty())
                adapter.setNetworkState(state)
        })
    }

    private fun getViewModel(): ReviewViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return ReviewViewModel(repository) as T
            }
        })[ReviewViewModel::class.java]
    }
}