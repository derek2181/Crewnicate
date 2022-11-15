package com.example.piapoi.DAO

import com.example.piapoi.Helpers.UserInstance
import com.example.piapoi.Models.ChatPreview
import com.example.piapoi.Models.Message
import com.example.piapoi.Models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*

class ChatDAO {
    companion object {
        private val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(Message::class.java.simpleName)

        // CRUD
        fun add(message: Message, chatUid : String,receiverUid : String): Task<Void> {
            var receiverImagePath :String = ""
            var receiverName : String = ""
            var receiverLastName : String = ""
            var ref=FirebaseDatabase.getInstance().getReference().child(User::class.java.simpleName).child(receiverUid)

            ref.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                   receiverImagePath =snapshot.child("imagePath").getValue(String::class.java) as String
                    receiverName=snapshot.child("name").getValue(String::class.java) as String
                    receiverLastName=snapshot.child("lastName").getValue(String::class.java) as String


                    //Cuando mandas un mensaje la otra persona tambien tiene que generar una preview de ese mensaje entonces se agregan dos campos a la tabla ChatPreview
                    //Conteniendo la persona que mandó el mensaje al ultimo
                    val previewActiveUser : ChatPreview = ChatPreview(receiverImagePath,receiverName + " " + receiverLastName,if(message.message.equals("")) "Attached multimedia content" else message.message,message.senderUid,receiverUid)
                    val previewReceiver : ChatPreview = ChatPreview(UserInstance.getUserInstance()?.imagePath.toString(),UserInstance.getUserInstance()?.name + " " +UserInstance.getUserInstance()?.lastName,
                        if(message.message.equals("")) "Attached multimedia content" else message.message,message.senderUid,UserInstance.getUserInstance()?.uid.toString())

                    //ChatPreviewDAO.add(previewActiveUser,receiverUid,chatUid)
                    ChatPreviewDAO.add(previewReceiver,receiverUid,chatUid)
                    ChatPreviewDAO.add(previewActiveUser,UserInstance.getUserInstance()?.uid.toString(),chatUid)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })


            return databaseReference.child(chatUid).child("Thread").push().setValue(message)
        }

        fun update(key: String, hashMap: HashMap<String, Any>): Task<Void>? {
            return databaseReference.child(key).updateChildren(hashMap)
        }

    }
}
