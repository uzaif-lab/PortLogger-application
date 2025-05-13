package com.yourname.portlogger.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.yourname.portlogger.data.LogEntry
import com.yourname.portlogger.databinding.FragmentLogsBinding
import com.yourname.portlogger.ui.adapters.LogAdapter

class AllLogsFragment : BaseLogsFragment() {
    override val adapter: LogAdapter by lazy {
        LogAdapter().apply {
            onSelectionModeChange = { isSelectionMode ->
                if (isSelectionMode) {
                    actionMode = requireActivity().startActionMode(actionModeCallback)
                } else {
                    actionMode?.finish()
                }
            }
            onSelectionCountChange = { count ->
                actionMode?.title = resources.getQuantityString(
                    R.plurals.items_selected,
                    count,
                    count
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadLogs()
    }

    private fun setupRecyclerView() {
        safeBinding.rvLogs.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@AllLogsFragment.adapter
            itemAnimator = null
            setHasFixedSize(true)
        }
    }

    override fun loadLogs() {
        val logs = dbHelper.getAllLogs()
        updateUI(logs)
    }

    override fun updateUI(logs: List<LogEntry>) {
        safeBinding.apply {
            tvEmpty.isVisible = logs.isEmpty()
            rvLogs.isVisible = logs.isNotEmpty()
        }
        adapter.submitList(logs)
    }
}
