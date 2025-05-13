package com.yourname.portlogger.ui.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.yourname.portlogger.R
import com.yourname.portlogger.databinding.FragmentLoginBinding
import com.yourname.portlogger.security.PasswordManager

class LoginFragment : Fragment(R.layout.fragment_login) {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var passwordManager: PasswordManager
    private var timer: CountDownTimer? = null

    private companion object {
        const val MAX_ATTEMPTS = 5
        const val LOCKOUT_DURATION_MS = 60000L
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)
        passwordManager = PasswordManager(requireContext())

        checkLockoutStatus()

        binding.loginButton.setOnClickListener {
            val password = binding.passwordInput.text.toString()

            if (password.length != 4) {
                showError(getString(R.string.error_pin_length))
                return@setOnClickListener
            }

            if (passwordManager.verifyPassword(password)) {
                passwordManager.resetLoginAttempts()
                findNavController().navigate(R.id.action_login_to_home)
            } else {
                handleFailedAttempt()
            }
        }

        updateAttemptsText()
    }

    private fun handleFailedAttempt() {
        passwordManager.incrementLoginAttempts()
        val attempts = passwordManager.getLoginAttempts()

        if (attempts >= MAX_ATTEMPTS) {
            passwordManager.setLastAttemptTime(System.currentTimeMillis())
            startLockoutTimer()
        } else {
            showError(getString(R.string.error_incorrect_pin_attempts, MAX_ATTEMPTS - attempts))
        }

        updateAttemptsText()
    }

    private fun checkLockoutStatus() {
        val lastAttemptTime = passwordManager.getLastAttemptTime()
        val timeElapsed = System.currentTimeMillis() - lastAttemptTime

        if (timeElapsed < LOCKOUT_DURATION_MS && passwordManager.getLoginAttempts() >= MAX_ATTEMPTS) {
            startLockoutTimer(LOCKOUT_DURATION_MS - timeElapsed)
        }
    }

    private fun startLockoutTimer(remainingTime: Long = LOCKOUT_DURATION_MS) {
        binding.loginButton.isEnabled = false
        binding.passwordInput.isEnabled = false

        timer = object : CountDownTimer(remainingTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.attemptsText.text = getString(
                    R.string.error_lockout_time,
                    millisUntilFinished / 1000
                )
            }

            override fun onFinish() {
                passwordManager.resetLoginAttempts()
                binding.loginButton.isEnabled = true
                binding.passwordInput.isEnabled = true
                updateAttemptsText()
            }
        }.start()
    }

    private fun updateAttemptsText() {
        when (val attempts = passwordManager.getLoginAttempts()) {
            in 1..4 -> binding.attemptsText.text = getString(
                R.string.attempts_remaining,
                MAX_ATTEMPTS - attempts
            )
            0 -> binding.attemptsText.text = ""
        }
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        _binding = null
    }
}
