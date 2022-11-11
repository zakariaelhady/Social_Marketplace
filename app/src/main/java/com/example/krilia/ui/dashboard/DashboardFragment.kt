package com.example.krilia.ui.dashboard

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.krilia.*
import com.example.krilia.Retrofit.ApiInterface
import com.example.krilia.Retrofit.SessionManager
import com.example.krilia.adapters.CategoryRecyclerViewAdapter
import com.example.krilia.adapters.ProductsRecyclerViewAdapter
import com.example.krilia.databinding.FragmentDashboardBinding
import com.example.krilia.models.Categories
import com.example.krilia.models.Category
import com.example.krilia.models.Product
import com.example.krilia.models.ProductModel
import kotlinx.android.synthetic.main.fragment_dashboard.*
import retrofit2.Call
import retrofit2.Callback
import kotlin.math.abs


class DashboardFragment : Fragment() {
    lateinit var productsFRecyclerView: RecyclerView
    lateinit var productsFList: List<Product>
    lateinit var categoriesList: List<Category>

    lateinit var productsLRecyclerView: RecyclerView
    lateinit var productsLList: List<Product>


    private var _binding: FragmentDashboardBinding? = null


    private val binding get() = _binding!!
    private var sliderHandler=Handler(Looper.getMainLooper())

    private lateinit var sessionManager : SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
//        (activity as AppCompatActivity).supportActionBar?.title = "Example 1"

        sessionManager=SessionManager(requireContext())

        productsFRecyclerView=binding.rvFavouritedProducts
        productsFRecyclerView.isNestedScrollingEnabled = false
        productsFList=ArrayList()
        productsLRecyclerView=binding.rvLastProducts
        productsLRecyclerView.isNestedScrollingEnabled = false
        productsLList=ArrayList()

        categoriesList=ArrayList()

        showProgressDialog(resources.getString(R.string.please_wait))
        loadCategoriesData()
        loadProductsData()

        return root
    }

    var sliderRunnable: Runnable= Runnable {
        run {
            vp_categories.currentItem+=1
        }
    }

    override fun onPause(){
        super.onPause()
        sliderHandler.removeCallbacks(sliderRunnable)
    }

    override fun onResume() {
        super.onResume()
        sliderHandler.postDelayed(sliderRunnable,2000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)

        vp_categories.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable,2000)
            }
        })

        val pullToRefresh: SwipeRefreshLayout = dashboard_swipe
        pullToRefresh.setOnRefreshListener {
            loadCategoriesData()
            loadProductsData(pullToRefresh) // your code
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_addProduct->{
                startActivity(Intent(activity,AddProductActivity::class.java))
//                requireActivity().finish()
                return true
            }
            R.id.action_likes->{
                startActivity(Intent(activity,LikedProductsActivity::class.java))
                return true
            }
            R.id.action_saved->{
                startActivity(Intent(activity,SavedProductsActivity::class.java))
                return true
            }
            R.id.action_aboutUs->{
                startActivity(Intent(activity,AboutUsActivity::class.java))
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        popular_products.setOnClickListener {
            val intent= Intent(requireContext(), ProductOfCategoryActivity::class.java)
            val b = Bundle()
            b.putString("popular","yes" )
            intent.putExtras(b)
            startActivity(intent)
        }

        last_arrived_products.setOnClickListener {
            val intent= Intent(requireContext(), ProductOfCategoryActivity::class.java)
            val b = Bundle()
            b.putString("last","yes" )
            intent.putExtras(b)
            startActivity(intent)
        }
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
    private fun loadCategoriesData(){
        val apiInterface2 = ApiInterface.create().getCategories()

        apiInterface2.enqueue( object : Callback<Categories> {
            override fun onResponse(call: Call<Categories>, response: retrofit2.Response<Categories>) {

                if(response.body() != null){
                    categoriesList=response.body()!!.data
                    val filteredItems = categoriesList.filter { it.image != null }
//                    Log.d("response",filteredItems.count().toString());
                    val categoriesAdapter= CategoryRecyclerViewAdapter(filteredItems,vp_categories)
                    vp_categories.adapter=categoriesAdapter

                    vp_categories.offscreenPageLimit=3
                    vp_categories.clipChildren=false
                    vp_categories.clipToPadding=false
                    vp_categories.getChildAt(0).overScrollMode=RecyclerView.OVER_SCROLL_NEVER

                    val transformer=CompositePageTransformer()
                    transformer.addTransformer(MarginPageTransformer(40))
                    transformer.addTransformer { page, position ->
                        val r = 1 - abs(position)
                        page.scaleY = 0.85f + r * 0.14f
                    }
                    vp_categories.setPageTransformer(transformer)


                }
                hideProgressDialog()
            }

            override fun onFailure(call: Call<Categories>, t: Throwable) {
                println(t.message)
                hideProgressDialog()
            }

        })
    }
    private fun loadProductsData(pullToRefresh: SwipeRefreshLayout?=null) {
        val numberOfColumns = 2

        val apiInterface = ApiInterface.create().getProducts()
        if(pullToRefresh!=null){
            pullToRefresh.isRefreshing = true
        }
        apiInterface.enqueue( object : Callback<ProductModel> {
            override fun onResponse(call: Call<ProductModel>, response: retrofit2.Response<ProductModel>) {

                if(response.body() != null){
                    if(response.body()!!.data!=null && response.body()!!.data.trendProducts!=null){
                        productsFList=response.body()!!.data.trendProducts
                        productsLList=response.body()!!.data.lastarrived
                    }
                    val productsFAdapter= ProductsRecyclerViewAdapter(productsFList)
                    productsFRecyclerView.layoutManager = GridLayoutManager(requireContext(), numberOfColumns)
                    productsFRecyclerView.adapter=productsFAdapter

                    val productsLAdapter= ProductsRecyclerViewAdapter(productsLList)
                    productsLRecyclerView.layoutManager = GridLayoutManager(requireContext(), numberOfColumns)
                    productsLRecyclerView.adapter=productsLAdapter
                }
                if(pullToRefresh!=null){
                    pullToRefresh.isRefreshing = false
                }
                else{
                    hideProgressDialog()
                }
            }

            override fun onFailure(call: Call<ProductModel>, t: Throwable) {
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

}