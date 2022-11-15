package com.example.piapoi

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.piapoi.Adapter.ContactsAdapter
import com.example.piapoi.Helpers.UserInstance
import com.example.piapoi.Helpers.hideProgressBar
import com.example.piapoi.Helpers.showProgressBar

import com.example.piapoi.Models.*
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_send_new_message.*


class SendNewMessageActivity : AppCompatActivity() {
    private lateinit var dialog : Dialog
    val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().reference
    var contactList : ArrayList<Contact>? = ArrayList();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_new_message)

        getSupportActionBar()?.hide()

        dialog= Dialog(this@SendNewMessageActivity)
        var layoutManager =  LinearLayoutManager(this)
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL)
        contact_recyclerview.layoutManager = layoutManager
        //  contact_recyclerview.layoutManager= LinearLayoutManager(this)
        initContacts()

        SearchUserChatView.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val searchQuery  :String = SearchUserChatView.text.toString()
                getChatsBySearch(searchQuery)
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })


        backSendNewMessageButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)

            this?.startActivity(intent)
            finish()
        }

    }

    private fun getChatsBySearch(searchQuery: String) {

        dialog= Dialog(this@SendNewMessageActivity)
        showProgressBar(dialog)
        if (!searchQuery.equals("")){
            databaseReference.child(Contact::class.java.simpleName).orderByChild("name").startAt(searchQuery).endAt(searchQuery + "\uf8ff").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onCancelled(snapshotError: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val activeUid : String =UserInstance.getUserInstance()?.uid.toString()
                    contactList?.clear()
                    snapshot!!.children.forEach {
                        val contact :Contact? =  it.getValue(Contact::class.java)
                        if (contact != null) {

                            if(!contact.uid.equals(activeUid)){
                                contactList?.add(contact)
                            }

                        }
                    }
                    contact_recyclerview.adapter= ContactsAdapter(contactList!!)

                    hideProgressBar(dialog)
                }
            })
        }
        else{
            initContacts()

        }



    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {


        return super.onCreateView(name, context, attrs)
    }

    private fun initContacts() {
        databaseReference.child(Contact::class.java.simpleName).addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(snapshotError: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(contactList!=null){
                    contactList?.clear()
                }
                val activeUid : String =UserInstance.getUserInstance()?.uid.toString()
                snapshot!!.children.forEach {
                    val contact :Contact? =  it.getValue(Contact::class.java) as Contact
                    if (contact != null) {

                      contact.active= it.child("active").getValue(String::class.java) as String
                        if(!contact.uid.equals(activeUid)){
                            contactList?.add(contact)
                        }
                    }
                }
                contact_recyclerview.adapter= ContactsAdapter(contactList!!)
                hideProgressBar(dialog)
            }
            }
        )
    }
}