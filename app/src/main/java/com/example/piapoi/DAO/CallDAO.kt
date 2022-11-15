package com.example.piapoi.DAO

import com.example.piapoi.Models.Call

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CallDAO {
    companion object {
        private val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(
                Call::class.java.simpleName)

        // CRUD
        fun add(call: Call, uid : String): Task<Void> {
            return databaseReference.child(uid).setValue(call)
        }

        fun update(key: String, hashMap: HashMap<String, Any>): Task<Void>? {
            return databaseReference.child(key).updateChildren(hashMap)
        }

        fun remove(key: String): Task<Void>? {
            return databaseReference.child(key).removeValue()
        }
    }
}