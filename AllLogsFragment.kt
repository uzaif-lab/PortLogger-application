class AllLogsFragment : BaseLogsFragment() {
    // Option 1: Lazy initialization
    private val logAdapter by lazy { LogAdapter() }
    
    // OR Option 2: Initialize in onViewCreated
    // private lateinit var logAdapter: LogAdapter

    private var logAdapter: LogAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // If using Option 2:
        // logAdapter = LogAdapter()
        
        binding.recyclerView.apply {
            adapter = logAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        
        // Call loadLogs AFTER initialization
        loadLogs()
    }

    private fun loadLogs() {
        val logs = dbHelper.getAllLogs()
        logAdapter?.submitList(logs) ?: run {
            Log.e("AllLogsFragment", "Adapter not initialized!")
        }
    }
} 