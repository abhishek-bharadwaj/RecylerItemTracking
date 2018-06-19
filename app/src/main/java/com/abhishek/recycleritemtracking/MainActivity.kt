package com.abhishek.recycleritemtracking

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_item.view.*
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val words = resources.getStringArray(R.array.words)
        layoutManager = LinearLayoutManager(this)
        rv.layoutManager = layoutManager
        rv.adapter = Adapter(this, words)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    inner class Adapter(private val context: Context, private val words: Array<String>) :
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

            Log.e("OOOOOOO", "visible item $position")
            Completable.timer(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        val firstVisible = layoutManager.findFirstVisibleItemPosition()
                        val lastVisible = layoutManager.findLastVisibleItemPosition()
                        val currentPosition = holder.adapterPosition
                        if (currentPosition in firstVisible..lastVisible) {
                            Toast.makeText(this@MainActivity,
                                "Sending event for $currentPosition", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onError(e: Throwable) {

                    }
                })
        }
    }

    class VH(rootView: View) : RecyclerView.ViewHolder(rootView)
}