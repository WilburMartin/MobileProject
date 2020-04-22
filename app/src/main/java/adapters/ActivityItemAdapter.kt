package adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileproject.R
import com.example.mobileproject.ViewSingleActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import data_classes.activity
import local_database.LocalActivityViewModel
import places_api_setup.ActivityViewModel
import java.util.zip.Inflater

class ActivityViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.activity_item, parent, false)) {
    private val activityNameView: TextView
    private val activityTypeView: TextView
    //private val activityDistanceView: TextView
    private val activityLocationView: TextView
    private val activityDistanceView: TextView
    private val addButton: FloatingActionButton


    init {
        activityNameView = itemView.findViewById(R.id.activity_name);
        activityTypeView = itemView.findViewById(R.id.activity_type);
        activityLocationView=itemView.findViewById(R.id.activity_location);
        activityDistanceView = itemView.findViewById(R.id.activity_distance)
        addButton = itemView.findViewById(R.id.add_button)
    }
    fun bind(activity: activity, listener: (activity)->Unit) {
        activityNameView.text = activity.name
        activityTypeView.text = activity.type
        activityLocationView.text = activity.formatted_address
        activityDistanceView.text = activity.distance.toString() + " miles away"
        addButton.setOnClickListener({
            listener(activity)
        })
        activityNameView.setOnClickListener({
            val context = itemView.getContext()
            val intent = Intent(context, ViewSingleActivity::class.java)
            val activity = activity.
            intent!!.putExtra("place_id", activity.place_id)
            context.startActivity(intent)
        })
    }

    class ActivityItemAdapter(private val list: ArrayList<activity>?, val listener: (activity) -> Unit) :
        RecyclerView.Adapter<ActivityViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return ActivityViewHolder(inflater, parent)

        }

        override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
            val activity = list!!.get(position)
            holder.bind(activity, listener)
        }

        override fun getItemCount(): Int = list!!.size

        fun updatePlaylists(activities: List<activity>?) {
            list?.clear()
            list?.addAll(activities!!)
            this.notifyDataSetChanged()
        }

    }
}