package com.gm.template.components.util

import com.gm.template.core.domain.Queue
import com.gm.template.core.domain.UIComponent

class UIComponentQueueUtil {
    fun doesUIComponentAlreadyExistInQueue(queue: Queue<UIComponent>, uiComponent: UIComponent) : Boolean {
        for(item in queue.items){
            when(item) {
                is UIComponent.Dialog -> {
                    if (item.id ==  (uiComponent as UIComponent.Dialog).id){
                        return true
                    }
                }

                is UIComponent.SnackBar -> {
                    if (item.id ==  (uiComponent as UIComponent.SnackBar).id){
                        return true
                    }
                }

                else -> {
                    return false
                }
            }
        }
        return false
    }
}