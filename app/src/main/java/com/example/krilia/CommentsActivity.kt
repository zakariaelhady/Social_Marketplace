package com.example.krilia

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.krilia.Retrofit.ApiInterface
import com.example.krilia.Retrofit.SessionManager
import com.example.krilia.adapters.CommentsRecyclerViewAdapter
import com.example.krilia.models.Comment
import com.example.krilia.models.Comments
import kotlinx.android.synthetic.main.activity_comments.*
import retrofit2.Call
import retrofit2.Callback

class CommentsActivity() : BaseActivity() {
    var commentsList: MutableList<Comment> = ArrayList()
    var productId : String? ="0"
    private lateinit var sessionManager : SessionManager
    val commentsAdapter= CommentsRecyclerViewAdapter(commentsList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        registerForContextMenu(rv_comments)
        sessionManager=SessionManager(this@CommentsActivity)
        val b = intent.extras
        productId=b!!.getString("productId")

        showProgressDialog(resources.getString(R.string.please_wait))
        loadCommentdata()

        iv_send_comment.setOnClickListener {
            if(TextUtils.isEmpty(et_comment.text.toString().trim{it <=' '})){
                showErrorSnackbar("write the comment first",true)
            }else{
                addComment()
                et_comment.setText("")
            }
        }

        val pullToRefresh: SwipeRefreshLayout=comments_swipe
        pullToRefresh.setOnRefreshListener {
            loadCommentdata(pullToRefresh) // your code
        }
    }

    fun addComment(){
        val apiInterface = ApiInterface.create().add_Comment("Bearer ${sessionManager.fetchAuthToken()}",productId!!,et_comment.text.toString())

        apiInterface.enqueue( object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: retrofit2.Response<Any>) {
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
            }
        })
        rv_comments.visibility= View.VISIBLE
        tv_no_comments.visibility=View.INVISIBLE
        loadCommentdata()
    }
    fun loadCommentdata(pullToRefresh: SwipeRefreshLayout?=null){
        val apiInterface = ApiInterface.create().getComments(productId!!)

        apiInterface.enqueue( object : Callback<Comments> {
            override fun onResponse(call: Call<Comments>, response: retrofit2.Response<Comments>) {

                if(response.body() != null){
                    commentsList.clear()
                    commentsList.addAll(response.body()!!.data.toMutableList())
                    commentsAdapter.notifyDataSetChanged()
//                    val commentsAdapter= CommentsRecyclerViewAdapter(commentsList)
                    val numberOfColumns = 1
                    rv_comments.layoutManager = GridLayoutManager(this@CommentsActivity, numberOfColumns)
                    val dividerItemDecoration = DividerItemDecoration(this@CommentsActivity, DividerItemDecoration.VERTICAL)
                    rv_comments.addItemDecoration(dividerItemDecoration)
                    rv_comments.adapter=commentsAdapter

                    if(commentsList.count() == 0){
                        rv_comments.visibility= View.INVISIBLE
                        tv_no_comments.visibility=View.VISIBLE
                    }

                }
                if(pullToRefresh!=null){
                    pullToRefresh.isRefreshing = false
                }
                else{
                    hideProgressDialog()
                }
            }

            override fun onFailure(call: Call<Comments>, t: Throwable) {

                rv_comments.visibility= View.INVISIBLE
                tv_no_comments.visibility=View.VISIBLE
                tv_no_comments.text=getString(R.string.error_occured)
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