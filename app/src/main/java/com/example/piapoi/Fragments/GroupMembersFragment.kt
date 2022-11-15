package com.example.piapoi.Fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.piapoi.Adapter.ContactsAdapter
import com.example.piapoi.Helpers.UserInstance
import com.example.piapoi.Helpers.hideProgressBar
import com.example.piapoi.Helpers.showProgressBar
import com.example.piapoi.Models.Contact
import com.example.piapoi.Models.User

import com.example.piapoi.R
import com.google.firebase.database.*



import kotlinx.android.synthetic.main.fragment_group_members.*

class GroupMembersFragment : Fragment() {
    private lateinit var dialog : Dialog
    val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().reference
    var contactList : ArrayList<Contact>? = ArrayList();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_group_members, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog= Dialog(view.context)

        var layoutManager =  LinearLayoutManager(context)
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL)

        val recycler=view.findViewById<RecyclerView>(R.id.group_members_recyclerview)
        recycler.layoutManager = layoutManager
        recycler.layoutManager= LinearLayoutManager(view.context)
        initContacts(view)
    }
    private fun initContacts(view :View) {
        databaseReference.child(Contact::class.java.simpleName).addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(snapshotError: DatabaseError) {

            }
            override fun onDataChange(snapshot: DataSnapshot) {
                dialog= Dialog(view.context)
                showProgressBar(dialog)
                if(contactList!=null){
                    contactList?.clear()
                }
                val activeUid : String = UserInstance.getUserInstance()?.uid.toString()
                snapshot!!.children.forEach {
                    val contact :Contact? =  it.getValue(Contact::class.java) as Contact
                    if (contact != null) {

                        contact.active= it.child("active")?.getValue(String::class.java) as String
                        if(!contact.uid.equals(activeUid) && contact.career.equals(UserInstance.getUserInstance()?.career.toString())){
                            contactList?.add(contact)
                        }
                    }
                }
                val recycler=view.findViewById<RecyclerView>(R.id.group_members_recyclerview)
                recycler.adapter= ContactsAdapter(contactList!!)
                hideProgressBar(dialog)
            }
        }
        )
    }
}