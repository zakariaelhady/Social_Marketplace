package com.example.krilia

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.krilia.Retrofit.ApiInterface
import com.example.krilia.Retrofit.SessionManager
import com.example.krilia.adapters.ProductImagesRecyclerViewAdapter
import com.example.krilia.adapters.ProductsRecyclerViewAdapter
import com.example.krilia.models.LikedOrSaved
import com.example.krilia.models.Product
import com.example.krilia.models.ProductDetails
import com.example.krilia.models.productAvailability
import kotlinx.android.synthetic.main.activity_product_details.*
import retrofit2.Call
import retrofit2.Callback

class ProductDetailsActivity : BaseActivity() {
    var imagesList : List<String> = ArrayList()
    var productId: String?="0"
    var receiverId: Int=0
    var similarProductsList: List<Product> = ArrayList()
    private lateinit var sessionManager : SessionManager

    private var sliderHandler= Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)

        sessionManager=SessionManager(this@ProductDetailsActivity)

        val b = intent.extras
        productId=b!!.getString("productId")

        if(b.getString("productCreated")!=null){
            showErrorSnackbar("product created successfully",false)
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        loadProductDetails()
        getLikedOrSaved(productId!!.toInt())
        getAvailability(productId!!.toInt())

        tv_comments_count.setOnClickListener {
            val intent= Intent(this@ProductDetailsActivity, CommentsActivity::class.java)
            val bundle = Bundle()
            bundle.putString("productId", productId!!) //Your id
            intent.putExtras(b)
            startActivity(intent)
        }


        togglebutton.setOnCheckedChangeListener { _, checked ->
            if(checked){
                tv_status.text=getString(R.string.available)
                changeAvailability(productId!!.toInt(),"available")
            }else{
                tv_status.text=getString(R.string.not_available)
                changeAvailability(productId!!.toInt(),"not available")
            }
        }

        btn_delete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Are you sure you want to delete this product permanently?")
                .setPositiveButton("yes") { _, _ ->
                    deleteProduct(productId!!.toInt())
                    showErrorSnackbar("the product was deleted successfully", false)
                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            onBackPressed()
                        },
                        2000
                    )
                }
                .setNegativeButton("No") { _, _ ->
                    // user doesn't want to logout
                }
                .show()
        }

        btn_edit.setOnClickListener {
            val intent=Intent(this@ProductDetailsActivity, EditProductActivity::class.java)
            val bundle = Bundle()
            bundle.putString("productId", productId) //Your id
            intent.putExtras(b)
            startActivity(intent)
        }

        btn_contact_owner.setOnClickListener {
            val input = EditText(this@ProductDetailsActivity)
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            input.layoutParams = lp
            val dialog: AlertDialog.Builder=AlertDialog.Builder(this)
            dialog.setTitle("Contact Owner")
                .setView(input)
                .setPositiveButton("send") { _, _ ->
                    if (TextUtils.isEmpty(input.text.toString().trim { it <= ' ' })) {
                        showErrorSnackbar("type the message you want to send", true)
                    } else {
                        addMessage(input.text.toString())
                        input.setText("")
                        showErrorSnackbar("The message is sent successfully", false)
                    }
                }
                .setNegativeButton("cancel") { _, _ ->
                    // user doesn't want to logout
                }
                .show()
        }

        val pullToRefresh: SwipeRefreshLayout =product_details_swipe
        pullToRefresh.setOnRefreshListener {
            loadProductDetails(pullToRefresh)
            getLikedOrSaved(productId!!.toInt())
            getAvailability(productId!!.toInt())
        }
    }

    var sliderRunnable: Runnable= Runnable {
        run {
            vp_product_images.currentItem+=1
        }
    }

    override fun onPause(){
        super.onPause()
        sliderHandler.removeCallbacks(sliderRunnable)
    }

    override fun onResume() {
        super.onResume()
        sliderHandler.postDelayed(sliderRunnable,4000)
    }

    private fun changeAvailability(productId: Int,avail: String){
        val apiInterface=ApiInterface.create().changeAvailability(productId,avail)
        apiInterface.enqueue( object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: retrofit2.Response<Any>) {
            }
            override fun onFailure(call: Call<Any>, t: Throwable) {
            }
        })
    }

    private fun getAvailability(productId: Int){
        val apiInterface = ApiInterface.create().getAvailability(productId)
        apiInterface.enqueue( object : Callback<productAvailability> {
            override fun onResponse(call: Call<productAvailability>, response: retrofit2.Response<productAvailability>) {
                if(response.body()!=null){
                    if (!response.body()!!.available){
                        togglebutton.isChecked=false
                    }
                }
            }

            override fun onFailure(call: Call<productAvailability>, t: Throwable) {
                println(t.message)
            }

        })
    }


    private fun getLikedOrSaved(productId: Int){
        val apiInterface = ApiInterface.create().getLikeOrSave("Bearer ${sessionManager.fetchAuthToken()}",productId)
        apiInterface.enqueue( object : Callback<LikedOrSaved> {
            override fun onResponse(call: Call<LikedOrSaved>, response: retrofit2.Response<LikedOrSaved>) {

                if(response.body() != null){
                    val responseData=response.body()!!
                    val liked= responseData.liked
                    val saved= responseData.saved

                    if(liked){
                        iv_like.setImageResource(R.drawable.ic_favorite_24)
                        iv_like.tag="liked"
                    }
                    if(saved){
                        iv_saved.visibility= View.GONE
                        tv_saved.visibility= View.VISIBLE
                    }

                    iv_like.setOnClickListener {
                        if(iv_like.tag == "liked"){
                            iv_like.tag= "notLiked"
                            iv_like.setImageResource(R.drawable.ic_favorite_border_24)
                            tv_likes_count.text= (tv_likes_count.text.toString().toInt()-1).toString()
                        }
                        else{
                            iv_like.tag= "liked"
                            iv_like.setImageResource(R.drawable.ic_favorite_24)
                            tv_likes_count.text= (tv_likes_count.text.toString().toInt()+1).toString()
                        }
                        addLike(productId)
                    }

                    iv_saved.setOnClickListener {
                        iv_saved.visibility=View.GONE
                        tv_saved.visibility=View.VISIBLE
                        addSave(productId)
                    }
                    tv_saved.setOnClickListener {
                        tv_saved.visibility=View.GONE
                        iv_saved.visibility=View.VISIBLE
                        addSave(productId)
                    }
                }
            }

            override fun onFailure(call: Call<LikedOrSaved>, t: Throwable) {
                println(t.message)
            }

        })
    }
    private fun loadProductDetails(pullToRefresh: SwipeRefreshLayout?=null) {
        val apiInterface = ApiInterface.create().getProductDetails(productId!!)

        apiInterface.enqueue( object : Callback<ProductDetails> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<ProductDetails>, response: retrofit2.Response<ProductDetails>) {

                if(response.body() != null){
                    val dataDetails=response.body()!!.data
                    val product =dataDetails.product
                    val user= dataDetails.user

                    imagesList=product.images
                    val productimagesAdapter= ProductImagesRecyclerViewAdapter(imagesList,vp_product_images)
                    vp_product_images.adapter=productimagesAdapter

                    vp_product_images.offscreenPageLimit=3
                    vp_product_images.getChildAt(0).overScrollMode= RecyclerView.OVER_SCROLL_NEVER

                    vp_product_images.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)
                            sliderHandler.removeCallbacks(sliderRunnable)
                            sliderHandler.postDelayed(sliderRunnable,4000)
                        }
                    })

                    left_arrow.setOnClickListener {
                        vp_product_images.currentItem-=1
                    }
                    right_arrow.setOnClickListener {
                        vp_product_images.currentItem+=1
                    }

                    tv_title.text=product.title
                    tv_likes_count.text=product.favourites.toString()
                    tv_comments_count.text="("+dataDetails.commentCount.toString()+getString(R.string.comments)
                    tv_price.text=product.price.toString()+getString(R.string.currency)
                    tv_description.text=product.description
                    tv_categorie.text=product.categorie
                    tv_status.text=product.status

                    val numberOfColumns = 2
                    similarProductsList=dataDetails.similar_products
                    val similarProductsAdapter= ProductsRecyclerViewAdapter(similarProductsList)
                    rv_similar_products.layoutManager = GridLayoutManager(this@ProductDetailsActivity, numberOfColumns)
                    rv_similar_products.adapter=similarProductsAdapter

                    val id=user.id
                    receiverId=id
                    if(id == sessionManager.fetchUserId()!!.toInt()){
                        availability.visibility=View.VISIBLE
                        update_delete.visibility=View.VISIBLE
                    }
                    else{
                        btn_contact_owner.visibility=View.VISIBLE
                    }
                }

                if(pullToRefresh!=null){
                    pullToRefresh.isRefreshing = false
                }
                else{
                    hideProgressDialog()
                }
            }

            override fun onFailure(call: Call<ProductDetails>, t: Throwable) {
                println(t.message)
                if(pullToRefresh!=null){
                    pullToRefresh.isRefreshing = false
                }
                else{
                    hideProgressDialog()
                }
            }

        })
    }

    fun addLike( productId : Int){
        val apiInterface = ApiInterface.create().addLike("Bearer ${sessionManager.fetchAuthToken()}",productId)

        apiInterface.enqueue( object : Callback<UInt> {
            override fun onResponse(call: Call<UInt>, response: retrofit2.Response<UInt>) {
            }
            override fun onFailure(call: Call<UInt>, t: Throwable) {
            }
        })
    }

    fun addSave( productId : Int){
        val apiInterface = ApiInterface.create().addSave("Bearer ${sessionManager.fetchAuthToken()}",productId)

        apiInterface.enqueue( object : Callback<UInt> {
            override fun onResponse(call: Call<UInt>, response: retrofit2.Response<UInt>) {
            }
            override fun onFailure(call: Call<UInt>, t: Throwable) {
            }
        })
    }

    private fun deleteProduct(productId: Int){
        val apiInterface = ApiInterface.create().deleteProduct(productId)

        apiInterface.enqueue( object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: retrofit2.Response<Any>) {
            }
            override fun onFailure(call: Call<Any>, t: Throwable) {
            }
        })
    }

    private fun addMessage(message: String){
        val apiInterface = ApiInterface.create().addMessage(token = "Bearer ${sessionManager.fetchAuthToken()}",message,productId!!.toInt(),receiverId)

        apiInterface.enqueue( object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: retrofit2.Response<Any>) {
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
            }
        })
    }
}