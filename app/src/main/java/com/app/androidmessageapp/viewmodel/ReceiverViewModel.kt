package com.app.androidmessageapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.androidmessageapp.model.Message
import com.app.androidmessageapp.repository.MessageRepository
import kotlinx.coroutines.launch

class ReceiverViewModel(private val repository: MessageRepository) : ViewModel() {
    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> get() = _messages

}