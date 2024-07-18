package com.example.notesapp.db

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.EditActivity
import com.example.notesapp.R

class MyAdapter(listMain: ArrayList<ListItem>, contextM: Context) :
    RecyclerView.Adapter<MyAdapter.MyHolder>() {
    var listArray = listMain
    var context = contextM


    class MyHolder(itemView: View, contextV: Context) : RecyclerView.ViewHolder(itemView) {
        var context = contextV
        val tvTitle = itemView.findViewById<TextView>(R.id.tvTi)
        val tvTime = itemView.findViewById<TextView>(R.id.tvTime)

        fun setData(item: ListItem) {
            tvTitle.text = item.title
            tvTime.text = item.time
            itemView.setOnClickListener {
                val intent = Intent(context, EditActivity::class.java).apply {
                    putExtra(MyIntentConstance.I_TITLE_KEY, item.title)
                    putExtra(MyIntentConstance.I_DESC_KEY, item.desc)
                    putExtra(MyIntentConstance.I_URI_KEY, item.uri)
                    putExtra(MyIntentConstance.I_ID_KEY, item.id)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyHolder(inflater.inflate(R.layout.list_item, parent, false), context)
    }

    override fun getItemCount(): Int {
        return listArray.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.setData(listArray.get(position))
    }

    fun updateAdapter(listItems: List<ListItem>) {
        listArray.clear()
        listArray.addAll(listItems)
        notifyDataSetChanged()

    }

    fun removeItem(pos: Int, dbMAnager: MyDbMAnager) {
        dbMAnager.removeItemtoDb(listArray[pos].id.toString())
        listArray.removeAt(pos)
        notifyItemRangeChanged(0, listArray.size)
        notifyItemRemoved(pos)
    }
}