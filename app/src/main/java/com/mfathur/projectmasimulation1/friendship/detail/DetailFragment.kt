package com.mfathur.projectmasimulation1.friendship.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mfathur.projectmasimulation1.R
import com.mfathur.projectmasimulation1.databinding.FragmentDetailBinding

class DetailFragment : Fragment(), View.OnClickListener {

    private val args: DetailFragmentArgs by navArgs()

    private var _binding: FragmentDetailBinding? = null

    private val binding
        get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.btnBackToHomeScreen?.setOnClickListener(this)

        val requestOptions = RequestOptions().error(R.drawable.item_friend_img_placeholder)
            .placeholder(R.drawable.item_friend_img_placeholder)

        binding?.imgFriendUser?.let {
            Glide.with(view).setDefaultRequestOptions(requestOptions).load(args.friend?.photoUrl)
                .into(it)
        }

        binding?.tvOriginProvince?.text = args.friend?.origin
        binding?.tvFriendPhoneNumber?.text = args.friend?.phoneNumber
        binding?.tvFriendName?.text = args.friend?.name
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_back_to_home_screen -> activity?.onBackPressed()
        }
    }

}