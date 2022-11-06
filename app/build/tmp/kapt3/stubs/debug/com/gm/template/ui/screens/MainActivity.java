package com.gm.template.ui.screens;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.dynamicfeatures.DynamicExtras;
import androidx.navigation.dynamicfeatures.DynamicInstallMonitor;
import androidx.navigation.dynamicfeatures.fragment.DynamicNavHostFragment;
import androidx.navigation.fragment.NavHostFragment;
import com.gm.template.R;
import com.gm.template.databinding.ActivityMainBinding;
import com.gm.template.plugin.IPluginInterface;
import com.gm.template.plugin.Plugin;
import com.gm.template.plugin.PluginFragment;
import com.gm.template.plugin.PluginManager;
import com.gm.template.ui.MainActivityInterface;
import com.gm.template.ui.MainViewModel;
import com.google.android.play.core.splitinstall.SplitInstallSessionState;
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus;
import dagger.hilt.android.AndroidEntryPoint;
import java.util.ArrayList;

@kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000\u0086\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u00012\u00020\u00022\u00020\u0003B\u0005\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u000bH\u0014J\u0016\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u001f0\u001e2\u0006\u0010 \u001a\u00020!H\u0017J\u0018\u0010\"\u001a\u00020\u001b2\u0006\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020&H\u0016J<\u0010\'\u001a\u00020\u001b2\u0006\u0010(\u001a\u00020!2\u0006\u0010%\u001a\u00020&2\"\u0010)\u001a\u001e\u0012\u0004\u0012\u00020!\u0012\u0004\u0012\u00020+0*j\u000e\u0012\u0004\u0012\u00020!\u0012\u0004\u0012\u00020+`,H\u0016J\u0012\u0010-\u001a\u00020\u001b2\b\u0010.\u001a\u0004\u0018\u00010/H\u0014J\u001c\u00100\u001a\u00020\u001b2\b\u00101\u001a\u0004\u0018\u0001022\b\u00103\u001a\u0004\u0018\u000104H\u0016J\u0012\u00105\u001a\u00020\u001b2\b\u00101\u001a\u0004\u0018\u000102H\u0016R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\u00020\u00068BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\b\u0010\tR\u001a\u0010\n\u001a\u00020\u000bX\u0086.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000fR\u001b\u0010\u0010\u001a\u00020\u00118BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0014\u0010\u0015\u001a\u0004\b\u0012\u0010\u0013R\u000e\u0010\u0016\u001a\u00020\u0017X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0019X\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u00066"}, d2 = {"Lcom/gm/template/ui/screens/MainActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "Lcom/gm/template/ui/MainActivityInterface;", "Landroid/content/ServiceConnection;", "()V", "_binding", "Lcom/gm/template/databinding/ActivityMainBinding;", "binding", "getBinding", "()Lcom/gm/template/databinding/ActivityMainBinding;", "context", "Landroid/content/Context;", "getContext", "()Landroid/content/Context;", "setContext", "(Landroid/content/Context;)V", "mainViewModel", "Lcom/gm/template/ui/MainViewModel;", "getMainViewModel", "()Lcom/gm/template/ui/MainViewModel;", "mainViewModel$delegate", "Lkotlin/Lazy;", "navController", "Landroidx/navigation/NavController;", "navHostFragment", "Landroidx/navigation/fragment/NavHostFragment;", "attachBaseContext", "", "newBase", "findPluginByActionName", "", "Lcom/gm/template/plugin/Plugin;", "actionName", "", "loadFragment", "pluginFragment", "Lcom/gm/template/plugin/PluginFragment;", "addToBackStack", "", "loadFragmentByAction", "pluginActionName", "arguments", "Ljava/util/HashMap;", "", "Lkotlin/collections/HashMap;", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onServiceConnected", "componentName", "Landroid/content/ComponentName;", "binder", "Landroid/os/IBinder;", "onServiceDisconnected", "app_debug"})
@dagger.hilt.android.AndroidEntryPoint()
public final class MainActivity extends androidx.appcompat.app.AppCompatActivity implements com.gm.template.ui.MainActivityInterface, android.content.ServiceConnection {
    private final kotlin.Lazy mainViewModel$delegate = null;
    private androidx.navigation.NavController navController;
    private androidx.navigation.fragment.NavHostFragment navHostFragment;
    public android.content.Context context;
    private com.gm.template.databinding.ActivityMainBinding _binding;
    
    public MainActivity() {
        super();
    }
    
    private final com.gm.template.ui.MainViewModel getMainViewModel() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final android.content.Context getContext() {
        return null;
    }
    
    public final void setContext(@org.jetbrains.annotations.NotNull()
    android.content.Context p0) {
    }
    
    private final com.gm.template.databinding.ActivityMainBinding getBinding() {
        return null;
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override()
    protected void attachBaseContext(@org.jetbrains.annotations.NotNull()
    android.content.Context newBase) {
    }
    
    @java.lang.Override()
    public void loadFragmentByAction(@org.jetbrains.annotations.NotNull()
    java.lang.String pluginActionName, boolean addToBackStack, @org.jetbrains.annotations.NotNull()
    java.util.HashMap<java.lang.String, java.lang.Object> arguments) {
    }
    
    @org.jetbrains.annotations.NotNull()
    @android.annotation.SuppressLint(value = {"QueryPermissionsNeeded"})
    @java.lang.Override()
    public java.util.List<com.gm.template.plugin.Plugin> findPluginByActionName(@org.jetbrains.annotations.NotNull()
    java.lang.String actionName) {
        return null;
    }
    
    @java.lang.Override()
    public void onServiceDisconnected(@org.jetbrains.annotations.Nullable()
    android.content.ComponentName componentName) {
    }
    
    @java.lang.Override()
    public void onServiceConnected(@org.jetbrains.annotations.Nullable()
    android.content.ComponentName componentName, @org.jetbrains.annotations.Nullable()
    android.os.IBinder binder) {
    }
    
    @java.lang.Override()
    public void loadFragment(@org.jetbrains.annotations.NotNull()
    com.gm.template.plugin.PluginFragment pluginFragment, boolean addToBackStack) {
    }
}