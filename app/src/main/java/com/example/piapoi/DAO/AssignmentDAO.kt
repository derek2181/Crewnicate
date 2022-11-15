package com.example.piapoi.DAO

import com.example.piapoi.Helpers.UserInstance
import com.example.piapoi.Models.Assignment
import com.example.piapoi.Models.Post
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AssignmentDAO {
    companion object {
        private val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(Assignment::class.java.simpleName)

        // CRUD
        fun add(assignment: Assignment,teamKey : String): Task<Void> {
            return databaseReference.child(UserInstance.getUserInstance()?.career.toString()).child(teamKey).child(assignment.id.toString()).setValue(assignment)
        }

        fun update(key: String, hashMap: HashMap<String, Any>): Task<Void>? {
            return databaseReference.child(key).updateChildren(hashMap)
        }

    }
}