package com.monday8am.cycleradar.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.monday8am.cycleradar.R
import com.monday8am.cycleradar.data.Cyclist

class PhotoListAdapter(private val models: List<Cyclist>): RecyclerView.Adapter<PhotoListAdapter.ViewHolder>(){

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = models[position]
        when (model.imageUrl) {
            null ->  holder.spinner.visibility = if (model.completed) View.GONE else View.VISIBLE
            else -> {
                Glide
                    .with(holder.itemView)
                    .load(model.imageUrl)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.imageView)
            }
        }
    }

    override fun getItemCount(): Int {
        return models.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(v)
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imageView: ImageView = itemView.findViewById(R.id.imageViewBig)
        val spinner: ProgressBar = itemView.findViewById(R.id.progressBar)
    }
}
