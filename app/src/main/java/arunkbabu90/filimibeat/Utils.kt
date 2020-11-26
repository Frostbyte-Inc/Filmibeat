@file:JvmName("Utils")
package arunkbabu90.filimibeat

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
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

/**
 * Check for internet availability
 * @return True: if internet is available; False otherwise
 */
fun isNetworkConnected(context: Context?): Boolean {
    if (context == null) return false

    val cm: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
        val network: Network = cm.activeNetwork ?: return false
        val nc: NetworkCapabilities = cm.getNetworkCapabilities(network) ?: return false

        return nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                nc.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                nc.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH)
    } else {
        return cm.activeNetworkInfo?.isConnected ?: false
    }
}

/**
 * Checks whether the provided email is valid
 * @param email The email id to be verified
 * @return True if the email is valid
 */
fun verifyEmail(email: String): Boolean {
    val mailFormat = Regex ("^([a-zA-B 0-9.-]+)@([a-zA-B 0-9]+)\\.([a-zA-Z]{2,8})(\\.[a-zA-Z]{2,8})?$")
    return !email.matches(mailFormat)
}




