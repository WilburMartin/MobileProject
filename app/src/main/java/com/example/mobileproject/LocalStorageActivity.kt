package com.example.mobileproject

import adapters.ActivityViewHolder
import adapters.LocalStorageViewHolder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import data_classes.activity
import kotlinx.android.synthetic.main.local_storage_activity.*
import local_database.LocalActivityViewModel
import local_database.localactivity

class LocalStorageActivity:AppCompatActivity() {

    private lateinit var viewModel: LocalActivityViewModel

    private var activityList: ArrayList<localactivity> = ArrayList()
    lateinit var adapter: LocalStorageViewHolder.LocalStorageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.local_storage_activity)

    }

    override fun onStart() {
        super.onStart()
        adapter = LocalStorageViewHolder.LocalStorageAdapter(activityList as ArrayList<localactivity>, {localactivity:localactivity->partItemClicked(localactivity)} )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        viewModel = ViewModelProviders.of(this).get(LocalActivityViewModel::class.java)

        viewModel!!.localActivityList.observe(this, Observer { activity ->
            // Update the cached copy of the words in the adapter.
            activityList.clear()
            activityList.addAll(activity)
            adapter.notifyDataSetChanged()
        })

        }
    private fun partItemClicked(localactivity: localactivity) {
        viewModel!!.delete(localactivity)
        adapter.notifyDataSetChanged()
    }

    }





