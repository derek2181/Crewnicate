package com.example.piapoi

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.example.piapoi.Adapter.GroupsAdapter
import com.example.piapoi.DAO.AssignmentDAO
import com.example.piapoi.Helpers.UserInstance
import com.example.piapoi.Helpers.hideProgressBar
import com.example.piapoi.Helpers.showProgressBar
import com.example.piapoi.Models.Assignment
import com.example.piapoi.Models.Contact
import com.example.piapoi.Models.Team
import com.example.piapoi.ModelsAdapters.SpinerAdapter
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_create_task.*
import java.sql.Timestamp
import java.util.*

class CreateTaskActivity : AppCompatActivity(),View.OnClickListener, AdapterView.OnItemClickListener {
    private val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().reference
    private lateinit var dialog : Dialog
    private var selectedTeam : String =""
    private var teamsList = mutableListOf<SpinerAdapter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_create_task)
        getSupportActionBar()?.hide()
        initTeams(this)


        val validator : AwesomeValidation = AwesomeValidation(ValidationStyle.BASIC)
        validator.addValidation(this,R.id.selectTeams, RegexTemplate.NOT_EMPTY,R.string.empty_name)
        validator.addValidation(this,R.id.taskTitle, RegexTemplate.NOT_EMPTY,R.string.empty_name)


        val adapter=ArrayAdapter(this,R.layout.team_names_select,teamsList)
        val select=findViewById<AutoCompleteTextView>(R.id.selectTeams)
        val backButton=findViewById<ImageButton>(R.id.backCreateTask)

        with(select){
            setAdapter(adapter)
            onItemClickListener=this@CreateTaskActivity
        }


        addTask.setOnClickListener{

            if(validator.validate()){
                dialog=Dialog(this)
                showProgressBar(dialog)
                val key: UUID = UUID.randomUUID()
                val randomUUIDString: String = key.toString()

                val assignment = Assignment(selectedTeam,taskTitle.text.toString(),ServerValue.TIMESTAMP,taskDescription.text.toString(),randomUUIDString)
                    AssignmentDAO.add(assignment,selectedTeam).addOnSuccessListener {

                        val intent=Intent(this,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        hideProgressBar(dialog)
                    }

            }
        }
        //AQUI PASAR A QUE ACTIVITY SE TIENE QUE REGRESAR EL BOTON
        backButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }


    private fun initTeams(context : Context){

        dialog= Dialog(context)
        showProgressBar(dialog)
        teamsList.clear()
        databaseReference.child(Team::class.java.simpleName).child(UserInstance.getUserInstance()?.career.toString()).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach({team->

                    val contactList = mutableListOf<Contact>()
                    team.children.forEach({contact->
                        val contact = Contact( contact.child("name").value.toString(),
                            contact.child("lastName").value.toString(),contact.child("imagePath").value.toString()
                            ,contact.child("uid").value.toString(),contact.child("active").value.toString(),
                            contact.child("career").value.toString())

                        contactList.add(contact)
                    })

                    val aux = Team(team.key.toString(),contactList)

                    for(member in aux.members){
                        if(member.uid.equals(UserInstance.getUserInstance()?.uid.toString())){

                            val adapter=SpinerAdapter(aux.name,aux.name.split("-")[0])
                            teamsList.add(adapter)
                        }
                    }

                })

                hideProgressBar(dialog)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        selectedTeam=teamsList[position].key

    }

    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }
}
