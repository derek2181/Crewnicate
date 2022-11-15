package com.example.piapoi.DAO

import com.example.piapoi.Helpers.UserInstance
import com.example.piapoi.Models.Contact
import com.example.piapoi.Models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_profile.*

class UserDAO {
    companion  object {
        private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference(User::class.java.simpleName)

       // CRUD
        fun add(user : User) : Task<Void> {
            return databaseReference.push().setValue(user)
        }
        fun update(key: String, hashMap: HashMap<String, Any>): Task<Void>? {

            val contactMap: HashMap<String, Any> = HashMap()
            val loginMap: HashMap<String, Any> = HashMap()
            if(hashMap.containsKey("password")){
                //Actualizo el login con la info que se puede actualizar
                loginMap.put("password",hashMap.getValue("password").toString())

            }
            if(hashMap.containsKey("encrypted")){
                loginMap.put("encrypted",hashMap.getValue("encrypted").toString())
            }
            if(hashMap.containsKey("name")){
                contactMap.put("name",hashMap.getValue("name").toString())
            }

            if(hashMap.containsKey("lastName")){
                contactMap.put("lastName",hashMap.getValue("lastName").toString())
            }

            if(hashMap.containsKey("imagePath")){
                contactMap.put("imagePath",hashMap.getValue("imagePath").toString())

            }

            LoginDAO.update(key,loginMap)
            ContactDAO.update(key,contactMap)

            return  databaseReference.child(key).updateChildren(hashMap)
        }

        fun remove(key: String): Task<Void>? {
            return  databaseReference.child(key).removeValue()
        }
    }

}