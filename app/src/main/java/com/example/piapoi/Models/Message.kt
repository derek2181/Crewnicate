package com.example.piapoi.Models

import java.util.*

data class Message(var message : String = "", var date : Any? = null, var senderUid : String = "",
                   var audioPath : String ="", var imagePath : String = "", var filePath  :String = "",var fileName :  String="",
                   var latitude : String = "",var longitud : String=""
) {

}