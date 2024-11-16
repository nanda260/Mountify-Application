package com.azhar.infopendakian.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.azhar.infopendakian.fragment.FragmentPeralatan
import com.azhar.infopendakian.fragment.FragmentTips

/**
 * Created by Azhar Rivaldi on 03-06-2021
 * Youtube Channel : https://bit.ly/2PJMowZ
 * Github : https://github.com/AzharRivaldi
 * Twitter : https://twitter.com/azharrvldi_
 * Instagram : https://www.instagram.com/azhardvls_
 * Linkedin : https://www.linkedin.com/in/azhar-rivaldi
 */

class ViewPagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> FragmentPeralatan()
            1 -> FragmentTips()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Peralatan"
            1 -> "Tips"
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }
}
