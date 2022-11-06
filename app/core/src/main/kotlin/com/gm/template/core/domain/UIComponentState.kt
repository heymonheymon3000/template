package com.gm.template.core.domain

/**
 * A generic class for hiding/showing some ui component.
 */
sealed class UIComponentState {

    object Show: UIComponentState()

    object Hide: UIComponentState()
}