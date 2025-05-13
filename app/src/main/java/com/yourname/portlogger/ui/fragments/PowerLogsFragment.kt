package com.yourname.portlogger.ui.fragments

import android.os.Bundle
import android.view.View
import com.yourname.portlogger.data.LogContract
import com.yourname.portlogger.data.LogDbHelper
import com.yourname.portlogger.data.LogEntry
import com.yourname.portlogger.databinding.FragmentLogsBinding
import com.yourname.portlogger.ui.adapters.LogAdapter

class PowerLogsFragment : BaseLogsFragment() {
    private lateinit var powerAdapter: LogAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentLogsBinding.bind(view)
        setupRecyclerView()
        loadLogs()
    }

    private fun setupRecyclerView() {
        powerAdapter = LogAdapter()
        binding?.rvLogs?.apply {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            adapter = powerAdapter
            itemAnimator = null
        }
    }

    override fun loadLogs() {
        dbHelper = LogDbHelper(requireContext())
        val logs = dbHelper.getLogsByType(LogContract.EventTypes.POWER)
        updateUI(logs)
    }

    override fun updateUI(logs: List<LogEntry>) {
        binding?.apply {
            rvLogs.visibility = if (logs.isEmpty()) View.GONE else View.VISIBLE
        }
        powerAdapter.updateLogs(logs)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
