package adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileproject.R
import data_classes.activity
import java.util.zip.Inflater

class ActivityViewHolder(inflater: LayoutInflater, parent: ViewGroup ) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.activity_item, parent, false)) {
    private val activityNameView: TextView
    private val activityTypeView: TextView
   //private val activityDistanceView: TextView
    private val activityLocationView: TextView

    init {
        activityNameView = itemView.findViewById(R.id.activity_name);
        activityTypeView = itemView.findViewById(R.id.activity_type);
        activityLocationView=itemView.findViewById(R.id.activity_location);
    }
    fun bind(activity: activity) {
        activityNameView.text = activity.name
        activityTypeView.text = activity.type
        activityLocationView.text = activity.formatted_address
    }

    class ActivityItemAdapter(private val list: ArrayList<activity>?) :
        RecyclerView.Adapter<ActivityViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return ActivityViewHolder(inflater, parent)

        }

        override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
            val activity = list!!.get(position)
            holder.bind(activity)
        }

        override fun getItemCount(): Int = list!!.size

        fun updatePlaylists(activities: List<activity>?) {
            list?.clear()
            list?.addAll(activities!!)
            this.notifyDataSetChanged()
        }

    }
}