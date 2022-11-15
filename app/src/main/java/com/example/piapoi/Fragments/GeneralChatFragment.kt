package com.example.piapoi.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.piapoi.Adapter.PostAdapter
import com.example.piapoi.CreatePostActivity
import com.example.piapoi.Helpers.UserInstance
import com.example.piapoi.Models.ChatPreview
import com.example.piapoi.Models.Post

import com.example.piapoi.R
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_chat.*

import kotlinx.android.synthetic.main.fragment_general_chat.*


class GeneralChatFragment : Fragment() {
    private val postsList = mutableListOf<Post>()
    val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_general_chat, container, false)

        return v
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addNewPost.setOnClickListener {
            val intent = Intent(activity, CreatePostActivity::class.java)
            activity?.startActivity(intent)
        }
        var layoutManager = LinearLayoutManager(context)
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL)
        post_recycler_view.layoutManager = layoutManager
        //  contact_recyclerview.layoutManager= LinearLayoutManager(this)
        post_recycler_view.adapter = PostAdapter(postsList)
        initPosts(view)
    }


    private fun initPosts(view : View) {
        databaseReference.child(Post::class.java.simpleName)
            .child(UserInstance.getUserInstance()?.career.toString()).child("General")
            .addValueEventListener(object :
                ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    postsList?.clear()

                    for (snap in snapshot.children) {

                        val currentPost: Post = snap.getValue(Post::class.java) as Post
                        postsList?.add(currentPost)
                    }
                    if (postsList?.size!! > 0) {
                        view.findViewById<RecyclerView>(R.id.post_recycler_view).adapter = PostAdapter(postsList)
                        view.findViewById<RecyclerView>(R.id.post_recycler_view).smoothScrollToPosition(postsList!!.size - 1)
                    }
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
    }
}
