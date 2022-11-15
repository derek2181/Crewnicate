package com.example.piapoi.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.example.piapoi.Adapter.TasksViewPagerAdapter
import com.example.piapoi.Helpers.UserInstance
import com.example.piapoi.R
import com.google.android.material.tabs.TabLayout


class HomeFragment : Fragment() {

    private var fragmentName=UserInstance.getUserInstance()?.career.toString()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    fun getFragmentName(): String {
        return fragmentName;
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTabLayout(view)
    }

    private fun initTabLayout(v :View ) {
        val tab =v.findViewById<TabLayout>(R.id.tabGroupLayout)
        val pager= v.findViewById<ViewPager>(R.id.viewGroupPager)
        pager.isSaveEnabled=false;
        val adapter= TasksViewPagerAdapter(childFragmentManager)
        adapter.addFragment(GeneralChatFragment(),"General")
        adapter.addFragment(GroupTeamsFragment(), "Groups")
        adapter.addFragment(GroupMembersFragment(), "Members")
        pager.adapter=adapter

        tab.setupWithViewPager(pager)
    }




}