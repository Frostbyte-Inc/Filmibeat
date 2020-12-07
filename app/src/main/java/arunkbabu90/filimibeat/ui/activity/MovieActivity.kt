package arunkbabu90.filimibeat.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import arunkbabu90.filimibeat.Constants
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

        // Set Menu Behaviours
        binding.toolbarMain.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.mnu_favourites -> {
                    if (Constants.userType == Constants.USER_TYPE_PERSON) {
                        // Safety Check to prevent opening Favourites if the user is a Guest
                        val favIntent = Intent(this, FavouritesActivity::class.java)
                        startActivity(favIntent)
                    }
                    true
                }

                R.id.mnu_profile_name -> {
                    val profIntent = Intent(this, ProfileActivity::class.java)
                    startActivity(profIntent)
                    true
                }
                else -> false
            }
        }
        val menu = if (Constants.userType == Constants.USER_TYPE_PERSON) R.menu.main_menu_user else R.menu.main_menu_guest
        binding.toolbarMain.inflateMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (false) {
            menuInflater.inflate(R.menu.main_menu_user, menu)
        } else {
            menuInflater.inflate(R.menu.main_menu_guest, menu)
        }
        return true
    }
}