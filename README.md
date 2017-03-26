# RoundChoiceView
A widget for round shape choice view with number or cross indicator

####先看下效果
![demo.gif](http://upload-images.jianshu.io/upload_images/2816392-5eac67fed1131ba9.gif?imageMogr2/auto-orient/strip)
####说在前面的话
开发这个控件是因为在仿探探的图片选择器组件的时候，他们有这样的一个控件。照猫画虎，我也做了一个。
想来，其实还是有很多的朋友需要这样的控件。
所以开源出来给大家使用。

####使用方式
项目放在gihub上，欢迎使用。
github地址：https://github.com/klisly/RoundChoiceView
已经发布到jitpack
  源配置

     allprojects {
        repositories {
           jcenter()
           maven { url "https://jitpack.io" }
        }
    }
依赖配置
    compile 'com.github.klisly:RoundChoiceView:v1.1'

####设计步骤
######1.定义样式属性
定义了控件的的属性有如下几个：
borderColor：border的颜色
backgroundColor：控件未选中状态的时候的背景颜色
checkedColor：选中状态时控件填充颜色
crossType：选中状态时的类型（数字或者打勾）
crossColor：类型是打勾时，勾的颜色
 rippleBorderWidth：border的宽度
rippleduration：选中时的动画过渡时间
checked：控件选中状态
number：如果是crossType是数字时，默认显示的数字
textSize：数字的字体size

      <declare-styleable name="RoundChoiceView">
        <attr name="borderColor" format="color" />
        <attr name="checkedColor" format="color" />
        <attr name="backgroundColor" format="color" />
        <attr name="crossColor" format="color" />
        <attr name="rippleBorderWidth" format="dimension" />
        <attr name="rippleduration" format="integer" />
        <attr name="checked" format="boolean" />
        <attr name="crossType" format="string" >
            <enum name="cross" value="2" />
            <enum name="number" value="1" />
        </attr>
        <attr name="number" format="integer" />
        <attr name="textSize" format="dimension" />
    </declare-styleable>

######2.读取样式
这一步骤获取已经定义好的属性，并初始化所需要的画笔

 mDensity = getContext().getResources().getDisplayMetrics().density;
        scaledDensity = getContext().getResources().getDisplayMetrics().scaledDensity;

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.RoundChoiceView, defStyle, 0);
        mUnCheckColor = a.getColor(R.styleable.RoundChoiceView_borderColor, mUnCheckColor);
        mCheckColor = a.getColor(R.styleable.RoundChoiceView_checkedColor, mCheckColor);
        mCrossColor = a.getColor(R.styleable.RoundChoiceView_crossColor, mCrossColor);
        mBgColor = a.getColor(R.styleable.RoundChoiceView_backgroundColor, mBgColor);
        mTextSize = a.getDimension(R.styleable.RoundChoiceView_textSize, mTextSize);
        int crossType = a.getInt(R.styleable.RoundChoiceView_crossType, TYPE_CROSS_CROSS);
        if (crossType == TYPE_CROSS_NUMBER) {
            mCrossType = TYPE_CROSS_NUMBER;
        } else {
            mCrossType = TYPE_CROSS_CROSS;
        }
        mBorderWidth = a.getDimension(R.styleable.RoundChoiceView_rippleBorderWidth, dp2px(2));
        mDuration = a.getInt(R.styleable.RoundChoiceView_rippleduration, mDuration);
        mChecked = a.getBoolean(R.styleable.RoundChoiceView_checked, mChecked);
        mNumber = a.getInt(R.styleable.RoundChoiceView_number, mNumber);
        a.recycle();
        mHookDuration = (int) (mDuration * 0.3);
        mCircleDuration = mDuration - mHookDuration;
        mUncheckPaint = new Paint();
        mUncheckPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mUncheckPaint.setStrokeWidth(mBorderWidth);
        mUncheckPaint.setColor(mUnCheckColor);
        mUncheckPaint.setStyle(Paint.Style.STROKE);
        mCheckPaint = new Paint();
        mCheckPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mCheckPaint.setColor(mCheckColor);
        mCheckPaint.setStyle(Paint.Style.STROKE);
        isHookShow = mChecked;
        mFraction = mChecked ? 1.0f : 0.0f;

######3.确定尺寸
onSizeChanged方法回调的时候可以获得控件的最终尺寸，在此可以初始化
勾的起点start，折点 middle，终点的坐标。

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        mRectF = new RectF(getPaddingLeft(), getPaddingTop(),
                w - getPaddingRight(), h - getPaddingBottom());
        mRadius = Math.min(mRectF.width(), mRectF.height()) / 2;
        hookStart = new PointF(mRectF.centerX() - mRadius / 2, mRectF.centerY() + mRadius / 10);
        hookMiddle = new PointF(mRectF.centerX() - mRadius * 1 / 6, mRectF.centerY() + mRadius * 2 / 5);
        hookEnd = new PointF(mRectF.centerX() + mRadius / 2, mRectF.centerY() - mRadius * 1 / 3);
    }

######4.绘制控件
绘制逻辑：
  1.绘制未选中状态时的圆形和border
  2.绘制过渡状态的选中的圆形
  3.绘制勾或者数字
  
       @Override
       protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mUncheckPaint.setColor(mBgColor);
        mUncheckPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mRectF.centerX(), mRectF.centerY(), mRadius - mBorderWidth, mUncheckPaint);
        mUncheckPaint.setColor(mUnCheckColor);
        mUncheckPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(mRectF.centerX(), mRectF.centerY(), mRadius - mBorderWidth + 1, mUncheckPaint);
        if (mChecked || mIsAnimating) {
            float stroke = mFraction * mRadius;
            if (!mIsAnimating) {
                stroke = mRadius;
            }

            if (mChecked) {
                mCheckPaint.setStrokeWidth(stroke);
            } else {
                mCheckPaint.setStrokeWidth(mRadius - stroke);
            }

            if (isHookShow) {
                mNRectF.left = getPaddingLeft() + mRadius / 2;
                mNRectF.top = getPaddingTop() + mRadius / 2;
                mNRectF.right = width - getPaddingRight() - mRadius / 2;
                mNRectF.bottom = height - getPaddingBottom() - mRadius / 2;
                mCheckPaint.setStrokeWidth(mRadius);
            } else if (mChecked) {
                mNRectF.left = getPaddingLeft() + stroke / 2;
                mNRectF.top = getPaddingTop() + stroke / 2;
                mNRectF.right = width - getPaddingRight() - stroke / 2;
                mNRectF.bottom = height - getPaddingBottom() - stroke / 2;
            } else {
                mNRectF.left = getPaddingLeft() + (mRadius - stroke) / 2;
                mNRectF.top = getPaddingTop() + (mRadius - stroke) / 2;
                mNRectF.right = width - getPaddingRight() - (mRadius - stroke) / 2;
                mNRectF.bottom = height - getPaddingBottom() - (mRadius - stroke) / 2;
            }
            canvas.drawArc(mNRectF, 0f, 360f, false, mCheckPaint);
        }

        mUncheckPaint.setColor(mCrossColor);
        if (mCrossType == TYPE_CROSS_NUMBER) {
            if (mChecked) {
                mUncheckPaint.setTextSize(mTextSize);
                mUncheckPaint.setStyle(Paint.Style.FILL);
                Paint.FontMetrics metrics = mUncheckPaint.getFontMetrics();
                int baseline = (int) ((mRectF.bottom + mRectF.top - metrics.bottom - metrics.top) / 2);
                mUncheckPaint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(String.valueOf(mNumber), mRectF.centerX(), baseline, mUncheckPaint);
                mUncheckPaint.setStyle(Paint.Style.STROKE);
            }
        } else {

            if (mIsAnimating) {
                if (!mChecked && isHookShow) {
                    mFraction = 1 - mFraction;
                }

                if (isHookShow && mFraction > 0) {// y1 - x1
                    Log.i("isHookShow", isHookShow + " " + mFraction);
                    if (mFraction < 0.4) {
                        canvas.drawLine(hookStart.x, hookStart.y, getr1x((float) (mFraction / 0.4)), getr1y((float) (mFraction / 0.4)), mUncheckPaint);
                    } else {
                        canvas.drawLine(hookStart.x, hookStart.y, hookMiddle.x + 2, hookMiddle.y + 2, mUncheckPaint);
                        canvas.drawLine(hookMiddle.x, hookMiddle.y, getr2x((float) ((mFraction - 0.4) / 0.6)), getr2y((float) ((mFraction - 0.4f) / 0.6)), mUncheckPaint);
                    }
                }
            } else {
                if (mChecked && isHookShow) {
                    canvas.drawLine(hookStart.x, hookStart.y, hookMiddle.x + 2, hookMiddle.y + 2, mUncheckPaint);
                    canvas.drawLine(hookMiddle.x, hookMiddle.y, getr2x((float) ((1 - 0.4) / 0.6)), getr2y((float) ((1 - 0.4f) / 0.6)), mUncheckPaint);
                }
            }
        }
    }

######4.事件控制
1.在ACTION_DOWN时捕获事件
2.在ACTION_UP中判断是否处理选中状态的监听，并处理动画的绘制

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!isEnabled()) {
                    return false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if (mIsAnimating) {
                    return true;
                }
                if (x + getLeft() < getRight() && y + getTop() < getBottom()) {
                    mChecked = !mChecked;
                    if (mIsAnimating) {
                        mFractionAnimator.cancel();
                    }
                    if (mChecked) {
                        initValueAnimator(mCircleDuration);
                    } else {
                        initValueAnimator(mHookDuration);
                    }
                    mFractionAnimator.start();
                    if (onCheckedChangeListener != null) {
                        onCheckedChangeListener.onCheckedChanged(this, mChecked);
                    }
                }
                break;
        }
        return true;
    }
