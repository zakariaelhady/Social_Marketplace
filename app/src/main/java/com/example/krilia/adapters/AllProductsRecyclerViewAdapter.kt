package com.example.krilia.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.krilia.CommentsActivity
import com.example.krilia.ProductDetailsActivity
import com.example.krilia.R
import com.example.krilia.Retrofit.ApiInterface
import com.example.krilia.Retrofit.SessionManager
import com.example.krilia.models.PItem
import com.example.krilia.utils.Constant
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback


class AllProductsRecyclerViewAdapter(
    private var productsList: MutableList<PItem>
) : RecyclerView.Adapter<AllProductsRecyclerViewAdapter.ViewHolder>() {
    private lateinit var con:Context
    private lateinit var sessionManager : SessionManager

//    private var productsList=chosenList
    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_card, parent, false)

        con = parent.context
        sessionManager=SessionManager(con)
        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = productsList[position]


        Picasso.get().load(Constant.storage+ItemsViewModel.image).into(holder.imageView)
        holder.textView.text = ItemsViewModel.title
        val productPrice: String=ItemsViewModel.price.toString()+con.getString(R.string.currency)
        holder.pricetextview.text = productPrice
        holder.likesCount.text=ItemsViewModel.productLikes.toString()

        holder.id=ItemsViewModel.id
        holder.liked=ItemsViewModel.liked
        holder.saved=ItemsViewModel.saved
        holder.savedIcon=ItemsViewModel.savedIcon

        if(holder.liked == "fas"){
            holder.heartIcon.setImageResource(R.drawable.ic_favorite_24)
        }
        if(holder.saved == "saved"){
            holder.saveIcon.visibility=View.GONE
            holder.savedText.visibility=View.VISIBLE
        }

        holder.heartIcon.setOnClickListener {
            if(holder.liked == "fas"){
                holder.liked= "far"
                holder.heartIcon.setImageResource(R.drawable.ic_favorite_border_24)
                holder.likesCount.text= (holder.likesCount.text.toString().toInt()-1).toString()
            }
            else{
                holder.liked= "fas"
                holder.heartIcon.setImageResource(R.drawable.ic_favorite_24)
                holder.likesCount.text= (holder.likesCount.text.toString().toInt()+1).toString()
            }
            addLike(holder.id)
        }

        holder.saveIcon.setOnClickListener {
            holder.saveIcon.visibility=View.GONE
            holder.savedText.visibility=View.VISIBLE
            addSave(holder.id)
        }
        holder.savedText.setOnClickListener {
            holder.saveIcon.visibility=View.VISIBLE
            holder.savedText.visibility=View.GONE
            addSave(holder.id)
        }

        holder.commentIcon.setOnClickListener {
            val intent=Intent(con, CommentsActivity::class.java)
            val b = Bundle()
            b.putString("productId", holder.id.toString()) //Your id
            intent.putExtras(b)
            con.startActivity(intent)
        }

        holder.imageView.setOnClickListener {
            val intent=Intent(con, ProductDetailsActivity::class.java)
            val b = Bundle()
            b.putString("productId", holder.id.toString()) //Your id
            intent.putExtras(b)
            con.startActivity(intent)
        }
        holder.titleCard.setOnClickListener {
            val intent=Intent(con, ProductDetailsActivity::class.java)
            val b = Bundle()
            b.putString("productId", holder.id.toString()) //Your id
            intent.putExtras(b)
            con.startActivity(intent)
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return productsList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_product)
        val textView: TextView = itemView.findViewById(R.id.tv_product)
        val pricetextview : TextView=itemView.findViewById(R.id.tv_price)
        val likesCount: TextView=itemView.findViewById(R.id.tv_likes_count)

        var id: Int=0
        var liked: String=""
        var saved: String=""
        var savedIcon: String=""

        val commentIcon: ImageView = itemView.findViewById(R.id.iv_comment)
        val titleCard : ConstraintLayout=itemView.findViewById(R.id.cl_card)
        val heartIcon : ImageView = itemView.findViewById(R.id.iv_like)
        val saveIcon : ImageView = itemView.findViewById(R.id.iv_saved)
        val savedText: TextView = itemView.findViewById(R.id.tv_saved)

    }

    fun filterList(list: MutableList<PItem>){
        productsList=list
        notifyDataSetChanged()
    }

    private fun addLike( productId : Int){
        val apiInterface = ApiInterface.create().addLike("Bearer ${sessionManager.fetchAuthToken()}",productId)

        apiInterface.enqueue( object : Callback<UInt> {
            override fun onResponse(call: Call<UInt>, response: retrofit2.Response<UInt>) {
            }
            override fun onFailure(call: Call<UInt>, t: Throwable) {
            }
        })
    }

    private fun addSave( productId : Int){
        val apiInterface = ApiInterface.create().addSave("Bearer ${sessionManager.fetchAuthToken()}",productId)

        apiInterface.enqueue( object : Callback<UInt> {
            override fun onResponse(call: Call<UInt>, response: retrofit2.Response<UInt>) {
            }
            override fun onFailure(call: Call<UInt>, t: Throwable) {
            }
        })
    }

}