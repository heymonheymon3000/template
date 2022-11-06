package com.gm.template.plugin

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteException

abstract class PluginService: Service() {
    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    override fun onUnbind(intent: Intent): Boolean {
        mBinder = null
        return super.onUnbind(intent)
    }

    private var mBinder: IPluginInterface.Stub? = object: IPluginInterface.Stub() {
        @Throws(RemoteException::class)
        override fun registerFragment(name: String) { registerPluginFragment(name) }
    }

    protected abstract fun registerPluginFragment(name: String)
}