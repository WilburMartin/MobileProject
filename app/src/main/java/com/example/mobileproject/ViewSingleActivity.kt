package com.example.mobileproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
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
        internal var apiKey = "AIzaSyAlI0k8ZhRZuywIpOHH0_9ls5a0JhyF1Pg"
        internal var fields = "formatted_address,name,photo,type,website,opening_hours,website,formatted_phone_number"

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
            }
            viewModel!!.locationQuery(placeid, apiKey, fields)

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

            })




//                Picasso.get().load(it.album.cover).into(trackPicture)
//                trackNameView.text = it.title
//                trackAlbumView.text = it.album.title
//                trackArtistView.text = "Artists: " + it.artist.name
//                trackPositionView.text = "Position: " + it.track_position.toString()
//                trackLengthView.text = "Song length: " + it.duration.toString()
//                trackReleaseDateView.text = "Release date: " + it.release_date
//                trackRankView.text = "Song rank: " + it.rank.toString()
//                trackGainView.text = "Song gain: " + it.gain.toString()


            backButton.setOnClickListener() {
                val intent = Intent(this, MainActivity::class.java)
                intent!!.putExtra("reload", false)
                startActivity(intent)
            }
        }
    }
