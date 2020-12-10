package arunkbabu90.filimibeat.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import arunkbabu90.filimibeat.data.api.TMDBClient
import arunkbabu90.filimibeat.data.repository.NetworkState
import arunkbabu90.filimibeat.data.repository.ReviewRepository
import arunkbabu90.filimibeat.databinding.ActivityReviewsBinding
import arunkbabu90.filimibeat.databinding.ItemNetworkStateBinding
import arunkbabu90.filimibeat.ui.adapter.ReviewAdapter
import arunkbabu90.filimibeat.ui.viewmodel.ReviewViewModel

class ReviewsActivity : AppCompatActivity() {
    private lateinit var repository: ReviewRepository
    private lateinit var binding: ActivityReviewsBinding
    private lateinit var networkBinding: ItemNetworkStateBinding

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
        val adapter = ReviewAdapter()

        binding.rvReviews.layoutManager = lm
        binding.rvReviews.setHasFixedSize(true)
        binding.rvReviews.adapter = adapter

        val viewModel = getViewModel()
        viewModel.fetchReviews(movieId).observe(this, { reviewPagedList ->
            adapter.submitList(reviewPagedList)
        })

        viewModel.networkState.observe(this, { state ->
            networkBinding.itemNetworkStateProgressBar.visibility =
                if (viewModel.isEmpty() && state == NetworkState.LOADING) View.VISIBLE else View.GONE
            networkBinding.itemNetworkStateErrTextView.visibility =
                if (viewModel.isEmpty() && state == NetworkState.ERROR) View.VISIBLE else View.GONE

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