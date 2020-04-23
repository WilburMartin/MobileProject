package com.example.mobileproject
import adapters.ActivityViewHolder
import android.content.Intent
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
import data_classes.NearbySearch
import data_classes.activity
import kotlinx.android.synthetic.main.activity_main.*
import local_database.LocalActivityViewModel
import local_database.localactivity
import weather_api_setup.CityWeather
import weather_api_setup.WeatherViewModel
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    internal var sort = arrayOf("Distance", "Type")
    lateinit var placesClient:PlacesClient
    var sortablePlaces: ArrayList<PlaceLikelihood> = ArrayList<PlaceLikelihood>()
  //  lateinit var searchButton: SearchView
    lateinit var searchBox: EditText
    lateinit var spinner: Spinner
    lateinit var viewModel: ActivityViewModel
    lateinit var weatherView: WeatherViewModel
    lateinit var image:ImageView
    lateinit var imagetexts: TextView
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
    internal var searchRadius = "50000"
    internal var apiKey = "AIzaSyAlI0k8ZhRZuywIpOHH0_9ls5a0JhyF1Pg"

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
//        searchButton = search_button
        searchBox= search_box
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
        image = imageView1
        imagetexts = imagetext
        image.setImageResource(R.drawable.weathercloud)
        imagetexts.setText("Weather!")
        requestPermission()

        initPlaces()

        setupPlacesAutoComplete()

        setupCurrentPlace()

        if (intent != null && intent.getExtras() != null) {
            reload = intent!!.getBooleanExtra("reload", true)
        }

        goToLocalActivities.setOnClickListener() {
            val intent = Intent(this, LocalStorageActivity::class.java)
            startActivity(intent)
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
        if (reload) {
            placesList.clear()
            activityList.clear()
        }
        val request = FindCurrentPlaceRequest.builder(placeFields).build()
        btn_get_current_place.setOnClickListener {
            if(ActivityCompat.checkSelfPermission(this@MainActivity,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                reload = true
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
                    if (!outdoorWeather) {
                        image.setImageResource(R.drawable.bad_weather)
                        imagetexts.setText("Bad Weather! Stay indoors!")
                    }
                    else if (outdoorWeather) {
                        image.setImageResource(R.drawable.sunshine)
                        imagetexts.setText("Good Weather! Have fun!")
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
        return outdoorWeather;
    }
    private fun checkActivityType(activType: String): Boolean{
        var outdoorActivit: Boolean = false;
        outdoorActivit = when(activType) {
            "accounting" -> false
            "airport" -> false
            "amusement_park" -> true
            "aquarium" -> false
            "art_gallery" -> false
            "atm" -> false
            "bakery" -> false
            "bank" -> false
            "bar" -> false
            "beauty_salon" -> false
            "bicycle_store" -> false
            "book_store" -> false
            "bowling_alley" -> false
            "bus_station" -> false
            "cafe" -> false
            "campground" -> true
            "car_dealer" -> false
            "car_rental" -> false
            "car_repair" -> false
            "car_wash" -> false
            "casino" -> false
            "cemetery" -> true
            "church" -> false
            "city_hall" -> false
            "clothing_store" -> false
            "convenience_store" -> false
            "courthouse" -> false
            "dentist" -> false
            "department_store" -> false
            "doctor" -> false
            "drugstore" -> false
            "electrician" -> false
            "electronics_store" -> false
            "embassy" -> false
            "fire_station" -> false
            "florist" -> false
            "funeral_home" -> false
            "furniture_store" -> false
            "gas_station" -> false
            "grocery_or_supermarket" -> false
            "gym" -> false
            "hair_care" -> false
            "hardware_store" -> false
            "hindu_temple" -> false
            "home_goods_store" -> false
            "hospital" -> false
            "insurance_agency" -> false
            "jewelry_store" -> false
            "laundry" -> false
            "lawyer" -> false
            "library" -> false
            "light_rail_station" -> false
            "liquor_store" -> false
            "local_government_office" -> false
            "locksmith" -> false
            "lodging" -> false
            "meal_delivery" -> false
            "meal_takeaway" -> false
            "mosque" -> false
            "movie_rental" -> false
            "movie_theater" -> false
            "moving_company" -> false
            "museum" -> false
            "night_club" -> false
            "painter" -> false
            "park" -> true
            "parking" -> false
            "pet_store" -> false
            "pharmacy" -> false
            "physiotherapist" -> false
            "plumber" -> false
            "police" -> false
            "post_office" -> false
            "primary_school" -> false
            "real_estate_agency" -> false
            "restaurant" -> false
            "roofing_contractor" -> false
            "rv_park" -> false
            "school" -> false
            "secondary_school" -> false
            "shoe_store" -> false
            "shopping_mall" -> false
            "spa" -> false
            "stadium" -> false
            "storage" -> false
            "store" -> false
            "subway_station" -> false
            "supermarket" -> false
            "synagogue" -> false
            "taxi_stand" -> false
            "tourist_attraction" -> false
            "train_station" -> false
            "transit_station" -> false
            "travel_agency" -> false
            "university" -> false
            "veterinary_care" -> false
            "zoo" -> true
            else -> false
        }
        return outdoorActivit;

    }

    private fun getNearbyActivities() {
        //set recycler view
        //placesList.clear()
        checkWeather();
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
                        var activity = activity(result.name, result.types.component1(), result.vicinity, distanceInMiles, result.place_id)


                        var outdoorActivit = false;
                        outdoorActivit = checkActivityType(activity.type);
                        if(!outdoorActivit || outdoorWeather){
                            activityList.add(activity)
                        }


                      //activityList.add(activity)
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

           var stringCoordinates = "$currentPlaceLat,$currentPlaceLong"
            //click listener for when search button is pressed from edit text
            searchBox.setOnEditorActionListener() { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    placesList.clear()
                    activityList.clear()
                    val input: String = searchBox.text.toString()
                    viewModel!!.getNearbySearch(stringCoordinates, searchRadius, input, apiKey)
                    true
                }
                false

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