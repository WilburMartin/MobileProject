//package adapters
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.example.mobileproject.R
//import data_classes.activity
//import local_database.localactivity
//
//class LocalStorageViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
//    RecyclerView.ViewHolder(inflater.inflate(R.layout.local_storage_item, parent, false)) {
//    private val activityNameView: TextView
//    private val activityTypeView: TextView
//    //private val activityDistanceView: TextView
//    private val activityLocationView: TextView
//    private val activityDistanceView: TextView
//
//    init {
//        activityNameView = itemView.findViewById(R.id.activity_name);
//        activityTypeView = itemView.findViewById(R.id.activity_type);
//        activityLocationView=itemView.findViewById(R.id.activity_location);
//        activityDistanceView = itemView.findViewById(R.id.activity_distance)
//    }
//    fun bind(activity: activity) {
//        activityNameView.text = activity.name
//        activityTypeView.text = activity.type
//        activityLocationView.text = activity.formatted_address
//        activityDistanceView.text = activity.distance.toString() + " miles away"
//    }
//
//    class LocalStorageAdapter(private val locallist: ArrayList<localactivity>?) :
//        RecyclerView.Adapter<LocalStorageViewHolder>() {
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalStorageViewHolder {
//            val inflater = LayoutInflater.from(parent.context)
//            return LocalStorageViewHolder(inflater, parent)
//
//        }
//
//        override fun onBindViewHolder(holder: LocalStorageViewHolder, position: Int) {
//            val localactivity = locallist!!.get(position)
//            holder.bind(localactivity)
//        }
//
//        override fun getItemCount(): Int = locallist!!.size
//
//        fun updateActivities(activities: List<localactivity>?) {
//            locallist?.clear()
//            locallist?.addAll(activities!!)
//            this.notifyDataSetChanged()
//        }
//
//    }
//}