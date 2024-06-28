package com.app.androidmessageapp.repository

import com.app.androidmessageapp.api.ApiService
import com.app.androidmessageapp.model.Message
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MessageRepository {

    private val api: ApiService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://crudcrud.com/api/6a99fe78655c4ad39ddaea5e89979a34/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(ApiService::class.java)
    }

    fun getMessages() = api.getMessages()

    fun sendMessage(message: Message) = api.sendMessage(message)
}