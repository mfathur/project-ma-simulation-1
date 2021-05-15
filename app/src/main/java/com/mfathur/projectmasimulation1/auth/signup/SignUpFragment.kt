package com.mfathur.projectmasimulation1.auth.signup

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.mfathur.projectmasimulation1.R
import com.mfathur.projectmasimulation1.core.util.CustomPatterns
import com.mfathur.projectmasimulation1.core.util.showLongToastMessage
import com.mfathur.projectmasimulation1.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth

    private var _binding: FragmentSignUpBinding? = null

    private val binding
        get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.btnSignUp?.setOnClickListener(this)
        binding?.tvToLoginScreen?.setOnClickListener(this)

        auth = FirebaseAuth.getInstance()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_sign_up -> {
                val emailInput = binding?.etEmail?.text?.trim()
                val passwordInput = binding?.etPassword?.text
                if (validateEmailAndPasswordInput(emailInput, passwordInput)) {
                    signUp(emailInput.toString(), passwordInput.toString())
                    binding?.btnSignUp?.isEnabled = false
                }
            }
            R.id.tv_to_login_screen -> activity?.onBackPressed()
        }
    }

    private fun validateEmailAndPasswordInput(
        emailInput: CharSequence?,
        passwordInput: CharSequence?
    ): Boolean {
        var isValidated = true

        when {
            emailInput.isNullOrEmpty() -> {
                binding?.etEmail?.error = getString(R.string.error_empty_email)
                isValidated = false
            }

            !Patterns.EMAIL_ADDRESS.matcher(emailInput).matches() -> {
                binding?.etEmail?.error = getString(R.string.error_invalid_email)
                isValidated = false
            }
        }

        when {
            passwordInput.isNullOrEmpty() -> {
                binding?.etPassword?.error = getString(R.string.error_empty_password)
                isValidated = false
            }

            passwordInput.length < 8 -> {
                binding?.etPassword?.error = getString(R.string.error_insufficient_password)
                isValidated = false
            }

            !CustomPatterns.PASSWORD_PATTERN.matcher(passwordInput).matches() -> {
                binding?.etPassword?.error = getString(R.string.error_weak_password)
                isValidated = false
            }
        }

        return isValidated
    }

    private fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            binding?.btnSignUp?.isEnabled = true
            val action = SignUpFragmentDirections.actionSignUpFragmentToHomeFragment()
            findNavController().navigate(action)
        }.addOnFailureListener {
            context?.showLongToastMessage(it.message.toString())
            binding?.btnSignUp?.isEnabled = true
        }
    }

}