package com.example.krilia.adapters


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.krilia.ProfileActivity
import com.example.krilia.R
import com.example.krilia.Retrofit.ApiInterface
import com.example.krilia.Retrofit.SessionManager
import com.example.krilia.models.Message
import com.example.krilia.utils.Constant
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import java.text.SimpleDateFormat


class MessagesRecyclerViewAdapter(
    private val messageList: MutableList<Message>,private val sessionManager: SessionManager
) : RecyclerView.Adapter<MessagesRecyclerViewAdapter.ViewHolder>() {
        private val VIEW_TYPE_ONE = 1
        private val VIEW_TYPE_TWO = 2
        private lateinit var con: Context
    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view1 = LayoutInflater.from(parent.context)
            .inflate(R.layout.message_item_sender, parent, false)
        val view2= LayoutInflater.from(parent.context)
            .inflate(R.layout.message_item_receiver, parent, false)
        con=parent.context
        view1.tag="view1"
        view2.tag="view2"
        return if (viewType == VIEW_TYPE_ONE) {
            ViewHolder(view1)
        } else{
            ViewHolder(view2)
        }
    }

    // binds the list items to a view
    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = messageList[position]

        Picasso.get().load(Constant.storage+ItemsViewModel.image).into(holder.userImg)
        holder.message.text=ItemsViewModel.message
        Picasso.get().load(Constant.storage2+ItemsViewModel.image_msg).into(holder.image_msg)
//        holder.messageDate.text=ItemsViewModel.date

        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm")
        val output: String = formatter.format(parser.parse(ItemsViewModel.date)!!)
        holder.messageDate.text=output
        holder.other_id=ItemsViewModel.other_id
        holder.image_exist=ItemsViewModel.image_exist
        holder.message_exist=ItemsViewModel.message_exist
        holder.id=ItemsViewModel.id
        holder.receiver=ItemsViewModel.receiver
        holder.sender=ItemsViewModel.sender

        if (holder.message_exist){
            holder.message.visibility=View.VISIBLE
        }
        else{
            holder.message.visibility=View.GONE
        }

        if(holder.image_exist){
            holder.image_msg.visibility=View.VISIBLE
        }
        else{
            holder.image_msg.visibility=View.GONE
        }

        if(holder.itemView.tag == "view1"){
            holder.itemView.setOnCreateContextMenuListener { contextMenu, view, contextMenuInfo ->
                contextMenu.add("delete").setOnMenuItemClickListener {
                    deleteMSG(holder.id)
                    messageList.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position,itemCount)
                    return@setOnMenuItemClickListener true
                }
            }
        }

        if(holder.itemView.tag == "view1") {
            holder.userImg.setOnClickListener {
                val intent = Intent(con, ProfileActivity::class.java)
                val b = Bundle()
                b.putString("userID", sessionManager.fetchUserId().toString()) //Your id
                intent.putExtras(b)
                con.startActivity(intent)
            }
        }else{
            holder.userImg.setOnClickListener {
                val intent = Intent(con, ProfileActivity::class.java)
                val b = Bundle()
                b.putString("userID", holder.other_id.toString()) //Your id
                intent.putExtras(b)
                con.startActivity(intent)
            }
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return messageList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val userImg: ImageView = itemView.findViewById(R.id.user_image)
        val message: TextView = itemView.findViewById(R.id.tv_message)
        val image_msg: ImageView=itemView.findViewById(R.id.message_image)
        val messageDate: TextView = itemView.findViewById(R.id.tv_time)

        var other_id: Int=0
        var image_exist: Boolean=false
        var message_exist : Boolean=false
        var id: Int=0
        var receiver : Boolean=false
        var sender: Boolean=false

    }

    override fun getItemViewType(position: Int): Int {
        return if (messageList[position].sender) {
            VIEW_TYPE_ONE
        }
        else VIEW_TYPE_TWO
    }
    fun deleteMSG(id: Int){
        val apiInterface = ApiInterface.create().deleteMessage("Bearer ${sessionManager.fetchAuthToken()}",id)

        apiInterface.enqueue( object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: retrofit2.Response<Any>) {
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
            }
        })
    }
}

