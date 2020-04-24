package adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileproject.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import data_classes.activity
import local_database.localactivity

class LocalStorageViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.local_storage_item, parent, false)) {
    private val activityNameView: TextView
    private val activityTypeView: TextView
    //private val activityDistanceView: TextView
    private val activityLocationView: TextView
    private val activityDistanceView: TextView
    private val deleteButton: FloatingActionButton

    init {
        activityNameView = itemView.findViewById(R.id.activity_name);
        activityTypeView = itemView.findViewById(R.id.activity_type);
        activityLocationView=itemView.findViewById(R.id.activity_location);
        activityDistanceView = itemView.findViewById(R.id.activity_distance)
        deleteButton = itemView.findViewById(R.id.delete_button)
    }
    fun bind(localactivity: localactivity, listener: (localactivity)->Unit) {
        activityNameView.text = localactivity.name
        activityTypeView.text = localactivity.type.capitalize()
        activityLocationView.text = localactivity.address
        activityDistanceView.text = localactivity.distance.toString() + " miles away"
        deleteButton.setOnClickListener({
            listener(localactivity)
        })
    }

    class LocalStorageAdapter(private val locallist: ArrayList<localactivity>?, val listener: (localactivity) -> Unit) :
        RecyclerView.Adapter<LocalStorageViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalStorageViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return LocalStorageViewHolder(inflater, parent)

        }

        override fun onBindViewHolder(holder: LocalStorageViewHolder, position: Int) {
            val localactivity = locallist!!.get(position)
            holder.bind(localactivity, listener)
        }

        override fun getItemCount(): Int = locallist!!.size

        fun updateActivities(activities: List<localactivity>?) {
            locallist?.clear()
            locallist?.addAll(activities!!)
            this.notifyDataSetChanged()
        }

    }
}