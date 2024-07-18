package com.example.notesapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.db.MyAdapter
import com.example.notesapp.db.MyDbMAnager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class  MainActivity : AppCompatActivity() {
    private val myDbManager = MyDbMAnager(this)
    private val myAdapter = MyAdapter(ArrayList(), this)
    private var job :Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        initSearchView()
    }

    fun onClickNew(view: View) {
        val i = Intent(this, EditActivity::class.java)
        startActivity(i)
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDB()
        fillAdapter("")
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()
    }
    private fun init(){
        val rcView: RecyclerView = findViewById(R.id.rcView)
        val swapHelper = getSwapMg()
        swapHelper.attachToRecyclerView(rcView)
          rcView.layoutManager = LinearLayoutManager(this)
        rcView.adapter = myAdapter
    }
    private fun initSearchView(){
        val searchView : SearchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(text: String?): Boolean {
               fillAdapter(text!!)
                return true
            }
        })
    }

    private fun fillAdapter(text: String){
        val tvNoElements: TextView = findViewById(R.id.tvNotElements)


        job?.cancel()
       job = CoroutineScope(Dispatchers.Main).launch{
            val list = myDbManager.readDbData(text)
            myAdapter.updateAdapter(list )
            if (list.size > 0 ) {
                tvNoElements.visibility = View.GONE
            }
            else {
                tvNoElements.visibility = View.VISIBLE
            }
        }

    }


    private fun getSwapMg(): ItemTouchHelper{
        return ItemTouchHelper(object : ItemTouchHelper
            .SimpleCallback(0,ItemTouchHelper.RIGHT or ItemTouchHelper. LEFT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                myAdapter.removeItem(viewHolder.adapterPosition, myDbManager)
            }
        })
    }
}
