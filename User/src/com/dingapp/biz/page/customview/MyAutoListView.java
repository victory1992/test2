package com.dingapp.biz.page.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class MyAutoListView extends ListView {
	int mPosition;
	public MyAutoListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyAutoListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyAutoListView(Context context) {
		super(context);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	

	       @Override
       public boolean dispatchTouchEvent(MotionEvent event) {
	    	   final int actionMasked = event.getActionMasked() & MotionEvent.ACTION_MASK;
	    	   
	           if (actionMasked == MotionEvent.ACTION_DOWN) {
	               // 记录手指按下时的位置
	               mPosition = pointToPosition((int) event.getX(), (int) event.getY());
	               return super.dispatchTouchEvent(event);
	           }
	     
	           if (actionMasked == MotionEvent.ACTION_MOVE) {
	               // 最关键的地方，忽略MOVE 事件
	               // ListView onTouch获取不到MOVE事件所以不会发生滚动处理
	               return true;
	           }
	     
	           // 手指抬起时
	           if (actionMasked == MotionEvent.ACTION_UP
	                   || actionMasked == MotionEvent.ACTION_CANCEL) {
	               // 手指按下与抬起都在同一个视图内，交给父控件处理，这是一个点击事件
	               if (pointToPosition((int) event.getX(), (int) event.getY()) == mPosition) {
	                   super.dispatchTouchEvent(event);
	               } else {
	                   // 如果手指已经移出按下时的Item，说明是滚动行为，清理Item pressed状态
	                   setPressed(false);
	                   invalidate();
	                   return true;
	               }
	           }
	     
	           return super.dispatchTouchEvent(event);


     }

}