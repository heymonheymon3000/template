package com.gm.template.ui;

import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import javax.inject.Inject;

@dagger.hilt.android.lifecycle.HiltViewModel()
@kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0005\b\u0007\u0018\u00002\u00020\u0001B\u0007\b\u0007\u00a2\u0006\u0002\u0010\u0002R\u001a\u0010\u0003\u001a\u00020\u0004X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR6\u0010\t\u001a\u001e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u000b0\nj\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u000b`\fX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\u001a\u0010\u0011\u001a\u00020\u0012X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0014\"\u0004\b\u0015\u0010\u0016\u00a8\u0006\u0017"}, d2 = {"Lcom/gm/template/ui/MainViewModel;", "Landroidx/lifecycle/ViewModel;", "()V", "actionName", "", "getActionName", "()Ljava/lang/String;", "setActionName", "(Ljava/lang/String;)V", "mArguments", "Ljava/util/HashMap;", "", "Lkotlin/collections/HashMap;", "getMArguments", "()Ljava/util/HashMap;", "setMArguments", "(Ljava/util/HashMap;)V", "mIsBound", "", "getMIsBound", "()Z", "setMIsBound", "(Z)V", "app_release"})
public final class MainViewModel extends androidx.lifecycle.ViewModel {
    private boolean mIsBound = false;
    @org.jetbrains.annotations.NotNull()
    private java.util.HashMap<java.lang.String, java.lang.Object> mArguments;
    @org.jetbrains.annotations.NotNull()
    private java.lang.String actionName = "";
    
    @javax.inject.Inject()
    public MainViewModel() {
        super();
    }
    
    public final boolean getMIsBound() {
        return false;
    }
    
    public final void setMIsBound(boolean p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.HashMap<java.lang.String, java.lang.Object> getMArguments() {
        return null;
    }
    
    public final void setMArguments(@org.jetbrains.annotations.NotNull()
    java.util.HashMap<java.lang.String, java.lang.Object> p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getActionName() {
        return null;
    }
    
    public final void setActionName(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
    }
}