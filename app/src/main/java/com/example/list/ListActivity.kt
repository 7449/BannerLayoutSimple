package com.example.list

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.R
import com.example.newModel
import io.reactivex.network.RxNetWork
import io.reactivex.network.RxNetWorkListener
import io.reactivex.network.getApi
import kotlinx.android.synthetic.main.activity_recycler_view.*

/**
 * by y on 2017/3/8.
 */
class ListActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener, RxNetWorkListener<ListModel>, Runnable {

    private lateinit var adapter: ListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "RecyclerView Example"
        setContentView(R.layout.activity_recycler_view)
        refresh_layout.setOnRefreshListener(this)
        refresh_layout.post(this)
        adapter = ListAdapter()
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = adapter
    }

    override fun onRefresh() {
        RxNetWork.cancelTag(javaClass.simpleName)
        RxNetWork
                .observable(Api.ZLService::class.java)
                .getList()
                .getApi(javaClass.simpleName, this)
    }

    override fun onNetWorkStart() {
        refresh_layout.isRefreshing = true
    }

    override fun onNetWorkError(e: Throwable) {
        refresh_layout.isRefreshing = false
        Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
    }

    override fun onNetWorkComplete() {
        refresh_layout.isRefreshing = false
    }

    override fun onNetWorkSuccess(data: ListModel) {
        adapter.clear()
        adapter.addBanner(newModel())
        adapter.addAll(data.topStories)
    }

    override fun onDestroy() {
        super.onDestroy()
        RxNetWork.cancelTag(javaClass.simpleName)
    }

    override fun run() {
        onRefresh()
    }
}