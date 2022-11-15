package com.example.piapoi.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.piapoi.ChatActivity
import com.example.piapoi.Helpers.Paths
import com.example.piapoi.Helpers.UserInstance
import com.example.piapoi.Helpers.hideProgressBar
import com.example.piapoi.Models.Contact
import com.example.piapoi.R
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.contact_select_recycler.view.*


class SelectMembersAdapter(val contacts : List<Contact>) : RecyclerView.Adapter<SelectMembersAdapter.MembersViewHolder>(){
    class MembersViewHolder(val view: View): RecyclerView.ViewHolder(view)
    var listString : String= ""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembersViewHolder {
        return MembersViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.contact_select_recycler,parent,false)
        );
    }


    fun returnSelectedItems() : MutableList<Contact>{
        val selectedItems = mutableListOf<Contact>()
        for(contact in contacts){

            if(contact.isSelected){
                selectedItems.add(contact)
            }
        }
        return selectedItems
    }
    override fun onBindViewHolder(holder: MembersViewHolder, position: Int) {
       val contact : Contact=contacts[position]


        holder.view.setOnClickListener{
            contacts[position].isSelected=! contacts[position].isSelected

            if(!contacts[position].isSelected){
                holder.view.circleSelected.setBackgroundResource(R.drawable.ic_circle_state)
            }else{
                holder.view.circleSelected.setBackgroundResource(R.drawable.ic_circle_state_active)
            }


        }


        holder.view.contactName.text= contact.name

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