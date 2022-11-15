package com.example.piapoi

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.piapoi.DAO.CallDAO
import com.example.piapoi.Helpers.Paths
import com.example.piapoi.Helpers.UserInstance
import com.example.piapoi.Helpers.hideProgressBar
import com.example.piapoi.Helpers.showProgressBar
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_calling.*

import kotlinx.android.synthetic.main.activity_main.*

class CallingActivity: AppCompatActivity() {
    private var name :String=""
    private var career : String= ""
    private var callerImage : String= ""
    private var currentCallerUid : String= ""
    private lateinit var dialog : Dialog
    private val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().reference
    private var didUserCancelCall: Boolean = false
    private var childRemovedOnce : Boolean = true
    private var onChildChanged: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calling)
        getSupportActionBar()?.hide()

        dialog = Dialog(this@CallingActivity)
        showProgressBar(dialog)

        val bundle = intent.extras
        name = bundle!!.getString("callerName").toString();
        career = bundle!!.getString("callerCareer").toString()
        callerImage = bundle!!.getString("callerImage").toString()
        currentCallerUid = bundle!!.getString("currentCallerUid").toString()

        callerName.text=name
        careerName.text=career
        //val cancelBtn=findViewById<ImageButton>(R.id.cancelCall)
        cancelCall.setOnClickListener{
            didUserCancelCall=true

            CallDAO.remove(currentCallerUid)?.addOnSuccessListener {
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
            }
        }

        databaseReference.child("Call").child(currentCallerUid).addChildEventListener(object :
            ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                if(onChildChanged){
                    onChildChanged=false
                        //doSomethingHere()
                        val intent = Intent(this@CallingActivity,CallActivity::class.java)
                        intent.putExtra("callID",currentCallerUid)
                        startActivity(intent)
                        finish()
                }



            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                if(!didUserCancelCall){

                    if(childRemovedOnce){

                        

                Toast.makeText(this@CallingActivity,name +" "+ "ha cancelado la llamada",
                    Toast.LENGTH_SHORT).show()
                childRemovedOnce=false

                val intent = Intent(this@CallingActivity,MainActivity::class.java)
                startActivity(intent)

                finish()
                    }
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })
        if(callerImage.equals("Default")){
            Glide.with(this).load(Paths.defaultImagePath).into(callerImageView)
        }else{
            FirebaseStorage.getInstance().getReference(callerImage).downloadUrl.addOnSuccessListener {
                Glide.with(this)
                    .load( it.toString())
                    .into(callerImageView)
            }.addOnCompleteListener{

                hideProgressBar(dialog)
            }
        }

    }
}