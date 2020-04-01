package com.example.mobileapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileapp.R
import com.example.mobileapp.data.Activity
import java.util.zip.Inflater

class ActivityViewHolder(inflater: LayoutInflater, parent: ViewGroup ) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.activity_item, parent, false)) {
    private val activityNameView: TextView
    private val activityTypeView: TextView
    private val activityDistanceView: TextView
    private val activityLocationView: TextView

    init {
        activityNameView = itemView.findViewById(R.id.activity_name);
        activityTypeView = itemView.findViewById(R.id.activity_type);
        activityDistanceView=itemView.findViewById(R.id.activity_distance);
        activityLocationView=itemView.findViewById(R.id.activity_location);
    }
    fun bind(activity: Activity) {
        activityNameView.text = activity.name
        activityTypeView.text = activity.type
        activityDistanceView.text = activity.distance.toString()
        activityLocationView.text = activity.location
    }

    class ActivityAdapter(private val list: ArrayList<Activity>?) :
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

        fun updatePlaylists(activities: List<Activity>?) {
            list?.clear()
            list?.addAll(activities!!)
            this.notifyDataSetChanged()
        }

    }
}