package com.dingapp.commonui.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;
import android.widget.ImageView;

public class RotateDisk extends ImageView {
	private static int LENGTH = -1;
	private int mXCenter = -1;
	private int mYCenter = -1;

	private ArrayList<String> mTitles = new ArrayList<String>();
	private ArrayList<String> mColors = new ArrayList<String>();

	private int mCurrentIdx = 0;
	private int mDegree;

	private Bitmap mBitmap;
	private int mArcDegree;
	private int mHalfDegree;
	private int mStartDegree;
	private int mNextDegree;
	private int mNextIdx;
	private boolean mAnimationing;
	private int fontSize;
	private int yoffline;
	private int maxWidth1;
	private int maxWidth2;
	private RectF mRotateArea;
	private float mRadius;
	private Paint pBorder;
	private Paint pSep;
	private TextPaint pText;
	private int yRow1Off;
	private int yRow2Off;
	private Paint p;
	private RotateAnimationListener mAnimListener;
	private AccelerateDecelerateInterpolator mInterpolator;
	public onAfterChooseListener mListener;
	private int radiusLength;

	public RotateDisk(Context context) {
		this(context, null, 0);
	}

	public RotateDisk(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RotateDisk(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs, defStyle);
	}

	public void init(Context context, AttributeSet attrs, int defStyle) {
		Drawable dr = getDrawable();
		int drWidth = dr.getIntrinsicWidth();
		int drHeight = dr.getIntrinsicHeight();
		LENGTH = Math.min(drWidth, drHeight);
		if (LENGTH <= 0) {
			LENGTH = 300;
		}
		final double factor = 185d / 300d;
		radiusLength = (int) (LENGTH * factor);
		// mRotateArea = new RectF(3, 3, radiusLength-4, radiusLength-4);
		// mRadius = (mRotateArea.right-mRotateArea.left)/2;
		mXCenter = LENGTH / 2;
		mYCenter = mXCenter;
		mRotateArea = new RectF(mXCenter - radiusLength / 2, mYCenter
				- radiusLength / 2, mXCenter + radiusLength / 2 - 1, mYCenter
				+ radiusLength / 2 - 1);
		mRadius = (mRotateArea.right - mRotateArea.left) / 2;

		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		fontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				13, dm);
		yoffline = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				3, dm);

		pBorder = new Paint();
		pBorder.setAntiAlias(true);
		pBorder.setStrokeWidth(2);
		pBorder.setStyle(Style.FILL_AND_STROKE);

		pSep = new Paint();
		pSep.setAntiAlias(true);
		pSep.setColor(Color.parseColor("#ffffff"));
		pSep.setStrokeWidth(2);
		pSep.setStyle(Style.STROKE);

		pText = new TextPaint();
		pText.setAntiAlias(true);
		pText.setStrokeWidth(1);
		pText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

		p = new Paint();

		mBitmap = Bitmap.createBitmap(LENGTH, LENGTH, Config.ARGB_8888);
		ArrayList<String> titles = new ArrayList<String>();
		for (int i = 0; i < 6; i++) {
			titles.add("");
		}
		ArrayList<String> colors = new ArrayList<String>();
		colors.add("#ffff00");
		colors.add("#ff0000");
		setTitles(titles, colors);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mBitmap.recycle();
	}

	public void setTitles(List<String> titles, List<String> colors) {
		if (mTitles.size() > 0) {
			mTitles.clear();
		}
		mTitles.addAll(titles);
		if (colors != null && colors.size() >= 2) {
			if (mColors.size() > 0) {
				mColors.clear();
			}
			mColors.addAll(colors);
		}
		mArcDegree = 360 / mTitles.size();
		mHalfDegree = mArcDegree / 2;
		mStartDegree = 270 - mHalfDegree;
		mDegree = 0;
		mCurrentIdx = 0;

		double radians = Math.toRadians(mHalfDegree);
		double sinRadians = Math.sin(radians);
		float r1 = mRadius - fontSize - yoffline;
		float r2 = r1 - fontSize;
		maxWidth1 = (int) (2 * r1 * sinRadians - 10);
		maxWidth2 = (int) (2 * r2 * sinRadians - 10);

		yRow1Off = fontSize + yoffline;
		yRow2Off = yRow1Off + fontSize;

		Canvas c = new Canvas();
		if (mBitmap == null || mBitmap.isRecycled()) {
			mBitmap = Bitmap.createBitmap(LENGTH, LENGTH, Config.ARGB_8888);
		}
		c.setBitmap(mBitmap);
		c.save();
		c.rotate(mHalfDegree, mXCenter, mYCenter);
		doDraw(c);
		c.restore();
		invalidate();
	}

	private void doDraw(Canvas canvas) {
		int titleCnt = mTitles.size();
		if (titleCnt <= 0) {
			return;
		}

		int colorsCnt = mColors.size();
		if (colorsCnt <= 0) {
			return;
		}

		pText.setTextSize(fontSize);

		for (int i = 0; i < titleCnt; i++) {
			String color = mColors.get(i % colorsCnt);
			pBorder.setColor(Color.parseColor(color));
			int rc = canvas.save();
			canvas.rotate(i * mArcDegree, mXCenter, mYCenter);
			canvas.drawArc(mRotateArea, mStartDegree, mArcDegree, true, pBorder);

			canvas.save();
			canvas.rotate(360 - mHalfDegree, mXCenter, mYCenter);
			canvas.drawLine(mXCenter, mRotateArea.top, mXCenter, mYCenter, pSep);
			canvas.restore();

			canvas.save();
			canvas.rotate(mHalfDegree, mXCenter, mYCenter);
			canvas.drawLine(mXCenter, mRotateArea.top, mXCenter, mYCenter, pSep);
			canvas.restore();
			canvas.save();
			// canvas.clipRect(mRotateArea);
			canvas.translate(mRotateArea.left, mRotateArea.top);
			drawTitle(canvas, pText, i);
			canvas.restore();
			canvas.restoreToCount(rc);
		}
	}

	private void drawTitle(Canvas canvas, TextPaint pText, int i) {
		String title = mTitles.get(i);
		if (TextUtils.isEmpty(title)) {
			return;
		}

		float[] widths1 = new float[10];
		int cnt1 = pText.breakText(title, true, maxWidth1, widths1);
		canvas.drawText(title.substring(0, cnt1),
				(radiusLength - widths1[0]) / 2, yRow1Off, pText);
		if (cnt1 >= title.length()) {
			return;
		}

		String nextLine = title.substring(cnt1);
		if (TextUtils.isEmpty(nextLine)) {
			return;
		}
		float[] widths2 = new float[10];
		int cnt2 = pText.breakText(nextLine, true, maxWidth2, widths2);
		if (cnt2 < nextLine.length()) {
			if (cnt2 > 3) {
				int width = (int) pText.measureText(nextLine.substring(0,
						cnt2 - 3) + "...");
				canvas.drawText(nextLine.substring(0, cnt2 - 3) + "...",
						(radiusLength - width) / 2, yRow2Off, pText);
			} else {
				int width = (int) pText.measureText("...");
				canvas.drawText("...", (radiusLength - width) / 2, yRow2Off,
						pText);
			}
		} else {
			canvas.drawText(nextLine, (radiusLength - widths2[0]) / 2,
					yRow2Off, pText);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(LENGTH, LENGTH);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.save();
		canvas.rotate(mDegree, mXCenter, mYCenter);
		canvas.drawBitmap(mBitmap, 0, 0, p);
		canvas.restore();
		Bitmap bgBm = null;
		Drawable dr = getDrawable();
		if (dr instanceof BitmapDrawable) {
			bgBm = ((BitmapDrawable) dr).getBitmap();
			canvas.drawBitmap(bgBm, 0, 0, p);
		}

	}

	public void startAnimation(int i) {
		if (mAnimationing) {
			return;
		}
		mAnimationing = true;
		mNextDegree = (mTitles.size() - i) * mArcDegree - mHalfDegree;
		mNextIdx = i;
		int totalDegree = mNextDegree + (int) (5 * 360) - mDegree;
		Animation anim = new RotatieAnimation(totalDegree, mDegree);
		if (mAnimListener == null) {
			mAnimListener = new RotateAnimationListener();
		}
		if (mInterpolator == null) {
			mInterpolator = new AccelerateDecelerateInterpolator();
		}
		anim.setAnimationListener(mAnimListener);
		anim.setInterpolator(mInterpolator);
		anim.setDuration(5000);
		startAnimation(anim);
	}

	public int getCurrentIdx() {
		return mCurrentIdx;
	}

	private class RotateAnimationListener implements AnimationListener {
		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			mCurrentIdx = mNextIdx;
			mAnimationing = false;
			if (mListener != null) {
				mListener.onFinishRotate();
			}
		}
	}

	private class RotatieAnimation extends Animation {
		private final int totalDegree;
		private final int fromDegree;

		private RotatieAnimation(int totalDegree, int fromDegree) {
			this.totalDegree = totalDegree;
			this.fromDegree = fromDegree;
		}

		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {
			int increment = (int) (totalDegree * interpolatedTime);
			mDegree = (int) (fromDegree + increment);
			mDegree %= 360;
			invalidate();
		}
	}

	public static interface onAfterChooseListener {
		public void onFinishRotate();
	}

	public void setOnAfterChooseListener(onAfterChooseListener listener) {
		mListener = listener;
	}

	public void reset() {
		mDegree = 0;
		invalidate();
	}
}
