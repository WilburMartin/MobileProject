package data_classes

import android.location.Location
import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class NearbySearch(
    val html_attributions: List<Any>,
    val results: List<Result>,
    val status: String
)

data class Result(
    val geometry: Geometry,
    val icon: String,
    val id: String,
    val name: String,
    val opening_hours: OpeningHours,
    val photos: List<Photo>,
    val place_id: String,
    val reference: String,
    val types: List<String>,
    val vicinity: String
)


data class activity(
    //val result: Result,
    val name: String,
    val type: String,
    val formatted_address: String,
    val distance: Double,
    val place_id: String,
    val fromCurrentLocation: Location

)

data class NearbyResponsePayload(val response_code: Int, val data: List<NearbySearch>)





