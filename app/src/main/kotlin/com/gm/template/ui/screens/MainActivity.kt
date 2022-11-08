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
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.util.ArrayList

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MainActivityInterface, ServiceConnection {
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var context: Context

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
        context = newBase
        super.attachBaseContext(newBase)
        SplitCompat.install(this)
    }

//    private fun loadPlugins() {
//        Log.i("Terry", "loadPlugins was called")
//        loadFragmentByAction("login", false, HashMap())
//    }

    override fun loadFragmentByAction(pluginActionName: String, addToBackStack: Boolean, arguments: HashMap<String, Any>) {
        val installMonitor = DynamicInstallMonitor()

        navController.navigate(
            StartFragmentDirections.startFragmentToLoginNavGraph(),
            DynamicExtras(installMonitor))

        if (installMonitor.isInstallRequired) {
            installMonitor.status.observe(this,
                object : Observer<SplitInstallSessionState> {
                    override fun onChanged(sessionState: SplitInstallSessionState) {
                        when (sessionState.status()) {
                            SplitInstallSessionStatus.INSTALLED -> {
                                navController.navigate(StartFragmentDirections.startFragmentToLoginNavGraph())
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
//        val plugins = findPluginByActionName(pluginActionName)
//        if (plugins.isNotEmpty()) {
//            val plugin = plugins[0]
//            val bindIntent = Intent()
//            bindIntent.setClassName(plugin.servicePackageName, plugin.serviceName)
//            mainViewModel.actionName = pluginActionName
//            context.bindService(bindIntent, this, BIND_AUTO_CREATE)
//            mainViewModel.mIsBound = true
//        } else {
//            CoroutineScope(Main).launch {
//                Toast.makeText(
//                    this@MainActivity,
//                    "Plugin did not load",
//                    Toast.LENGTH_LONG).show()
//            }
//        }
//    }

    @SuppressLint("QueryPermissionsNeeded")
    override fun findPluginByActionName(actionName: String): List<Plugin> {
        val resolveInfoList = context.packageManager.queryIntentServices(
            Intent(actionName),
            PackageManager.GET_META_DATA
        )

        val plugins: MutableList<Plugin> = ArrayList()

        if (resolveInfoList.size == 0) {
            return plugins
        }

        for (resolveInfo in resolveInfoList) {
            if (context.packageName.equals(resolveInfo.serviceInfo.processName, ignoreCase = true)) {
                val plugin = Plugin(resolveInfo)
                plugins.add(plugin)
            }
        }

        return plugins
    }


    override fun onServiceDisconnected(componentName: ComponentName?) {}

    override fun onServiceConnected(componentName: ComponentName?, binder: IBinder?) {
        CoroutineScope(Main).launch {
            val pluginInterface: IPluginInterface? = IPluginInterface.Stub.asInterface(binder)
            if(pluginInterface !=null ) {
                try {
                    pluginInterface.registerFragment("some fragment name")
                    val pluginFragment: PluginFragment? =
                        PluginManager.getInstance(context).getPluginFragmentByName(mainViewModel.actionName)
                    pluginFragment?.argument = mainViewModel.mArguments
                    pluginFragment?.let { plugin -> loadFragment(plugin, false) }
                } catch (e: Exception) {
                    Log.i("E", "Something wrong")
                }

                if (mainViewModel.mIsBound) {
                    mainViewModel.mIsBound = false
                    context.unbindService(this@MainActivity)
                }
            }
        }
    }

    override fun loadFragment(pluginFragment: PluginFragment, addToBackStack: Boolean) {
        if(pluginFragment.navGraphId != navController.currentDestination?.id) {
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
                                    CoroutineScope(Main).launch {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "SplitInstallSessionStatus.INSTALLED",
                                            Toast.LENGTH_LONG).show()
                                    }

                                    navController.navigate(
                                        pluginFragment.navGraphId, null,
                                        navOptions, null)
                                }

                                SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                                    CoroutineScope(Main).launch {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION",
                                            Toast.LENGTH_LONG).show()
                                    }
                                    //SplitInstallManager.startConfirmationDialogForResult(...)
                                }

                                SplitInstallSessionStatus.FAILED -> {
                                    CoroutineScope(Main).launch {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "SplitInstallSessionStatus.FAILED",
                                            Toast.LENGTH_LONG).show()
                                    }
                                }

                                SplitInstallSessionStatus.CANCELED -> {
                                    CoroutineScope(Main).launch {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "SplitInstallSessionStatus.CANCELED",
                                            Toast.LENGTH_LONG).show()
                                    }
                                }

                                SplitInstallSessionStatus.CANCELING -> {
                                    CoroutineScope(Main).launch {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "SplitInstallSessionStatus.CANCELING",
                                            Toast.LENGTH_LONG).show()
                                    }
                                }

                                SplitInstallSessionStatus.DOWNLOADED -> {
                                    CoroutineScope(Main).launch {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "SplitInstallSessionStatus.DOWNLOADED",
                                            Toast.LENGTH_LONG).show()
                                    }
                                }

                                SplitInstallSessionStatus.DOWNLOADING -> {
                                    CoroutineScope(Main).launch {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "SplitInstallSessionStatus.DOWNLOADING",
                                            Toast.LENGTH_LONG).show()
                                    }
                                }

                                SplitInstallSessionStatus.INSTALLING -> {
                                    CoroutineScope(Main).launch {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "SplitInstallSessionStatus.INSTALLING",
                                            Toast.LENGTH_LONG).show()
                                    }
                                }

                                SplitInstallSessionStatus.PENDING -> {
                                    CoroutineScope(Main).launch {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "SplitInstallSessionStatus.PENDING",
                                            Toast.LENGTH_LONG).show()
                                    }
                                }

                                SplitInstallSessionStatus.UNKNOWN -> {
                                    CoroutineScope(Main).launch {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "SplitInstallSessionStatus.UNKNOWN",
                                            Toast.LENGTH_LONG).show()
                                    }
                                }
                            }

                            if (sessionState.hasTerminalStatus()) {
                                installMonitor.status.removeObserver(this)
                            }
                        }
                    })
            } else {
                CoroutineScope(Main).launch {
                    Toast.makeText(
                        this@MainActivity,
                        "ALREADY installed!!",
                        Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}