package com.timmymike.demoallapp

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.widget.OverScroller
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.github.mikephil.charting.animation.Easing
import com.timmymike.demoallapp.chartViewInit.CombineChartView
import com.timmymike.demoallapp.chartViewInit.HorizontalBarChartView
import com.timmymike.demoallapp.chartViewInit.PieChartView
import com.timmymike.demoallapp.chartViewInit.RadarChartView
import com.timmymike.demoallapp.databinding.ActivityMainBinding
import com.timmymike.demoallapp.dialog_base.DateTool
import com.timmymike.radarcharttrial.dialog_base.UserInterfaceTool
import com.timmymike.radarcharttrial.dialog_base.loge
import com.timmymike.radarcharttrial.dialog_base.logi
import java.lang.reflect.Field

class MainActivity : AppCompatActivity() {

    private val widthPixel by lazy { UserInterfaceTool.getWindowWidth(windowManager) }
    private val heightPixel by lazy { UserInterfaceTool.getWindowHeight(windowManager) }

    private val TAG = javaClass.simpleName
    private val context: Context = this
    private val activity = this

    //    private var startTime = DateTool.nowDate
    private lateinit var mBinding: ActivityMainBinding

//    private var dataSize = 7 //預設資料筆數
//
//    private var zeroNum = 0 // 是空的資料筆數(讓其顯示為缺席)

    private val timeFormate = "yyyy-MM-dd HH:mm:ss"


    //    private var example = getExampleData(dataSize, zeroNum)
//
    private var randomRange = DateTool.oneMin * 30  // 預設亂數範圍

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        loge(TAG, "螢幕寬===>$widthPixel")
        loge(TAG, "螢幕高===>$heightPixel")

        mBinding = DataBindingUtil.setContentView(activity, R.layout.activity_main)

        refreshAllChart()

        initEvent()

    }

    private fun initEvent() {
        mBinding.ivCombineRefresh.setOnClickListener {
            refreshCombineChart()
        }
        mBinding.ivHbRefresh.setOnClickListener {
            refreshHorizontalBarChart()
        }
        mBinding.ivPieRefresh.setOnClickListener {
            refreshPieChart()
        }
        mBinding.ivRadarRefresh.setOnClickListener {
            refreshRardarChart()
        }
        var upBooleanArray = BooleanArray(4) { true }
        var downBooleanArray = BooleanArray(4) { true }


        mBinding.scrollview.setOnScrollChangeListener { _, scrollX, scrollY, oldScrollX, oldScrollY ->

            //先判斷上滑下滑
            val slideDirection = when {
                scrollY > oldScrollY -> 1 // 下滑
                scrollY < oldScrollY -> 0 // 上滑
                else -> 2 // 相等，不處理
            } // 2不處理
//            logi(TAG, "現在方向是===>$slideDirection")

//            logi(TAG, "scrollX===>$scrollX")
//            logi(TAG, "scrollY===>$scrollY")
//            logi(TAG, "oldScrollX===>$oldScrollX")
//            logi(TAG, "oldScrollY===>$oldScrollY")
            val outValue = TypedValue()
            resources.getValue(R.dimen.content_width_percent, outValue, true)
            val contentWidthPercent: Float = outValue.float

            if (slideDirection == 0) {//上滑的時候
                downBooleanArray = BooleanArray(4) { true }
                val boundaryline = scrollY
                val contentHight = (widthPixel * contentWidthPercent).toInt()
                logi(TAG, "contentHight===>$contentHight")
//            logi(TAG, "停止滑動布林值===>${isScrollOver(mBinding.scrollview)}")
                when {
                    boundaryline in 0..contentHight -> {// 第一區間，播放CombineChartView動畫
                        if (upBooleanArray[0]) {
                            playConbineChartAnimate()
                            upBooleanArray[0] = false
                        }
                    }
                    boundaryline in contentHight..contentHight*2 -> {// 第二區間，播放HorizontalBarChartView動畫
                        if (upBooleanArray[1]) {
                            playHorizontalBarChartAnimate()
                            upBooleanArray[1] = false
                        }
                    }
                    boundaryline in contentHight*2..contentHight*3 -> {// 第三區間，播放PieChartView動畫
                        if (upBooleanArray[2]) {
                            playPieChartAnimate()
                            upBooleanArray[2] = false
                        }
                    }
                    boundaryline in contentHight*3..contentHight*4 -> {// 第四區間，播放RardarChartView動畫
                        if (upBooleanArray[3]) {
                            playRardarChartAnimate()
                            upBooleanArray[3] = false
                        }
                    }
                }
            }
            else if(slideDirection == 1){// 下滑的時候
                upBooleanArray = BooleanArray(4) { true }
                val boundaryline = scrollY+heightPixel
                val contentHight = (widthPixel * contentWidthPercent).toInt()
                logi(TAG, "contentHight===>$contentHight")
//            logi(TAG, "停止滑動布林值===>${isScrollOver(mBinding.scrollview)}")
                when {
                    boundaryline in 0..contentHight -> {// 第一區間，播放CombineChartView動畫
                        if (upBooleanArray[0]) {
                            playConbineChartAnimate()
                            upBooleanArray[0] = false
                        }
                    }
                    boundaryline in contentHight..contentHight*2 -> {// 第二區間，播放HorizontalBarChartView動畫
                        if (upBooleanArray[1]) {
                            playHorizontalBarChartAnimate()
                            upBooleanArray[1] = false
                        }
                    }
                    boundaryline in contentHight*2..contentHight*3 -> {// 第三區間，播放PieChartView動畫
                        if (upBooleanArray[2]) {
                            playPieChartAnimate()
                            upBooleanArray[2] = false
                        }
                    }
                    boundaryline in contentHight*3..contentHight*4 -> {// 第四區間，播放RardarChartView動畫
                        if (upBooleanArray[3]) {
                            playRardarChartAnimate()
                            upBooleanArray[3] = false
                        }
                    }
                }

            }

        }
    }

    private fun playConbineChartAnimate() {
        mBinding.chartLineExample2.animateXY(0, 1000, Easing.EaseInCirc)
    }

    private fun playHorizontalBarChartAnimate() {
        mBinding.chartHbExample.animateXY(0, 1000, Easing.EaseOutCirc)
    }

    private fun playPieChartAnimate() {

        mBinding.chartPieExample.animateXY(0, 1000, Easing.EaseInOutQuad)       //設置PieChart出現動畫
        mBinding.chartPieExample.spin(1000, pieChartView.fromAngle, (pieChartView.fromAngle - 360f), Easing.EaseInOutQuad) //for rotating anti-clockwise(旋轉逆神針方向
    }

    private fun playRardarChartAnimate() {
        mBinding.chartRadarChartExample.animateXY(2000, 2000, Easing.EaseOutCirc) //設定圖表出現動畫(X軸動畫值，Y軸動畫值，動畫形式)
    }

    /**停止滑動才要動作的方法(判斷是否停止滑動)
     * @param scrollView
     * @return false 表示ScrollView已經停止滑動,否則正在滑動中
     */
    private fun isScrollOver(scrollView: ScrollView?): Boolean {
        try {
            if (scrollView != null) {
                val mScroller: Field = scrollView.javaClass.getDeclaredField("mScroller")
                mScroller.isAccessible = true
                val `object`: Any = mScroller.get(scrollView)
                if (`object` is OverScroller) {
                    val overScroller: OverScroller = `object` as OverScroller
                    return overScroller.isFinished
                }
            }
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        return false
    }

    private fun refreshAllChart() {
        refreshCombineChart()

        refreshHorizontalBarChart()

        refreshPieChart()

        refreshRardarChart()

    }


    private fun refreshCombineChart() {
        // 初始化CombineChart
        val combineChartViewClass = CombineChartView(context).apply {
            dataSize = 7
            zeroNum = 0
            timeFormate = this@MainActivity.timeFormate
        }

        val combineChartXLabels = arrayOf("", "一", "二", "三", "四", "五", "六", "日", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "")
        val combineData = combineChartViewClass.forChartData(combineChartViewClass.getCombineExampleData(combineChartViewClass.dataSize, combineChartViewClass.zeroNum), randomRange)
        combineChartViewClass.initCombineCharView(mBinding.chartLineExample2, combineData, combineChartXLabels)
    }

    private fun refreshHorizontalBarChart() {
        // 初始化HorizontalBarChart
        val horizontalBarChartView = HorizontalBarChartView(context).apply {
            dataSize = 8
            horizontalLine = 5
            reverse = 1
            timeFormate = this@MainActivity.timeFormate
        }

        val horizontalData = horizontalBarChartView.getHorizontalBarExampleData(horizontalBarChartView.dataSize)
        val horizontalXLabels = horizontalBarChartView.getXlabels(horizontalBarChartView.dataSize, horizontalBarChartView.reverse)

        horizontalBarChartView.initHorizontalBarCharView(mBinding.chartHbExample, horizontalData, horizontalXLabels)

    }

    private val pieChartView by lazy {
        PieChartView(context).apply {
            fromAngle = 270f
            timeFormate = this@MainActivity.timeFormate
            dataSize = 50
        }
    }

    private fun refreshPieChart() {
        //初始化PieChart


        val pieChartData = pieChartView.getPieChartExampleData(pieChartView.dataSize)
        pieChartView.initPieChartView(mBinding.chartPieExample, pieChartData.toMutableList())


    }

    private fun refreshRardarChart() {
        //初始化RardarChart
        val radarChartView = RadarChartView(context).apply {
            hLineNum = 7
            dataSize = 9
        }

        val radarData = radarChartView.getRadarExampleData(radarChartView.dataSize)
        radarChartView.initRadarChatView(mBinding.chartRadarChartExample, radarData)

    }

}
