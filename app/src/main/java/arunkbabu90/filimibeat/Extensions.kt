package arunkbabu90.filimibeat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

/**
 * Extension function to inflate a view using LayoutInflater
 * @param layoutRes int: ID for an XML layout resource to load (e.g., R.layout.main_page)
 * @param attachToRoot boolean: Whether the inflated hierarchy should be attached to the
 * root parameter? If false, root is only used to create the correct subclass of LayoutParams
 * for the root view in the XML.(Default: false)
 */
fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}