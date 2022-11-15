package com.example.piapoi.Helpers
import android.app.Dialog
import android.util.Base64.DEFAULT
import android.util.Base64.encodeToString
import android.view.Window
import com.example.piapoi.Models.User
import com.example.piapoi.R
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory

import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec

import javax.crypto.spec.SecretKeySpec
import kotlin.collections.HashMap

object Encryption {

    const val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
    const val salt = "QWlGNHNhMTJTQWZ2bGhpV3U=" // base64 decode => AiF4sa12SAfvlhiWu
    const val iv = "bVQzNFNhRkQ1Njc4UUFaWA==" // base64 decode => mT34SaFD5678QAZX

    fun encrypt(strToEncrypt: String) :  String?
    {
        try
        {
            val ivParameterSpec = IvParameterSpec(android.util.Base64.decode(iv, android.util.Base64.DEFAULT))

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val spec =  PBEKeySpec(secretKey.toCharArray(), android.util.Base64.decode(salt, android.util.Base64.DEFAULT), 10000, 256)
            val tmp = factory.generateSecret(spec)
            val secretKey =  SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
            return android.util.Base64.encodeToString(cipher.doFinal(strToEncrypt.toByteArray(Charsets.UTF_8)), android.util.Base64.DEFAULT)
        }
        catch (e: Exception)
        {
            println("Error while encrypting: $e")
        }
        return null
    }

    fun decrypt(strToDecrypt : String) : String? {
        try
        {

            val ivParameterSpec =  IvParameterSpec(android.util.Base64.decode(iv, android.util.Base64.DEFAULT))

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val spec =  PBEKeySpec(secretKey.toCharArray(), android.util.Base64.decode(salt, android.util.Base64.DEFAULT), 10000, 256)
            val tmp = factory.generateSecret(spec);
            val secretKey =  SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            return  String(cipher.doFinal(android.util.Base64.decode(strToDecrypt, android.util.Base64.DEFAULT)))
        }
        catch (e : Exception) {
            println("Error while decrypting: $e");
        }
        return null
    }
}



object UserInstance{
    private var user : User? = null
    private var userUID : String?=null

    fun setUserInstance(user: User?){
        this.user=user
    }

    fun deleteUserInstance(){
        this.user=null
    }

    fun getUserInstance() : User?{
        return user
    }

    fun updateInstance(hashMap: HashMap<String, Any>){
        user?.name =hashMap.get("name").toString()
        user?.lastName=hashMap.get("lastName").toString()
        user?.email=hashMap.get("email").toString()
        user?.password=hashMap.get("password").toString()
    }

}
object Paths{
    var defaultImagePath="https://firebasestorage.googleapis.com/v0/b/crewnicate-7ad02.appspot.com/o/User%2Fuser.png?alt=media&token=7d357701-fe2d-46a9-9791-b0f6e3119671"
}

 fun showProgressBar(dialog : Dialog){

    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.progress_bar)
    dialog.setCanceledOnTouchOutside(false)
    dialog.show()
}

 fun hideProgressBar(dialog : Dialog){
    dialog.dismiss()
}