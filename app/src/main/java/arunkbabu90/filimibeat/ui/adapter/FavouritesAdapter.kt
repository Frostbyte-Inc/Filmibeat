package arunkbabu90.filimibeat.ui.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import arunkbabu90.filimibeat.R
import arunkbabu90.filimibeat.data.api.IMG_SIZE_MID
import arunkbabu90.filimibeat.data.model.Favourite
import arunkbabu90.filimibeat.getImageUrl
import arunkbabu90.filimibeat.inflate
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.firebase.ui.firestore.paging.LoadingState
import kotlinx.android.synthetic.main.item_favourites.view.*

class FavouritesAdapter(options: FirestorePagingOptions<Favourite>,
                        private val refreshLayout: SwipeRefreshLayout,
                        private val errView: TextView,
                        private val itemClickListener: (Favourite) -> Unit)
    : FirestorePagingAdapter<Favourite, FavouritesAdapter.FavouritesViewHolder>(options) {

    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritesViewHolder {
        val v = parent.inflate(R.layout.item_favourites)
        context = parent.context
        return FavouritesViewHolder(v, parent.context)
    }

    override fun onBindViewHolder(holder: FavouritesViewHolder, position: Int, model: Favourite) {
        holder.bind(model, itemClickListener)
    }

    override fun onError(e: Exception) {
        super.onError(e)
        errView.visibility = View.VISIBLE
        errView.text = context?.getString(R.string.err_fav_movie)
    }

    override fun onLoadingStateChanged(state: LoadingState) {
        when (state) {
            LoadingState.LOADING_INITIAL -> {
                errView.visibility = View.GONE
                refreshLayout.isRefreshing = true
            }

            LoadingState.LOADING_MORE -> {
                refreshLayout.isRefreshing = true
            }

            LoadingState.ERROR -> {
                errView.visibility = View.VISIBLE
                errView.text = context?.getString(R.string.err_fav_movie)
                refreshLayout.isRefreshing = false
            }

            LoadingState.FINISHED -> {
                refreshLayout.isRefreshing = false
                if (itemCount <= 0) {
                    errView.visibility = View.VISIBLE
                    errView.text = context?.getString(R.string.no_favourites)
                } else {
                    errView.visibility = View.GONE
                }
            }

            else -> super.onLoadingStateChanged(state)
        }
    }

    class FavouritesViewHolder(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView) {
        var movie: Favourite? = null

        fun bind(favourite: Favourite, itemClickListener: (Favourite) -> Unit) {
            movie = favourite

            val posterUrl = getImageUrl(favourite.posterPath, IMG_SIZE_MID)
            Glide.with(context).load(posterUrl).into(itemView.iv_fav_poster)

            itemView.tv_fav_title.text = favourite.title
            itemView.tv_fav_year.text = context.getString(R.string.released, favourite.releaseYear)
            itemView.tv_fav_rating.text = context.getString(R.string.rating, favourite.rating)

            itemView.setOnClickListener{ itemClickListener(favourite) }
        }
    }
}