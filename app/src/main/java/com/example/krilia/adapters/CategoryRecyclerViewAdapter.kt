package com.example.krilia.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.krilia.ProductOfCategoryActivity
import com.example.krilia.R
import com.example.krilia.models.Category
import com.example.krilia.utils.Constant
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation


class CategoryRecyclerViewAdapter(
    private var categoriesList: List<Category>, private val vp_categories: ViewPager2
) : RecyclerView.Adapter<CategoryRecyclerViewAdapter.ViewHolder>() {
    private lateinit var con: Context
    // create new views

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_card, parent, false)
        view.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        con=parent.context

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = categoriesList[position]

        val transformation: Transformation = RoundedTransformationBuilder().borderColor(R.color.primary)
            .borderWidthDp(1f)
            .cornerRadiusDp(10f)
            .oval(false)
            .build()
        Picasso.get().load(Constant.url+ItemsViewModel.image).transform(transformation).into(holder.imageView)
        holder.textView.text = ItemsViewModel.category
        if (position==categoriesList.size-2){
            vp_categories.post(runnable)
        }

        holder.imageView.setOnClickListener {
            val intent= Intent(con, ProductOfCategoryActivity::class.java)
            val b = Bundle()
            b.putString("category", holder.textView.text.toString()) //Your id
            intent.putExtras(b)
            con.startActivity(intent)
        }
    }
    // return the number of the items in the list
    override fun getItemCount(): Int {
        return categoriesList.size
    }

    var runnable: Runnable= Runnable {
        run {
            categoriesList=categoriesList.plus(categoriesList)
            notifyDataSetChanged()
        }
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_category)
        val textView: TextView = itemView.findViewById(R.id.tv_category)
    }
}

