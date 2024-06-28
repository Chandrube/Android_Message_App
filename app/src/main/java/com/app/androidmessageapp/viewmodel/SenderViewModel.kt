package com.app.androidmessageapp.viewmodel

import androidx.lifecycle.*
import com.app.androidmessageapp.model.Message
import com.app.androidmessageapp.repository.MessageRepository
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class SenderViewModel(private val repository: MessageRepository) : ViewModel() {

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> get() = _messages

    fun sendMessage(message: Message) {
        viewModelScope.launch {
            repository.sendMessage(message).awaitResponse()
            fetchMessages()
        }
    }

    fun fetchMessages() {
        viewModelScope.launch {
            val response = repository.getMessages().awaitResponse()
            if (response.isSuccessful) {
                _messages.postValue(response.body())
            }
        }
    }
}

class SenderViewModelFactory(private val repository: MessageRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SenderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SenderViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}