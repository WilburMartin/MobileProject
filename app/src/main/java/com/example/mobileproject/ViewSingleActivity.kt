package com.example.mobileproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.single_activity_layout.*
import local_database.localactivity

class ViewSingleActivity:AppCompatActivity() {
    class ViewSingleTrackActivity: AppCompatActivity() {
        lateinit var activityNameView: TextView
        lateinit var activityPicture: ImageView
        lateinit var addressView: TextView
        lateinit var distanceView: TextView
        lateinit var typeView: TextView
        lateinit var openingHoursView: TextView
        lateinit var openNowView: TextView

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.single_activity_layout)
        }

        override fun onStart() {
            super.onStart()
            activityNameView = activity_name
            activityPicture = activity_image
            addressView = formatted_address
            distanceView = distance
            typeView = type
            openingHoursView = opening_hours
            openNowView=open_now

            if(intent != null && intent.getExtras() != null) {
                var activityId = intent!!.getIntExtra("place_id",0)
            }
                Picasso.get().load(it.album.cover).into(trackPicture)
                trackNameView.text = it.title
                trackAlbumView.text = it.album.title
                trackArtistView.text = "Artists: " + it.artist.name
                trackPositionView.text = "Position: " + it.track_position.toString()
                trackLengthView.text = "Song length: " + it.duration.toString()
                trackReleaseDateView.text = "Release date: " + it.release_date
                trackRankView.text = "Song rank: " + it.rank.toString()
                trackGainView.text = "Song gain: " + it.gain.toString()

            })


            backButton.setOnClickListener() {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
}