package com.example.krilia.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.krilia.R
import com.example.krilia.Retrofit.ApiInterface
import com.example.krilia.Retrofit.SessionManager
import com.example.krilia.models.ProductLiKedOrSaved
import com.example.krilia.utils.Constant
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback


class LikedProductsRecyclerViewAdapter(
    private val productsList: MutableList<ProductLiKedOrSaved>,private val type: String
) : RecyclerView.Adapter<LikedProductsRecyclerViewAdapter.ViewHolder>() {
    private lateinit var sessionManager : SessionManager
    private lateinit var con: Context
    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_card3, parent, false)

        sessionManager=SessionManager(parent.context)
        con=parent.context
        return ViewHolder(view)
    }

    // binds the list items to a view
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = productsList[position]


        Picasso.get().load(Constant.storage+ItemsViewModel.image).into(holder.imageView)
        holder.titleView.text = ItemsViewModel.title
        holder.pricetextview.text = ItemsViewModel.price.toString() + con.getString(R.string.currency)
        holder.availabilityView.text=ItemsViewModel.status
        holder.categoryView.text=ItemsViewModel.categorie
        holder.id=ItemsViewModel.id

        holder.deleteIcon.setOnClickListener {
//            holder.card.visibility=View.GONE
            if(type == "like"){
                addLike(holder.id)
            }
            else{
                addSave(holder.id)
            }
            productsList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position,itemCount)
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return productsList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_product)
        val titleView: TextView = itemView.findViewById(R.id.tv_product)
        val pricetextview : TextView=itemView.findViewById(R.id.tv_price)
        val availabilityView: TextView=itemView.findViewById(R.id.tv_availability)
        val categoryView: TextView=itemView.findViewById(R.id.tv_category)

        val deleteIcon: ImageView =itemView.findViewById(R.id.iv_delete)
        var id: Int=0


    }

    private fun addLike(productId : Int){
        val apiInterface = ApiInterface.create().addLike("Bearer ${sessionManager.fetchAuthToken()}",productId)

        apiInterface.enqueue( object : Callback<UInt> {
            override fun onResponse(call: Call<UInt>, response: retrofit2.Response<UInt>) {
            }
            override fun onFailure(call: Call<UInt>, t: Throwable) {
            }
        })
    }

    private fun addSave(productId : Int){
        val apiInterface = ApiInterface.create().addSave("Bearer ${sessionManager.fetchAuthToken()}",productId)

        apiInterface.enqueue( object : Callback<UInt> {
            override fun onResponse(call: Call<UInt>, response: retrofit2.Response<UInt>) {
            }
            override fun onFailure(call: Call<UInt>, t: Throwable) {
            }
        })
    }
}