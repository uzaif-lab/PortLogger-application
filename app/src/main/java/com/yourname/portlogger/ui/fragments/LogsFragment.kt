package com.yourname.portlogger.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.yourname.portlogger.R
import com.yourname.portlogger.data.LogContract
import com.yourname.portlogger.data.LogDbHelper
import com.yourname.portlogger.databinding.FragmentLogsBinding
import com.yourname.portlogger.ui.adapters.LogAdapter
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.yourname.portlogger.data.LogEntry

class LogsFragment : Fragment(R.layout.fragment_logs) {
    private var _binding: FragmentLogsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var adapter: LogAdapter
    private lateinit var dbHelper: LogDbHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLogsBinding.bind(view)
        
        setupTabs()
        setupRecyclerView()
        loadAllLogs()
    }

    private fun setupTabs() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("All"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Power"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("USB"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("SIM"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> loadAllLogs()
                    1 -> loadLogsByType(LogContract.EventTypes.POWER)
                    2 -> loadLogsByType(LogContract.EventTypes.USB)
                    3 -> loadLogsByType(LogContract.EventTypes.SIM)
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupRecyclerView() {
        adapter = LogAdapter()
        binding.rvLogs.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapter
            itemAnimator = null
        }
    }

    private fun loadAllLogs() {
        dbHelper = LogDbHelper(requireContext())
        val logs = dbHelper.getAllLogs()
        updateUI(logs)
    }

    private fun loadLogsByType(eventType: String) {
        val logs = dbHelper.getLogsByType(eventType)
        updateUI(logs)
    }

    private fun updateUI(logs: List<LogEntry>) {
        binding.tvEmpty.isVisible = logs.isEmpty()
        binding.rvLogs.isVisible = logs.isNotEmpty()
        adapter.updateLogs(logs)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 