package com.example.krilia.models

import android.icu.text.CaseMap
import com.example.krilia.Retrofit.ApiFuctions
import com.google.gson.annotations.SerializedName

data class ProductModel(
    var data: Data
)


// @Serializable
data class Data(

    var trendProducts: List<Product>,
    var lastarrived: List<Product>

)

// @Serializable
data class Product(
//    @SerializedName("name")
    val id: Int,
    val title : String,
    val description: String,
    val price: Float,
    val status: String,
    val categorie: String,
    val favourites: Int,
    val user_id: Int,
    val created_at: String,
    val updated_at: String,
    val images: List<String>,
)

data class ProductLiKedOrSaveddata(
    val data: MutableList<ProductLiKedOrSaved>
)

data class ProductLiKedOrSaved(
    val id: Int,
    val title : String,
    val price: Float,
    val status: String,
    val categorie: String,
    val image: String
)

data class LikedOrSaved(
    val liked: Boolean,
    val saved: Boolean
)

data class productAvailability(
    val available: Boolean
)

data class createProductData(
    val success: Boolean,
    val data: Int
)