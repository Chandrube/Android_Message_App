package com.app.androidmessageapp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.androidmessageapp.repository.MessageRepository
import com.app.androidmessageapp.viewmodel.ReceiverViewModel
import com.app.androidmessageapp.viewmodel.SenderViewModel

class ViewModelFactory(private val repository: MessageRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SenderViewModel::class.java) -> SenderViewModel(repository) as T
            modelClass.isAssignableFrom(ReceiverViewModel::class.java) -> ReceiverViewModel(
                repository
            ) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}