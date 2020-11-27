package arunkbabu90.filimibeat.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import arunkbabu90.filimibeat.R
import arunkbabu90.filimibeat.calculateNoOfColumns
import arunkbabu90.filimibeat.data.api.TMDBClient
import arunkbabu90.filimibeat.data.api.TMDBInterface
import arunkbabu90.filimibeat.data.model.Movie
import arunkbabu90.filimibeat.data.repository.MoviePopularRepository
import arunkbabu90.filimibeat.data.repository.NetworkState
import arunkbabu90.filimibeat.ui.activity.MovieDetailsActivity
import arunkbabu90.filimibeat.ui.adapter.MovieAdapter
import arunkbabu90.filimibeat.ui.viewmodel.PopularMovieViewModel
import kotlinx.android.synthetic.main.fragment_movies_list.*
import kotlinx.android.synthetic.main.item_movie.*
import kotlinx.android.synthetic.main.item_network_state.*
import kotlin.concurrent.thread

class PopularFragment : Fragment() {
    private lateinit var repository: MoviePopularRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movies_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiService: TMDBInterface = TMDBClient.getClient()
        repository = MoviePopularRepository(apiService)

        val noOfCols: Int = calculateNoOfColumns(context)

        val lm = GridLayoutManager(context, noOfCols)
        val adapter = MovieAdapter { movie -> if (movie != null) onMovieClick(movie) }
        lm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType = adapter.getItemViewType(position)
                return if (viewType == adapter.VIEW_TYPE_MOVIE) 1 else noOfCols
            }
        }
        rv_movie_list?.setHasFixedSize(true)
        rv_movie_list?.layoutManager = lm
        rv_movie_list?.adapter = adapter

        tv_err?.visibility = View.VISIBLE
        tv_err?.text = getString(R.string.loading)

        val viewModel = getViewModel()
        viewModel.popularMovies.observe(this, { moviePagedList ->
            thread {
                adapter.submitList(moviePagedList)
            }
        })

        viewModel.networkState.observe(this, { state ->
            item_network_state_progress_bar?.visibility = if (viewModel.isEmpty() && state == NetworkState.LOADING) View.VISIBLE else View.GONE
            item_network_state_err_text_view?.visibility = if (viewModel.isEmpty() && state == NetworkState.ERROR) View.VISIBLE else View.GONE

            if (state == NetworkState.LOADED)
                tv_err?.visibility = View.GONE

            if (!viewModel.isEmpty()) {
                adapter.setNetworkState(state)
            }
        })
    }

    /**
     * Called when a movie item in the grid is clicked
     * @param movie The popular movie
     */
    private fun onMovieClick(movie: Movie) {
        val intent = Intent(activity, MovieDetailsActivity::class.java)
        val posterView = iv_main_poster
        val transitionOptions: ActivityOptionsCompat? =
            activity?.let { activity ->
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity,
                    posterView,
                    ViewCompat.getTransitionName(posterView) ?: "null"
                )
            }

        intent.putExtra(MovieDetailsActivity.KEY_MOVIE_ID_EXTRA, movie.movieId)
        intent.putExtra(MovieDetailsActivity.KEY_POSTER_PATH_EXTRA, movie.posterPath)
        intent.putExtra(MovieDetailsActivity.KEY_BACKDROP_PATH_EXTRA, movie.backdropPath)
        intent.putExtra(MovieDetailsActivity.KEY_RATING_EXTRA, movie.rating)
        intent.putExtra(MovieDetailsActivity.KEY_OVERVIEW_EXTRA, movie.overview)
        intent.putExtra(MovieDetailsActivity.KEY_RELEASE_YEAR_EXTRA, movie.releaseYear)
        intent.putExtra(MovieDetailsActivity.KEY_TITLE_EXTRA, movie.title)

        if (transitionOptions != null) {
            startActivity(intent, transitionOptions.toBundle())
        }
        else
            startActivity(intent)
    }


    private fun getViewModel(): PopularMovieViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T = PopularMovieViewModel(repository) as T
        })[PopularMovieViewModel::class.java]
    }
}