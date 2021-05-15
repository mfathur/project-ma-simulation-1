package com.mfathur.projectmasimulation1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.mfathur.projectmasimulation1.databinding.ItemFriendBinding

class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val binding = ItemFriendBinding.bind(itemView)

    private val requestOptions = RequestOptions().error(R.drawable.ic_baseline_broken_image_24)
        .placeholder(R.drawable.item_friend_img_placeholder)

    fun bind(item: Friend) {
        with(binding) {
            Glide.with(itemView).setDefaultRequestOptions(requestOptions).load(item.photoUrl)
                .transform(CircleCrop()).into(imgFriend)

            tvFriendName.text = item.name
            tvFriendOrigin.text = item.origin
        }
    }

    companion object {
        fun create(parent: ViewGroup): FriendViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false)
            return FriendViewHolder(view)
        }
    }

}