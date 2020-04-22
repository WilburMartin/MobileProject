//package com.example.mobileproject
//
//import android.widget.Toast
//import adapters.RecyclerViewClickListener
//import androidx.recyclerview.widget.LinearLayoutManager
//import android.R
//import androidx.recyclerview.widget.RecyclerView
//import android.os.Bundle
//import android.view.ViewGroup
//import android.view.LayoutInflater
//
//
//fun onCreateView(inflater: LayoutInflater, @Nullable container: ViewGroup, @Nullable savedInstanceState: Bundle): View {
//    val v = inflater.inflate(R.layout.my_fragment, container, false)
//
//    val recyclerView = v.findViewById(R.id.my_recyclerview) as RecyclerView
//
//    recyclerView.setHasFixedSize(true)
//    val layoutManager = LinearLayoutManager(getContext())
//    recyclerView.layoutManager = layoutManager
//
//    val listener = { view, position ->
//        Toast.makeText(getContext(), "Position $position", Toast.LENGTH_SHORT).show()
//    }
//    val adapter = RecyclerViewAdapter(listener)
//    recyclerView.adapter = adapter
//
//    return v
//}