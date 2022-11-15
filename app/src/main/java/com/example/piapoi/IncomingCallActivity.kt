package com.example.piapoi

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.piapoi.DAO.CallDAO
import com.example.piapoi.Helpers.Paths
import com.example.piapoi.Helpers.UserInstance
import com.example.piapoi.Helpers.hideProgressBar
import com.example.piapoi.Helpers.showProgressBar
import com.example.piapoi.Models.User
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_incoming_call.*
import kotlinx.android.synthetic.main.activity_main.*

class IncomingCallActivity : AppCompatActivity() {
    private var name :String=""
    private var career : String= ""
    private var callerImage : String= ""
    private var currentCallerUid : String= ""
    private lateinit var dialog : Dialog
    private val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().reference
    private var didUserCancelCall: Boolean = false
    private var childRemovedOnce : Boolean = true
    private var childChangedOnce : Boolean=true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_call)
        getSupportActionBar()?.hide()

        dialog = Dialog(this@IncomingCallActivity)
        showProgressBar(dialog)

        val bundle = intent.extras
        name = bundle!!.getString("callerName").toString();
        career = bundle!!.getString("callerCareer").toString()
        callerImage = bundle!!.getString("callerImage").toString()
        currentCallerUid = bundle!!.getString("currentCallerUid").toString()

        callerName.text=name
        careerName.text=career
        declineCall.setOnClickListener{
            didUserCancelCall=true
            CallDAO.remove(UserInstance.getUserInstance()?.uid.toString())?.addOnSuccessListener {
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        acceptCall.setOnClickListener{
            val map: HashMap<String, Any> = HashMap()
            map.put("callStatus","Active")
            CallDAO.update(UserInstance.getUserInstance()?.uid.toString(),map)
        }


        databaseReference.child("Call").child(UserInstance.getUserInstance()?.uid.toString()).addChildEventListener(object :
            ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                if(childChangedOnce){

                    childChangedOnce=false
                    val intent = Intent(this@IncomingCallActivity,CallActivity::class.java)
                    intent.putExtra("callID",UserInstance.getUserInstance()?.uid.toString())

                    startActivity(intent)

                    finish()
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                if(!didUserCancelCall){

                if(childRemovedOnce){

                childRemovedOnce=false
                Toast.makeText(this@IncomingCallActivity,name +" "+ "ha cancelado la llamada",Toast.LENGTH_SHORT).show()
                val intent = Intent(this@IncomingCallActivity,MainActivity::class.java)
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


        //POner un listener por si el otro wey cancela la llamada
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