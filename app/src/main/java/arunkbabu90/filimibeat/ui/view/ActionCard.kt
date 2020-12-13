package arunkbabu90.filimibeat.ui.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import arunkbabu90.filimibeat.R
import com.google.android.material.card.MaterialCardView

class ActionCard@JvmOverloads constructor(context: Context, attr: AttributeSet, defStyleAttributeSet: Int = 0)
    : MaterialCardView(context, attr, defStyleAttributeSet){
        init {
            inflate(context, R.layout.vm_action_card,this)

            val imageView : ImageView = findViewById(R.id.action_card_icon_view)
            val textView : TextView = findViewById(R.id.action_card_desc)

            val padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics).toInt()
            val cardRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics)
            setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorDarkGrey))
            radius = cardRadius
            cardElevation = 10f
            cardElevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics).toInt().toFloat()
            setPadding(padding, padding, padding, padding)
            val attributes = context.obtainStyledAttributes(attr, R.styleable.ActionCard)
            imageView.setImageDrawable(attributes.getDrawable(R.styleable.ActionCard_icn))

            textView.text = attributes.getString(R.styleable.ActionCard_description)

            attributes.recycle()
        }
    }