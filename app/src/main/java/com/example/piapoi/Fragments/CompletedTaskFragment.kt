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
import com.example.piapoi.Helpers.UserInstance
import com.example.piapoi.Helpers.hideProgressBar
import com.example.piapoi.Helpers.showProgressBar
import com.example.piapoi.Models.AssignmentCompleted
import com.example.piapoi.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_completed_task.*
import java.text.SimpleDateFormat
import java.util.*


class CompletedTaskFragment : Fragment() {
    private val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().reference
    private lateinit var dialog : Dialog
    private val assignments= mutableListOf<AssignmentCompleted>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_completed_task, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager =  LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        completedTasksRecyclerView.layoutManager = layoutManager;
        // recyclerViewChats.layoutManager= LinearLayoutManager(activity)
        initAssignment(view)
    }
    private fun initAssignment(view:View) {
        dialog= Dialog(view.context)
        showProgressBar(dialog)

                databaseReference.child(AssignmentCompleted::class.java.simpleName)
                    .child(UserInstance.getUserInstance()?.uid.toString())
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            assignments.clear()
                            snapshot.children.forEach { dbTeam ->

                                val assignment= dbTeam.getValue(AssignmentCompleted::class.java) as AssignmentCompleted

                                var date= assignment.date.toString().toLong()

                                //holder.view.date_message.text =SimpleDateFormat("dd/MM/yyyy - HH:mm:ss", Locale("es", "MX")).format(date)
                                val dateText = SimpleDateFormat("dd/MM/yyyy", Locale("es", "MX")).format(date)

                                assignment.date="Completed " +dateText.toString()

                                    assignments.add(assignment)
                            }

                            if (assignments?.size!! > 0 ) {
                                val recycler =view.findViewById<RecyclerView>(R.id.completedTasksRecyclerView)
                                recycler.adapter= AssignmentCompletedAdapter(assignments)
                                recycler.smoothScrollToPosition(assignments!!.size - 1)
                            }
                            hideProgressBar(dialog)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }


                    })

            }

}