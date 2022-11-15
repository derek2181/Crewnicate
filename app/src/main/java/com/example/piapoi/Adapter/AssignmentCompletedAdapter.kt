package com.example.piapoi.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.piapoi.DeliverTaskActivity

import com.example.piapoi.Models.AssignmentCompleted
import com.example.piapoi.R
import kotlinx.android.synthetic.main.assignment_recycler.view.*

class AssignmentCompletedAdapter(val assignments : List<AssignmentCompleted>) : RecyclerView.Adapter<AssignmentCompletedAdapter.AssignmentsViewHolder>(){
    class AssignmentsViewHolder(val view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentsViewHolder {
        return AssignmentsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.assignment_recycler,parent,false)
        );
    }
    //TODO :Hacer que se despliegue el evento onClick en el adapter, que me lleve a la tarea y que pueda picar en el boton entregar para ingresar a la tabla TasksCompleted
    override fun onBindViewHolder(holder: AssignmentsViewHolder, position: Int) {
        val assignment : AssignmentCompleted =assignments[position]

        holder.view.groupNameAssignment.text=assignment.team.split("-")[0]
        holder.view.assignmentNameAssignment.text=assignment.name
        holder.view.assignmentDate.text=assignment.date.toString()
    }

    override fun getItemCount()= assignments.size
}