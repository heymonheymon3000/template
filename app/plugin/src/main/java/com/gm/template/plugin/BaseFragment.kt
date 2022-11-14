package com.gm.template.plugin

import android.content.Context
import androidx.fragment.app.Fragment
import com.google.android.play.core.splitcompat.SplitCompat

abstract class BaseFragment: Fragment() {
    override fun onAttach(context: Context) {
        super.onAttach(context)
        SplitCompat.installActivity(context)
    }
}