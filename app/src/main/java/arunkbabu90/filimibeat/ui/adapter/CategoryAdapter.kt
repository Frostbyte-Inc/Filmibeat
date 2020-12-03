package arunkbabu90.filimibeat.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import arunkbabu90.filimibeat.Constants
import arunkbabu90.filimibeat.ui.fragment.*
import org.jetbrains.annotations.NotNull

class CategoryAdapter(@NotNull fm: FragmentManager, @NotNull lifecycle: Lifecycle,
                      private val pageCount: Int)
    : FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int = pageCount

    override fun createFragment(position: Int): Fragment {
        return setFragmentsBasedOnUserType(position)
    }

    /**
     * Helper method to set the fragments to the pager based on the userType
     * @param position The tab position
     */
    private fun setFragmentsBasedOnUserType(position: Int): Fragment {
        return if (Constants.userType == Constants.USER_TYPE_PERSON) {
            // Normal User
            when (position) {
                0 -> NowPlayingFragment()
                1 -> PopularFragment()
                2 -> TopRatedFragment()
                3 -> SearchFragment()
                4 -> FavouritesFragment()
                else -> NowPlayingFragment()
            }
        } else {
            // Other Users; Guest
            when (position) {
                0 -> NowPlayingFragment()
                1 -> PopularFragment()
                2 -> TopRatedFragment()
                3 -> SearchFragment()
                else -> NowPlayingFragment()
            }
        }
    }
}