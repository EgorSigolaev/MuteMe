package com.egorsigolaev.muteme.presentation.screens.addplace

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.egorsigolaev.muteme.R
import com.egorsigolaev.muteme.data.models.network.SearchPlace
import kotlinx.android.synthetic.main.cell_search_place.view.*

class SearchPlaceAdapter(val searchPlaceClickListener: SearchPlaceClickListener?): RecyclerView.Adapter<SearchPlaceAdapter.SearchPlaceHolder>() {

    private val places = mutableListOf<SearchPlace>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchPlaceHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cell_search_place, parent, false)
        return SearchPlaceHolder(view)
    }

    override fun getItemCount(): Int {
        return places.size
    }

    override fun onBindViewHolder(holder: SearchPlaceHolder, position: Int) {
        holder.bind(place = places[position])
    }

    fun submitList(places: List<SearchPlace>?){
        places?.let {
            this.places.clear()
            this.places.addAll(it)
        } ?: run{
            this.places.clear()
        }
        notifyDataSetChanged()
    }

    interface SearchPlaceClickListener{
        fun onSearchPlaceClick(place: SearchPlace)
    }

    inner class SearchPlaceHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewPlaceName = itemView.textViewPlaceName
        private val textViewPlaceDescription = itemView.textViewPlaceDescription

        fun bind(place: SearchPlace){
            itemView.setOnClickListener{
                searchPlaceClickListener?.onSearchPlaceClick(place = places[adapterPosition])
            }
            textViewPlaceName.text = place.formatted_info.mainText
            textViewPlaceDescription.text = place.formatted_info.secondaryText
        }
    }

}