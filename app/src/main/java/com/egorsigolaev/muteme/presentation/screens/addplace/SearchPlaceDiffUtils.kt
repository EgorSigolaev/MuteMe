package com.egorsigolaev.muteme.presentation.screens.addplace

import androidx.recyclerview.widget.DiffUtil
import com.egorsigolaev.muteme.data.models.network.SearchPlace

class SearchPlaceDiffUtils(private val oldList: List<SearchPlace>, val newList: List<SearchPlace>?): DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newList != null && oldList[oldItemPosition].formatted_info.mainText == newList[newItemPosition].formatted_info.mainText &&
                oldList[oldItemPosition].formatted_info.secondaryText == newList[newItemPosition].formatted_info.secondaryText
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList?.size ?: 0
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return false
    }
}