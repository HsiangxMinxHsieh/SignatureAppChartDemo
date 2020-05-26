package com.timmymike.notcontinuelinechart

import com.github.mikephil.charting.data.Entry

class VisibleEntry ( x:Float, y:Float,val isVisible:Boolean=true): Entry(x,y){
    override fun toString(): String {
        return "VisibleEntry(x=${x},y=${y},isVisible=$isVisible)"
    }
}