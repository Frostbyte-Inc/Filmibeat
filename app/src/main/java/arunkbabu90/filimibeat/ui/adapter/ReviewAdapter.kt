package arunkbabu90.filimibeat.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import arunkbabu90.filimibeat.R
import arunkbabu90.filimibeat.data.model.Review
import arunkbabu90.filimibeat.data.repository.NetworkState
import arunkbabu90.filimibeat.databinding.ItemNetworkStateBinding
import arunkbabu90.filimibeat.databinding.ItemReviewBinding
import arunkbabu90.filimibeat.inflate

class ReviewAdapter : PagedListAdapter<Review, RecyclerView.ViewHolder>(ReviewDiffCallback()) {
    private lateinit var binding: ItemReviewBinding
    private lateinit var networkBinding: ItemNetworkStateBinding

    val VIEW_TYPE_REVIEW = 1
    val VIEW_TYPE_NETWORK = 2

    private var networkState: NetworkState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemReviewBinding.inflate(inflater, parent, false)
        networkBinding = ItemNetworkStateBinding.bind(parent.inflate(R.layout.item_network_state))

        return if (viewType == VIEW_TYPE_REVIEW)
            ReviewViewHolder(binding.root)
        else
            NetworkStateViewHolder(networkBinding.root)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ReviewViewHolder).bind(getItem(position))
        (holder as NetworkStateViewHolder).bind(networkState)
    }

    private inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(review: Review?) {

        }
    }

    private class NetworkStateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(networkState: NetworkState?) {

        }
    }

    class ReviewDiffCallback : DiffUtil.ItemCallback<Review>() {
        override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem == newItem
        }
    }
}