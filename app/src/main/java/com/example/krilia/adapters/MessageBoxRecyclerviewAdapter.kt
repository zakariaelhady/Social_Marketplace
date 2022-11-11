package com.example.krilia.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.krilia.MessagesActivity
import com.example.krilia.R
import com.example.krilia.models.Conversations
import com.example.krilia.utils.Constant
import com.squareup.picasso.Picasso


class MessageBoxRecyclerviewAdapter(
    private var msgBoxList: MutableList<Conversations>
) : RecyclerView.Adapter<MessageBoxRecyclerviewAdapter.ViewHolder>() {
    private lateinit var con: Context
    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.message_box, parent, false)

        con = parent.context
        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = msgBoxList[position]
        // sets the image to the imageview from our itemHolder class
        Picasso.get().load(Constant.storage+ItemsViewModel.image).into(holder.profileImg)
        // sets the text to the textview from our itemHolder class
        holder.producttitle.text = ItemsViewModel.title
        holder.productOwner.text = ItemsViewModel.name
        holder.msgTime.text = ItemsViewModel.date
        holder.unreadMsgs.text = ""

        holder.other_id=ItemsViewModel.other_id
        holder.id=ItemsViewModel.id
        holder.product_id=ItemsViewModel.product_id
        holder.date=ItemsViewModel.date

        holder.ConvItem.setOnClickListener {
            val intent= Intent(con, MessagesActivity::class.java)
            val b = Bundle()
            b.putString("productId", holder.product_id.toString())
            b.putString("receiverId", holder.other_id.toString())
            intent.putExtras(b)
            con.startActivity(intent)
        }


    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return msgBoxList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val profileImg: ImageView = itemView.findViewById(R.id.profile)
        val producttitle: TextView = itemView.findViewById(R.id.tv_product_title)
        val productOwner: TextView = itemView.findViewById(R.id.tv_product_owner)
        val msgTime: TextView = itemView.findViewById(R.id.tv_time)
        val unreadMsgs: TextView = itemView.findViewById(R.id.tv_unread_messages)

        var other_id: Int=0
        var id: Int=0
        var product_id: Int=0
        var date: String=""

        var ConvItem: LinearLayout=itemView.findViewById(R.id.ll_message)
    }
    fun filterList(list: MutableList<Conversations>){
        msgBoxList=list
        notifyDataSetChanged()
    }

}