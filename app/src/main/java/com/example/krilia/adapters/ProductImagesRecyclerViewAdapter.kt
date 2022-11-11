package com.example.krilia.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.krilia.R
import com.example.krilia.utils.Constant
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation


class ProductImagesRecyclerViewAdapter(
    private var imagesList: List<String>, private val vp_product_images: ViewPager2
) : RecyclerView.Adapter<ProductImagesRecyclerViewAdapter.ViewHolder>() {
    // create new views

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_images_card, parent, false)
        view.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = imagesList[position]

        val transformation: Transformation = RoundedTransformationBuilder().borderColor(R.color.primary)
            .borderWidthDp(0.5f)
            .cornerRadiusDp(3f)
            .oval(false)
            .build()
        Picasso.get().load(Constant.storage+ItemsViewModel).transform(transformation).into(holder.imageView)

        if (position==imagesList.size-2){
            vp_product_images.post(runnable)
        }
    }
    // return the number of the items in the list
    override fun getItemCount(): Int {
        return imagesList.size
    }

    var runnable: Runnable= Runnable {
        run {
            imagesList=imagesList.plus(imagesList)
            notifyDataSetChanged()
        }
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_product_image)
    }
}

