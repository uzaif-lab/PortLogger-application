package com.yourname.portlogger.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.yourname.portlogger.R
import com.yourname.portlogger.data.LogDbHelper
import com.yourname.portlogger.data.LogEntry
import com.yourname.portlogger.databinding.FragmentLogsBinding
import com.yourname.portlogger.ui.adapters.LogAdapter

abstract class BaseLogsFragment : Fragment(R.layout.fragment_logs) {
    protected var binding: FragmentLogsBinding? = null
    protected val safeBinding: FragmentLogsBinding
        get() = binding ?: throw IllegalStateException("Binding is not initialized")

    protected abstract val adapter: LogAdapter
    protected lateinit var dbHelper: LogDbHelper
    
    private var actionMode: ActionMode? = null
    
    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.menu_selection, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean = false

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.action_select_all -> {
                    adapter.selectAll()
                    true
                }
                R.id.action_delete -> {
                    showDeleteConfirmation()
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            actionMode = null
            adapter.clearSelection()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = LogDbHelper(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLogsBinding.bind(view)
    }

    protected fun showDeleteConfirmation() {
        val selectedCount = adapter.getSelectedItems().size
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_logs))
            .setMessage(
                resources.getQuantityString(
                    R.plurals.delete_logs_confirmation,
                    selectedCount,
                    selectedCount
                )
            )
            .setPositiveButton(R.string.delete) { _, _ -> deleteSelectedLogs() }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun deleteSelectedLogs() {
        val selectedItems = adapter.getSelectedItems()
        if (selectedItems.isNotEmpty()) {
            dbHelper.deleteLogs(selectedItems.map { it.id })
            adapter.deleteSelectedItems(dbHelper)
            actionMode?.finish()
        }
    }

    abstract fun loadLogs()
    abstract fun updateUI(logs: List<LogEntry>)

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
