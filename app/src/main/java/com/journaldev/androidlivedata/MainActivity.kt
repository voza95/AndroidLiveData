package com.journaldev.androidlivedata

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.journaldev.androidlivedata.MainActivity.FavAdapter.FavViewHolder
import java.util.*

class MainActivity : AppCompatActivity() {
    private var mFavAdapter: FavAdapter? = null
    private var mFavViewModel: FavouritesViewModel? = null
    private var mFav: List<Favourites>? = null
    var fab: FloatingActionButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fab = findViewById(R.id.fab)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mFavViewModel = ViewModelProvider(this).get(FavouritesViewModel::class.java)
        val favsObserver: Observer<List<Favourites>> = Observer { updatedList ->
            if (mFav == null) {
                mFav = updatedList
                mFavAdapter = FavAdapter()
                recyclerView.adapter = mFavAdapter
            } else {
                val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                    override fun getOldListSize(): Int {
                        return mFav!!.size
                    }

                    override fun getNewListSize(): Int {
                        return updatedList!!.size
                    }

                    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                        return mFav!![oldItemPosition].mId ==
                                updatedList!![newItemPosition].mId
                    }

                    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                        val oldFav = mFav!![oldItemPosition]
                        val newFav = updatedList!![newItemPosition]
                        return oldFav == newFav
                    }
                })
                result.dispatchUpdatesTo(mFavAdapter!!)
                mFav = updatedList
            }
        }
        fab?.setOnClickListener {
            val inUrl = EditText(this@MainActivity)
            val dialog = AlertDialog.Builder(this@MainActivity)
                    .setTitle("New favourite")
                    .setMessage("Add a url link below")
                    .setView(inUrl)
                    .setPositiveButton("Add") { dialog, which ->
                        val url = inUrl.text.toString()
                        val date = Date().time
                        // VM AND VIEW
                        mFavViewModel!!.addFav(url, date)
                    }
                    .setNegativeButton("Cancel", null)
                    .create()
            dialog.show()
        }
        mFavViewModel!!.favs.observe(this, favsObserver)
    }

    inner class FavAdapter : RecyclerView.Adapter<FavViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_row, parent, false)
            return FavViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
            val favourites = mFav!![position]
            holder.mTxtUrl.text = favourites.mUrl
            holder.mTxtDate.text = Date(favourites.mDate).toString()
        }

        override fun getItemCount(): Int {
            return mFav!!.size
        }

        inner class FavViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var mTxtUrl: TextView
            var mTxtDate: TextView

            init {
                mTxtUrl = itemView.findViewById(R.id.tvUrl)
                mTxtDate = itemView.findViewById(R.id.tvDate)
                val btnDelete = itemView.findViewById<ImageButton>(R.id.btnDelete)
                btnDelete.setOnClickListener {
                    val pos = adapterPosition
                    val favourites = mFav!![pos]
                    mFavViewModel!!.removeFav(favourites.mId)
                }
            }
        }
    }
}