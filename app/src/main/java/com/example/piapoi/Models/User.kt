package com.example.piapoi.Models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(var email: String = "", var password: String = "", var name: String = "", var lastName: String = "", var uid: String?="", var imagePath: String="Default",
var career : String = "", var encrypted  :String = "") {

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "email" to email,
            "name" to name,
            "lastName" to lastName,
            "imagePath" to imagePath,
            "password" to password
        )
    }
}

