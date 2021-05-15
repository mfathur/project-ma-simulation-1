package com.mfathur.projectmasimulation1.friendship.addfriend

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.mfathur.projectmasimulation1.R
import com.mfathur.projectmasimulation1.core.util.CustomActivityResultContracts
import com.mfathur.projectmasimulation1.core.util.showLongToastMessage
import com.mfathur.projectmasimulation1.core.util.showShortToastMessage
import com.mfathur.projectmasimulation1.databinding.FragmentAddFriendBinding

class AddFriendFragment : Fragment(), View.OnClickListener {

    private lateinit var fireStore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>
    private var uri: Uri? = null

    private var _binding: FragmentAddFriendBinding? = null

    private val binding
        get() = _binding

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                cropActivityResultLauncher.launch(null)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddFriendBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.btnAddNewFriend?.setOnClickListener(this)
        binding?.fabAddNewFriendPhoto?.setOnClickListener(this)
        binding?.btnBackToHomeScreen?.setOnClickListener(this)
        fireStore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        storage = Firebase.storage

        cropActivityResultLauncher =
            registerForActivityResult(CustomActivityResultContracts.cropImageActivityResultContract) {
                uri = it
                val requestOptions = RequestOptions.circleCropTransform()
                    .placeholder(R.drawable.item_friend_img_placeholder)
                    .error(R.drawable.ic_baseline_broken_image_24)

                binding?.let { imageView ->
                    if (it != null) {
                        Glide.with(view).setDefaultRequestOptions(requestOptions).load(it)
                            .circleCrop()
                            .into(imageView.imgNewFriend)
                    }
                }
            }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_back_to_home_screen -> activity?.onBackPressed()
            R.id.btn_add_new_friend -> {
                if (validateUserInput()) {
                    binding?.btnAddNewFriend?.isEnabled = false
                    context?.showLongToastMessage(getString(R.string.uploading))
                    saveNewFriendData()
                }
            }
            R.id.fab_add_new_friend_photo -> {
                when {
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        cropActivityResultLauncher.launch(null)
                    }

                    shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                        val alertDialog = AlertDialog.Builder(v.context)
                        alertDialog.setTitle(getString(R.string.request_camera_title))
                        alertDialog.setMessage(getString(R.string.request_camera_desc))
                        alertDialog.setPositiveButton(getString(R.string.okay)) { _, _ ->
                            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                        alertDialog.setNegativeButton(getString(R.string.no_thanks)) { _, _ -> }
                        alertDialog.show()
                    }

                    else -> {
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
            }
        }
    }

    private fun saveNewFriendData() {
        val newFriendDataRef =
            fireStore.collection("users").document(auth.currentUser!!.uid).collection("friends")

        val storageRef =
            storage.reference.child("images/${auth.currentUser!!.uid}-${System.currentTimeMillis()}.jpg")

        if (uri != null) {
            try {
                storageRef.putFile(uri!!).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        storageRef.downloadUrl.addOnSuccessListener { url ->
                            uri = url
                            uploadDataToFireStore(newFriendDataRef)
                        }
                    } else {
                        task.exception?.let { e ->
                            context?.showLongToastMessage(e.message.toString())
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("saveNewFriendData", e.message.toString())
                context?.showShortToastMessage(getString(R.string.error_upload_failed))
            }
        } else {
            uploadDataToFireStore(newFriendDataRef)
        }
    }

    private fun uploadDataToFireStore(collectionRef: CollectionReference) {
        try {
            val newFriendData = hashMapOf(
                "id" to collectionRef.id,
                "name" to binding?.etFriendName?.text.toString(),
                "origin" to binding?.etFriendOrigin?.text.toString(),
                "phone_number" to binding?.etFriendPhoneNumber?.text.toString(),
                "photo_url" to uri.toString()
            )

            collectionRef.add(newFriendData).addOnSuccessListener {
                context?.showLongToastMessage(getString(R.string.upload_complete))
                activity?.onBackPressed()
            }
        } catch (e: Exception) {
            Log.e("uploadDataToFireStore", e.message.toString())
            context?.showShortToastMessage(getString(R.string.error_upload_failed))
        }

    }

    private fun validateUserInput(): Boolean {
        var isValidated = true
        val name = binding?.etFriendName?.text
        val phoneNumber = binding?.etFriendPhoneNumber?.text
        val origin = binding?.etFriendOrigin?.text

        if (name.isNullOrEmpty()) {
            binding?.etFriendName?.error = getString(R.string.error_empty_field)
            isValidated = false
        }

        if (phoneNumber.isNullOrEmpty()) {
            binding?.etFriendPhoneNumber?.error = getString(R.string.error_empty_field)
            isValidated = false
        }

        if (origin.isNullOrEmpty()) {
            binding?.etFriendOrigin?.error = getString(R.string.error_empty_field)
            isValidated = false
        }

        return isValidated
    }
}