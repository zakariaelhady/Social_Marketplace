package com.example.krilia

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.example.krilia.Retrofit.ApiInterface
import com.example.krilia.Retrofit.SessionManager
import com.example.krilia.models.LoginData
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback

class LoginActivity : BaseActivity(),View.OnClickListener {
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sessionManager=SessionManager(this)

        //hide statusBar
        @Suppress("DEPRECATION")
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }


        val tvRegister= findViewById<TextView>(R.id.tv_register)
        tvRegister.setOnClickListener(this)

        val btnLogin=findViewById<AppCompatButton>(R.id.btn_login)
        btnLogin.setOnClickListener(this)

        val tvForgotPassword=findViewById<TextView>(R.id.tv_forgot_password)
        tvForgotPassword.setOnClickListener(this)

    }

    override fun onClick(v: View?){
        if(v!=null){
            when(v.id){
                R.id.tv_forgot_password ->{
                    startActivity(Intent(this@LoginActivity,ForgotPasswordActivity::class.java))
                }
                R.id.btn_login->{
//                    startActivity(Intent(this@LoginActivity,DashboardActivity::class.java))
                    if (validateLoginDetails()){
                        login(et_email.text.toString(),et_password.text.toString())
                    }
                }
                R.id.tv_register->{
                    val i = Intent(this@LoginActivity,RegisterActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(i)
                    finish()
                }
            }
        }
    }


    private fun validateLoginDetails():Boolean{
        return when{
            TextUtils.isEmpty(findViewById<EditText>(R.id.et_email).text.toString().trim{it <=' '}) -> {
                showErrorSnackbar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(findViewById<EditText>(R.id.et_password).text.toString().trim{it <=' '}) -> {
                showErrorSnackbar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else->{
                true
            }
        }
    }

    private fun login(email: String, password: String){
        showProgressDialog(resources.getString(R.string.please_wait))
        val apiInterface = ApiInterface.create().login(email, password)

        apiInterface.enqueue( object : Callback<LoginData> {
            override fun onResponse(call: Call<LoginData>, response: retrofit2.Response<LoginData>) {

                if(response.body() != null){
                    val loginResponse = response.body()!!.data

                    if (response.body()!!.success && loginResponse != null) {
                        sessionManager.saveAuthToken(loginResponse.token)
                        sessionManager.saveUserId(loginResponse.user.id)
                        hideProgressDialog()
//                        showErrorSnackbar(resources.getString(R.string.login_successful),false)
                        val i = Intent(this@LoginActivity, DashboardActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        startActivity(i)
                        finish()

                    } else {
                        hideProgressDialog()
                        showErrorSnackbar("not succeed",true)
                    }
                }else{
                    hideProgressDialog()
                    showErrorSnackbar("your email or password is incorrect",true)
                }
            }

            override fun onFailure(call: Call<LoginData>, t: Throwable) {
                println(t.message)
                hideProgressDialog()
                showErrorSnackbar("error connecting to the server",true)
            }
        })

    }
}