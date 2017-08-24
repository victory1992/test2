package com.dingapp.commonui.widget;

import com.dingapp.commonui.widget.GestureRelativeLayout.OnGestureChangedListener.GESTRUE_DIRECTION;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;

public class GestureRelativeLayout extends RelativeLayout {
	private int verticalMinDistance;
	private int minVelocity;
	private double minAngle = Math.toRadians(0);
	private double maxAngle = Math.toRadians(30);
	private OnGestureChangedListener mListener;
	private boolean mDisallowIntercept;
	private VelocityTracker mVelocityTracker;
	private float mLastX;
	private float mLastY;
	private MotionEvent mLastEvent;
	private float mMaximumVelocity;
	private boolean mDisableGensture;

	public GestureRelativeLayout(Context context) {
		super(context);
		init();
	}

	public GestureRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public GestureRelativeLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	@Override
	public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
		mDisallowIntercept = disallowIntercept;
		if (getParent() != null) {
			getParent().requestDisallowInterceptTouchEvent(disallowIntercept);
		}
	}

	private void init() {
		final ViewConfiguration configuration = ViewConfiguration
				.get(getContext());
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
		minVelocity = ViewConfiguration.getMinimumFlingVelocity();
		verticalMinDistance = 30;
	}

	private GestureProcessedResult calculateGesture(float mLastX2,
			float mLastY2, float x, float y, float velocityX, float velocityY) {
		float delY = Math.abs(velocityY);
		float delX = Math.abs(velocityX);
		float delDistanceX = x - mLastX2;

		GestureProcessedResult result = new GestureProcessedResult();
		if (Math.abs(delDistanceX) < verticalMinDistance || delX < minVelocity) {
			return result;
		}

		float tanAngle = delY / delX;
		double angle = Math.atan(tanAngle);
		double degree = Math.toDegrees(angle);

		if (angle < minAngle || angle > maxAngle) {
			return result;
		}

		if (mLastX2 - x > verticalMinDistance) {
			if (mListener != null) {
				result.mHandled = mListener
						.onGestureChanged(GESTRUE_DIRECTION.LEFT);
			}
		} else if (x - mLastX2 > verticalMinDistance) {
			if (mListener != null) {
				result.mHandled = mListener
						.onGestureChanged(GESTRUE_DIRECTION.RIGHT);
			}
		}

		return result;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		GestureProcessedResult result = null;
		if (!mDisableGensture) {
			result = processGesture(ev);
		}
		if (result != null && result.mHandled) {
			return true;
		}

		if (mDisallowIntercept) {
			return false;
		}

		return super.onInterceptTouchEvent(ev);
	}

	private GestureProcessedResult processGesture(MotionEvent ev) {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);
		mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
		float x = mVelocityTracker.getXVelocity();
		float y = mVelocityTracker.getYVelocity();
		GestureProcessedResult result = null;
		if (mLastEvent != null) {
			result = calculateGesture(mLastX, mLastY, ev.getX(), ev.getY(), x,
					y);
		} else {
			result = new GestureProcessedResult();
		}
		if (result.mIsGesture) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}

		if (ev.getAction() == MotionEvent.ACTION_UP) {
			mLastEvent = null;
		} else {
			mLastEvent = ev;
			mLastX = ev.getX();
			mLastY = ev.getY();
		}

		return result;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		GestureProcessedResult result = null;
		if (!mDisableGensture) {
			result = processGesture(event);
		}

		if (result != null && result.mHandled) {
			return true;
		}
		return super.onTouchEvent(event);
	}

	public void setOnGestureChangedListener(OnGestureChangedListener listener) {
		mListener = listener;
	}

	public static interface OnGestureChangedListener {
		public static enum GESTRUE_DIRECTION {
			LEFT, RIGHT, UP, DOWN
		}

		public boolean onGestureChanged(GESTRUE_DIRECTION direction);
	}

	private static class GestureProcessedResult {
		boolean mHandled;
		boolean mIsGesture;

		public GestureProcessedResult() {
			mHandled = false;
			mIsGesture = false;
		}
	}

	public void disableGesture() {
		mDisableGensture = true;
		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
		}
		mVelocityTracker = null;
		mLastEvent = null;
	}

	public void enableGesture() {
		mDisableGensture = false;
	}
}
