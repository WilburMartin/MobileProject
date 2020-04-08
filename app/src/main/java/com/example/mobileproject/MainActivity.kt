package com.example.mobileproject
import adapters.ActivityViewHolder
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import places_api_setup.ActivityViewModel
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import data_classes.NearbySearch
import data_classes.activity
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.StringBuilder
import java.util.*

class MainActivity : AppCompatActivity()  {

    lateinit var placesClient:PlacesClient
    var sortablePlaces: ArrayList<PlaceLikelihood> = ArrayList<PlaceLikelihood>()
    lateinit var searchButton: SearchView
    lateinit var searchBox: EditText
    lateinit var viewModel: ActivityViewModel
    var placesList: ArrayList<NearbySearch> = ArrayList<NearbySearch>()
    var activityList: ArrayList<activity> = ArrayList<activity>()
    internal var placeId = ""
    var currentPlaceCoordinates=" "
    internal var searchRadius = "50000"
    internal var apiKey = "AIzaSyAlI0k8ZhRZuywIpOHH0_9ls5a0JhyF1Pg"

    var placeFields = arrayListOf(Place.Field.ID,
        Place.Field.NAME,
        Place.Field.ADDRESS,
        Place.Field.LAT_LNG)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        searchButton = search_button
        searchBox=search_box
        viewModel = ViewModelProviders.of(this).get(ActivityViewModel::class.java)

        requestPermission()

        initPlaces()

        setupPlacesAutoComplete()

        setupCurrentPlace()


    }

    private fun requestPermission() {
        Dexter.withActivity(this)
            .withPermissions(Arrays.asList(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ))
            .withListener(object:MultiplePermissionsListener{
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    Toast.makeText( this@MainActivity,"Accept Permissions", Toast.LENGTH_SHORT).show()
                }
            }).check()
    }

    private fun setupCurrentPlace() {
        val request = FindCurrentPlaceRequest.builder(placeFields).build()
        btn_get_current_place.setOnClickListener {
            if(ActivityCompat.checkSelfPermission(this@MainActivity,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return@setOnClickListener;
            }
            val placeResponse = placesClient.findCurrentPlace(request)
            placeResponse.addOnCompleteListener {task->
                if(task.isSuccessful) {
                    val response = task.result
                    println(response.toString())
                    for (placeLikelihood in response!!.getPlaceLikelihoods()) {
                         sortablePlaces.add(placeLikelihood)

                    }
                    sortablePlaces.sortWith(Comparator { placeChildHood, t1 ->
                        placeChildHood.likelihood.toDouble().compareTo(t1.likelihood.toDouble())
                    })
                        Collections.reverse(sortablePlaces)
                        placeId = sortablePlaces[0].place.id!!
                        currentPlaceCoordinates = "37.235352,-122.06272"
                     println("Current coordinates: 37.235352,-122.06272")
                    println(currentPlaceCoordinates)
                    println(placeId)
                    getNearbyActivities()
                    val likehoods = StringBuilder("")

                   // edt_address.setText(StringBuilder(sortablePlaces[0].place.address!!))

                    for(placeLikelihood in sortablePlaces) {
                        likehoods.append(String.format("Place '%s'", placeLikelihood.place.name))
                            .append("\n")
                    }
                    //edt_place_likelihoods.setText(" ")
                    //edt_place_likelihoods.setText(likehoods.toString())
                }
                else {
                    Toast.makeText(this@MainActivity, "Place not found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getNearbyActivities() {
        //set recycler view
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = ActivityViewHolder.ActivityItemAdapter(activityList as ArrayList<activity>)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        if (currentPlaceCoordinates != null || currentPlaceCoordinates !=  " ") {
            viewModel.placesList.observe(this, Observer { places ->
                placesList.clear()
                placesList.add(places)
                println(placesList)
                for (NearbySearch in placesList) {
                    for (result in NearbySearch.results) {
                        var activity = activity(result.name, result.types.component1(), result.vicinity)
                        activityList.add(activity)
                        println(activityList.toString())
                    }
                }
                adapter.notifyDataSetChanged()
            })
            //autofill the recycler view on creation

            viewModel.getNearbySearch(currentPlaceCoordinates, searchRadius, "bar", apiKey)


            //click listener for when search button is pressed from edit text
            searchBox.setOnEditorActionListener() { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //your code here
                    val input: String = searchBox.text.toString()
                    //viewModel!!.getNearbyBySearch(apiKey, currentPlaceCoordinates, searchRadius)
                    true
                }
                false

            }
        }
    }





    private fun setupPlacesAutoComplete() {
        val autocompleteFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_place) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(placeFields)

        autocompleteFragment.setOnPlaceSelectedListener(object:PlaceSelectionListener{
            override fun onPlaceSelected(p0: Place) {
                Toast.makeText(this@MainActivity,"" + p0.address, Toast.LENGTH_SHORT).show()
            }

            override fun onError(p0: Status) {
                Toast.makeText(this@MainActivity,"" + p0.statusMessage, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun initPlaces() {
        Places.initialize(this,apiKey)
        placesClient = Places.createClient(this)
    }
}