package com.example.krilia

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.WindowInsets
import android.view.WindowManager
import com.example.krilia.Retrofit.ApiInterface
import com.example.krilia.Retrofit.SessionManager
import com.example.krilia.models.Categories
import retrofit2.Call
import retrofit2.Callback

class SplashActivity : BaseActivity() {
    private lateinit var sessionManager : SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        sessionManager=SessionManager(this@SplashActivity)
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

        val apiInterface = ApiInterface.create().getCategories()

        apiInterface.enqueue( object : Callback<Categories> {
            override fun onResponse(call: Call<Categories>, response: retrofit2.Response<Categories>) {
                @Suppress("DEPRECATION")
                Handler().postDelayed(
                    {
                        if (sessionManager.tokenExist()) {
                            startActivity(Intent(this@SplashActivity, DashboardActivity::class.java))
                        } else {
                            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                        }
                        finish()
                    },
                    2500
                )
            }

            override fun onFailure(call: Call<Categories>, t: Throwable) {
                showErrorSnackbar("Error connecting to the server,check you Internet Connection", true)
            }
        })

    }
}