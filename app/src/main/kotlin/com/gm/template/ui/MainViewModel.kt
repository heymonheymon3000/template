package com.gm.template.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(): ViewModel() {
    var mIsBound = false
    var mArguments: HashMap<String, Any> = HashMap()
    var actionName: String = ""


}