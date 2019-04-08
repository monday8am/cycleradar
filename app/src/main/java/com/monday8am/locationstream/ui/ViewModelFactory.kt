package com.monday8am.locationstream.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.monday8am.locationstream.data.PhotoDataRepository


class ViewModelFactory(private val repository: PhotoDataRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PhotoListViewModel::class.java)) {
            return PhotoListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
