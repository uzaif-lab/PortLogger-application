package com.yourname.portlogger.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.yourname.portlogger.data.LogContract
import com.yourname.portlogger.data.LogDbHelper
import com.yourname.portlogger.data.LogEntry
import com.yourname.portlogger.ui.adapters.LogAdapter

class UsbLogsFragment : BaseLogsFragment() {
    private lateinit var logAdapter: LogAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadLogs()
    }

    private fun setupRecyclerView() {
        logAdapter = LogAdapter()
        safeBinding.rvLogs.layoutManager = LinearLayoutManager(context)
        safeBinding.rvLogs.adapter = logAdapter
        safeBinding.rvLogs.itemAnimator = null
    }

    override fun loadLogs() {
        dbHelper = LogDbHelper(requireContext())
        val logs = dbHelper.getLogsByType(LogContract.EventTypes.USB)
        updateUI(logs)
    }

    override fun updateUI(logs: List<LogEntry>) {
        logAdapter.updateLogs(logs)
        safeBinding.tvEmpty.isVisible = logs.isEmpty()
        safeBinding.rvLogs.isVisible = logs.isNotEmpty()
    }
} 