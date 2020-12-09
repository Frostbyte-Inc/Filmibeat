package arunkbabu90.filimibeat.ui.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import arunkbabu90.filimibeat.R
import com.google.android.material.card.MaterialCardView

class ActionCard@JvmOverloads constructor(context: Context, attr: AttributeSet, defStyleAttributeSet: int = 0)
    : MaterialCardView(context, attr, defStyleAttributeSet){
        init {
            inflate(context, R.layout.vm_action_card,this)
            val imageView : ImageView = findViewById(R.id.action_card_icon_view)
            val textView : TextView = findViewById(R.id.action_card_desc)
            val attributes = context.obtainStyledAttributes(attr, R.styleable.ActionCard)
            imageView.setImageDrawable(attributes.getDrawable(R.styleable.ActionCard_icon))
            textView.text = attributes.getString(R.styleable.ActionCard_description)
            attributes.recycle()
        }
    }