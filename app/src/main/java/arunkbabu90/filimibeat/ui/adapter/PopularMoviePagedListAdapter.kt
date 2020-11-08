package arunkbabu90.filimibeat.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import arunkbabu90.filimibeat.R
import arunkbabu90.filimibeat.data.database.MoviePopular
import arunkbabu90.filimibeat.data.repository.NetworkState
import arunkbabu90.filimibeat.inflate
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_movie.view.*
import kotlinx.android.synthetic.main.item_network_state.view.*

class PopularMoviePagedListAdapter : PagedListAdapter<MoviePopular, RecyclerView.ViewHolder>(PopularMovieDiffCallback()) {

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
        if (getItemViewType(position) == VIEW_TYPE_MOVIE) {
            (holder as MovieViewHolder).bind(getItem(position))
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
        fun bind(movie: MoviePopular?) {
            itemView.tv_poster_title.text = movie?.title
            Glide.with(itemView.context).load(movie?.posterUrl).into(itemView.iv_main_poster)
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
                itemView.item_network_state_progress_bar.visibility = View.VISIBLE
                itemView.item_network_state_err_text_view.text = networkState.msg
            } else {
                itemView.item_network_state_err_text_view.visibility = View.GONE
            }
        }
    }

    class PopularMovieDiffCallback: DiffUtil.ItemCallback<MoviePopular>() {
        override fun areItemsTheSame(oldItem: MoviePopular, newItem: MoviePopular): Boolean
                = oldItem.movieId == newItem.movieId

        override fun areContentsTheSame(oldItem: MoviePopular, newItem: MoviePopular): Boolean
                = oldItem == newItem
    }
}