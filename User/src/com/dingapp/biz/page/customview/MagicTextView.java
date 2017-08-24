package com.dingapp.biz.page.customview;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

public class MagicTextView extends TextView {  
    // 递减/递增 的变量值  
    private double mRate;  
    // 当前显示的值  
    private double mCurValue;  
    // 当前变化后最终状态的目标值  
    private double mGalValue;  
    // 控制加减法  
    private double rate = 0.1;  
    // 当前变化状态(增/减/不变)  
    private boolean refreshing;  
    private static final int REFRESH = 1;  
  
    // 偏移量 主要用来进行校正距离。  
    DecimalFormat fnum = new DecimalFormat( "0.00");  
  
    private String beforeStr;        //滚动数字前面的字符串  
    private String afterStr;         //滚动数字后面的字符串  
  
    private Handler mHandler = new Handler() {  
        public void handleMessage(android.os.Message msg) {  
            switch (msg. what) {  
            
                case REFRESH:  
                	
                    if( rate* mCurValue < rate* mGalValue){  
                        setText( beforeStr+ NumberFormat.getNumberInstance().format(Double.parseDouble(fnum.format(mCurValue)))+ afterStr); //格式+设置显示内容  
                        mCurValue += mRate * rate;  
                        mHandler.sendEmptyMessageDelayed( REFRESH, 30);  
                    } else{  
                        setText(beforeStr+NumberFormat.getNumberInstance().format(mGalValue)+ afterStr);  
                        mCurValue = mGalValue;      //滚动完成之后当前值设置为目标值  
                    }  
                    break;  
            }  
        };  
    };  
  
    public MagicTextView(Context context) {  
        super(context);  
    }  
  
    public MagicTextView(Context context, AttributeSet set) {  
        super(context, set);  
    }  
  
    public MagicTextView(Context context, AttributeSet set, int defStyle) {  
        super(context, set, defStyle);  
    }  
  
  
    public void setValue( double value) {  
        mGalValue = value;  
        mRate = Math.abs((mGalValue -mCurValue )/20);  
        BigDecimal b = new BigDecimal( mRate);  
        mRate = b.setScale(2, BigDecimal. ROUND_HALF_UP).doubleValue();  
    }  
    public void setCurValue(Double value){
    	mCurValue = value;
    }
    public void startScroll(String beforeStr, double value,String afterStr) {  
        setValue(value);  
        this.beforeStr = beforeStr== null? "":beforeStr;  
        this.afterStr = afterStr== null? "":afterStr;  
        doScroll();  
    }  
  
    private void doScroll() {  
        //if ( refreshing)  
            //return;  
  
        if (mCurValue > mGalValue) {  
            rate = -0.5;      //如果当前值大于目标值,向下滚动  
        } else{  
            rate = +0.5;       //如果当前值小于目标值,向上滚动  
        }  
        mHandler.sendEmptyMessage( REFRESH);  
    }  
}  