package com.example.mysets.view.model.searchBrickViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mysets.repositories.Repository

class SearchBrickViewModelFactory(private val repository: Repository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = SearchBrickViewModel(
        repository
    ) as T
}