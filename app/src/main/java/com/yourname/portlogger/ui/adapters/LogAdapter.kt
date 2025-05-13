package com.yourname.portlogger.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yourname.portlogger.R
import com.yourname.portlogger.data.LogContract
import com.yourname.portlogger.data.LogEntry
import com.yourname.portlogger.databinding.ItemLogEntryBinding
import com.yourname.portlogger.data.LogDbHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LogAdapter : ListAdapter<LogEntry, LogAdapter.LogViewHolder>(LogDiffCallback()) {
    private val selectedItems = mutableSetOf<Long>()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    
    var onSelectionModeChange: ((Boolean) -> Unit)? = null
    var onSelectionCountChange: ((Int) -> Unit)? = null

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long = getItem(position).id

    private fun toggleSelection(logId: Long) {
        if (selectedItems.contains(logId)) {
            selectedItems.remove(logId)
        } else {
            selectedItems.add(logId)
        }
        currentList.indexOfFirst { it.id == logId }.takeIf { it != -1 }
            ?.let { notifyItemChanged(it) }
        onSelectionCountChange?.invoke(selectedItems.size)
        if (selectedItems.isEmpty()) onSelectionModeChange?.invoke(false)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val binding = ItemLogEntryBinding.inflate(
            LayoutInflater.from(parent.context), 
            parent, 
            false
        )
        return LogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val log = getItem(position)
        holder.bind(
            log = log,
            isSelected = selectedItems.contains(log.id),
            onLongClick = { 
                if (selectedItems.isEmpty()) onSelectionModeChange?.invoke(true)
                toggleSelection(log.id)
            }
        )
    }

    fun selectAll() {
        selectedItems.addAll(currentList.map { it.id })
        notifyItemRangeChanged(0, itemCount)
        onSelectionCountChange?.invoke(selectedItems.size)
    }

    fun clearSelection() {
        selectedItems.clear()
        notifyItemRangeChanged(0, itemCount)
        onSelectionCountChange?.invoke(0)
        onSelectionModeChange?.invoke(false)
    }

    fun getSelectedItems() = currentList.filter { selectedItems.contains(it.id) }

    fun deleteSelectedItems(dbHelper: LogDbHelper) {
        val ids = selectedItems.toList()
        dbHelper.deleteLogs(ids)
        submitList(currentList.filterNot { it.id in ids })
        clearSelection()
    }

    inner class LogViewHolder(
        private val binding: ItemLogEntryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(log: LogEntry, isSelected: Boolean, onLongClick: () -> Unit) {
            binding.apply {
                root.isSelected = isSelected
                root.setOnLongClickListener { 
                    onLongClick()
                    true
                }
                
                tvEventTitle.text = log.event
                tvTimestamp.text = dateFormat.format(Date(log.timestamp))

                when (log.type) {
                    LogContract.EventTypes.POWER -> {
                        ivEventIcon.setImageResource(
                            if (log.event.contains("on", true)) 
                                R.drawable.ic_power_on 
                            else 
                                R.drawable.ic_power_off
                        )
                        tvEventDetails.isVisible = false
                    }
                    LogContract.EventTypes.USB -> {
                        ivEventIcon.setImageResource(R.drawable.ic_usb)
                        tvEventDetails.isVisible = !log.filePath.isNullOrEmpty()
                        tvEventDetails.text = log.filePath
                    }
                    LogContract.EventTypes.SIM -> {
                        ivEventIcon.setImageResource(R.drawable.ic_sim_card)
                        tvEventDetails.isVisible = false
                    }
                }
            }
        }
    }

    class LogDiffCallback : DiffUtil.ItemCallback<LogEntry>() {
        override fun areItemsTheSame(oldItem: LogEntry, newItem: LogEntry): Boolean =
            oldItem.id == newItem.id
            
        override fun areContentsTheSame(oldItem: LogEntry, newItem: LogEntry): Boolean =
            oldItem == newItem
    }
}
