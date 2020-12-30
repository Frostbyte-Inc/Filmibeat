package arunkbabu90.filimibeat.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import arunkbabu90.filimibeat.Constants
import arunkbabu90.filimibeat.R
import arunkbabu90.filimibeat.databinding.ActivityMovieBinding
import arunkbabu90.filimibeat.ui.adapter.CategoryAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_movie.*

class MovieActivity : AppCompatActivity(), FirebaseAuth.AuthStateListener {
    private lateinit var binding: ActivityMovieBinding
    private lateinit var auth: FirebaseAuth

    private var tabLayoutMediator: TabLayoutMediator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        auth.addAuthStateListener(this)

        // Set the user id and name globally available in the app to the user
        Constants.userId = auth.currentUser?.uid ?: ""
        Constants.userFullName = auth.currentUser?.displayName ?: ""

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
                    // Favourites
                    if (Constants.userType == Constants.USER_TYPE_PERSON) {
                        // Safety Check to prevent opening Favourites if the user is a Guest
                        val favIntent = Intent(this, FavouritesActivity::class.java)
                        startActivity(favIntent)
                    }
                    true
                }

                R.id.mnu_profile_name -> {
                    // Profile
                    if (Constants.userType == Constants.USER_TYPE_PERSON) {
                        // Prevent opening Profile if the user is a Guest
                        val profIntent = Intent(this, ProfileActivity::class.java)
                        startActivity(profIntent)
                    }
                    true
                }
                R.id.mnu_sign_out -> {
                    // Sign Out
                    if (Constants.userType == Constants.USER_TYPE_PERSON) {
                        auth.signOut()
                    } else {
                        startActivity(Intent(this, LoginActivity::class.java))
                        Toast.makeText(this, getString(R.string.signed_out), Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    true
                }
                else -> false
            }
        }
        val menu = if (Constants.userType == Constants.USER_TYPE_PERSON) R.menu.main_menu_user else R.menu.main_menu_guest
        binding.toolbarMain.inflateMenu(menu)
    }

    override fun onAuthStateChanged(p0: FirebaseAuth) {
        // User is either signed out or the login credentials no longer exists. So launch the login
        // activity again for the user to sign-in if the user is not a Guest User
        if (auth.currentUser == null && Constants.userType != Constants.USER_TYPE_GUEST) {
            startActivity(Intent(this, LoginActivity::class.java))
            Toast.makeText(this, getString(R.string.signed_out), Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}