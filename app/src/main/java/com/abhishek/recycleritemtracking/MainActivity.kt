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
import android.widget.Toast
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_item.view.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var listDisposable: MutableList<Disposable?>
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val words = resources.getStringArray(R.array.words)
        listDisposable = ArrayList(words.size)
        for (i in 0..words.size) listDisposable.add(null)
        layoutManager = LinearLayoutManager(this)
        rv.layoutManager = layoutManager
        rv.adapter = Adapter(this, words)

        rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstVisible = layoutManager.findFirstVisibleItemPosition()
                val lastVisible = layoutManager.findLastVisibleItemPosition()
                for (i in 0 until listDisposable.size) {
                    if (i in firstVisible..lastVisible) {
                        if (listDisposable[i] == null) createEventCompletable(i)
                    } else {
                        listDisposable[i]?.dispose()
                        listDisposable[i] = null
                    }
                }
            }
        })
    }

    private fun createEventCompletable(position: Int) {
        Completable.timer(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {
                    Toast.makeText(this@MainActivity,
                        "Event send for $position", Toast.LENGTH_SHORT).show()
                }

                override fun onSubscribe(d: Disposable) {
                    listDisposable.set(position, d)
                }

                override fun onError(e: Throwable) {

                }
            })
    }

    inner class Adapter(context: Context, private val words: Array<String>) :
        RecyclerView.Adapter<VH>() {

        private val rnd = Random()
        private val inflater = LayoutInflater.from(context)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(inflater.inflate(R.layout.layout_item, parent, false))
        }

        override fun getItemCount() = words.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.itemView.tv.text = "$position : ${words[position]}"
            val color = Color.argb(255,
                rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
            holder.itemView.setBackgroundColor(color)
        }
    }

    class VH(rootView: View) : RecyclerView.ViewHolder(rootView)
}