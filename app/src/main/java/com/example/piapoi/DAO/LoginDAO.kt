package com.example.piapoi.DAO

import com.example.piapoi.Models.Login
import com.example.piapoi.Models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginDAO {
    companion  object {
        private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference(
            Login::class.java.simpleName)

        // CRUD
        fun add(user : Login) : Task<Void> {
            return databaseReference.child(user.uid).setValue(user)
        }
        fun update(key: String, hashMap: HashMap<String, Any>): Task<Void>? {
            return  databaseReference.child(key).updateChildren(hashMap)
        }

        fun remove(key: String): Task<Void>? {
            return  databaseReference.child(key).removeValue()
        }
    }
}