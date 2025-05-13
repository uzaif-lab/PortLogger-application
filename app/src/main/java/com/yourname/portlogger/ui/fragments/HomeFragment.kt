package com.yourname.portlogger.ui.fragments

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import com.yourname.portlogger.R
import com.yourname.portlogger.databinding.FragmentHomeBinding
import com.yourname.portlogger.MainActivity

class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var isServiceEnabled = false
    private val servicePrefs by lazy {
        requireContext().getSharedPreferences("service_prefs", Context.MODE_PRIVATE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        // Load main toggle state
        isServiceEnabled = servicePrefs.getBoolean("service_enabled", false)
        updateToggleState(isServiceEnabled, animate = false)

        // Load individual service states
        binding.powerServiceToggle.isChecked = servicePrefs.getBoolean("power_service", true)
        binding.usbServiceToggle.isChecked = servicePrefs.getBoolean("usb_service", true)
        binding.simServiceToggle.isChecked = servicePrefs.getBoolean("sim_service", true)

        setupClickListeners()
        updateServiceTogglesEnabled(isServiceEnabled)
    }

    private fun setupClickListeners() {
        binding.toggleCard.setOnClickListener {
            toggleService()
        }

        // Service toggle listeners
        binding.powerServiceToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isServiceEnabled) {
                saveServiceState("power_service", isChecked)
                updateServices()
            }
        }

        binding.usbServiceToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isServiceEnabled) {
                saveServiceState("usb_service", isChecked)
                updateServices()
            }
        }

        binding.simServiceToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isServiceEnabled) {
                saveServiceState("sim_service", isChecked)
                updateServices()
            }
        }
    }

    private fun toggleService() {
        isServiceEnabled = !isServiceEnabled
        updateToggleState(isServiceEnabled, animate = true)

        servicePrefs.edit()
            .putBoolean("service_enabled", isServiceEnabled)
            .apply()

        if (isServiceEnabled) {
            startLoggingService()
        } else {
            stopLoggingService()
        }
    }

    private fun updateServiceTogglesEnabled(enabled: Boolean) {
        binding.powerServiceToggle.isEnabled = enabled
        binding.usbServiceToggle.isEnabled = enabled
        binding.simServiceToggle.isEnabled = enabled
    }

    private fun saveServiceState(service: String, enabled: Boolean) {
        servicePrefs.edit()
            .putBoolean(service, enabled)
            .apply()
    }

    private fun updateServices() {
        if (isServiceEnabled) {
            (activity as? MainActivity)?.updateActiveServices(
                powerEnabled = binding.powerServiceToggle.isChecked,
                usbEnabled = binding.usbServiceToggle.isChecked,
                simEnabled = binding.simServiceToggle.isChecked
            )
        }
    }

    private fun updateToggleState(enabled: Boolean, animate: Boolean) {
        val translationX = if (enabled) 0f else (binding.toggleCard.width - binding.toggleButton.width).toFloat()

        if (animate) {
            ObjectAnimator.ofFloat(binding.toggleButton, "translationX", translationX).apply {
                duration = 300L
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }
        } else {
            binding.toggleButton.translationX = translationX
        }

        binding.toggleText.apply {
            text = if (enabled) "ON" else "OFF"
            setTextColor(context.getColor(
                if (enabled) R.color.power_on else R.color.power_off
            ))
        }

        // Fixed both errors here:
        binding.textHome.text = if (enabled) {
            "Service is running"
        } else {
            "Service is stopped"
        }
    }

    private fun startLoggingService() {
        (activity as? MainActivity)?.startMonitoringServices()
        updateServiceTogglesEnabled(true)
    }

    private fun stopLoggingService() {
        (activity as? MainActivity)?.stopMonitoringServices()
        updateServiceTogglesEnabled(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
