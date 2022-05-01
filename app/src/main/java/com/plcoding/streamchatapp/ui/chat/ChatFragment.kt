package com.plcoding.streamchatapp.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.getstream.sdk.chat.viewmodel.MessageInputViewModel
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel
import com.plcoding.streamchatapp.databinding.FragmentChatBinding
import com.plcoding.streamchatapp.ui.BindingFragment
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.ui.message.input.viewmodel.bindView
import io.getstream.chat.android.ui.message.list.MessageListView
import io.getstream.chat.android.ui.message.list.adapter.MessageListItemViewHolderFactory
import io.getstream.chat.android.ui.message.list.header.viewmodel.MessageListHeaderViewModel
import io.getstream.chat.android.ui.message.list.header.viewmodel.bindView
import io.getstream.chat.android.ui.message.list.viewmodel.bindView
import io.getstream.chat.android.ui.message.list.viewmodel.factory.MessageListViewModelFactory

@AndroidEntryPoint
class ChatFragment : BindingFragment<FragmentChatBinding>() {

    val args: ChatFragmentArgs by navArgs()
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentChatBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val facotry = MessageListViewModelFactory(args.channelId ?: "")
        val messageListViewModelHeader: MessageListHeaderViewModel by viewModels { facotry }
        val messageListViewModel: MessageListViewModel by viewModels { facotry }
        val messageInputViewModel: MessageInputViewModel by viewModels { facotry }

        messageListViewModelHeader.bindView(binding.messageListHeaderView, viewLifecycleOwner)
        messageListViewModel.bindView(binding.messageListView, viewLifecycleOwner)
        messageInputViewModel.bindView(binding.messageInputView, viewLifecycleOwner)
        messageListViewModel.mode.observe(viewLifecycleOwner) { message ->
            when (message) {
                is MessageListViewModel.Mode.Thread -> {
                    messageInputViewModel.setActiveThread(message.parentMessage)
                    messageListViewModelHeader.setActiveThread(message = message.parentMessage)
                }
                is MessageListViewModel.Mode.Normal -> {
                    messageInputViewModel.resetThread()
                    messageListViewModelHeader.resetThread()
                }
            }
        }

        binding.messageListView.setMessageEditHandler(messageInputViewModel::postMessageToEdit)
        messageListViewModel.state.observe(viewLifecycleOwner){
            state->
            if (state is MessageListViewModel.State.NavigateUp){
                findNavController().navigateUp()
            }
        }
        val backHandler = {
            messageListViewModel.onEvent(MessageListViewModel.Event.BackButtonPressed)
        }

        binding.messageListHeaderView.setBackButtonClickListener(backHandler)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
            backHandler()
        }
    }
}