package com.example.piapoi.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.piapoi.ChatActivity
import com.example.piapoi.Helpers.Paths
import com.example.piapoi.Helpers.hideProgressBar
import com.example.piapoi.Models.Contact
import com.example.piapoi.R
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.contact_recycler.view.*
import kotlinx.android.synthetic.main.user_chat_recycler.view.*


class ContactsAdapter(val contacts : List<Contact>) : RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder>(){
    class ContactsViewHolder(val view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        return ContactsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.contact_recycler,parent,false)
        );
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        val contact : Contact =contacts[position]
        //Aqui falta la imagen no he puesto imagenes dinamicas en ningun adapter\
        holder.view.setOnClickListener{
            val intent = Intent(holder.view.context, ChatActivity::class.java)

            intent.putExtra("chatName",contact.name +" "+ contact.lastName);
            intent.putExtra("uid",contact.uid);
            holder.view.context?.startActivity(intent)
        }
        holder.view.contactName.text= contact.name+ " " +contact.lastName


        if(contact.tasksCompleted.toInt()<=5){
            holder.view.contactBadge.setBackgroundResource(R.drawable.bronze_badge)
        }else if(contact.tasksCompleted.toInt()>5 && contact.tasksCompleted.toInt()<=10 ){
            holder.view.contactBadge.setBackgroundResource(R.drawable.silver_badge)
        }else{
            holder.view.contactBadge.setBackgroundResource(R.drawable.gold_badge)
        }


        if(contact.active.equals("active")){

            holder.view.userCurrentState.setBackgroundResource(R.drawable.ic_circle_state_active)
        }else{
            holder.view.userCurrentState.setBackgroundResource(R.drawable.ic_circle_state)
        }

        if(contact.imagePath.equals("Default")){
            Glide.with(holder.view.context)
                .load(Paths.defaultImagePath)
                .into(holder.view.contactImage)
        }else{
            FirebaseStorage.getInstance().getReference(contact.imagePath).downloadUrl.addOnSuccessListener {
                Glide.with(holder.view.context)
                    .load( it.toString())
                    .into(holder.view.contactImage)
            }.addOnCompleteListener{


            }
        }


       // holder.view.userNameChat.text=contact.userName

    }

    override fun getItemCount()= contacts.size
}