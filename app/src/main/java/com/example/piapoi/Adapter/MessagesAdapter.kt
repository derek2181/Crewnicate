package com.example.piapoi.Adapter

import android.app.DownloadManager
import android.app.Service
import android.content.Intent
import android.graphics.Typeface
import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.piapoi.GoogleMapActivity
import com.example.piapoi.Helpers.UserInstance
import com.example.piapoi.Models.Message
import com.example.piapoi.R
import kotlinx.android.synthetic.main.left_message_audio_recycler.view.*
import kotlinx.android.synthetic.main.left_message_recycler.view.*
import kotlinx.android.synthetic.main.left_message_recycler.view.date_message
import kotlinx.android.synthetic.main.right_message_map_recycler.view.*
import kotlinx.coroutines.Runnable
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MessagesAdapter(val messages: MutableList<Message>, val name: String,val uid: String) : RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder>(){

    object MessageType {
        const val MSG_TYPE_RIGHT = 0
        const val MSG_TYPE_LEFT = 1
        const val MSG_TYPE_RIGHT_AUDIO=2
        const val MSG_TYPE_LEFT_AUDIO=3
        const val MSG_TYPE_RIGHT_MAP=4
        const val MSG_TYPE_LEFT_MAP=5
    }

    class MessagesViewHolder(val view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder {

        when (viewType) {
            0 -> return MessagesViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.right_message_recycler, parent, false)
            )
            1 -> return MessagesViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.left_message_recycler, parent, false)
            )

            2 -> return MessagesViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.right_message_audio_recycler, parent, false)
            )

            3 -> return MessagesViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.left_message_audio_recycler, parent, false)
            )

            4 -> return MessagesViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.right_message_map_recycler, parent, false)
            )

            5 -> return MessagesViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.left_message_map_recycler, parent, false)
            )
        }

        return MessagesViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.left_message_map_recycler, parent, false))
    }

    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
        val message : Message? = messages[position]


        if(message?.audioPath.equals("")){

          if(message?.latitude.equals("")){


        if(message?.fileName.equals("")){
        if(message?.message.equals("")){

            holder.view.text_message.visibility=View.GONE
        }else{
            holder.view.text_message.text=message?.message
        }
        }else{
            holder.view.text_message.text=message?.fileName
            holder.view.text_message.typeface= Typeface.DEFAULT_BOLD

            holder.view.setOnClickListener{
                val request= DownloadManager.Request(Uri.parse(message?.filePath)).setTitle(message?.fileName.toString())
                    .setDescription("Downloading...")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setAllowedOverMetered(true)


                    val dm= holder.view.context.getSystemService(Service.DOWNLOAD_SERVICE) as DownloadManager

                dm.enqueue(request)
            }
        }

            if(message?.imagePath.equals("")){
                holder.view.messageImage.visibility=View.GONE
            }else{
                Glide.with(holder.view.context)
                    .load( message?.imagePath)
                    .into(holder.view.messageImage)
            }
          }else{
              holder.view.watchLocation.setOnClickListener {
                  val mapsIntent= Intent(holder.view.context, GoogleMapActivity::class.java)
                  mapsIntent.putExtra("uid",uid)
                  mapsIntent.putExtra("chatName",name)
                  mapsIntent.putExtra("context","Chat")
                  mapsIntent.putExtra("latitud",message?.latitude)
                  mapsIntent.putExtra("longitud",message?.longitud)
                  holder.view.context?.startActivity(mapsIntent)
              }
          }
        }else{
            holder.view.stopAudioBtn.visibility=View.GONE
            val audioUri=Uri.parse(message?.audioPath)
           val mediaPlayer=MediaPlayer.create(holder.view.context,audioUri)
            val handler=android.os.Handler()
           val runnable= object : Runnable {
                override fun run() {
                    holder.view.audioProgress.setProgress(mediaPlayer.currentPosition)
                    handler.postDelayed(this, 1000)
                }
            }
            val time=mediaPlayer.duration
            holder.view.audioTime.text=convertFormat(time)



            holder.view.audioProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                    if(fromUser){
                        mediaPlayer.seekTo(progress)
                    }
                    holder.view.audioTime.text=convertFormat(mediaPlayer.currentPosition)
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {}
                override fun onStopTrackingTouch(p0: SeekBar?) {}
            })

            mediaPlayer.setOnCompletionListener {
                holder.view.stopAudioBtn.visibility=View.GONE
                holder.view.playAudioBtn.visibility=View.VISIBLE
                mediaPlayer.seekTo(0)
            }
            holder.view.playAudioBtn.setOnClickListener{

                holder.view.playAudioBtn.visibility=View.GONE
                mediaPlayer.start()
                holder.view.stopAudioBtn.visibility=View.VISIBLE
                holder.view.audioProgress.max=mediaPlayer.duration
                handler.postDelayed(runnable ,0)
            }

            holder.view.stopAudioBtn.setOnClickListener{
                holder.view.playAudioBtn.visibility=View.VISIBLE
                holder.view.stopAudioBtn.visibility=View.GONE
                mediaPlayer.pause()
            }
        }
            val date= message?.date as Long
            //holder.view.date_message.text =SimpleDateFormat("dd/MM/yyyy - HH:mm:ss", Locale("es", "MX")).format(date)
             holder.view.date_message.text =SimpleDateFormat("HH:mm", Locale("es", "MX")).format(date)

    }

    private fun convertFormat(time: Int) : String {
    return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(time.toLong()),
    TimeUnit.MILLISECONDS.toSeconds(time.toLong())-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(
        time.toLong()
    )))
    }

    override fun getItemCount()= messages.size


    override fun getItemViewType(position: Int): Int {
        if(messages[position].senderUid.equals(UserInstance.getUserInstance()?.uid)){

            if(messages[position].longitud.equals("")){

            if(messages[position].audioPath.equals(""))
                return MessageType.MSG_TYPE_RIGHT
            else
                return MessageType.MSG_TYPE_RIGHT_AUDIO

            }else{
                return MessageType.MSG_TYPE_RIGHT_MAP
            }
        }else{

            if(messages[position].longitud.equals("")){

            if(messages[position].audioPath.equals(""))
                return MessageType.MSG_TYPE_LEFT
            else
                return MessageType.MSG_TYPE_LEFT_AUDIO

            }else{
                return MessageType.MSG_TYPE_LEFT_MAP
            }
        }

    }
}