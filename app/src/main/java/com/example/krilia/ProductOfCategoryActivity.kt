@file:Suppress("DEPRECATION")

package com.example.krilia

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.krilia.Retrofit.ApiInterface
import com.example.krilia.Retrofit.SessionManager
import com.example.krilia.adapters.AllProductsRecyclerViewAdapter
import com.example.krilia.models.PItem
import com.example.krilia.models.ProductItem
import kotlinx.android.synthetic.main.activity_product_of_category.*
import kotlinx.android.synthetic.main.filter_product.*
import retrofit2.Call
import retrofit2.Callback

class ProductOfCategoryActivity : BaseActivity() {
    lateinit var productsRecyclerView: RecyclerView
    lateinit var productsList: MutableList<PItem>

    private lateinit var productsAdapter: AllProductsRecyclerViewAdapter

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_of_category)

        sessionManager=SessionManager(this@ProductOfCategoryActivity)

        productsRecyclerView=rv_products
        productsList=ArrayList()

        productsRecyclerView.visibility=View.GONE
        productsAdapter= AllProductsRecyclerViewAdapter(productsList)

        showProgressDialog(resources.getString(R.string.please_wait))
        loadProducts()
        val b = intent.extras
        if(b!!.getString("category")!= null){
            chosenCategory=b.getString("category")!!
            var index=0
            for(i in 0 until et_product_category.count){
                if(et_product_category.getItemAtPosition(i).toString().equals(chosenCategory,ignoreCase = true)){
                    index=i
                    break
                }
            }
            et_product_category.setSelection(index)
        }else if(b.getString("popular")!= null){
            favourited=true
            tv_filter_popular.backgroundTintList=resources.getColorStateList(R.color.primary)
        }
        else if(b.getString("last")!= null){
            latest=true
            tv_filter_last_added.backgroundTintList=resources.getColorStateList(R.color.primary)
        }


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

        val pullToRefresh: SwipeRefreshLayout =products_of_category_swipe
        pullToRefresh.setOnRefreshListener {
            loadProducts(pullToRefresh) // your code
        }
    }

    fun loadProducts(pullToRefresh: SwipeRefreshLayout?=null){
        val apiInterface = ApiInterface.create().getAllProduct("Bearer ${sessionManager.fetchAuthToken()}",0,true,false,false,true,
            false,false,false,false,false,false,false,false,"")

        apiInterface.enqueue( object : Callback<ProductItem> {
            override fun onResponse(call: Call<ProductItem>, response: retrofit2.Response<ProductItem>) {

                if(response.body() != null){
                    productsList.clear()
                    productsList.addAll(response.body()!!.data.toMutableList())
                    productsAdapter.notifyDataSetChanged()

                    val numberOfColumns = 1
                    productsRecyclerView.layoutManager = GridLayoutManager(this@ProductOfCategoryActivity, numberOfColumns)
                    productsRecyclerView.adapter=productsAdapter
                    apply_filter(pullToRefresh)
                }
            }

            override fun onFailure(call: Call<ProductItem>, t: Throwable) {}
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

    fun apply_filter(pullToRefresh: SwipeRefreshLayout?=null){
        val apiInterface = ApiInterface.create().getAllProduct("Bearer ${sessionManager.fetchAuthToken()}",0,allStatus,available,notAvailable,allPrices,
            firstPrice,secondPrice,thirdPrice, fourthPrice, morePrice, latest, favourited, lessprice, chosenCategory)

        apiInterface.enqueue( object : Callback<ProductItem> {
            override fun onResponse(call: Call<ProductItem>, response: retrofit2.Response<ProductItem>) {
                if(response.body() != null){
                    productsAdapter.filterList(response.body()!!.data.toMutableList())
                    productsRecyclerView.visibility=View.VISIBLE
                }
                if(pullToRefresh!=null){
                    pullToRefresh.isRefreshing = false
                }
                else{
                    hideProgressDialog()
                }
            }
            override fun onFailure(call: Call<ProductItem>, t: Throwable) {
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