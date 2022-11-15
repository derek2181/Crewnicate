package com.example.piapoi

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.example.piapoi.DAO.ContactDAO
import com.example.piapoi.DAO.LoginDAO
import com.example.piapoi.DAO.UserDAO
import com.example.piapoi.Helpers.UserInstance
import com.example.piapoi.Helpers.hideProgressBar
import com.example.piapoi.Helpers.showProgressBar
import com.example.piapoi.Models.Contact
import com.example.piapoi.Models.Login
import com.example.piapoi.Models.User
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*





class RegisterActivity: AppCompatActivity(), AdapterView.OnItemClickListener {
    private lateinit var dialog : Dialog

    private var itemSelected : String =""


    override fun onCreate(savedInstanceState: Bundle?) {
        val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().reference
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        getSupportActionBar()?.hide()

        val careers=initCareers()
        val validator : AwesomeValidation= AwesomeValidation(ValidationStyle.BASIC)



        validator.addValidation(this,R.id.inputNameRegister,RegexTemplate.NOT_EMPTY,R.string.empty_name)
        validator.addValidation(this,R.id.inputNameRegister,RegexTemplate.NOT_EMPTY,R.string.empty_name)
       validator.addValidation(this,R.id.inputLastnameRegister,RegexTemplate.NOT_EMPTY,R.string.empty_name)
        validator.addValidation(this,R.id.selectCareer,RegexTemplate.NOT_EMPTY,R.string.empty_name)

        validator.addValidation(this,R.id.inputEmailRegister, Patterns.EMAIL_ADDRESS,R.string.invalid_email)
        validator.addValidation(this,R.id.inputPasswordRegister,".{6,}",R.string.invalid_password)
       // validator.addValidation(this,R.id.inputPasswordRegister,"^(?=.[0-9])(?=.[a-z])(?=.*[A-Z]).{8,20}$",R.string.password_requirements)
        validator.addValidation(this,R.id.inputConfirmpasswordRegister,R.id.inputPasswordRegister,R.string.invalid_password)

        val adapter= ArrayAdapter(this,R.layout.career_names_select,careers)
        with(selectCareer){
            setAdapter(adapter)
            onItemClickListener=this@RegisterActivity
        }

        var btnRegister= findViewById<Button>(R.id.btnRegister)



        //var  userDAO : UserDAO = UserDAO();
        btnRegister.setOnClickListener{
            if(validator.validate()){
                dialog= Dialog(this@RegisterActivity)
                showProgressBar(dialog)
                databaseReference.child(User::class.java.simpleName).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if(isEmailAvailable(snapshot)){
                            val user = User(
                                inputEmailRegister.text.toString(),
                                inputPasswordRegister.text.toString(),
                                inputNameRegister.text.toString(),
                                inputLastnameRegister.text.toString(),
                                null,
                                "Default",
                                selectCareer.text.toString(),
                                "deactivated"
                            )


                            UserDAO.add(user)?.addOnSuccessListener {

                                databaseReference.child(User::class.java.simpleName).addListenerForSingleValueEvent(object :
                                    ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        snapshot.children.forEach{user->
                                            if(user.child("email").value?.equals(inputEmailRegister.text.toString()) == true){
                                                    var activeUser : User= User(user.child("email").value.toString(),
                                                        user.child("password").value.toString(),user.child("name").value.toString(),user.child("lastName").value.toString(),user.key.toString(),user.child("imagePath").value.toString(),
                                                    user.child("career").value.toString());
                                                    UserInstance.setUserInstance(activeUser)
                                                    val login = Login(inputEmailRegister.text.toString(), inputPasswordRegister.text.toString(),user.key.toString())
                                                var contact= Contact(inputNameRegister.text.toString(),inputLastnameRegister.text.toString(),"Default",user.key.toString(),"active",
                                                selectCareer.text.toString(),tasksCompleted = "0",email=activeUser.email.toString())
                                                    ContactDAO.add(contact)
                                                    LoginDAO.add(login).addOnCompleteListener{
                                                        val intent= Intent(this@RegisterActivity,MainActivity::class.java)
                                                        startActivity(intent)
                                                        hideProgressBar(dialog)
                                                    }

                                                    return
                                            }
                                        }

                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Toast.makeText(this@RegisterActivity, error.message, Toast.LENGTH_LONG).show()
                                    }

                                })
                                Toast.makeText(this@RegisterActivity, "Bienvenido", Toast.LENGTH_LONG).show()
                            }?.addOnFailureListener {
                                Toast.makeText(this@RegisterActivity, "Failure record inserted", Toast.LENGTH_LONG).show()
                            }
                        }else{
                            inputEmailRegister.setError("The provided email is already in use")
                        }


                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
            }
        }
    }

    private fun initCareers(): List<String> {
        return listOf(
            "LMAD",
            "LCC",
            "LSTI",
            "LA",
            "LM",
            "LF"
        )
    }

    private fun isEmailAvailable(snapshot: DataSnapshot): Boolean {
        snapshot.children.forEach{user->
            if(user.child("email").value ==inputEmailRegister.text.toString()){
                return false
            }
        }
        return true
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item= parent?.getItemAtPosition(position).toString();

    }
}