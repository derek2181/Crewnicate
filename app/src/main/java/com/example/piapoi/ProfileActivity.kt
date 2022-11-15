package com.example.piapoi

import android.app.Dialog
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.bumptech.glide.Glide.with
import com.example.piapoi.DAO.ContactDAO
import com.example.piapoi.DAO.UserDAO
import com.example.piapoi.Helpers.*
import com.example.piapoi.Helpers.Encryption.decrypt
import com.example.piapoi.Helpers.Encryption.encrypt

import com.example.piapoi.Helpers.UserInstance
import com.example.piapoi.Models.Contact
import com.example.piapoi.Models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_profile.*
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;





class ProfileActivity : AppCompatActivity() {
    //Data encryption
    private val SALT_BYTES: Int = 8
    private val PBK_ITERATIONS = 1000
    private val ENCRYPTION_ALGORITHM = "AES/CBC/PKCS5Padding"
    private val PBE_ALGORITHM = "PBEwithSHA256and128BITAES-CBC-BC"


    private lateinit var storageReference : StorageReference
    private var imageUri : Uri? = null;
    private lateinit var dialog : Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        getSupportActionBar()?.hide()
        storageReference=FirebaseStorage.getInstance().getReference("User")

        logOutButton.setOnClickListener{

        dialog= Dialog(this)
        showProgressBar(dialog)
            val hashMap : HashMap<String,Any> = HashMap()
            hashMap.put("active","offline")

            ContactDAO.update(UserInstance.getUserInstance()?.uid.toString(),hashMap)?.addOnSuccessListener {
                UserInstance.deleteUserInstance()
                val intent = Intent(this, LoginActivity::class.java)
                hideProgressBar(dialog)
                startActivity(intent)
                finish()

            }

        }

        if(UserInstance.getUserInstance()?.encrypted.toString().equals("activated")){

            encryptPasswordSwitch.isChecked=true

        }else{
            encryptPasswordSwitch.isChecked=false
        }


        ContactDAO.get(UserInstance.getUserInstance()?.uid.toString()).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                statusSwitch.isChecked = snapshot.child("active").value.toString().equals("active")

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })
        statusSwitch.setOnClickListener{
            if(statusSwitch.isChecked){
                val hashMap :HashMap<String, Any> = HashMap()

                hashMap.put("active","active")
               ContactDAO.update(UserInstance.getUserInstance()?.uid.toString(),hashMap)
            }else{
                val hashMap :HashMap<String, Any> = HashMap()

                hashMap.put("active","offline")
                ContactDAO.update(UserInstance.getUserInstance()?.uid.toString(),hashMap)
            }
        }
        encryptPasswordSwitch.setOnClickListener{

            dialog=Dialog(this)
            showProgressBar(dialog)
            if(encryptPasswordSwitch.isChecked){

               val hashMap :HashMap<String, Any> = HashMap()

               val encodedPassword=UserInstance.getUserInstance()?.password.toString()

                val encrypt=encrypt(encodedPassword)!!
               hashMap.put("password",encrypt)
                hashMap.put("encrypted","activated")
                UserInstance.getUserInstance()?.password=encrypt
                UserInstance.getUserInstance()?.encrypted="activated"
               UserDAO.update(UserInstance.getUserInstance()?.uid.toString(),hashMap)?.addOnSuccessListener {
                   hideProgressBar(dialog)
               }
            }else{

                val decodedPassword=UserInstance.getUserInstance()?.password.toString()

                val decryptedPassword= decrypt(decodedPassword)!!
                val hashMap :HashMap<String, Any> = HashMap()
                hashMap.put("password",decryptedPassword)
                hashMap.put("encrypted","deactivated")
                UserInstance.getUserInstance()?.encrypted="deactivated"
                UserInstance.getUserInstance()?.password=decryptedPassword
                UserDAO.update(UserInstance.getUserInstance()?.uid.toString(),hashMap)?.addOnSuccessListener {
                    hideProgressBar(dialog)
                }
            }
        }
        backProfileButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        var user : User? = UserInstance.getUserInstance()



        if (user != null) {
            userNameProfile.setText(user.name)
            lastNameProfile.setText(user.lastName)



            if(encryptPasswordSwitch.isChecked){
                userPasswordProfile.setText(decrypt(user.password))
            }else{
                userPasswordProfile.setText(user.password)
            }


            if(user.imagePath.equals("Default")) {

                val completeReference=FirebaseStorage.getInstance().getReference("User/user.png")
                    with(this )
                    .load(Paths.defaultImagePath)
                    .into(profileImageView)

            }else{
                dialog= Dialog(this)
                showProgressBar(dialog)
                FirebaseStorage.getInstance().getReference(user.imagePath.toString()).downloadUrl.addOnSuccessListener {
                    with(this )
                        .load( it.toString())
                        .into(profileImageView)
                }.addOnCompleteListener{
                    hideProgressBar(dialog)
                }

                //val imageUri=Uri.parse(downloadURL)

               // profileImageView.setImageURI(imageUri)
            }

        }
        val validator : AwesomeValidation = AwesomeValidation(ValidationStyle.BASIC)
        validator.addValidation(this,R.id.userNameProfile, RegexTemplate.NOT_EMPTY,R.string.empty_name)
        validator.addValidation(this,R.id.lastNameProfile, RegexTemplate.NOT_EMPTY,R.string.empty_name)
        validator.addValidation(this,R.id.userPasswordProfile,".{6,}",R.string.invalid_password)
        fileChooserButton.setOnClickListener{
            openFileChooser()
        }
        //editImageButton.setOnClickListener{

       //     uploadProfileImage()
       // }


        applyChangesProfile.setOnClickListener{
            dialog= Dialog(this)
            showProgressBar(dialog)

            if(validator.validate()){
                val userMap: HashMap<String, Any> = HashMap()
                userMap.put("name",userNameProfile.text.toString())
                userMap.put("lastName",lastNameProfile.text.toString())

                var encryptedPassword= ""
                if(encryptPasswordSwitch.isChecked){
                    encryptedPassword=encrypt(userPasswordProfile.text.toString())!!
                }else{
                    encryptedPassword=userPasswordProfile.text.toString()
                }


                userMap.put("password",encryptedPassword)

                if (user != null) {
                    UserDAO.update(user.uid.toString(),userMap)
                    UserInstance.updateInstance(userMap)
                }
            }
        hideProgressBar(dialog)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==1 && resultCode== RESULT_OK
            && data!=null && data.data!=null){
            imageUri= data.data!!

            //Picasso.with(this).load(imageUri).into(profileImageView)
            profileImageView.setImageURI(imageUri)
            uploadProfileImage()
        }
    }
    private fun openFileChooser() {
        var intent : Intent = Intent()

        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent,1)
    }


    private fun getFileExtension(uri : Uri) : String?{
        val cR: ContentResolver = contentResolver

        val mime : MimeTypeMap =MimeTypeMap.getSingleton()

        return mime.getExtensionFromMimeType(cR.getType(uri))
    }
    private fun uploadProfileImage(){
        if(imageUri!=null){

            dialog= Dialog(this@ProfileActivity)
            showProgressBar(dialog)

            if(!UserInstance.getUserInstance()?.imagePath.equals("Default")) {
                var userImagePathReference = FirebaseStorage.getInstance().getReference(UserInstance.getUserInstance()?.imagePath.toString())

                userImagePathReference.delete()
            }

            val fileReference =storageReference.child(UserInstance.getUserInstance()?.uid.toString()
                    + "." + getFileExtension(imageUri!!))
            fileReference.putFile(imageUri!!).addOnProgressListener {

            }.addOnSuccessListener {


                val imagePath= it.metadata?.path.toString()
                val userMap: HashMap<String, Any> = HashMap()
                userMap.put("imagePath",imagePath.toString())
                UserDAO.update(UserInstance.getUserInstance()?.uid.toString(),userMap)
                    ?.addOnCompleteListener {
                        hideProgressBar(dialog)
                    }
                UserInstance.getUserInstance()?.imagePath =imagePath.toString()

            }

        }else{
            Toast.makeText(this@ProfileActivity,"No haz seleccionado una imagen de tu galeria",Toast.LENGTH_SHORT)
        }

    }



}