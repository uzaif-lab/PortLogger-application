package com.yourname.portlogger.ui.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.yourname.portlogger.R
import com.yourname.portlogger.databinding.FragmentPermissionsBinding

class PermissionsFragment : Fragment(R.layout.fragment_permissions) {
    private var _binding: FragmentPermissionsBinding? = null
    private val binding get() = _binding!!

    private val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        arrayOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.FOREGROUND_SERVICE
        )
    } else {
        arrayOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            // All permissions granted
            proceedToNextScreen()
        } else {
            // Some permissions denied
            showPermissionDeniedMessage()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPermissionsBinding.bind(view)

        binding.grantAllButton.setOnClickListener {
            requestPermissions()
        }

        // Check if permissions are already granted
        if (areAllPermissionsGranted()) {
            proceedToNextScreen()
        }
    }

    private fun requestPermissions() {
        permissionLauncher.launch(requiredPermissions)
    }

    private fun areAllPermissionsGranted(): Boolean {
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun showPermissionDeniedMessage() {
        Snackbar.make(
            binding.root,
            "Some permissions are required for the app to function properly",
            Snackbar.LENGTH_LONG
        ).setAction("Grant") {
            requestPermissions()
        }.show()
    }

    private fun proceedToNextScreen() {
        // Save that permissions have been shown
        requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("permissions_shown", true)
            .apply()

        // Navigate to password setup
        findNavController().navigate(R.id.action_permissions_to_passwordSetup)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
