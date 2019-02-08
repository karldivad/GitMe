package com.example.karldivad.gitme

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.repository_item.view.*
import java.util.*

class RepositoryAdapter (val items : ArrayList<Repository>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.nameRepo.text = items[p1].name
        p0.descriptionRepo.text = items[p1].description
        p0.prcount.text = ("PR Count: " + items[p1].PR_count.toString())
        if (p1 % 2 != 0){
            p0.itemView.setBackgroundColor(Color.parseColor("#87CEFA"))
        }

    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.repository_item, p0, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }
}


class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    val nameRepo : TextView = view.name_txt
    val descriptionRepo : TextView = view.description_txt
    val prcount : TextView = view.pr_count_int
}