package com.example.piapoi.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.viewpager.widget.ViewPager
import com.example.piapoi.Adapter.TasksViewPagerAdapter
import com.example.piapoi.CreateTaskActivity
import com.example.piapoi.R
import com.google.android.material.tabs.TabLayout


class TasksFragment : Fragment() {

    private var fragmentName="TASKS"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    public fun getFragmentName(): String {
        return fragmentName;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addTask=view.findViewById<ImageButton>(R.id.addTask)
        addTask.setOnClickListener { view ->
            val intent = Intent(activity, CreateTaskActivity::class.java)
            activity?.startActivity(intent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val v=inflater.inflate(R.layout.fragment_tasks, container, false)

        initTabLayout(v)
        return v;
    }


    private fun initTabLayout(v : View) {
        //Aqui estoy cocinando arroz
        val tab =v.findViewById<TabLayout>(R.id.tabLayout)
        val pager= v.findViewById<ViewPager>(R.id.viewPager)
        //Aqui rosas me paso las verduras y las metio a la olla
        pager.isSaveEnabled=false; //Esto hace que ya no truene por el unique id que tenian los otros Fragments ahora unos huevitos con chorizo de vaca
        val adapter=TasksViewPagerAdapter(childFragmentManager)
        adapter.addFragment(AssignedTasks(),"Assigned")
        adapter.addFragment(CompletedTaskFragment(), "Completed")
        pager.adapter=adapter;
        //Aqui junto to/do y a darle con mole de olla
        tab.setupWithViewPager(pager);


    }


}