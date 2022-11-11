package com.example.krilia

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.krilia.Retrofit.ApiFuctions
import com.example.krilia.Retrofit.ApiInterface
import com.example.krilia.Retrofit.SessionManager
import com.example.krilia.models.Profile
import com.example.krilia.models.ProfileData
import com.example.krilia.models.User
import com.example.krilia.utils.Constant
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_edit_profile.profile_image
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import java.io.File

class EditProfileActivity : BaseActivity() {
    lateinit var profile: User

    private var Read_Permission=101
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private lateinit var sessionManager : SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        sessionManager=SessionManager(this@EditProfileActivity)
        showProgressDialog(resources.getString(R.string.please_wait))
        loadProfileDetails()


        activityResultLauncher=registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK &&  result.data != null) {
                if (result.data!!.data != null) {
                    val uri=result.data!!.data
                    profile_image.setImageURI(uri)
                    profile_image.tag=uri
                }
            }
        }

        profile_image.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this@EditProfileActivity,android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this@EditProfileActivity, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),Read_Permission)
                return@setOnClickListener
            }

            val intent=Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false)
            intent.action=Intent.ACTION_GET_CONTENT
            activityResultLauncher.launch(intent)
        }

        btn_update_profile.setOnClickListener {
            if(validateRegisterDetails()){
                val name=et_user_name.text.toString()
                val country=et_user_country.text.toString()
                val phone=et_user_phone.text.toString()
                val location=et_user_address.text.toString()
                val email=et_user_email.text.toString()


                val namebody =RequestBody.create(MediaType.parse("text/plain"),name)
                val countrybody =RequestBody.create(MediaType.parse("text/plain"),country)
                val phonebody =RequestBody.create(MediaType.parse("text/plain"),phone)
                val locationbody =RequestBody.create(MediaType.parse("text/plain"),location)
                val emailbody =RequestBody.create(MediaType.parse("text/plain"),email)

                val map: MutableMap<String, RequestBody> = mutableMapOf()
                map.put("name",namebody)
                map.put("country",countrybody)
                map.put("phone",phonebody)
                map.put("location",locationbody)
                map.put("email",emailbody)

                if(profile_image.tag == null){
                    updateProfile(map)
                }
                else{
                    val imageTag: Uri = profile_image.tag as Uri
                    val file = File(ApiFuctions.getPath(this,imageTag)!!)
                    val imageBody=RequestBody.create(MediaType.parse("image/*"), file)
                    val multipartImage: MultipartBody.Part? = MultipartBody.Part.createFormData("image", file.getName(), imageBody)
                    updateProfile(map,multipartImage)
                }
            }
        }

        val pullToRefresh: SwipeRefreshLayout =edit_profileSwipe
        pullToRefresh.setOnRefreshListener {
            loadProfileDetails(pullToRefresh) // your code
        }
    }

    fun loadProfileDetails(pullToRefresh: SwipeRefreshLayout?=null){
        val apiInterface = ApiInterface.create().getProfile(sessionManager.fetchUserId()!!)

        apiInterface.enqueue( object : Callback<Profile> {
            override fun onResponse(call: Call<Profile>, response: retrofit2.Response<Profile>) {

                if(response.body() != null){
                    val profiledata: ProfileData =response.body()!!.data
                    profile=profiledata.authuser
                    et_user_name.setText(profile.name)
                    et_user_email.setText(profile.email)
                    et_user_address.setText(profile.location)
                    et_user_country.setText(profile.country)
                    et_user_phone.setText(profile.phone)
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
    fun updateProfile(map: MutableMap<String, RequestBody>, image: MultipartBody.Part? = null){
        showProgressDialog(resources.getString(R.string.please_wait))
        val apiInterface = ApiInterface.create().updateProfile(sessionManager.fetchUserId()!!,map, image)

        apiInterface.enqueue( object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: retrofit2.Response<Any>) {
                hideProgressDialog()
                showErrorSnackbar("Your profile was updated successfully",false)
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                hideProgressDialog()
                showErrorSnackbar("error connecting to the server",true)
            }
        })
    }

    private fun validateRegisterDetails():Boolean{
        return when{
            TextUtils.isEmpty(et_user_name.text.toString().trim{it <=' '}) -> {
                showErrorSnackbar(resources.getString(R.string.err_msg_enter_name), true)
                false
            }
            TextUtils.isEmpty(et_user_email.text.toString().trim{it <=' '}) -> {
                showErrorSnackbar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(et_user_address.text.toString().trim{it <=' '}) -> {
                showErrorSnackbar(resources.getString(R.string.err_msg_enter_location), true)
                false
            }
            TextUtils.isEmpty(et_user_country.text.toString().trim{it <=' '}) -> {
                showErrorSnackbar(resources.getString(R.string.err_msg_enter_country), true)
                false
            }
            TextUtils.isEmpty(et_user_phone.text.toString().trim{it <=' '}) -> {
                showErrorSnackbar(resources.getString(R.string.err_msg_enter_phone), true)
                false
            }
            else->{
                true
            }
        }
    }
}