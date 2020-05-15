package ru.skillbranch.gameofthrones.ui.houses

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import ru.skillbranch.gameofthrones.data.local.entities.HouseType
import ru.skillbranch.gameofthrones.ui.houses.house.HouseFragment

// 24:44
class HousesPagerAdapter(fm: FragmentManager) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return HouseFragment.newInstance(HouseType.values()[position].title)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return HouseType.values()[position].title
    }

    override fun getCount(): Int {
        return HouseType.values().size
    }


}