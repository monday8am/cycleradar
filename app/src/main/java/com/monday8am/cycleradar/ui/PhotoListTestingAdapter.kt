package com.monday8am.cycleradar.ui

import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.monday8am.cycleradar.R
import com.monday8am.cycleradar.data.Cyclist
import kotlin.math.roundToInt

class PhotoListTestingAdapter(private val models: List<Cyclist>): RecyclerView.Adapter<PhotoListTestingAdapter.ViewHolder>(){

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = models[position]

        holder.titleTextView.text = "id:${model.cyclistId} Loc:${model.latitude}/${model.longitude}"
        holder.subtitleTextView.text = "Distance: ${distanceWithPrevious(position)} meters"
        if (model.imageUrl != null) {
            Glide
                .with(holder.itemView)
                .load(model.imageUrl)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.iconImageView)
        }
    }

    override fun getItemCount(): Int {
        return models.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.test_item_layout, parent, false)
        return ViewHolder(v)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val titleTextView: TextView = itemView.findViewById(R.id.title)
        val subtitleTextView: TextView  = itemView.findViewById(R.id.subtitle)
        val iconImageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    private fun distanceWithPrevious(position: Int): Int {
        if (position == 0) {
            return 0
        }

        val locationA = Location("A")
        locationA.longitude = models[position].longitude
        locationA.latitude = models[position].latitude

        val locationB = Location("B")
        locationB.longitude = models[position - 1].longitude
        locationB.latitude = models[position - 1].latitude

        return locationA.distanceTo(locationB).roundToInt()
    }
}
