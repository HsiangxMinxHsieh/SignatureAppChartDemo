package com.timmy.barchartactivity

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.github.mikephil.charting.charts.BarLineChartBase
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.MPPointF
import com.timmy.barchartactivity.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.mark_for_chart.view.*
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {
    val TAG = javaClass.simpleName
    private val context: Context = this
    private lateinit var mBinding: ActivityMainBinding

    private var randomLimit = 1000
    private var randomNum = 45
    //    定義X軸標籤
    private var xLabels = getLabels(randomNum)

    //    資料值
    private var values = getRandomData(randomNum)

    /**一頁最多容許的分鐘值*/
    private val screenMaxIncludeValues = 15

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initView()

//        //一定要加上forBarChartData方法(頭尾要產生0值)
//        values = forChartData(values) as MutableList<Int>
        initBarCharView(values, xLabels)

        initLineCharView(forChartData(values), xLabels)
        initEvent()

    }


    private fun initView() {
//        mBinding.bcDbLabel.visibility = View.VISIBLE
//        mBinding.lcDbLabel.visibility = View.GONE
//
//      測試時使用值，製作長條圖時使用：
        mBinding.swBarLine.text = "Bar"
        mBinding.bcDbLabel.visibility = View.VISIBLE
        mBinding.lcDbLabel.visibility = View.GONE
        mBinding.lcCombineLabel.visibility = View.GONE

    }

//    Line Chart複製程式碼從以下開始：

    private val lineChart: LineChart by lazy { mBinding.lcDbLabel }

    //經過調查，LineChar的xLabel值得問題是value的Float取值會出現問題，所以利用此方法來一一判斷取值。
    private fun getXLabelStringFromInData(value: Float, xLabels: Array<String>): String {

        when {
            xLabels.size in 6..11 -> return if (value > 4.5F && value.roundToInt() < xLabels.size) {
                xLabels[value.roundToInt()]
            } else if (value.toInt() < xLabels.size) {
                xLabels[value.toInt()]
            } else {
                ""
            }
            xLabels.size in 12..15 -> return if (value > 9.5f && value.roundToInt() < xLabels.size) {
                xLabels[value.roundToInt()]
            } else if (value.toInt() < xLabels.size) {
                xLabels[value.toInt()]
            } else {
                ""
            }

            xLabels.size in 16..19 -> return if (value > 9.5f && value < 13.5f && value.roundToInt() < xLabels.size) {
                xLabels[value.roundToInt()]
            } else if (value >= 13.5F && value.toInt() + 1 < xLabels.size) {
                xLabels[value.toInt() + 1] // 不要問我這裡為什麼要+1，只能說是實測結果。
            } else if (value.toInt() < xLabels.size) {
                xLabels[value.toInt()]
            } else {
                ""
            }
//                println("value===>$value;;;;value.roundtoInt===>[${value.roundToInt()}];;;;value.toInt===>[${value.toInt()}];;;;取到的xLabel===>[${if (value.toInt() < xLabels.size) xLabels[value.toInt()] else ""}]")
            //大於等於20(項目個數大於等於18)是不會有問題的，所以直接回傳即可。
            else -> return if (value.toInt() < xLabels.size) xLabels[value.toInt()] else ""
        }
    }

    private fun initLineCharView(values: List<Int>, xLabels: Array<String>) {

        //  設定白色常數(因為版本高低[高於23&低於23]因此有兩種寫法)
        val white = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.getColor(R.color.txt_white)
        } else {
            resources.getColor(R.color.txt_white)
        }
        mBinding.tvDbMarkContent.setTextSize(12)
        //傳入資料值
        lineChart.data = getLineData(values)
//      取得資料頂端線數值(*8/7為固定算法)
        val max = values.max()!!.toFloat() * 8 / 7

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

        //由於LineChart的XLabels比較特別(或說比較舊？)，要做特殊處理(移位)
        //以下程式碼為實測後之結果，蝦米也不知道為什麼會要這樣做(就是會有一些數字偏移)。
//        val lineChartXlabels: Array<String> = getXLabelFromInData(xLabels)

//        定義X軸顯示
        lineChart.xAxis.apply {
            labelCount = values.size //實際資料量
            textColor = white //設定標籤文字顏色
            position = XAxis.XAxisPosition.BOTTOM//設定標籤對齊底部
            setDrawLabels(true)//是否顯示標籤
            setDrawGridLines(false)//是否顯示對齊線
            textSize = 12f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    Log.i(TAG,"現在value的值是===>$value")
                    return (value.toInt()).toString()//關鍵：根據需要返回x軸數據，x軸的數據個數一定要等於下面max-min的值
                }
            }
        }

        lineChart.setVisibleXRange(0F, (values.size - 1).toFloat())

//        定義左邊Ｙ軸座標
        lineChart.axisLeft.apply {
            setDrawLabels(max > 1.0F) //全部為0值時不顯示標籤
            spaceTop = 10f          //固定高度，以避免全部為0值時不顯示
            gridColor = white            //設定格線顏色
            gridLineWidth = 0.75f    //設定格線寬度
            axisMinimum = 0f        //設定最小值
            textSize = 12f           //設定文字大小
            setLabelCount(5, true) //設定橫向座標軸數量
//                               由於每次傳入值皆不同，因此Y座標之值皆要重算。
//            setDrawLabels(true)
            valueFormatter = MyYAxisValueFormatter(values.max()!!.toFloat())
//            axisMaximum = max        //設定圖表最大值//不可設定，因若設定的話，全為0的時候整個座標圖會不見。
            setDrawAxisLine(false) //設定軸線不顯示(只顯示水平Y軸線)
            setDrawGridLines(true) //設定網格線顯示(顯示座標橫槓)
            textColor = white //設定標籤文字顏色
        }

//        定義圖表
        lineChart.apply {
            setDrawGridBackground(false)//設定網格背景(由於需要手動設定，所以此為false)
            description.isEnabled = false //右下角不顯示描述
            isDoubleTapToZoomEnabled = false
            isDragEnabled = false   //禁止拖拽
            legend.isEnabled = false //左下角不顯示資料名稱
            setScaleEnabled(false)//設定不能縮放
            animateX(400)
            animateY(1200)
        }

//        設定點擊會彈出標記
        lineChart.setOnChartValueSelectedListener(
            ValueChartSelectListener(
                context,
                values.max(),
                xLabels,
                mBinding,
                lineChart as BarLineChartBase<*>
            )
        )

        lineChart.invalidate()//繪製圖表
    }

    private fun getLineData(values: List<Int>): LineData? {
        val entries = ArrayList<Entry>()
        //由於Entry只可以接受Float值，因此必須很疲勞的將傳入的Int轉為Float...@@a
        //這疲勞的事交給你了，for迴圈！
        for (i in 0 until values.size) {
            entries.add(Entry(i.toFloat(), values[i].toFloat()))
        }

        val set = LineDataSet(entries, "LineDataSet")

        set.mode = LineDataSet.Mode.HORIZONTAL_BEZIER //設定曲線模式
        set.setDrawCircles(false) //設定不顯示圓點
        set.color = ContextCompat.getColor(context, R.color.Line_Chart)//設定線條顏色
        set.lineWidth = 1.0f //設定線條寬度

        set.fillDrawable = ContextCompat.getDrawable(
            context, //設定曲線下背景漸層
            R.drawable.fade_line_chart_background
        )

        set.setDrawFilled(true)     //設定顯示曲線下背景漸層
        set.setDrawValues(false)    //設定不顯示頂端值
        set.setDrawHorizontalHighlightIndicator(false)  //設定點擊不顯示水平線條
        set.setDrawVerticalHighlightIndicator(false)    //設定點擊不顯示垂直線條
        return LineData(set)
    }

//    Line Chart複製程式碼到以上為止

//    Bar Chart複製程式碼從以下開始：

    private val barChart: CombinedChart by lazy { mBinding.bcDbLabel }

    private fun initBarCharView(values: List<Int>, xLabels: Array<String>, dataCount: Int = 3) {
//        println("全域Values ====>${this.values}")
        println("區域Values ====>$values")
        println("區域Values個數 ====>${values.size}")

        mBinding.tvDbMarkContent.setTextSize(12)

        //  設定白色常數(因為版本高低[高於23&低於23]因此有兩種寫法)
        val black = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.getColor(R.color.txt_black)
        } else {
            resources.getColor(R.color.txt_black)
        }


        //取得資料頂端線數值(*8/7為固定算法)
        val max = values.max()!!.toFloat() * 8 / 7

//        定義右邊Y軸座標 //藉由左邊Y軸來設定顯示，因此右邊全部不顯示。
        barChart.axisRight.apply {
            setDrawTopYLabelEntry(false)
            setDrawZeroLine(false)
            setDrawGridLines(false)
            setDrawLabels(false)
            setDrawAxisLine(false)
        }
//        定義X軸顯示
        barChart.xAxis.apply {

            ////            labelCount = values.size / dataCount   //實際資料量
////            if (values.size <= 14) { // 若小於等於14必須強迫設定標籤數量(不可註解)
//                labelCount = values.size / dataCount
            val labelcount =
                if (values.size / dataCount > screenMaxIncludeValues) screenMaxIncludeValues else values.size / dataCount
            setLabelCount(labelcount, (values.size < 6))
//            }
            textColor = black //設定標籤文字顏色
            position = XAxis.XAxisPosition.BOTTOM//設定標籤對齊底部
            setDrawLabels(true)//是否顯示標籤
            setDrawGridLines(true)//是否顯示對齊線
            textSize = 12f
            valueFormatter = //IndexAxisValueFormatter(xLabels)//設定標籤列表
                object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
//                        println("此時要取的X軸標籤Index值是===>$value")
                        return (value.toInt()).toString()//關鍵：根據需要返回x軸數據，x軸的數據個數一定要等於下面max-min的值
                    }
                }
            //20秒顯示測試開始
            gridColor = black // 設置網格線顏色
            gridLineWidth = 1.0f
            axisMaximum = (values.size / dataCount + 1).toFloat()
            axisMinimum = 1f //設定開始值，若為0f的畫會往右偏移一格。
            //将X轴的值显示在中央
            setCenterAxisLabels(true)
            //20秒顯示測試結束
        }
//        定義左邊Ｙ軸座標
//        println("values的max 為${values.max()}")
        barChart.axisLeft.apply {
            if (max <= 1.0F) setDrawLabels(false) else setDrawLabels(true) //全部為0值時不顯示標籤
            spaceTop = 10f          //固定高度，以避免全部為0值時不顯示
            gridColor = black            //設定格線顏色
            gridLineWidth = 0.75f    //設定格線寬度
            axisMinimum = 0f        //設定最小值
            textSize = 12f           //設定文字大小
            setLabelCount(5, true) //設定橫向座標軸數量
//                               由於每次傳入值皆不同，因此Y座標之值皆要重算。
            valueFormatter = MyYAxisValueFormatter(values.max()!!.toFloat())
//            axisMaximum = max        //設定圖表最大值//不可設定，因若設定的話，全為0的時候整個座標圖會不見。
            setDrawAxisLine(false) //設定軸線不顯示(只顯示水平Y軸線)
            setDrawGridLines(true) //設定網格線顯示(顯示座標橫槓)
            textColor = black //設定標籤文字顏色
        }
//        定義圖表
        barChart.apply {
            setDrawGridBackground(false)//設定網格背景(由於需要手動設定，所以此為false)
            setDrawBarShadow(false)//強制不顯示陰影(實測後，陰影是在沒有值的上方顯示
            description.isEnabled = false //右下角不顯示描述
            isDoubleTapToZoomEnabled = true
            setPinchZoom(true)
            isDragEnabled = true   //拖拽設定
            legend.isEnabled = true //左下角顯示資料名稱
            legend.textColor = black //資料名稱字體顏色
            legend.textSize = 10f  //資料名稱字體大小
            isScaleXEnabled = false //設定X軸座標縮放與否
            isScaleYEnabled = false //設定Y軸座標縮放與否
            setScaleMinima(valueSizeToRatio(values.size, dataCount), 1.0f)
            drawOrder =
                arrayOf(CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE) // 繪製順序(先的會在後面)
//            setVisibleXRange(1f,15f)
            isAutoScaleMinMaxEnabled = true
            animateX(400)
            animateY(1200)
        }

//        設定點擊會彈出標記
        barChart.setOnChartValueSelectedListener(
            ValueChartSelectListener(
                context,
                values.max(),
                xLabels,
                mBinding,
                barChart as BarLineChartBase<*>
            )
        )

        val cmdata = CombinedData()
        cmdata.setData(getSPMLineData(values, dataCount))
        cmdata.setData(getBarData(values))

        barChart.data = cmdata //傳入資料值
        barChart.invalidate()//繪製圖表
        //組合式的圖形於此顯示的原因：由於要達到「覆蓋的效果﹞，因此先做長條圖再做折線圖
//        initCombineLineCharView(values,xLabels)
    }

    /**根據設定回傳縮放比率(預設最適顯示是15分鐘，因此為除以45)*/
    private fun valueSizeToRatio(size: Int, dataCount: Int): Float {
        val ratio = size.toFloat() / screenMaxIncludeValues / dataCount //45(15分鐘)是預設最適顯示
        return if (ratio <= 1f) 1f else ratio
    }

    /**取得BarData的方法，20秒顯示時要處理所有資料*/
    private fun getBarData(values: List<Int>): BarData {

        val entries1 = ArrayList<BarEntry>()
        val entries2 = ArrayList<BarEntry>()
        val entries3 = ArrayList<BarEntry>()

        //由於BarEntry只可以接受Float值，因此必須很疲勞的將傳入的Int轉為Float...@@a
        val datas1 = forChartData(getListByIndex(values, 0, 3))
        val datas2 = forChartData(getListByIndex(values, 1, 3))
        val datas3 = forChartData(getListByIndex(values, 2, 3))

        println("取資料時，所有的values是===>$values")

        for (i in datas1.indices) {
            if (i == datas1.size)
                break
            entries1.add(BarEntry(i.toFloat(), datas1[i].toFloat()))
            if (i < datas2.size)
                entries2.add(BarEntry(i.toFloat(), datas2[i].toFloat()))
            if (i < datas3.size)
                entries3.add(BarEntry(i.toFloat(), datas3[i].toFloat()))
        }

//        val set = BarDataSet(entries, "BarDataSet")
//
//        //設定漸層顏色//只有3.1.0-alpha板後才有此功能。
//
//        val startColor1 = ContextCompat.getColor(context, R.color.Bar_Chart)
//        val endColor1 = ContextCompat.getColor(context, R.color.orange)
//
//        set.setGradientColor(startColor1, endColor1)
//        set.highLightAlpha = 0
//        set.setDrawValues(false)    //設定不顯示頂端值
//
//        val data = BarData()

        //20秒顯示測試開始
        val values1 = java.util.ArrayList<BarEntry>()
        val values2 = java.util.ArrayList<BarEntry>()
        val values3 = java.util.ArrayList<BarEntry>()

        values1.addAll(entries1)
        values2.addAll(entries2)
        values3.addAll(entries3)

        println("values1===>$values1")
        println("values2===>$values2")
        println("values3===>$values3")


//        println("process values1===>${values1.subList(0, values1.size - 1)}")
//        println("process values2===>${values2.subList(0, values2.size - 1)}")
//        println("process values3===>${values3.subList(0, values3.size - 1)}")

        val set1: BarDataSet
        val set2: BarDataSet
        val set3: BarDataSet
//        if (barChart.getData() != null && barChart.getData().getDataSetCount() > 0) {
//            set1 = barChart.getData().getDataSetByIndex(0) as BarDataSet
//            set2 = barChart.getData().getDataSetByIndex(1) as BarDataSet
//            set3 = barChart.getData().getDataSetByIndex(2) as BarDataSet
//            set1.values = values1
//            set2.values = values2
//            set3.values = values3
//            barChart.getData().notifyDataChanged()
//            barChart.notifyDataSetChanged()
//        } else {
        // create 3 DataSets
        set1 = BarDataSet(values1, "20")
        set1.setDrawValues(false)
        set1.color = Color.rgb(104, 241, 175)
        set2 = BarDataSet(values2, "40")
        set2.setDrawValues(false)
        set2.color = Color.rgb(164, 228, 251)
        set3 = BarDataSet(values3, "60")
        set3.setDrawValues(false)
        set3.color = Color.rgb(242, 247, 158)
        val data = BarData(set1, set2, set3)
        data.setValueFormatter(LargeValueFormatter())
//        data.setValueTypeface(Typeface.createFromAsset(assets, "OpenSans-Light.ttf"))
//        }

        val groupSpace = 0.08f //柱狀圖組之間的間距
        val barSpace = 0.02f // 每個長條徒之間的艱鉅
        val barWidth = (1f - groupSpace) / 3 - barSpace //長條圖寬度

        data.barWidth = barWidth    //設定每條bar的寬度(1是填滿)

        data.groupBars(0f, groupSpace, barSpace) //FromX：開始的位置，可正可負，正--->往右偏移、負--->往左偏移
        //20秒顯示測試結束

        return data
    }


    /**給你一個數列，請給我所有要用於顯示的SPM資料*/
    private fun getSPMLineData(values: List<Int>, dataCount: Int): LineData? {

        val spmValues = forChartData(getSPMDataList(values, dataCount))

        val entries = ArrayList<Entry>()
        //由於Entry只可以接受Float值，因此必須很疲勞的將傳入的Int轉為Float...@@a
        //這疲勞的事交給你了，for迴圈！
        println("顯示時，spmValues是===>$spmValues")
        for (i in spmValues.indices) {
            entries.add(Entry(i.toFloat(), spmValues[i].toFloat()))
        }

        val set = LineDataSet(entries, "SPM")

        set.mode = LineDataSet.Mode.HORIZONTAL_BEZIER //設定曲線模式
        set.setDrawCircles(false) //設定不顯示圓點
        set.color = ContextCompat.getColor(context, R.color.blue)//設定線條顏色
        set.lineWidth = 4.0f //設定線條寬度

        set.setDrawFilled(false)     //設定顯示曲線下背景漸層
        set.setDrawValues(false)    //設定不顯示頂端值
        set.setDrawHorizontalHighlightIndicator(false)  //設定點擊不顯示水平線條
        set.setDrawVerticalHighlightIndicator(false)    //設定點擊不顯示垂直線條
        return LineData(set)
    }

    /**給你一個數列，請給我SPM的所有資料
     * 算法：{1,2,3,4,5,6,7,8,9}
     * 要回傳
     * {(0+0+0+1+2+3)/6 = 1,(1+2+3+4+5+6)/6=3,(4+5+6+7+8+9)/6=6,(7+8+9+0+0+0)/6=4}
     *
     * */
    private fun getSPMDataList(values: List<Int>, dataCount: Int): ArrayList<Int> {
        val needList = ArrayList<Int>()
        var index = 0
        while (index <= values.size) {
            needList.add(
                (
                        judegIndexAndGetValue(values, index - 3) +
                                judegIndexAndGetValue(values, index - 2) +
                                judegIndexAndGetValue(values, index - 1) +
                                judegIndexAndGetValue(values, index) +
                                judegIndexAndGetValue(values, index + 1) +
                                judegIndexAndGetValue(values, index + 2)
                        ) /
                        dataCount /
                        2
            )
            index += dataCount
        }

        println("所得SPM結果是===>$needList")
        return needList
    }

    /**若超出範圍  回傳0*/
    private fun judegIndexAndGetValue(values: List<Int>, i: Int): Int {
        return if (i < 0 || i >= values.size)
            0
        else
            values[i]
    }

    /**根據index來取List
     * 例如 傳入{1,2,3,4,5,6,7,8,9,10},0,3時
     * 要給我{1,4,7}，代表同一顏色的數字陣列
     * */
    private fun getListByIndex(values: List<Int>, index: Int, dataCount: Int): ArrayList<Int> {
        val needList = ArrayList<Int>()
        for (i in values.indices) {
            if (i % dataCount == index)
                needList.add(values[i])
        }
        return needList
    }

//    Bar Chart複製程式碼到以上為止

//    無論是Bar還是Chart都要複製的程式碼以下開始
    /**設定左側Y座標值：*/
    inner class MyYAxisValueFormatter( private val trueMax: Float) : ValueFormatter() {

        override fun getFormattedValue(value: Float): String {
//            println("此時要取的Y軸標籤Index值是===>$value")
            return when {
                value == 0f -> "" // 0值時不顯示值
                //大於1000才要無條件進位至百位
                trueMax > 1000 -> inttoHundred(value.toInt()).toString()
                else -> value.toInt().toString()
            }
        }

        //無條件進位至百位數的方法(原理：利用Int不能儲存小數，除以一百以後一定是整數，加1後再乘以100，必然得到百位的整數。
        private fun inttoHundred(toInt: Int): Any {
            return String.format("%d00",(toInt.toDouble()/100).roundToInt())
        }
    }


    class ValueChartSelectListener(
        context: Context,
        valuesMax: Int?,
        xLabels: Array<String>,
        mBinding: ActivityMainBinding,
        chart: BarLineChartBase<*>
    ) : OnChartValueSelectedListener {

        private val context = context
        private val valuesMax = valuesMax
        private val xLabels = xLabels
        private val mBinding = mBinding
        private val thischart = chart
        private var crownView = false   //初始化皇冠顯示布林值為false

        override fun onValueSelected(e: Entry, h: Highlight) {
            //初始化皇冠顯示布林值
            crownView = false

            // 由於程式流程是先設定Listener，再產生CustemMarkerView，因此要於此處記憶是否顯示皇冠。

            if (e.y.toInt() == valuesMax && e.y.toInt() != 0)   //最大值或不為0時才要顯示
                crownView = true
//            println("CrownView ====> $crownView")
            val mv = CustomMarkerView(context, mBinding, R.layout.mark_for_chart, crownView)

            mv.chartView = thischart // For bounds control
            // 先移除所有關聯，若要顯示時才連接。
            thischart.marker = null
            // 設定關聯以顯示mark  //頭尾不顯示(因各有一個零值)
//            if (e.x.toInt() != xLabels.size - 1 && e.x.toInt() != 0) {
            thischart.marker = mv // Set the marker to the barChart
//            }
        }

        override fun onNothingSelected() {
//            println("沒有點到")
        }
    }

//
//    //自定左側Y座標值：
//    inner class MyYAxisValueFormatter(private val truemax: Float) : ValueFormatter() {
//
//        override fun getFormattedValue(value: Float, axis: AxisBase?): String {
//            println("此時的 truemax 值是===>$truemax ,,,value是===>$value")
//            return when {
//                value == 0f -> "0"
//                truemax > 1000 -> inttoHundred(value.roundToInt()).toString() //大於1000才要無條件進位至百位
//                else -> value.roundToInt().toString()
//            }
//        }
//
//        //無條件進位至百位數的方法(原理：利用Int不能儲存小數，除以一百以後一定是整數，加1後再乘以100，必然得到百位的整數。
//        private fun inttoHundred(toInt: Int): Any {
//            return ((toInt / 100) + 1) * 100
//        }
//    }


    class CustomMarkerView(
        context: Context,
        mBinding: ActivityMainBinding,
        layoutResource: Int,
        crownView: Boolean
    ) :
        MarkerView(context, layoutResource) {
        private val mBinding = mBinding
        private val crownView = crownView

        // callbacks everytime the MarkerView is redrawn, can be used to update the
        // content (user-interface)

        override fun refreshContent(e: Entry?, highlight: Highlight?) {

            val x: String = formatter(e!!.x)
            val y: String = formatter(e.y)
//            (待選到最大值時才顯示)
            if (crownView)
                img_mark_crown.visibility = View.VISIBLE
            else
                img_mark_crown.visibility = View.INVISIBLE

            mBinding.tvDbMarkContent.text = "At $x o'clock, $y Steps"
            //設定顯示文字
            tv_mark_Content.text = "${x}h,${y}s"

            //避免文字太長，縮小大小
            if (y.length + x.length > 5)
                tv_mark_Content.setTextSize(11)
            else
                tv_mark_Content.setTextSize(12)
        }

        private fun formatter(xory: Float): String {
            return xory.toInt().toString()
        }

        override fun getOffset(): MPPointF {
            return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
        }
    }

    //頭尾加上0值的方法，傳入InitChartView時務必使用此方法傳入值。
    private fun forChartData(list: MutableList<Int>): ArrayList<Int> {
        val needList = ArrayList<Int>()
        needList.addAll(list)
        needList.add(0, 0)//於最前面加上0值
        needList.add(0)//尾端加上零值
        return needList
    }

    //    無論是Bar還是Chart都要複製的程式碼到以上為止


    private fun initEvent() {
        mBinding.vTouch.visibility = View.VISIBLE


        mBinding.swBarLine.setOnCheckedChangeListener { _, isChecked ->
            //開關已切換，變更顯示文字與FragmentView
//            if (isChecked) {//變更為LineChart
//                mBinding.swBarLine.text = "Line"
//                initLineCharView(values, xLabels)
//                mBinding.bcDbLabel.visibility = View.GONE
//                mBinding.lcDbLabel.visibility = View.VISIBLE
//
//            } else {//變更為BarChart
//                mBinding.swBarLine.text = "Bar"
//                initBarCharView(values, xLabels)
//                mBinding.bcDbLabel.visibility = View.VISIBLE
//                mBinding.lcDbLabel.visibility = View.GONE
//            }
            //測試時使用值，製作折線圖時使用：
            if (isChecked) {//變更為LineChart
                mBinding.swBarLine.text = "Line"
                initLineCharView(forChartData(values), xLabels)
                mBinding.bcDbLabel.visibility = View.GONE
                mBinding.lcDbLabel.visibility = View.VISIBLE
                mBinding.lcCombineLabel.visibility = View.GONE
            } else {//變更為BarChart
                mBinding.swBarLine.text = "Bar"
                initBarCharView(values, xLabels)
                mBinding.bcDbLabel.visibility = View.VISIBLE
                mBinding.lcDbLabel.visibility = View.GONE
                mBinding.lcCombineLabel.visibility = View.GONE
            }

            hideTheMarks(lineChart, barChart)       //點擊toggleButton的時候要隱藏marker。
        }
        //設定點擊其他地方後要刪除標記：
        mBinding.vTouch.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideTheMarks(lineChart, barChart)
            }
            false
        }
    }

    private fun hideTheMarks(lineChart: LineChart, barChart: CombinedChart) {
        mBinding.tvDbMarkContent.text = ""      //清除顯示值
        if (lineChart.marker != null) {
            //選中時要虛構一個不存在的value給他更新值
            // (hightlightValue設計為：若點到時傳入之值找不到就會觸發onNothingSelceted。)
            lineChart.highlightValue(Highlight(-1F, -1F, 0), true)
        }

//
//        if (barChart.marker != null) {
//            //選中時要虛構一個不存在的value給他更新值
//            // (hightlightValue設計為：若點到時傳入之值找不到就會觸發onNothingSelceted。)
//            barChart.highlightValue(Highlight(-1F, -1F, 0), true)
//        }
    }

    //系統會在每次點開menu時呼叫此方法。
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }


    private fun getRandomData(num: Int): MutableList<Int> {

        //測試時使用隨機產生之值(依照傳入的個數)
        return MutableList(num) { ((Math.random() + 0) * randomLimit).toInt() }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_setting -> {

                //點擊選單的時候要隱藏marker。
                hideTheMarks(lineChart, barChart)

                val builder = AlertDialog.Builder(context)

                val inflater = layoutInflater
                val myview = inflater.inflate(R.layout.dialog_input_data, null)
                val edNums = myview.findViewById(R.id.edt_random_num) as EditText
                val edLimit = myview.findViewById(R.id.edt_random_limit) as EditText

                edNums.setText(randomNum.toString())
                edLimit.setText(randomLimit.toString())

                builder.setView(myview)
                builder.setPositiveButton("確定") { _, _ ->
                    val strNums = edNums.text.toString()
                    val strLimits = edLimit.text.toString()

                    if (strNums.length > 3 || strLimits.length > 8 || strNums.toInt() > 361 || strNums.toInt() < 2) {
                        Toast.makeText(context, "輸入數字過大或過小，請重新輸入。", Toast.LENGTH_SHORT).show()
                    } else {
                        randomNum = strNums.toInt()
                        randomLimit = strLimits.toInt()
//                        if (barChart.marker != null) {
//                            //選中時要虛構一個不存在的value給他更新值
//                            // 因hightlightValue設計為：若點到時傳入之值找不到就會觸發onNothingSelceted。
//                            barChart.highlightValue(Highlight(-1F, -1F, 0), true)
//                        }
                        //指定全域值 //由於沒有設計線程安全，因此forChartData只可於此呼叫一次後傳入initChartView。
                        values = getRandomData(randomNum)
                        xLabels = getLabels(randomNum)

                        initLineCharView(forChartData(values), xLabels)

                        initBarCharView(values, xLabels)
                    }
                }
                builder.setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }
                dialog = builder.create()
                dialog!!.setCancelable(false)
                dialog!!.show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private var dialog: AlertDialog? = null

    //依照亂數個數取得標籤數
    private fun getLabels(randomNum: Int): Array<String> {

        //由於最前端與最尾端是空值，因此要設定加2個size。
        val labels = Array(randomNum + 2) { "" }
        labels[0] = ""
        for (i in 0 until labels.size) {
            if (i % 2 == 0)
                labels[i] = i.toString()
//                labels[i] ="Summer"
//             labels[i] = "MAR"
        }
        labels[0] = ""
        labels[labels.size - 1] = ""
        /* 完成Lable範例：
              arrayOf(  "", "", "2", "", "4", "", "6", "", "8", "", "10", "", "12", ""
            )
        */
        return labels
    }


}

