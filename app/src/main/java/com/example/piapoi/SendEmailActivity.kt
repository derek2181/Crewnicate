package com.example.piapoi

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.example.piapoi.Adapter.SelectEmailAdapter
import com.example.piapoi.Helpers.UserInstance
import com.example.piapoi.Models.Contact
import com.example.piapoi.Models.Team
import com.example.piapoi.Models.User
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_send_email.*

class SendEmailActivity : AppCompatActivity() {
    private var teamUid  :String = ""
    private lateinit var dialog : Dialog
    val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().reference
    var contactList : ArrayList<Contact>? = ArrayList();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_email)
        getSupportActionBar()?.hide()
        val bundle = intent.extras
        teamUid = bundle!!.getString("teamUid").toString()
        var layoutManager =  LinearLayoutManager(this)
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL)
        select_members_email_recycler.layoutManager = layoutManager
        val validator : AwesomeValidation = AwesomeValidation(ValidationStyle.BASIC)



        validator.addValidation(this,R.id.emailSubjectView, RegexTemplate.NOT_EMPTY,R.string.empty_name)

        validator.addValidation(this,R.id.emailContentView, RegexTemplate.NOT_EMPTY,R.string.empty_name)
        initMembers()



        sendEmailButton.setOnClickListener{
            val emailList=SelectEmailAdapter(contactList!!).returnSelectedItems()

            if(emailList.size>0){

                if(validator.validate()){
                    var emailString=""
                    for(email in emailList){
                        emailString+=email+","
                    }
                    emailString.dropLast(1)

                    sendEmail(emailSubjectView.text.toString(),emailContentView.text.toString(),emailString)
                    //Toast.makeText(this,emailString,Toast.LENGTH_SHORT).show()
                }


            }else{
                Toast.makeText(this,"Select at least one member",Toast.LENGTH_SHORT).show()
            }
        }

        backSendEmail.setOnClickListener{
            val intent= Intent(this,TeamActivity::class.java)
            intent.putExtra("teamUid",teamUid)
            startActivity(intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)





    }
    private fun sendEmail(subject: String, content: String, emails: String) {
        val mIntent=Intent(Intent.ACTION_SEND)

        mIntent.data= Uri.parse("mailto:")
        mIntent.type="text/plain"
        mIntent.putExtra(Intent.EXTRA_EMAIL,arrayOf(emails))
        mIntent.putExtra(Intent.EXTRA_SUBJECT,subject)
        mIntent.putExtra(Intent.EXTRA_TEXT,content)

        try{
            startActivity(Intent.createChooser(mIntent,"Choose email client: "))
        }catch (e: Exception){
            Toast.makeText(this,"One email may not exist",Toast.LENGTH_SHORT).show()
        }

    }

    private fun initMembers() {

        databaseReference.child(Team::class.java.simpleName).child(UserInstance.getUserInstance()?.career.toString()).child(teamUid).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { currentContact ->
                    val contact = currentContact.getValue(Contact::class.java) as Contact

                    databaseReference.child(User::class.java.simpleName).child(contact.uid).addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {

                            val contact=snapshot.getValue(Contact::class.java) as Contact
                            contactList?.add(contact)
                            val recycler=findViewById<RecyclerView>(R.id.select_members_email_recycler)
                            recycler.adapter= SelectEmailAdapter(contactList!!)
                        }
                        override fun onCancelled(error: DatabaseError) {

                        }
                    })

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}