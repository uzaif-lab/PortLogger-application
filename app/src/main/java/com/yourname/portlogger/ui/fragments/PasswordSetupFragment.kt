package com.yourname.portlogger.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.yourname.portlogger.R
import com.yourname.portlogger.databinding.FragmentPasswordSetupBinding
import com.yourname.portlogger.security.PasswordManager

class PasswordSetupFragment : Fragment(R.layout.fragment_password_setup) {
    private var _binding: FragmentPasswordSetupBinding? = null
    private val binding get() = _binding!!
    private lateinit var passwordManager: PasswordManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPasswordSetupBinding.bind(view)
        passwordManager = PasswordManager(requireContext())

        binding.submitButton.setOnClickListener {
            val password = binding.passwordInput.text.toString()
            val confirmPassword = binding.confirmPasswordInput.text.toString()

            when {
                password.length != 4 -> {
                    showError("PIN must be 4 digits")
                }
                password != confirmPassword -> {
                    showError("PINs do not match")
                }
                else -> {
                    passwordManager.setPassword(password)
                    findNavController().navigate(R.id.action_passwordSetup_to_home)
                }
            }
        }
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 