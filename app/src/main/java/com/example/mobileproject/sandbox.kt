package com.example.mobileproject
//
//import android.widget.AdapterView
//import android.widget.Toast
//
//import android.widget.ArrayAdapter
//import android.R
//import android.widget.Spinner
//import android.os.Bundle
//import android.view.View
//import androidx.appcompat.app.AppCompatActivity
//
//
//class MainActivity1 : AppCompatActivity(), AdapterView.OnItemSelectedListener {
//    internal var country = arrayOf("India", "USA", "China", "Japan", "Other")
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.single_activity_layout.activity_main)
//        //Getting the instance of Spinner and applying OnItemSelectedListener on it
//        val spin = findViewById(R.id.sort_spinner) as Spinner
//        spin.onItemSelectedListener = this
//
//        //Creating the ArrayAdapter instance having the country list
//        val aa = ArrayAdapter(this, android.R.single_activity_layout.simple_spinner_item, country)
//        aa.setDropDownViewResource(android.R.single_activity_layout.simple_spinner_dropdown_item)
//        //Setting the ArrayAdapter data on the Spinner
//        spin.adapter = aa
//
//    }
//
//    //Performing action onItemSelected and onNothing selected
//    fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
//        Toast.makeText(applicationContext, country[position], Toast.LENGTH_LONG).show()
//    }
//
//    override fun onNothingSelected(arg0: AdapterView<*>) {
//        // TODO Auto-generated method stub
//    }
//}