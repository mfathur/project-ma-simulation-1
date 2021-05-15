package com.mfathur.projectmasimulation1.friendship

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.mfathur.projectmasimulation1.R
import com.mfathur.projectmasimulation1.core.domain.Friend
import com.mfathur.projectmasimulation1.databinding.ItemFriendBinding

class FriendViewHolder(
    private val binding: ItemFriendBinding,
    private val onItemFriendClicked: ((Friend) -> Unit)?
) : RecyclerView.ViewHolder(binding.root) {

    private val requestOptions = RequestOptions().error(R.drawable.item_friend_img_placeholder)
        .placeholder(R.drawable.item_friend_img_placeholder)

    fun bind(item: Friend) {
        with(binding) {
            Glide.with(itemView).setDefaultRequestOptions(requestOptions).load(item.photoUrl)
                .transform(CircleCrop()).into(imgFriend)

            tvFriendName.text = item.name
            tvFriendOrigin.text = item.origin

            root.setOnClickListener { onItemFriendClicked?.invoke(item) }
        }
    }

    companion object {
        fun create(parent: ViewGroup, onItemClicked: ((Friend) -> Unit)?): FriendViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false)
            val binding = ItemFriendBinding.bind(view)
            return FriendViewHolder(binding, onItemClicked)
        }
    }

}