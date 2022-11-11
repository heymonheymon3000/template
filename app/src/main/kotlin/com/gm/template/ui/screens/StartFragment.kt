package com.gm.template.ui.screens

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.gm.template.plugin.BaseFragment
import com.gm.template.ui.MainActivityInterface
import com.gm.template.ui.MainViewModel
import com.gm.template.ui.theme.TemplateTheme

class StartFragment: BaseFragment() {
    private var mainActivityInterface: MainActivityInterface? = null

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        return ComposeView(requireContext()).apply {
            setContent {
                TemplateTheme { mainActivityInterface?.let {
                    StartScreen(
                        mainActivityInterface = it,
                        state = viewModel.state.value,
                        events = viewModel::onTriggerEvent) } }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivityInterface = context as MainActivityInterface
    }

    override fun onDetach() {
        mainActivityInterface = null
        super.onDetach()
    }
}