package com.pohil.worktime;

import java.text.SimpleDateFormat;
import java.util.SimpleTimeZone;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.AttributeSet;
import android.widget.TextView;

public class TimerView extends TextView {
	
	private static final long TIME_DELAY = 1000;
	
	private int minute;
	private int hour;
	Timer timer;
	private SimpleDateFormat df;
	
	protected void init() {
		minute = 0;
		hour = 0;
		df = new SimpleDateFormat("HH:mm:ss");
		df.setTimeZone(new SimpleTimeZone(0, ""));
		setText(df.format(0));
	}
	
	public TimerView(Context context) {
		super(context);
		init();
	}
	
	public TimerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public TimerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void setEndTime(int hour, int minute) {
		this.minute = minute;
		this.hour = hour;
	}
	
	public void start() {
		timer = new Timer(true);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				handler.sendEmptyMessage(0);
			}
		}, 0, TIME_DELAY);
	};
	
	public void stop() {
		timer.cancel();
	}
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			long remainTime = TimeUtils.getRemainTime(hour, minute);
			if (remainTime < 0) {
				remainTime = 0;
			} 
			setText(df.format(remainTime));
			invalidate();
		}
	};
	

}
