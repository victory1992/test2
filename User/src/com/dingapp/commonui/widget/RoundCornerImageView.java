package com.dingapp.commonui.widget;

import com.dingapp.core.util.DispLengthUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundCornerImageView extends ImageView {
	private Paint paint = new Paint();

	public RoundCornerImageView(Context context) {
		super(context);
	}

	public RoundCornerImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RoundCornerImageView(Context context, AttributeSet attrs,
			int defStyleAttr) {
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
			b.recycle();
		} else {
			super.onDraw(canvas);
		}
	}

	private Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		int measuredWidth = getMeasuredWidth();
		int measuredHeight = getMeasuredHeight();
		if (measuredHeight <= 0 || measuredWidth <= 0) {
			measuredHeight = (int) DispLengthUtil.dipToPx(52);
			measuredWidth = (int) DispLengthUtil.dipToPx(52);
		}
		Bitmap cropBitmap = Bitmap.createScaledBitmap(bitmap, measuredWidth,
				measuredHeight, true);
		Bitmap output = Bitmap.createBitmap(cropBitmap.getWidth(),
				cropBitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		int color = 0xff424242;
		Rect rect = new Rect(0, 0, cropBitmap.getWidth(),
				cropBitmap.getHeight());
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);

		int radius = 14;// (int) CommonMethods.dipToPx(8);
		int xLeft = 0;
		int yTop = 0;
		int xRight = cropBitmap.getWidth();
		int yDown = cropBitmap.getHeight();
		int rectLength = 2 * radius;

		// 左上角圆弧
		RectF leftUpperRec = new RectF(xLeft, yTop, rectLength, rectLength);
		canvas.drawArc(leftUpperRec, 180, 90, true, paint);
		// 右上角圆弧
		RectF rightUpperRec = new RectF(xRight - rectLength, yTop, xRight,
				rectLength);
		canvas.drawArc(rightUpperRec, 270, 90, true, paint);
		// 左下角圆弧
		RectF leftDownRec = new RectF(xLeft, yDown - rectLength, rectLength,
				yDown);
		canvas.drawArc(leftDownRec, 90, 90, true, paint);
		// 右下角圆弧
		RectF rightDownRec = new RectF(xRight - rectLength, yDown - rectLength,
				xRight, yDown);
		canvas.drawArc(rightDownRec, 0, 90, true, paint);
		// 中间图形
		canvas.drawRect(xLeft, radius, xRight, yDown - radius + 1, paint);
		// 上边图形
		canvas.drawRect(radius, yTop, xRight - radius + 1, radius, paint);
		// 下边图形
		canvas.drawRect(xLeft + radius, yDown - radius, xRight - radius + 1,
				yDown, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(cropBitmap, null, rect, paint);
		return output;
	}
}
