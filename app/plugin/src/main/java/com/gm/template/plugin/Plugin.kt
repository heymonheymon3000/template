package com.gm.template.plugin

import android.content.pm.ResolveInfo
import android.os.Bundle

class Plugin(resolveInfo: ResolveInfo) {
    val serviceName: String
    val servicePackageName: String
    val pluginTitle: String
    private val metaData: Bundle

    init {
        serviceName = resolveInfo.serviceInfo.name
        servicePackageName = resolveInfo.serviceInfo.packageName
        metaData = resolveInfo.serviceInfo.metaData
        pluginTitle = metaData.getString("com.gm.template.META_DATA_PLUGIN_NAME", "")
    }
}