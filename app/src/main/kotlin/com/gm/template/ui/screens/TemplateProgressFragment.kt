package com.gm.template.ui.screens

import android.widget.ProgressBar
import android.widget.TextView
import androidx.navigation.dynamicfeatures.fragment.ui.AbstractProgressFragment
import com.gm.template.R

class TemplateProgressFragment : AbstractProgressFragment(R.layout.fragment_progress) {

    override fun onProgress(status: Int, bytesDownloaded: Long, bytesTotal: Long) {
        view?.findViewById<ProgressBar>(R.id.progressBar)?.progress =
            (bytesDownloaded.toDouble() * 100 / bytesTotal).toInt()
    }

    override fun onFailed(errorCode: Int) {
        view?.findViewById<TextView>(R.id.progressBar)?.text =
            getString(R.string.installing_module_failed)
    }

    override fun onCancelled() {
        view?.findViewById<TextView>(R.id.progressBar)?.text =
            getString(R.string.installing_module_cancelled)
    }
}
