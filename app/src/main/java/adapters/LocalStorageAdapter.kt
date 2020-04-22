package adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileproject.R
import data_classes.activity
import local_database.LocalActivityViewModel
import local_database.localactivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders

import adapters.LocalStorageViewHolder.LocalStorageAdapter.OnDeleteButtonClickListener

import android.view.View








interface RecyclerViewClickListener {

    fun onClick(view: View, position: Int)
}

 class LocalStorageViewHolder(inflater: LayoutInflater, parent: ViewGroup, listener: RecyclerViewClickListener) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.local_storage_item, parent, false)) {
     private val mListener: RecyclerViewClickListener?
    private val activityNameView: TextView
    private val activityTypeView: TextView
    //private val activityDistanceView: TextView
    private val activityLocationView: TextView
    private val activityDistanceView: TextView
    private val deleteButton: Button

    init {
        mListener=listener
        activityNameView = itemView.findViewById(R.id.activity_name);
        activityTypeView = itemView.findViewById(R.id.activity_type);
        activityLocationView = itemView.findViewById(R.id.activity_location);
        activityDistanceView = itemView.findViewById(R.id.activity_distance)
        deleteButton = itemView.findViewById(R.id.delete_button)

    }

    fun bind(activity: localactivity) {
        activityNameView.text = activity.name
        activityTypeView.text = activity.type
        activityLocationView.text = activity.address
        activityDistanceView.text = activity.distance.toString() + " miles away"
        deleteButton.setOnClickListener({
            mListener?.onClick(it, adapterPosition)

        })


    }


    class LocalStorageAdapter(context: Context, private val locallist: ArrayList<localactivity>?, listener: RecyclerViewClickListener) :
        RecyclerView.Adapter<LocalStorageViewHolder>() {
        var context = context
        private val mListener = listener


        interface OnDeleteButtonClickListener {
            fun onDeleteButtonClicked(localactivity: localactivity)
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalStorageViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return LocalStorageViewHolder(inflater, parent, mListener)

        }

        override fun onBindViewHolder(holder: LocalStorageViewHolder, position: Int) {
            val localactivity = locallist!!.get(position)
            holder.bind(localactivity)
        }

        override fun getItemCount(): Int = locallist!!.size

        fun updateActivities(activities: List<localactivity>?) {
            locallist?.clear()
            locallist?.addAll(activities!!)
            this.notifyDataSetChanged()
        }

    }
}