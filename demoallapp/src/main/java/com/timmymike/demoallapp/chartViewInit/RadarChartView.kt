package com.timmymike.demoallapp.chartViewInit

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet
import com.timmymike.demoallapp.R
import java.text.DecimalFormat
import java.util.ArrayList

class RadarChartView (private val context: Context){

    private val activity = this
//    private val context: Context = this
     var hLineNum = 7
     var dataSize = 9

//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        mBinding = DataBindingUtil.setContentView(activity, R.layout.activity_main)
////        setContentView(R.layout.activity_main)
//
//
//        initRadarChatView(mBinding.chartRadarChartExample)
//
//        initEvent()
//
//
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun initEvent() {
//        mBinding.ivRefresh.setOnClickListener {
//            initRadarChatView(mBinding.chartRadarChartExample)
//        }
//
//        mBinding.ivSetting.setOnClickListener {
//            SettingDialog(context).apply {
//                title = "圖表設定"
//                message = "標籤筆數(角數量)：$dataSize"
//                binding.seekBar.max = 10
//                binding.seekBar.min = 3
//                binding.seekBar.progress = dataSize
//                binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//                    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
//                        message = "標籤筆數(角數量)：$p1"
//                        binding.tvMessage.text = message
//                        dataSize = p1
////                        if (zeroNum > dataSize) {// 為空值數量不可超過資料筆數，若超過，於此處理
////                            binding.seekBar3.progress = dataSize
////                            zeroNum = dataSize
////                        }
//                    }
//
//                    override fun onStartTrackingTouch(p0: SeekBar?) {
//                    }
//
//                    override fun onStopTrackingTouch(p0: SeekBar?) {
//                    }
//                })
//                message2 = "橫線格數：${hLineNum} 條"
//                binding.seekBar2.max = 25
//                binding.seekBar2.min = 2
//                binding.seekBar2.progress = hLineNum
//                binding.seekBar2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//                    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
//                        message2 = "橫線格數：${p1} 條"
//                        binding.tvMessage2.text = message2
//                        hLineNum = p1
//                    }
//
//                    override fun onStartTrackingTouch(p0: SeekBar?) {
//                    }
//
//                    override fun onStopTrackingTouch(p0: SeekBar?) {
//                    }
//                })
////                message3 = "缺席天數：$zeroNum"
////                binding.seekBar3.max = 7
////                binding.seekBar3.min = 0
////                binding.seekBar3.progress = zeroNum
////                binding.seekBar3.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
////                    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
////                        message3 = "缺席天數：$p1"
////                        binding.tvMessage3.text = message3
////                        zeroNum = p1
////                    }
////
////                    override fun onStartTrackingTouch(p0: SeekBar?) {
////                    }
////
////                    override fun onStopTrackingTouch(p0: SeekBar?) {
////                    }
////                })
//
//
//                binding.btnRight.setOnClickListener {
//                    initRadarChatView(mBinding.chartRadarChartExample) // 不知道為什麼，橫線要設定兩次才有用。
//                    initRadarChatView(mBinding.chartRadarChartExample)
//                    dialog.dismiss()
//                }
//
//                binding.btnLift.visibility = View.GONE
//
//            }.show()
//        }
//
//    }


    fun initRadarChatView(chart: RadarChart,datas: ArrayList<ArrayList<RadarEntry>>) {
        // 如果沒有數據的時候，會顯示這個，類似ListView的EmptyView

        chart.apply {
            setBackgroundColor(context.getColor(R.color.theme_green_background))  //設置圖表背景

            description = Description().apply { text = "5678" }       //設置圖表右下角的敘述(文字)
            description.isEnabled = false                              //設置是否顯示圖表右下角的敘述(true=>顯示；false=>不顯示)(此行不可與上方設定對調，若對調則為一律顯示)

            webLineWidth = 2f                                         // 設置背後蜘蛛網的直線寬度
            webColor = context.getColor(R.color.blue)                 // 設置背後蜘蛛網的直線顏色
            webLineWidthInner = 0.5f                                  // 設置背後蜘蛛網的橫線寬度
            webColorInner = context.getColor(R.color.txt_dark_red)    // 設置背後蜘蛛網的橫線顏色
            webAlpha = 100                                            // 設置背後蜘蛛網的透明度(0~255)
            rotationAngle = 270f                                       // 設置第一個標籤的旋轉角度(預設為270f，靠上)
            isRotationEnabled = false                                 // 設置可否手動旋轉
            setExtraOffsets(0f, 40f, 0f, 0f)        //設置pieChart圖表上下左右的偏移，類似於內邊距
            // create a custom MarkerView (extend MarkerView) and specify the layout
            // to use for it

//        // create a custom MarkerView (extend MarkerView) and specify the layout
//        // to use for it
//        val mv: MarkerView = RadarMarkerView(this, R.layout.radar_markerview)
//        mv.chartView = chart // For bounds control
//
//        marker = mv // Set the marker to the chart

            val useTextSize = 10f
            chart.data = setRadarChartData(chart, datas)  //設定圖表資料

            animateXY(2000, 2000, Easing.EaseOutCirc) //設定圖表出現動畫(X軸動畫值，Y軸動畫值，動畫形式)


            //X軸(直線)設定
            chart.xAxis.apply {

//        typeface = tfLight
                textSize = useTextSize                                  // 設置X軸字體大小
                yOffset = 0f                                            // 設置X軸的Y方向偏移量(實測後設定無效果)
                xOffset = 0f                                            // 設置X軸的X方向偏移量(實測後設定無效果)
                valueFormatter = object : ValueFormatter() {            // 設置X軸的值
                    private val labels = // 標籤列表
                        arrayOf("Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H", "Party I", "Party J")

                    override fun getFormattedValue(value: Float): String {
                        return labels[value.toInt() % labels.size]
                    }
                }
                textColor = context.getColor(R.color.yellow)
                setLabelCount(3, true)
                labelCount = 3
            }
            //Y軸(橫線)設定
            chart.yAxis.apply {
//        typeface = tfLight
                setLabelCount(hLineNum, true)                      // 設置橫線數量(實際上設置完成後是幾格，因為0也算一條線)
                textSize = useTextSize                                    // 設置Y軸字體大小
                axisMinimum = 0f                                          // 設置Y軸最小值(如果值小於最小值，將會顯示在另一側)
                axisMaximum = 200f                                        // 設置Y軸最大值(如果值超過最大值，將會超出圖表)
                setDrawLabels(true)
                valueFormatter = object : ValueFormatter(){

                    override fun getFormattedValue(value: Float): String {
                        return "${DecimalFormat("#").format(super.getFormattedValue(value).toFloat())} $"
                    }

                }

            }

            chart.legend.apply {                                          // 設置圖例
                verticalAlignment = Legend.LegendVerticalAlignment.TOP    // 設置圖例垂直位置(靠上or靠下)
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER// 設置圖例水平位置(靠左or居中or靠右)
                orientation = Legend.LegendOrientation.HORIZONTAL           // 設置圖例水平顯示或垂直顯示
                setDrawInside(true)                                         // 是否畫在內側(若是，將和radarChart共用一空間，會顯得較擠)
//        typeface = tfLight
                xEntrySpace = 20f                                            // 設置圖例水平(X方向)空間
                yEntrySpace = 20f                                            // 設置圖例垂直(Y方向)空間
                textColor = context.getColor(R.color.line_chart_background_end) // 設置圖例文字顏色
            }
        }
    }

     fun getRadarExampleData(dataSize: Int):ArrayList<ArrayList<RadarEntry>>{
        val mul = 160f
        val min = 40f
        val cnt = dataSize // 資料筆數

        val entries1 = ArrayList<RadarEntry>()
        val entries2 = ArrayList<RadarEntry>()
        val entries3 = ArrayList<RadarEntry>()

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.

        for (i in 0 until cnt) {
            val val1 = (Math.random() * mul).toFloat() + min
            entries1.add(RadarEntry(val1))
            val val2 = (Math.random() * mul).toFloat() + min
            entries2.add(RadarEntry(val2))
            val val3 = (Math.random() * mul).toFloat() + min
            entries3.add(RadarEntry(val3))
        }

         val result = ArrayList<ArrayList<RadarEntry>>()
         result.add(entries1)
         result.add(entries2)
         result.add(entries3)

        return  result
    }

    private fun setRadarChartData(chart: RadarChart, datas:ArrayList<ArrayList<RadarEntry>>): RadarData {


        val set1Color = context.getColor(R.color.green)
        val set2Color = context.getColor(R.color.bar_chart_down)
        val set3Color = context.getColor(R.color.txt_lawn_blue)
        val textColor = context.getColor(R.color.txt_red)
        val textSize = 8f
        val set1 = RadarDataSet(datas[0], "Employer A").apply {
            color = set1Color                       // 設置線顏色
            fillColor = set1Color                   // 設置中心填滿顏色
            setDrawFilled(true)                     // 設置中心是否要填滿
            fillAlpha = 80                          // 設置資料透明度
            lineWidth = 3f                          // 設置線寬
            isDrawHighlightCircleEnabled = false    // 設置是否顯示點擊小圓圈
            setDrawHighlightIndicators(false)       // 設置是否顯示點擊水平線與垂直線
            setDrawValues(true)                     // 設置是否顯示Y值(預設為true(是))
            valueTextColor = textColor              // 設置Y值顏色(預設黑色)
            valueTextSize = textSize                // 設置Y值字型大小(預設8f)
        }

        val set2 = RadarDataSet(datas[1], "Employer B").apply {
            color = set2Color                       // 設置線顏色
            fillColor = set2Color                   // 設置中心填滿顏色
            setDrawFilled(true)                     // 設置中心是否要填滿
            fillAlpha = 80                          // 設置資料透明度
            lineWidth = 2f                          // 設置線寬
            isDrawHighlightCircleEnabled = true     // 設置是否顯示點擊小圓圈
            setDrawHighlightIndicators(false)       // 設置是否顯示點擊水平線與垂直線
            setDrawValues(true)                     // 設置是否顯示Y值(預設為true(是))
//            valueTextColor = textColor            // 設置Y值顏色(預設黑色)
            valueTextSize = textSize                // 設置Y值字型大小(預設8f)

        }

        val set3 = RadarDataSet(datas[2], "Employer C").apply {
            color = set3Color                      // 設置線顏色
            fillColor = set3Color                  // 設置中心填滿顏色
            setDrawFilled(true)                    // 設置中心是否要填滿
            fillAlpha = 80                         // 設置資料透明度
            lineWidth = 2f                         // 設置線寬
            isDrawHighlightCircleEnabled = true    // 設置是否顯示點擊小圓圈
            setDrawHighlightIndicators(false)      // 設置是否顯示點擊水平線與垂直線
            setDrawValues(false)                   // 設置是否顯示Y值(預設為true(是))
//            valueTextColor = textColor           // 設置Y值顏色(預設黑色)
            valueTextSize = textSize               // 設置Y值字型大小(預設8f)
        }

        val sets = ArrayList<IRadarDataSet>()
        sets.add(set1)
        sets.add(set2)
        sets.add(set3)

        val data = RadarData(sets)
//        data.setValueTypeface(tfLight)
//        data.setValueTextSize(8f)
//        data.setDrawValues(true)
//        data.setValueTextColor(textColor)
        return data
//        chart.data = data
//        chart.invalidate()


    }


}