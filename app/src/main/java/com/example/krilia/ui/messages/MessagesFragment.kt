package com.example.krilia.ui.messages

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.krilia.*
import com.example.krilia.Retrofit.ApiInterface
import com.example.krilia.Retrofit.SessionManager
import com.example.krilia.adapters.MessageBoxRecyclerviewAdapter
import com.example.krilia.databinding.FragmentMessagesBinding
import com.example.krilia.models.*
import kotlinx.android.synthetic.main.fragment_messages.*
import retrofit2.Call
import retrofit2.Callback


class MessagesFragment : Fragment() {

    lateinit var msgBoxRecyclerView: RecyclerView
    lateinit var msgBoxList: MutableList<Conversations>

    private var _binding: FragmentMessagesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var sessionManager : SessionManager
    private lateinit var msgBoxAdapter: MessageBoxRecyclerviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sessionManager=SessionManager(requireParentFragment().requireContext())

        msgBoxRecyclerView=binding.rvMessages
        msgBoxList=ArrayList()

        msgBoxAdapter= MessageBoxRecyclerviewAdapter(msgBoxList)

        showProgressDialog(resources.getString(R.string.please_wait))
        loadMessages()


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sv_messages.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Searchfilter(newText!!)
                return false
            }
        })

        val pullToRefresh: SwipeRefreshLayout =messageBox_swipe
        pullToRefresh.setOnRefreshListener {
            loadMessages(pullToRefresh) // your code
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_addProduct->{
                startActivity(Intent(activity, AddProductActivity::class.java))
                return true
            }
            R.id.action_likes->{
                startActivity(Intent(activity, LikedProductsActivity::class.java))
                return true
            }
            R.id.action_saved->{
                startActivity(Intent(activity, SavedProductsActivity::class.java))
                return true
            }
            R.id.action_aboutUs->{
                startActivity(Intent(activity, AboutUsActivity::class.java))
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
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun Searchfilter(text: String){
        val filteredItems: MutableList<Conversations> = ArrayList()
        for(item in msgBoxList){
            if(item.name.contains(text,true) or item.title.contains(text,true)){
                filteredItems.add(item)
            }
        }
        msgBoxAdapter.filterList(filteredItems)
    }

    private fun loadMessages(pullToRefresh: SwipeRefreshLayout?=null){
        val apiInterface = ApiInterface.create().getConversations("Bearer ${sessionManager.fetchAuthToken()}")

        apiInterface.enqueue( object : Callback<Conversation> {
            override fun onResponse(call: Call<Conversation>, response: retrofit2.Response<Conversation>) {

                if(response.body() != null){
                    msgBoxList.clear()
                    msgBoxList.addAll(response.body()!!.data.conversations.toMutableList())
                    msgBoxAdapter.notifyDataSetChanged()

                    val numberOfColumns = 1
                    msgBoxRecyclerView.layoutManager = GridLayoutManager(requireParentFragment().context, numberOfColumns)
                    val dividerItemDecoration = DividerItemDecoration(msgBoxRecyclerView.context, DividerItemDecoration.VERTICAL)
                    msgBoxRecyclerView.addItemDecoration(dividerItemDecoration)
                    msgBoxRecyclerView.adapter=msgBoxAdapter

                }
                if(pullToRefresh!=null){
                    pullToRefresh.isRefreshing = false
                }
                else{
                    hideProgressDialog()
                }
            }

            override fun onFailure(call: Call<Conversation>, t: Throwable) {
                if(pullToRefresh!=null){
                    pullToRefresh.isRefreshing = false
                }
                else{
                    hideProgressDialog()
                }
            }
        })
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
}