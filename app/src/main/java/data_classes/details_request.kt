package data_classes

data class place_details(
    val html_attributions: List<Any>,
    val result: detailsResult,
    val status: String
)

data class detailsResult(
    val name: String,
    val opening_hours: OpeningHours,
    val formatted_address:String,
    val formatted_phone_number: String,
    val rating: Double,
    val types: List<String>,
    val photos: List<Photo>,
    val website: String

)

data class AddressComponent(
    val long_name: String,
    val short_name: String,
    val types: List<String>
)


data class Review(
    val author_name: String,
    val author_url: String,
    val language: String,
    val profile_photo_url: String,
    val rating: Int,
    val relative_time_description: String,
    val text: String,
    val time: Int
)

data class detailsLocation(
    val lat: Double,
    val lng: Double
)

data class Viewport(
    val northeast: Northeast,
    val southwest: Southwest
)

data class Northeast(
    val lat: Double,
    val lng: Double
)

data class Southwest(
    val lat: Double,
    val lng: Double
)