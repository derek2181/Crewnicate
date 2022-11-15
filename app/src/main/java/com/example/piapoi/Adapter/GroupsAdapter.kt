package com.example.piapoi.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.example.piapoi.Models.Team
import com.example.piapoi.R
import com.example.piapoi.TeamActivity
import kotlinx.android.synthetic.main.group_team_recycler.view.*

class GroupsAdapter(val teams : List<Team>) : RecyclerView.Adapter<GroupsAdapter.GroupsViewHolder>(){
    class GroupsViewHolder(val view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupsViewHolder {
        return GroupsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.group_team_recycler,parent,false)
        );
    }


    override fun onBindViewHolder(holder: GroupsViewHolder, position: Int) {
        val team : Team =teams[position]

        holder.view.teamGroupName.text=team.name.split("-")[0]

        holder.view.numberMembers.text=team.members.size.toString()

        holder.view.setOnClickListener{
            val intent= Intent(holder.view.context,TeamActivity::class.java)
            intent.putExtra("teamUid",team.name)
            holder.view.context.startActivity(intent)
        }
    }

    override fun getItemCount()= teams.size
}