package com.dingapp.commonui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.dingapp.core.util.DispLengthUtil;

public class CircleImageView extends ImageView {
	private Paint paint = new Paint();

	public CircleImageView(Context context) {
		super(context);
	}

	public CircleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Drawable drawable = getDrawable();
		if (null != drawable) {
			Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
			Bitmap b = toRoundCorner(bitmap, 14);
			Rect rect = new Rect(0, 0, b.getWidth(), b.getHeight());
			paint.reset();
			canvas.drawBitmap(b, rect, rect, paint);
		} else {
			super.onDraw(canvas);
		}
	}

	private Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		try {
			int measuredWidth = getMeasuredWidth();
			int measuredHeight = getMeasuredHeight();
			if (measuredHeight <= 0 || measuredWidth <= 0) {
				measuredHeight = (int) DispLengthUtil.dipToPx(52);
				measuredWidth = (int) DispLengthUtil.dipToPx(52);
			}
			Bitmap cropBitmap = Bitmap.createScaledBitmap(bitmap,
					measuredWidth, measuredHeight, true);
			Bitmap output = Bitmap.createBitmap(cropBitmap.getWidth(),
					cropBitmap.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(output);

			int color = 0xff424242;
			Rect rect = new Rect(0, 0, cropBitmap.getWidth(),
					cropBitmap.getHeight());
			paint.setAntiAlias(true);

			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			int x = cropBitmap.getWidth();
			canvas.drawCircle(x / 2, x / 2, x / 2, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(cropBitmap, rect, rect, paint);
			return output;
		} catch (OutOfMemoryError e) {
			return null;
		}

	}
}
