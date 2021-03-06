package places_api_setup

import android.media.Image
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.model.LatLng
import data_classes.NearbySearch
import data_classes.place_details
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response

class PlacesRepository {
    //get the instance of retrofit
    val service = PlacesClient.makeRetrofitService()

    //searches for breweries based on string value
    fun getNearbySearch(resBody : MutableLiveData<NearbySearch>, location: String, radius: String, keyword: String, key: String ){
        //set the coroutine on a background thread
        CoroutineScope(Dispatchers.IO).launch {
              var response: Response<NearbySearch>
                response =  service.getNearbySearch(location, radius, keyword, key)

            //when the coroutine finishes
            withContext(Dispatchers.Main){
                try{
                    //success case
                    if(response.isSuccessful){
                        //println(response.body()?.size.toString() + " is the size")
                        resBody.value = response.body()


                    } else{
                        //response error
                        println("HTTP error")
                    }
                }catch (e: HttpException) {
                    //http exception
                    println("HTTP Exception")
                } catch (e: Throwable) {
                    //error
                    println("Error")
                }
            }
        }
    }
    fun locationQuery(resBody : MutableLiveData<place_details>, place_id:String, key: String, fields:String) {
        //set the coroutine on a background thread
        CoroutineScope(Dispatchers.IO).launch {
            var response: Response<place_details>
            response =  service.locationQuery(place_id, key, fields)

            //when the coroutine finishes
            withContext(Dispatchers.Main){
                try{
                    //success case
                    if(response.isSuccessful){
                        //println(response.body()?.size.toString() + " is the size")
                        resBody.value = response.body()


                    } else{
                        //response error
                        println("HTTP error")
                    }
                }catch (e: HttpException) {
                    //http exception
                    println("HTTP Exception")
                } catch (e: Throwable) {
                    //error
                    println("Error")
                }
            }
        }

    }
    fun photoQuery(resBody : MutableLiveData<Image>, key:String, photoreference: String, maxheight:String) {
        //set the coroutine on a background thread
        CoroutineScope(Dispatchers.IO).launch {
            var response: Response<Image>
            response =  service.photoQuery(key, photoreference, maxheight)

            //when the coroutine finishes
            withContext(Dispatchers.Main){
                try{
                    //success case
                    if(response.isSuccessful){
                        //println(response.body()?.size.toString() + " is the size")
                        resBody.value = response.body()


                    } else{
                        //response error
                        println("HTTP error")
                    }
                }catch (e: HttpException) {
                    //http exception
                    println("HTTP Exception")
                } catch (e: Throwable) {
                    //error
                    println("Error")
                }
            }
        }

    }
}