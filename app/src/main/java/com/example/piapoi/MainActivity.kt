package com.example.piapoi

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.piapoi.Fragments.*
import com.example.piapoi.Helpers.Paths
import com.example.piapoi.Helpers.UserInstance
import com.example.piapoi.Helpers.hideProgressBar
import com.example.piapoi.Helpers.showProgressBar
import com.example.piapoi.Models.User
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_profile.*


class MainActivity : AppCompatActivity() {
    private val homeFragment=HomeFragment()
    private val chatFragment= ChatFragment()
    private val taskFragment= TasksFragment()
    private lateinit var dialog : Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getSupportActionBar()?.hide()
        replaceFragment(homeFragment)
        findViewById<TextView>(R.id.fragmentName).text=homeFragment.getFragmentName()

        profileImageButton.setOnClickListener{

            intent= Intent(this,ProfileActivity::class.java)
            startActivity(intent)
        }


        dialog= Dialog(this@MainActivity)
        showProgressBar(dialog)
        val user = UserInstance.getUserInstance()
        if (user != null) {
            if(user.imagePath.equals("Default")){
                Glide.with(this)
                    .load(Paths.defaultImagePath)
                    .into(profileImageButton)
            }else{
                FirebaseStorage.getInstance().getReference(user.imagePath.toString()).downloadUrl.addOnSuccessListener {
                    Glide.with(this)
                        .load( it.toString())
                        .into(profileImageButton)
                }.addOnCompleteListener{

                    hideProgressBar(dialog)
                }
            }

        }
        hideProgressBar(dialog)
        bottom_nav.setOnNavigationItemSelectedListener{
            when(it.itemId){
                R.id.menuhome->{
                    findViewById<TextView>(R.id.fragmentName).text=homeFragment.getFragmentName()
                    replaceFragment(homeFragment)
                }
                R.id.chat->{
                    findViewById<TextView>(R.id.fragmentName).text=chatFragment.getFragmentName()
                    replaceFragment(chatFragment)
                }
                R.id.tasks->{
                    findViewById<TextView>(R.id.fragmentName).text=taskFragment.getFragmentName()
                    replaceFragment(taskFragment)
                }

            }
            true
        }

    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {


        return super.onCreateView(name, context, attrs)
    }
    private fun replaceFragment(fragment: Fragment){
    if(fragment!=null){
        val transaction=supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container,fragment)
        transaction.commit()
    }
    }
    //Aqui estoy QUEMANDO codigo

}