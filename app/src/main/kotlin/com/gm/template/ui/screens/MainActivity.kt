package com.gm.template.ui.screens

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
import com.gm.template.plugin.IPluginInterface
import com.gm.template.plugin.Plugin
import com.gm.template.plugin.PluginFragment
import com.gm.template.plugin.PluginManager
import com.gm.template.ui.MainActivityInterface
import com.gm.template.ui.MainViewModel
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.android.play.core.splitinstall.*
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MainActivityInterface {
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

    private var _binding: ActivityMainBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
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
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase)
        SplitCompat.install(this)
        SplitCompat.installActivity(this)
    }

    private fun launchFeature(pluginActionName: String) {
        bindToService(pluginActionName)
    }

    private val uiLogin by lazy { "ui_login" }

    override fun loadFragment(pluginFragment: PluginFragment, addToBackStack: Boolean) {
        SplitCompat.install(this)
        SplitCompat.installActivity(this)

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
                    navOptions, DynamicExtras(installMonitor)
                )

                if (installMonitor.isInstallRequired) {
                    installMonitor.status.observe(
                        this@MainActivity,
                        object : Observer<SplitInstallSessionState> {
                            override fun onChanged(sessionState: SplitInstallSessionState) {
                                when (sessionState.status()) {
                                    SplitInstallSessionStatus.INSTALLED -> {
                                        SplitCompat.install(this@MainActivity as Context)
                                        SplitCompat.installActivity(this@MainActivity as Context)

                                        navController.navigate(
                                            pluginFragment.navGraphId, null, navOptions
                                        )
                                    }

                                    SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                                        CoroutineScope(Main).launch {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                    SplitInstallSessionStatus.FAILED -> {
                                        CoroutineScope(Main).launch {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "SplitInstallSessionStatus.FAILED",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                    SplitInstallSessionStatus.CANCELED -> {
                                        CoroutineScope(Main).launch {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "SplitInstallSessionStatus.CANCELED",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                    SplitInstallSessionStatus.CANCELING -> {
                                        CoroutineScope(Main).launch {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "SplitInstallSessionStatus.CANCELING",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                    SplitInstallSessionStatus.DOWNLOADED -> {
                                        CoroutineScope(Main).launch {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "SplitInstallSessionStatus.DOWNLOADED",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                    SplitInstallSessionStatus.DOWNLOADING -> {
                                        CoroutineScope(Main).launch {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "SplitInstallSessionStatus.DOWNLOADING",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                    SplitInstallSessionStatus.INSTALLING -> {
                                        CoroutineScope(Main).launch {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "SplitInstallSessionStatus.INSTALLING",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                    SplitInstallSessionStatus.PENDING -> {
                                        CoroutineScope(Main).launch {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "SplitInstallSessionStatus.PENDING",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                    SplitInstallSessionStatus.UNKNOWN -> {
                                        CoroutineScope(Main).launch {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "SplitInstallSessionStatus.UNKNOWN",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                }

                                if (sessionState.hasTerminalStatus()) {
                                    installMonitor.status.removeObserver(this)
                                }
                            }
                        })
                }
            }
        }
    }

//    override fun findPluginByActionName(actionName: String): List<Plugin> {
//        TODO("Not yet implemented")
//    }

    override fun loadFragmentByAction(
        pluginActionName: String,
        addToBackStack: Boolean,
        arguments: HashMap<String, Any>
    ) {
        val splitInstallStateUpdatedListener = SplitInstallStateUpdatedListener { state ->
            when (state.status()) {
                SplitInstallSessionStatus.INSTALLED -> {
                    onSuccessfulLoad(uiLogin, pluginActionName)
                }
                SplitInstallSessionStatus.DOWNLOADING -> {}
                SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {}
                SplitInstallSessionStatus.INSTALLING -> {}
                SplitInstallSessionStatus.FAILED -> {}
                SplitInstallSessionStatus.CANCELED -> {}
                SplitInstallSessionStatus.CANCELING -> {}
                SplitInstallSessionStatus.DOWNLOADED -> {}
                SplitInstallSessionStatus.PENDING -> {}
                SplitInstallSessionStatus.UNKNOWN -> {}
            }
        }

        val splitInstallManager: SplitInstallManager =
            SplitInstallManagerFactory.create(applicationContext)

        if (splitInstallManager.installedModules.contains(uiLogin)) {
            SplitCompat.install(this)
            SplitCompat.installActivity(this)
            bindToService(pluginActionName)
        } else {
            val request = SplitInstallRequest.newBuilder()
                .addModule(uiLogin)
                .build()

            splitInstallManager.registerListener(splitInstallStateUpdatedListener)

            splitInstallManager.startInstall(request)
                .addOnCompleteListener {
                    splitInstallManager.unregisterListener(splitInstallStateUpdatedListener)
                }
                .addOnSuccessListener {
                    SplitCompat.install(this)
                    SplitCompat.installActivity(this)

                    //bindToService(pluginActionName)
                }
                .addOnFailureListener {}
        }
    }

    private fun bindToService(pluginActionName: String) {
        val plugins = findPluginByActionName(pluginActionName)
        if (plugins.isNotEmpty()) {
            val plugin = plugins[0]
            mainViewModel.actionName = pluginActionName
            val bindIntent =
                Intent().apply { setClassName(plugin.servicePackageName, plugin.serviceName) }
            applicationContext.bindService(bindIntent, mServiceConnection, BIND_AUTO_CREATE)
            mainViewModel.mIsBound = true
        } else {
            CoroutineScope(Main).launch {
                Toast.makeText(
                    this@MainActivity,
                    "Plugin did not load",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    @SuppressLint("QueryPermissionsNeeded")
    override fun findPluginByActionName(actionName: String): List<Plugin> {
        val resolveInfoList = packageManager.queryIntentServices(
            Intent(actionName),
            PackageManager.GET_META_DATA
        )
        val plugins: MutableList<Plugin> = ArrayList()
        if (resolveInfoList.size == 0) {
            return plugins
        }

        for (resolveInfo in resolveInfoList) {
            if (packageName.equals(resolveInfo.serviceInfo.processName, ignoreCase = true)) {
                val plugin = Plugin(resolveInfo)
                plugins.add(plugin)
            }
        }

        return plugins
    }


    private fun onSuccessfulLoad(moduleName: String, pluginActionName: String) {
        when (moduleName) {
            uiLogin -> launchFeature(pluginActionName)
        }
    }

    private val mServiceConnection = object : ServiceConnection {
        var pluginInterface: IPluginInterface? = null
        override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
            pluginInterface = IPluginInterface.Stub.asInterface(binder)
            pluginInterface?.let {
                try {
                    it.registerFragment("some fragment name")
                    val pluginFragment: PluginFragment? =
                        PluginManager.getInstance(applicationContext)
                            .getPluginFragmentByName(mainViewModel.actionName)
                    pluginFragment?.argument = mainViewModel.mArguments
                    pluginFragment?.let { plugin -> loadFragment(plugin, false) }
                } catch (e: Exception) {
                    Log.i("E", "Something wrong")
                }

                if (mainViewModel.mIsBound) {
                    mainViewModel.mIsBound = false
                    applicationContext.unbindService(this)
                }
            }
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            pluginInterface = null
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
