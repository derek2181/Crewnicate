package com.example.piapoi.DAO

import com.example.piapoi.Helpers.UserInstance
import com.example.piapoi.Models.ChatPreview
import com.example.piapoi.Models.Message
import com.example.piapoi.Models.Post
import com.example.piapoi.Models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*

class PostDAO {

    companion object {
        private val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(Post::class.java.simpleName)

        // CRUD
        fun add(post: Post,career : String,postContext : String): Task<Void> {

            return databaseReference.child(career).child(postContext).push().setValue(post)
        }

        fun update(key: String, hashMap: HashMap<String, Any>): Task<Void>? {
            return databaseReference.child(key).updateChildren(hashMap)
        }

    }
}