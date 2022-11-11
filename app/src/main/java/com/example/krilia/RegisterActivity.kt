package com.example.krilia

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.krilia.Retrofit.ApiFuctions
import com.example.krilia.Retrofit.ApiInterface
import com.example.krilia.Retrofit.SessionManager
import com.example.krilia.models.RegisterData
import kotlinx.android.synthetic.main.activity_register.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import java.io.File


class RegisterActivity : BaseActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var Read_Permission=101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        sessionManager=SessionManager(this)

        //hide statusBar
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        tv_login.setOnClickListener {
            val i = Intent(this@RegisterActivity, LoginActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(i)
            finish()
        }

        val btnRegister = findViewById<AppCompatButton>(R.id.btn_register)
        btnRegister.setOnClickListener{
            if(validateRegisterDetails()){
                val name=et_register_name.text.toString()
                val country=et_register_country.text.toString()
                val phone=et_register_phone.text.toString()
                val location=et_register_address.text.toString()
                val email=et_register_email.text.toString()
                val password=et_register_password.text.toString()
                val c_password=et_register_confirm_password.text.toString()


                val namebody =RequestBody.create(MediaType.parse("text/plain"),name)
                val countrybody =RequestBody.create(MediaType.parse("text/plain"),country)
                val phonebody =RequestBody.create(MediaType.parse("text/plain"),phone)
                val locationbody =RequestBody.create(MediaType.parse("text/plain"),location)
                val emailbody =RequestBody.create(MediaType.parse("text/plain"),email)
                val passwordbody =RequestBody.create(MediaType.parse("text/plain"),password)
                val c_passwordbody =RequestBody.create(MediaType.parse("text/plain"),c_password)

                val map: MutableMap<String, RequestBody> = mutableMapOf()
                map.put("name",namebody)
                map.put("country",countrybody)
                map.put("phone",phonebody)
                map.put("location",locationbody)
                map.put("email",emailbody)
                map.put("password",passwordbody)
                map.put("c_password",c_passwordbody)


                val imageTag: Uri= profile_image.tag as Uri

                if(imageTag==null){
                    register(map)
                }
                else{
                    val file = File(ApiFuctions.getPath(this,imageTag)!!)
                    val imageBody=RequestBody.create(MediaType.parse("image/*"), file)
                    val multipartImage: MultipartBody.Part? = MultipartBody.Part.createFormData("image", file.getName(), imageBody)
                    register(map,multipartImage)
                }
            }
        }


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
            if (ContextCompat.checkSelfPermission(this@RegisterActivity,android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this@RegisterActivity, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),Read_Permission)
                return@setOnClickListener
            }

            val intent=Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false)
            intent.action=Intent.ACTION_GET_CONTENT
            activityResultLauncher.launch(intent)
        }
    }


    private fun validateRegisterDetails():Boolean{
            return when{
                TextUtils.isEmpty(et_register_name.text.toString().trim{it <=' '}) -> {
                    showErrorSnackbar(resources.getString(R.string.err_msg_enter_name), true)
                    false
                }
                TextUtils.isEmpty(et_register_email.text.toString().trim{it <=' '}) -> {
                    showErrorSnackbar(resources.getString(R.string.err_msg_enter_email), true)
                    false
                }
                TextUtils.isEmpty(et_register_address.text.toString().trim{it <=' '}) -> {
                    showErrorSnackbar(resources.getString(R.string.err_msg_enter_location), true)
                    false
                }
                TextUtils.isEmpty(et_register_country.text.toString().trim{it <=' '}) -> {
                    showErrorSnackbar(resources.getString(R.string.err_msg_enter_country), true)
                    false
                }
                TextUtils.isEmpty(et_register_phone.text.toString().trim{it <=' '}) -> {
                    showErrorSnackbar(resources.getString(R.string.err_msg_enter_phone), true)
                    false
                }
                TextUtils.isEmpty(et_register_password.text.toString().trim{it <=' '}) -> {
                    showErrorSnackbar(resources.getString(R.string.err_msg_enter_password), true)
                    false
                }
                TextUtils.isEmpty(et_register_confirm_password.text.toString().trim{it <=' '}) -> {
                    showErrorSnackbar(resources.getString(R.string.err_msg_confirm_password), true)
                    false
                }
                et_register_password.text.toString().trim{it <=' '} != et_register_confirm_password.text.toString()
                    .trim{it <=' '} -> {
                    showErrorSnackbar(resources.getString(R.string.err_msg_password_and_confirmation_mismatch), true)
                    false
                }
                !findViewById<AppCompatCheckBox>(R.id.cb_agree_to_terms).isChecked -> {
                    showErrorSnackbar(resources.getString(R.string.err_msg_agree_terms), true)
                    false
                }
                else->{
//                    showErrorSnackbar(resources.getString(R.string.registery_successful),false)
                    true
                }
            }
        }


    private fun register(map: MutableMap<String, RequestBody>, image: MultipartBody.Part? = null ){
        showProgressDialog(resources.getString(R.string.please_wait))
        val apiInterface = ApiInterface.create().register(map, image)

        apiInterface.enqueue( object : Callback<RegisterData> {
            override fun onResponse(call: Call<RegisterData>, response: retrofit2.Response<RegisterData>) {

                if(response.body() != null){
                    val registerResponse = response.body()!!.data

                    if (response.body()!!.success && registerResponse != null) {
                        sessionManager.saveAuthToken(registerResponse.token)
                        sessionManager.saveUserId(registerResponse.id)
                        hideProgressDialog()

                        val i = Intent(this@RegisterActivity, DashboardActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        startActivity(i)
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

            override fun onFailure(call: Call<RegisterData>, t: Throwable) {
                println(t.message)
                hideProgressDialog()
                showErrorSnackbar("error connecting to the server",true)
            }
        })

    }


}