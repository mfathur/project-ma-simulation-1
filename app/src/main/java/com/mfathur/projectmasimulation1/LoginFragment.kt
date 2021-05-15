package com.mfathur.projectmasimulation1

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.mfathur.projectmasimulation1.databinding.FragmentLoginBinding

class LoginFragment : Fragment(), View.OnClickListener {

    companion object {
        private const val EXTRA_EMAIL = "extra_email"
        private const val EXTRA_PASSWORD = "extra_password"
    }

    private lateinit var auth: FirebaseAuth

    private var _binding: FragmentLoginBinding? = null

    private val binding
        get() = _binding

    override fun onStart() {
        super.onStart()
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            navigateToHomeFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.btnLogin?.setOnClickListener(this)
        binding?.tvForgotPassword?.setOnClickListener(this)
        binding?.tvToSignUpScreen?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_login -> {
                val emailInput = binding?.etEmail?.text?.trim()
                val passwordInput = binding?.etPassword?.text
                if (validateEmailAndPasswordInput(emailInput, passwordInput)) {
                    showLoadingState(true)
                    login(emailInput.toString(), passwordInput.toString())
                    binding?.btnLogin?.isEnabled = false
                }
            }
            R.id.tv_to_sign_up_screen -> {
                val action = LoginFragmentDirections.actionLoginFragmentToSignUpFragment()
                findNavController().navigate(action)
            }
            R.id.tv_forgot_password -> {
                requireContext().showShortToastMessage("Forgot password clicked")
            }
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

            !CustomPattern.PASSWORD_PATTERN.matcher(passwordInput).matches() -> {
                binding?.etPassword?.error = getString(R.string.error_weak_password)
                isValidated = false
            }
        }

        return isValidated
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            navigateToHomeFragment()
            binding?.btnLogin?.isEnabled = true
            showLoadingState(false)
        }.addOnFailureListener {
            requireContext().showLongToastMessage("Login Failed")
            binding?.btnLogin?.isEnabled = true
            showLoadingState(false)
        }


    }

    private fun navigateToHomeFragment() {
        val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
        findNavController().navigate(action)
    }

    private fun showLoadingState(state: Boolean) {
        if (state) {
            binding?.progressBar?.visibility = View.VISIBLE
        } else {
            binding?.progressBar?.visibility = View.GONE
        }
    }


}