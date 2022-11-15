package com.example.piapoi

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.piapoi.DAO.AssignmentCompletedDAO
import com.example.piapoi.DAO.ContactDAO
import com.example.piapoi.Helpers.UserInstance
import com.example.piapoi.Helpers.hideProgressBar
import com.example.piapoi.Helpers.showProgressBar
import com.example.piapoi.Models.Assignment
import com.example.piapoi.Models.AssignmentCompleted
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_deliver_task.*
import kotlinx.android.synthetic.main.activity_deliver_task.view.*

class DeliverTaskActivity : AppCompatActivity() {
    private var team: String = ""
    private var id: String = ""
    private lateinit var dialog: Dialog
    private lateinit var assignment: AssignmentCompleted
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deliver_task)
        getSupportActionBar()?.hide()

        //AQUI PASAR A QUE ACTIVITY SE TIENE QUE REGRESAR EL BOTON


            val bundle = intent.extras
            team = bundle!!.getString("team").toString()
            id = bundle!!.getString("id").toString()



        databaseReference.child(Assignment::class.java.simpleName)
            .child(UserInstance.getUserInstance()?.career.toString()).child(team).child(id)
            .addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    assignment =
                        snapshot.getValue(AssignmentCompleted::class.java) as AssignmentCompleted
                    taskTitle.text = assignment.name
                    if (assignment.description.equals("")) {
                        taskInstructions.text = "No instructions provided"
                    } else {
                        taskInstructions.text = assignment.description
                    }


                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        val backButton = findViewById<ImageButton>(R.id.backDeliverTask)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        deliverTask.setOnClickListener {


            try {

            assignment.date = ServerValue.TIMESTAMP
            AssignmentCompletedDAO.add(assignment).addOnCompleteListener {


                databaseReference.child(AssignmentCompleted::class.java.simpleName)
                    .child(UserInstance.getUserInstance()?.uid.toString())
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val numberOfTasksCompleted = snapshot.children.count()

                            val contactMap: HashMap<String, Any> = HashMap()

                            contactMap.put("tasksCompleted", numberOfTasksCompleted.toString())

                            Toast.makeText(
                                this@DeliverTaskActivity,
                                "You have completed " + numberOfTasksCompleted.toString() + " " + "assignments, keep it up!",
                                Toast.LENGTH_LONG
                            ).show()
                            ContactDAO.update(
                                UserInstance.getUserInstance()?.uid.toString(),
                                contactMap
                            )?.addOnSuccessListener {
                                val intent =
                                    Intent(this@DeliverTaskActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })


            }
            } catch (e: Exception) {

                Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()

            }

        }

    }
}