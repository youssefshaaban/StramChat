package com.plcoding.streamchatapp.ui.channel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.plcoding.streamchatapp.R
import com.plcoding.streamchatapp.databinding.FragmentChannelBinding
import com.plcoding.streamchatapp.ui.BindingFragment
import com.plcoding.streamchatapp.util.navigateSafely
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.ui.channel.list.header.viewmodel.ChannelListHeaderViewModel
import io.getstream.chat.android.ui.channel.list.header.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModel
import io.getstream.chat.android.ui.channel.list.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.factory.ChannelListViewModelFactory
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ChannelFragment : BindingFragment<FragmentChannelBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentChannelBinding::inflate

    private val viewModel: ChannelViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = viewModel.getUser()
        if (user == null) {
            findNavController().popBackStack()
            return
        }

        val factory = ChannelListViewModelFactory(
            filter = Filters.and(
                Filters.eq("type", "messaging"),
            ),
            sort = ChannelListViewModel.DEFAULT_SORT,
            limit = 30
        )
        val channelListViewModel: ChannelListViewModel by viewModels { factory }
        val channelListHeaderViewModel: ChannelListHeaderViewModel by viewModels()
        channelListViewModel.bindView(binding.channelListView, viewLifecycleOwner)
        channelListHeaderViewModel.bindView(binding.channelListHeaderView, viewLifecycleOwner)
        binding.channelListHeaderView.setOnActionButtonClickListener {

        }

        binding.channelListHeaderView.setOnUserAvatarClickListener {
            viewModel.logout()
            findNavController().popBackStack()
        }
        binding.channelListHeaderView.setOnActionButtonClickListener {
            findNavController().navigateSafely(R.id.action_channelFragment_to_createChannelDialog)
        }
        binding.channelListView.setChannelItemClickListener { cahnnel ->
            findNavController().navigateSafely(
                R.id.action_channelFragment_to_chatFragment,
                Bundle().apply {
                    putString("channelId", cahnnel.cid)
                })
        }
        lifecycleScope.launchWhenStarted {
            viewModel.createChannel.collectLatest { event ->
                when (event) {
                    is ChannelViewModel.CreateChannelEvent.ErrorCreateChannel -> {
                        Snackbar.make(requireView(), event.message, 1000).show()
                    }
                    is ChannelViewModel.CreateChannelEvent.Success -> Snackbar.make(
                        requireView(),
                        R.string.channel_created,
                        1000
                    ).show()
                    else -> {}
                }
            }
        }
    }
}