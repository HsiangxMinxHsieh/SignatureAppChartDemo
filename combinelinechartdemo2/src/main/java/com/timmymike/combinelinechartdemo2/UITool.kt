package com.timmymike.combinelinechartdemo2

import android.R
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.os.Handler
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout


//fun addColon(str: String): String{
//    var result = StringBuffer()
//    for (i:Int in 0 until str.length) {
//        result.append(str[i])
//        if (i % 2 == 1 && i < str.length - 1){
//            result.append(":")
//        }
//    }
//    return result.toString()
//}
//
/**自動根據內容縮小TextSize的大小
 * @author 網路
 * @date 2019/11/29
 * */
fun TextView.setTextSizeScale(){
    val testPaint = this.paint
    val text = this.text.toString()
    val textWidth = this.width
    if (textWidth > 0) {
        val availableWidth = textWidth - this.paddingLeft -this.paddingRight
        var trySize = this.textSize
        testPaint.textSize = trySize
        while (testPaint.measureText(text) > availableWidth) {
            trySize -= 2f
            this.setTextSize(TypedValue.COMPLEX_UNIT_PX, trySize) //这里必须使用px，因为testPaint.measureText(text)和availableWidth的单位都是px
        }
        this.textSize = trySize
    }

}
//
///**
// * 設定 RecycleView 的捲動位置(由於內建方法一定要在Handler()內才能有效執行，因此新增此擴展方法)
// * @param position 欲捲動的位置
// * @author 蝦米
// * @date 2019/11/12
// */
//fun RecyclerView.handlerToScrollToPostion(position: Int, offsetItem: Int = 0) {
//    var finalPostion = position + offsetItem
//    val maxIndex = (this.adapter?.itemCount ?: 0)-1
//    logi("RecyclerView.handlerToScrollToPostion","載入時變更與取到的的maxIndex是===>$maxIndex")
//    if (finalPostion >= maxIndex) //滾動前先判斷是否大於最大值
//        finalPostion = maxIndex
//    else if(position == 0)
//        finalPostion = 0
//    Handler().postDelayed({
//        this.scrollToPosition(finalPostion)
//    }, 100)
//}
//
//
///**
// * 設定 壓下的按鈕切換效果(為TRAQ專門製作，產生「背景顏色變換」且「字變色」的效果)
// * @param pressTextColor 按下的文字顏色 (資源檔路徑)
// * @param unPressTextColor 未按下的文字顏色 (資源檔路徑)
// * @param unPressBackgroundColor 未按下的背景顏色 (資源檔路徑)
// * @param backgroundBorderColor 邊框顏色與按下後改變的背景顏色 (資源檔路徑)
// * @author 蝦米
// * @date 2019/11/07
// */
//@SuppressLint("ClickableViewAccessibility")
//fun Button.setDefaultBackgroundAndTouchListener(pressTextColor: Int, unPressTextColor: Int, unPressBackgroundColor: Int, backgroundBorderColor: Int) {
//    val corner = 1000       //圓角弧度
//    //按鈕設定參數：
//    val btnTextSize = context.resources.getDimensionPixelSize(com.smartq.smartcube.R.dimen.btn_text_size) //按鍵文字大小
//    val strokeWidth = context.resources.getDimensionPixelSize(com.smartq.smartcube.R.dimen.btn_stroke_width) //按鈕邊界寬度
//    setPressedBackground(
//        BackgroundDrawable.getRectangleBg(context, corner, corner, corner, corner, unPressBackgroundColor, backgroundBorderColor, strokeWidth),
//        BackgroundDrawable.getRectangleBg(context, corner, corner, corner, corner, backgroundBorderColor, backgroundBorderColor, strokeWidth)
//    )
//    setPressedTextColor(unPressTextColor, pressTextColor)
//}

fun removeColon(str: String): String {
    return str.replace(":", "")
}

/**
 * 設定 view的長寬 單位為畫素(pixel)
 * @param view
 * @param w
 * @param h
 * @author Wang / Robert
 * @date 2015/5/8 下午3:13:42
 * @version
 */
fun View.setViewSize(w: Int, h: Int) {
    try {
        this.layoutParams.width = w
        this.layoutParams.height = h
    } catch (e: Exception) {
        //如果prams不存在 則重新建立
        val params = ViewGroup.LayoutParams(w, h)
        params.width = w
        params.height = h
        this.layoutParams = params
    }
}

fun NumberPicker.setDividerColor(color: Int) {
    val dividerField = NumberPicker::class.java.declaredFields.firstOrNull { it.name == "mSelectionDivider" } ?: return
    try {
        dividerField.isAccessible = true
        dividerField.set(this, resources.getDrawable(color))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun NumberPicker.setTextColor(color: Int) {
    try {
        val selectorWheelPaintField = NumberPicker::class.java.getDeclaredField("mSelectorWheelPaint")
        selectorWheelPaintField.setAccessible(true)
        (selectorWheelPaintField.get(this) as Paint).color = resources.getColor(color)
    } catch (e: NoSuchFieldException) {

    } catch (e: IllegalAccessException) {

    } catch (e: IllegalArgumentException) {

    }

    val count = childCount
    for (i in 0 until count) {
        val child = getChildAt(i)
        if (child is EditText)
            child.setTextColor(resources.getColor(color))
    }
    invalidate()
}

fun NumberPicker.setTextSize(size: Int) {
    try {
        val selectorWheelPaintField = NumberPicker::class.java.getDeclaredField("mSelectorWheelPaint")
        selectorWheelPaintField.setAccessible(true)
        (selectorWheelPaintField.get(this) as Paint).textSize = UserInterfaceTool.getTextSize(context, size).toFloat()
    } catch (e: NoSuchFieldException) {

    } catch (e: IllegalAccessException) {

    } catch (e: IllegalArgumentException) {

    }

    val count = childCount
    for (i in 0 until count) {
        val child = getChildAt(i)
        if (child is EditText)
            child.setTextSize(size)
    }
    invalidate()
}

/**
 * 設定 view的長寬 單位為dp
 * @param view
 * @param w
 * @param h
 * @author Wang / Robert
 * @date 2015/5/8 下午3:13:42
 * @version
 */
fun View.setViewSizeByDpUnit(view: View, w: Int, h: Int) {
    setViewSize(getPixelFromDpByDevice(view.context, w), getPixelFromDpByDevice(view.context, h))
}

/**
 * 設定 view的長寬 單位為畫素(pixel) 自動高度
 * @param view
 * @param w
 * @param rid
 * @author Wang / Robert
 * @date 2015/5/8 下午3:13:42
 * @version
 */
fun View.setViewSizeByResWidth(w: Int, rid: Int) {
    val h = ViewTool.getImageHeight(this.context, rid, w)
    try {
        this.layoutParams.width = w
        this.layoutParams.height = h
    } catch (e: Exception) {
        //如果prams不存在 則重新建立
        val params = ViewGroup.LayoutParams(w, h)
        params.width = w
        params.height = h
        this.layoutParams = params
    }
}

/**
 * 設定 view的長寬 單位為畫素(pixel) 自動寬度
 * @param view
 * @param h
 * @param rid
 * @author Wang / Robert
 * @date 2015/5/8 下午3:13:42
 * @version
 */
fun View.setViewSizeByResHeight(h: Int, rid: Int) {
    val w = ViewTool.getImageWidth(this.context, rid, h)
    try {
        this.layoutParams.width = w
        this.layoutParams.height = h
    } catch (e: Exception) {
        //如果prams不存在 則重新建立
        val params = ViewGroup.LayoutParams(w, h)
        params.width = w
        params.height = h
        this.layoutParams = params
    }
}

fun View.setTextSize(sp: Int) {
    val displayMetrics = this.context.resources.displayMetrics
    val realSpSize = ((sp * displayMetrics.widthPixels).toFloat() / displayMetrics.density / 360f).toInt()
    if (this is TextView) {
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, realSpSize.toFloat())
    } else if (this is Button) {
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, realSpSize.toFloat())
    } else if (this is EditText) {
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, realSpSize.toFloat())
    } else {
        (this as TextView).setTextSize(TypedValue.COMPLEX_UNIT_SP, realSpSize.toFloat())
    }
}

/**
 *
 * 輸入DP單位數值 根據裝置動態 回傳像素:
 * @author Robert Chou didi31139@gmail.com
 * @param dpSize 整數 單位為dp
 * @date 2015/6/17 下午5:25:39
 * @return dp根據裝置動態計算 回傳pixel
 * @version
 */
fun getPixelFromDpByDevice(context: Context, dpSize: Int): Int {
    val displayMetrics = context.resources.displayMetrics
    val realSpSize = ((dpSize * displayMetrics.widthPixels).toFloat() / displayMetrics.density / 360f).toInt()
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, realSpSize.toFloat(), context.resources.displayMetrics).toInt()
}

/**
 *
 * 設定物件間距  單位為畫素(pixel)
 * 上層類別須為 RelativeLayout or LinearLayout
 * @author Wang / Robert Chou didi31139@gmail.com
 * @date 2015/5/26 下午3:25:33
 * @version
 */
fun View.setMarginByDpUnit(leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int) {
    val params = this.layoutParams
    if (params is LinearLayout.LayoutParams) {
        params.setMargins(
            getPixelFromDpByDevice(this.context, leftMargin),
            getPixelFromDpByDevice(this.context, topMargin),
            getPixelFromDpByDevice(this.context, rightMargin),
            getPixelFromDpByDevice(this.context, bottomMargin)
        )
    } else if (params is RelativeLayout.LayoutParams) {
        params.setMargins(
            getPixelFromDpByDevice(this.context, leftMargin),
            getPixelFromDpByDevice(this.context, topMargin),
            getPixelFromDpByDevice(this.context, rightMargin),
            getPixelFromDpByDevice(this.context, bottomMargin)
        )
    } else if (params is ConstraintLayout.LayoutParams) {
        params.setMargins(
            getPixelFromDpByDevice(this.context, leftMargin),
            getPixelFromDpByDevice(this.context, topMargin),
            getPixelFromDpByDevice(this.context, rightMargin),
            getPixelFromDpByDevice(this.context, bottomMargin)
        )
    }
    this.layoutParams = params
}

fun View.setPaddingByDpUnit(leftPadding: Int, topPadding: Int, rightPadding: Int, bottomPadding: Int) {
    this.setPadding(
        getPixelFromDpByDevice(this.context, leftPadding),
        getPixelFromDpByDevice(this.context, topPadding),
        getPixelFromDpByDevice(this.context, rightPadding),
        getPixelFromDpByDevice(this.context, bottomPadding)
    )
}

/**
 * 設定 壓下的圖片切換效果
 * @param unPressedDrawable 未按下的圖片 R.drawable.image
 * @param pressedDrawable 未按下的圖片 R.drawable.pressedimage
 */
fun View.setPressedImage(unPressedDrawable: Drawable, pressedDrawable: Drawable?) {
    if (pressedDrawable == null) {
        this.background = unPressedDrawable
        return
    }
    val states = StateListDrawable()
    states.addState(intArrayOf(android.R.attr.state_pressed), pressedDrawable)
    states.addState(intArrayOf(android.R.attr.state_focused), pressedDrawable)
    states.addState(intArrayOf(android.R.attr.state_checked), pressedDrawable)
    states.addState(intArrayOf(), unPressedDrawable)
    if (this is Button) {
        this.background = states
    } else {
        (this as ImageView).setImageDrawable(states)
    }
}

/**
 * 設定 壓下的圖片切換效果
 * @param unPressedDrawableID 未按下的圖片 R.drawable.image
 * @param pressedDrawableID 未按下的圖片 R.drawable.pressedimage
 */
fun View.setPressedImage(unPressedDrawableID: Int, pressedDrawableID: Int) {
    setPressedImage(this.context.resources.getDrawable(unPressedDrawableID), this.context.resources.getDrawable(pressedDrawableID))
}

/**
 * 設定 壓下的圖片切換效果
 * @param unPressedDrawable 未按下的圖片 R.drawable.image
 * @param pressedDrawable 未按下的圖片 R.drawable.pressedimage
 */
fun View.setPressedBackground(unPressedDrawable: Drawable, pressedDrawable: Drawable?) {
    if (pressedDrawable == null) {
        this.background = unPressedDrawable
        return
    }
    val states = StateListDrawable()
    states.addState(intArrayOf(android.R.attr.state_pressed), pressedDrawable)
    states.addState(intArrayOf(android.R.attr.state_focused), pressedDrawable)
    states.addState(intArrayOf(android.R.attr.state_checked), pressedDrawable)
    states.addState(intArrayOf(), unPressedDrawable)
    this.background = states
}

/**
 * 設定 壓下的圖片切換效果
 * @param unPressedDrawable 未按下的圖片 R.drawable.image
 * @param pressedDrawable 未按下的圖片 R.drawable.pressedimage
 */
fun View.setPressedBackground(unPressedDrawable: Int, pressedDrawable: Int) {
    setPressedBackground(this.context.resources.getDrawable(unPressedDrawable), this.context.resources.getDrawable(pressedDrawable))
}

/**
 * 設定按鈕 被按住的顏色背景
 * @param unPressedColor 未按下的顏色背景 R.color.color1
 * @param pressedColor 按下的顏色 R.color.color 0為不給
 */
fun View.setPressedBackgroundColor(unPressedColor: Int, pressedColor: Int) {
    val context = this.context
    if (pressedColor == 0) {
        this.setBackgroundResource(unPressedColor)
        return
    }
    val unPressedcolorDrawable = ColorDrawable(context.resources.getColor(unPressedColor))
    val pressedcolorDrawable = ColorDrawable(context.resources.getColor(pressedColor))
    val states = StateListDrawable()
    states.addState(intArrayOf(android.R.attr.state_pressed), pressedcolorDrawable)
    states.addState(intArrayOf(android.R.attr.state_focused), pressedcolorDrawable)
    states.addState(intArrayOf(android.R.attr.state_checked), pressedcolorDrawable)
    states.addState(intArrayOf(), unPressedcolorDrawable)
    this.background = states
}


/**
 * 設定按鈕 被按住的顏色背景
 * @param unPressedColor 未按下的顏色背景 R.color.color1
 * @param pressedColor 按下的顏色 R.color.color 0為不給
 */
fun View.setPressedTextColor(unPressedColor: Int, pressedColor: Int) {
    val context = this.context
    if (pressedColor == 0) {
        (this as TextView).setTextColor(context.resources.getColor(unPressedColor))
        return
    }
    val colorStateList = ColorStateList(
        arrayOf(
            intArrayOf(R.attr.state_pressed),
            intArrayOf(R.attr.state_focused),
            intArrayOf(R.attr.state_checked),
            intArrayOf()
        ),
        intArrayOf(
            context.resources.getColor(pressedColor),
            context.resources.getColor(pressedColor),
            context.resources.getColor(pressedColor),
            context.resources.getColor(unPressedColor)
        )
    )
    (this as TextView).setTextColor(colorStateList)
}

/**
 * check box 狀態設定
 * @param basedrawable 未按下的圖片 R.drawable.image
 * @param checkeddrawable 未按下的圖片 R.drawable.pressedimage 0為不給
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
fun View.setCheckDrawable(basedrawable: Int, checkeddrawable: Int) {
    if (checkeddrawable == 0) {
        this.setBackgroundResource(basedrawable)
        return
    }
    val states = StateListDrawable()
    states.addState(intArrayOf(android.R.attr.state_checkable), context.resources.getDrawable(basedrawable))
    states.addState(intArrayOf(android.R.attr.state_checked), context.resources.getDrawable(checkeddrawable))
    states.addState(intArrayOf(), context.resources.getDrawable(basedrawable))

    if (this is CheckBox) {
        this.buttonDrawable = states
    } else if (Build.VERSION.SDK_INT >= 16) {
        this.background = states
    } else {
        this.setBackgroundDrawable(states)
    }
}

/**
 * 設定Tab按鈕 被按住的顏色背景
 */
fun View.setTabPressedImage(unPressedDrawable: Drawable, pressedDrawable: Drawable?) {
    if (pressedDrawable == null) {
        this.background = unPressedDrawable
        return
    }
    val states = StateListDrawable()
    states.addState(intArrayOf(android.R.attr.state_pressed), unPressedDrawable)
    states.addState(intArrayOf(android.R.attr.state_focused), unPressedDrawable)
    states.addState(intArrayOf(android.R.attr.state_checked), unPressedDrawable)
    states.addState(intArrayOf(android.R.attr.state_selected), pressedDrawable)
    states.addState(intArrayOf(), unPressedDrawable)
    if (this is Button) {
        this.background = states
    } else {
        (this as ImageView).setImageDrawable(states)
    }
}

/**
 * 設定Tab按鈕 被按住的顏色背景
 * @param unPressedDrawableID 未按下的顏色背景 R.color.color1
 * @param pressedDrawableID 按下的顏色 R.color.color 0為不給
 */
fun View.setTabPressedImage(unPressedDrawableID: Int, pressedDrawableID: Int) {
    var unPressedDrawable: Drawable? = null
    var pressedDrawable: Drawable? = null
    if (unPressedDrawableID == 0) {
        unPressedDrawable = ColorDrawable(this.resources.getColor(android.R.color.transparent))
    } else {
        unPressedDrawable = this.resources.getDrawable(unPressedDrawableID)
    }
    if (pressedDrawableID == 0) {
        pressedDrawable = ColorDrawable(this.resources.getColor(android.R.color.transparent))
    } else {
        pressedDrawable = this.resources.getDrawable(pressedDrawableID)
    }
    if (pressedDrawable == null) {
        this.background = unPressedDrawable
        return
    }
    val states = StateListDrawable()
    states.addState(intArrayOf(android.R.attr.state_pressed), unPressedDrawable)
    states.addState(intArrayOf(android.R.attr.state_focused), unPressedDrawable)
    states.addState(intArrayOf(android.R.attr.state_checked), unPressedDrawable)
    states.addState(intArrayOf(android.R.attr.state_selected), pressedDrawable)
    states.addState(intArrayOf(), unPressedDrawable)
    if (this is Button) {
        this.background = states
    } else {
        (this as ImageView).setImageDrawable(states)
    }

}

/**
 * 設定Tab按鈕 被按住的顏色背景
 * @param unPressedDrawableID 未按下的顏色背景 R.color.color1
 * @param pressedDrawableID 按下的顏色 R.color.color 0為不給
 */
fun View.setTabPressedBackgroundColor(unPressedDrawableID: Int, pressedDrawableID: Int) {
    var unPressedDrawable: Drawable? = null
    var pressedDrawable: Drawable? = null
    if (unPressedDrawableID == 0) {
        unPressedDrawable = ColorDrawable(this.resources.getColor(android.R.color.transparent))
    } else {
        unPressedDrawable = this.resources.getDrawable(unPressedDrawableID)
    }
    if (pressedDrawableID == 0) {
        pressedDrawable = ColorDrawable(this.resources.getColor(android.R.color.transparent))
    } else {
        pressedDrawable = this.resources.getDrawable(pressedDrawableID)
    }
    if (pressedDrawable == null) {
        this.background = unPressedDrawable
        return
    }
    val states = StateListDrawable()
    states.addState(intArrayOf(android.R.attr.state_pressed), unPressedDrawable)
    states.addState(intArrayOf(android.R.attr.state_focused), unPressedDrawable)
    states.addState(intArrayOf(android.R.attr.state_checked), unPressedDrawable)
    states.addState(intArrayOf(android.R.attr.state_selected), pressedDrawable)
    states.addState(intArrayOf(), unPressedDrawable)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        //設定水波文
        this.setRippleBackground(R.color.holo_blue_bright, states)
    } else {
        this.background = states
    }
}

/**
 * 設定Tab按鈕 被按住的顏色背景
 * @param unPressedColor 未按下的顏色背景 R.color.color1
 * @param pressedColor 按下的顏色 R.color.color 0為不給
 */
fun View.setTabPressedTextColor(unPressedColor: Int, pressedColor: Int) {
    val context = this.context
    if (pressedColor == 0) {
        (this as TextView).setTextColor(context.resources.getColor(unPressedColor))
        return
    }
    val colorStateList = ColorStateList(
        arrayOf(
            intArrayOf(android.R.attr.state_pressed),
            intArrayOf(android.R.attr.state_focused),
            intArrayOf(android.R.attr.state_checked),
            intArrayOf(android.R.attr.state_selected),
            intArrayOf()
        ),
        intArrayOf(
            context.resources.getColor(unPressedColor),
            context.resources.getColor(unPressedColor),
            context.resources.getColor(unPressedColor),
            context.resources.getColor(pressedColor),
            context.resources.getColor(unPressedColor)
        )
    )
    (this as TextView).setTextColor(colorStateList)
}

/**設定水波文*/
fun View.setRippleBackground(color: Int, states: Drawable?) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            val rippleDrawable = RippleDrawable(ColorStateList.valueOf(Color.BLACK) , states , null)
        val attrs = intArrayOf(android.R.attr.selectableItemBackground)
        val typedArray = this.context.obtainStyledAttributes(attrs)
        val rippleDrawable = typedArray.getDrawable(0) as RippleDrawable
        typedArray.recycle()
        rippleDrawable.setColor(ColorStateList.valueOf(this.resources.getColor(color)))
        if (states != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                rippleDrawable.addLayer(states)
            }
        }
        this.background = rippleDrawable
    }
}