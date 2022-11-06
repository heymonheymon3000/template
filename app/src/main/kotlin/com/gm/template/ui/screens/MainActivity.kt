package com.gm.template.ui.screens

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.Observer
import androidx.navigation.NavController
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
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import dagger.hilt.android.AndroidEntryPoint
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
    }

//    private fun loadPlugins() {
//        Log.i("Terry", "loadPlugins was called")
//        loadFragmentByAction("login", false, HashMap())
//    }

    override fun loadFragmentByAction(pluginActionName: String, addToBackStack: Boolean, arguments: HashMap<String, Any>) {
        val plugins = findPluginByActionName(pluginActionName)
        if (plugins.isNotEmpty()) {
            val plugin = plugins[0]
            val bindIntent = Intent()
            bindIntent.setClassName(plugin.servicePackageName, plugin.serviceName)
            mainViewModel.actionName = pluginActionName
//            Log.i("Terry", "Before bindService")
            context.bindService(bindIntent, this, BIND_AUTO_CREATE)
//            Log.i("Terry", "After bindService")
            mainViewModel.mIsBound = true
        } else {
//            Log.i("Terry", "Did not load!!!")
        }
    }

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
//        CoroutineScope(Main).launch {
//        Log.i("Terry", "onServiceConnected bindService")
        runOnUiThread {
//            Log.i("Terry", "getting pluginInterface ")
            val pluginInterface: IPluginInterface? = IPluginInterface.Stub.asInterface(binder)
//            Log.i("Terry", "got pluginInterface ")

            if(pluginInterface !=null ) {
                try {
                    pluginInterface.registerFragment("some fragment name")
//                    Log.i("Terry", "pluginInterface was not null ")

//                    Log.i("Terry", "mainViewModel.actionName = ${mainViewModel.actionName}")


                    val pluginFragment: PluginFragment? =
                        PluginManager.getInstance(context).getPluginFragmentByName(mainViewModel.actionName)


                    pluginFragment?.argument = mainViewModel.mArguments
                    pluginFragment?.let { plugin ->
//                        Log.i("Terry", "pluginFragment was not null")

                        loadFragment(plugin, false)
                    }
                } catch (e: Exception) {
//                    Log.i("E", "Something wrong")
                }

                if (mainViewModel.mIsBound) {
                    mainViewModel.mIsBound = false
                    context.unbindService(this)
                }
            }
        }
    }

    override fun loadFragment(pluginFragment: PluginFragment, addToBackStack: Boolean) {
        if(pluginFragment.navGraphId != navController.currentDestination?.id) {
//            val navOptions = NavOptions.Builder().setLaunchSingleTop(true)
//                .setEnterAnim(R.anim.slide_in_from_right)
//                .setExitAnim(R.anim.slide_out_to_left)
//                .setPopEnterAnim(R.anim.slide_in_from_left)
//                .setPopExitAnim(R.anim.slide_out_to_right)
//                .build()
//            val duration = Toast.LENGTH_LONG


//            CoroutineScope(Main).launch {
            val installMonitor = DynamicInstallMonitor()

            navController.navigate(
                    pluginFragment.navGraphId, null,
                    null, DynamicExtras(installMonitor))

            if (installMonitor.isInstallRequired) {
                installMonitor.status.observe(
                    this@MainActivity,
                    object : Observer<SplitInstallSessionState> {
                        override fun onChanged(sessionState: SplitInstallSessionState) {
                            when (sessionState.status()) {
                                SplitInstallSessionStatus.INSTALLED -> {

                                    //Log.i("Terry", "SplitInstallSessionStatus.INSTALLED")
                                    // Call navigate again here or after user taps again in the UI:
                                    navController.navigate(
                                        pluginFragment.navGraphId, null,
                                        null, null)
                                }

                                SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
//                                        val text = "SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION"
//                                        val toast = Toast.makeText(applicationContext, text, duration)
                                    //CoroutineScope(Main).launch {  toast.show() }
//                                        Log.i(
//                                            "Terry",
//                                            "SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION"
//                                        )
                                    //SplitInstallManager.startConfirmationDialogForResult(...)
                                }

                                // Handle all remaining states:
                                SplitInstallSessionStatus.FAILED -> {
//                                        val text = "SplitInstallSessionStatus.FAILED"
//                                        val toast = Toast.makeText(applicationContext, text, duration)
                                    //CoroutineScope(Main).launch {  toast.show() }
//                                        Log.i("Terry", "SplitInstallSessionStatus.FAILED")
                                }

                                SplitInstallSessionStatus.CANCELED -> {
//                                        val text = "SplitInstallSessionStatus.CANCELED"
//                                        val toast = Toast.makeText(applicationContext, text, duration)
                                    //CoroutineScope(Main).launch {  toast.show() }
//                                        Log.i("Terry", "SplitInstallSessionStatus.CANCELED")
                                }

                                SplitInstallSessionStatus.CANCELING -> {
//                                        val text = "SplitInstallSessionStatus.CANCELING"
//                                        val toast = Toast.makeText(applicationContext, text, duration)
                                    //CoroutineScope(Main).launch {  toast.show() }
//                                        Log.i("Terry", "SplitInstallSessionStatus.CANCELING")
                                }

                                SplitInstallSessionStatus.DOWNLOADED -> {
//                                        val text = "SplitInstallSessionStatus.DOWNLOADED"
//                                        val toast = Toast.makeText(applicationContext, text, duration)
                                    //CoroutineScope(Main).launch {  toast.show() }
//                                        Log.i("Terry", "SplitInstallSessionStatus.DOWNLOADED")
                                }

                                SplitInstallSessionStatus.DOWNLOADING -> {
//                                        val text = "SplitInstallSessionStatus.DOWNLOADING"
//                                        val toast = Toast.makeText(applicationContext, text, duration)
                                    //CoroutineScope(Main).launch {  toast.show() }
//                                        Log.i("Terry", "SplitInstallSessionStatus.DOWNLOADING")
                                }

                                SplitInstallSessionStatus.INSTALLING -> {
//                                        val text = "SplitInstallSessionStatus.INSTALLING"
//                                        val toast = Toast.makeText(applicationContext, text, duration)
                                    //CoroutineScope(Main).launch {  toast.show() }
//                                        Log.i("Terry", "SplitInstallSessionStatus.INSTALLING")
                                }

                                SplitInstallSessionStatus.PENDING -> {
//                                        val text = "SplitInstallSessionStatus.PENDING"
//                                        val toast = Toast.makeText(applicationContext, text, duration)
                                    //CoroutineScope(Main).launch {  toast.show() }
//                                        Log.i("Terry", "SplitInstallSessionStatus.PENDING")
                                }

                                SplitInstallSessionStatus.UNKNOWN -> {
//                                        val text = "SplitInstallSessionStatus.UNKNOWN"
//                                        val toast = Toast.makeText(applicationContext, text, duration)
                                    //CoroutineScope(Main).launch {  toast.show() }
//                                        Log.i("Terry", "SplitInstallSessionStatus.UNKNOWN")
                                }
                            }

                            if (sessionState.hasTerminalStatus()) {
                                installMonitor.status.removeObserver(this)
                            }
                        }
                    })
            } else {
//                Log.i("Terry", "ALREADY SplitInstallSessionStatus.INSTALLING")
//                    val text = "ALREADY installed!!"
//                    val toast = Toast.makeText(this, "text", duration)
               // CoroutineScope(Main).launch {  toast.show() }

            }
//            }
        }
    }
}