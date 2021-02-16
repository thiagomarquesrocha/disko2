package com.tja.disko2.features.listplace

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tja.disko2.R
import com.tja.disko2.domain.PlaceO2

class AdapterPlaceList() :
    RecyclerView.Adapter<AdapterPlaceList.ViewHolder>(), Filterable {

    var dataSetFiltered: List<PlaceO2> = arrayListOf()

    private lateinit var itemClick: (PlaceO2, View) -> Unit

    var dataSet: List<PlaceO2> = listOf()

    fun setData(newData: List<PlaceO2>) {
        val diffResult = DiffUtil.calculateDiff(MyDiffCallback(dataSetFiltered, newData))
        dataSet = newData
        this.dataSetFiltered = dataSet
        diffResult.dispatchUpdatesTo(this)
    }

    fun setOnClickPlace(itemClick: (PlaceO2, View) -> Unit) {
        this.itemClick = itemClick
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_list_place, parent, false)
        )
    }

    override fun getItemCount(): Int = dataSetFiltered.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(dataSetFiltered[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindView(placeO2: PlaceO2) {

            val tvTitle = itemView.findViewById<TextView>(R.id.tv_title)
            tvTitle.text = placeO2.name
            val tvSubTitle = itemView.findViewById<TextView>(R.id.tv_sutitle)
            val tvLocation = itemView.findViewById<TextView>(R.id.tv_location)

            tvLocation.text =
                if (placeO2.address.isEmpty()) itemView.context.getString(R.string.place_not_address) else placeO2.address
            val ivFavorite = itemView.findViewById<ImageView>(R.id.iv_favorite)
            val container = itemView.findViewById<ConstraintLayout>(R.id.container)
            val cardWpp = itemView.findViewById<CardView>(R.id.card_wpp)
            val cardCall = itemView.findViewById<CardView>(R.id.card_call)

            //Favorite
            if (placeO2.favorite == 1) {
                ivFavorite.setImageResource(R.drawable.icon_heart_red)
            } else {
                ivFavorite.setImageResource(R.drawable.icon_heart_outline)
            }

            //Types
            when (placeO2.type) {
                1 -> {
                    tvSubTitle.text = itemView.context.getString(R.string.place_type_1)
                }
                2 -> {
                    tvSubTitle.text = itemView.context.getString(R.string.place_type_2)
                }
                3 -> {
                    tvSubTitle.text = itemView.context.getString(R.string.place_type_3)
                }
            }

            //Clicks
            cardCall.setOnClickListener { itemClick(placeO2, it) }
            cardWpp.setOnClickListener { itemClick(placeO2, it) }
            ivFavorite.setOnClickListener { itemClick(placeO2, it) }
            container.setOnClickListener { itemClick(placeO2, it) }

            // Disable call button
            if(placeO2.call.isEmpty()){
                cardCall.setEnabled(false)
                cardCall.setAlpha(0.4F)
            }else{
                cardCall.setEnabled(true)
                cardCall.setAlpha(1.0F)
            }
            // Disable whatsapp button
            if(placeO2.whatsapp.isEmpty()){
                cardWpp.setEnabled(false)
                cardWpp.setAlpha(0.4F)
            }else{
                cardWpp.setEnabled(true)
                cardWpp.setAlpha(1.0F)
            }

        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): FilterResults {

                val charString = charSequence.toString()

                if (charString.isEmpty()) {
                    dataSetFiltered = dataSet
                } else {
                    var filteredList: MutableList<PlaceO2> = arrayListOf()
                    dataSet.forEach {
                        if (it.name.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(it)
                        }
                    }
                    dataSetFiltered = filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = dataSetFiltered
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                dataSetFiltered = results?.values as List<PlaceO2>
                notifyDataSetChanged()
            }

        }
    }

}