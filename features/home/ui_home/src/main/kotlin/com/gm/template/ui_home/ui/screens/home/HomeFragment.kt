package com.gm.template.ui_home.ui.screens.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewModelProvider
import com.gm.template.di.DaggerDependencies
import com.gm.template.plugin.BaseFragment
import com.gm.template.ui.MainActivityInterface
import com.gm.template.ui.theme.TemplateTheme
import com.gm.template.ui_home.di.DaggerHomeComponent
import com.gm.template.ui_home.ui.HomeViewModel
import com.google.android.play.core.splitcompat.SplitCompat
import dagger.hilt.android.EntryPointAccessors
import javax.inject.Inject

class HomeFragment: BaseFragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(requireActivity(), viewModelFactory)[HomeViewModel::class.java]
    }

    private var mainActivityInterface: MainActivityInterface? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                TemplateTheme {
                    HomeScreen(
                        state = viewModel.state.value,
                        events = viewModel::onTriggerEvent) }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        SplitCompat.installActivity(context)

        DaggerHomeComponent
            .builder()
            .context(requireContext())
            .appDependencies(
                EntryPointAccessors.fromApplication(
                    requireContext(),
                    DaggerDependencies::class.java
                )
            ).build().inject(this)

        mainActivityInterface = context as MainActivityInterface
    }

    override fun onDetach() {
        mainActivityInterface = null
        super.onDetach()
    }
}