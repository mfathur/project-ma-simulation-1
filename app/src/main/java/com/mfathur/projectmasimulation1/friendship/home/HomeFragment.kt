package com.mfathur.projectmasimulation1.friendship.home

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mfathur.projectmasimulation1.R
import com.mfathur.projectmasimulation1.core.domain.Friend
import com.mfathur.projectmasimulation1.core.util.showLongToastMessage
import com.mfathur.projectmasimulation1.core.util.showShortToastMessage
import com.mfathur.projectmasimulation1.databinding.FragmentHomeBinding
import com.mfathur.projectmasimulation1.friendship.FriendListAdapter

class HomeFragment : Fragment(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: FriendListAdapter

    private var _binding: FragmentHomeBinding? = null

    private val binding
        get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(binding?.toolbar)
        }
        binding?.fabAddFriend?.setOnClickListener(this)

        adapter = FriendListAdapter()
        binding?.rvListFriends?.let {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(requireContext())
        }

        binding?.rvListFriends?.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && binding?.fabAddFriend?.isExtended == true){
                    binding?.fabAddFriend?.shrink()
                } else if (dy < 0 && binding?.fabAddFriend?.isExtended == false) {
                    binding?.fabAddFriend?.extend()
                }
            }
        })

        adapter.onItemClicked = {
            val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment()
            action.friend = it
            findNavController().navigate(action)
        }

        observeFriendData()
    }

    private fun observeFriendData() {
        val list = ArrayList<Friend>()
        val fireStore = FirebaseFirestore.getInstance()
        fireStore.collection("users").document(auth.currentUser!!.uid).collection("friends")
            .addSnapshotListener { querySnapshot, error ->
                if (querySnapshot != null) {
                    for (item in querySnapshot) {
                        list.add(
                            Friend(
                                id = item.id,
                                photoUrl = item.getString("photo_url") ?: "",
                                name = item.getString("name") ?: "",
                                phoneNumber = item.getString("phone_number") ?: "",
                                origin = item.getString("origin") ?: ""
                            )
                        )
                    }
                    adapter.submitList(list)
                }

                if (error != null) {
                    context?.showLongToastMessage(error.message.toString())
                }
            }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                auth.signOut()
                val action = HomeFragmentDirections.actionHomeFragmentToLoginFragment()
                findNavController().navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.fab_add_friend -> {
                val action = HomeFragmentDirections.actionHomeFragmentToAddFriendFragment()
                findNavController().navigate(action)
            }
        }
    }
}