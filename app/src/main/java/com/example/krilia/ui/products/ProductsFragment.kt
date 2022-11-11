@file:Suppress("DEPRECATION")

package com.example.krilia.ui.products

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.krilia.*
import com.example.krilia.Retrofit.ApiInterface
import com.example.krilia.Retrofit.SessionManager
import com.example.krilia.adapters.AllProductsRecyclerViewAdapter
import com.example.krilia.databinding.FragmentProductsBinding
import com.example.krilia.models.PItem
import com.example.krilia.models.ProductItem
import kotlinx.android.synthetic.main.filter_product.*
import kotlinx.android.synthetic.main.fragment_products.*
import retrofit2.Call
import retrofit2.Callback


class ProductsFragment : Fragment() {


//    lateinit var productsGRV: GridView
    lateinit var productsRecyclerView: RecyclerView
    lateinit var productsList: MutableList<PItem>
    private var _binding: FragmentProductsBinding? = null
    private lateinit var productsAdapter: AllProductsRecyclerViewAdapter

    private lateinit var sessionManager : SessionManager

    private val binding get() = _binding!!

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
        setHasOptionsMenu(true)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sessionManager=SessionManager(requireContext())

        productsRecyclerView=binding.rvProducts
        productsList=ArrayList()

        productsAdapter= AllProductsRecyclerViewAdapter(productsList)

        showProgressDialog(resources.getString(R.string.please_wait))
        loadProducts()

        return root
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
                    productsRecyclerView.layoutManager = GridLayoutManager(requireContext(), numberOfColumns)
                    productsRecyclerView.adapter=productsAdapter
                }
                if(pullToRefresh!=null){
                    pullToRefresh.isRefreshing = false
                }
                else{
                    hideProgressDialog()
                }
            }

            override fun onFailure(call: Call<ProductItem>, t: Throwable) {
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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                filter_options.visibility=View.VISIBLE
            }else{
                filter_options.visibility=View.GONE
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
            filter_options.visibility=View.GONE
        }

        val pullToRefresh: SwipeRefreshLayout =products_fragmentswipe
        pullToRefresh.setOnRefreshListener {
            loadProducts(pullToRefresh) // your code
        }
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_addProduct->{
                startActivity(Intent(activity, AddProductActivity::class.java))
                return true
            }
            R.id.action_likes->{
                startActivity(Intent(activity, LikedProductsActivity::class.java))
                return true
            }
            R.id.action_saved->{
                startActivity(Intent(activity, SavedProductsActivity::class.java))
                return true
            }
            R.id.action_aboutUs->{
                startActivity(Intent(activity, AboutUsActivity::class.java))
                return true
            }
            R.id.action_logout->{
                AlertDialog.Builder(context)
                    .setTitle("Logout")
                    .setMessage("Would you like to logout?")
                    .setPositiveButton("yes") { _, _ ->
                        sessionManager.deleteToken()
                        val i = Intent(requireContext(), LoginActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        startActivity(i)
                        requireActivity().finish()
                    }
                    .setNegativeButton("No") { _, _ ->
                    }
                    .show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private lateinit var mProgressDialog: Dialog
    fun showProgressDialog(text: String){
        mProgressDialog= Dialog(requireContext())

        mProgressDialog.setContentView(R.layout.dialog_progress)
        mProgressDialog.findViewById<TextView>(R.id.tv_progress_text).text=text

        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)

        mProgressDialog.show()
    }
    fun hideProgressDialog(){
        mProgressDialog.dismiss()
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

    fun apply_filter(){
        val apiInterface = ApiInterface.create().getAllProduct("Bearer ${sessionManager.fetchAuthToken()}",0,allStatus,available,notAvailable,allPrices,
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