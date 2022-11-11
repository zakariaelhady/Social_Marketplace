package com.example.krilia.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.krilia.R
import com.example.krilia.utils.Constant
import com.squareup.picasso.Picasso


class LoadedImagesRecyclerViewAdapter(
    private var imageList: MutableList<String>
) : RecyclerView.Adapter<LoadedImagesRecyclerViewAdapter.ViewHolder>() {
    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.picked_image_card, parent, false)
        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = imageList[position]
//        holder.imageView.setImageURI(ItemsViewModel)
        Picasso.get().load(Constant.storage+ ItemsViewModel).into(holder.imageView)
        holder.deleteImageView.setOnClickListener {
            imageList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position,itemCount)
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return imageList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_image)
        val deleteImageView : ImageView=itemView.findViewById(R.id.iv_delete)
    }

}