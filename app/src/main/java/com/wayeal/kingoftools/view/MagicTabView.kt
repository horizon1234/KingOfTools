package com.wayeal.kingoftools.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.wayeal.kingoftools.R
import kotlinx.android.synthetic.main.layout_tab_item.view.*
import kotlin.math.pow
import kotlin.math.roundToInt

const val TAG = "MagicTabView"

@SuppressLint("ResourceAsColor")
class MagicTabView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var title = DEFAULT_TITLE
    private var normalColor = DEFAULT_NORMAL_COLOR
    private var selectColor = DEFAULT_SELECT_COLOR
    private var lottieImage = DEFAULT_LOTTIE_IMAGE

    companion object {
        //默认标题
        const val DEFAULT_TITLE = "默认"

        //默认不选中颜色，为黑色
        const val DEFAULT_NORMAL_COLOR = R.color.tab_normal

        //默认选中颜色，为绿色
        const val DEFAULT_SELECT_COLOR = R.color.tab_red

        //默认lottie资源
        const val DEFAULT_LOTTIE_IMAGE = "home_icon.json"
    }

    init {
        //直接inflate布局，包括一个lottieView和一个title
        inflate(context, R.layout.layout_tab_item, this)
        //解析自定义的属性
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.MagicTabView)
        for (i in 0 until typeArray.indexCount) {
            when (val attr = typeArray.getIndex(i)) {
                R.styleable.MagicTabView_tabTitle -> {
                    //获取标题名
                    title = typeArray.getString(attr).toString()
                }
                R.styleable.MagicTabView_textColor -> {
                    //获取默认颜色
                    normalColor = typeArray.getColor(attr, DEFAULT_NORMAL_COLOR)
                }
                R.styleable.MagicTabView_textSelectColor -> {
                    //获取选中颜色
                    selectColor = typeArray.getColor(attr, DEFAULT_SELECT_COLOR)
                }
                R.styleable.MagicTabView_lottieImage -> {
                    //获取lottie动画
                    lottieImage = typeArray.getString(attr).toString()
                }
            }
        }
        Log.i(TAG, "normalColor : = $normalColor ")
        Log.i(TAG, "selectColor : = $selectColor ")
        typeArray.recycle()
    }

    @SuppressLint("ResourceAsColor")
    override fun onFinishInflate() {
        super.onFinishInflate()
        //设置标题
        tab_title.text = title
        tab_title.setTextColor(normalColor)
        //设置lottie
        tab_image.setAnimation(lottieImage)
    }

    private var mPercent = 0f

    /**
     * 设置进度，这里字体要变化，同时动画也要变化
     * */
    fun setPercentage(percentage: Float) {
        mPercent = percentage
        Log.i(TAG, "setPercentage: $percentage")
        if (percentage < 0 || percentage > 1) {
            return
        }
        //颜色变化
        val currentColor = evaluate(percentage, normalColor, selectColor)
        tab_title.setTextColor(currentColor)
        //动画运行
        tab_image.progress = percentage
        //更新UI
        if (Looper.myLooper() == Looper.getMainLooper()) {
            invalidate()
        } else {
            postInvalidate()
        }
    }

    fun getPercent():Float = mPercent

    /**
     * 计算不同进度值对应的颜色值，这个方法取自 ArgbEvaluator.java 类。
     *
     * @param percentage 进度值，范围[0, 1]。
     * @param startValue 起始颜色值。
     * @param endValue 最终颜色值。
     * @return 返回与进度值相应的颜色值。
     */
    private fun evaluate(percentage: Float, startValue: Int, endValue: Int): Int {
        val startA = (startValue shr 24 and 0xff) / 255.0f
        var startR = (startValue shr 16 and 0xff) / 255.0f
        var startG = (startValue shr 8 and 0xff) / 255.0f
        var startB = (startValue and 0xff) / 255.0f
        val endA = (endValue shr 24 and 0xff) / 255.0f
        var endR = (endValue shr 16 and 0xff) / 255.0f
        var endG = (endValue shr 8 and 0xff) / 255.0f
        var endB = (endValue and 0xff) / 255.0f

        // convert from sRGB to linear
        startR = startR.toDouble().pow(2.2).toFloat()
        startG = startG.toDouble().pow(2.2).toFloat()
        startB = startB.toDouble().pow(2.2).toFloat()
        endR = endR.toDouble().pow(2.2).toFloat()
        endG = endG.toDouble().pow(2.2).toFloat()
        endB = endB.toDouble().pow(2.2).toFloat()

        // compute the interpolated color in linear space
        var a = startA + percentage * (endA - startA)
        var r = startR + percentage * (endR - startR)
        var g = startG + percentage * (endG - startG)
        var b = startB + percentage * (endB - startB)

        // convert back to sRGB in the [0..255] range
        a *= 255.0f
        r = r.toDouble().pow(1.0 / 2.2).toFloat() * 255.0f
        g = g.toDouble().pow(1.0 / 2.2).toFloat() * 255.0f
        b = b.toDouble().pow(1.0 / 2.2).toFloat() * 255.0f
        return a.roundToInt() shl 24 or (r.roundToInt() shl 16) or (g.roundToInt() shl 8) or b.roundToInt()
    }
}