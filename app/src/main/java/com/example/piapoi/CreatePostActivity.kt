package com.example.piapoi

import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.bumptech.glide.Glide
import com.example.piapoi.DAO.PostDAO
import com.example.piapoi.Helpers.Paths
import com.example.piapoi.Helpers.UserInstance
import com.example.piapoi.Helpers.hideProgressBar
import com.example.piapoi.Helpers.showProgressBar
import com.example.piapoi.Models.Post
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.card_post_recycler.*
import java.util.*

class CreatePostActivity : AppCompatActivity() {

    private lateinit var dialog : Dialog
    private  var fileUri : Uri?=null
    private var fileDownloadableLink : String = ""
    private  var imageUri: Uri? = null
    private var imageDownloadableLink : String = ""
    private var longitude :String = ""
    private var latitude : String = ""
    private var fileName : String=""
    private var filePreviewName : String = ""
    private lateinit var currentLocation : Location
    private lateinit var fusedLocationProvider : FusedLocationProviderClient
    private val permissionCode=101
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_create_post)
        getSupportActionBar()?.hide()

        val backButton=findViewById<ImageButton>(R.id.backCreatePost)
        val validator : AwesomeValidation = AwesomeValidation(ValidationStyle.BASIC)
        validator.addValidation(this,R.id.postText, RegexTemplate.NOT_EMPTY,R.string.empty_post)

        backButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }
        careerName.text=UserInstance.getUserInstance()?.career.toString()
        fusedLocationProvider= LocationServices.getFusedLocationProviderClient(this)
        imagePostPreview.visibility=View.GONE
        filePreviewPost.visibility=View.GONE
        locationPreviewPost.visibility=View.GONE
        imagePostPreview.visibility=View.GONE
        removeImage.visibility=View.GONE
        removeFile.visibility=View.GONE
        removeLocation.visibility=View.GONE
        val user = UserInstance.getUserInstance()

        dialog= Dialog(this@CreatePostActivity)
        showProgressBar(dialog)

        if (user != null) {
            userNameCreatePost.text=user.name + " "+ user.lastName
            if(user.imagePath.equals("Default")){
                Glide.with(this)
                    .load(Paths.defaultImagePath)
                    .into(createPostUserPic)
                hideProgressBar(dialog)
            }else{
                FirebaseStorage.getInstance().getReference(user.imagePath.toString()).downloadUrl.addOnSuccessListener {
                    Glide.with(this)
                        .load( it.toString())
                        .into(createPostUserPic)
                }.addOnCompleteListener{

                    hideProgressBar(dialog)
                }.addOnFailureListener{

                }.addOnCanceledListener {

                }
            }

        }


        removeImage.setOnClickListener{
            imageUri=null
            imageDownloadableLink=""
            Glide.with(this)
                .clear(imagePostPreview)
            imagePostPreview.visibility=View.GONE
            removeImage.visibility=View.GONE
        }
        removeLocation.setOnClickListener{
            longitude=""
            latitude=""
            locationPreviewPost.text=""
            locationPreviewPost.visibility=View.GONE
            removeLocation.visibility=View.GONE
        }

        removeFile.setOnClickListener{
            fileUri=null
            fileDownloadableLink=""
            filePreviewName=""
            filePreviewPost.text=""
            removeFile.visibility=View.GONE
            filePreviewPost.visibility=View.GONE
        }

        createPostBtn.setOnClickListener{


            if(validator.validate()){
                dialog= Dialog(this@CreatePostActivity)
                showProgressBar(dialog)
                val reference = FirebaseStorage.getInstance().getReference("Post")
                var imageReference : StorageReference = reference.child("failureReference")
               var fileReference : StorageReference = reference.child("failureReference")
                if(imageUri!=null){
                    imageReference =reference.child(UserInstance.getUserInstance()?.uid.toString()).child("Images").child(System.currentTimeMillis().toString() + "." + getFileExtension(imageUri!!) )
                }

                if(fileUri!=null){
                    fileReference =reference.child(UserInstance.getUserInstance()?.uid.toString()).child("Files").child(System.currentTimeMillis().toString() + "." + getFileExtension(fileUri!!) )

                }
            try {
                imageReference.putFile(imageUri!!).addOnSuccessListener {

                    it.storage.downloadUrl.addOnSuccessListener{
                        imageDownloadableLink=it.toString()
                        try {
                            fileReference.putFile(fileUri!!).addOnSuccessListener {
                                it.storage.downloadUrl.addOnSuccessListener{
                                    fileDownloadableLink=it.toString()
                                    insertPost(dialog)
                                }
                            }
                        }catch (e : Exception){
                            insertPost(dialog)
                        }
                    }
                }
            }
            catch(e : Exception){
                try {
                    fileReference.putFile(fileUri!!).addOnSuccessListener {
                        it.storage.downloadUrl.addOnSuccessListener{
                            fileDownloadableLink=it.toString()
                            insertPost(dialog)
                        }
                    }

                }catch (e : Exception){
                    insertPost(dialog)
                }

            }


            }
        }

        //Files event
        attach_file_button.setOnClickListener{
            openFileChooser()
        }
        //Images event
        uploadImageButton.setOnClickListener{
            openImageChooser()
        }
        locationButton.setOnClickListener{
            fetchLocation()
        }
       //  var bottomSheetFragment= MessageBottomSheetDialogFragment()
         //optionsButton.setOnClickListener { view ->
        //     bottomSheetFragment.show(supportFragmentManager,"BottomSheetDialog")
       //  }

    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)
    }
    private fun insertPost(d: Dialog) {
        val post = Post(UserInstance.getUserInstance()?.imagePath.toString(),UserInstance.getUserInstance()?.name.toString() + " "
                +UserInstance.getUserInstance()?.lastName.toString(),
            ServerValue.TIMESTAMP, postText.text.toString(),imageDownloadableLink,fileDownloadableLink,
            UserInstance.getUserInstance()?.uid.toString(),filePreviewName, longitude, latitude)

        PostDAO.add(post,UserInstance.getUserInstance()?.career.toString(),"General").addOnSuccessListener {

            val mainActivityIntent=Intent(this,MainActivity::class.java)
            hideProgressBar(d)
            startActivity(mainActivityIntent)
            finish()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if( resultCode== RESULT_OK
            && data!=null && data.data!=null){

            if(requestCode==1){

                imageUri=data.data!!
                uploadPostImage()

            }

            if(requestCode==2){
                fileUri=data.data!!
                //Toast.makeText(this,"I work", Toast.LENGTH_SHORT).show()
                uploadPostFile()
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
        intent.setAction(Intent.ACTION_GET_CONTENT)

        startActivityForResult(intent,2)
    }
    private fun uploadPostImage(){
        if(imageUri!=null) {

            Glide.with(this)
                .load(imageUri)
                .into(imagePostPreview)
            imagePostPreview.visibility=View.VISIBLE
            removeImage.visibility=View.VISIBLE
        }
    }
    private fun uploadPostFile(){
        if(fileUri!=null) {
            filePreviewPost.text= fileUri!!.path.toString().substring(fileUri!!.path.toString().lastIndexOf("/")+1) ;
            filePreviewName=fileUri!!.path.toString().substring(fileUri!!.path.toString().lastIndexOf("/")+1) ;
          //  filePreviewPost.text=File(fileUri!!.path).name + "." +getFileExtension(fileUri!!)
            filePreviewPost.visibility=View.VISIBLE
            removeFile.visibility=View.VISIBLE
        }
    }


    private fun fetchLocation() {
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),permissionCode)
            Toast.makeText(this,"You need to provide permissions in order to share your location",Toast.LENGTH_LONG).show()
            return
        }
        dialog = Dialog(this@CreatePostActivity)
        showProgressBar(dialog)
        fusedLocationProvider.lastLocation.addOnSuccessListener{location->

            if(location!=null){
                currentLocation=location
                longitude=location.longitude.toString()
                latitude=location.latitude.toString()
                    val latitudeD : Double = currentLocation.latitude.toDouble()
                    val longitudeD : Double=currentLocation.longitude.toDouble()

                   val geo :  Geocoder = Geocoder(this.getApplicationContext(), Locale.getDefault());
                   val addresses : List<Address> = geo.getFromLocation(latitudeD, longitudeD, 1)
                    if (!addresses.isEmpty()) {
                        locationPreviewPost.visibility=View.VISIBLE
                        removeLocation.visibility=View.VISIBLE
                        locationPreviewPost.text=addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName()
                    }
                    else {
                        Toast.makeText(this,"RIP NO EXISTES EN EL MAPA",Toast.LENGTH_LONG).show()
                    }

            }else{
                Toast.makeText(this,"RIP",Toast.LENGTH_LONG).show()
            }
            hideProgressBar(dialog)
        }.addOnFailureListener{
            hideProgressBar(dialog)
        }
    }
    private fun getFileExtension(uri : Uri) : String?{
        if(uri!=null){


        val cR: ContentResolver = contentResolver

        val mime : MimeTypeMap = MimeTypeMap.getSingleton()

        return mime.getExtensionFromMimeType(cR.getType(uri))
        }
        return ""
    }
}