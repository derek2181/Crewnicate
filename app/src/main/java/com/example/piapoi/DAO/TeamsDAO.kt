package com.example.piapoi.DAO

import com.example.piapoi.Helpers.UserInstance
import com.example.piapoi.Models.Contact
import com.example.piapoi.Models.Team
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class TeamsDAO  {

    companion  object {
        private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference(
            Team::class.java.simpleName)

        // CRUD
        fun add(key : String,team : Team) : Task<Void> {
            return databaseReference.child(UserInstance.getUserInstance()?.career.toString()).child( team.name+"-"+key ).setValue(team.members)
        }
        fun update(key: String, hashMap: HashMap<String, Any>): Task<Void>? {

            return  databaseReference.child(key).updateChildren(hashMap)
        }

        fun remove(key: String): Task<Void>? {
            return  databaseReference.child(key).removeValue()
        }
    }

}