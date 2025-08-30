package com.alirezaiyan.kalendar.widget.actions

import androidx.glance.action.ActionParameters

/**
 * Action parameters for widget interactions.
 */
object Params {
    val Direction = ActionParameters.Key<Int>("dir")
    val EpochDay = ActionParameters.Key<Long>("epochDay")
}
