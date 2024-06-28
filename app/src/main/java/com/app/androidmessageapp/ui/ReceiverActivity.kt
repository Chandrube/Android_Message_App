package com.app.androidmessageapp.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.androidmessageapp.databinding.ActivityReceiverBinding
import com.app.androidmessageapp.model.Message
import com.app.androidmessageapp.repository.MessageRepository
import com.app.androidmessageapp.ui.adapter.MessageAdapter
import com.app.androidmessageapp.utilities.CryptoUtils
import com.app.androidmessageapp.utilities.Utils
import com.app.androidmessageapp.viewmodel.SenderViewModel
import com.app.androidmessageapp.viewmodel.SenderViewModelFactory
import javax.crypto.SecretKey

class ReceiverActivity : AppCompatActivity() {
    private var binding: ActivityReceiverBinding? = null
    private val viewModel: SenderViewModel by viewModels {
        SenderViewModelFactory(
            MessageRepository()
        )
    }
    private lateinit var secretKey: SecretKey
    private lateinit var adapter: MessageAdapter
    private val messages: MutableList<Message> = mutableListOf()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReceiverBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        secretKey = Utils.getSecretKey(this) ?: run {
            val newKey = CryptoUtils.generateKey()
            Utils.saveSecretKey(this, newKey)
            newKey
        }

        adapter = MessageAdapter(messages,"Receiver")
        binding!!.recyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
            reverseLayout = false
        }
        binding!!.recyclerView.adapter = adapter

        // Observe the LiveData from the ViewModel
        viewModel.messages.observe(this, Observer { newMessages ->
            messages.clear()
            val decryptedMessages = newMessages.map { message ->
                val decryptedContent = CryptoUtils.decrypt(secretKey, message.message)
                message.copy(message = decryptedContent)
            }
            messages.addAll(decryptedMessages)
            adapter.notifyDataSetChanged()
            binding!!.recyclerView.scrollToPosition(messages.size - 1)
        })

        // Initial fetch of messages
        viewModel.fetchMessages()
    }

}