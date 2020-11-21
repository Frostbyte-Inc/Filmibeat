@file:JvmName("MovieUtils")
package arunkbabu90.filimibeat

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentActivity


/**
 * Intelligently calculates the number of grid columns to be displayed on screen with respect to
 * the available screen size
 * @param context The Application Context
 * @return int  The number of columns to be displayed
 */
fun calculateNoOfColumns(context: Context?): Int {
    return if (context != null) {
        val displayMetrics = context.resources.displayMetrics
        val dpWidth = displayMetrics.widthPixels / displayMetrics.density
        val columnWidth = 115
        (dpWidth / columnWidth).toInt()
    } else {
        0
    }
}

/**
 * Hides the virtual keyboard from the activity
 * @param activity The current activity where the virtual keyboard exists
 */
fun closeSoftInput(activity: FragmentActivity?) {
    val inputMethodManager: InputMethodManager? = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    var v: View? = activity.currentFocus
    if (v == null) v = View(activity)

    inputMethodManager?.hideSoftInputFromWindow(v.windowToken, 0)
}




