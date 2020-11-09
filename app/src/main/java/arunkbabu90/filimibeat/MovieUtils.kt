@file:JvmName("MovieUtils")
package arunkbabu90.filimibeat

import android.content.Context

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




