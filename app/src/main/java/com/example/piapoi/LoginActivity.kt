package com.example.piapoi

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.example.piapoi.DAO.ContactDAO
import com.example.piapoi.Helpers.Encryption.decrypt
import com.example.piapoi.Models.User
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import com.example.piapoi.Helpers.UserInstance
import com.example.piapoi.Helpers.hideProgressBar
import com.example.piapoi.Helpers.showProgressBar
import com.example.piapoi.Models.Login
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity: AppCompatActivity() {
    private lateinit var dialog : Dialog
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        getSupportActionBar()?.hide()

        val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().reference

        try {
            Firebase.database.setPersistenceEnabled(true)
            databaseReference.keepSynced(true)

        }catch (e :Exception){

        }


        val validator : AwesomeValidation = AwesomeValidation(ValidationStyle.BASIC)
        validator.addValidation(this,R.id.inputPasswordLogin, RegexTemplate.NOT_EMPTY,R.string.empty_name)
        validator.addValidation(this,R.id.inputEmailLogin, RegexTemplate.NOT_EMPTY,R.string.empty_name)

        signUpActivity.setOnClickListener{
            val intent= Intent(this@LoginActivity,RegisterActivity::class.java)
            startActivity(intent)
        }
        //var loginButton= findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener{

            if(validator.validate()){
                dialog = Dialog(this@LoginActivity)
                showProgressBar(dialog)
                databaseReference.child(Login::class.java.simpleName).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach{user->
                            if(user.child("email").value?.equals(inputEmailLogin.text.toString()) == true){

                                var encodedPassword=""
                                if(user.child("encrypted").value?.toString().equals("activated")){
                                    encodedPassword= decrypt( user.child("password").value?.toString()!!)!!
                                }


                                if(user.child("password").value?.equals(inputPasswordLogin.text.toString()) == true || encodedPassword.equals(inputPasswordLogin.text.toString())   ){

                                    //Toast.makeText(this@LoginActivity, "Correct credentials", Toast.LENGTH_LONG).show()
                                    var activeUser : User? = null;
                                    val matchUserReference =databaseReference.child(User::class.java.simpleName).child(user.key.toString()).addListenerForSingleValueEvent(object: ValueEventListener{

                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                            var activeUser : User? = dataSnapshot.getValue(User::class.java)

                                            val hashMap : HashMap<String,Any> = HashMap()

                                            hashMap.put("active","active")

                                            activeUser?.uid=dataSnapshot.key.toString()
                                            if (activeUser != null) {
                                                UserInstance.setUserInstance(activeUser)
                                            }

                                            ContactDAO.update(user.key.toString(),hashMap)?.addOnCompleteListener {
                                                val intent= Intent(this@LoginActivity,MainActivity::class.java)

                                                startActivity(intent)
                                                hideProgressBar(dialog)

                                                finish()
                                            }

                                        }
                                        override fun onCancelled(databaseError: DatabaseError) {
                                            // handle error
                                        }

                                    });
                                    return
                                }else{
                                    inputPasswordLogin.setError("The password you entered is incorrect")
                                    hideProgressBar(dialog)
                                    return
                                }
                            }
                        }
                       inputEmailLogin.setError("The email that you entered does not exist")
                        hideProgressBar(dialog)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@LoginActivity, error.message, Toast.LENGTH_LONG).show()
                    }

                })
            }

        }


    }

}