package com.example.sergey.sunshine;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;


/**
 * TODO: document your custom view class.
 */
public class WindView extends View {

    public static final String NORTH_LABEL = "N";
    public static final int MIN_VIEW_WIDTH = 100;
    public static final int MIN_VIEW_HEIGHT = 100;
    public static final float TAGS_WIDTH = 4.0f;

    private int mBackgroundColor;

    private Paint mShadowPaint;
    private RectF mShadowBounds;
    private RectF mArrowOval;
    private Paint mLinesPaint;

    private float mTextSize = 20.0f;
    private Paint mTextPaint;
    private int mTextColor;
    private String mText;

    private int mViewWidth;
    private int mViewHeight;
    private int mViewDiameter;

    private float mWindDirection = 500;

    private int mTagsColor;
    private static final float TAGS_LENGTH = 15;
    private static final float TAGS_LABEL_LENGTH = 33;

    private int mArrowColor;
    private Paint mArrowPaint;
    private static final float ARROW_ANGLE = 20;
    private static final float ARROW_LENGTH = 50;


    public WindView(Context context) {
        super(context);
        init();
    }

    public WindView(Context context, AttributeSet attrs) {
        super(context, attrs);
        AccessibilityManager accessibilityManager =
                (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (accessibilityManager.isEnabled()){
            AccessibilityEvent event = AccessibilityEvent.obtain(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
            accessibilityManager.sendAccessibilityEvent(event);
        }
        initAttrs(context, attrs);
        init();
    }

    public WindView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(context, attrs);
        init();
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        event.getText().add(mText);
        return true;
    }

    private void initAttrs(Context context, AttributeSet attrs){
        // attrs contains the raw values for the XML attributes
        // that were specified in the layout, which don't include
        // attributes set by styles or themes, and which may have
        // unresolved references. Call obtainStyledAttributes()
        // to get the final values for each attribute.
        //
        // This call uses R.styleable.PieChart, which is an array of
        // the custom attributes that were declared in attrs.xml.
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.WindView,
                0, 0
        );

        try {
            // Retrieve the values from the TypedArray and store into
            // fields of this class.
            //
            // The R.styleable.PieChart_* constants represent the index for
            // each custom attribute in the R.styleable.PieChart array.
//            mShowText = a.getBoolean(R.styleable.PieChart_showText, false);
//            mTextY = a.getDimension(R.styleable.PieChart_labelY, 0.0f);
//            mTextWidth = a.getDimension(R.styleable.PieChart_labelWidth, 0.0f);
//            mTextHeight = a.getDimension(R.styleable.PieChart_labelHeight, 0.0f);
            mTextSize = a.getDimension(R.styleable.WindView_text_size, 20.0f);
            mTextColor = a.getColor(R.styleable.WindView_text_color, 0xffffffff);
            mBackgroundColor = a.getColor(R.styleable.WindView_background_color, 0xff616161);
            mArrowColor = a.getColor(R.styleable.WindView_arrow_color, 0xffffffff);
            mTagsColor = a.getColor(R.styleable.WindView_tags_color, 0xffffffff);
//            mTextColor = a.getColor(R.styleable.PieChart_labelColor, 0xff000000);
//            mHighlightStrength = a.getFloat(R.styleable.PieChart_highlightStrength, 1.0f);
//            mPieRotation = a.getInt(R.styleable.PieChart_pieRotation, 0);
//            mPointerRadius = a.getDimension(R.styleable.PieChart_pointerRadius, 2.0f);
//            mAutoCenterInSlice = a.getBoolean(R.styleable.PieChart_autoCenterPointerInSlice, false);
        } finally {
            // release the TypedArray so that it can be reused.
            a.recycle();
        }

    }

    private void init(){

        // Set up the paint for the shadow
        mShadowPaint = new Paint(0);
        mShadowPaint.setColor(mBackgroundColor);
        mShadowPaint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));

        mLinesPaint = new Paint();
        mLinesPaint.setColor(mTagsColor);
        mLinesPaint.setStrokeWidth(TAGS_WIDTH);
        mLinesPaint.setStyle(Paint.Style.STROKE);

        // Set up the paint for the label text
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mShadowBounds = new RectF();

        mArrowOval = new RectF();
        mArrowPaint = new Paint(mLinesPaint);
        mArrowPaint.setColor(mArrowColor);


    }

    public String getText() {
        return mText;
    }

    public void setText(String mText) {
        this.mText = mText;
        invalidate();
    }

    public float getWindDirection() {
        return mWindDirection;
    }

    public void setWindDirection(float mWindDirection) {
        this.mWindDirection = mWindDirection;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawOval(mShadowBounds, mShadowPaint);

        float xCenter = mShadowBounds.centerX();
        float yCenter = mShadowBounds.centerY();

        float radius  = mViewDiameter * 0.5f;
        float radius0  = radius - TAGS_LENGTH;

        // draw tags
        for (float angel = 0; angel<360; angel += 90){
            canvas.drawLine(getX(radius, angel), getY(radius, angel), getX(radius0, angel), getY(radius0, angel), mLinesPaint);
        }
        // draw tags label
        canvas.drawText(NORTH_LABEL, getX(radius - TAGS_LABEL_LENGTH, 0), getY(radius - TAGS_LABEL_LENGTH, 0), mTextPaint);

        // draw arrow
        if (mWindDirection <= 360) {
            float radius1 = radius - ARROW_LENGTH;
            canvas.drawLine(getX(radius, mWindDirection), getY(radius, mWindDirection), getX(radius1, mWindDirection - ARROW_ANGLE), getY(radius1, mWindDirection - ARROW_ANGLE), mArrowPaint);
            canvas.drawLine(getX(radius, mWindDirection), getY(radius, mWindDirection), getX(radius1, mWindDirection + ARROW_ANGLE), getY(radius1, mWindDirection + ARROW_ANGLE), mArrowPaint);
            canvas.drawArc(mArrowOval, mWindDirection - ARROW_ANGLE - 90, ARROW_ANGLE * 2, false, mArrowPaint);
        }
        // draw text
        if (mText != null) {
            canvas.drawText(mText, xCenter, yCenter + mTextPaint.getTextSize() / 2, mTextPaint);
        }
    }

    private float getX(float radius, float angel) {
        return (float)(mShadowBounds.centerX() + radius * Math.cos((double)(angel-90) * Math.PI / 180));
    }

    private float getY(float radius, float angel) {
        return (float)(mShadowBounds.centerY() + radius * Math.sin((double) (angel - 90) * Math.PI / 180));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int wSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        int hSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        mViewWidth = Math.max(wSpecSize, MIN_VIEW_WIDTH);
        mViewHeight = Math.max(hSpecSize, MIN_VIEW_HEIGHT);

        if (wSpecMode == MeasureSpec.EXACTLY) mViewWidth = wSpecSize;
        else if (wSpecMode == MeasureSpec.AT_MOST); //wrap content

        if (hSpecMode == MeasureSpec.EXACTLY) mViewHeight = hSpecSize;
        else if (hSpecMode == MeasureSpec.AT_MOST); //wrap content

        mViewDiameter = Math.min(mViewWidth, mViewHeight);
        mShadowBounds.set(0, 0, mViewDiameter, mViewDiameter);
        mArrowOval.set(ARROW_LENGTH, ARROW_LENGTH, mViewDiameter-ARROW_LENGTH, mViewDiameter-ARROW_LENGTH);

        setMeasuredDimension(mViewWidth, mViewHeight);
    }

}
