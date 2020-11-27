package arunkbabu90.filimibeat.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import arunkbabu90.filimibeat.R
import arunkbabu90.filimibeat.data.api.IMG_SIZE_MID
import arunkbabu90.filimibeat.data.model.Movie
import arunkbabu90.filimibeat.data.repository.NetworkState
import arunkbabu90.filimibeat.getImageUrl
import arunkbabu90.filimibeat.inflate
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_movie.view.*
import kotlinx.android.synthetic.main.item_network_state.view.*

class MovieAdapter(private val itemClickListener: (Movie?) -> Unit)
    : PagedListAdapter<Movie, RecyclerView.ViewHolder>(MovieDiffCallback()) {
        
    val VIEW_TYPE_MOVIE = 1
    val VIEW_TYPE_NETWORK = 2

    private var networkState: NetworkState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_MOVIE)
            MovieViewHolder(parent.inflate(R.layout.item_movie))
        else
            NetworkStateViewHolder(parent.inflate(R.layout.item_network_state))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val movie = getItem(position) ?: return

        if (getItemViewType(position) == VIEW_TYPE_MOVIE) {
            (holder as MovieViewHolder).bind(movie, itemClickListener)
        } else {
            (holder as NetworkStateViewHolder).bind(networkState)
        }
    }

    override fun getItemCount(): Int = super.getItemCount() + if (hasExtraRow()) 1 else 0

    override fun getItemViewType(position: Int): Int
            = if (hasExtraRow() && position == itemCount - 1) VIEW_TYPE_NETWORK else VIEW_TYPE_MOVIE

    private fun hasExtraRow(): Boolean = networkState != null && networkState != NetworkState.LOADED

    fun setNetworkState(networkState: NetworkState) {
        val prevState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = networkState
        val hasExtraRow = hasExtraRow()

        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow)
                notifyItemRemoved(super.getItemCount())
            else
                notifyItemInserted(super.getItemCount())
        } else if (hasExtraRow && prevState != networkState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    /**
     * ViewHolder for the movies
     */
    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(movie: Movie, itemClickListener: (Movie?) -> Unit) {
            val posterUrl = getImageUrl(movie.posterPath, IMG_SIZE_MID)
            Glide.with(itemView.context).load(posterUrl).error(R.drawable.ic_img_err).into(itemView.iv_main_poster)

            itemView.tv_poster_title.text = movie.title

            itemView.setOnClickListener { itemClickListener(movie) }
        }
    }

    /**
     * ViewHolder for the Network State
     */
    class NetworkStateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(networkState: NetworkState?) {
            if (networkState != null && networkState == NetworkState.LOADING) {
                itemView.item_network_state_progress_bar.visibility = View.VISIBLE
            } else {
                itemView.item_network_state_progress_bar.visibility = View.GONE
            }

            if (networkState != null && networkState == NetworkState.ERROR) {
                itemView.item_network_state_err_text_view.visibility = View.VISIBLE
                itemView.item_network_state_err_text_view.text = networkState.msg
            } else if (networkState != null && networkState == NetworkState.EOL) {
                itemView.item_network_state_err_text_view.visibility = View.VISIBLE
                itemView.item_network_state_err_text_view.text = networkState.msg
            } else {
                itemView.item_network_state_err_text_view.visibility = View.GONE
            }

            if (networkState != null && networkState == NetworkState.CLEAR) {
                itemView.item_network_state_err_text_view.visibility = View.VISIBLE
                itemView.item_network_state_err_text_view.text = networkState.msg
            }
        }
    }

    class MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean
                = oldItem.movieId == newItem.movieId

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean
                = oldItem == newItem
    }
}