package com.example.krilia.adapters


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.krilia.ProfileActivity
import com.example.krilia.R
import com.example.krilia.Retrofit.ApiInterface
import com.example.krilia.Retrofit.SessionManager
import com.example.krilia.models.Comment
import com.example.krilia.utils.Constant
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_comments.*
import retrofit2.Call
import retrofit2.Callback


class CommentsRecyclerViewAdapter(
    private val commentsList: MutableList<Comment>
) : RecyclerView.Adapter<CommentsRecyclerViewAdapter.ViewHolder>() {
    private lateinit var sessionManager: SessionManager
    private lateinit var con: Context
    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.comment_product_item, parent, false)
            sessionManager=SessionManager(parent.context)
        con=parent.context
        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = commentsList[position]

        Picasso.get().load(Constant.storage + ItemsViewModel.image).into(holder.userImg)
        holder.userComment.text = ItemsViewModel.comment
        holder.userName.text = ItemsViewModel.name
        holder.commentDate.text = ItemsViewModel.date

        holder.commentId = ItemsViewModel.id
        holder.auth = ItemsViewModel.auth
        holder.userId = ItemsViewModel.userId

        if (holder.userId == sessionManager.fetchUserId()!!.toInt()) {
            holder.itemView.setOnCreateContextMenuListener { contextMenu, view, contextMenuInfo ->
                contextMenu.add("delete").setOnMenuItemClickListener {
                    deleteComment(holder.commentId.toString())
                    commentsList.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, itemCount)
                    return@setOnMenuItemClickListener true
                }
            }

        }

        holder.userImg.setOnClickListener {
            val intent = Intent(con, ProfileActivity::class.java)
            val b = Bundle()
            b.putString("userID", holder.userId.toString()) //Your id
            intent.putExtras(b)
            con.startActivity(intent)
        }
        holder.userName.setOnClickListener {
            val intent = Intent(con, ProfileActivity::class.java)
            val b = Bundle()
            b.putString("userID", holder.userId.toString()) //Your id
            intent.putExtras(b)
            con.startActivity(intent)
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return commentsList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val userImg: ImageView = itemView.findViewById(R.id.user_image)
        val userComment: TextView = itemView.findViewById(R.id.tv_user_comment)
        val userName: TextView = itemView.findViewById(R.id.tv_user_name)
        val commentDate: TextView = itemView.findViewById(R.id.tv_time)

        var commentId: Int=0
        var auth: Boolean=false
        var userId: Int=0
    }

    fun deleteComment(commentId: String){
        val apiInterface = ApiInterface.create().deleteComment(commentId)

        apiInterface.enqueue( object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: retrofit2.Response<Any>) {
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
            }
        })
    }


}