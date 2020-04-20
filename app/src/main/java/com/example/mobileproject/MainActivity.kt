package com.example.mobileproject
import adapters.ActivityViewHolder
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import places_api_setup.ActivityViewModel
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
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
import data_classes.Geometry
import data_classes.NearbySearch
import data_classes.activity
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    internal var sort = arrayOf("Distance", "Type")
    lateinit var placesClient:PlacesClient
    var sortablePlaces: ArrayList<PlaceLikelihood> = ArrayList<PlaceLikelihood>()
//    lateinit var searchButton: SearchView
//    lateinit var searchBox: EditText
    lateinit var spinner: Spinner
    lateinit var viewModel: ActivityViewModel
    var placesList: ArrayList<NearbySearch> = ArrayList<NearbySearch>()
    var activityList: ArrayList<activity> = ArrayList<activity>()
    internal var placeId = ""
    lateinit var currentPlaceCoordinates:LatLng
     var currentPlaceLat = 0.0
     var currentPlaceLong = 0.0
    lateinit var currentLocation : Location
    lateinit var adapter: ActivityViewHolder.ActivityItemAdapter
    internal var searchRadius = "50000"
    internal var apiKey = "AIzaSyAlI0k8ZhRZuywIpOHH0_9ls5a0JhyF1Pg"

    var placeFields = arrayListOf(Place.Field.ID,
        Place.Field.NAME,
        Place.Field.ADDRESS,
        Place.Field.LAT_LNG)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        searchButton = search_button
//        searchBox= search_box
        viewModel = ViewModelProviders.of(this).get(ActivityViewModel::class.java)
        adapter = ActivityViewHolder.ActivityItemAdapter(activityList as ArrayList<activity>)
        spinner = findViewById(R.id.sort_spinner)
        spinner.onItemSelectedListener = this
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, sort)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);


        requestPermission()

        initPlaces()

        setupPlacesAutoComplete()

        setupCurrentPlace()

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
        placesList.clear()
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
                        if (placeLikelihood.place.name!=null)
                         sortablePlaces.add(placeLikelihood)

                    }
                    sortablePlaces.sortWith(Comparator { placeChildHood, t1 ->
                        placeChildHood.likelihood.toDouble().compareTo(t1.likelihood.toDouble())
                    })
                        Collections.reverse(sortablePlaces)
                        placeId = sortablePlaces[0].place.id!!
                        currentPlaceCoordinates = sortablePlaces[0].place.latLng!!
                    if (currentPlaceCoordinates!=null)
                        currentPlaceLat=currentPlaceCoordinates.latitude
                        currentPlaceLong=currentPlaceCoordinates.longitude
                        println("Current coordinates: " + currentPlaceCoordinates)

                        currentLocation = Location(" ")
                        currentLocation.latitude = currentPlaceLat.toDouble()
                        currentLocation.longitude = currentPlaceLong.toDouble()
                    println("Current location: " + currentLocation)
                     println("Current lat: " + currentPlaceLat)
                     println("Current long" + currentPlaceLong)
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
        //placesList.clear()
        var index = 0;
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = adapter
        var layoutManager: LinearLayoutManager = LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        var searchTypes :ArrayList<String> = ArrayList<String>()
        searchTypes.add("restaurant")
        searchTypes.add("park")
        if (currentPlaceCoordinates != null) {
            viewModel.placesList.observe(this, Observer { places ->
                placesList.add(places)
                for (NearbySearch in placesList) {
                    for (result in NearbySearch.results) {
                        var placeLat = result.geometry.location.lat
                        var placeLong = result.geometry.location.lng
                        var calcLocation = Location(" ")
                        calcLocation.latitude = placeLat
                        calcLocation.longitude = placeLong
                        var distanceInMilesFloat = (currentLocation.distanceTo(calcLocation)/1609);
                        var distanceInMiles = "%.2f".format(distanceInMilesFloat).toDouble()
                        println("Distance in miles: " + distanceInMiles)
                        var activity = activity(result.name, result.types.component1(), result.vicinity, distanceInMiles)
                        activityList.add(activity)
                        println(activityList.toString())
                    }
                }
                index = index + 1
                println("Index : $index")
                adapter.notifyDataSetChanged()
                var stringCoordinates = "$currentPlaceLat,$currentPlaceLong"
                if (index < searchTypes.size) {
                viewModel.getNearbySearch(stringCoordinates, searchRadius, searchTypes[index], apiKey)
                }
            })
            var stringCoordinates = "$currentPlaceLat,$currentPlaceLong"
            //autofill the recycler view on creation
            viewModel.getNearbySearch(stringCoordinates, searchRadius, searchTypes[0], apiKey)

            var numberOfObjects = searchTypes.size


            }


            //click listener for when search button is pressed from edit text
//            searchBox.setOnEditorActionListener() { v, actionId, event ->
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    //your code here
//                    val input: String = searchBox.text.toString()
//                    viewModel!!.getNearbySearch(currentPlaceCoordinates, searchRadius, input, apiKey)
//                    true
//                }
//                false
//
//            }
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