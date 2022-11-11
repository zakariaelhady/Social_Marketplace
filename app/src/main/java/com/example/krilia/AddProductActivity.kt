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
import com.example.krilia.Retrofit.ApiFuctions
import com.example.krilia.Retrofit.ApiInterface
import com.example.krilia.Retrofit.SessionManager
import com.example.krilia.adapters.PickedImagesRecyclerViewAdapter
import com.example.krilia.models.createProductData
import kotlinx.android.synthetic.main.activity_add_product.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import java.io.File


class AddProductActivity : BaseActivity() {
    private  var imageList: MutableList<Uri> = ArrayList()
    private var Read_Permission=101
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private lateinit var sessionManager: SessionManager

    private var imagesAdapter: PickedImagesRecyclerViewAdapter=PickedImagesRecyclerViewAdapter(imageList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        sessionManager=SessionManager(this)

        val numberOfColumns = 3
        rv_product_images.layoutManager = GridLayoutManager(this@AddProductActivity, numberOfColumns)

//        val imagesAdapter=PickedImagesRecyclerViewAdapter(imageList)
        rv_product_images.adapter=imagesAdapter
        rv_product_images.isNestedScrollingEnabled=false

        activityResultLauncher=registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK &&  result.data != null) {
                if (result.data!!.clipData != null) {
                    val n = result.data!!.clipData!!.itemCount
//                    imageList.clear()
                    for (i in 0 until n) {
                        if (imageList.size < 11) {
                            imageList.add(result.data!!.clipData!!.getItemAt(i).uri)
                        } else {
                            Toast.makeText(
                                this@AddProductActivity,
                                "You are not allowed to pick more than 10 images",
                                Toast.LENGTH_LONG
                            ).show()
                            break
                        }
                    }
                }
                else{
                    if (imageList.size < 11) {
//                        val selectedImageUri = result.data!!.data!!.path
                        val uri=result.data!!.data
                        if (uri != null) {
                            imageList.add(uri)
                        }
//                        imageList.add(Uri.parse(selectedImageUri))
                    } else {
                        Toast.makeText(
                            this@AddProductActivity,
                            "You are not allowed to pick more than 10 images",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                imagesAdapter.notifyDataSetChanged()

            } else {
                Toast.makeText(
                    this@AddProductActivity,
                    "You didn't pick any image",
                    Toast.LENGTH_LONG
                ).show()
            }
        }


        btn_pick_image.setOnClickListener{
            if (ContextCompat.checkSelfPermission(this@AddProductActivity,android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this@AddProductActivity, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),Read_Permission)
                return@setOnClickListener
            }

            val intent=Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
            intent.action=Intent.ACTION_GET_CONTENT
//            startActivityForResult(Intent.createChooser(intent,"Select Pictures"),Pick_Image)
            activityResultLauncher.launch(intent)
        }

        btn_add_product.setOnClickListener {
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

                createProduct(map,uriToPart(imageList))
            }
        }
    }

    fun createProduct(map: MutableMap<String, RequestBody>,images: List<MultipartBody.Part>){
        showProgressDialog(resources.getString(R.string.please_wait))
        val apiInterface = ApiInterface.create().createProduct("Bearer ${sessionManager.fetchAuthToken()}",map, images)

        apiInterface.enqueue( object : Callback<createProductData> {
            override fun onResponse(call: Call<createProductData>, response: retrofit2.Response<createProductData>) {

                if(response.body() != null){
                    val dataResponse = response.body()!!.data

                    if (response.body()!!.success && dataResponse != null) {
                        val intent=Intent(this@AddProductActivity, ProductDetailsActivity::class.java)
                        val b = Bundle()
                        b.putString("productId", dataResponse.toString()) //Your id
                        b.putString("productCreated","c")
                        intent.putExtras(b)
                        hideProgressDialog()
                        startActivity(intent)
                        finish()
                    } else {
                        hideProgressDialog()
                        showErrorSnackbar("not succeed",true)
                    }
                }else{
                    hideProgressDialog()
                    showErrorSnackbar("something went wrong",true)
                }
            }

            override fun onFailure(call: Call<createProductData>, t: Throwable) {
                println(t.message)
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
            imageList.count() == 0 -> {
                showErrorSnackbar("upload at least one image for the product", true)
                false
            }
            else->{
                true
            }
        }
    }


}