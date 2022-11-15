package com.example.piapoi.DAO

import com.example.piapoi.Models.Contact
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*

class ContactDAO {
    companion object {
        private val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(
                Contact::class.java.simpleName)

        // CRUD
        fun add(user: Contact): Task<Void> {
            return databaseReference.child(user.uid).setValue(user)
        }

        fun update(key: String, hashMap: HashMap<String, Any>): Task<Void>? {
            return databaseReference.child(key).updateChildren(hashMap)
        }
        fun get(key  :String): DatabaseReference {
            return databaseReference.child(key)
        }
        fun remove(key: String): Task<Void>? {
            return databaseReference.child(key).removeValue()
        }
    }
}