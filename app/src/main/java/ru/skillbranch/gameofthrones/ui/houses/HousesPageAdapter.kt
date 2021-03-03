@file:Suppress("DEPRECATION")

package ru.skillbranch.gameofthrones.ui.houses

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import ru.skillbranch.gameofthrones.data.local.entities.HouseType
import ru.skillbranch.gameofthrones.ui.houses.house.HouseFragment


class HousesPageAdapter(fragmentManager: FragmentManager, private val showBookmarked: Boolean)
    : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return HouseFragment.newInstance(HouseType.values()[position].title, showBookmarked)
    }

    override fun getCount(): Int {
        return HouseType.values().size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return HouseType.values()[position].title
    }

}
