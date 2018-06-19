package com.abhishek.recycleritemtracking

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_item.view.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val words = resources.getStringArray(R.array.words)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = Adapter(this, words)

    }

    class Adapter(private val context: Context, private val words: Array<String>) :
        RecyclerView.Adapter<VH>() {

        private val rnd = Random()
        private val inflater = LayoutInflater.from(context)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(inflater.inflate(R.layout.layout_item, parent, false))
        }

        override fun getItemCount() = words.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.itemView.tv.text = words[position]
            val color = Color.argb(255,
                rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
            holder.itemView.setBackgroundColor(color)
        }
    }

    class VH(rootView: View) : RecyclerView.ViewHolder(rootView)
}