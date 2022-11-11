@file:Suppress("DEPRECATION")
package com.example.krilia

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.krilia.Retrofit.ApiInterface
import com.example.krilia.Retrofit.SessionManager
import com.example.krilia.adapters.AllProductsRecyclerViewAdapter
import com.example.krilia.models.*
import com.example.krilia.utils.Constant
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.filter_product.*
import retrofit2.Call
import retrofit2.Callback

class ProfileActivity : BaseActivity() {
    lateinit var profile: User

    private lateinit var productsAdapter: AllProductsRecyclerViewAdapter
    lateinit var productsList: MutableList<PItem>

    private lateinit var sessionManager : SessionManager

    private var allStatus=true
    private var available=false
    private var notAvailable=false

    private var allPrices=true
    private var firstPrice=false
    private var secondPrice=false
    private var thirdPrice=false
    private var fourthPrice=false
    private var morePrice=false

    private var latest=false
    private var favourited=false
    private var lessprice=false

    private var chosenCategory=""

    private var userID: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val b = intent.extras
        userID=b!!.getString("userID")

        sessionManager=SessionManager(this@ProfileActivity)
        productsList=ArrayList()

        productsAdapter= AllProductsRecyclerViewAdapter(productsList)


        showProgressDialog(resources.getString(R.string.please_wait))
        loadProfile()

        loadProductsData()

        sv_products.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean { return false }
            override fun onQueryTextChange(newText: String?): Boolean {
                Searchfilter(newText!!)
                return false
            }
        })


        tv_filter_popular.setOnClickListener {
            if (tv_filter_popular.backgroundTintList == resources.getColorStateList(R.color.onPrimary)){
                tv_filter_popular.backgroundTintList=resources.getColorStateList(R.color.primary)
                tv_filter_less_price.backgroundTintList= resources.getColorStateList(R.color.onPrimary)
                tv_filter_last_added.backgroundTintList= resources.getColorStateList(R.color.onPrimary)
                favourited=true
                lessprice=false
                latest=false
                apply_filter()
            }else{
                tv_filter_popular.backgroundTintList= resources.getColorStateList(R.color.onPrimary)
                productsAdapter.filterList(productsList)
                favourited=false
            }
        }
        tv_filter_less_price.setOnClickListener {
            if (tv_filter_less_price.backgroundTintList == resources.getColorStateList(R.color.onPrimary)){
                tv_filter_less_price.backgroundTintList=resources.getColorStateList(R.color.primary)
                tv_filter_popular.backgroundTintList= resources.getColorStateList(R.color.onPrimary)
                tv_filter_last_added.backgroundTintList= resources.getColorStateList(R.color.onPrimary)
                lessprice=true
                latest=false
                favourited=false
                apply_filter()
            }else{
                tv_filter_less_price.backgroundTintList= resources.getColorStateList(R.color.onPrimary)
                productsAdapter.filterList(productsList)
                lessprice=false
            }
        }
        tv_filter_last_added.setOnClickListener {
            if (tv_filter_last_added.backgroundTintList == resources.getColorStateList(R.color.onPrimary)){
                tv_filter_last_added.backgroundTintList=resources.getColorStateList(R.color.primary)
                tv_filter_less_price.backgroundTintList= resources.getColorStateList(R.color.onPrimary)
                tv_filter_popular.backgroundTintList= resources.getColorStateList(R.color.onPrimary)
                latest=true
                favourited=false
                lessprice=false
                apply_filter()
            }else{
                tv_filter_last_added.backgroundTintList= resources.getColorStateList(R.color.onPrimary)
                productsAdapter.filterList(productsList)
                latest=false
            }
        }

        iv_filter.setOnClickListener {
            if(filter_options.visibility== View.GONE){
                filter_options.visibility= View.VISIBLE
            }else{
                filter_options.visibility= View.GONE
            }
        }

        tv_filter_all_prices.setOnClickListener {
            if(tv_filter_all_prices.backgroundTintList == resources.getColorStateList(R.color.onPrimary)){
                tv_filter_all_prices.backgroundTintList=resources.getColorStateList(R.color.primary)
                allPrices=true
                firstPrice=false
                tv_filter_firstprice.backgroundTintList = resources.getColorStateList(R.color.onPrimary)
                secondPrice=false
                tv_filter_secondprice.backgroundTintList = resources.getColorStateList(R.color.onPrimary)
                thirdPrice=false
                tv_filter_thirdprice.backgroundTintList = resources.getColorStateList(R.color.onPrimary)
                fourthPrice=false
                tv_filter_fourthprice.backgroundTintList = resources.getColorStateList(R.color.onPrimary)
                morePrice=false
                tv_filter_moreprice.backgroundTintList = resources.getColorStateList(R.color.onPrimary)
            }else{
                tv_filter_all_prices.backgroundTintList = resources.getColorStateList(R.color.onPrimary)
                allPrices=false
            }
        }

        tv_filter_firstprice.setOnClickListener {
            if(tv_filter_firstprice.backgroundTintList == resources.getColorStateList(R.color.onPrimary)){
                tv_filter_firstprice.backgroundTintList=resources.getColorStateList(R.color.primary)
                allPrices=false
                tv_filter_all_prices.backgroundTintList = resources.getColorStateList(R.color.onPrimary)
                firstPrice=true
            }else{
                tv_filter_firstprice.backgroundTintList = resources.getColorStateList(R.color.onPrimary)
                firstPrice=false
            }
        }

        tv_filter_secondprice.setOnClickListener {
            if(tv_filter_secondprice.backgroundTintList == resources.getColorStateList(R.color.onPrimary)){
                tv_filter_secondprice.backgroundTintList=resources.getColorStateList(R.color.primary)
                allPrices=false
                tv_filter_all_prices.backgroundTintList = resources.getColorStateList(R.color.onPrimary)
                secondPrice=true
            }else{
                tv_filter_secondprice.backgroundTintList = resources.getColorStateList(R.color.onPrimary)
                secondPrice=false
            }
        }

        tv_filter_thirdprice.setOnClickListener {
            if(tv_filter_thirdprice.backgroundTintList == resources.getColorStateList(R.color.onPrimary)){
                tv_filter_thirdprice.backgroundTintList=resources.getColorStateList(R.color.primary)
                allPrices=false
                tv_filter_all_prices.backgroundTintList = resources.getColorStateList(R.color.onPrimary)
                thirdPrice=true
            }else{
                tv_filter_thirdprice.backgroundTintList = resources.getColorStateList(R.color.onPrimary)
                thirdPrice=false
            }
        }

        tv_filter_fourthprice.setOnClickListener {
            if(tv_filter_fourthprice.backgroundTintList == resources.getColorStateList(R.color.onPrimary)){
                tv_filter_fourthprice.backgroundTintList=resources.getColorStateList(R.color.primary)
                allPrices=false
                tv_filter_all_prices.backgroundTintList = resources.getColorStateList(R.color.onPrimary)
                fourthPrice=true
            }else{
                tv_filter_fourthprice.backgroundTintList = resources.getColorStateList(R.color.onPrimary)
                fourthPrice=false
            }
        }

        tv_filter_moreprice.setOnClickListener {
            if(tv_filter_moreprice.backgroundTintList == resources.getColorStateList(R.color.onPrimary)){
                tv_filter_moreprice.backgroundTintList=resources.getColorStateList(R.color.primary)
                allPrices=false
                tv_filter_all_prices.backgroundTintList = resources.getColorStateList(R.color.onPrimary)
                morePrice=true
            }else{
                tv_filter_moreprice.backgroundTintList = resources.getColorStateList(R.color.onPrimary)
                morePrice=false
            }
        }

        tv_filter_all_status.setOnClickListener {
            if(tv_filter_all_status.backgroundTintList == resources.getColorStateList(R.color.onPrimary)){
                tv_filter_all_status.backgroundTintList=resources.getColorStateList(R.color.primary)
                allStatus=true
                available=false
                tv_filter_available_status.backgroundTintList = resources.getColorStateList(R.color.onPrimary)
                notAvailable=false
                tv_filter_notavailable_status.backgroundTintList = resources.getColorStateList(R.color.onPrimary)
            }else{
                tv_filter_all_status.backgroundTintList = resources.getColorStateList(R.color.onPrimary)
                allStatus=false
            }
        }

        tv_filter_available_status.setOnClickListener {
            if(tv_filter_available_status.backgroundTintList == resources.getColorStateList(R.color.onPrimary)){
                tv_filter_available_status.backgroundTintList=resources.getColorStateList(R.color.primary)
                available=true
                allStatus=false
                tv_filter_all_status.backgroundTintList = resources.getColorStateList(R.color.onPrimary)
            }else{
                tv_filter_available_status.backgroundTintList = resources.getColorStateList(R.color.onPrimary)
                available=false
            }
        }

        tv_filter_notavailable_status.setOnClickListener {
            if(tv_filter_notavailable_status.backgroundTintList == resources.getColorStateList(R.color.onPrimary)){
                tv_filter_notavailable_status.backgroundTintList=resources.getColorStateList(R.color.primary)
                notAvailable=true
                allStatus=false
                tv_filter_all_status.backgroundTintList = resources.getColorStateList(R.color.onPrimary)
            }else{
                tv_filter_notavailable_status.backgroundTintList = resources.getColorStateList(R.color.onPrimary)
                notAvailable=false
            }
        }

        btn_apply_filter.setOnClickListener {
            chosenCategory=et_product_category.selectedItem.toString()
            if(chosenCategory=="All"){
                chosenCategory=""
            }

            apply_filter()
            filter_options.visibility= View.GONE
        }

        val pullToRefresh: SwipeRefreshLayout=profileSwipe
        pullToRefresh.setOnRefreshListener {
            loadProfile(pullToRefresh)
        }
    }

    private fun loadProfile(pullToRefresh: SwipeRefreshLayout?=null){
        val apiInterface = ApiInterface.create().getProfile(userID!!)

        apiInterface.enqueue( object : Callback<Profile> {
            override fun onResponse(call: Call<Profile>, response: retrofit2.Response<Profile>) {

                if(response.body() != null){
                    val profiledata: ProfileData =response.body()!!.data
                    profile=profiledata.authuser
                    name.text = profile.name
                    products_count.text=profiledata.totalProducts.toString()
                    tv_email.text=profile.email
                    tv_address.text=profile.location
                    tv_country.text=profile.country
                    tv_phone.text=profile.phone
                    Picasso.get().load(Constant.storage+profile.image).into(profile_image)
                }
                if(pullToRefresh!=null){
                    pullToRefresh.isRefreshing = false
                }
                else{
                    hideProgressDialog()
                }
            }

            override fun onFailure(call: Call<Profile>, t: Throwable) {
                if(pullToRefresh!=null){
                    pullToRefresh.isRefreshing = false
                }
                else{
                    hideProgressDialog()
                }
            }
        })
    }

    fun Searchfilter(text: String){
        val filteredItems: MutableList<PItem> = ArrayList()
        for(item in productsList){
            if(item.title.contains(text,true)){
                filteredItems.add(item)
            }
        }
        productsAdapter.filterList(filteredItems)
    }

    private fun loadProductsData(){
        val apiInterface = ApiInterface.create().getAllProduct("Bearer ${sessionManager.fetchAuthToken()}",userID!!.toInt(),true,false,false,true,
            false,false,false,false,false,false,false,false,"")

        apiInterface.enqueue( object : Callback<ProductItem> {
            override fun onResponse(call: Call<ProductItem>, response: retrofit2.Response<ProductItem>) {

                if(response.body() != null){
                    productsList.clear()
                    productsList.addAll(response.body()!!.data.toMutableList())
                    productsAdapter.notifyDataSetChanged()

                    val numberOfColumns = 1
                    rv_products.layoutManager = GridLayoutManager(this@ProfileActivity, numberOfColumns)
                    rv_products.adapter=productsAdapter

                    rv_products.isNestedScrollingEnabled=false

                }
            }

            override fun onFailure(call: Call<ProductItem>, t: Throwable) {
            }
        })
    }

    private fun apply_filter(){
        val apiInterface = ApiInterface.create().getAllProduct("Bearer ${sessionManager.fetchAuthToken()}",userID!!.toInt(),allStatus,available,notAvailable,allPrices,
            firstPrice,secondPrice,thirdPrice, fourthPrice, morePrice, latest, favourited, lessprice, chosenCategory)

        apiInterface.enqueue( object : Callback<ProductItem> {
            override fun onResponse(call: Call<ProductItem>, response: retrofit2.Response<ProductItem>) {
                if(response.body() != null){
                    productsAdapter.filterList(response.body()!!.data.toMutableList())
                }
            }
            override fun onFailure(call: Call<ProductItem>, t: Throwable) {}
        })
    }
}