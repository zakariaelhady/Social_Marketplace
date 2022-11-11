package com.example.krilia.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.krilia.ProductDetailsActivity
import com.example.krilia.R
import com.example.krilia.models.Product
import com.example.krilia.utils.Constant
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation


class ProductsRecyclerViewAdapter(
    private val productsList: List<Product>
) : RecyclerView.Adapter<ProductsRecyclerViewAdapter.ViewHolder>() {
    private lateinit var con: Context
    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_card2, parent, false)
        con = parent.context
        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = productsList[position]


        val transformation: Transformation = RoundedTransformationBuilder().borderColor(R.color.primary)
            .cornerRadiusDp(3f)
            .oval(false)
            .build()
        Picasso.get().load(Constant.storage+ItemsViewModel.images[0]).transform(transformation).into(holder.imageView)
        holder.textView.text = ItemsViewModel.title.trim()
        val productPrice: String=ItemsViewModel.price.toString()+con.getString(R.string.currency)
        holder.pricetextview.text = productPrice

        holder.id=ItemsViewModel.id

        holder.imageView.setOnClickListener {
            val intent= Intent(con, ProductDetailsActivity::class.java)
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


        var id: Int=0

    }
}