package com.pohil.worktime;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class UnlockerView extends View {
	
	interface onUnlockListener{
		public void onUnlock();
	}
	
	Paint paintBground;
	Paint paintSlider;
	RectF rectBground;
	RectF rectSlider;
	int width, heihgt;
	onUnlockListener unlockListener;
	
	public UnlockerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public UnlockerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public UnlockerView(Context context) {
		super(context);
		init();
	}
	
	protected void init() {
		paintBground = new Paint();
		paintBground.setColor(Color.LTGRAY);
		paintSlider = new Paint();
		paintSlider.setColor(Color.DKGRAY);
		setOnTouchListener(touchListener);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		heihgt = h;
		rectBground = new RectF(0, 0, w, h);
		float size = getSlidetrSize();
		rectSlider = new RectF((w - size) / 2, (h-size) / 2, (w+size) / 2, (h+size) / 2);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawOval(rectBground, paintBground);
		canvas.drawOval(rectSlider, paintSlider);
	}
	
	public void setOnUnlockListener(onUnlockListener listener) {
		unlockListener = listener;
	}
	
	public float getSlidetrSize() {
		return 120;
	}
	
	protected void showAnimation() {
		
	}
	
	OnTouchListener touchListener = new OnTouchListener() {
		
		boolean isPull = false;
		float depthKoef = 0.33f;
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				isPull = rectSlider.contains(event.getX(), event.getY());
				return true;
			case MotionEvent.ACTION_MOVE:
				if(isPull) {	
					float halfSize = getSlidetrSize() / 2;
					rectSlider.offsetTo(event.getX() - halfSize, event.getY() - halfSize);
					double offsetX = rectSlider.centerX() - rectBground.centerX();
					double offsetY = rectSlider.centerY() - rectBground.centerY();
					double offset = Math.sqrt(offsetX * offsetX + offsetY * offsetY);
					if (offset >= rectBground.width() / 2 - rectSlider.width() * depthKoef) {
						paintSlider.setColor(Color.BLUE);
						showAnimation();
						//rectSlider.set(0, 0, 0, 0);
						isPull = false;
						if (unlockListener != null) {
							unlockListener.onUnlock();
						}
					} else {
						paintSlider.setColor(Color.DKGRAY);
					}
					invalidate();
				}
				break;
			case MotionEvent.ACTION_UP:
				isPull = false;
				break;
			}
			return false;
		}
		
	};
		
}
