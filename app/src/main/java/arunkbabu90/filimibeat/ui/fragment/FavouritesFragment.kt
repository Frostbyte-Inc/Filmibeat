package arunkbabu90.filimibeat.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.paging.PagedList
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arunkbabu90.filimibeat.R
import arunkbabu90.filimibeat.data.api.PAGE_SIZE
import arunkbabu90.filimibeat.data.model.Favourite
import arunkbabu90.filimibeat.ui.Constants
import arunkbabu90.filimibeat.ui.activity.MovieDetailsActivity
import arunkbabu90.filimibeat.ui.adapter.FavouritesAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_favourites.*
import kotlinx.android.synthetic.main.item_favourites.*

class FavouritesFragment : Fragment() {
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        db = Firebase.firestore

        val user: FirebaseUser = auth.currentUser ?: return

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPrefetchDistance(10)
            .setPageSize(PAGE_SIZE)
            .build()

        val path = "${Constants.COLLECTION_USERS}/${user.uid}/${Constants.COLLECTION_FAVOURITES}"
        val query = db.collection(path).orderBy(Constants.FIELD_TIMESTAMP, Query.Direction.ASCENDING)

        val options = FirestorePagingOptions.Builder<Favourite>()
            .setLifecycleOwner(this)
            .setQuery(query, config) { snapshot ->
                val fav: Favourite = snapshot.toObject(Favourite::class.java) ?: Favourite()
                fav.movieId = snapshot.id
                fav
            }
            .build()

        val adapter = FavouritesAdapter(options, swipeRefreshLayout_favourites,
            tv_fav_err) { favouriteMovie -> onFavouriteClick(favouriteMovie) }

        val lm = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv_favourites.setHasFixedSize(true)
        rv_favourites.layoutManager = lm
        rv_favourites.adapter = adapter

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Delete the Favourite Movie from the list (database)
                val holder = viewHolder as FavouritesAdapter.FavouritesViewHolder
                val movie = holder.movie ?: return

                db.collection(path)
                    .document(movie.movieId)
                    .delete()
                    .addOnSuccessListener {
                        // Undo Action
                        val snackbar = Snackbar.make(favourites_fragment_layout, "${movie.title} Deleted", Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.undo)) {
                                val favMovie = hashMapOf(
                                    Constants.FIELD_TITLE to movie.title,
                                    Constants.FIELD_POSTER_PATH to movie.posterPath,
                                    Constants.FIELD_BACKDROP_PATH to movie.backdropPath,
                                    Constants.FIELD_RELEASE_YEAR to movie.releaseYear,
                                    Constants.FIELD_RATING to movie.rating,
                                    Constants.FIELD_TIMESTAMP to Timestamp.now())

                                db.collection(path).document(movie.movieId)
                                    .set(favMovie, SetOptions.merge())
                            }
                        snackbar.setActionTextColor(ContextCompat.getColor(requireContext(), R.color.colorGreen))
                        snackbar.show()
                    }
            }
        }).attachToRecyclerView(rv_favourites)
    }

    /**
     * Called when a Favourite Movie is clicked
     */
    private fun onFavouriteClick(favMovie: Favourite) {
        val intent = Intent(activity, MovieDetailsActivity::class.java)
        val posterView = iv_fav_poster
        val transitionOptions: ActivityOptionsCompat? =
            activity?.let { activity ->
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity,
                    posterView,
                    ViewCompat.getTransitionName(posterView) ?: "null"
                )
            }

        intent.putExtra(MovieDetailsActivity.KEY_MOVIE_ID_EXTRA, favMovie.movieId)
        intent.putExtra(MovieDetailsActivity.KEY_POSTER_PATH_EXTRA, favMovie.posterPath)
        intent.putExtra(MovieDetailsActivity.KEY_BACKDROP_PATH_EXTRA, favMovie.backdropPath)
        intent.putExtra(MovieDetailsActivity.KEY_RATING_EXTRA, favMovie.rating)
        intent.putExtra(MovieDetailsActivity.KEY_OVERVIEW_EXTRA, favMovie.overview)
        intent.putExtra(MovieDetailsActivity.KEY_RELEASE_YEAR_EXTRA, favMovie.releaseYear)
        intent.putExtra(MovieDetailsActivity.KEY_TITLE_EXTRA, favMovie.title)

        if (transitionOptions != null)
            startActivity(intent, transitionOptions.toBundle())
        else
            startActivity(intent)
    }
}