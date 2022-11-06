package com.gm.template.ui;

import com.gm.template.plugin.Plugin;
import com.gm.template.plugin.PluginFragment;

@kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\u0005\u001a\u00020\u0006H&J\u0018\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH&J<\u0010\r\u001a\u00020\b2\u0006\u0010\u000e\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\f2\"\u0010\u000f\u001a\u001e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\u0010j\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u0001`\u0011H&\u00a8\u0006\u0012"}, d2 = {"Lcom/gm/template/ui/MainActivityInterface;", "", "findPluginByActionName", "", "Lcom/gm/template/plugin/Plugin;", "actionName", "", "loadFragment", "", "pluginFragment", "Lcom/gm/template/plugin/PluginFragment;", "addToBackStack", "", "loadFragmentByAction", "pluginActionName", "arguments", "Ljava/util/HashMap;", "Lkotlin/collections/HashMap;", "app_debug"})
public abstract interface MainActivityInterface {
    
    public abstract void loadFragment(@org.jetbrains.annotations.NotNull()
    com.gm.template.plugin.PluginFragment pluginFragment, boolean addToBackStack);
    
    public abstract void loadFragmentByAction(@org.jetbrains.annotations.NotNull()
    java.lang.String pluginActionName, boolean addToBackStack, @org.jetbrains.annotations.NotNull()
    java.util.HashMap<java.lang.String, java.lang.Object> arguments);
    
    @org.jetbrains.annotations.NotNull()
    public abstract java.util.List<com.gm.template.plugin.Plugin> findPluginByActionName(@org.jetbrains.annotations.NotNull()
    java.lang.String actionName);
}