//package com.gm.template.ui.screens
//
//import android.annotation.SuppressLint
//import android.content.ComponentName
//import android.content.Context
//import android.content.Intent
//import android.content.ServiceConnection
//import android.content.pm.PackageManager
//import android.os.Build
//import android.os.Bundle
//import android.os.IBinder
//import android.util.Log
//import androidx.activity.viewModels
//import androidx.annotation.RequiresApi
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.WindowCompat
//import androidx.lifecycle.Observer
//import androidx.navigation.NavController
//import androidx.navigation.dynamicfeatures.DynamicExtras
//import androidx.navigation.dynamicfeatures.DynamicInstallMonitor
//import androidx.navigation.dynamicfeatures.fragment.DynamicNavHostFragment
//import androidx.navigation.fragment.NavHostFragment
//import com.gm.template.R
//import com.gm.template.databinding.ActivityMainBinding
//import com.gm.template.plugin.IPluginInterface
//import com.gm.template.plugin.Plugin
//import com.gm.template.plugin.PluginFragment
//import com.gm.template.plugin.PluginManager
//import com.gm.template.ui.MainActivityInterface
//import com.gm.template.ui.MainViewModel
//import com.google.android.play.core.splitinstall.SplitInstallSessionState
//import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
//import dagger.hilt.android.AndroidEntryPoint
//import java.util.ArrayList
//
//@AndroidEntryPoint
//abstract class NavigationActivity: AppCompatActivity(), MainActivityInterface, ServiceConnection {
//    private val TAG = NavigationActivity::class.java.simpleName
//    private val mainViewModel: MainViewModel by viewModels()
//    private lateinit var navController: NavController
//    private lateinit var navHostFragment: NavHostFragment
//    lateinit var context: Context
//
//    private var _binding: ActivityMainBinding? = null
//    // This property is only valid between onCreateView and onDestroyView.
//    private val binding get() = _binding!!
//
//    protected var pluginIdentifier: String? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        supportActionBar?.hide()
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//
//        _binding = ActivityMainBinding.inflate(layoutInflater)
//
//        setContentView(binding.root)
//
//        navHostFragment =
//            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as DynamicNavHostFragment
//        navController = navHostFragment.navController
//    }
//
//    override fun attachBaseContext(newBase: Context) {
//        context = newBase
//        super.attachBaseContext(newBase)
//    }
//
//    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
//    fun loadPlugins() {
//        Log.i("Terry", "loadPlugins was called")
//        loadFragmentByAction("login", false, HashMap())
//    }
//
//    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
//    override fun loadFragmentByAction(pluginActionName: String, addToBackStack: Boolean, arguments: HashMap<String, Any>) {
//        val plugins = findPluginByActionName(pluginActionName)
//        if (plugins.isNotEmpty()) {
//            val plugin = plugins[0]
//            val bindIntent = Intent()
//            bindIntent.setClassName(plugin.servicePackageName, plugin.serviceName)
//            mainViewModel.actionName = pluginActionName
//            Log.i("Terry", "Before bindService")
//            context.bindService(bindIntent, this, BIND_AUTO_CREATE)
//            Log.i("Terry", "After bindService")
//            mainViewModel.mIsBound = true
//        } else {
//            Log.i("Terry", "Did not load!!!")
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
//    @SuppressLint("QueryPermissionsNeeded")
//    override fun findPluginByActionName(actionName: String): List<Plugin> {
//        val resolveInfoList = context.packageManager.queryIntentServices(
//            Intent(actionName),
//            PackageManager.GET_META_DATA
//        )
//
//        val plugins: MutableList<Plugin> = ArrayList()
//
//        if (resolveInfoList.size == 0) {
//            return plugins
//        }
//
//        for (resolveInfo in resolveInfoList) {
//            if (context.packageName.equals(resolveInfo.serviceInfo.processName, ignoreCase = true)) {
//                val plugin = Plugin(resolveInfo)
//                plugins.add(plugin)
//            }
//        }
//
//        return plugins
//    }
//
//
//    override fun onServiceDisconnected(componentName: ComponentName?) {}
//    override fun onServiceConnected(componentName: ComponentName?, binder: IBinder?) {
////        CoroutineScope(Main).launch {
//        Log.i("Terry", "onServiceConnected bindService")
//        runOnUiThread {
//            Log.i("Terry", "getting pluginInterface ")
//            var pluginInterface: IPluginInterface? = IPluginInterface.Stub.asInterface(binder)
//            Log.i("Terry", "got pluginInterface ")
//
//            if(pluginInterface !=null ) {
//                try {
//                    pluginInterface.registerFragment("some fragment name")
//                    Log.i("Terry", "pluginInterface was not null ")
//
//                    Log.i("Terry", "mainViewModel.actionName = ${mainViewModel.actionName}")
//
//
//                    val pluginFragment: PluginFragment? =
//                        PluginManager.getInstance(context).getPluginFragmentByName(mainViewModel.actionName)
//
//
//                    pluginFragment?.argument = mainViewModel.mArguments
//                    pluginFragment?.let { plugin ->
//                        Log.i("Terry", "pluginFragment was not null")
//
//                         loadFragment(plugin, false)
//                    }
//                } catch (e: Exception) {
//                    Log.i("E", "Something wrong")
//                }
//
//                if (mainViewModel.mIsBound) {
//                    mainViewModel.mIsBound = false
//                    context.unbindService(this)
//                }
//
//                pluginInterface = null
//            }
//        }
//    }
//
//
//
//    override fun loadFragment(pluginFragment: PluginFragment, addToBackStack: Boolean) {
//        //CoroutineScope(Main).launch {
//
//        Log.i("Terry", pluginFragment.navGraphId.toString())
//        Log.i("Terry", pluginFragment.pluginId.toString())
//        Log.i("Terry", pluginFragment.id.toString())
//        Log.i("Terry", R.id.login_nav_graph.toString())
//
//        runOnUiThread {
//            val installMonitor = DynamicInstallMonitor()
//
//            navController.navigate(
//                pluginFragment.navGraphId,
//                null,
//                null,
//                DynamicExtras(installMonitor))
//
//            if (installMonitor.isInstallRequired) {
//                installMonitor.status.observe(this, object : Observer<SplitInstallSessionState> {
//                    override fun onChanged(sessionState: SplitInstallSessionState) {
//                        when (sessionState.status()) {
//                            SplitInstallSessionStatus.INSTALLED -> {
//                                Log.i("Terry", "SplitInstallSessionStatus.INSTALLED")
//                                // Call navigate again here or after user taps again in the UI:
//                                navController.navigate(pluginFragment.navGraphId, null, null, null)
//
//                            }
//                            SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
//                                Log.i("Terry", "SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION")
//
//                                //SplitInstallManager.startConfirmationDialogForResult(...)
//                            }
//
//                            // Handle all remaining states:
//                            SplitInstallSessionStatus.FAILED -> {
//                                Log.i("Terry", "SplitInstallSessionStatus.FAILED")
//
//                            }
//
//                            SplitInstallSessionStatus.CANCELED -> {
//                                Log.i("Terry", "SplitInstallSessionStatus.CANCELED")
//
//                            }
//
//                            SplitInstallSessionStatus.CANCELING -> {
//                                Log.i("Terry", "SplitInstallSessionStatus.CANCELING")
//
//                            }
//
//                            SplitInstallSessionStatus.DOWNLOADED -> {
//                                Log.i("Terry", "SplitInstallSessionStatus.DOWNLOADED")
//
//                            }
//
//                            SplitInstallSessionStatus.DOWNLOADING -> {
//                                Log.i("Terry", "SplitInstallSessionStatus.DOWNLOADING")
//
//                            }
//
//                            SplitInstallSessionStatus.INSTALLING -> {
//                                Log.i("Terry", "SplitInstallSessionStatus.INSTALLING")
//
//                            }
//
//                            SplitInstallSessionStatus.PENDING -> {
//                                Log.i("Terry", "SplitInstallSessionStatus.PENDING")
//
//                            }
//
//                            SplitInstallSessionStatus.UNKNOWN -> {
//                                Log.i("Terry", "SplitInstallSessionStatus.UNKNOWN")
//
//                            }
//                        }
//
//                        if (sessionState.hasTerminalStatus()) {
//                            installMonitor.status.removeObserver(this);
//                        }
//                    }
//                });
//            }
//
//
//
////                navController.navigate(
////                    pluginFragment.navGraphId,
////                    Bundle(),
////                    NavOptions.Builder().setLaunchSingleTop(true)
////                        .setEnterAnim(R.anim.slide_in_from_right)
////                        .setExitAnim(R.anim.slide_out_to_left)
////                        .setPopEnterAnim(R.anim.slide_in_from_left)
////                        .setPopExitAnim(R.anim.slide_out_to_right)
////                        .build())
//            }
//        }
//
//}