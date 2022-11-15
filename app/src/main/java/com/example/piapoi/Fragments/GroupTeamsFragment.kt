package com.example.piapoi.Fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.piapoi.Adapter.ChatPreviewAdapter
import com.example.piapoi.Adapter.GroupsAdapter
import com.example.piapoi.CreateTeamActivity
import com.example.piapoi.Helpers.UserInstance
import com.example.piapoi.Helpers.hideProgressBar
import com.example.piapoi.Helpers.showProgressBar
import com.example.piapoi.Models.Contact
import com.example.piapoi.Models.Team
import com.example.piapoi.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_send_new_message.*
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_group_teams.*


class GroupTeamsFragment : Fragment() {
    private val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().reference
    private lateinit var dialog : Dialog
    private var teams = mutableListOf<Team>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_teams, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog= Dialog(view.context)
        var layoutManager =  LinearLayoutManager(context)
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL)
        teams_recycler_view.layoutManager = layoutManager

        initGroups(view)
        var btnAddTeam=view.findViewById<ImageButton>(R.id.addGroup)

        btnAddTeam.setOnClickListener{

            val intent = Intent(activity, CreateTeamActivity::class.java)

            startActivity(intent)

        }

    }
    private fun initGroups(view : View)
    {
        dialog=Dialog(view.context)
        showProgressBar(dialog)

        databaseReference.child(Team::class.java.simpleName).child(UserInstance.getUserInstance()?.career.toString()).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                teams.clear()
                snapshot.children.forEach({team->

                    val contactList = mutableListOf<Contact>()
                    team.children.forEach({contact->
                        val contact = Contact( contact.child("name").value.toString(),
                            contact.child("lastName").value.toString(),contact.child("imagePath").value.toString()
                            ,contact.child("uid").value.toString(),contact.child("active").value.toString(),
                            contact.child("career").value.toString())

                        contactList.add(contact)
                    })

                    val aux =Team(team.key.toString(),contactList)


                    for(member in aux.members){
                        if(member.uid.equals(UserInstance.getUserInstance()?.uid.toString())){
                            teams.add(aux)
                        }
                    }

                })
                val recycler=view.findViewById<RecyclerView>(R.id.teams_recycler_view)
                if (teams?.size!! > 0 ) {
                    recycler.adapter= GroupsAdapter(teams)
                    recycler.smoothScrollToPosition(teams!!.size - 1)
                }
                hideProgressBar(dialog)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

    }
}