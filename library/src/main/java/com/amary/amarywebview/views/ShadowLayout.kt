package com.amary.amarywebview.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.amary.amarywebview.R
import kotlin.math.abs

class ShadowLayout : FrameLayout {
  private var shadowColor = 0
  private var shadowSize = 0f
  private var cornerRadius = 0f
  private var dx = 0f
  private var dy = 0f

  constructor(context: Context) : super(context) {
    setWillNotDraw(false)
    initAttributes(null)
    setPadding()
  }

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    setWillNotDraw(false)
    initAttributes(attrs)
    setPadding()
  }

  constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
  ) : super(context, attrs, defStyleAttr) {
    setWillNotDraw(false)
    initAttributes(attrs)
    setPadding()
  }

  private fun initAttributes(attrs: AttributeSet?) {
    val attr = context.obtainStyledAttributes(attrs, R.styleable.ShadowLayout, 0, 0)
    try {
      cornerRadius = attr.getDimension(R.styleable.ShadowLayout_slCornerRadius, resources.getDimension(R.dimen.defaultMenuDropShadowCornerRadius))
      shadowSize = attr.getDimension(R.styleable.ShadowLayout_slShadowSize, resources.getDimension(R.dimen.defaultMenuDropShadowSize))
      dx = attr.getDimension(R.styleable.ShadowLayout_slDx, 0f)
      dy = attr.getDimension(R.styleable.ShadowLayout_slDy, 0f)
      shadowColor = attr.getColor(
        R.styleable.ShadowLayout_slShadowColor,
        ContextCompat.getColor(context, R.color.finestBlack10)
      )
    } finally {
      attr.recycle()
    }
  }

  private fun setPadding() {
    val xPadding = (shadowSize + abs(dx)).toInt()
    val yPadding = (shadowSize + abs(dy)).toInt()
    setPadding(xPadding, yPadding, xPadding, yPadding)
  }

  fun setShadowColor(shadowColor: Int) {
    this.shadowColor = shadowColor
    invalidate()
  }

  fun setShadowSize(shadowSize: Float) {
    this.shadowSize = shadowSize
    setPadding()
  }

  fun setCornerRadius(cornerRadius: Float) {
    this.cornerRadius = cornerRadius
    invalidate()
  }

  fun setDx(dx: Float) {
    this.dx = dx
    setPadding()
  }

  fun setDy(dy: Float) {
    this.dy = dy
    setPadding()
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    setBackgroundCompat(width, height)
  }

  private fun setBackgroundCompat(w: Int, h: Int) {
    val bitmap = createShadowBitmap(
      w,
      h,
      cornerRadius,
      shadowSize,
      dx,
      dy,
      shadowColor,
      Color.TRANSPARENT
    )
    val drawable = BitmapDrawable(resources, bitmap)
    background = drawable
  }

  private fun createShadowBitmap(shadowWidth: Int, shadowHeight: Int, cornerRadius: Float, shadowSize: Float, dx: Float, dy: Float, shadowColor: Int, fillColor: Int): Bitmap {
    val output = Bitmap.createBitmap(shadowWidth, shadowHeight, Bitmap.Config.ALPHA_8)
    val canvas = Canvas(output)
    val shadowRect = RectF(shadowSize, shadowSize, shadowWidth - shadowSize, shadowHeight - shadowSize)
    if (dy > 0) {
      shadowRect.top += dy
      shadowRect.bottom -= dy
    } else if (dy < 0) {
      shadowRect.top += abs(dy)
      shadowRect.bottom -= abs(dy)
    }
    if (dx > 0) {
      shadowRect.left += dx
      shadowRect.right -= dx
    } else if (dx < 0) {
      shadowRect.left += abs(dx)
      shadowRect.right -= abs(dx)
    }
    val shadowPaint = Paint()
    shadowPaint.isAntiAlias = true
    shadowPaint.color = fillColor
    shadowPaint.style = Paint.Style.FILL
    shadowPaint.setShadowLayer(shadowSize, dx, dy, shadowColor)
    canvas.drawRoundRect(shadowRect, cornerRadius, cornerRadius, shadowPaint)
    return output
  }

  override fun getSuggestedMinimumWidth() = 0

  override fun getSuggestedMinimumHeight() = 0
}