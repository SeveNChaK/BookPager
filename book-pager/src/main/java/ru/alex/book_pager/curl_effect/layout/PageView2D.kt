package ru.alex.book_pager.curl_effect.layout

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.Region
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan
import ru.alex.book_pager.BuildConfig
import ru.alex.book_pager.curl_effect.graphics.CurvesHelper
import ru.alex.book_pager.Logger
import ru.alex.book_pager.R
import ru.alex.book_pager.dpToPixels
import ru.alex.book_pager.curl_effect.graphics.FullCurl
import ru.alex.book_pager.curl_effect.graphics.FullCurlRect

class PageView2D(context: Context, attrs: AttributeSet?) :
    FrameLayout(context, attrs),
    View.OnLayoutChangeListener,
    RecyclerView.SmoothScroller.ScrollVectorProvider {

    /**
     * Если null, то при перелистывании видно следующую страницу,
     * в противно случае будет рисоваться указанный цвет.
     */
    @ColorInt
    var nextPageColor: Int? = null
    /**
     * Если null, то при перелистывании используется бекграунд,
     * в противно случае будет рисоваться указанный цвет.
     */
    @ColorInt
    var curlColor: Int? = null

    private val defaultBackgroundColor = ContextCompat.getColor(context, R.color.background)
    /** Bitmap для бекграунда загиба */
    private var curlBackgroundBitmap: Bitmap? = null
    /** Bitmap установленного бекграунда */
    private var sourceBackgroundBitmap: Bitmap? = null

    /*
    Чтобы не допускать "открывания страницы", запоминаем предыдущее корретное состояние здесь.
    И в ситуации, когда угол уходит за пределы экрана, подставляем эти данные
     */
    private var moveStateOld = MoveState()

    private var fullCurlRect = FullCurlRect()
    private var fullCurl = FullCurl()
    private var moveState = MoveState()

    private val gradientMatrix = Matrix()
    private var lGradient: LinearGradient? = null
    private val gradientPaint = Paint().apply {
        isAntiAlias = true
    }
    private val shadowPaint = Paint().apply {
        color = Color.argb(40, 0, 0, 0)
        isAntiAlias = true
        strokeWidth = 3f.dpToPixels(context)
        style = Paint.Style.STROKE
    }
    private val bitmapPaint = Paint().apply {
        isDither = true
        isAntiAlias = true
        isFilterBitmap = true
    }
    private val curlDefaultPaint = Paint().apply {
        color = defaultBackgroundColor
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    private val nextPageColorPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val drawDebug = false
    private val debugPointPaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 20f
    }

    init {
        addOnLayoutChangeListener(this)
    }

    override fun computeScrollVectorForPosition(targetPosition: Int): PointF {
        Logger.d("Calculate dx and dy to target position. Use to snap or smooth scroll.")

        val resultPoint = PointF(0f,0f)
        if (moveState.isMove()) {
            Logger.d("Calculating...")

            val dx: Float
            val dy: Float
            when{
                moveState.isFlipToLeft() -> {
                    if (moveState.movement.x.compareTo(0) < 0 && moveState.movement.x.compareTo(-(width.toFloat() / 2f)) <= 0) {
                        //Долистываем к следующей странице
                        dx = -width.toFloat() * 2 - moveState.movement.x
                        Logger.d("Move to next.")
                    } else {
                        //Возвращаем страницу в исходное положение
                        dx = -moveState.movement.x
                        Logger.d("Move to source.")
                    }
                    dy = -moveState.movement.y
                }
                moveState.isFlipToRight() -> {
                    if (moveState.movement.x.compareTo(0) > 0 && moveState.movement.x.compareTo(width.toFloat() / 2f) >= 0) {
                        //Долистываем к предыдущей странице
                        dx = width.toFloat() * 2 - moveState.movement.x
                        Logger.d("Move to previous.")
                    } else {
                        //Возвращаем страницу в исходное положение
                        dx = -moveState.movement.x
                        Logger.d("Move to source.")
                    }
                    dy = -moveState.movement.y
                }
                else -> {
                    dx = 0f
                    dy = 0f
                }
            }

            resultPoint.set(dx, dy)
            Logger.d("Move{${moveState.movement.x}; ${moveState.movement.y}}, BackMove{$dx; $dy}")
        } else {
            Logger.d("We don't scroll or snap, because we don't move.")
        }

        return resultPoint
    }

    override fun setBackground(background: Drawable?) {
        if (background == null) {
            if (BuildConfig.DEBUG) {
                //Потому что тогда ломается визуализация
                throw IllegalStateException("Background can not be NULL.")
            }
            setBackgroundColor(defaultBackgroundColor)
        } else {
            sourceBackgroundBitmap = if (background is BitmapDrawable && background.bitmap != null) {
                background.bitmap
            } else if(background.intrinsicWidth <= 0 || background.intrinsicHeight <= 0) {
                Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Single color bitmap will be created of 1x1 pixel
            } else {
                Bitmap.createBitmap(background.intrinsicWidth, background.intrinsicHeight, Bitmap.Config.ARGB_8888)
            }
            super.setBackground(background)
        }
    }

    override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
        if (background == null) {
            setBackgroundColor(defaultBackgroundColor)
        }

        if (sourceBackgroundBitmap != null) {
            curlBackgroundBitmap = createCurlBackgroundBitmap(left, top, right, bottom)
        }

        lGradient = LinearGradient(
            width.toFloat(), 0f, 0f, 0f,
            ContextCompat.getColor(context, R.color.book_page_curl_shadow_start),
            ContextCompat.getColor(context, R.color.book_page_curl_shadow_end),
            Shader.TileMode.CLAMP
        )
        gradientPaint.shader = lGradient

        move(moveState)
    }

    private fun createCurlBackgroundBitmap(left: Int, top: Int, right: Int, bottom: Int): Bitmap {
        val width = right - left
        val height = bottom - top
        fullCurl.background.scale.set(1f, 1f)
        var tmpDrawable = background.constantState?.newDrawable()?.mutate()
        if (tmpDrawable == null) {
            if (BuildConfig.DEBUG) {
                throw IllegalStateException("CurlBackgroundDrawable can not be null.")
            } else {
                Logger.e("Use defaultBackgroundColor. Cause: curlBackgroundDrawable is null. Background drawable class name = ${background.javaClass.simpleName}")
                tmpDrawable = ColorDrawable(defaultBackgroundColor)
            }
        }

        val tmpBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        tmpDrawable.apply {
            bounds = Rect(left, top, right, bottom)
            draw(Canvas(tmpBitmap))
        }
        return tmpBitmap
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        //Чтоб внутри себя клики обработались, а сквозь себя ничего не пропускали
        return true
    }

    /**
     * Задается смещение изгибу страницы.
     *
     * @return Boolean true - если было сделано движение и никаких ограничений не нарушено,
     * false - если были нарушеные какие-либо ограничения или было полученно не поддерживаемое состояние.
     */
    fun move(moveState: MoveState): Boolean {
        this.moveState.set(moveState)

        //Запоминаем состояние и обновимся, когда узнаем наши размеры
        if (width == 0 || height == 0) {
            return true
        }

        return when {
            moveState.isIdle() -> {
                Logger.d("Idle")
                this.moveStateOld.set(moveState)
                resetRects()
                visibility = View.VISIBLE
                true
            }
            moveState.isHide() -> {
                Logger.d("Hide")
                this.moveStateOld.set(moveState)
                visibility = View.INVISIBLE
                true
            }
            moveState.isMove() -> {
                Logger.d("Move")
                visibility = View.VISIBLE
                doMove()
            }
            else -> {
                Logger.d("Unknown state.")
                false
            }
        }
    }

    private fun doMove(): Boolean {
        if (moveState.isFlipDirectionNone()) {
            return false
        }

        fullCurlRect.isTopCornerMoving = moveState.movement.y.compareTo(0f) > 0

        //Расчитываем прямогольник смещения
        if (fullCurlRect.isTopCornerMoving) {
            moveTopCorner(moveState.isFlipToRight())
        } else {
            moveBottomCorner(moveState.isFlipToRight())
        }

        val resultMove = if (checkDegrees90()) { //Хак для 90 градусов, чтобы поведения загиба было корректным
            Logger.d("State breaks constraint (90 degrees): $moveState. Use old state $moveStateOld")
            move(moveStateOld)
            false
        } else {
            //Расчитываем скругления
            CurvesHelper.calcCurves(fullCurlRect, width.toFloat(), height.toFloat(), fullCurl)

            if (checkMovementOutOfLeftBound()) { //Если было нарушено ограничение, то используем предыдущее корретное состояние
                Logger.d("State breaks constraint (out left bound): $moveState. Use old state $moveStateOld")
                move(moveStateOld)
                false
            } else { //Если все ОК, запоминаем корретное состояние
                Logger.d("State OK: $moveState. Save to old state.")
                moveStateOld.set(moveState)
                true
            }
        }

        Logger.d("Will draw $fullCurl.")
        invalidate()

        return resultMove
    }

    private fun moveTopCorner(prev: Boolean = false) {
        if (prev) {
            fullCurlRect.leftTop.apply {
                x = -width.toFloat() + moveState.movement.x
                y = 0f + moveState.movement.y
            }
        } else {
            fullCurlRect.leftTop.apply {
                x = width + moveState.movement.x
                y = 0f + moveState.movement.y
            }
        }

        //расстояние по осям от края, до места касания (катеты)
        val deltaX = width - fullCurlRect.leftTop.x
        val deltaY = fullCurlRect.leftTop.y
        //Расстояние от края, до места касания (гипотенуза)
        val distance = sqrt(deltaX * deltaX + deltaY * deltaY)
        /*
            Половина расстояние от края, до места касания (гипотенузы).
            В этом месте проходит сгиб уголка под прямым углом.
        */
        val BH = distance / 2f

        val tangAlpha = deltaY / deltaX
        val alpha = atan(tangAlpha)
        val cosAlpha = cos(alpha)
        val sinAlpha = sin(alpha)

        //Находим нижнюю точку сгиба
        fullCurlRect.rightTop.x = width - BH / cosAlpha
        fullCurlRect.rightTop.y = 0f

        //Находим верхнюю точку сгиба
        fullCurlRect.rightBottom.x = width.toFloat()
        fullCurlRect.rightBottom.y = 0f + BH / sinAlpha

        fullCurlRect.leftBottom.set(fullCurlRect.rightBottom)
        if (fullCurlRect.rightBottom.y > height) {
            fullCurlRect.rightBottom.x = width + tangAlpha * (height - fullCurlRect.rightBottom.y)
            fullCurlRect.leftBottom.x = width + tan(2 * alpha) * (height - fullCurlRect.rightBottom.y)
        }
    }

    private fun moveBottomCorner(prev: Boolean = false) {
        if (prev) {
            //уголок, который следует за пальцем
            fullCurlRect.leftBottom.apply {
                x = -width.toFloat() + moveState.movement.x
                y = height + moveState.movement.y
            }
        } else {
            //уголок, который следует за пальцем
            fullCurlRect.leftBottom.apply {
                x = width + moveState.movement.x
                y = height + moveState.movement.y
            }
        }

        //расстояние по осям от края, до места касания (катеты)
        val deltaX = width - fullCurlRect.leftBottom.x
        val deltaY = height - fullCurlRect.leftBottom.y
        //Расстояние от края, до места касания (гипотенуза)
        val distance = sqrt(deltaX * deltaX + deltaY * deltaY)
        /*
         Половина расстояние от края, до места касания (гипотенузы).
         В этом месте проходит сгиб уголка под прямым углом.
         */
        val BH = distance / 2f

        val tangAlpha = deltaY / deltaX
        val alpha = atan(tangAlpha)
        val cosAlpha = cos(alpha)
        val sinAlpha = sin(alpha)

        //Находим нижнюю точку сгиба
        fullCurlRect.rightBottom.x = width - BH / cosAlpha
        fullCurlRect.rightBottom.y = height.toFloat()

        //Находим верхнюю точку сгиба
        fullCurlRect.rightTop.y = height - BH / sinAlpha
        fullCurlRect.rightTop.x = width.toFloat()

        fullCurlRect.leftTop.set(fullCurlRect.rightTop)
        if (fullCurlRect.rightTop.y < 0) {
            fullCurlRect.rightTop.x = width + tangAlpha * fullCurlRect.rightTop.y
            fullCurlRect.leftTop.x = width + tan(2 * alpha) * fullCurlRect.rightTop.y
        }
    }

    private fun checkDegrees90(): Boolean {
        return if (fullCurlRect.isTopCornerMoving) {
            fullCurlRect.leftTop.x.roundToInt() == fullCurlRect.rightTop.x.roundToInt()
        } else {
            fullCurlRect.leftBottom.x.roundToInt() == fullCurlRect.rightBottom.x.roundToInt()
        }
    }

    private fun checkMovementOutOfLeftBound(): Boolean {
        return if (fullCurlRect.isTopCornerMoving) {
            fullCurl.topCurl.curve.start.x.compareTo(0f) <= 0
        } else {
            fullCurl.bottomCurl.curve.start.x.compareTo(0f) <= 0
        }
    }

    private fun resetRects() {
        fullCurl.reset()
        fullCurlRect.reset()
    }

    override fun draw(canvas: Canvas) {
        canvas.save()
        val nextPageMask = drawNextPage(canvas)
        super.draw(canvas)
        canvas.restore()

        if (moveState.isMove()) {
            val curlPath = fullCurl.getPath(fullCurlRect.isTopCornerMoving)

            if (nextPageColor != null && nextPageMask != null) {
                nextPageColorPaint.color = nextPageColor!!
                canvas.drawPath(nextPageMask, nextPageColorPaint)
            }

            canvas.drawPath(curlPath, shadowPaint)

            canvas.save()
            canvas.clipPath(curlPath)

            if (curlBackgroundBitmap != null && curlColor == null) {
                canvas.drawBitmap(curlBackgroundBitmap!!, fullCurl.background.createTransformMatrix(), bitmapPaint)
            } else {
                if (curlColor != null) {
                    curlDefaultPaint.color = curlColor!!
                } else {
                    curlDefaultPaint.color = defaultBackgroundColor
                }
                canvas.drawPath(curlPath, curlDefaultPaint)
            }

            lGradient?.let {
                gradientMatrix.apply {
                    reset()
                    postRotate(fullCurl.background.degreesRotate, fullCurl.background.rotatePoint.x, fullCurl.background.rotatePoint.y)
                    postTranslate(fullCurl.background.translate.x, fullCurl.background.translate.y)
                }
                it.setLocalMatrix(gradientMatrix)
                canvas.drawPath(curlPath, gradientPaint)
            }

            canvas.restore()

            canvas.drawDebug()
        }
    }

    private fun drawNextPage(canvas: Canvas): Path? {
        return if (moveState.isMove()) {
            val contentMask = fullCurl.getNextPageMask(fullCurlRect.isTopCornerMoving, width.toFloat(), height.toFloat())

            if (nextPageColor != null) {
                return contentMask
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                canvas.clipPath(contentMask, Region.Op.DIFFERENCE)
            } else {
                canvas.clipOutPath(contentMask)
            }
            contentMask
        } else {
            null
        }
    }

    private fun Canvas.drawDebug() {
        if (!drawDebug) return

        //top
        drawDebugCircle(fullCurl.topCurl.debugCircle.center.x, fullCurl.topCurl.debugCircle.center.y, fullCurl.topCurl.debugCircle.radius, Color.GREEN)
        drawDebugPoint(fullCurl.topCurl.debugCircle.center.x, fullCurl.topCurl.debugCircle.center.y, Color.RED)
        drawDebugPoint(fullCurl.topCurl.closerCurve.start.x, fullCurl.topCurl.closerCurve.start.y, Color.BLACK)
        drawDebugPoint(fullCurl.topCurl.curve.end.x, fullCurl.topCurl.curve.end.y, Color.GRAY)
        drawDebugPoint(fullCurl.topCurl.closerCurve.end.x, fullCurl.topCurl.closerCurve.end.y, Color.YELLOW)

        //bottom
        drawDebugCircle(fullCurl.bottomCurl.debugCircle.center.x, fullCurl.bottomCurl.debugCircle.center.y, fullCurl.bottomCurl.debugCircle.radius, Color.GREEN)
        drawDebugPoint(fullCurl.bottomCurl.debugCircle.center.x, fullCurl.bottomCurl.debugCircle.center.y, Color.RED)
        drawDebugPoint(fullCurl.bottomCurl.closerCurve.start.x, fullCurl.bottomCurl.closerCurve.start.y, Color.BLACK)
        drawDebugPoint(fullCurl.bottomCurl.curve.end.x, fullCurl.bottomCurl.curve.end.y, Color.GRAY)
        drawDebugPoint(fullCurl.bottomCurl.closerCurve.end.x, fullCurl.bottomCurl.closerCurve.end.y, Color.YELLOW)

        //Исходный четырех угольник, без скруглений
        drawDebugPoint(fullCurlRect.leftTop.x, fullCurlRect.leftTop.y, Color.RED)
        drawDebugPoint(fullCurlRect.rightTop.x, fullCurlRect.rightTop.y, Color.GREEN)
        drawDebugPoint(fullCurlRect.leftBottom.x, fullCurlRect.leftBottom.y, Color.DKGRAY)
        drawDebugPoint(fullCurlRect.rightBottom.x, fullCurlRect.rightBottom.y, Color.DKGRAY)
    }

    private fun Canvas.drawDebugCircle(cx: Float, cy: Float, radius: Float, color: Int = Color.BLACK) {
        if (!drawDebug) {
            return
        }
        drawCircle(cx, cy, radius, debugPointPaint.apply { this.color = color })
    }

    private fun Canvas.drawDebugPoint(x: Float, y: Float, color: Int = Color.BLACK) {
        if (!drawDebug) {
            return
        }
        drawPoint(x, y, debugPointPaint.apply { this.color = color })
    }
}
