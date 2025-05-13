package com.yourname.portlogger

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.Menu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.*
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.yourname.portlogger.data.LogContract
import com.yourname.portlogger.data.LogDbHelper
import com.yourname.portlogger.databinding.ActivityMainBinding
import com.yourname.portlogger.security.PasswordManager
import com.yourname.portlogger.utils.PowerStateManager
import com.yourname.portlogger.utils.UsbMonitor
import com.yourname.portlogger.workers.PowerMonitorWorker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var powerStateManager: PowerStateManager
    private lateinit var usbMonitor: UsbMonitor
    private var isServiceRunning = false
    private var powerMonitorEnabled = true
    private var usbMonitorEnabled = true
    private var simMonitorEnabled = true
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, initialize SIM monitoring
            initSimMonitoring()
        }
    }
    private lateinit var passwordManager: PasswordManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        passwordManager = PasswordManager(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_all_logs,
                R.id.nav_switch,
                R.id.nav_usb,
                R.id.nav_sim
            ),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        powerStateManager = PowerStateManager(this)
        
        // Check if device was potentially powered off
        if (powerStateManager.checkPowerGap()) {
            // TODO: Log potential power off event
            Log.d("PowerState", "Detected potential power off before current launch")
        }

        // Check if service should be running
        if (getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                .getBoolean("service_enabled", false)) {
            startMonitoringServices()
        }

        checkPermissions()

        // Check if this is first launch
        val isFirstLaunch = !getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .getBoolean("permissions_shown", false)

        if (isFirstLaunch) {
            navController.navigate(R.id.nav_permissions)
        } else if (!passwordManager.isPasswordSet()) {
            navController.navigate(R.id.nav_password_setup)
        } else {
            navController.navigate(R.id.nav_login)
        }
    }

    override fun onResume() {
        super.onResume()
        powerStateManager.recordActivityTimestamp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun checkPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission already granted
                initSimMonitoring()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE) -> {
                // Show explanation why permission is needed
                showPermissionRationale()
            }
            else -> {
                // Request permission
                requestPermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
            }
        }
    }

    private fun showPermissionRationale() {
        // Simple dialog explaining why permission is needed
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("This permission is needed to monitor SIM card status.")
            .setPositiveButton("Grant") { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
            }
            .setNegativeButton("Deny") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun initSimMonitoring() {
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val dbHelper = LogDbHelper(this)
        
        val message = when (tm.simState) {
            TelephonyManager.SIM_STATE_ABSENT -> "No SIM card present"
            TelephonyManager.SIM_STATE_READY -> {
                val operatorName = tm.simOperatorName.takeIf { !it.isNullOrBlank() } ?: "Unknown"
                "SIM card ready: $operatorName"
            }
            TelephonyManager.SIM_STATE_PIN_REQUIRED -> "SIM PIN required"
            TelephonyManager.SIM_STATE_PUK_REQUIRED -> "SIM PUK required"
            TelephonyManager.SIM_STATE_NETWORK_LOCKED -> "SIM network locked"
            TelephonyManager.SIM_STATE_NOT_READY -> "SIM not ready"
            TelephonyManager.SIM_STATE_PERM_DISABLED -> "SIM permanently disabled"
            TelephonyManager.SIM_STATE_CARD_IO_ERROR -> "SIM card I/O error"
            TelephonyManager.SIM_STATE_CARD_RESTRICTED -> "SIM card restricted"
            TelephonyManager.SIM_STATE_UNKNOWN -> "SIM state unknown"
            else -> "Unknown SIM state: ${tm.simState}"
        }
        
        dbHelper.insertLog(message, LogContract.EventTypes.SIM)
    }

    fun updateActiveServices(
        powerEnabled: Boolean,
        usbEnabled: Boolean,
        simEnabled: Boolean
    ) {
        powerMonitorEnabled = powerEnabled
        usbMonitorEnabled = usbEnabled
        simMonitorEnabled = simEnabled

        if (isServiceRunning) {
            // Update power monitoring
            if (powerEnabled) {
                startPowerMonitoring()
            } else {
                WorkManager.getInstance(applicationContext).cancelUniqueWork("power_monitoring")
            }

            // Update USB monitoring
            if (usbEnabled) {
                if (!::usbMonitor.isInitialized) {
                    usbMonitor = UsbMonitor(this)
                }
                usbMonitor.startMonitoring()
            } else if (::usbMonitor.isInitialized) {
                usbMonitor.stopMonitoring()
            }

            // Update SIM monitoring
            if (simEnabled) {
                initSimMonitoring()
            }
        }
    }

    fun startMonitoringServices() {
        if (!isServiceRunning) {
            if (powerMonitorEnabled) {
                startPowerMonitoring()
            }
            if (usbMonitorEnabled) {
                usbMonitor = UsbMonitor(this)
                usbMonitor.startMonitoring()
            }
            if (simMonitorEnabled) {
                initSimMonitoring()
            }
            isServiceRunning = true
        }
    }

    fun stopMonitoringServices() {
        if (isServiceRunning) {
            WorkManager.getInstance(applicationContext).cancelUniqueWork("power_monitoring")
            if (::usbMonitor.isInitialized) {
                usbMonitor.stopMonitoring()
            }
            isServiceRunning = false
        }
    }

    private fun startPowerMonitoring() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        val powerMonitorRequest = PeriodicWorkRequestBuilder<PowerMonitorWorker>(
            15, TimeUnit.MINUTES,
            5, TimeUnit.MINUTES
        ).setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueueUniquePeriodicWork(
                "power_monitoring",
                ExistingPeriodicWorkPolicy.UPDATE,
                powerMonitorRequest
            )
    }
}