package arunkbabu90.filimibeat.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import arunkbabu90.filimibeat.R
import arunkbabu90.filimibeat.data.api.IMG_SIZE_MID
import arunkbabu90.filimibeat.data.model.Person
import arunkbabu90.filimibeat.getImageUrl
import arunkbabu90.filimibeat.inflate
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.item_person.view.*

class CastCrewAdapter(private val isCast: Boolean = false,
                      private val personList: ArrayList<Person>)
    : RecyclerView.Adapter<CastCrewAdapter.CastCrewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastCrewViewHolder {
        val view = parent.inflate(R.layout.item_person)
        return CastCrewViewHolder(view)
    }

    override fun onBindViewHolder(holder: CastCrewViewHolder, position: Int) {
        holder.bind(personList[position])
    }

    override fun getItemCount() = personList.size

    inner class CastCrewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(person: Person?) {
            val dpUrl = getImageUrl(person?.dpPath ?: "", IMG_SIZE_MID)
            Glide.with(itemView.context)
                .load(dpUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.default_dp)
                .into(itemView.itemPerson_displayPicture)

            if (isCast) {
                // Cast
                itemView.itemPerson_name.text = person?.name
                itemView.itemPerson_designation.text = person?.characterName
            } else {
                // Crew
                itemView.itemPerson_name.text = person?.name
                itemView.itemPerson_designation.text = person?.department
            }
        }
    }
}