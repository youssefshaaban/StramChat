package com.plcoding.streamchatapp.ui.channel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.call.await
import io.getstream.chat.android.client.models.User
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChannelViewModel @Inject constructor(
    private val client: ChatClient
) : ViewModel() {
    private val _createChannelEvent = MutableSharedFlow<CreateChannelEvent>()
    val createChannel get() = _createChannelEvent.asSharedFlow()
    fun createChannel(channelName: String) {
        val trimChannelName = channelName.trim()
        viewModelScope.launch {
            if (trimChannelName.isBlank()) {
                _createChannelEvent.emit(CreateChannelEvent.ErrorCreateChannel("channel name can't be empty string"))
                return@launch
            }
            val result =
                client.channel(channelType = "message", channelId = UUID.randomUUID().toString())
                    .create(
                        mapOf("name" to trimChannelName)
                    ).await()
            if (result.isError) {
                _createChannelEvent.emit(
                    CreateChannelEvent.ErrorCreateChannel(
                        result.error().message ?: "UnknownError"
                    )
                )
                return@launch
            }
            _createChannelEvent.emit(CreateChannelEvent.Success)
        }
    }

    fun logout() {
        client.disconnect()
    }

    fun getUser(): User? {
        return client.getCurrentUser()
    }

    sealed class CreateChannelEvent {
        data class ErrorCreateChannel(val message: String) : CreateChannelEvent()
        object Success : CreateChannelEvent()
    }
}