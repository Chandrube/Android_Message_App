package com.app.androidmessageapp.ui


import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import android.telephony.SmsManager
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.androidmessageapp.R
import com.app.androidmessageapp.databinding.ActivitySenderBinding
import com.app.androidmessageapp.model.Message
import com.app.androidmessageapp.repository.MessageRepository
import com.app.androidmessageapp.ui.adapter.MessageAdapter
import com.app.androidmessageapp.utilities.CryptoUtils
import com.app.androidmessageapp.utilities.Utils
import com.app.androidmessageapp.viewmodel.SenderViewModel
import com.app.androidmessageapp.viewmodel.SenderViewModelFactory
import javax.crypto.SecretKey

class SenderActivity : AppCompatActivity(), View.OnClickListener {

    private var binding: ActivitySenderBinding? = null
    private val viewModel: SenderViewModel by viewModels {
        SenderViewModelFactory(
            MessageRepository()
        )
    }
    private lateinit var secretKey: SecretKey
    private lateinit var adapter: MessageAdapter
    private val messages: MutableList<Message> = mutableListOf()
    private val REQUEST_SMS_PERMISSION = 1
    private val TAG = "SenderActivity"

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySenderBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        requestSmsPermissions()

        binding!!.receiverButton.setOnClickListener(this)
        secretKey = Utils.getSecretKey(this) ?: run {
            val newKey = CryptoUtils.generateKey()
            Utils.saveSecretKey(this, newKey)
            newKey
        }
        adapter = MessageAdapter(messages,"Sender")
        binding!!.recyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
            reverseLayout = false
        }
        binding!!.recyclerView.adapter = adapter

        binding!!.sendButton.setOnClickListener {
            val messageContent = binding!!.messageInput.text.toString()
            if (messageContent.isNotEmpty()) {
                val encryptedMessage = CryptoUtils.encrypt(secretKey, messageContent)
                Log.d(TAG, "Encrypted message: $encryptedMessage")
                val message = Message(id = generateMessageId(), message = encryptedMessage)
                viewModel.sendMessage(message)
                sendSms("+917904294253", encryptedMessage)
                insertMessageIntoSmsProvider("+1234567890", encryptedMessage)
                binding!!.messageInput.text.clear()
            }
        }

        // Observe the LiveData from the ViewModel
        viewModel.messages.observe(this, Observer { newMessages ->
            messages.clear()
            try {
                val decryptedMessages = newMessages.map { message ->
                    val decryptedContent = CryptoUtils.decrypt(secretKey, message.message)
                    message.copy(message = decryptedContent)
                }
                messages.addAll(decryptedMessages)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            adapter.notifyDataSetChanged()
            binding!!.recyclerView.scrollToPosition(messages.size - 1)
        })

        // Initial fetch of messages
        viewModel.fetchMessages()
    }

    private fun generateMessageId(): String {
        return System.currentTimeMillis().toString()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.receiverButton -> {
                val intent = Intent(this@SenderActivity, ReceiverActivity::class.java)
                startActivity(intent)
            }
        }
    }


    private fun sendSms(phoneNumber: String, message: String) {
        try {
            val smsManager: SmsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Log.d(TAG, "SMS sent successfully")
        } catch (e: Exception) {
            Log.e(TAG, "SMS sending failed", e)
        }
    }

    private fun insertMessageIntoSmsProvider(phoneNumber: String, message: String) {
        val contentValues = ContentValues()
        contentValues.put(Telephony.Sms.ADDRESS, phoneNumber)
        contentValues.put(Telephony.Sms.BODY, message)
        contentValues.put(Telephony.Sms.DATE, System.currentTimeMillis())
        contentValues.put(Telephony.Sms.READ, 1)
        contentValues.put(Telephony.Sms.TYPE, Telephony.Sms.MESSAGE_TYPE_SENT)

        try {
            contentResolver.insert(Telephony.Sms.Sent.CONTENT_URI, contentValues)
            Log.d(TAG, "SMS inserted into provider successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to insert SMS into provider", e)
        }
    }

    private fun requestSmsPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.READ_SMS
                ),
                REQUEST_SMS_PERMISSION
            )
        }
    }
}