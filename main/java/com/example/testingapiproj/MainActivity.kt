package com.example.testingapiproj

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: TriviaViewModel
    var questionList: ArrayList<CityWeather> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(TriviaViewModel::class.java)

        viewModel!!.categoryList.observe(this, Observer {
            questionList.clear()
            questionList.add(it)

            //centertext.text = questionList.get(0).weather.get(0).description; //For Weather Long Desc
            //centertext.text = questionList.get(0).weather.get(0).main; //For Weather Short Desc
            //centertext.text = questionList.get(0).visibility; //For Visibility

        })

        viewModel.getCategories("London");
    }
}
