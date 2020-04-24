package com.example.mobileproject
import adapters.ActivityViewHolder
import adapters.LocalStorageViewHolder
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
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
import kotlinx.android.synthetic.main.dialog_view.*
import kotlinx.android.synthetic.main.dialog_view.view.*
import local_database.LocalActivityViewModel
import local_database.localactivity
import weather_api_setup.CityWeather
import weather_api_setup.WeatherViewModel
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    internal var sort = arrayOf("Distance", "Type")
    lateinit var placesClient:PlacesClient
    var sortablePlaces: ArrayList<PlaceLikelihood> = ArrayList<PlaceLikelihood>()
    lateinit var searchButton: SearchView
    lateinit var searchBox: EditText
    lateinit var spinner: Spinner
    lateinit var viewModel: ActivityViewModel
    lateinit var weatherView: WeatherViewModel
    lateinit var localStorageViewModel: LocalActivityViewModel

    var placesList: ArrayList<NearbySearch> = ArrayList<NearbySearch>()
    var activityList: ArrayList<activity> = ArrayList<activity>()
    var questionList: ArrayList<CityWeather> = ArrayList()
    var reload: Boolean=true
    internal var placeId = ""
    lateinit var currentPlaceCoordinates:LatLng
     var currentPlaceLat = 0.0
     var currentPlaceLong = 0.0
    lateinit var currentLocation : Location
    lateinit var adapter: ActivityViewHolder.ActivityItemAdapter
    internal var searchRadius = "10000"
    internal var apiKey = "AIzaSyAlI0k8ZhRZuywIpOHH0_9ls5a0JhyF1Pg"
    lateinit var weatherGood:TextView

    lateinit var goToLocalActivities:Button

    //Weather check variables
    var weatherStat = arrayOf<Boolean>(
        true, //weatherStat[0]: Is Temperature in Range (60 degrees to 90 degrees)
        true, //weatherStat[1]: Is Wind in Range (60 degrees to 90 degrees)
        true, //weatherStat[2]: Is it not Raining or Snowing?
        true  //weatherStat[3]: Other conditions
    )
    var outdoorWeather = true; //Is the weather good for outdoors?
    var tempLowBound = 289; //In Kelvin. 60 degrees Fahrenheit by default
    var tempHighBound = 300; //In Kelvin. 80 degrees Fahrenheit by default
    var windHighBound = 18; //In m/s. 40 mph by default.


    var placeFields = arrayListOf(Place.Field.ID,
        Place.Field.NAME,
        Place.Field.ADDRESS,
        Place.Field.LAT_LNG)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        weatherGood = display_weather
        goToLocalActivities = findViewById(R.id.go_to_stored)
        viewModel = ViewModelProviders.of(this).get(ActivityViewModel::class.java)
        weatherView = ViewModelProviders.of(this).get(WeatherViewModel::class.java)
        localStorageViewModel=ViewModelProviders.of(this).get(LocalActivityViewModel::class.java)
        adapter = ActivityViewHolder.ActivityItemAdapter(activityList as ArrayList<activity>, {activity: activity->partItemClicked(activity)})
        spinner = findViewById(R.id.sort_spinner)
        spinner.onItemSelectedListener = this
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, sort)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);


        requestPermission()

        initPlaces()
         var sharedPreferences:SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPreferences.getBoolean("IS_FIRST_TIME", true)) {
        getTargetDistance()
        sharedPreferences.edit().putBoolean("IS_FIRST_TIME", false).apply();
    }
        setupCurrentPlace()

        setupPlacesAutoComplete(this)

        if (intent != null && intent.getExtras() != null) {
            reload = intent!!.getBooleanExtra("reload", true)
        }

        goToLocalActivities.setOnClickListener() {
            val intent = Intent(this, LocalStorageActivity::class.java)
            startActivity(intent)
        }

    }




    private fun getTargetDistance() {
        // Opens the dialog view asking the user for their target cals for the day, based on to do app
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_view, null)
        val alertBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Enter distance")
        val show = alertBuilder.show()
        show.submit_distance.setOnClickListener {
            var distanceHolder = dialogView.target_distance.text.toString()
            if (!distanceHolder.isNullOrEmpty()) {
                var distanceNumber = Integer.parseInt(distanceHolder)
                searchRadius = (distanceNumber * 1600).toString()
                show.dismiss()
            }
        }
    }


    private fun partItemClicked(activity: activity) {
        var localactivity= localactivity(activity.name, activity.formatted_address, activity.type, activity.distance)
        localStorageViewModel.insert(localactivity)
        Toast.makeText(this, "Activity stored", Toast.LENGTH_SHORT)
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
        val request = FindCurrentPlaceRequest.builder(placeFields).build()
        btn_get_current_place.setOnClickListener {
            if(ActivityCompat.checkSelfPermission(this@MainActivity,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                placesList.clear()
                activityList.clear()
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
                        currentLocation = Location(" ")
                        currentLocation.latitude = currentPlaceLat.toDouble()
                        currentLocation.longitude = currentPlaceLong.toDouble()
                    val likehoods = StringBuilder("")
                    getNearbyActivities()
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
    private fun checkWeather(): Boolean{
        var i: Int = 0;
        var comma: Char = ',';
        //var xCoord: String = currentPlaceCoordinates.substring(0,currentPlaceCoordinates.indexOf(comma));
        //var yCoord: String = currentPlaceCoordinates.substring(currentPlaceCoordinates.indexOf(comma) + 1);
        var xCoord: String = "" + currentPlaceLat;
        var yCoord: String = "" + currentPlaceLong;
        weatherView.getWeatherByCoords(xCoord,yCoord);


        weatherView!!.weatherList.observe(this, Observer {
            questionList.clear()
            questionList.add(it)
            var main = questionList.get(0).weather.get(0).main;
            var wind = questionList.get(0).wind.speed;
            var temp = questionList.get(0).main.temp;

            //Checks Temperature
            if(temp <= tempHighBound && temp >= tempLowBound){
                weatherStat[0] = true;
            }else{
                weatherStat[0] = false;
                outdoorWeather = false;
            }

            //Checks Wind Speed
            if(wind <= windHighBound){
                weatherStat[1] = true;
            }else{
                weatherStat[1] = false;
                outdoorWeather = false;
            }


            //Checks for Rain/Snow/Extreme/Sleet weather

            if(main.equals("Extreme") || main.equals("Rain") || main.equals("Snow") || main.equals("Sleet")){
                weatherStat[2] = false;
                outdoorWeather = false;
            }else{
                weatherStat[3] = true;
            }
        })
        if (outdoorWeather == true) {
            weatherGood.text = "Weather good for outside activities today"
        }
        else{
            weatherGood.text = "Indoor activities better today"
        }
        return outdoorWeather;
    }

    private fun getActivityList(): Array<Pair<String,Boolean>>{
        return arrayOf<Pair<String, Boolean>>(
            Pair("amusement_park", true),
            Pair("aquarium" , false),
            Pair("art_gallery" , false),
            Pair("bakery", false),
            Pair("bar", false),
            Pair("beauty_salon" , false),
            Pair("book_store" , false),
            Pair("bowling_alley", false),
            Pair("cafe", false),
            Pair("campground", false),
            Pair("casino", false),
            Pair("clothing_store" , false),
            Pair("department_store", false),
            Pair("library", false),
            Pair("movie_theater", false),
            Pair("museum", false),
            Pair("night_club", false),
            Pair("park", false),
            Pair("restaurant", false),
            Pair("shopping_mall", false),
            Pair("spa", false),
            Pair("stadium", false),
            Pair("store", false),
            Pair("tourist_attraction", false),
            Pair("zoo", true)
        )
    }

    private fun getNearbyActivities() {
        //set recycler view
        checkWeather();

        var index = 0;
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = adapter
        var layoutManager: LinearLayoutManager = LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        var searchTypes :Array<Pair<String, Boolean>> = getActivityList()
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
                        var distanceInMilesFloat =
                            (currentLocation.distanceTo(calcLocation) / 1609);
                        var distanceInMiles = "%.2f".format(distanceInMilesFloat).toDouble()
                        var activity = activity(
                            result.name,
                            result.types.component1().replace('_', ' '),
                            result.vicinity,
                            distanceInMiles,
                            result.place_id,
                            currentLocation
                        )
                        activityList.add(activity)



                    }
                }
                var checkSet: HashSet<activity> = HashSet<activity>(activityList)
                activityList.clear()
                activityList.addAll(checkSet)
                index = index + 1
                println("Index : $index")
                adapter.notifyDataSetChanged()
                var stringCoordinates = "$currentPlaceLat,$currentPlaceLong"
                if (index < searchTypes.size) {
                    if (outdoorWeather && searchTypes[index].second ) {
                        viewModel.getNearbySearch(
                            stringCoordinates,
                            searchRadius,
                            searchTypes[index].first,
                            apiKey
                        )
                    }
                        if ((!outdoorWeather || outdoorWeather) && !searchTypes[index].second) {
                            viewModel.getNearbySearch(
                                stringCoordinates,
                                searchRadius,
                                searchTypes[index].first,
                                apiKey
                            )
                        }
                    if (!outdoorWeather && searchTypes[index].second) {
                        index = index + 1
                    }

                }
            })
            var stringCoordinates = "$currentPlaceLat,$currentPlaceLong"
            //autofill the recycler view on creation
            viewModel.getNearbySearch(stringCoordinates, searchRadius, searchTypes[0].first, apiKey)

            }


    }

    private fun setupPlacesAutoComplete(context: Context) {
        val autocompleteFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_place) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(placeFields)

        autocompleteFragment.setOnPlaceSelectedListener(object:PlaceSelectionListener{
            override fun onPlaceSelected(p0: Place) {
                val intent = Intent(context, ViewSingleActivity::class.java)
                intent.putExtra("place_id", p0.id)
                intent.putExtra("lat", currentPlaceLat)
                intent.putExtra("long", currentPlaceLong)
                startActivity(intent)

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