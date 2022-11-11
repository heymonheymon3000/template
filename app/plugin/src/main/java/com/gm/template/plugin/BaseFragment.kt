package com.gm.template.plugin

import android.content.Context
import com.google.android.play.core.splitcompat.SplitCompat

open class BaseFragment: PluginFragment() {
    override fun onAttach(context: Context) {
        super.onAttach(context)
        SplitCompat.installActivity(context)
    }
}