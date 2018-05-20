package com.vdroid.youtubelayout

import android.content.Context
import android.view.ViewGroup
import android.support.v4.widget.ViewDragHelper
import android.util.AttributeSet
import android.view.View
import android.support.v4.view.ViewCompat

import android.view.MotionEvent





class YoutubeLayout : ViewGroup
{
    private var mDragHelper: ViewDragHelper? = null

    /*Normally, properties declared as having a non-null type must be initialized in the constructor.
    However, fairly often this is not convenient. For example, properties can be initialized through dependency injection,
    or in the setup method of a unit test*/
    private lateinit var mHeaderView: View
    private lateinit var mDescView: View

    private var mInitialMotionX: Float = 0.toFloat()
    private var mInitialMotionY: Float = 0.toFloat()

    private var mDragRange: Int = 0
    private var mTop: Int = 0
    private var mDragOffset: Float = 0.toFloat()

    /*
    If the class has no primary constructor,
     then each secondary constructor has to initialize the base type using the super keyword,
     or to delegate to another constructor which does that.
     Note that in this case different secondary constructors
     can call different constructors of the base type
     ##### https://stackoverflow.com/questions/44213958/how-to-access-constructor-argument-thats-not-member-variable-in-init-function
     */
    constructor(context: Context, attrs: AttributeSet?, defStyle : Int = 0 ) : super(context, attrs, defStyle)
    {
        mDragHelper = ViewDragHelper.create(this, 1f, DragHelperCallback())
    }

   /*
   If the class has a primary constructor, each secondary constructor
    needs to delegate to the primary constructor, either directly or indirectly
    through another secondary constructor(s).
    Delegation to another constructor of the same class is done using the this keyword
    */
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    override fun onFinishInflate()
    {
        super.onFinishInflate()
        mHeaderView = findViewById(R.id.viewHeader)
        mDescView = findViewById(R.id.viewDesc)
    }

    fun maximize()
    {
        smoothSlideTo(0f)
    }

    fun smoothSlideTo(slideOffset: Float): Boolean
    {
        val topBound = paddingTop
        val y = (topBound + slideOffset * mDragRange).toInt()
        if (mDragHelper!!.smoothSlideViewTo(mHeaderView!!, mHeaderView!!.left, y))
        {
            ViewCompat.postInvalidateOnAnimation(this)
            return true
        }
        return false
    }

    override fun computeScroll()
    {
        if (mDragHelper!!.continueSettling(true))
        {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean
    {
        val action = ev.actionMasked

        if (action != MotionEvent.ACTION_DOWN)
        {
            mDragHelper!!.cancel()
            return super.onInterceptTouchEvent(ev)
        }

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP)
        {
            mDragHelper!!.cancel()
            return false
        }

        val x = ev.x
        val y = ev.y
        var interceptTap = false

        when (action)
        {
            MotionEvent.ACTION_DOWN ->
            {
                mInitialMotionX = x
                mInitialMotionY = y
                interceptTap = mDragHelper!!.isViewUnder(mHeaderView, x.toInt(), y.toInt())
            }

            MotionEvent.ACTION_MOVE ->
            {
                val adx = Math.abs(x - mInitialMotionX)
                val ady = Math.abs(y - mInitialMotionY)
                val slop = mDragHelper!!.getTouchSlop()

                if (ady > slop && adx > ady)
                {
                    mDragHelper!!.cancel()
                    return false
                }
            }
        }

        return mDragHelper!!.shouldInterceptTouchEvent(ev) || interceptTap
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean
    {
        mDragHelper!!.processTouchEvent(ev)

        val action = ev.action
        val x = ev.x
        val y = ev.y

        val isHeaderViewUnder = mDragHelper!!.isViewUnder(mHeaderView, x.toInt(), y.toInt())
        when (action and MotionEvent.ACTION_MASK)
        {
            MotionEvent.ACTION_DOWN -> {
                mInitialMotionX = x
                mInitialMotionY = y
            }

            MotionEvent.ACTION_UP -> {
                val dx = x - mInitialMotionX
                val dy = y - mInitialMotionY
                val slop = mDragHelper!!.getTouchSlop()
                if (dx * dx + dy * dy < slop * slop && isHeaderViewUnder)
                {
                    if (mDragOffset == 0.toFloat())
                    {
                        smoothSlideTo(1f)
                    }
                    else
                    {
                        smoothSlideTo(0f)
                    }
                }
            }
        }


        return isHeaderViewUnder && isViewHit(mHeaderView!!, x.toInt(), y.toInt()) || isViewHit(mDescView!!, x.toInt(), y.toInt())
    }


    private fun isViewHit(view: View, x: Int, y: Int): Boolean
    {
        val viewLocation = IntArray(2)
        view.getLocationOnScreen(viewLocation)
        val parentLocation = IntArray(2)
        this.getLocationOnScreen(parentLocation)
        val screenX = parentLocation[0] + x
        val screenY = parentLocation[1] + y
        return screenX >= viewLocation[0] && screenX < viewLocation[0] + view.width &&
                screenY >= viewLocation[1] && screenY < viewLocation[1] + view.height
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildren(widthMeasureSpec, heightMeasureSpec)

        val maxWidth = View.MeasureSpec.getSize(widthMeasureSpec)
        val maxHeight = View.MeasureSpec.getSize(heightMeasureSpec)

        setMeasuredDimension(View.resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
                View.resolveSizeAndState(maxHeight, heightMeasureSpec, 0))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        mDragRange = height - mHeaderView.getHeight()

        mHeaderView.layout(
                0,
                mTop,
                r,
                mTop + mHeaderView.getMeasuredHeight())

        mDescView.layout(
                0,
                mTop + mHeaderView.getMeasuredHeight(),
                r,
                mTop + b)
    }

    private inner class DragHelperCallback : ViewDragHelper.Callback()
    {

        override fun tryCaptureView(child: View, pointerId: Int): Boolean
        {
            return child === mHeaderView
        }

        override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int)
        {
            mTop = top

            mDragOffset = top.toFloat() / mDragRange

            mHeaderView.pivotX = mHeaderView.width.toFloat()
            mHeaderView.pivotY = mHeaderView!!.height.toFloat()
            mHeaderView!!.scaleX = 1 - mDragOffset / 2
            mHeaderView!!.scaleY = 1 - mDragOffset / 2

            mDescView!!.setAlpha(1 - mDragOffset)

            requestLayout()
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float)
        {
            var top = paddingTop
            if (yvel > 0 || yvel == 0f && mDragOffset > 0.5f) {
                top += mDragRange
            }
            mDragHelper!!.settleCapturedViewAt(releasedChild.left, top)
        }

        override fun getViewVerticalDragRange(child: View): Int
        {
            return mDragRange
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int
        {
            val topBound = paddingTop
            val bottomBound = height - mHeaderView.getHeight() - mHeaderView.getPaddingBottom()

            return Math.min(Math.max(top, topBound), bottomBound)
        }

    }



}
