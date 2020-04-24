package com.example.mobileproject

import adapters.ActivityViewHolder
import adapters.LocalStorageViewHolder
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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

class LocalStorageActivity:AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var viewModel: LocalActivityViewModel
    internal var sort = arrayOf("Distance", "Type")

    private var activityList: ArrayList<localactivity> = ArrayList()
    lateinit var adapter: LocalStorageViewHolder.LocalStorageAdapter
    lateinit var backButton: Button
    lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.local_storage_activity)
        spinner = findViewById(R.id.sort_spinner)
        spinner.onItemSelectedListener = this
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, sort)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);


    }
    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
        var result = sort[position]
        if (result == "Distance") {
            activityList.sortBy { it.distance }
            adapter.notifyDataSetChanged()

        }
        if (result == "Type") {
            activityList.sortBy{ it.type }
            adapter.notifyDataSetChanged()
        }

    }

    override fun onNothingSelected(arg0: AdapterView<*>) {
        // TODO Auto-generated method stub
    }

    override fun onStart() {
        super.onStart()
        adapter = LocalStorageViewHolder.LocalStorageAdapter(activityList as ArrayList<localactivity>, {localactivity:localactivity->partItemClicked(localactivity)} )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        viewModel = ViewModelProviders.of(this).get(LocalActivityViewModel::class.java)
        backButton=back_button

        viewModel!!.localActivityList.observe(this, Observer { activity ->
            // Update the cached copy of the words in the adapter.
            activityList.clear()
            activityList.addAll(activity)
            adapter.notifyDataSetChanged()
        })

        backButton.setOnClickListener({
            val intent = Intent(this, MainActivity::class.java)
            intent!!.putExtra("reload", false)
            startActivity(intent)
        })

        }
    private fun partItemClicked(localactivity: localactivity) {
        viewModel!!.delete(localactivity)
        adapter.notifyDataSetChanged()
        Toast.makeText(this, "Activity deleted", Toast.LENGTH_SHORT)
    }

    }





