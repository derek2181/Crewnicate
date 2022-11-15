package com.example.piapoi.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class TasksViewPagerAdapter(manager: FragmentManager): FragmentStatePagerAdapter(manager){
    private val fragmentList : MutableList<Fragment> = ArrayList();
    private val titleList : MutableList<String> = ArrayList()

    override fun getCount()=fragmentList.size

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    public fun addFragment(fragment: Fragment, title:String){
        fragmentList.add(fragment);
        titleList.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titleList[position]
    }
}