package data_classes

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

data class activity (
    //val result: Result,
    val name: String,
    val type: String,
    val formatted_address: String
)

data class NearbyResponsePayload(val response_code: Int, val data: List<NearbySearch>)




