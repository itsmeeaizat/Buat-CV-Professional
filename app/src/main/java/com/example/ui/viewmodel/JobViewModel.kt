package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.JobPosting
import com.example.data.model.SavedJob
import com.example.data.repository.JobRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class JobViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: JobRepository

    val savedJobs: StateFlow<List<SavedJob>>

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _locationQuery = MutableStateFlow("")
    val locationQuery: StateFlow<String> = _locationQuery.asStateFlow()

    private val _selectedJobType = MutableStateFlow("Semua")
    val selectedJobType: StateFlow<String> = _selectedJobType.asStateFlow()

    private val _jobsList = MutableStateFlow<List<JobPosting>>(emptyList())
    val jobsList: StateFlow<List<JobPosting>> = _jobsList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedJobForDetail = MutableStateFlow<JobPosting?>(null)
    val selectedJobForDetail: StateFlow<JobPosting?> = _selectedJobForDetail.asStateFlow()

    init {
        val jobDao = AppDatabase.getInstance(application).jobDao()
        repository = JobRepository(jobDao)
        savedJobs = repository.savedJobs.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        performSearch()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onLocationQueryChanged(loc: String) {
        _locationQuery.value = loc
    }

    fun onJobTypeSelected(type: String) {
        _selectedJobType.value = type
        performSearch()
    }

    fun performSearch() {
        viewModelScope.launch {
            _isLoading.value = true
            val results = repository.searchJobs(_searchQuery.value, _locationQuery.value)
            val filtered = if (_selectedJobType.value == "Semua") {
                results
            } else {
                results.filter { it.jobType.contains(_selectedJobType.value, ignoreCase = true) }
            }
            _jobsList.value = filtered
            _isLoading.value = false
        }
    }

    fun selectJobForDetail(job: JobPosting?) {
        _selectedJobForDetail.value = job
    }

    fun toggleSaveJob(job: JobPosting) {
        viewModelScope.launch {
            if (job.isSaved) {
                repository.deleteSavedJob(job.id)
            } else {
                repository.saveJob(job)
            }
            // Refresh current view
            performSearch()
        }
    }
}
