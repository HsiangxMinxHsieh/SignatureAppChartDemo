package com.timmymike.radarcharttrial

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.afollestad.materialdialogs.MaterialDialog
import com.timmymike.radarcharttrial.databinding.DialogSettingBinding
import com.timmymike.radarcharttrial.dialog_base.BackgroundDrawable
import com.timmymike.radarcharttrial.dialog_base.setPressedBackground
import com.timmymike.radarcharttrial.dialog_base.setTextSize


class SettingDialog(val context: Context) {
    var title = ""
    var message = ""
    var message2 = ""
    var message3 = ""
    val dialog by lazy { MaterialDialog(context) }
    val binding by lazy { DataBindingUtil.inflate<DialogSettingBinding>(LayoutInflater.from(context), R.layout.dialog_setting, null, false) }
    fun show() {
        if (dialog.isShowing) {
            return
        }
        //
        binding.apply {
            val backgroundCorner = 25
            root.background = BackgroundDrawable.getRectangleBg(context, backgroundCorner, backgroundCorner, backgroundCorner, backgroundCorner, R.color.dialog_bg, 0, 0)
            tvTitle.text = title
            tvTitle.setTextSize(20)
            if (title == "") {
                tvTitle.visibility = View.GONE
            }
            if(message ==""){
                tvMessage.visibility = View.GONE
                seekBar.visibility = View.GONE
            }else {
                tvMessage.text = message
            }
            if(message2 ==""){
                tvMessage2.visibility = View.GONE
                seekBar2.visibility = View.GONE
            }else {
                tvMessage2.text = message2
            }
            if(message3 ==""){
                tvMessage3.visibility = View.GONE
                seekBar3.visibility = View.GONE
            }else {
                tvMessage3.text = message3
            }


            tvMessage.setTextSize(16)
            tvMessage2.setTextSize(16)
            tvMessage3.setTextSize(16)
//            tvMessage.movementMethod = ScrollingMovementMethod.getInstance()
//            if (message.length > 600) {
//                val layoutParams = scrollView.layoutParams ?: ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0)
//                layoutParams.height = (UserInterfaceTool.getScreenHeightPixels(context) * 0.4).toInt()
//                scrollView.layoutParams = layoutParams
//            }
            //按鈕設定參數：
            val corner = 100 //圓角弧度
            val btnTextSize = context.resources.getDimensionPixelSize(R.dimen.btn_text_size) //按鍵文字大小
            val strokeWidth = context.resources.getDimensionPixelSize(R.dimen.btn_stroke_width) //按鈕邊界寬度

            btnRight.setTextSize(btnTextSize)
//            btnRight.background = null
//            btnRight.setDefaultBackgroundAndTouchListener(R.color.white, R.color.theme_green, R.color.white, R.color.theme_green)
            btnRight.setPressedBackground(
                BackgroundDrawable.getRectangleBg(context, corner, corner, corner, corner, R.color.white, R.color.theme_gray, strokeWidth),
                BackgroundDrawable.getRectangleBg(context, corner, corner, corner, corner, R.color.theme_gray, R.color.theme_gray, strokeWidth)
            )
//            if (btnLift.visibility == View.VISIBLE) {
                btnLift.setTextSize(btnTextSize)
//            btnLift.background = null
                btnLift.setPressedBackground(
                    BackgroundDrawable.getRectangleBg(context, corner, corner, corner, corner, R.color.white, R.color.theme_gray, strokeWidth),
                    BackgroundDrawable.getRectangleBg(context, corner, corner, corner, corner, R.color.theme_gray, R.color.theme_gray, strokeWidth)
                )
//            }
        }
        //
        dialog.apply {

            setContentView(binding.root)
            setCancelable(false)
            window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            show()
        }
    }
}