package com.example.piapoi.Adapter

import android.app.DownloadManager
import android.app.Service
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.piapoi.Adapter.PostAdapter.MessageType.CALL_VIEW_TYPE
import com.example.piapoi.Adapter.PostAdapter.MessageType.POST_VIEW_TYPE
import com.example.piapoi.GoogleMapActivity
import com.example.piapoi.Helpers.Paths
import com.example.piapoi.Models.Post
import com.example.piapoi.R
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.card_post_call_recycler.view.*
import kotlinx.android.synthetic.main.card_post_recycler.view.*
import kotlinx.android.synthetic.main.card_post_recycler.view.datePost
import kotlinx.android.synthetic.main.card_post_recycler.view.textPost
import kotlinx.android.synthetic.main.card_post_recycler.view.userImagePost
import kotlinx.android.synthetic.main.card_post_recycler.view.userNamePost
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class PostAdapter(val posts: MutableList<Post>) : RecyclerView.Adapter<PostAdapter.PostsViewHolder>(){
    class PostsViewHolder(val view: View): RecyclerView.ViewHolder(view)
    object MessageType {
        const val POST_VIEW_TYPE= 0
        const val CALL_VIEW_TYPE = 1

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        if(viewType== POST_VIEW_TYPE){
            return PostsViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.card_post_recycler,parent,false)
            );
        }else{
            return PostsViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.card_post_call_recycler,parent,false)
            );
        }
    }

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        val post : Post =posts[position]

        holder.view.userNamePost.text=post.name
        holder.view.textPost.text=post.postText


        if(post.posterImagePath.equals("Default")){
            Glide.with(holder.view.context)
                .load(Paths.defaultImagePath)
                .into(holder.view.userImagePost)
        }else{
            FirebaseStorage.getInstance().getReference(post.posterImagePath).downloadUrl.addOnSuccessListener {
                Glide.with(holder.view.context)
                    .load( it.toString())
                    .into(holder.view.userImagePost)
            }.addOnCompleteListener{


            }
        }
        val date= post?.date as Long
        holder.view.datePost.text = SimpleDateFormat("dd/MM/yyyy", Locale("es", "MX")).format(date)

        if(!post.call.equals("")){

            holder.view.joinMeetingButton.setOnClickListener{

                val options = JitsiMeetConferenceOptions.Builder()
                    .setRoom(post.call)
                    .setServerURL(URL("https://meet.jit.si/"))
                    .setWelcomePageEnabled(false)
                    .build()
                JitsiMeetActivity.launch(holder.view.context, options)
            }
        }else{


        if(post.fileName.equals(""))
        {
            holder.view.filePostContainer.visibility=View.GONE
        }else{
            holder.view.filePost.text=post.fileName

            holder.view.filePost.setOnClickListener{
                 holder.view.filePost.setOnClickListener{
                val request= DownloadManager.Request(Uri.parse(post?.filePath)).setTitle(post?.fileName.toString())
                    .setDescription("Downloading...")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setAllowedOverMetered(true)

                val dm= holder.view.context.getSystemService(Service.DOWNLOAD_SERVICE) as DownloadManager

                dm.enqueue(request)
            }
            }
        }

        if(post.postImage.equals("")){
            holder.view.imagePost.visibility=View.GONE
        }else{

            Glide.with(holder.view.context)
                .load( post.postImage)
                .into(holder.view.imagePost)
        }

        if(post.latitude.equals("")){
            holder.view.locationPostContainer.visibility=View.GONE
        }else{
            holder.view.seeLocationPost.setOnClickListener{
                val mapsIntent= Intent(holder.view.context, GoogleMapActivity::class.java)
                mapsIntent.putExtra("uid",post.posterUid)
                mapsIntent.putExtra("context","Post")
                mapsIntent.putExtra("chatName",post.name)
                mapsIntent.putExtra("latitud",post.latitude)
                mapsIntent.putExtra("longitud",post.longitude)
                holder.view.context?.startActivity(mapsIntent)
            }
        }

        }
    }
    override fun getItemViewType(position: Int): Int {

        if(posts[position].call.equals("")){
            return POST_VIEW_TYPE
        }else{
            return CALL_VIEW_TYPE
        }

    }
    override fun getItemCount()= posts.size
}