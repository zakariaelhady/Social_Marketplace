package com.example.krilia

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.krilia.utils.Constant
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_about_us.*


class AboutUsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
        Picasso.get().load(Constant.storage+"author/me.jpg").into(me_image)
        btn_tip.setOnClickListener {
            goToUrl("https://www.paypal.com/paypalme/zakariaelhady")
        }
        icon_facebook.setOnClickListener{
            goToUrl("https://www.facebook.com/zakaria.elhadi.58/")
        }
        icon_twitter.setOnClickListener{
            goToUrl("https://twitter.com/zakariaelhady1")
        }
        icon_instagram.setOnClickListener{
            goToUrl("https://www.instagram.com/zakariaelhady/")
        }
        icon_linkedIn.setOnClickListener{
            goToUrl("https://www.linkedin.com/in/zakariae-el-hady-4a414a1b1/")
        }
    }
    private fun goToUrl(url: String) {
        val uriUrl: Uri = Uri.parse(url)
        val launchBrowser = Intent(Intent.ACTION_VIEW, uriUrl)
        startActivity(launchBrowser)
    }

}