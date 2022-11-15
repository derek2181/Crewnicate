package com.example.piapoi.DAO

import com.example.piapoi.Helpers.UserInstance
import com.example.piapoi.Models.Assignment
import com.example.piapoi.Models.AssignmentCompleted
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AssignmentCompletedDAO {

    companion object {
        private val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(AssignmentCompleted::class.java.simpleName)

        // CRUD
        fun add(assignment: AssignmentCompleted): Task<Void> {
            return databaseReference.child(UserInstance.getUserInstance()?.uid.toString()).child(assignment.id).setValue(assignment)
        }

        fun update(key: String, hashMap: HashMap<String, Any>): Task<Void>? {
            return databaseReference.child(key).updateChildren(hashMap)
        }

    }
}