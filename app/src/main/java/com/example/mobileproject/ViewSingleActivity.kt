package com.example.mobileproject

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import data_classes.detailsResult
import data_classes.place_details
import kotlinx.android.synthetic.main.single_activity_layout.*
import local_database.LocalActivityViewModel
import local_database.localactivity
import places_api_setup.ActivityViewModel

class ViewSingleActivity:AppCompatActivity() {
        lateinit var activityNameView: TextView
        lateinit var activityPicture: ImageView
        lateinit var addressView: TextView
        lateinit var distanceView: TextView
        lateinit var typeView: TextView
        lateinit var websiteView: TextView
        lateinit var phoneView: TextView
        lateinit var openNowView: TextView
        lateinit var backButton: Button
        lateinit var viewModel: ActivityViewModel
        lateinit var localViewModel: LocalActivityViewModel
        var photoref=" "
        var placeid = " "
        var currentLocation: Location = Location(" ")
        var currentPlaceLat = 0.0
        var currentPlaceLong = 0.0

        internal var apiKey = "AIzaSyAlI0k8ZhRZuywIpOHH0_9ls5a0JhyF1Pg"
        internal var fields = "formatted_address,name,photo,type,website,opening_hours,website,formatted_phone_number,geometry"

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.single_activity_layout)
        }

        override fun onStart() {
            super.onStart()
            viewModel = ViewModelProviders.of(this).get(ActivityViewModel::class.java)
            localViewModel = ViewModelProviders.of(this).get(LocalActivityViewModel::class.java)
            activityNameView = activity_name
            activityPicture = activity_image
            addressView = formatted_address
            distanceView = distance
            typeView = type
            openNowView = open_now
            websiteView = website
            phoneView = phone_number
            backButton = back_button

            if (intent != null && intent.getExtras() != null) {
                 placeid = intent!!.getStringExtra("place_id")
                 currentPlaceLat=intent!!.getDoubleExtra("lat",0.0)
                 currentPlaceLong = intent!!.getDoubleExtra("long",0.0)

                 currentLocation.latitude= currentPlaceLat
                 currentLocation.longitude = currentPlaceLong

            }
            viewModel!!.locationQuery(placeid, apiKey, fields)

            var name = " "
            var type =  " "
            var address = " "
            var distance = 0.0

            viewModel.singlePlace.observe(this, Observer{

                activityNameView.text = it.result.name
                photoref =it.result.photos[0].photo_reference
                var photoURL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=${it.result.photos[0].photo_reference}&key=$apiKey"
                Picasso.get().load(photoURL).into(activityPicture)
                addressView.text = "Address: " +  it.result.formatted_address
                typeView.text = "Type: " + it.result.types[0]
                openNowView.text = "Open now?: " + it.result.opening_hours.open_now.toString().capitalize()
                websiteView.text = "Website: " + it.result.website
                phoneView.text = "Phone number: " + it.result.formatted_phone_number
                var latGeometry = it.result.geometry.location.lat
                var longGeometry = it.result.geometry.location.lng
                var calcLocation = Location(" ")
                calcLocation.latitude = latGeometry
                calcLocation.longitude = longGeometry
                var distanceInMilesFloat =
                    (currentLocation.distanceTo(calcLocation) / 1609);
                var distanceInMiles = "%.2f".format(distanceInMilesFloat).toDouble()
                distanceView.text = "Distance : " + distanceInMiles + " miles away"
                name = it.result.name
                type = it.result.types[0]
                address = it.result.formatted_address
                distance = distanceInMiles

            })

            backButton.setOnClickListener() {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }

            add_button.setOnClickListener() {
                var adder:localactivity = localactivity(name, address, type, distance)
                localViewModel!!.insert(adder)
                Toast.makeText(this, "Activity stored", Toast.LENGTH_SHORT)
            }
        }
    }
