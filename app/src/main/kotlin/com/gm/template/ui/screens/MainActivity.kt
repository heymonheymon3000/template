package com.gm.template.ui.screens

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.dynamicfeatures.DynamicExtras
import androidx.navigation.dynamicfeatures.DynamicInstallMonitor
import androidx.navigation.dynamicfeatures.fragment.DynamicNavHostFragment
import androidx.navigation.fragment.NavHostFragment
import com.gm.template.R
import com.gm.template.databinding.ActivityMainBinding
import com.gm.template.ui.MainActivityInterface
import com.gm.template.ui.MainEvents
import com.gm.template.ui.MainViewModel
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.android.play.core.splitinstall.*
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity(), MainActivityInterface {
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    private val mainViewModel: MainViewModel by viewModels()
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        _binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as DynamicNavHostFragment
        navController = navHostFragment.navController

        mainViewModel.navController = navController

        processEvents()
    }

    private fun processEvents() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    mainViewModel.triggerMainEvent.collect { event ->
                        event.getContentIfNotHandled()?.let { mainEvent ->
                            when (mainEvent) {
                                is MainEvents.OnLoadFeatureNavGraphEvent -> {
                                    SplitCompat.installActivity(this@MainActivity)
                                    loadFeature(mainEvent.navGraphId )
                                }

                                else -> {}
                            }
                        }
                    }
                }
            }
        }
    }

    private fun loadFeature(navGraphId: Int) {
        if (navGraphId != navController.currentDestination?.id) {
            val navOptions = if(navController.currentDestination?.id != R.id.feature_progress_bar_fragment) {
                NavOptions.Builder().setLaunchSingleTop(true)
                    .setEnterAnim(R.anim.slide_in_from_right)
                    .setExitAnim(R.anim.slide_out_to_left)
                    .setPopEnterAnim(R.anim.slide_in_from_left)
                    .setPopExitAnim(R.anim.slide_out_to_right)
                    .build()
            } else {
                navController.popBackStack()
                NavOptions.Builder().setLaunchSingleTop(true)
                    .setEnterAnim(R.anim.fadein)
                    .setExitAnim(R.anim.fadeout)
                    .setPopEnterAnim(R.anim.fadein)
                    .setPopExitAnim(R.anim.fadeout)
                    .build()
            }

            val installMonitor = DynamicInstallMonitor()

            navController.navigate(
                navGraphId, null,
                navOptions, DynamicExtras(installMonitor))

            if (installMonitor.isInstallRequired) {
                installMonitor.status.observe(
                    this@MainActivity,
                    object : Observer<SplitInstallSessionState> {
                        override fun onChanged(sessionState: SplitInstallSessionState) {
                            when (sessionState.status()) {
                                SplitInstallSessionStatus.INSTALLED -> {
                                    val bundle = null  // set this up
                                    navController.navigate(navGraphId, bundle, navOptions)
                                }

                                SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {}
                                SplitInstallSessionStatus.FAILED -> {}
                                SplitInstallSessionStatus.CANCELED -> {}
                                SplitInstallSessionStatus.CANCELING -> {}
                                SplitInstallSessionStatus.DOWNLOADED -> {}
                                SplitInstallSessionStatus.DOWNLOADING -> {}
                                SplitInstallSessionStatus.INSTALLING -> {}
                                SplitInstallSessionStatus.PENDING -> {}
                                SplitInstallSessionStatus.UNKNOWN -> {}
                            }

                            if (sessionState.hasTerminalStatus()) {
                                installMonitor.status.removeObserver(this)
                            }
                        }
                    }
                )
            }
        }
    }
}
