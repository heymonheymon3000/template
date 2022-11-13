package com.gm.template.ui.screens.feature_progress_bar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewModelProvider
import com.gm.template.plugin.BaseFragment
import com.gm.template.ui.MainViewModel
import com.gm.template.ui.theme.TemplateTheme

class FeatureProgressBarFragment: BaseFragment() {
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        return ComposeView(requireContext()).apply {
            setContent {
                TemplateTheme {
                    FeatureProgressBarScreen(
                        state = viewModel.state.value,
                        events = viewModel::onTriggerEvent) }
            }
        }
    }
}