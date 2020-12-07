package arunkbabu90.filimibeat.ui.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.annotation.StyleRes
import arunkbabu90.filimibeat.R
import com.google.android.material.card.MaterialCardView

class ActionCard @JvmOverloads constructor(context: Context, attr:AttributeSet?=null, defStyleAttr: int = 0, defStyleRes: StyleRes)
    : MaterialCardView(context,attr,defStyleAttr){
        private var iconResourceid=-1
        private  var cardDescriptionText = ""
    init {
        val a: TypedArray = context.theme.obtainStyledAttributes(attr, R.styleable.ActionCard,defStyleAttr, defStyleRes: 0)
        try {
            iconResourceid = a.getResourceId(R.styleable.ActionCard_icon, defvalue:-1)
            cardDescriptionText = a.getString(R.styleable.ActionCard_description) ?: ""
        }
finally {
    a.recycle()
}
        val inflater context.getStystemService(context.LAYOUT_INFLATER_SERVICE) as LayoutInfalter
    }
    }