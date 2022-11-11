package com.example.krilia

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.krilia.Retrofit.ApiInterface
import com.example.krilia.Retrofit.SessionManager
import com.example.krilia.adapters.LikedProductsRecyclerViewAdapter
import com.example.krilia.models.ProductLiKedOrSaved
import com.example.krilia.models.ProductLiKedOrSaveddata
import kotlinx.android.synthetic.main.activity_liked_products.*
import retrofit2.Call
import retrofit2.Callback


class LikedProductsActivity : BaseActivity() {
    lateinit var productsList: MutableList<ProductLiKedOrSaved>

    private lateinit var sessionManager : SessionManager
    private lateinit var productsAdapter : LikedProductsRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liked_products)
        sessionManager=SessionManager(this@LikedProductsActivity)

        likes_swipe.setOnRefreshListener {
            loadData(likes_swipe)
        }
        rv_liked_products.isNestedScrollingEnabled = false
        productsList=ArrayList()
        showProgressDialog(resources.getString(R.string.please_wait))
        loadData()
    }

    private fun loadData(pullToRefresh: SwipeRefreshLayout?=null){
        val apiInterface = ApiInterface.create().getLikedOrSavedProducts("Bearer ${sessionManager.fetchAuthToken()}",1,0)

        apiInterface.enqueue( object : Callback<ProductLiKedOrSaveddata> {
            override fun onResponse(call: Call<ProductLiKedOrSaveddata>, response: retrofit2.Response<ProductLiKedOrSaveddata>) {
                if(response.body() != null){
                    productsList=response.body()!!.data
//                    Log.d("response", response.body().toString())
                    productsAdapter= LikedProductsRecyclerViewAdapter(productsList,"like")

                    val numberOfColumns = 1
                    rv_liked_products.layoutManager = GridLayoutManager(this@LikedProductsActivity, numberOfColumns)
                    rv_liked_products.adapter=productsAdapter
                }
                if(productsList.count()==0){
                    tv_no_liked_products_msg.visibility=View.VISIBLE
                    rv_liked_products.visibility=View.INVISIBLE
                }
                else{
                    tv_no_liked_products_msg.visibility=View.INVISIBLE
                    rv_liked_products.visibility=View.VISIBLE
                }

                if(pullToRefresh!=null){
                    pullToRefresh.isRefreshing = false
                }
                else{
                    hideProgressDialog()
                }
            }
            override fun onFailure(call: Call<ProductLiKedOrSaveddata>, t: Throwable) {
                println(t.message)
                if(productsList.count()==0){
                    tv_no_liked_products_msg.visibility=View.VISIBLE
                    rv_liked_products.visibility=View.INVISIBLE
                }
                else{
                    tv_no_liked_products_msg.visibility=View.INVISIBLE
                    rv_liked_products.visibility=View.VISIBLE
                }

                if(pullToRefresh!=null){
                    pullToRefresh.isRefreshing = false
                }
                else{
                    hideProgressDialog()
                }
            }
        })
    }

}
