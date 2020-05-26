package com.timmymike.combinelinechartdemo2

import android.content.Context
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import com.timmymike.combinelinechartdemo2.databinding.ActivityMainBinding
import java.text.DecimalFormat
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    val TAG = javaClass.simpleName
    private val context: Context = this
    private val activity = this

    //    private var startTime = DateTool.nowDate
    private lateinit var mBinding: ActivityMainBinding

    private var dataSize = 7 //預設資料筆數

    private var zeroNum = 2 // 是空的資料筆數(讓其顯示為缺席)

    private val timeFormate = "yyyy-MM-dd HH:mm:ss"

    private val xLabels = arrayOf("", "一", "二", "三", "四", "五", "六", "日", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "")

    private var example = getCombineExampleData(dataSize, zeroNum)

    private var randomRange = DateTool.oneMin * 30  // 預設亂數範圍

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initCombineCharView(mBinding.chartLineExample2, forChartData(example, randomRange), xLabels)

        initEvent()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initEvent() {
        mBinding.ivRefresh.setOnClickListener {
            example = getCombineExampleData(dataSize, zeroNum)
            initCombineCharView(mBinding.chartLineExample2, forChartData(example, randomRange), xLabels)
        }

        mBinding.ivSetting.setOnClickListener {

            SettingDialog(context).apply {
                title = "圖表亂數設定"
                message = "資料筆數：$dataSize"

                binding.seekBar.max = 7
                binding.seekBar.min = 1
                binding.seekBar.progress = dataSize
                binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                        message = "資料筆數：$p1"
                        binding.tvMessage.text = message
                        dataSize = p1
                        if (zeroNum > dataSize) {// 為空值數量不可超過資料筆數，若超過，於此處理
                            binding.seekBar3.progress = dataSize
                            zeroNum = dataSize
                        }
                    }

                    override fun onStartTrackingTouch(p0: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(p0: SeekBar?) {
                    }
                })
                message2 = "頭尾資料亂數範圍：${(randomRange / DateTool.oneMin).toInt()} 分鐘"
                binding.seekBar2.max = 120
                binding.seekBar2.min = 0
                binding.seekBar2.progress = (randomRange / DateTool.oneMin).toInt()
                binding.seekBar2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                        message2 = "頭尾資料亂數範圍：$p1 分鐘"
                        binding.tvMessage2.text = message2
                        randomRange = (p1.toLong()) * DateTool.oneMin
                    }

                    override fun onStartTrackingTouch(p0: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(p0: SeekBar?) {
                    }
                })
                message3 = "缺席天數：$zeroNum"
                binding.seekBar3.max = 7
                binding.seekBar3.min = 0
                binding.seekBar3.progress = zeroNum
                binding.seekBar3.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                        message3 = "缺席天數：$p1"
                        binding.tvMessage3.text = message3
                        zeroNum = p1
                    }

                    override fun onStartTrackingTouch(p0: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(p0: SeekBar?) {
                    }
                })


                binding.btnRight.setOnClickListener {


                    example = getCombineExampleData(dataSize, zeroNum)
                    initCombineCharView(mBinding.chartLineExample2, forChartData(example, randomRange), xLabels)
                    dialog.dismiss()
                }

                binding.btnLift.visibility = View.GONE

            }.show()
        }
    }
    /**CombineChart 程式碼由此開始複製：*/
    /**偽造資料的部分：前後加上兩個隨機值*/
    private fun forChartData(data: ArrayList<UserRecordModel.Data>, randomRange: Long): List<UserRecordModel.Data> {
        val replyData = ArrayList<UserRecordModel.Data>()
        replyData.addAll(data)
        replyData.add(0, UserRecordModel.Data().apply { // 第0個值由第一個值去寫入
            val randomStart = if ((0..1).random() == 1) (0..randomRange).random() else -1 * (0..randomRange).random()
            val randomEnd = if ((0..1).random() == 1) (0..randomRange).random() else -1 * (0..randomRange).random()
//            logi(TAG,"取到的randomStart是===>$randomStart")
            startedWorkAt = ((data[0].startedWorkAt?.toDate(timeFormate)?.time ?: 0L) + randomStart).toDate().toString(timeFormate)
//            logi(TAG,"原始值是===>${data[0].startedWorkAt?.toDate(timeFormate)?.time?:0L}")
//            logi(TAG,"結果開始值是===>$startedWorkAt")

            finishedWorkAt = ((data[0].finishedWorkAt?.toDate(timeFormate)?.time ?: 0L) + randomEnd).toDate().toString(timeFormate)
        })

        replyData.add(UserRecordModel.Data().apply { // 最後一個值由最後一個值去寫入

            val randomStart = if ((0..1).random() == 1) (0..randomRange).random() else -1 * (0..randomRange).random()
            val randomEnd = if ((0..1).random() == 1) (0..randomRange).random() else -1 * (0..randomRange).random()

            startedWorkAt = ((data[data.size - 1].startedWorkAt?.toDate(timeFormate)?.time ?: 0L) + randomStart).toDate().toString(timeFormate)
            finishedWorkAt = ((data[data.size - 1].finishedWorkAt?.toDate(timeFormate)?.time ?: 0L) + randomEnd).toDate().toString(timeFormate)
        })

        return replyData
    }

    /**這裡要對資料作處理，將傳入的dataList轉為DayData並回傳*/
    private fun getDayData(dataList: List<UserRecordModel.Data>): ArrayList<DayData> {
        val result = ArrayList<DayData>()
        for (data in dataList) {
            result.add(DayData().apply {
                //實際時間       取時間的方法：由於時區的關係，必須轉為1970-01-01 HH:mm:ss再轉回time(Long值)
                startedWorkTime = (data.startedWorkAt?.toDate(timeFormate)?.toString("1970-01-01 HH:mm:ss")?.toDate(timeFormate)?.time ?: 0L)//- (data.startedWorkAt?.toDate(timeFormate)?.getStartOfDay()?.time ?: 0L)
//                startedWorkTime = (DateTool.getMilliseconds(data.startedWorkAt?:"1970-01-01 00:00:00",timeFormate)) - (DateTool.getMillSecondAtZeroOclock(DateTool.getMilliseconds(data.startedWorkAt?:"1970-01-01 00:00:00",timeFormate)))
//                logi(TAG,"於 getDayData 時,,,開始上班時間字串為===>${data.startedWorkAt}")
//                logi(TAG,"於 getDayData 時,,,開始上班時的開始日期為===>${(DateTool.getTime(DateTool.getMillSecondAtZeroOclock(DateTool.getMilliseconds(data.startedWorkAt?:"1970-01-01 00:00:00",timeFormate)),timeFormate))}")
//                logi(TAG,"於 getDayData 時,,,開始上班時間其值為===>$startedWorkTime")
//                logi(TAG,"於 getDayData 時,,,開始上班時間轉換後的儲存值為===>${startedWorkTime.toDate().toString(timeFormate)}")
                endWorkTime = (data.finishedWorkAt?.toDate(timeFormate)?.toString("1970-01-01 HH:mm:ss")?.toDate(timeFormate)?.time ?: 0L)
//                logi(TAG,"於 getDayData 時,,,結束下班時間字串為===>${data.finishedWorkAt}")
//                logi(TAG,"於 getDayData 時,,,結束下班時的開始日期為===>${(DateTool.getTime(DateTool.getMillSecondAtZeroOclock(DateTool.getMilliseconds(data.finishedWorkAt?:"1970-01-01 00:00:00",timeFormate)),timeFormate))}")
//                logi(TAG,"於 getDayData 時,,,結束下班時間其值為===>$endWorkTime")
//                logi(TAG,"於 getDayData 時,,,結束下班時間轉換後的儲存值為===>${endWorkTime.toDate().toString(timeFormate)}")
                workTime = endWorkTime - startedWorkTime
                averangeWorkTime = (startedWorkTime + endWorkTime) / 2
            })
        }
        return result
    }

    /**將資料轉為LineDataSet並放入LineData內中的方法*/
    private fun getData(/*lineChart: CombinedChart, */dayDatas: ArrayList<DayData>): CombinedData {

        //下班資料塞入
        val endWorkEntries = ArrayList<Entry>()
        //由於Entry只可以接受Float值，因此必須很疲勞的將傳入的Int轉為Float...@@a
        for (i in 0 until dayDatas.size) {
            endWorkEntries.add(Entry(i.toFloat(), dayDatas[i].endWorkTime.toFloat()))
        }
        logi(TAG, "完成後的endWorkEntries是===>$endWorkEntries")
        val endWorkSet = LineDataSet(endWorkEntries, "EndWorkTime").apply {
            mode = LineDataSet.Mode.HORIZONTAL_BEZIER //設定曲線模式
            setDrawCircles(false) //設定不顯示圓點
            color = ContextCompat.getColor(context, R.color.green)//設定線條顏色
            lineWidth = 1.0f //設定線條寬度
//            fillColor = context.ge
            fillAlpha = 200
//            fillDrawable = ContextCompat.getDrawable(context, R.drawable.fade_line_chart_background)//設定曲線下背景漸層
            fillDrawable = null
            setDrawFilled(false)     //設定顯示曲線下背景漸層
            setDrawValues(false)    //設定不顯示頂端值
            setDrawHorizontalHighlightIndicator(false)  //設定點擊不顯示水平線條
            setDrawVerticalHighlightIndicator(false)    //設定點擊不顯示垂直線條
//            fillFormatter = IFillFormatter { _, _ ->
//                lineChart.axisLeft.axisMinimum
//            }
        }
        //平均上班時間資料塞入
        val aveWorkEntries = ArrayList<Entry>()
        //由於Entry只可以接受Float值，因此必須很疲勞的將傳入的Int轉為Float...@@a
        for (i in 0 until dayDatas.size) {
            aveWorkEntries.add(Entry(i.toFloat(), dayDatas[i].averangeWorkTime.toFloat()))
        }
        logi(TAG, "完成後的 aveWorkEntries 是===>$aveWorkEntries")
        val aveWorkSet = LineDataSet(aveWorkEntries, "aveWorkTime").apply {
            mode = LineDataSet.Mode.HORIZONTAL_BEZIER //設定曲線模式
            setDrawCircles(false) //設定不顯示圓點
            color = ContextCompat.getColor(context, R.color.txt_dark_red)//設定線條顏色
            lineWidth = 1.0f //設定線條寬度
            fillDrawable = null
//            fillDrawable = ContextCompat.getDrawable(context, R.drawable.fade_line_chart_background)//設定曲線下背景漸層

            setDrawFilled(false)     //設定顯示曲線下背景漸層
            setDrawValues(false)    //設定不顯示頂端值
            setDrawHorizontalHighlightIndicator(false)  //設定點擊不顯示水平線條
            setDrawVerticalHighlightIndicator(false)    //設定點擊不顯示垂直線條

        }
        //開始上班時間資料塞入
        val startWorkEntries = ArrayList<Entry>()
        //由於Entry只可以接受Float值，因此必須很疲勞的將傳入的Int轉為Float...@@a
        for (i in 0 until dayDatas.size) {
            startWorkEntries.add(Entry(i.toFloat(), dayDatas[i].startedWorkTime.toFloat()))
//            logi(TAG, "開始上班轉換前的值是===>${dayDatas[i].startedWorkTime}")
//            logi(TAG, "開始上班其意義為是===>${dayDatas[i].startedWorkTime.toDate().toString(timeFormate)}")
        }
        logi(TAG, "完成後的 startWorkEntries 是===>$startWorkEntries")
        val startWorkSet = LineDataSet(startWorkEntries, "StartWorkTime").apply {
            mode = LineDataSet.Mode.HORIZONTAL_BEZIER //設定曲線模式
            setDrawCircles(false) //設定不顯示圓點
            color = ContextCompat.getColor(context, R.color.txt_gray5)//設定線條顏色
            lineWidth = 1.0f //設定線條寬度
//            fillDrawable = null
//            fillDrawable = ContextCompat.getDrawable(context, R.drawable.fade_line_chart_background)//設定曲線下背景漸層

            setDrawFilled(false)     //設定顯示曲線下背景漸層
            setDrawValues(false)    //設定不顯示頂端值
            setDrawHorizontalHighlightIndicator(false)  //設定點擊不顯示水平線條
            setDrawVerticalHighlightIndicator(false)    //設定點擊不顯示垂直線條
            // change the return value here to better understand the effect
            // return 600;
//            fillFormatter = IFillFormatter { _, _ ->
//                lineChart.axisLeft.axisMaximum
//            }
        }
        //全部線圖資料塞入
        val lineData = LineData()
        lineData.addDataSet(startWorkSet)
        lineData.addDataSet(endWorkSet)
        lineData.addDataSet(aveWorkSet)

        //製作Bar(長條圖資料)
        //上班時間長度資料塞入
        val workEntries = ArrayList<BarEntry>()
        //由於Entry只可以接受Float值，因此必須很疲勞的將傳入的Int轉為Float...@@a
        for (i in 0 until dayDatas.size) {
            if (i == 0 || i == dayDatas.size - 1) // 第一個或最後一個時
                workEntries.add(BarEntry(i.toFloat(), 0f))
            else
                workEntries.add(BarEntry(i.toFloat(), dayDatas[i].workTime.toFloat()))
        }
        logi(TAG, "完成後的WorkTimeEntries 是===>$aveWorkEntries")
        val workSet = BarDataSet(workEntries, "WorkTime").apply {
//            color = context.getColor(R.color.yellow)
            setDrawValues(false)    //設置是否顯示頂端值
            //設定漸層顏色//只有3.1.0-alpha板後才有此功能。
            val startColor1 = ContextCompat.getColor(context, R.color.bg_color_2)
            val endColor1 = ContextCompat.getColor(context, R.color.bg_color_1)

            setGradientColor(startColor1, endColor1)

            isHighlightEnabled = false // 點擊顯示變更顏色是否開啟
        }
        val barData = BarData().apply {
            barWidth = 0.8f
        }
        barData.addDataSet(workSet)


        val combinedData = CombinedData()
        combinedData.setData(lineData)
        combinedData.setData(barData)
        return combinedData
    }

    class DayData(
        var startedWorkTime: Long = 0L,
        var endWorkTime: Long = 0L,
        var averangeWorkTime: Long = 0L,
        var workTime: Long = 0L
    )

    /**換行用X座標軸類別*/
    class ChangeLineXAxisRenderer(viewPortHandler: ViewPortHandler?, xAxis: XAxis?, trans: Transformer?) : XAxisRenderer(viewPortHandler, xAxis, trans) {
        override fun drawLabel(
            c: Canvas?, formattedLabel: String, x: Float, y: Float, anchor: MPPointF?, angleDegrees: Float
        ) {
//        super.drawLabel(c, formattedLabel, x, y, anchor, angleDegrees); //註釋掉這個，否則坐標標籤複寫兩次
            val lines = formattedLabel.split("\n").toTypedArray()
            for (i in lines.indices) {
                val vOffset = i * mAxisLabelPaint.textSize
                Utils.drawXAxisValue(c, lines[i], x, y + vOffset, mAxisLabelPaint, anchor, angleDegrees)
            }
        }
    }

    /**設定左側Y座標值：*/
    inner class MyYAxisValueFormatter(private val min: Float) : ValueFormatter() {

        override fun getFormattedValue(value: Float): String {
//            println("此時要取的Y軸標籤Index值是===>$value")
//            println("此時要取的Y軸標籤min值是===>$min")
            return when (value) {
                min -> "" // 最下方不顯示值
                else -> value.toLong().toDate().toString("HH:mm")
            }
        }
    }

    private fun initCombineCharView(lineChart: CombinedChart, datas: List<UserRecordModel.Data>, xLabels: Array<String>) {
        logi(TAG, "傳入的XLabels是===>${xLabels.toList()}")
        //  設定白色常數(因為版本高低[高於23&低於23]因此有兩種寫法)
        val useLineColor = context.getColor(R.color.txt_gray5)

//      //傳入資料值
        val dayDatas = getDayData(datas)
        lineChart.setBackgroundColor(context.getColor(R.color.theme_green_background))
        lineChart.data = getData(/*lineChart,*/ dayDatas)
//      取得資料頂端線數值(*8/7為固定算法)
        val max = (dayDatas.map { it.endWorkTime }.max()?.toFloat() ?: 0f) * 8 / 7 // 先試試看Long轉Float是否可行，如果不行再說。
        logi(TAG, "最大值是===>$max")
        logi(TAG, "最大值是===>${max.toLong()}")

        //依照dayDatas內算出的每天工作時數作處理：加上換行符號與小時
        val useXLables = ArrayList<String>()
        useXLables.addAll(xLabels)
        for (index in dayDatas.indices) {
            if (useXLables[index] != "") {
                val caculateTime = DecimalFormat("#.#Hr").format(dayDatas[index].workTime / 1000f / 60f / 60f)
//                logi(TAG,"此時計算出的上班時間為===>$caculateTime")
                useXLables[index] += "\n$caculateTime"
            }
        }

//      定義右邊Y軸座標 //藉由左邊Y軸來設定顯示，因此右邊全部不顯示。
        lineChart.axisRight.apply {
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
        lineChart.xAxis.apply {
            setLabelCount(dayDatas.size, true)
//            labelCount = datas.size //實際資料量
            textColor = useLineColor //設定標籤文字顏色
            position = XAxis.XAxisPosition.BOTTOM//設定標籤對齊底部
            setDrawLabels(true)//是否顯示標籤
            setDrawGridLines(true)//是否顯示對齊線
            textSize = 12f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
//                    logi(TAG, "value % 1f是===>[${value % 1f}]")
                    if (needTrans) { // 變更為false以後就要用false去做到底
                        needTrans = value % 1f == 0f
                        valueDivider = 1f
                    }

                    val index = if (needTrans)
                        (value).roundToInt()
                    else {
//                        logi(TAG, "needTrans為false了！現在要執行轉換囉！Divider是===>$valueDivider")
                        (value / valueDivider).roundToInt()
                    }
//                    logi(TAG, "現在的value值是===>$value")
//                    logi(TAG, "現在要取的值是===>$index,,,value的Size是===>${xLabels.size}")
                    return if (index.toFloat() >= dayDatas.size - 1)
                        ""
                    else
                        useXLables[index]
                }
            }
        }
        lineChart.setXAxisRenderer(ChangeLineXAxisRenderer(lineChart.viewPortHandler, lineChart.xAxis, lineChart.getTransformer(YAxis.AxisDependency.LEFT)))

        lineChart.setVisibleXRange(0F, (datas.size - 1).toFloat())
        //美觀起見，實測後要減去這麼多的Float。
        val minValue = 0f//(dayDatas.map { it.startedWorkTime }.min()?.toFloat() ?: 0f) - 10000000f
//        定義左邊Ｙ軸座標
        lineChart.axisLeft.apply {
            setDrawLabels(max > 1.0F) //全部為0值時不顯示標籤
            spaceTop = 10f          //固定高度，以避免全部為0值時不顯示
            gridColor = useLineColor            //設定格線顏色
            gridLineWidth = 0.75f    //設定格線寬度
            axisMinimum = minValue     //設定最小值
            textSize = 12f           //設定文字大小
            setLabelCount(5, true) //設定橫向座標軸數量

//                               由於每次傳入值皆不同，因此Y座標之值皆要重算。
//            setDrawLabels(true)
//            axisMaximum = max        //設定圖表最大值//不可設定，因若設定的話，全為0的時候整個座標圖會不見。
            valueFormatter = MyYAxisValueFormatter(minValue)
            setDrawAxisLine(false) //設定軸線不顯示(只顯示水平Y軸線)
            setDrawGridLines(true) //設定網格線顯示(顯示座標橫槓)
            textColor = useLineColor //設定標籤文字顏色

            //增加上班限制線
            addLimitLine(LimitLine(7200000f, "10:00").apply {
                lineColor = context.getColor(R.color.txt_red)
                textColor = context.getColor(R.color.txt_red)
            })
            //增加下班限制線
            addLimitLine(LimitLine(37800000f, "18:30").apply {
                lineColor = context.getColor(R.color.txt_blue)
                textColor = context.getColor(R.color.txt_blue)
            })
        }

//        定義圖表
        lineChart.apply {
            setDrawGridBackground(false)//設定網格背景(由於需要手動設定，所以此為false)
            description.isEnabled = false //右下角不顯示描述
            isDoubleTapToZoomEnabled = false
            isDragEnabled = false   //禁止拖拽
            legend.isEnabled = true //左下角顯示資料名稱
            extraBottomOffset = 20f //設定下方空間
            setScaleEnabled(false) //設定不能縮放
            animateXY(0, 1000, Easing.EaseInCirc)
//            animateX(2000)
//            animateY(2000)
//                setRenderer(MyLineLegendRenderer(lineChart, lineChart.getAnimator(), lineChart.getViewPortHandler()));

        }

//        設定點擊會彈出標記
//        lineChart.setOnChartValueSelectedListener(
//            ValueChartSelectListener(
//                context,
//                values.max(),
//                xLabels,
//                mBinding,
//                lineChart as BarLineChartBase<*>
//            )
//        )

        lineChart.invalidate()//繪製圖表
    }

    /**CombineChart 程式碼到此為止。*/

    /**==========================產生測試資料程式碼如下==========================*/
    private fun getCombineExampleData(dataSize: Int, zeroNum: Int): ArrayList<UserRecordModel.Data> {
        val result = ArrayList<UserRecordModel.Data>()
        //藉由zeroNum的數字決定現在有幾個值是沒有值得資料
        //具體作法：
        // 先宣告一個布林陣列，讓後方的取值回圈依照此部臨陣獵取值：若為true，則取有值；若為false，則取無值
        // 而這個布林陣列如何產生？
        // 先產生一個zeroNum數量的false的陣列，再將其隨機排序即可。
        var userZeroNum = zeroNum
        val booleanArray = ArrayList<Boolean>()
        //放入資料階段
        for (i in 0 until dataSize) {
            if (userZeroNum > 0) {
                booleanArray.add(false)
                userZeroNum -= 1
            } else {
                booleanArray.add(true)
            }
        }
        logi(TAG, "產生useBooleanArray前，完成的booleanArray是===>$booleanArray")
        //隨機排序階段
        val useBooleanArray = ArrayList<Boolean>()
        while (booleanArray.size > 0) {
            val randomIndex = (0 until booleanArray.size).random()
            useBooleanArray.add(booleanArray[randomIndex])
            booleanArray.removeAt(randomIndex)
        }

        logi(TAG, "產生資料前，完成的useBooleanArray是===>$useBooleanArray")
        logi(TAG, "==========================開始產生資料========================")

        for (i in 0 until dataSize) {
//            logi(TAG,"i現在是$i")
            val randomIndex = (0..7).random()

//            result.add(UserRecordModel.Data(department = getNameByIndex(randomIndex), departmentId = randomIndex + 1, startedWorkAt = getStartTime(i), finishedWorkAt = getEndTime(i)))
//            val randomNum = (0..1).random()
//            logi(TAG, "現在取的隨機值是===>$randomNum")
//            logi(TAG, "現在取值的底值是===>${(zeroNum / 2)}")
//            logi(TAG, "userZeroNum是===>${userZeroNum}")
            if (useBooleanArray[i]) {
//                userZeroNum -= 1
                result.add(UserRecordModel.Data("", 0, getStartTime(i), getEndTime(i)))
            } else {
                result.add(UserRecordModel.Data("", 0, null, 0L.toDate().toString(timeFormate)))
            }

        }
//        logi(TAG,"回傳前，資料大小是===>${result.size},,,dataSize是===>$dataSize")
        logi(TAG, "產生的資料為===>$result")
        return result
    }

    /**依照index回傳隨機的開始時間
     * 格式如下：
     * "yyyy-MM-dd HH:mm:ss"
     * 具體作法：9:30~18:30隨機取一個資料，接著第二筆再於剩下的時間去取
     */


    private fun getStartTime(i: Int): String {
        val nowStartTime = 1586914200000L //2020-04-15 09:30:00.000
        val getTime = (nowStartTime + (0L..1800000L).random() + (DateTool.oneDay * i))
        val nowGet = getTime.toDate().toString(timeFormate)
//        logi(TAG, "產生資料時 的開始時間是===>$getTime")
//        logi(TAG, "產生資料時 的開始時間是===>$nowGet")
        return nowGet
    }

    /**依照index回傳隨機的結束時間
     * 格式如下：
    "yyyy-MM-dd HH:mm:ss"*/
    private fun getEndTime(i: Int): String {
        val endTime = 1586946600000L      //2020-04-15 18:30:00.000
//        if (i == maxSize - 1)//下班了的最後那段時間
//            return "2020-04-10 18:30:00"
//        var nowget = (endTime + (0L..10800000L).random())
//        while (nowget - nowStartTime > DateTool.oneHour) //如果結束時間超過現在開始時間一小時要重取(這代表每筆資料不會大於一小時)
//            nowget = (nowStartTime..endTime).random()
//        nowStartTime = nowget
//        return nowget.toDate().toString(timeFormate)
        return (endTime + (0L..10800000L).random() + (DateTool.oneDay * i)).toDate().toString(timeFormate)
    }

    private fun getNameByIndex(i: Int): String {
        return when (i) {
            0 -> "工作區\n(5hr)"
            1 -> "樣品室\n(3.5hr)"
            2 -> "餐廳\n(6hr)"
            3 -> "廁所\n(9hr)"
            4 -> "財務部\n(8hr)"
            5 -> "研發部"
            6 -> "辦公室"
            7 -> "會議室"
            else -> ""
        }
    }

    /**==========================產生測試資料程式碼如上==========================*/
}
