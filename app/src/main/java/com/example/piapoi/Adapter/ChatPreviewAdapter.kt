package com.example.piapoi.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.piapoi.ChatActivity
import com.example.piapoi.Helpers.Paths
import com.example.piapoi.Models.ChatPreview
import com.example.piapoi.R
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.user_chat_recycler.view.*

class ChatPreviewAdapter(val chats: MutableList<ChatPreview> ) : RecyclerView.Adapter<ChatPreviewAdapter.ChatsViewHolder>(){
    class ChatsViewHolder(val view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder {
       return ChatsViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.user_chat_recycler,parent,false)
       );
    }

    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
    val chatPreview : ChatPreview =chats[position]

        holder.view.messageChat.text= chatPreview.message
        holder.view.userNameChat.text=chatPreview.name

        if(chatPreview.imagePath.equals("Default")){
            Glide.with(holder.view.context)
                .load(Paths.defaultImagePath)
                .into(holder.view.user_image_messages)
        }else{
            FirebaseStorage.getInstance().getReference(chatPreview.imagePath).downloadUrl.addOnSuccessListener {
                Glide.with(holder.view.context)
                    .load( it.toString())
                    .into(holder.view.user_image_messages)
            }.addOnCompleteListener{


            }
        }


        holder.view.setOnClickListener{
            val intent = Intent(holder.view.context, ChatActivity::class.java)
            intent.putExtra("uid",chatPreview.userUid)
            intent.putExtra("chatName",chatPreview.name);
            holder.view.context?.startActivity(intent)
        }
    }

    override fun getItemCount()= chats.size



}