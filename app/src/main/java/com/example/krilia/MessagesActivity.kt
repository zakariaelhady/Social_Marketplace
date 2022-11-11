package com.example.krilia

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.example.krilia.Retrofit.ApiInterface
import com.example.krilia.Retrofit.SessionManager
import com.example.krilia.adapters.MessagesRecyclerViewAdapter
import com.example.krilia.models.Message
import com.example.krilia.models.Messages
import kotlinx.android.synthetic.main.activity_messages.*
import kotlinx.android.synthetic.main.type_message_area.*
import retrofit2.Call
import retrofit2.Callback

class MessagesActivity : BaseActivity() {
    var messagesList: MutableList<Message> = ArrayList()
    var productId : String? ="0"
    var receiverId : String? ="0"
    lateinit var messagesAdapter: MessagesRecyclerViewAdapter
    private lateinit var sessionManager : SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        sessionManager=SessionManager(this@MessagesActivity)
        registerForContextMenu(rv_messages)
        val b = intent.extras
        productId=b!!.getString("productId")
        receiverId=b.getString("receiverId")

        showProgressDialog(resources.getString(R.string.please_wait))
        loadMessagesdata()

        iv_send.setOnClickListener {
            if(TextUtils.isEmpty(et_type.text.toString().trim{it <=' '})){
                showErrorSnackbar("type the message you want to send", true)
            }
            else{
                addMessage(et_type.text.toString())
                et_type.setText("")
                loadMessagesdata()
            }
        }
    }

    fun loadMessagesdata(){
        val apiInterface = ApiInterface.create().getMessages(token = "Bearer ${sessionManager.fetchAuthToken()}",productId!!)

        apiInterface.enqueue( object : Callback<Messages> {
            override fun onResponse(call: Call<Messages>, response: retrofit2.Response<Messages>) {

                if(response.body() != null){
                    messagesList=response.body()!!.data

                    messagesAdapter= MessagesRecyclerViewAdapter(messagesList,sessionManager)
                    val numberOfColumns = 1
                    rv_messages.layoutManager = GridLayoutManager(this@MessagesActivity, numberOfColumns)
//                    val dividerItemDecoration = DividerItemDecoration(this@MessagesActivity, DividerItemDecoration.VERTICAL)
//                    rv_comments.addItemDecoration(dividerItemDecoration)
                    rv_messages.adapter=messagesAdapter

                    if(messagesList.count() == 0){
                        rv_messages.visibility= View.INVISIBLE
                        tv_no_messages.visibility= View.VISIBLE
                    }

                    rv_messages.smoothScrollToPosition(messagesAdapter.itemCount)

                }
                hideProgressDialog()
            }

            override fun onFailure(call: Call<Messages>, t: Throwable) {
                println(t.message)

                rv_messages.visibility= View.INVISIBLE
                tv_no_messages.visibility= View.VISIBLE
                tv_no_messages.text=getString(R.string.error_occured)
                hideProgressDialog()
            }
        })
    }

    fun addMessage(message: String){
        val apiInterface = ApiInterface.create().addMessage(token = "Bearer ${sessionManager.fetchAuthToken()}",message,productId!!.toInt(),receiverId!!.toInt())

        apiInterface.enqueue( object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: retrofit2.Response<Any>) {
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
            }
        })
    }
}