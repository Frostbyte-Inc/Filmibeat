package arunkbabu90.filimibeat.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import arunkbabu90.filimibeat.ui.fragment.NowPlayingFragment
import arunkbabu90.filimibeat.ui.fragment.PopularFragment
import arunkbabu90.filimibeat.ui.fragment.SearchFragment
import arunkbabu90.filimibeat.ui.fragment.TopRatedFragment
import org.jetbrains.annotations.NotNull

class CategoryAdapter(@NotNull fm: FragmentManager, @NotNull lifecycle: Lifecycle)
    : FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> NowPlayingFragment()
            1 -> PopularFragment()
            2 -> TopRatedFragment()
            3 -> SearchFragment()
            else -> NowPlayingFragment()
        }
    }
}