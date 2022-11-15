package com.example.piapoi

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location

import android.media.MediaRecorder
import android.net.Uri

import android.os.Bundle

import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.piapoi.Adapter.MessagesAdapter
import com.example.piapoi.DAO.ChatDAO
import com.example.piapoi.Helpers.UserInstance
import com.example.piapoi.Helpers.hideProgressBar
import com.example.piapoi.Helpers.showProgressBar
import com.example.piapoi.Models.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.message_bottom_sheet_dialog.*
import java.io.File
import android.view.MotionEvent
import android.view.View.OnTouchListener
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.piapoi.DAO.CallDAO
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.location.*

import com.google.firebase.storage.StorageReference
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_team.*
import org.json.JSONObject
import java.lang.Exception


class ChatActivity : AppCompatActivity() ,  GoogleApiClient.ConnectionCallbacks ,GoogleApiClient.OnConnectionFailedListener {
    private var uid :String=""
    private var name : String= ""
    private lateinit var dialog : Dialog
    private var recorder: MediaRecorder? = null
    private lateinit var currentLocation : Location
    private lateinit var fusedLocationProvider : FusedLocationProviderClient
    private val permissionCode=101


    private var audioName :  String?=null

    private val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().reference
    private lateinit var storageReference : StorageReference;
    private val messageList = mutableListOf<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        getSupportActionBar()?.hide()

      storageReference=  FirebaseStorage.getInstance().reference

        fusedLocationProvider=LocationServices.getFusedLocationProviderClient(this)



        val layoutManager =  LinearLayoutManager(this);
        val optionsButton=findViewById<ImageButton>(R.id.options_files_chat)
        layoutManager.orientation = LinearLayoutManager.VERTICAL;
        messages_recyclerview.layoutManager = layoutManager;
        // recyclerViewChats.layoutManager= LinearLayoutManager(activity)
        messages_recyclerview.adapter= MessagesAdapter(messageList, name, uid)

        val chatName=findViewById<TextView>(R.id.chatUser)
        val bundle = intent.extras
        uid = bundle!!.getString("uid").toString();
        name = bundle!!.getString("chatName").toString()
        chatName.text=name
        var chatUID : String=UserInstance.getUserInstance()?.uid.toString() + uid
        val arr = chatUID.toCharArray()
        chatUID= arr.sorted().joinToString("")


        databaseReference.child(Message::class.java.simpleName).child(chatUID).child("Thread").keepSynced(true)

        initMessages(chatUID)

        audioName = "${externalCacheDir?.absolutePath}/${System.currentTimeMillis().toString()}.mp3";



        sendMessageBtn.setOnClickListener{
            val message=SendTextChat.text.toString()
            SendTextChat.setText("")
            if(!message.trim().equals("")){

                var messageMessage : com.example.piapoi.Models.Message = Message(message,
                    ServerValue.TIMESTAMP,UserInstance.getUserInstance()?.uid.toString())
                if (uid != null) {
                    ChatDAO.add(messageMessage,chatUID,uid)
                }
            }

        }
        val micBtn=findViewById<ImageButton>(R.id.recordAudioBtn)
        micBtn.setOnTouchListener(object : OnTouchListener {

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN ->{
                        micBtn.setBackgroundResource(R.drawable.ic_mic_chat_red)
                        Toast.makeText(this@ChatActivity,"Recording audio...",Toast.LENGTH_SHORT).show()
                        if(ActivityCompat.checkSelfPermission(this@ChatActivity, Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED)
                            ActivityCompat.requestPermissions(this@ChatActivity,arrayOf(Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE),111)
                        startRecording()
                    }

                    MotionEvent.ACTION_UP->{
                        Toast.makeText(this@ChatActivity,"Audio sent",Toast.LENGTH_SHORT).show()
                       stopRecording()
                        micBtn.setBackgroundResource(R.drawable.ic_mic_chat)
                    }
                }

                return v?.onTouchEvent(event) ?: true
            }
        })

        val backBtn=findViewById<ImageButton>(R.id.backChat)

        backBtn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)

            this?.startActivity(intent)
        }
        optionsButton.setOnClickListener { view ->

            val view : View=layoutInflater.inflate(R.layout.message_bottom_sheet_dialog,null)
            val attachPictureBtn =view.findViewById<LinearLayout>(R.id.attachPicture)

            attachPictureBtn.setOnClickListener(){
                openImageChooser()
            }
            val attachFileBtn=view.findViewById<LinearLayout>(R.id.attachFile)

            attachFileBtn.setOnClickListener{
                openFileChooser()
            }
            val sendLocationBtn=view.findViewById<LinearLayout>(R.id.sendLocation)
            sendLocationBtn.setOnClickListener{
                fetchLocation()
            }
            val dialog=BottomSheetDialog(this)
            dialog.setContentView(view)
            dialog.show()
        }



        databaseReference.child("Call").child(UserInstance.getUserInstance()?.uid.toString()).addValueEventListener(object : ValueEventListener{

           // override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
          //  }


            override fun onDataChange(snapshot: DataSnapshot) {
                try{

                  //  Toast.makeText(this@ChatActivity,"onChildAdded", Toast.LENGTH_SHORT).show()

                    val call =snapshot.getValue(Call::class.java) as Call
                    if(call !=null && call.callStatus.equals("")){

                    val intent = Intent(this@ChatActivity,IncomingCallActivity::class.java )
                    intent.putExtra("callerName",call.callerName)
                    intent.putExtra("callerCareer",call.callerCareer)
                    intent.putExtra("callerImage",call.callerImage)
                    intent.putExtra("currentCallerUid",call.currentCallerUid)

                    startActivity(intent)
                        finish()
                    }
                }catch (e : Exception){
                //    Toast.makeText(this@ChatActivity,"onChildAdded exception", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }


        })


        callButton.setOnClickListener{
            if(ActivityCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE)!=PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.CALL_PHONE),1)
            }else{

                databaseReference.child("Call").child(uid).addListenerForSingleValueEvent(object : ValueEventListener{

                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {

                        if(!snapshot.exists()){
                            val call = Call(UserInstance.getUserInstance()?.imagePath.toString(),UserInstance.getUserInstance()?.name.toString()+ " " + UserInstance.getUserInstance()?.lastName.toString()
                                ,UserInstance.getUserInstance()?.uid.toString(),UserInstance.getUserInstance()?.career.toString())
                            CallDAO.add(call,uid)
                            databaseReference.child("User").child(uid).addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {

                                    var user : User = snapshot.getValue(User::class.java) as User

                                    val intent = Intent(this@ChatActivity,CallingActivity::class.java)
                                    intent.putExtra("callerName",user.name + " "+user.lastName)
                                    intent.putExtra("callerCareer",user.career)
                                    intent.putExtra("callerImage",user.imagePath)
                                    intent.putExtra("currentCallerUid",snapshot.key.toString())
                                    startActivity(intent)
                                    finish()
                                }

                                override fun onCancelled(error: DatabaseError) {

                                }

                            })
                        }else{
                            Toast.makeText(this@ChatActivity,"The user is currently in a call",Toast.LENGTH_SHORT).show()
                        }
                    }
                })




            }
        }
    }
    private fun startCall(){

    }
    private fun fetchLocation() {
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),permissionCode)
            return
        }
        val task=fusedLocationProvider.lastLocation.addOnSuccessListener{location->

                if(location!=null){
                    dialog = Dialog(this@ChatActivity)
                    showProgressBar(dialog)
                    currentLocation=location
                    Toast.makeText(this,currentLocation.latitude.toString()+ " " + currentLocation.longitude.toString(),Toast.LENGTH_LONG).show()

                    var message : Message = Message("",
                        ServerValue.TIMESTAMP, UserInstance.getUserInstance()?.uid.toString(),"", "","","",currentLocation.latitude.toString(),
                    currentLocation.longitude.toString())
                    var chatUID : String=UserInstance.getUserInstance()?.uid.toString() + uid
                    val arr = chatUID.toCharArray()
                    chatUID= arr.sorted().joinToString("")

                    ChatDAO.add(message,chatUID,uid).addOnCompleteListener {
                        hideProgressBar(dialog)
                    }
                }else{
                    Toast.makeText(this,"RIP",Toast.LENGTH_LONG).show()
                }

        }
    }


    //TODO arreglar esta cagada porque felix me corta los huevos
    private fun startRecording() {
        recorder = MediaRecorder()
        recorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder!!.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
        recorder!!.setOutputFile(audioName)
        recorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        recorder!!.prepare()
        recorder!!.start()

    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        //val fileUri = Uri.fromFile(File(audioName))
      //  val audioUri=Uri.parse(message?.audioPath)
        //val mediaPlayer= MediaPlayer.create(this,fileUri)

       // mediaPlayer.start()
        uploadAudio()
    }

    private fun uploadAudio() {
        dialog = Dialog(this@ChatActivity)
        showProgressBar(dialog)
        val fileUri = Uri.fromFile(File(audioName))
        val reference=storageReference.child("Chat").child("Audio").child(UserInstance.getUserInstance()?.uid.toString())
            .child(System.currentTimeMillis().toString() + ".mp3" )

        reference.putFile(fileUri).addOnSuccessListener {

            it.storage.downloadUrl.addOnSuccessListener{

                var message : Message = Message("",
                    ServerValue.TIMESTAMP, UserInstance.getUserInstance()?.uid.toString(),it.toString(), "","","")
                var chatUID : String=UserInstance.getUserInstance()?.uid.toString() + uid
                val arr = chatUID.toCharArray()
                chatUID= arr.sorted().joinToString("")

                ChatDAO.add(message,chatUID,uid).addOnCompleteListener {
                    hideProgressBar(dialog)
                }

            }

        }

    }

    private fun initMessages(chatUID : String){

        databaseReference.child(Message::class.java.simpleName).child(chatUID).child("Thread").addValueEventListener(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {

                messageList?.clear()

                for (snap in snapshot.children) {

                    val currentMessage: Message = snap.getValue(Message::class.java) as Message

                    messageList?.add(currentMessage)
                }
                if (messageList?.size!! > 0 ) {
                    messages_recyclerview.adapter= MessagesAdapter(messageList, name,uid)
                    messages_recyclerview.smoothScrollToPosition(messageList!!.size - 1)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if( resultCode== RESULT_OK
            && data!=null && data.data!=null){
            var fileUri : Uri = data.data!!


            if(requestCode==1){
                val intent = Intent(this, MessageImageActivity::class.java)
                intent.putExtra("uid",uid)
                intent.putExtra("uri",fileUri.toString())
                intent.putExtra("chatName",name)
                this?.startActivity(intent)
            }

            if(requestCode==2){
              //  Toast.makeText(this,"I work",Toast.LENGTH_SHORT).show()
                uploadChatFile(fileUri)
            }
          //  uploadProfileImage()
        }
    }

    private fun openImageChooser() {
        var intent : Intent = Intent()

        intent.setType("image/*")

        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent,1)
    }
    private fun openFileChooser() {
        var intent : Intent = Intent()

        intent.setType("*/*")
        intent.putExtra("File",0)
        intent.setAction(Intent.ACTION_GET_CONTENT)

        startActivityForResult(intent,2)
    }

    private fun getFileExtension(uri : Uri) : String?{
        val cR: ContentResolver = contentResolver

        val mime : MimeTypeMap = MimeTypeMap.getSingleton()

        return mime.getExtensionFromMimeType(cR.getType(uri))
    }
    private fun uploadChatFile(fileUri : Uri){
        if(fileUri!=null) {

            val fileName=fileUri!!.path.toString().substring(fileUri!!.path.toString().lastIndexOf("/")+1) ;
            val reference = FirebaseStorage.getInstance().getReference("Chat")
            dialog = Dialog(this@ChatActivity)
            showProgressBar(dialog)

            var chatUID : String=UserInstance.getUserInstance()?.uid.toString() + uid
            val arr = chatUID.toCharArray()
            chatUID= arr.sorted().joinToString("")

            val fileReference =reference.child(UserInstance.getUserInstance()?.uid.toString()).child("Files").child(System.currentTimeMillis().toString() + "." + getFileExtension(fileUri!!) )

            fileReference.putFile(fileUri!!).addOnProgressListener {

            }.addOnSuccessListener {

                it.storage.downloadUrl.addOnSuccessListener{

                    var message : Message = Message("",
                        ServerValue.TIMESTAMP, UserInstance.getUserInstance()?.uid.toString(),"", "",it.toString(),fileName)

                    ChatDAO.add(message,chatUID,uid).addOnCompleteListener {
                        hideProgressBar(dialog)
                    }

                }
            }
        }
    }

    override fun onConnected(p0: Bundle?) {
        Toast.makeText(this@ChatActivity,"win",Toast.LENGTH_LONG).show()

    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

}