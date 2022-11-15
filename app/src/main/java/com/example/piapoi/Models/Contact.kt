package com.example.piapoi.Models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties


data class Contact(var name: String = "", var lastName: String = "",var imagePath : String = "",var uid : String="", var active : String ="",var career : String = ""
,var isSelected : Boolean = false,var tasksCompleted : String = "", var email : String = "") {

}