package arunkbabu90.filimibeat.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import arunkbabu90.filimibeat.R
import arunkbabu90.filimibeat.ui.Constants
import arunkbabu90.filimibeat.ui.adapter.CategoryAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_movie.*

class MovieActivity : AppCompatActivity() {
    private var tabLayoutMediator: TabLayoutMediator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        window.statusBarColor = ActivityCompat.getColor(this, R.color.colorPurpleDark)
        window.navigationBarColor = ActivityCompat.getColor(this, R.color.colorPurple)

        // Set the title of the tabs
        tabLayoutMediator = TabLayoutMediator(movie_tab_layout, movie_view_pager) { tab, position ->
            tab.text = setTabTitle(position)
        }

        val pageCount = if (Constants.userType == Constants.USER_TYPE_PERSON) 5 else 4
        val categoryAdapter = CategoryAdapter(supportFragmentManager, lifecycle, pageCount)
        movie_view_pager.adapter = categoryAdapter
        movie_view_pager.offscreenPageLimit = 4

        tabLayoutMediator?.attach()
    }

    /**
     * Helper method to add or remove tab title based on userType
     * @param position The tab position
     */
    private fun setTabTitle(position: Int): String {
        return if (Constants.userType == Constants.USER_TYPE_PERSON) {
            // Normal User
            when (position) {
                0 -> getString(R.string.tab_title_now_playing)
                1 -> getString(R.string.tab_title_popular)
                2 -> getString(R.string.tab_title_top_rated)
                3 -> getString(R.string.tab_title_search)
                4 -> getString(R.string.tab_title_favourites)
                else -> ""
            }
        } else {
            // Other users; Guest
            when (position) {
                0 -> getString(R.string.tab_title_now_playing)
                1 -> getString(R.string.tab_title_popular)
                2 -> getString(R.string.tab_title_top_rated)
                3 -> getString(R.string.tab_title_search)
                else -> ""
            }
        }
    }
}