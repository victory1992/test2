package com.dingapp.core.util;

import android.util.TypedValue;

public class DispLengthUtil {
	public static float dipToPx(float dip) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip,
				Constants.getDisplayMetrics());
	}
}
