package com.example.piapoi.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.piapoi.Adapter.ContactsAdapter
import com.example.piapoi.Helpers.UserInstance
import com.example.piapoi.Models.Contact
import com.example.piapoi.Models.Team
import com.example.piapoi.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_group_members_team.*


class GroupMembersTeamFragment(val teamUid: String) : Fragment() {
    val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().reference
    var contactList : ArrayList<Contact>? = ArrayList();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        var layoutManager =  LinearLayoutManager(context)
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL)
        team_members_recyclerview.layoutManager = layoutManager
        //  contact_recyclerview.layoutManager= LinearLayoutManager(this)

        initMembers(view)
    }

    private fun initMembers(view:View) {

        databaseReference.child(Team::class.java.simpleName).child(UserInstance.getUserInstance()?.career.toString()).child(teamUid).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               snapshot.children.forEach { currentContact ->
                   val contact = currentContact.getValue(Contact::class.java) as Contact

                   contactList?.add(contact)

               }
                val recycler=view.findViewById<RecyclerView>(R.id.team_members_recyclerview)
                recycler.adapter= ContactsAdapter(contactList!!)
            }

            override fun onCancelled(error: DatabaseError) {

            }

    })
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_members_team, container, false)
    }

}