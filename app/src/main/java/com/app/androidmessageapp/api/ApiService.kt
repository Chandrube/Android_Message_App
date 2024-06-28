package com.app.androidmessageapp.api

import com.app.androidmessageapp.model.Message
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("unicorns")
    fun getMessages(): Call<List<Message>>

    @POST("unicorns")
    fun sendMessage(@Body message: Message): Call<Message>
}