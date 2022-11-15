package com.example.piapoi

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_google_map.*

class GoogleMapActivity :AppCompatActivity(), OnMapReadyCallback {
    private lateinit var uid :String
    private lateinit var mMap: GoogleMap
    private lateinit var name : String
    private var latitude :Double=0.0
    private  var longitude :Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_map)
        getSupportActionBar()?.hide()
        val bundle = intent.extras
        val context=bundle!!.getString("context").toString();
        uid = bundle!!.getString("uid").toString();
        name = bundle!!.getString("chatName").toString()
        latitude=bundle!!.getString("latitud").toString().toDouble()
        longitude=bundle!!.getString("longitud").toString().toDouble()


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        backCreateTask.setOnClickListener{

            if(context.equals("Chat")){


            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("uid",uid)
            intent.putExtra("chatName",name)
            this?.startActivity(intent)
            }else if(context.equals("Post")){
                val intent = Intent(this, MainActivity::class.java)
                this?.startActivity(intent)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val userLocation = LatLng(latitude, longitude)
        mMap.addMarker(MarkerOptions().position(userLocation).title("Location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation))

    }
}