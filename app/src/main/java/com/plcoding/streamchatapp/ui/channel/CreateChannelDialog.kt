package com.plcoding.streamchatapp.ui.channel

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.plcoding.streamchatapp.R
import com.plcoding.streamchatapp.databinding.DialogChannelNameBinding

class CreateChannelDialog : DialogFragment() {

    private var _binding: DialogChannelNameBinding? = null
    private val binding: DialogChannelNameBinding
        get() = _binding!!
    val viewModel:ChannelViewModel by activityViewModels()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogChannelNameBinding.inflate(layoutInflater)
        return MaterialAlertDialogBuilder(requireContext()).setTitle("").setView(_binding!!.root)
            .setPositiveButton(
                R.string.create
            ) { _, _ ->
                viewModel.createChannel(binding.etChannelName.text.toString())
            }.setNegativeButton(R.string.cancel){
                    dailogInterface,_->
                dailogInterface.cancel()
            }.create()
    }

}