package com.example.piapoi

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager

import com.example.piapoi.Adapter.TasksViewPagerAdapter
import com.example.piapoi.DAO.PostDAO
import com.example.piapoi.Fragments.*
import com.example.piapoi.Helpers.UserInstance

import com.example.piapoi.Helpers.showProgressBar
import com.example.piapoi.Models.Post
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.ServerValue

import kotlinx.android.synthetic.main.activity_team.*


import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import java.net.URL
import java.util.*

class TeamActivity :AppCompatActivity() {
    private lateinit var dialog : Dialog
    private var teamUid  :String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team)
        getSupportActionBar()?.hide()

        val bundle = intent.extras
        teamUid = bundle!!.getString("teamUid").toString()

        val backButton=findViewById<ImageButton>(R.id.backButtonTeamGroupButton)

        backButton.setOnClickListener{
           val intent = Intent(this,MainActivity::class.java)
           this?.startActivity(intent)
           finish()
        }

        sendEmailButton.setOnClickListener{
        val intent = Intent(this,SendEmailActivity::class.java)
            intent.putExtra("teamUid",teamUid)
            startActivity(intent)
        }
        teamName.text=teamUid.toString().split("-")[0]
        try
        {
        val callPhoneBtn=findViewById<ImageButton>(R.id.startMeeting)
        callPhoneBtn.setOnClickListener{
            insertPostAndStartMeeting()
        }
          }catch (e : Exception){


          Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()


       }
        initTabLayout(teamUid)
    }
    private fun insertPostAndStartMeeting() {

            val uidCall=UUID.randomUUID().toString()
            val post = Post(
                UserInstance.getUserInstance()?.imagePath.toString(),
                UserInstance.getUserInstance()?.name.toString() + " "
                        + UserInstance.getUserInstance()?.lastName.toString(),ServerValue.TIMESTAMP,UserInstance.getUserInstance()?.name.toString() + " "
                        + UserInstance.getUserInstance()?.lastName.toString() + " " + "has started a meeting",
                UserInstance.getUserInstance()?.uid.toString(),"","" , "","","",uidCall)

            PostDAO.add(post, UserInstance.getUserInstance()?.career.toString(),teamUid).addOnSuccessListener {
                val options = JitsiMeetConferenceOptions.Builder()
                    .setRoom(uidCall)
                    .setServerURL(URL("https://meet.jit.si/"))
                    .setWelcomePageEnabled(false)
                    .build()
                JitsiMeetActivity.launch(this, options)

            }

    }
    private fun initTabLayout(teamUid: String?) {
       // Aqui estoy cocinando arroz
       val tab =findViewById<TabLayout>(R.id.tabTeamLayout)
       val pager= findViewById<ViewPager>(R.id.viewTeamPager)
        //Aqui rosas me paso las verduras y las metio a la olla
       pager.isSaveEnabled=false; //Esto hace que ya no truene por el unique id que tenian los otros Fragments ahora unos huevitos con chorizo de vaca
        val adapter= TasksViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(GeneralChatTeamFragment(teamUid.toString()),"Chat")
        adapter.addFragment(GroupMembersTeamFragment(teamUid.toString()), "Members")
        pager.adapter=adapter

        tab.setupWithViewPager(pager)
    }
    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)
    }
}