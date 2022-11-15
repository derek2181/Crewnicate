package com.example.piapoi

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.example.piapoi.Adapter.SelectMembersAdapter
import com.example.piapoi.DAO.TeamsDAO
import com.example.piapoi.Helpers.UserInstance
import com.example.piapoi.Helpers.hideProgressBar
import com.example.piapoi.Helpers.showProgressBar
import com.example.piapoi.Models.Contact
import com.example.piapoi.Models.Team
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_create_group.*
import kotlinx.android.synthetic.main.activity_send_new_message.*
import kotlinx.android.synthetic.main.fragment_group_members.*

class CreateTeamActivity : AppCompatActivity() {
    private lateinit var dialog : Dialog
    val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().reference
    var userTasksCompleted : String = ""
    var contactList : ArrayList<Contact>? = ArrayList();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)
        getSupportActionBar()?.hide()
        val backButton=findViewById<ImageButton>(R.id.backCreateGroup)
        val bundle = intent.extras

        dialog= Dialog(this@CreateTeamActivity)
        var layoutManager =  LinearLayoutManager(this)
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL)
        select_members_recycler.layoutManager = layoutManager
        //  contact_recyclerview.layoutManager= LinearLayoutManager(this)
        initContacts()
        val validator : AwesomeValidation = AwesomeValidation(ValidationStyle.BASIC)
        validator.addValidation(this,R.id.groupNameView, RegexTemplate.NOT_EMPTY,R.string.empty_name)

        createGroupBtn.setOnClickListener{
            dialog= Dialog(this)
           var contactsSelected= SelectMembersAdapter(contactList!!).returnSelectedItems()




            if(validator.validate()){



            if(contactsSelected.size>0){

                showProgressBar(dialog)
                    val key=System.currentTimeMillis().toString()

                    val userContact=Contact(
                        UserInstance.getUserInstance()?.name.toString(),
                        UserInstance.getUserInstance()?.lastName.toString(),
                        UserInstance.getUserInstance()?.imagePath.toString(),
                        UserInstance.getUserInstance()?.uid.toString(),
                        "active",
                        UserInstance.getUserInstance()?.career.toString(),
                    true,
                        userTasksCompleted,
                        UserInstance.getUserInstance()?.email.toString())
                     contactsSelected.add(userContact)
                    val group= Team(groupNameView.text.toString(),contactsSelected)

                    TeamsDAO.add(key,group).addOnSuccessListener {
                    hideProgressBar(dialog)
                        val intent = Intent(this,TeamActivity::class.java)
                        intent.putExtra("teamUid",group.name+"-"+key)
                        startActivity(intent)

                    }
            }else{
                Toast.makeText(this,"Agrega al menos un usuario",Toast.LENGTH_SHORT).show()
            }


            }
        }

        backButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)

            this?.startActivity(intent)
        }
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
                val activeUid : String = UserInstance.getUserInstance()?.uid.toString()
                snapshot!!.children.forEach {
                    val contact :Contact? =  it.getValue(Contact::class.java) as Contact
                    if (contact != null) {

                        contact.active= it.child("active").getValue(String::class.java) as String
                        if(!contact.uid.equals(activeUid) && contact.career.equals(UserInstance.getUserInstance()?.career.toString())){
                            contactList?.add(contact)
                        }else if(contact.uid.equals(UserInstance.getUserInstance()?.uid.toString())){
                            userTasksCompleted=contact.tasksCompleted
                        }
                    }
                }
                select_members_recycler.adapter= SelectMembersAdapter(contactList!!)
                hideProgressBar(dialog)
            }
        }
        )
    }
}