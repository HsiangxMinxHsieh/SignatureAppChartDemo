package com.timmymike.piecharttrial

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.timmymike.piecharttrial.databinding.ActivityMainBinding
import tools.DateTool
import tools.toDate
import tools.toDateNonNull
import tools.toString
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    val TAG = javaClass.simpleName
    private val activity = this
    private val context: Context = this
    private lateinit var mBinding: ActivityMainBinding

    private val timeFormate = "yyyy-MM-dd HH:mm:ss"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(activity, R.layout.activity_main)
//        setContentView(R.layout.activity_main)
        refreshData()
        mBinding.ivRefresh.setOnClickListener {
            refreshData()
        }
    }

    private fun refreshData() {
        nowStartTime = 1586482200000L //2020-04-10 09:30:00.000
        val example = getPieChartExampleData(100)
        logi(TAG, "產生的資料如下：$example")

        setValueAndRefreshChart(mBinding.chartPieExample, example.toMutableList())

        setValueAndRefreshHalfChart(mBinding.chartHalfPieExample, example.toMutableList())
    }


    /**==========================正式PieChart程式碼如下==========================*/
    class CacuLationData(
        var departmentName: String = "",
        var stayTime: Long = 0L,
        var stayTimePercent: Double = 0.0
    )

    private fun setValueAndRefreshChart(chart: PieChart, dataList: List<UserRecordModel.Data>) {
//        logi(TAG,"setValueAndRefreshChart 收到的資料內容如下：$dataList")

        //這裡要對數據資料作處理：
        //dataList內有依照時間戳做的資料，但是內容可能重複，因此要依照內容作處理
        val cacuLationArray = ArrayList<CacuLationData>()

        for (data in dataList) {
            if (cacuLationArray.none { it.departmentName == data.department }) { //舊的沒有，要新增
                cacuLationArray.add(CacuLationData().apply {
                    departmentName = data.department
                    stayTime = data.finishedWorkAt.toDateNonNull(timeFormate).time - data.startedWorkAt.toDateNonNull(timeFormate).time
                })
            } else {//舊的已有，要加上去
                cacuLationArray.find { it.departmentName == data.department }?.apply {
                    stayTime += data.finishedWorkAt.toDateNonNull(timeFormate).time - data.startedWorkAt.toDateNonNull(timeFormate).time
                }
            }
        }

//        //依照每個時間去計算出現百分比與隱藏不顯示
//        val timeDenominator =cacuLationArray.sumByDouble { it.stayTime.toDouble() }
//        for(data in cacuLationArray){
//            data.stayTimePercent = data.stayTime.toDouble()/timeDenominator
//            if(data.stayTimePercent<=0.05) //小於5%不顯示文字
//                data.departmentName = ""
//        }


        //接著將資料塞維PieEntry
        val dataEmp = ArrayList<PieEntry>()
        for (data in cacuLationArray) {
            dataEmp.add(PieEntry(data.stayTime.toFloat(), data.departmentName))
        }

        //製作資料與連結
        val dataSet = PieDataSet(dataEmp.toMutableList(), "").apply {
            //餅狀圖數據集 PieDataSet
            sliceSpace = 3f;           //設置餅狀Item之間的間隙
            selectionShift = 10f;      //設置餅狀Item被選中時變化的距離(MPChart會自動預留長出後的距離，因此此值越大，代表圖表出現時，沒有選取的圖形會越小)
//            setColors(colors);
            colors = COLORFUL_COLORS//為DataSet中的數據匹配上顏色集(餅圖Item顏色)
        }

        val textSize = 14f
        //集成數據
        val data = PieData(dataSet).apply {
            setDrawValues(false)            //設置是否顯示數據實體(百分比，true:以下屬性才有意義)
            setValueTextColor(context.getColor(R.color.txt_gray5))  //設置所有DataSet內數據實體（百分比）的文本顏色
            setValueTextSize(textSize)          //設置所有DataSet內數據實體（百分比）的文本字體大小 //此值同時掌控與下方Label的距離
//            setValueTypeface(mTfLight)     //設置所有DataSet內數據實體（百分比）的文本字體樣式
            setValueFormatter(PercentFormatter(chart).apply { mFormat = DecimalFormat("#.#") }) //設置所有DataSet內數據實體（百分比）的文本字體格式
        }

        //連結圖表
        chart.data = data

        //設定圖表
        chart.apply {
            // 設置 pieChart 圖表基本屬性
            setUsePercentValues(true)            //使用百分比顯示
            description.isEnabled = false    //設置pieChart圖表的右下角描述
            setBackgroundColor(context.getColor(R.color.theme_green_background))      //設置pieChart圖表背景色
            setExtraOffsets(30f, 0f, 5f, 0f)        //設置pieChart圖表上下左右的偏移，類似於內邊距
            dragDecelerationFrictionCoef = 0.95f //設置pieChart圖表轉動阻力摩擦係數 例如如果它設定為0,它會立即停止。1是一個無效的值,並將自動轉換為0.999f(越接近1代表越不容易停下來)。
            val fromAngle  = 270f
            rotationAngle = fromAngle                  //設置pieChart圖表起始角度。預設270f -->頂(北)
            isRotationEnabled = true              //設置pieChart圖表是否可以手動旋轉
            isHighlightPerTapEnabled = true       //設置piecahrt圖表點擊Item高亮是否可用
//            animateY(1400, Easing.EasingOption.EaseInOutQuad)// 設置pieChart圖表展示動畫效果

            // 設置 pieChart 圖表Item文本屬性
            setDrawEntryLabels(false)              //設置pieChart百分比下面是否顯示文字（true：下面屬性才有效果）
            setEntryLabelColor(context.getColor(R.color.white))       //設置pieChart圖表文本字體顏色
//            setEntryLabelTypeface(mTfRegular)     //設置pieChart圖表文本字體樣式
            setEntryLabelTextSize(textSize)            //設置pieChart圖表文本字體大小

            // 設置 pieChart 內部圓環屬性
            isDrawHoleEnabled = true                //是否顯示PieChart內部圓環(true:下面屬性才有意義)
            holeRadius = 40f                       //設置PieChart內部圓的半徑(這裡設置28.0f)
            transparentCircleRadius = 50f         //設置PieChart內部透明圓的半徑(這裡設置31.0f)
            setTransparentCircleColor(context.getColor(R.color.theme_green_background))  //設置PieChart內部透明圓與內部圓間距(31f-28f)填充顏色
            setTransparentCircleAlpha(110)           //設置PieChart內部透明圓與內部圓間距(31f-28f)透明度[0~255]數值越小越透明
            setHoleColor(context.getColor(R.color.theme_green_background))               //設置PieChart內部圓的顏色
            setDrawCenterText(true);               //是否繪製PieChart內部中心文本（true：下面屬性才有意義）
//            setCenterTextTypeface(mTfLight);       //設置PieChart內部圓文字的字體樣式
            centerText = "出現部門\n紀錄一覽表";                 //設置PieChart內部圓文字的內容
            setCenterTextSize(textSize);                //設置PieChart內部圓文字的大小
            setCenterTextColor(context.getColor(R.color.theme_green));         //設置PieChart內部圓文字的顏色
//            spin(2000, 0f, -360f, Easing.EaseInOutQuad) //for rotating anti-clockwise(旋轉逆神針方向
            animateXY(0, 1000, Easing.EaseInOutQuad)       //設置PieChart出現動畫
            spin(1000, fromAngle, fromAngle-360f, Easing.EaseInOutQuad) //for rotating anti-clockwise(旋轉逆神針方向
                //
//            animateXY(1000, 1000)       //設置PieChart出現動畫

            //設定Legend物件
            val l: Legend = legend
            l.apply {
                isEnabled = true                    //是否啟用圖列（true：下面屬性才有意義）
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                orientation = Legend.LegendOrientation.VERTICAL
                form = Legend.LegendForm.CIRCLE //設置圖例的形狀 (CIRCLE 是圓形，SQUARE 是方形)
                formSize = 10f                      //設置圖例的大小
                formToTextSpace = 10f              //設置每個圖例實體中標籤和形狀之間的間距
                setDrawInside(false)               //設置是否在內部製圖(若為true，圖例會比較容易和圖擠在一起)
                isWordWrapEnabled = true              //設置圖列換行(注意使用影響性能,僅適用legend位於圖表下面)
                xEntrySpace = 10f                  //設置圖例實體之間延X軸的間距（setOrientation = HORIZONTAL有效）
                yEntrySpace = 8f                  //設置圖例實體之間延Y軸的間距（setOrientation = VERTICAL 有效）
                yOffset = 0f                      //設置比例塊Y軸偏移量
                setTextSize(textSize)                      //設置圖例標籤文本的大小
                textColor = context.getColor(R.color.theme_green)//設置圖例標籤文本的顏色
            }
            //最後要重新整理
            invalidate()
        }
    }

    /**==========================正式PieChart程式碼如上==========================*/
    val COLORFUL_COLORS = mutableListOf(
        Color.rgb(178, 34, 34), Color.rgb(255, 140, 0), Color.rgb(255, 215, 0),
        Color.rgb(50, 205, 50), Color.rgb(0, 0, 205), Color.rgb(106, 90, 205),
        Color.rgb(199, 21, 133), Color.rgb(0, 128, 0)
    )

    /**==========================正式HalfPieChart程式碼如下==========================*/

    private fun setValueAndRefreshHalfChart(chart: PieChart, dataList: List<UserRecordModel.Data>) {
//        logi(TAG,"setValueAndRefreshHalfChart 收到的資料內容如下：$dataList")

        //這裡要對數據資料作處理：
        //dataList內有依照時間戳做的資料，但是內容可能重複，因此要依照內容作處理
        val cacuLationArray = ArrayList<CacuLationData>()

        for (data in dataList) {
            if (cacuLationArray.none { it.departmentName == data.department }) { //舊的沒有，要新增
                cacuLationArray.add(CacuLationData().apply {
                    departmentName = data.department
                    stayTime = data.finishedWorkAt.toDateNonNull(timeFormate).time - data.startedWorkAt.toDateNonNull(timeFormate).time
                })
            } else {//舊的已有，要加上去
                cacuLationArray.find { it.departmentName == data.department }?.apply {
                    stayTime += data.finishedWorkAt.toDateNonNull(timeFormate).time - data.startedWorkAt.toDateNonNull(timeFormate).time
                }
            }
        }

//        //依照每個時間去計算出現百分比與隱藏不顯示
//        val timeDenominator =cacuLationArray.sumByDouble { it.stayTime.toDouble() }
//        for(data in cacuLationArray){
//            data.stayTimePercent = data.stayTime.toDouble()/timeDenominator
//            if(data.stayTimePercent<=0.05) //小於5%不顯示文字
//                data.departmentName = ""
//        }


        //接著將資料塞維PieEntry
        val dataEmp = ArrayList<PieEntry>()
        for (data in cacuLationArray) {
            dataEmp.add(PieEntry(data.stayTime.toFloat(), data.departmentName))
        }


        //製作資料與連結
        val dataSet = PieDataSet(dataEmp.toMutableList(), "").apply {
            //餅狀圖數據集 PieDataSet
            sliceSpace = 3f;           //設置餅狀Item之間的間隙
            selectionShift = 10f;      //設置餅狀Item被選中時變化的距離(MPChart會自動預留長出後的距離，因此此值越大，代表圖表出現時，沒有選取的圖形會越小)
//            setColors(colors);
            colors = COLORFUL_COLORS//為DataSet中的數據匹配上顏色集(餅圖Item顏色)
        }

        val textSize = 12f
        //集成數據
        val data = PieData(dataSet).apply {
            setDrawValues(false)            //設置是否顯示數據實體(百分比，true:以下屬性才有意義)
            setValueTextColor(context.getColor(R.color.txt_gray5))  //設置所有DataSet內數據實體（百分比）的文本顏色
            setValueTextSize(textSize)          //設置所有DataSet內數據實體（百分比）的文本字體大小 //此值同時掌控與下方Label的距離
//            setValueTypeface(mTfLight)     //設置所有DataSet內數據實體（百分比）的文本字體樣式
            setValueFormatter(PercentFormatter(chart).apply { mFormat = DecimalFormat("#.#") }) //設置所有DataSet內數據實體（百分比）的文本字體格式
        }

        //連結圖表
        chart.data = data

        //設定圖表
        chart.apply {
            //半圓設定
            maxAngle = 160f                          //最多旋轉的角度
            rotationAngle = 10f                      //起始旋轉角度

            // 設置 pieChart 圖表基本屬性
            setUsePercentValues(true)            //使用百分比顯示
            description.isEnabled = false    //設置pieChart圖表的右下角描述
            setBackgroundColor(context.getColor(R.color.theme_green_background))      //設置pieChart圖表背景色
            setExtraOffsets(0f, 0f, 0f, 0f)        //設置pieChart圖表上下左右的偏移，類似於內邊距
            moveOffScreen(chart, 0.0)                  //設定向下偏移量
            dragDecelerationFrictionCoef = 0.95f //設置pieChart圖表轉動阻力摩擦係數 例如如果它設定為0,它會立即停止。1是一個無效的值,並將自動轉換為0.999f(越接近1代表越不容易停下來)。
            isRotationEnabled = false              //設置pieChart圖表是否可以手動旋轉
            isHighlightPerTapEnabled = true       //設置piecahrt圖表點擊Item高亮是否可用
//            animateY(1400, Easing.EasingOption.EaseInOutQuad)// 設置pieChart圖表展示動畫效果

            // 設置 pieChart 圖表Item文本屬性
            setDrawEntryLabels(false)             //設置pieChart百分比下面是否顯示文字（true：下面屬性才有效果）
            setEntryLabelColor(context.getColor(R.color.white))       //設置pieChart圖表文本字體顏色
//            setEntryLabelTypeface(mTfRegular)     //設置pieChart圖表文本字體樣式
            setEntryLabelTextSize(textSize)            //設置pieChart圖表文本字體大小

            // 設置 pieChart 內部圓環屬性
            isDrawHoleEnabled = true                //是否顯示PieChart內部圓環(true:下面屬性才有意義)
            holeRadius = 40f                       //設置PieChart內部圓的半徑(這裡設置28.0f)
            transparentCircleRadius = 50f         //設置PieChart內部透明圓的半徑(這裡設置31.0f)
            setTransparentCircleColor(context.getColor(R.color.theme_green_background))  //設置PieChart內部透明圓與內部圓間距(31f-28f)填充顏色
            setTransparentCircleAlpha(110)           //設置PieChart內部透明圓與內部圓間距(31f-28f)透明度[0~255]數值越小越透明
            setHoleColor(context.getColor(R.color.theme_green_background))               //設置PieChart內部圓的顏色
            setDrawCenterText(true);               //是否繪製PieChart內部中心文本（true：下面屬性才有意義）
//            setCenterTextTypeface(mTfLight);       //設置PieChart內部圓文字的字體樣式
            centerText = "出現部門\n紀錄一覽表";                 //設置PieChart內部圓文字的內容
            setCenterTextOffset(0f, 20f)
            setCenterTextSize(textSize);                //設置PieChart內部圓文字的大小
            setCenterTextColor(context.getColor(R.color.theme_green));         //設置PieChart內部圓文字的顏色

            animateXY(1000, 1000)       //設置PieChart出現動畫

            //設定Legend物件
            val l: Legend = legend
            l.apply {
                isEnabled = true                    //是否啟用圖列（true：下面屬性才有意義）
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                orientation = Legend.LegendOrientation.HORIZONTAL
                form = Legend.LegendForm.SQUARE //設置圖例的形狀 (CIRCLE 是圓形，SQUARE 是方形)
                formSize = 10f                      //設置圖例的大小
                formToTextSpace = 10f              //設置每個圖例實體中標籤和形狀之間的間距
                setDrawInside(true)                  //設置是否在內部製圖(若為true，圖例會比較容易和圖擠在一起)
                isWordWrapEnabled = true              //設置圖列換行(注意使用影響性能,僅適用legend位於圖表下面)
                xEntrySpace = 10f                  //設置圖例實體之間延X軸的間距（setOrientation = HORIZONTAL有效）
                yEntrySpace = 8f                  //設置圖例實體之間延Y軸的間距（setOrientation = VERTICAL 有效）
                yOffset = 0f                      //設置比例塊Y軸偏移量
                setTextSize(textSize)                      //設置圖例標籤文本的大小
                textColor = context.getColor(R.color.theme_green)//設置圖例標籤文本的顏色
            }
            //最後要重新整理
            invalidate()
        }
    }

    /**依照傳入值設定下移距離(半圓圖表才需要)*/
    private fun moveOffScreen(chart: PieChart, dis: Double) {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val offset = (height * dis).toInt() /* 移動百分比，數字越大代表下移越多*/
//        Log.i(TAG,"")
        val rlParams = chart.layoutParams as RelativeLayout.LayoutParams
        rlParams.setMargins(0, -offset, 0, 0)
        chart.layoutParams = rlParams
    }
    /**==========================正式HalfPieChart程式碼如上==========================*/

    /**==========================產生測試資料程式碼如下==========================*/

    private fun getPieChartExampleData(dataSize: Int): TreeSet<UserRecordModel.Data> {
        val result = TreeSet<UserRecordModel.Data>()
        for (i in 0..dataSize) {
//            logi(TAG,"i現在是$i")
            val randomIndex = (0..7).random()
            result.add(UserRecordModel.Data(department = getNameByIndex(randomIndex), departmentId = randomIndex + 1, startedWorkAt = getStartTime(), finishedWorkAt = getEndTime(i, dataSize)))
        }
//        logi(TAG,"回傳前，資料大小是===>${result.size},,,dataSize是===>$dataSize")
        return result
    }

    /**依照index回傳隨機的開始時間
     * 格式如下：
     * "yyyy-MM-dd HH:mm:ss"
     * 具體作法：9:30~18:30隨機取一個資料，接著第二筆再於剩下的時間去取
     */
    var nowStartTime = 1586482200000L //2020-04-10 09:30:00.000
    val endTime = 1586514600000L      //2020-04-10 18:30:00.000
    private fun getStartTime(): String {

        var nowget = (nowStartTime..endTime).random()
        while ((nowget - nowStartTime) > DateTool.oneMin)//如果超過1分鐘要重取（這代表每筆資料間隔不超過1分鐘)
            nowget = (nowStartTime..endTime).random()
        nowStartTime = nowget
        return nowget.toDate().toString(timeFormate)
    }

    /**依照index回傳隨機的結束時間
     * 格式如下：
    "yyyy-MM-dd HH:mm:ss"*/
    private fun getEndTime(i: Int, maxSize: Int): String {
        if (i == maxSize - 1)//下班了的最後那段時間
            return "2020-04-10 18:30:00"
        var nowget = (nowStartTime..endTime).random()
        while (nowget - nowStartTime > DateTool.oneHour) //如果結束時間超過現在開始時間一小時要重取(這代表每筆資料不會大於一小時)
            nowget = (nowStartTime..endTime).random()
        nowStartTime = nowget
        return nowget.toDate().toString(timeFormate)
    }

    private fun getNameByIndex(i: Int): String {
        return when (i) {
            0 -> "工作區"
            1 -> "樣品室"
            2 -> "餐廳"
            3 -> "廁所"
            4 -> "財務部"
            5 -> "研發部"
            6 -> "辦公室"
            7 -> "會議室"
            else -> ""
        }
    }

    /**==========================產生測試資料程式碼如上==========================*/
}
