package com.example.xml_app.ui.modals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.xml_app.databinding.ItemBottomSheetDeleteCartBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DeleteCartBottomSheet : BottomSheetDialogFragment() {
    private var _binding: ItemBottomSheetDeleteCartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ItemBottomSheetDeleteCartBinding.inflate(inflater, container, false)
        return binding.root
    }
}