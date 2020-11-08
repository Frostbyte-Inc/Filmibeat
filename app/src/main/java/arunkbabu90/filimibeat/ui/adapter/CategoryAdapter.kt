package arunkbabu90.filimibeat.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import arunkbabu90.filimibeat.ui.fragment.*
import org.jetbrains.annotations.NotNull

class CategoryAdapter(@NotNull fm: FragmentManager, @NotNull lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle) {
    companion object {
        private const val NUM_PAGES = 5
    }

    override fun getItemCount(): Int = NUM_PAGES

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> NowPlayingFragment()
            1 -> TopRatedFragment()
            2 -> PopularFragment()
            3 -> SearchFragment()
            4 -> FavouritesFragment()
            else -> NowPlayingFragment()
        }
    }
}