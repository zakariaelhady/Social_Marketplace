package com.example.krilia

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.krilia.Retrofit.ApiFuctions
import com.example.krilia.Retrofit.ApiInterface
import com.example.krilia.Retrofit.SessionManager
import com.example.krilia.adapters.LoadedImagesRecyclerViewAdapter
import com.example.krilia.adapters.PickedImagesRecyclerViewAdapter
import com.example.krilia.models.ProductDetails
import kotlinx.android.synthetic.main.activity_edit_product.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import java.io.File


class EditProductActivity : BaseActivity() {
    private  var imageList: MutableList<Uri> = ArrayList()
    private  var LoadedimageList: MutableList<String> = ArrayList()
    private var Read_Permission=101
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    var productId: String?="0"

    private lateinit var sessionManager: SessionManager

    private var loadedimagesAdapter: LoadedImagesRecyclerViewAdapter=LoadedImagesRecyclerViewAdapter(LoadedimageList)
    private var imagesAdapter: PickedImagesRecyclerViewAdapter=PickedImagesRecyclerViewAdapter(imageList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_product)

        val b = intent.extras
        productId=b!!.getString("productId")

        sessionManager=SessionManager(this)

        val numberOfColumns = 3
        rv_product_images.layoutManager = GridLayoutManager(this@EditProductActivity, numberOfColumns)
        rv_product_loadedimages.layoutManager = GridLayoutManager(this@EditProductActivity, numberOfColumns)

        rv_product_images.adapter=imagesAdapter
        rv_product_images.isNestedScrollingEnabled=false

        rv_product_loadedimages.adapter=loadedimagesAdapter
        rv_product_loadedimages.isNestedScrollingEnabled=false

        showProgressDialog(resources.getString(R.string.please_wait))
        loadProductData()

        activityResultLauncher=registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK &&  result.data != null) {
                if (result.data!!.clipData != null) {
                    val n = result.data!!.clipData!!.itemCount
                    for (i in 0 until n) {
                        if ((imageList.size + LoadedimageList.size) < 10) {
                            imageList.add(result.data!!.clipData!!.getItemAt(i).uri)
                        } else {
                            Toast.makeText(
                                this@EditProductActivity,
                                "You are not allowed to pick more than 10 images",
                                Toast.LENGTH_LONG
                            ).show()
                            break
                        }
                    }
                }
                else{
                    if ((imageList.size + LoadedimageList.size) < 10) {
                        val uri=result.data!!.data
                        if (uri != null) {
                            imageList.add(uri)
                        }
                    } else {
                        Toast.makeText(
                            this@EditProductActivity,
                            "You are not allowed to pick more than 10 images",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                imagesAdapter.notifyDataSetChanged()

            } else {
                Toast.makeText(
                    this@EditProductActivity,
                    "You didn't pick any image",
                    Toast.LENGTH_LONG
                ).show()
            }
        }


        btn_pick_image.setOnClickListener{
            if (ContextCompat.checkSelfPermission(this@EditProductActivity,android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this@EditProductActivity, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),Read_Permission)
                return@setOnClickListener
            }

            val intent=Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
            intent.action=Intent.ACTION_GET_CONTENT
//            startActivityForResult(Intent.createChooser(intent,"Select Pictures"),Pick_Image)
            activityResultLauncher.launch(intent)
        }

        btn_update_product.setOnClickListener {
            if(verifyAllDAtaFilled()){
                val title=et_title.text.toString()
                val description=et_product_desc.text.toString()
                val price=et_product_price.text.toString()
                val categorie=et_product_category.selectedItem.toString()

                val titlebody =RequestBody.create(MediaType.parse("text/plain"),title)
                val descriptionbody =RequestBody.create(MediaType.parse("text/plain"),description)
                val pricebody =RequestBody.create(MediaType.parse("text/plain"),price)
                val categoriebody =RequestBody.create(MediaType.parse("text/plain"),categorie)

                val map: MutableMap<String, RequestBody> = mutableMapOf()
                map.put("title",titlebody)
                map.put("description",descriptionbody)
                map.put("price",pricebody)
                map.put("categorie",categoriebody)

                if(LoadedimageList.count() == 0){
                    val imageBody=RequestBody.create(MediaType.parse("text/plain")," ")
                    map.put("oldImages["+0+"]", imageBody);
                }else{
                    for (i in 0 until LoadedimageList.count()){
                        val imagesBody=RequestBody.create(MediaType.parse("text/plain"),LoadedimageList[i])
                        map.put("oldImages["+i+"]", imagesBody);
                    }
                }


                if(imageList.count()==0){
                    createProduct(map)
                }else {
                    createProduct(map, uriToPart(imageList))
                }
            }
        }

        val pullToRefresh: SwipeRefreshLayout=edit_profile_swipe
        pullToRefresh.setOnRefreshListener {
            loadProductData(pullToRefresh)
        }
    }

    fun loadProductData(pullToRefresh: SwipeRefreshLayout?=null){
        val apiInterface = ApiInterface.create().getProductDetails(productId!!)

        apiInterface.enqueue( object : Callback<ProductDetails> {
            override fun onResponse(call: Call<ProductDetails>, response: retrofit2.Response<ProductDetails>) {

                if(response.body() != null){
                    val dataDetails=response.body()!!.data
                    val product =dataDetails.product

                    LoadedimageList=product.images.toMutableList()

                    loadedimagesAdapter=LoadedImagesRecyclerViewAdapter(LoadedimageList)
                    val numberOfColumns = 3
                    rv_product_loadedimages.layoutManager = GridLayoutManager(this@EditProductActivity, numberOfColumns)
                    rv_product_loadedimages.adapter=loadedimagesAdapter
                    rv_product_loadedimages.isNestedScrollingEnabled=false

                    et_title.setText(product.title)
                    et_product_price.setText(product.price.toString())
                    et_product_desc.setText(product.description)

                    var index =0
                    for(i in 0 until et_product_category.count){
                        if(et_product_category.getItemAtPosition(i).toString().equals(product.categorie,ignoreCase = true)){
                            index=i
                            break
                        }
                    }
                    et_product_category.setSelection(index)
                }
                if(pullToRefresh!=null){
                    pullToRefresh.isRefreshing = false
                }
                else{
                    hideProgressDialog()
                }
            }

            override fun onFailure(call: Call<ProductDetails>, t: Throwable) {
                if(pullToRefresh!=null){
                    pullToRefresh.isRefreshing = false
                }
                else{
                    hideProgressDialog()
                }
            }

        })
    }

    fun createProduct(map: MutableMap<String, RequestBody>,images: List<MultipartBody.Part>? = null){
        showProgressDialog(resources.getString(R.string.please_wait))
        val apiInterface = ApiInterface.create().updateProduct(productId!!.toInt(),map, images)

        apiInterface.enqueue( object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: retrofit2.Response<Any>) {
                hideProgressDialog()
                showErrorSnackbar("The product was updated successfully",false)
            }
            override fun onFailure(call: Call<Any>, t: Throwable) {
                hideProgressDialog()
                showErrorSnackbar("error connecting to the server",true)
            }
        })
    }

    fun uriToPart(imagesList : MutableList<Uri>) : List<MultipartBody.Part>{
        var partList: List<MultipartBody.Part> = ArrayList()
        for (i in 0 until imagesList.count()){
            val file = File(ApiFuctions.getPath(this, imagesList[i])!!)
            val imageBody=RequestBody.create(MediaType.parse("image/*"), file)
            val multipartImage: MultipartBody.Part? = MultipartBody.Part.createFormData("image[]", file.getName(), imageBody)
            partList=partList+multipartImage!!
        }
        return partList
    }

    fun verifyAllDAtaFilled(): Boolean{
        return when{
            TextUtils.isEmpty(et_title.text.toString().trim{it <=' '}) -> {
                showErrorSnackbar("enter the product title", true)
                false
            }
            TextUtils.isEmpty(et_product_price.text.toString().trim{it <=' '}) -> {
                showErrorSnackbar("enter the product price", true)
                false
            }
            imageList.count() == 0 && LoadedimageList.count() == 0 -> {
                showErrorSnackbar("upload at least one image for the product", true)
                false
            }
            else->{
                true
            }
        }
    }


}