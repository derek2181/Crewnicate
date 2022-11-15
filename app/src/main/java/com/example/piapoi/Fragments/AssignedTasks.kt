package com.example.piapoi.Fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.piapoi.Adapter.AssignmentCompletedAdapter
import com.example.piapoi.Adapter.AssignmentsAdapter
import com.example.piapoi.Adapter.GroupsAdapter
import com.example.piapoi.Helpers.UserInstance
import com.example.piapoi.Helpers.hideProgressBar
import com.example.piapoi.Helpers.showProgressBar
import com.example.piapoi.Models.Assignment
import com.example.piapoi.Models.AssignmentCompleted
import com.example.piapoi.Models.Contact
import com.example.piapoi.Models.Team
import com.example.piapoi.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_create_task.*
import kotlinx.android.synthetic.main.fragment_assigned_tasks.*
import kotlinx.android.synthetic.main.fragment_completed_task.*
import java.text.SimpleDateFormat
import java.util.*


class AssignedTasks : Fragment() {
    private val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().reference
    private lateinit var dialog : Dialog
    private val assignments= mutableListOf<Assignment>()
    private val assignmentsCompletedId= mutableListOf<String>()
    private val teams= mutableListOf<String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_assigned_tasks, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager =  LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        assignedTasksRecyclerView.layoutManager = layoutManager;
        // recyclerViewChats.layoutManager= LinearLayoutManager(activity)
        initAssignment(view)
    }
    private fun initAssignment(view: View) {
        dialog= Dialog(view.context)
        showProgressBar(dialog)

        databaseReference.child(Team::class.java.simpleName).child(UserInstance.getUserInstance()?.career.toString()).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                snapshot.children.forEach { team ->

                    val contactList = mutableListOf<Contact>()
                    team.children.forEach { contact ->
                        val contact = Contact(
                            contact.child("name").value.toString(),
                            contact.child("lastName").value.toString(),
                            contact.child("imagePath").value.toString(),
                            contact.child("uid").value.toString(),
                            contact.child("active").value.toString(),
                            contact.child("career").value.toString()
                        )

                        contactList.add(contact)
                    }

                    val aux = Team(team.key.toString(), contactList)


                    for (member in aux.members) {
                        if (member.uid.equals(UserInstance.getUserInstance()?.uid.toString())) {
                            teams.add(aux.name.toString())
                        }
                    }

                }

                databaseReference.child(AssignmentCompleted::class.java.simpleName)
                    .child(UserInstance.getUserInstance()?.uid.toString())
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {

                            snapshot.children.forEach { dbTeam ->

                                val assignmentId= dbTeam.child("id").value.toString()

                                assignmentsCompletedId.add(assignmentId)
                            }

                            databaseReference.child(Assignment::class.java.simpleName)
                                .child(UserInstance.getUserInstance()?.career.toString())
                                .addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        assignments.clear()
                                        snapshot.children.forEach { dbTeam ->


                                            if (teams.contains(dbTeam.key.toString())) {

                                                dbTeam.children.forEach { task ->

                                                    if (!assignmentsCompletedId.contains(
                                                            task.child(
                                                                "id"
                                                            ).value.toString()
                                                        )
                                                    ) {
                                                        var date =
                                                            task.child("date").value.toString()
                                                                .toLong()
                                                        //holder.view.date_message.text =SimpleDateFormat("dd/MM/yyyy - HH:mm:ss", Locale("es", "MX")).format(date)
                                                        val dateText = SimpleDateFormat(
                                                            "dd/MM/yyyy",
                                                            Locale("es", "MX")
                                                        ).format(date)

                                                        val assignment = Assignment(
                                                            task.child("team").value.toString(),
                                                            task.child("name").value.toString(),
                                                            "Assigned " + dateText,
                                                            task.child("description").value.toString(),
                                                            task.child("id").value.toString()
                                                        )
                                                        assignments.add(assignment)

                                                    }

                                                }


                                            }
                                        }
                                        if (teams?.size!! > 0 )
                                        {
                                            val recycler=view.findViewById<RecyclerView>(R.id.assignedTasksRecyclerView)
                                            recycler.adapter = AssignmentsAdapter(assignments)
                                            recycler.smoothScrollToPosition(teams!!.size - 1)
                                        }
                                        hideProgressBar(dialog)
                                    }



                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }

                                    })
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }


                    })





            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

    }
    }


