package space.inaction.mirror

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View


/**
 * 自定义view，歌曲进度条
 * 高度至少给30
 */
class ProgressBar(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {
    /**
     * 播放进度比率
     */
    private var rate = 0.4f

    /**
     * 画笔
     */
    private val paint = Paint()

    /**
     * 进度条颜色-左
     */
    private val firstColor = Color.parseColor("#ffffff")

    /**
     * 进度条颜色-右
     */
    private val secondColor = Color.parseColor("#66ffffff")

    /**
     * 指示进度的小圆点的颜色
     */
    private val thirdColor = Color.parseColor("#ffffff")

    /**
     * 指示进度的小圆点的小半径
     */
    private val smallRadius = 10f

    /**
     * 指示进度的小圆点的大半径
     */
    private val bigRadius = 20f

    /**
     * 指示进度的小圆点的变化的半径
     */
    private var radius = smallRadius

    private val padding = 10f

    private var onRateChangeListener: OnRateChangeListener? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.strokeWidth = 5f

        paint.color = firstColor
        canvas.drawLine(
            padding,
            (height / 2).toFloat(),
            width * rate,
            (height / 2).toFloat(),
            paint
        )
        paint.color = secondColor
        canvas.drawLine(
            padding + width * rate,
            (height / 2).toFloat(),
            width - padding,
            (height / 2).toFloat(),
            paint
        )
        paint.color = thirdColor
        canvas.drawCircle(
            (width - padding * 2) * rate + padding,
            (height / 2).toFloat(),
            radius,
            paint
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        rate = event.x / width
        onRateChangeListener?.onRateChange(rate)
        invalidate()
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                Log.e("tag", "UP")
                stopAnimation()
            }

            MotionEvent.ACTION_MOVE -> {
                Log.e("tag", "MOVE")
            }

            MotionEvent.ACTION_DOWN -> {
                Log.e("tag", "DOWN")
                startAnimation()
            }
        }
        return true
    }

    private fun startAnimation() {
        val pointAnimator = ValueAnimator.ofFloat(smallRadius, bigRadius)
        pointAnimator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            radius = value
            invalidate()
        }

        pointAnimator.setDuration(100)
        pointAnimator.start()
    }

    private fun stopAnimation() {
        val pointAnimator = ValueAnimator.ofFloat(bigRadius, smallRadius)
        pointAnimator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            radius = value
            invalidate()
        }

        pointAnimator.setDuration(100)
        pointAnimator.start()
    }


    fun setRate(rate: Float) {
        this.rate = rate
        invalidate()
    }

    fun getRate(): Float {
        return rate
    }

    fun setOnRateChangeListener(onRateChangeListener: OnRateChangeListener?) {
        this.onRateChangeListener = onRateChangeListener
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    interface OnRateChangeListener {
        fun onRateChange(rate: Float)
    }
}