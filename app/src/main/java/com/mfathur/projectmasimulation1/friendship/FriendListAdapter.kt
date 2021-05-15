package com.mfathur.projectmasimulation1.friendship

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.mfathur.projectmasimulation1.core.domain.Friend

class FriendListAdapter : ListAdapter<Friend, FriendViewHolder>(FRIEND_COMPARATOR) {

    var onItemClicked: ((Friend) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        return FriendViewHolder.create(parent, onItemClicked)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val FRIEND_COMPARATOR = object : DiffUtil.ItemCallback<Friend>() {
            override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean {
                return newItem.id == oldItem.id
            }

            override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean {
                return newItem == oldItem
            }
        }
    }
}