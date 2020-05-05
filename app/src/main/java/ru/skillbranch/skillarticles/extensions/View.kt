package ru.skillbranch.skillarticles.extensions

import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import androidx.navigation.NavDestination
import com.google.android.material.bottomnavigation.BottomNavigationView

fun View.setMarginOptionally(
    left: Int = this.marginStart,
    top: Int = this.marginTop,
    right: Int = this.marginEnd,
    bottom: Int = this.marginBottom
) {
    val param = this.layoutParams as ViewGroup.MarginLayoutParams
    param.setMargins(left, top, right, bottom)
    this.layoutParams = param
}

fun View.selectDestination(destination: NavDestination) {
    val bnv = this as? BottomNavigationView
    bnv ?: return
    if (bnv.selectedItemId != destination.id) {
        val mi = bnv.menu.findItem(destination.id)
        mi?.setChecked(true)
    }
}