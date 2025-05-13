package com.yourname.portlogger.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yourname.portlogger.R
import com.yourname.portlogger.data.LogEntry
import com.yourname.portlogger.databinding.ItemPowerLogBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PowerLogAdapter : RecyclerView.Adapter<PowerLogAdapter.PowerLogViewHolder>() {
    private val logs = mutableListOf<LogEntry>()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun updateLogs(newLogs: List<LogEntry>) {
        if (logs.isEmpty()) {
            logs.addAll(newLogs)
            notifyItemRangeInserted(0, newLogs.size)
            return
        }

        val diffCallback = PowerLogDiffCallback(logs, newLogs)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        
        logs.clear()
        logs.addAll(newLogs)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PowerLogViewHolder {
        return PowerLogViewHolder(
            ItemPowerLogBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = logs.size

    override fun onBindViewHolder(holder: PowerLogViewHolder, position: Int) {
        holder.bind(logs[position])
    }

    inner class PowerLogViewHolder(
        private val binding: ItemPowerLogBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(log: LogEntry) {
            binding.apply {
                tvEventType.text = log.event
                // Format timestamp as string before setting it to TextView
                val formattedDate = dateFormat.format(Date(log.timestamp))
                tvTimestamp.text = formattedDate.toString()
                ivPowerIcon.setImageResource(
                    if (log.event.contains("on", ignoreCase = true))
                        R.drawable.ic_power_on
                    else
                        R.drawable.ic_power_off
                )
            }
        }
    }
}

private class PowerLogDiffCallback(
    private val oldList: List<LogEntry>,
    private val newList: List<LogEntry>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].timestamp == newList[newItemPosition].timestamp
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
} 