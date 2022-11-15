package com.example.piapoi

import android.app.Dialog
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.piapoi.DAO.ChatDAO
import com.example.piapoi.DAO.UserDAO
import com.example.piapoi.Helpers.UserInstance
import com.example.piapoi.Helpers.hideProgressBar
import com.example.piapoi.Helpers.showProgressBar
import com.example.piapoi.Models.Message
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_message_image.*

class MessageImageActivity  :AppCompatActivity() {
    private lateinit var dialog : Dialog
    private var uid :String= ""
    private var chatName : String=""
    private var chatUID : String=""
    private lateinit var storageReference : StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_image)

        storageReference=FirebaseStorage.getInstance().getReference("Chat")
        getSupportActionBar()?.hide()
        val bundle = intent.extras
         chatName = bundle!!.getString("chatName").toString()
         uid = bundle!!.getString("uid").toString()
        chatUID=UserInstance.getUserInstance()?.uid.toString() + uid
        val arr = chatUID.toCharArray()
        chatUID= arr.sorted().joinToString("")

        val uriString=bundle!!.getString("uri").toString()
        val uri = Uri.parse(uriString)


        Glide.with(this).load(uri).into(messageImage)
        cancelImageMessage.setOnClickListener{
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("chatName",chatName);
            intent.putExtra("uid",uid);
            startActivity(intent)
        }
        sendImageMessage.setOnClickListener{

            uploadProfileImage(uri)
        }
    }
    private fun getFileExtension(uri : Uri) : String?{
        val cR: ContentResolver = contentResolver

        val mime : MimeTypeMap = MimeTypeMap.getSingleton()

        return mime.getExtensionFromMimeType(cR.getType(uri))
    }
    private fun uploadProfileImage(imageUri  :Uri){
        if(imageUri!=null){
            val messageCaption= messsageImageCaption.text.toString()
            dialog= Dialog(this@MessageImageActivity)
            showProgressBar(dialog)


            val fileReference =storageReference.child(System.currentTimeMillis().toString() + "." + getFileExtension(imageUri!!) )


            fileReference.putFile(imageUri!!).addOnProgressListener {

            }.addOnSuccessListener {

                it.storage.downloadUrl.addOnSuccessListener{

                    var message : Message = Message(messageCaption,
                        ServerValue.TIMESTAMP, UserInstance.getUserInstance()?.uid.toString(),"", it.toString())

                    ChatDAO.add(message,chatUID,uid).addOnCompleteListener {
                        hideProgressBar(dialog)

                        val intent = Intent(this, ChatActivity::class.java)
                        intent.putExtra("chatName",chatName);
                        intent.putExtra("uid",uid);
                        startActivity(intent)
                    }

                }
            }

        }

    }

}