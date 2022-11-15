package com.example.piapoi.DAO

import com.example.piapoi.Helpers.UserInstance
import com.example.piapoi.Models.ChatPreview
import com.example.piapoi.Models.Message
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ChatPreviewDAO {
    companion object {
        private val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(ChatPreview::class.java.simpleName)

        // CRUD
        fun add(preview: ChatPreview,userUid: String, chatUid : String): Task<Void> {
            return databaseReference.child(userUid).child(chatUid).setValue(preview)
        }

        fun update(key: String, hashMap: HashMap<String, Any>): Task<Void>? {
            return databaseReference.child(key).updateChildren(hashMap)
        }

        fun remove(key: String): Task<Void>? {
            return databaseReference.child(key).removeValue()
        }
    }
}