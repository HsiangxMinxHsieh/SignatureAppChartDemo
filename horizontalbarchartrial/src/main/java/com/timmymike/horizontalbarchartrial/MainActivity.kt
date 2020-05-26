package com.timmymike.horizontalbarchartrial

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.format.DateUtils.LENGTH_SHORTER
import android.text.format.DateUtils.getMonthString
import android.view.View
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.timmymike.horizontalbarchartrial.databinding.ActivityMainBinding
import com.timmymike.radarcharttrial.dialog_base.logi
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {
    val TAG = javaClass.simpleName
    private val context: Context = this
    private val activity = this

    //    private var startTime = DateTool.nowDate
    private lateinit var mBinding: ActivityMainBinding

    private var dataSize = 7 //預設資料筆數

    private var horizontalLine = 5 // 預設橫軸座標數量

    private var reverse = 0

    private val timeFormate = "yyyy-MM-dd HH:mm:ss"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val example = getHorizontalBarExampleData(dataSize)
        val xLabels = getXlabels(dataSize,reverse)
        initHorizontalBarCharView(mBinding.chartHbExample, example, xLabels)

        initEvent()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initEvent() {
        mBinding.ivRefresh.setOnClickListener {
            val example = getHorizontalBarExampleData(dataSize)
            val xLabels = getXlabels(dataSize,reverse)
            initHorizontalBarCharView(mBinding.chartHbExample, example, xLabels)
        }

        mBinding.ivSetting.setOnClickListener {

            SettingDialog(context).apply {
                title = "圖表亂數設置"
                message = "資料筆數：$dataSize"

                binding.seekBar.max = 12
                binding.seekBar.min = 1
                binding.seekBar.progress = dataSize
                binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                        message = "資料筆數：$p1"
                        binding.tvMessage.text = message
                        dataSize = p1
//                        if (horizontalLine > dataSize) {// 為空值數量不可超過資料筆數，若超過，於此處理
//                            binding.seekBar3.progress = dataSize
//                            horizontalLine = dataSize
//                        }
                    }

                    override fun onStartTrackingTouch(p0: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(p0: SeekBar?) {
                    }
                })

                message2 = "橫軸(直線)數量：$horizontalLine"
                binding.seekBar2.max = 25
                binding.seekBar2.min = 2
                binding.seekBar2.progress = horizontalLine
                binding.seekBar2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                        message2 = "橫軸(直線)數量：$p1"
                        binding.tvMessage2.text = message2
                        horizontalLine = p1
                    }

                    override fun onStartTrackingTouch(p0: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(p0: SeekBar?) {
                    }
                })

                message3 = "標籤反向：${if(reverse==1) "是" else "否" } "
                binding.seekBar3.max = 1
                binding.seekBar3.min = 0
                binding.seekBar3.progress = reverse
                binding.seekBar3.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(p0: SeekBar?, p1: Int, p3: Boolean) {
                        message3 = "標籤反向：${if(p1==1) "是" else "否" } "
                        binding.tvMessage3.text = message3
                        reverse = p1
                    }

                    override fun onStartTrackingTouch(p0: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(p0: SeekBar?) {
                    }
                })
                binding.btnRight.setOnClickListener {

//                    example = getExampleData(dataSize, zeroNum)
//                    initLineCharView(mBinding.chartLineExample2, forChartData(example, randomRange), xLabels)
                    val example = getHorizontalBarExampleData(dataSize)
                    val xLabels = getXlabels(dataSize,reverse)
                    initHorizontalBarCharView(mBinding.chartHbExample, example, xLabels)
                    dialog.dismiss()
                }

                binding.btnLift.visibility = View.GONE

            }.show()
        }

    }

    /**依照dataSize取得xLabel的方法*/
    private fun getXlabels(dataSize: Int,reverse:Int): Array<String> {
        val result = Array<String>(dataSize) { "" }
        for (i in 0 until dataSize) {
            result[i] = getMonthString(i,LENGTH_SHORTER)
        }
//        logi(TAG,"回傳前的xLabels是===>$result")
        if(reverse == 1){
            result.reverse()
        }
        return result
    }

    /**依照dataSize取得隨機資料的方法*/
    private fun getHorizontalBarExampleData(dataSize: Int): ArrayList<Int> {
        val result = ArrayList<Int>()

        for (i in 0 until dataSize) {
            result.add((0..100).random())
        }
        return result
    }

    /**將資料轉為DataSet並放入Data內並回傳的方法*/
    private fun getData(/*lineChart: CombinedChart, */dataList: ArrayList<Int>): BarData {

        //製作Bar(長條圖資料)
        //分數資料塞入
        val workEntries = ArrayList<BarEntry>()
        //由於Entry只可以接受Float值，因此必須很疲勞的將傳入的Int轉為Float...@@a
        for (i in 0 until dataList.size) {
//            if (i == 0 || i == dataList.size - 1) // 第一個或最後一個時
//                workEntries.add(BarEntry(i.toFloat(), 0f))
//            else
                workEntries.add(BarEntry(i.toFloat(), dataList[i].toFloat(), context.resources.getDrawable(R.drawable.star)))
        }
        logi(TAG, "完成後的WorkTimeEntries 是===>$workEntries")
        val workSet = BarDataSet(workEntries, "WorkScore").apply {
            color = context.getColor(R.color.defaultBackgroundColor)
            setDrawValues(false)    //設置是否顯示頂端值
            setDrawIcons(true)    // 設置是否繪製Icon
            //設置漸層顏色 // 水平BarChart沒有辦法設置漸層效果 //只有3.1.0-alpha板後才有此功能。
//            val startColor1 = ContextCompat.getColor(context, R.color.bg_color_2)
//            val endColor1 = ContextCompat.getColor(context, R.color.bg_color_1)
//
//            setGradientColor(startColor1, endColor1)

            isHighlightEnabled = false // 點擊顯示變更顏色是否開啟
        }
        val barData = BarData().apply {
            barWidth = 0.8f
        }
        barData.addDataSet(workSet)
        return barData

//        val combinedData = CombinedData()
//        combinedData.setData(lineData)
//        combinedData.setData(barData)
//        return combinedData
    }

//   inner class MyXFormatter(private val mValues: List<String>) : ValueFormatter() {
//        override fun getFormattedValue(value: Float, axis: AxisBase): String {
//            logi(TAG,"現在要取的value是===>$value")
//            return if (value.toInt() >= 0 && (value.toInt()) < mValues.size) mValues[value.toInt()] else ""
//        }
//
//    }

    private fun initHorizontalBarCharView(chart: HorizontalBarChart, datas: ArrayList<Int>, xLabels: Array<String>) {
        logi(TAG, "傳入的XLabels是===>${xLabels.toList()}")
        logi(TAG,"傳入的datas是===>$datas")
        //  設置白色常數(因為版本高低[高於23&低於23]因此有兩種寫法)
        val useLineColor = context.getColor(R.color.txt_gray5)

//      //傳入資料值
//        val dayDatas = getDayData(datas)
        chart.setBackgroundColor(context.getColor(R.color.theme_green_background))
//        chart.data = getData(/*lineChart,*/ dayDatas)
        chart.data = getData(/*lineChart,*/ datas)


//      取得資料頂端線數值(*8/7為固定算法)
//        val max = (dayDatas.map { it.endWorkTime }.max()?.toFloat() ?: 0f) * 8 / 7 // 先試試看Long轉Float是否可行，如果不行再說。
//        logi(TAG, "最大值是===>$max")
//        logi(TAG, "最大值是===>${max.toLong()}")
//
//        //依照dayDatas內算出的每天工作時數作處理：加上換行符號與小時
//        val useXLables = ArrayList<String>()
//        useXLables.addAll(xLabels)
//        for (index in dayDatas.indices) {
//            if (useXLables[index] != "") {
//                val caculateTime = DecimalFormat("#.#Hr").format(dayDatas[index].workTime / 1000f / 60f / 60f)
////                logi(TAG,"此時計算出的上班時間為===>$caculateTime")
//                useXLables[index] += "\n$caculateTime"
//            }
//        }

//      定義右邊Y軸座標 //藉由左邊Y軸來設置顯示，因此右邊全部不顯示。
        chart.axisRight.apply {
            setDrawTopYLabelEntry(false)
            setDrawZeroLine(false)
            setDrawGridLines(false)
            setDrawLabels(false)
            setDrawAxisLine(false)
        }
//        print("xLabels：")
//        for (element in xLabels)
//            print("[${element}] ")
//        println()

//        定義X軸顯示
        var needTrans = true
        var valueDivider = 0f

        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
//            setTypeface(tfLight)
            setDrawAxisLine(false)
            setDrawGridLines(false)
            granularity = 1f                    // x標籤值的間距(若為2.0f，則每2個跳(顯示)一次)
//            setGranularity(dataSize.toFloat())
//            setLabelCount(datas.size+1, true)
//            labelCount = datas.size //實際資料量
            textColor = context.getColor(R.color.yellow) //設置標籤文字顏色
            position = XAxis.XAxisPosition.BOTTOM//設置標籤對齊底部
            setDrawLabels(true)//是否顯示標籤
            setDrawGridLines(true)//是否顯示對齊線
//            axisMinimum = dataSize.toFloat()
            axisMaximum = dataSize.toFloat() * dataSize.toFloat()
            textSize = 12f
            labelRotationAngle = 0f//設置左側標籤字體旋轉角度
//            val xAxisLabels = listOf("1", "2", "3", "4", "5", "6", "7")
//            valueFormatter = MyXFormatter(xLabels.toMutableList())
//            valueFormatter = IndexAxisValueFormatter(xAxisLabels)
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
//                    logi(TAG, "value % 1f是===>[${value % 1f}]")
                    if (needTrans) { // 變更為false以後就要用false去做到底
                        needTrans = value % 1f == 0f
                        valueDivider = 0.5f
                    }

                    val index = if (needTrans)
                        (value).roundToInt()
                    else {
//                        logi(TAG, "needTrans為false了！現在要執行轉換囉！Divider是===>$valueDivider")
                        (value / valueDivider).roundToInt()
                    }
                    logi(TAG, "現在的value值是===>$value")
                    logi(TAG, "現在要取的值是===>$index,,,value的Size是===>${xLabels.size}")
//                    logi(TAG, "現在要回傳的值是===>${xLabels[index]}")
                    return if (index >= datas.size )
                        ""
                    else
                        xLabels[index.absoluteValue]

//                    return xLabels[index]
                }
            }
        }
//        chart.setXAxisRenderer(ChangeLineXAxisRenderer(chart.viewPortHandler, chart.xAxis, chart.getTransformer(YAxis.AxisDependency.LEFT)))

        chart.setVisibleXRange(0F, (datas.size ).toFloat()) //設定可見範圍
        //美觀起見，實測後要減去這麼多的Float。
        val minValue = 0f//(dayDatas.map { it.startedWorkTime }.min()?.toFloat() ?: 0f) - 10000000f
//        定義左邊Ｙ軸座標
        chart.axisLeft.apply {
//            setDrawLabels(max > 1.0F) //全部為0值時不顯示標籤
            spaceTop = 10f          //固定高度，以避免全部為0值時不顯示
            gridColor = useLineColor            //設置格線顏色
            gridLineWidth = 0.75f    //設置格線寬度
            axisMinimum = minValue     //設置最小值
            textSize = 12f           //設置文字大小
            setLabelCount(horizontalLine, true) //設置橫向座標軸數量

//                               由於每次傳入值皆不同，因此Y座標之值皆要重算。
//            setDrawLabels(true)
//            axisMaximum = max        //設置圖表最大值//不可設置，因若設置的話，全為0的時候整個座標圖會不見。
//            valueFormatter = MyYAxisValueFormatter(minValue)
            setDrawAxisLine(false) //設置軸線不顯示(只顯示水平Y軸線)
            setDrawGridLines(true) //設置網格線顯示(顯示座標橫槓)
            textColor = useLineColor //設置標籤文字顏色
            isInverted = false      // 設置是否反向長出圖表(若為true，將從右邊長到左邊)

//            //增加上班限制線
//            addLimitLine(LimitLine(7200000f, "10:00").apply {
//                lineColor = context.getColor(R.color.txt_red)
//                textColor = context.getColor(R.color.txt_red)
//            })
//            //增加下班限制線
//            addLimitLine(LimitLine(37800000f, "18:30").apply {
//                lineColor = context.getColor(R.color.txt_blue)
//                textColor = context.getColor(R.color.txt_blue)
//            })
        }

//        定義圖表
        chart.apply {
            setDrawGridBackground(false)//設置網格背景(由於需要手動設置，所以此為false)
            description.isEnabled = false //右下角不顯示描述
            isDoubleTapToZoomEnabled = false
            isDragEnabled = false   //禁止拖拽
            legend.isEnabled = true //左下角顯示資料名稱
            extraLeftOffset = 20f // 設置左方(標籤)空間
            extraBottomOffset = 0f //設置下方空間
            setScaleEnabled(false) //設置不能縮放
            animateXY(0, 1000, Easing.EaseInCirc)
//            animateX(2000)
//            animateY(2000)
//                setRenderer(MyLineLegendRenderer(lineChart, lineChart.getAnimator(), lineChart.getViewPortHandler()))
            // chart.setHighlightEnabled(false)
            setDrawBarShadow(false)       // 設置是否顯示陰影
            setDrawBorders(true)          // 設置是否顯示外框邊線

            setDrawValueAboveBar(true)    // 設置為true時會將值放在Bar的上(右邊)
            animateXY(0, 1000, Easing.EaseOutCirc)
        }

//        設置點擊會彈出標記
//        lineChart.setOnChartValueSelectedListener(
//            ValueChartSelectListener(
//                context,
//                values.max(),
//                xLabels,
//                mBinding,
//                lineChart as BarLineChartBase<*>
//            )
//        )

        chart.invalidate()//繪製圖表
    }

    /**CombineChart 程式碼到此為止。*/


}
