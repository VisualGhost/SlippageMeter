package com.slippagemeter;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

public class SlippageView extends View {

    private static final float MAX_DEF_VALUE = 10;
    private static final float BEVEL_ANGLE = 60;

    private float mLengthLine;
    private float mWidthLine;
    private int mMinGreenColor;
    private int mMaxGreenColor;
    private int mMinRedColor;
    private int mMaxRedColor;
    private float mMaxValue;
    private float mAvatarRadius;
    private int mAvatarBubbleBackground;
    private int mAvatarBubbleCircleBackground;
    private float mAvatarBubbleStrokeWidth;
    private Drawable mDefaultAvatar;
    private float mSlippageTextSize;
    private float mPipsTextSize;
    private int mSlippageTextColor;
    private float mSlippageBubbleStrokeWidth;
    private int mSlippageBubbleBackgroundColor;
    private float mSlippageTextPadding;

    private Paint mGradientPaint;
    private Paint mAvatarPaint;
    private Paint mCircleAvatarBackgroundPaint;
    private Paint mSlippageTextPaint;
    private Paint mPipsTextPaint;
    private Paint mSlippageBubbleBackgroundPaint;
    private Paint mSlippageBubblePaint;
    private Paint mSlippageBubbleBevelPaint;

    private int[] mColors;
    private Bitmap mBitmap;
    private float mSlippageBubbleYCoordinate;
    private float mSlippage;

    public SlippageView(final Context context) {
        super(context);
        init(context, null);
    }

    public SlippageView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SlippageView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = null;
            try {
                array = context.obtainStyledAttributes(attrs, R.styleable.SlippageView);
                float scale = array.getFloat(R.styleable.SlippageView_scale, 1);
                mLengthLine = scale * array.getDimension(R.styleable.SlippageView_lengthLine, 0);
                mWidthLine = scale * array.getDimension(R.styleable.SlippageView_widthLine, 0);
                mMinGreenColor = array.getColor(R.styleable.SlippageView_minGreen, Color.WHITE);
                mMaxGreenColor = array.getColor(R.styleable.SlippageView_maxGreen, Color.WHITE);
                mMinRedColor = array.getColor(R.styleable.SlippageView_minRed, Color.WHITE);
                mMaxRedColor = array.getColor(R.styleable.SlippageView_maxRed, Color.WHITE);
                mMaxValue = array.getFloat(R.styleable.SlippageView_maxValue, MAX_DEF_VALUE);
                mAvatarRadius = scale * array.getDimension(R.styleable.SlippageView_avatarRadius, 0);
                mAvatarBubbleBackground = array.getColor(R.styleable.SlippageView_avatarBubbleBackground, Color.WHITE);
                mAvatarBubbleCircleBackground = array.getColor(R.styleable.SlippageView_avatarBubbleCircleBackground, Color.BLACK);
                mAvatarBubbleStrokeWidth = array.getDimension(R.styleable.SlippageView_avatarBubbleStrokeWidth, 0);
                mSlippageTextSize = scale * array.getDimension(R.styleable.SlippageView_slippageTextSize, 0);
                mPipsTextSize = scale * array.getDimension(R.styleable.SlippageView_pipsTextSize, 0);
                mSlippageTextColor = array.getColor(R.styleable.SlippageView_slippageTextColor, Color.WHITE);
                mSlippageBubbleStrokeWidth = array.getDimension(R.styleable.SlippageView_slippageBubbleStrokeWidth, 0);
                mSlippageBubbleBackgroundColor = array.getColor(R.styleable.SlippageView_slippageBubbleBackgroundColor, Color.BLACK);
                mSlippageTextPadding = scale * array.getDimension(R.styleable.SlippageView_slippageTextPadding, 0);
                try {
                    int mDefaultAvatarRes = array.getResourceId(R.styleable.SlippageView_defaultAvatar, 0);
                    mDefaultAvatar = getResources().getDrawable(mDefaultAvatarRes);
                } catch (Resources.NotFoundException e) {
                    // do nothing
                }
            } finally {
                if (array != null) {
                    array.recycle();
                }
            }
        }
        mColors = new int[]{mMaxGreenColor, mMinGreenColor, mMinRedColor, mMaxRedColor};
        initGradientPaint();
        initAvatarPaint();
        initCircleAvatarBackgroundPaint();
        initSlippageTextPaint();
        initPipsTextPaint();
        initSlippageBubbleBackgroundPaint();
        initSlippageBubblePaint();
        initSlippageBubbleBevelPaint();
    }

    private void initGradientPaint() {
        mGradientPaint = new Paint();
        mGradientPaint.setAntiAlias(true);
        mGradientPaint.setStyle(Paint.Style.FILL);
        mGradientPaint.setShader(getGradientShader());
    }

    private Shader getGradientShader() {
        return new LinearGradient(0, 0, 0, mLengthLine, mColors, new float[]{0.1f, 0.4f, 0.6f, 0.95f}, Shader.TileMode.CLAMP);
    }

    private void initAvatarPaint() {
        mAvatarPaint = new Paint();
        mAvatarPaint.setAntiAlias(true);
        mAvatarPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mAvatarPaint.setStrokeWidth(0);
        mAvatarPaint.setColor(mAvatarBubbleBackground);
    }

    private void initCircleAvatarBackgroundPaint() {
        mCircleAvatarBackgroundPaint = new Paint();
        mCircleAvatarBackgroundPaint.setAntiAlias(true);
        mCircleAvatarBackgroundPaint.setColor(mAvatarBubbleCircleBackground);
    }

    private void initSlippageTextPaint() {
        mSlippageTextPaint = new Paint();
        mSlippageTextPaint.setAntiAlias(true);
        mSlippageTextPaint.setColor(mSlippageTextColor);
        mSlippageTextPaint.setTextSize(mSlippageTextSize);
    }

    private void initPipsTextPaint() {
        mPipsTextPaint = new Paint();
        mPipsTextPaint.setAntiAlias(true);
        mPipsTextPaint.setColor(mSlippageTextColor);
        mPipsTextPaint.setTextSize(mPipsTextSize);
    }

    private void initSlippageBubblePaint() {
        mSlippageBubblePaint = new Paint();
        mSlippageBubblePaint.setAntiAlias(true);
        mSlippageBubblePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mSlippageBubblePaint.setStrokeWidth(mSlippageBubbleStrokeWidth);
    }

    private void initSlippageBubbleBevelPaint() {
        mSlippageBubbleBevelPaint = new Paint();
        mSlippageBubbleBevelPaint.setAntiAlias(true);
        mSlippageBubbleBevelPaint.setStyle(Paint.Style.FILL);
    }

    private void initSlippageBubbleBackgroundPaint() {
        mSlippageBubbleBackgroundPaint = new Paint();
        mSlippageBubbleBackgroundPaint.setAntiAlias(true);
        mSlippageBubbleBackgroundPaint.setColor(mSlippageBubbleBackgroundColor);
    }

    private float getAvatarBubbleWidth() {
        return (float) (mAvatarRadius * (Math.tan(BEVEL_ANGLE * Math.PI / 180) + 1));
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        int width = getMeasuredWidth();
        setMeasuredDimension(View.resolveSize(width, widthMeasureSpec), (int) (mLengthLine + getBubbleHeight()));
    }

    private int getBubbleHeight() {
        String slippageText = formatSlippage(mSlippage);
        int slippagePadding = (int) mSlippageTextPadding;
        int height = getSlippageTextHeight(slippageText) + slippagePadding;
        return height + (int) (2 * mSlippageBubbleStrokeWidth);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(0, getBubbleHeight());
        drawLine(canvas);
        drawAvatarBubble(canvas);
        drawAvatar(canvas);
        drawSlippageLevelBubble(canvas);
    }

    private float getLeftXGradientLine() {
        return getWidth() / 2 - mWidthLine / 2;
    }

    private float getRightXGradientLine() {
        return getWidth() / 2 + mWidthLine / 2;
    }

    private void drawLine(Canvas canvas) {
        RectF rectF = new RectF(getLeftXGradientLine(), 0, getRightXGradientLine(), mLengthLine);
        canvas.drawRect(rectF, mGradientPaint);
    }

    private void drawAvatarBubble(Canvas canvas) {
        Path path = new Path();
        path.moveTo(getLeftXGradientLine(), getBottomYBubbleAvatar());
        RectF rectF = new RectF(getLeftXBubbleAvatar(), getTopYBubbleAvatar(), getRightXBubbleAvatar(), getBottomYBubbleAvatar());
        path.arcTo(rectF, -30, -240, false);
        canvas.drawPath(path, mAvatarPaint);
    }

    private float getLeftXBubbleAvatar() {
        return getLeftXGradientLine() - getAvatarBubbleWidth();
    }

    private float getRightXBubbleAvatar() {
        return 2 * mAvatarRadius + getLeftXGradientLine() - getAvatarBubbleWidth();
    }

    private float getTopYBubbleAvatar() {
        return getYGradientLineCenter() - 2 * mAvatarRadius;
    }

    private float getBottomYBubbleAvatar() {
        return getYGradientLineCenter();
    }

    private float getYGradientLineCenter() {
        return mLengthLine / 2;
    }

    private void drawAvatar(Canvas canvas) {
        drawCircleBackgroundUnderAvatar(canvas);
        if (mBitmap == null && mDefaultAvatar != null) {
            useDefaultAvatar();
        }
        if (mBitmap != null) {
            drawBitmap(canvas);
        }
    }

    private void drawCircleBackgroundUnderAvatar(Canvas canvas) {
        float xCircleCenter = getXCenterCircleBackgroundUnderAvatar();
        float yCircleCenter = getYCenterCircleBackgroundUnderAvatar();
        float radius = mAvatarRadius - mAvatarBubbleStrokeWidth;
        canvas.drawCircle(xCircleCenter, yCircleCenter, radius, mCircleAvatarBackgroundPaint);
    }

    private float getXCenterCircleBackgroundUnderAvatar() {
        return getLeftXBubbleAvatar() + mAvatarRadius;
    }

    private float getYCenterCircleBackgroundUnderAvatar() {
        return getYGradientLineCenter() - mAvatarRadius;
    }

    private void useDefaultAvatar() {
        setImageDrawable(mDefaultAvatar);
    }

    private void drawBitmap(Canvas canvas) {
        float left = getXCenterCircleBackgroundUnderAvatar() - getAvatarImageSize() / 2;
        float top = getYGradientLineCenter() - mAvatarRadius - getAvatarImageSize() / 2;
        canvas.drawBitmap(mBitmap, left, top, mCircleAvatarBackgroundPaint);
    }

    private int getAvatarImageSize() {
        return (int) (2 * mAvatarRadius - mAvatarBubbleStrokeWidth);
    }

    public void setImageDrawable(final Drawable drawable) {
        mBitmap = ((BitmapDrawable) drawable).getBitmap();
        int size = getAvatarImageSize();
        mBitmap = Bitmap.createScaledBitmap(mBitmap, size, size, true);
        mBitmap = getClip();
    }

    private Bitmap getClip() {
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(width / 2, height / 2, width / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(mBitmap, rect, rect, paint);
        return output;
    }

    public void setSlippage(float slippage) {
        if (Float.compare(slippage, mMaxValue) > 0) {
            throw new IllegalArgumentException("Your slippage is out of range: " + slippage + " > " + mMaxValue);
        }
        mSlippage = slippage;
        mSlippageBubbleYCoordinate = convertSlippageLevelToYCoordinate(slippage);
    }

    private float convertYCoordinateToSlippage(float yCoordinate) {
        return mMaxValue * (1 - 2 * yCoordinate / mLengthLine);
    }

    private float convertSlippageLevelToYCoordinate(float slippage) {
        return mLengthLine * (mMaxValue - slippage) / 2 / mMaxValue;
    }

    private void drawSlippageLevelBubble(Canvas canvas) {
        String slippageText = formatSlippage(mSlippage);
        float slippageTextLength = getSlippageTextLength(slippageText);
        int slippagePadding = (int) mSlippageTextPadding;
        int slippageTextHeight = getSlippageTextHeight(slippageText) + slippagePadding;

        float bubbleBevelLength = getBubbleBevel(slippageTextHeight / 2);
        float realX = getRightXGradientLine() + bubbleBevelLength;
        float x = getBubbleX(realX, slippageTextHeight);
        float y = getBubbleY(mSlippageBubbleYCoordinate);

        String pips = getContext().getString(R.string.pips);
        float pipsTextLength = mPipsTextPaint.measureText(pips, 0, pips.length());

        mSlippageBubbleBevelPaint.setColor(getGradientColor(mSlippageBubbleYCoordinate));
        mSlippageBubblePaint.setColor(getGradientColor(mSlippageBubbleYCoordinate));

        drawBubbleBevel(canvas, getRightXGradientLine(), y, bubbleBevelLength);
        drawSlippageBubbleBackground(canvas, x, y, slippageTextLength + pipsTextLength, slippageTextHeight, mSlippageBubblePaint);
        drawSlippageBubbleBackground(canvas, x, y, slippageTextLength + pipsTextLength, slippageTextHeight, mSlippageBubbleBackgroundPaint);
        drawSlippageTextLevelInBubble(canvas, slippageText, x, y - slippagePadding / 2);
        drawPips(canvas, x + slippageTextLength, y - 2 * slippagePadding / 3);
    }

    private float getBubbleBevel(float radius) {
        return (float) (radius * Math.tan(BEVEL_ANGLE * Math.PI / 180));
    }

    private float getBubbleX(float x, float bubbleHeight) {
        return x + bubbleHeight / 2 + mSlippageBubbleStrokeWidth;
    }

    private float getBubbleY(float y) {
        return y - mSlippageBubbleStrokeWidth / 2;
    }

    private int getSlippageTextHeight(String slippageText) {
        Rect rect = new Rect();
        mSlippageTextPaint.getTextBounds(slippageText, 0, slippageText.length(), rect);
        return rect.height();
    }

    private float getSlippageTextLength(String slippageText) {
        return mSlippageTextPaint.measureText(slippageText, 0, slippageText.length());
    }

    private void drawSlippageBubbleBackground(Canvas canvas, float x, float y, float length, float height, Paint paint) {
        y = y - height;
        Path path = new Path();
        drawStartSegment(x - height / 2, y, height, path);
        path.lineTo(x, y + height);
        drawEndSegment(x, y, length, height, path);
        path.lineTo(x, y);
        canvas.drawPath(path, paint);
    }

    private void drawSlippageTextLevelInBubble(Canvas canvas, String slippageText, float x, float y) {
        canvas.drawText(slippageText, x, y, mSlippageTextPaint);
    }

    private void drawPips(Canvas canvas, float x, float y) {
        String pips = getContext().getString(R.string.pips);
        canvas.drawText(pips, x, y, mPipsTextPaint);
    }

    private void drawStartSegment(float x, float y, float height, Path path) {
        drawSegment(x, y, height + x, height + y, 90, 180, path);
    }

    private void drawEndSegment(float x, float y, float length, float height, Path path) {
        float left = x + length - height / 2;
        float right = height + left;
        float bottom = height + y;
        drawSegment(left, y, right, bottom, 90, -180, path);
    }

    private void drawSegment(
            float left,
            float top,
            float right,
            float bottom,
            float startAngle,
            float sweepAngle,
            Path path
    ) {
        RectF rectF = new RectF(left, top, right, bottom);
        path.arcTo(rectF, startAngle, sweepAngle);
    }

    private String formatSlippage(float v) {
        return String.format("%.1f", v);
    }

    private void drawBubbleBevel(Canvas canvas, float x, float y, float bubbleBevelLength) {
        Path path = new Path();
        path.moveTo(x, y + mSlippageBubbleStrokeWidth / 2);
        path.lineTo(x + bubbleBevelLength + getBubbleHeight() / 2, y + mSlippageBubbleStrokeWidth / 2);
        RectF rectF = new RectF(x + mSlippageBubbleStrokeWidth / 2, y - getBubbleHeight() + 2 * mSlippageBubbleStrokeWidth, x + bubbleBevelLength + getBubbleHeight() / 2, y);
        path.arcTo(rectF, -BEVEL_ANGLE, 0, false);
        canvas.drawPath(path, mSlippageBubbleBevelPaint);
    }

    private int getAverageColor(int color1, int color2, float percent) {
        int a = ave(Color.alpha(color1), Color.alpha(color2), percent);
        int r = ave(Color.red(color1), Color.red(color2), percent);
        int g = ave(Color.green(color1), Color.green(color2), percent);
        int b = ave(Color.blue(color1), Color.blue(color2), percent);

        return Color.argb(a, r, g, b);
    }

    private int ave(int s, int d, float p) {
        return Math.round(s + (1 - p) * (d - s));
    }

    private int getGradientColor(float y) {
        float percent;
        float a = 0;
        float b = 3 * mLengthLine / 8;
        float c = 5 * mLengthLine / 8;
        float d = mLengthLine;
        if (Float.compare(y, a) >= 0 && Float.compare(y, b) < 0) {
            percent = getPercentage(a, b, y);
            return getAverageColor(mColors[0], mColors[1], percent);
        }
        if (Float.compare(y, b) >= 0 && Float.compare(y, c) < 0) {
            percent = getPercentage(b, c, y);
            return getAverageColor(mColors[1], mColors[2], percent);
        }
        if (Float.compare(y, c) >= 0 && Float.compare(y, d) <= 0) {
            percent = getPercentage(c, d, y);
            return getAverageColor(mColors[2], mColors[3], percent);
        }

        return 0;
    }

    private float getPercentage(float a, float b, float currentValue) {
        if (Float.compare(a, currentValue) > 0 || Float.compare(b, currentValue) < 0) {
            throw new IllegalArgumentException("Wrong " + currentValue + ". It's not from [" + a + "," + b + "]");
        }
        return (currentValue - b) / (a - b);
    }

    static class SavedState extends BaseSavedState {
        float slippageBubbleYCoordinate;
        float slippage;

        SavedState(Parcelable superState) {
            super(superState);
        }

        protected SavedState(Parcel in) {
            super(in);
            slippageBubbleYCoordinate = (float) in.readSerializable();
            slippage = (float) in.readSerializable();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeSerializable(slippageBubbleYCoordinate);
            out.writeSerializable(slippage);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    @Override
    protected Parcelable onSaveInstanceState() {

        Parcelable superState = super.onSaveInstanceState();

        SavedState state = new SavedState(superState);
        state.slippageBubbleYCoordinate = mSlippageBubbleYCoordinate;
        state.slippage = mSlippage;
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());

        mSlippageBubbleYCoordinate = savedState.slippageBubbleYCoordinate;
        mSlippage = savedState.slippage;
        invalidate();
    }

    public void setMaxValue(final float maxValue) {
        mMaxValue = maxValue;
    }
}
