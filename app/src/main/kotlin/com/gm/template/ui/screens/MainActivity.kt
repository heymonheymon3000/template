package com.gm.template.ui.screens

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.dynamicfeatures.DynamicExtras
import androidx.navigation.dynamicfeatures.DynamicInstallMonitor
import androidx.navigation.dynamicfeatures.fragment.DynamicNavHostFragment
import androidx.navigation.fragment.NavHostFragment
import com.gm.template.R
import com.gm.template.databinding.ActivityMainBinding
import com.gm.template.plugin.PluginFragment
import com.gm.template.ui.MainActivityInterface
import com.gm.template.ui.MainEvents
import com.gm.template.ui.MainViewModel
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.android.play.core.splitinstall.*
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
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

        subscribeObservers()
    }

    private fun subscribeObservers() {
        mainViewModel.triggerMainEvent.observe(this) { mainEvent ->
            when (mainEvent) {
                is MainEvents.OnLoadFeatureEvent -> {
                    SplitCompat.installActivity(this)
                    Toast.makeText(application, "handle OnLoadFeatureEvent", Toast.LENGTH_SHORT).show()
                    loadFeature(mainEvent.pluginFragment)
                }

                else -> {}
            }
        }
    }

    private fun loadFeature(pluginFragment: PluginFragment) {
        Toast.makeText(application, "${pluginFragment.navGraphId} != ${navController.currentDestination?.id}", Toast.LENGTH_SHORT).show()
        CoroutineScope(Main).launch {
            if (pluginFragment.navGraphId != navController.currentDestination?.id) {
                val navOptions = NavOptions.Builder().setLaunchSingleTop(true)
                    .setEnterAnim(R.anim.slide_in_from_right)
                    .setExitAnim(R.anim.slide_out_to_left)
                    .setPopEnterAnim(R.anim.slide_in_from_left)
                    .setPopExitAnim(R.anim.slide_out_to_right)
                    .build()

                val installMonitor = DynamicInstallMonitor()

                navController.navigate(
                    pluginFragment.navGraphId, null,
                    navOptions, DynamicExtras(installMonitor))

                if (installMonitor.isInstallRequired) {
                    installMonitor.status.observe(
                        this@MainActivity,
                        object : Observer<SplitInstallSessionState> {
                            override fun onChanged(sessionState: SplitInstallSessionState) {
                                when (sessionState.status()) {
                                    SplitInstallSessionStatus.INSTALLED -> {
                                        val bundle = null  // set this up
                                        navController.navigate(pluginFragment.navGraphId, bundle, navOptions)
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
}

























//    private fun loadPlugins() {
//        Log.i("Terry", "loadPlugins was called")
//        loadFragmentByAction("login", false, HashMap())
//    }


//
//@SuppressLint("QueryPermissionsNeeded")
//override fun findPluginByActionName(actionName: String): List<Plugin> {
//    val resolveInfoList = packageManager.queryIntentServices(
//        Intent(actionName),
//        PackageManager.GET_META_DATA
//    )
//    val plugins: MutableList<Plugin> = ArrayList()
//    if (resolveInfoList.size == 0) {
//        return plugins
//    }
//
//    for (resolveInfo in resolveInfoList) {
//        if (packageName.equals(resolveInfo.serviceInfo.processName, ignoreCase = true)) {
//            val plugin = Plugin(resolveInfo)
//            plugins.add(plugin)
//        }
//    }
//
//    return plugins
//}


//        val plugins = findPluginByActionName(pluginActionName)
//        if (plugins.isNotEmpty()) {
//            val plugin = plugins[0]
//            mainViewModel.actionName = pluginActionName
//            val bindIntent = Intent().apply { setClassName(plugin.servicePackageName, plugin.serviceName) }
//            applicationContext.bindService(bindIntent, mServiceConnection, BIND_AUTO_CREATE)
//            mainViewModel.mIsBound = true
//        } else {
//            CoroutineScope(Main).launch {
//                Toast.makeText(
//                    this@MainActivity,
//                    "Plugin did not load",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        }

//    override fun loadFragment(pluginFragment: PluginFragment, addToBackStack: Boolean) {
//        CoroutineScope(Main).launch {
//            if(pluginFragment.navGraphId != navController.currentDestination?.id) {
//                val navOptions = NavOptions.Builder().setLaunchSingleTop(true)
//                    .setEnterAnim(R.anim.slide_in_from_right)
//                    .setExitAnim(R.anim.slide_out_to_left)
//                    .setPopEnterAnim(R.anim.slide_in_from_left)
//                    .setPopExitAnim(R.anim.slide_out_to_right)
//                    .build()
//
//                val installMonitor = DynamicInstallMonitor()
//
//                navController.navigate(
//                    pluginFragment.navGraphId, null,
//                    navOptions, DynamicExtras(installMonitor))
//
//                if (installMonitor.isInstallRequired) {
//                    installMonitor.status.observe(
//                        this@MainActivity,
//                        object : Observer<SplitInstallSessionState> {
//                            override fun onChanged(sessionState: SplitInstallSessionState) {
//                                when (sessionState.status()) {
//                                    SplitInstallSessionStatus.INSTALLED -> {
//                                        navController.navigate(
//                                            pluginFragment.navGraphId, null, navOptions)
//                                    }
//
//                                    SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
//                                        CoroutineScope(Main).launch {
//                                            Toast.makeText(
//                                                this@MainActivity,
//                                                "SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION",
//                                                Toast.LENGTH_LONG).show()
//                                        }
//                                    }
//                                    SplitInstallSessionStatus.FAILED -> {
//                                        CoroutineScope(Main).launch {
//                                            Toast.makeText(
//                                                this@MainActivity,
//                                                "SplitInstallSessionStatus.FAILED",
//                                                Toast.LENGTH_LONG).show()
//                                        }
//                                    }
//                                    SplitInstallSessionStatus.CANCELED -> {
//                                        CoroutineScope(Main).launch {
//                                            Toast.makeText(
//                                                this@MainActivity,
//                                                "SplitInstallSessionStatus.CANCELED",
//                                                Toast.LENGTH_LONG).show()
//                                        }
//                                    }
//                                    SplitInstallSessionStatus.CANCELING -> {
//                                        CoroutineScope(Main).launch {
//                                            Toast.makeText(
//                                                this@MainActivity,
//                                                "SplitInstallSessionStatus.CANCELING",
//                                                Toast.LENGTH_LONG).show()
//                                        }
//                                    }
//                                    SplitInstallSessionStatus.DOWNLOADED -> {
//                                        CoroutineScope(Main).launch {
//                                            Toast.makeText(
//                                                this@MainActivity,
//                                                "SplitInstallSessionStatus.DOWNLOADED",
//                                                Toast.LENGTH_LONG).show()
//                                        }
//                                    }
//                                    SplitInstallSessionStatus.DOWNLOADING -> {
//                                        CoroutineScope(Main).launch {
//                                            Toast.makeText(
//                                                this@MainActivity,
//                                                "SplitInstallSessionStatus.DOWNLOADING",
//                                                Toast.LENGTH_LONG).show()
//                                        }
//                                    }
//                                    SplitInstallSessionStatus.INSTALLING -> {
//                                        CoroutineScope(Main).launch {
//                                            Toast.makeText(
//                                                this@MainActivity,
//                                                "SplitInstallSessionStatus.INSTALLING",
//                                                Toast.LENGTH_LONG).show()
//                                        }
//                                    }
//                                    SplitInstallSessionStatus.PENDING -> {
//                                        CoroutineScope(Main).launch {
//                                            Toast.makeText(
//                                                this@MainActivity,
//                                                "SplitInstallSessionStatus.PENDING",
//                                                Toast.LENGTH_LONG).show()
//                                        }
//                                    }
//                                    SplitInstallSessionStatus.UNKNOWN -> {
//                                        CoroutineScope(Main).launch {
//                                            Toast.makeText(
//                                                this@MainActivity,
//                                                "SplitInstallSessionStatus.UNKNOWN",
//                                                Toast.LENGTH_LONG).show()
//                                        }
//                                    }
//                                }
//
//                                if (sessionState.hasTerminalStatus()) {
//                                    installMonitor.status.removeObserver(this)
//                                }
//                            }
//                        })
//                }
//            }
//        }
//    }
//}

//override fun loadFragmentByAction(pluginActionName: String, addToBackStack: Boolean, arguments: HashMap<String, Any>) {

//        /** Listener used to handle changes in state for install requests. */
//        val splitInstallStateUpdatedListener = SplitInstallStateUpdatedListener { state ->
//            when (state.status()) {
//                SplitInstallSessionStatus.INSTALLED -> {
//                    onSuccessfulLoad(uiLogin, pluginActionName)
//                }
//                SplitInstallSessionStatus.DOWNLOADING -> {}
//                SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {}
//                SplitInstallSessionStatus.INSTALLING -> {}
//                SplitInstallSessionStatus.FAILED -> {}
//                SplitInstallSessionStatus.CANCELED -> {}
//                SplitInstallSessionStatus.CANCELING -> {}
//                SplitInstallSessionStatus.DOWNLOADED -> {}
//                SplitInstallSessionStatus.PENDING -> {}
//                SplitInstallSessionStatus.UNKNOWN -> {}
//            }
//        }
//
//        val splitInstallManager: SplitInstallManager =
//            SplitInstallManagerFactory.create(applicationContext)
//
//        if (splitInstallManager.installedModules.contains(uiLogin)) {
//            bindToService(pluginActionName)
//        } else {
//            val request = SplitInstallRequest.newBuilder()
//                .addModule(uiLogin)
//                .build()
//
//            splitInstallManager.registerListener(splitInstallStateUpdatedListener)
//
//            splitInstallManager.startInstall(request)
//                .addOnCompleteListener {
//                    splitInstallManager.unregisterListener(splitInstallStateUpdatedListener)
//                }
//                .addOnSuccessListener { bindToService(pluginActionName) }
//                .addOnFailureListener {}
//        }
//}


//        /** Listener used to handle changes in state for install requests. */
//        val splitInstallStateUpdatedListener = SplitInstallStateUpdatedListener { state ->
//            when (state.status()) {
//                SplitInstallSessionStatus.INSTALLED -> {
//                    onSuccessfulLoad(uiLogin, pluginActionName)
//                }
//                SplitInstallSessionStatus.DOWNLOADING -> {}
//                SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {}
//                SplitInstallSessionStatus.INSTALLING -> {}
//                SplitInstallSessionStatus.FAILED -> {}
//                SplitInstallSessionStatus.CANCELED -> {}
//                SplitInstallSessionStatus.CANCELING -> {}
//                SplitInstallSessionStatus.DOWNLOADED -> {}
//                SplitInstallSessionStatus.PENDING -> {}
//                SplitInstallSessionStatus.UNKNOWN -> {}
//            }
//        }
//
//        val splitInstallManager: SplitInstallManager =
//            SplitInstallManagerFactory.create(applicationContext)
//
//        if (splitInstallManager.installedModules.contains(uiLogin)) {
//            bindToService(pluginActionName)
//        } else {
//            val request = SplitInstallRequest.newBuilder()
//                .addModule(uiLogin)
//                .build()
//
//            splitInstallManager.registerListener(splitInstallStateUpdatedListener)
//
//            splitInstallManager.startInstall(request)
//                .addOnCompleteListener {
//                    splitInstallManager.unregisterListener(splitInstallStateUpdatedListener)
//                }
//                .addOnSuccessListener { bindToService(pluginActionName) }
//                .addOnFailureListener {}
//        }
//
//private fun onSuccessfulLoad(moduleName: String, pluginActionName: String) {
//    when (moduleName) {
//        uiLogin -> launchFeature(pluginActionName)
//    }
//}
//
//private fun launchFeature(pluginActionName: String) {
//    bindToService(pluginActionName)
//}

//private val uiLogin by lazy { "ui_login" }
