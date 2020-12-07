package arunkbabu90.filimibeat.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import arunkbabu90.filimibeat.R
import arunkbabu90.filimibeat.databinding.ActivityMovieBinding
import arunkbabu90.filimibeat.ui.adapter.CategoryAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_movie.*

class MovieActivity : AppCompatActivity() {
    private var tabLayoutMediator: TabLayoutMediator? = null
    private lateinit var binding: ActivityMovieBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ActivityCompat.getColor(this, R.color.colorPurpleDark)
        window.navigationBarColor = ActivityCompat.getColor(this, R.color.colorPurple)

        // Set the title of the tabs
        tabLayoutMediator = TabLayoutMediator(movie_tab_layout, movie_view_pager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_title_now_playing)
                1 -> getString(R.string.tab_title_popular)
                2 -> getString(R.string.tab_title_top_rated)
                3 -> getString(R.string.tab_title_search)
                else -> ""
            }
        }

        val categoryAdapter = CategoryAdapter(supportFragmentManager, lifecycle)
        movie_view_pager.adapter = categoryAdapter
        movie_view_pager.offscreenPageLimit = 4

        tabLayoutMediator?.attach()


        // Set Toolbar
        binding.toolbarMain.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.mnu_favourites -> {
                    true
                }

                R.id.mnu_profile_name -> {
                    true
                }
                else -> false
            }
        }
    }
}