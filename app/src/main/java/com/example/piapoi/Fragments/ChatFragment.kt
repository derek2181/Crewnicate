package com.example.piapoi.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.piapoi.Adapter.ChatPreviewAdapter
import com.example.piapoi.Helpers.UserInstance
import com.example.piapoi.Models.ChatPreview
import com.example.piapoi.R
import com.example.piapoi.SendNewMessageActivity
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_chat.*


class ChatFragment : Fragment() {

    private var fragmentName="CHAT"
    private val chatPreviewList = mutableListOf<ChatPreview>()
    val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    public fun getFragmentName(): String {
        return fragmentName;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager =  LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewChats.layoutManager = layoutManager;
        // recyclerViewChats.layoutManager= LinearLayoutManager(activity)
        recyclerViewChats.adapter= ChatPreviewAdapter(chatPreviewList)
        initChats(view)
        //val view: View = inflater!!.inflate(R.layout.fragment_chat, container, false)
       // val sendNewMessagebutton =view.findViewById<ImageButton>(R.id.sendNewMessage)

        val sendButton=view.findViewById<ImageButton>(R.id.sendNewMessage)
        sendButton.setOnClickListener { view ->

                val intent = Intent(activity, SendNewMessageActivity::class.java)
                activity?.startActivity(intent)

        }

    }

    private fun initChats(view: View) {
        databaseReference.child(ChatPreview::class.java.simpleName).child(UserInstance.getUserInstance()?.uid.toString()).addValueEventListener(object :
            ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                chatPreviewList?.clear()

                for (snap in snapshot.children) {

                    val currentPreview: ChatPreview = snap.getValue(ChatPreview::class.java) as ChatPreview

                    if(currentPreview.senderUid.equals(UserInstance.getUserInstance()?.uid.toString())){
                        val sufix :String = "You: "
                        currentPreview.message=sufix + currentPreview.message
                    }
                    chatPreviewList?.add(currentPreview)
                }
                if (chatPreviewList?.size!! > 0 ) {
                    val recycler= view.findViewById<RecyclerView>(R.id.recyclerViewChats)
                    recycler.adapter= ChatPreviewAdapter(chatPreviewList,)
                    recycler.smoothScrollToPosition(chatPreviewList!!.size - 1)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

}