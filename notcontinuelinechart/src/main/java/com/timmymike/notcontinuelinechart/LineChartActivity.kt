package com.timmymike.notcontinuelinechart

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.timmymike.notcontinuelinechart.databinding.ActivityLineChartBinding
import com.timmymike.notcontinuelinechart.dialog_base.DateTool
import com.timmymike.notcontinuelinechart.dialog_base.toDate
import com.timmymike.notcontinuelinechart.dialog_base.toString
import com.timmymike.radarcharttrial.dialog_base.logi
import java.text.DecimalFormat
import kotlin.math.roundToInt

class LineChartActivity : AppCompatActivity() {
    val TAG = javaClass.simpleName
    val activity = this

    val context: Context = this
    var timeFormate = "yyyy-MM-dd HH:mm:ss"
    lateinit var mBinding: ActivityLineChartBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_line_chart)
        mBinding = DataBindingUtil.setContentView(activity, R.layout.activity_line_chart)
        val xLabels = arrayOf("日", "一", "二", "三", "四", "五", "六", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "")

        mBinding.chartLineExample3.initChartView(getCombineExampleData(7, 2), xLabels)
        mBinding.ivRefresh.setOnClickListener {
            mBinding.chartLineExample3.initChartView(getCombineExampleData(7, 2), xLabels)
        }
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

    private fun LineChart.initChartView(datas: List<UserRecordModel.Data>, xLabels: Array<String>) {
//        logi(TAG, "傳入的XLabels是===>${xLabels.toList()}")
        //  設定白色常數(因為版本高低[高於23&低於23]因此有兩種寫法)
        val useLineColor = context.getColor(R.color.txt_gray5)

//      //傳入資料值
        val dayDatas = getDayData(datas)
        this.setBackgroundColor(context.getColor(R.color.theme_green_background))
        this.data = getData(/*this,*/ dayDatas)
//      取得資料頂端線數值(*8/7為固定算法)
        val max = (dayDatas.map { it.endWorkTime }.max()?.toFloat() ?: 0f) * 8 / 7 // 先試試看Long轉Float是否可行，如果不行再說。
//        logi(TAG, "最大值是===>$max")
//        logi(TAG, "最大值是===>${max.toLong()}")

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
        this.axisRight.apply {
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
//        var needTrans = true
//        var valueDivider = 0f
        this.xAxis.apply {
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
//                    if (needTrans) { // 變更為false以後就要用false去做到底
//                        needTrans = value % 1f == 0f
//                        valueDivider = 1f
//                    }

//                    val index = if (needTrans)
//                        (value).roundToInt()
//                    else {
////                        logi(TAG, "needTrans為false了！現在要執行轉換囉！Divider是===>$valueDivider")
//                        (value / valueDivider).roundToInt()
//                    }
//                    logi(TAG, "現在的value值是===>$value")
//                    logi(TAG, "現在要取的值是===>$index,,,value的Size是===>${xLabels.size}")
                    return xLabels[value.roundToInt()]
                }
            }
        }
//        this.setXAxisRenderer(ChangeLineXAxisRenderer(this.viewPortHandler, this.xAxis, this.getTransformer(YAxis.AxisDependency.LEFT)))

        this.setVisibleXRange(0F, (datas.size - 1).toFloat())
        //美觀起見，實測後要減去這麼多的Float。
        val minValue = 0f//(dayDatas.map { it.startedWorkTime }.min()?.toFloat() ?: 0f) - 10000000f
//        定義左邊Ｙ軸座標
        this.axisLeft.apply {
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
        this.apply {
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
            renderer = VisibleLineRender(this, this.animator, this.viewPortHandler);

        }

//        設定點擊會彈出標記
//        this.setOnChartValueSelectedListener(
//            ValueChartSelectListener(
//                context,
//                values.max(),
//                xLabels,
//                mBinding,
//                this as BarthisBase<*>
//            )
//        )

        this.invalidate()//繪製圖表


    }

    class DayData(
        var startedWorkTime: Long = 0L,
        var endWorkTime: Long = 0L,
        var averangeWorkTime: Long = 0L,
        var workTime: Long = 0L
    )

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

    /**將資料轉為LineDataSet並放入LineData內中的方法*/
    private fun getData(dayDatas: ArrayList<DayData>): LineData {

        //下班資料塞入
        val endWorkEntries = ArrayList<VisibleEntry>()
        //由於Entry只可以接受Float值，因此必須很疲勞的將傳入的Int轉為Float...@@a
        for (i in 0 until dayDatas.size) {
            if ( dayDatas[i].endWorkTime == 0L)
                endWorkEntries.add(VisibleEntry(i.toFloat(), dayDatas[i].endWorkTime.toFloat(), false))
            else
                endWorkEntries.add(VisibleEntry(i.toFloat(), dayDatas[i].endWorkTime.toFloat(), true))
        }
        logi(TAG, "完成後的endWorkEntries是===>$endWorkEntries")
        val endWorkSet = VisibleLineDataSet(endWorkEntries, "EndWorkTime").apply {
            mode = LineDataSet.Mode.LINEAR //設定曲線模式
            setDrawCircles(true) //設定是否顯示圓點
            setDrawCircleHole(false)
            setCircleColor(context.getColor(R.color.green))
            color = ContextCompat.getColor(context, R.color.green)//設定線條顏色
            lineWidth = 1.0f //設定線條寬度
//            fillColor = context.ge
            fillAlpha = 200
            fillDrawable = ContextCompat.getDrawable(context, R.drawable.fade_line_chart_background)//設定曲線下背景漸層
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
        val aveWorkEntries = ArrayList<VisibleEntry>()
        //由於Entry只可以接受Float值，因此必須很疲勞的將傳入的Int轉為Float...@@a
        for (i in 0 until dayDatas.size) {
            if ( dayDatas[i].averangeWorkTime == 0L)
                aveWorkEntries.add(VisibleEntry(i.toFloat(), dayDatas[i].averangeWorkTime.toFloat(), false))
            else
                aveWorkEntries.add(VisibleEntry(i.toFloat(), dayDatas[i].averangeWorkTime.toFloat(), true))
        }
        logi(TAG, "完成後的 aveWorkEntries 是===>$aveWorkEntries")
        val aveWorkSet = VisibleLineDataSet(aveWorkEntries, "aveWorkTime").apply {
            mode = LineDataSet.Mode.LINEAR //設定曲線模式
            setDrawCircles(true) //設定是否顯示圓點
            setDrawCircleHole(false)
            setCircleColor(context.getColor(R.color.txt_dark_red))
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
        val startWorkEntries = ArrayList<VisibleEntry>()
        //由於Entry只可以接受Float值，因此必須很疲勞的將傳入的Int轉為Float...@@a
        for (i in 0 until dayDatas.size) {
            if ( dayDatas[i].startedWorkTime == 0L)
                startWorkEntries.add(VisibleEntry(i.toFloat(), dayDatas[i].startedWorkTime.toFloat(), false))
            else
                startWorkEntries.add(VisibleEntry(i.toFloat(), dayDatas[i].startedWorkTime.toFloat(), true))
//            logi(TAG, "開始上班轉換前的值是===>${dayDatas[i].startedWorkTime}")
//            logi(TAG, "開始上班其意義為是===>${dayDatas[i].startedWorkTime.toDate().toString(timeFormate)}")
        }
        logi(TAG, "完成後的 startWorkEntries 是===>$startWorkEntries")
        val startWorkSet = VisibleLineDataSet(startWorkEntries, "StartWorkTime").apply {
            mode = LineDataSet.Mode.LINEAR //設定曲線模式
            setDrawCircles(true) //設定是否顯示圓點
            setDrawCircleHole(false)
            setCircleColor(context.getColor(R.color.txt_gray5))
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

        return lineData
    }

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
//        logi(TAG, "產生useBooleanArray前，完成的booleanArray是===>$booleanArray")
        //隨機排序階段
        val useBooleanArray = ArrayList<Boolean>()
        while (booleanArray.size > 0) {
            val randomIndex = (0 until booleanArray.size).random()
            useBooleanArray.add(booleanArray[randomIndex])
            booleanArray.removeAt(randomIndex)
        }

//        logi(TAG, "產生資料前，完成的useBooleanArray是===>$useBooleanArray")
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
                result.add(UserRecordModel.Data("", 0, 0L.toDate().toString(timeFormate), null))
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

}

